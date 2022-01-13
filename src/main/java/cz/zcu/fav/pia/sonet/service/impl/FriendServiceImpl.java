package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.dto.BlockedDTO;
import cz.zcu.fav.pia.sonet.dto.FriendDTO;
import cz.zcu.fav.pia.sonet.entity.*;
import cz.zcu.fav.pia.sonet.repository.*;
import cz.zcu.fav.pia.sonet.service.FriendService;
import cz.zcu.fav.pia.sonet.service.LoggedUserService;
import cz.zcu.fav.pia.sonet.util.Utils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUser;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.*;
import java.util.stream.Collectors;


@Service("friendService")
@RequiredArgsConstructor
@Slf4j
@PreAuthorize("isAuthenticated()")
public class FriendServiceImpl implements FriendService {

    private final UserEntityRepository userEntityRepository;
    private final FriendEntityRepository friendEntityRepository;
    private final RequestFriendEntityRepository requestFriendEntityRepository;
    private final BlockEntityRepository blockEntityRepository;
    private final ChatEntityRepository chatEntityRepository;

    private final LoggedUserService loggedUserService;
    private final SimpUserRegistry simpUserRegistry;

    @Override
    public List<UserDomain> getFriends(String user) {
        List<UserDomain> userDomainList = new ArrayList<>();

        for (FriendEntity friendEntity : friendEntityRepository.findAllByUser1Username(user)) {
            UserEntity userEntity = friendEntity.getUser2();

            Set<GrantedAuthority> authorities = new HashSet<>();

            for (RoleEntity currentRole : userEntity.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(Utils.toSpringRole(currentRole)));
            }

            userDomainList.add(new UserDomain(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    authorities
            ));

        }

        return Collections.unmodifiableList(userDomainList);
    }

    @Override
    public List<FriendDTO> getFriendsWithStatus(String user) {
        List<FriendDTO> friendsWithStatus = new ArrayList<>();
        int status = 0;

        List<String> onlineUsers = simpUserRegistry.getUsers().stream().map(SimpUser::getName).collect(Collectors.toList());

        for (UserDomain friend : getFriends(user)) {
            status = 0;

            for (String onlineUser : onlineUsers) {
                if (onlineUser.equals(friend.getUsername())) {
                    status = 1;
                    break;
                }
            }

            friendsWithStatus.add(new FriendDTO(friend.getUsername(), status));

        }

        return friendsWithStatus;
    }

    @Override
    public List<UserDomain> getBlockedUsers(String user) {
        List<UserDomain> userDomainList = new ArrayList<>();

        for (BlockEntity blockEntity : blockEntityRepository.findAllByUser1Username(user)) {
            UserEntity userEntity = blockEntity.getUser2();

            Set<GrantedAuthority> authorities = new HashSet<>();

            for (RoleEntity currentRole : userEntity.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(Utils.toSpringRole(currentRole)));
            }

            userDomainList.add(new UserDomain(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    authorities
            ));

        }

        return Collections.unmodifiableList(userDomainList);
    }

    @Override
    public List<UserDomain> getUsersToBeBlockWith(String user) {
        List<UserDomain> userDomainList = new ArrayList<>();

        for (BlockEntity blockEntity : blockEntityRepository.findAllByUser2Username(user)) {
            UserEntity userEntity = blockEntity.getUser1();

            Set<GrantedAuthority> authorities = new HashSet<>();

            for (RoleEntity currentRole : userEntity.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(Utils.toSpringRole(currentRole)));
            }

            userDomainList.add(new UserDomain(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    authorities
            ));

        }

        return Collections.unmodifiableList(userDomainList);
    }

    @Override
    public List<UserDomain> getSentRequestsToUsers(String loggedUser) {
        List<UserDomain> userDomainList = new ArrayList<>();

        for (RequestFriendEntity requestFriendEntity : requestFriendEntityRepository.findAllByUser1Username(loggedUser)) {
            UserEntity userEntity = requestFriendEntity.getUser2();

            Set<GrantedAuthority> authorities = new HashSet<>();

            for (RoleEntity currentRole : userEntity.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(Utils.toSpringRole(currentRole)));
            }

            userDomainList.add(new UserDomain(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    authorities
            ));

        }

        return Collections.unmodifiableList(userDomainList);
    }

    @Override
    public List<UserDomain> getAcceptableRequests(String loggedUser) {
        List<UserDomain> userDomainList = new ArrayList<>();

        for (RequestFriendEntity requestFriendEntity : requestFriendEntityRepository.findAllByUser2Username(loggedUser)) {
            UserEntity userEntity = requestFriendEntity.getUser1();

            Set<GrantedAuthority> authorities = new HashSet<>();

            for (RoleEntity currentRole : userEntity.getRoles()) {
                authorities.add(new SimpleGrantedAuthority(Utils.toSpringRole(currentRole)));
            }

            userDomainList.add(new UserDomain(
                    userEntity.getId(),
                    userEntity.getUsername(),
                    userEntity.getPassword(),
                    userEntity.getFirstName(),
                    userEntity.getLastName(),
                    authorities
            ));

        }

        return Collections.unmodifiableList(userDomainList);
    }

    @Override
    public boolean isChatting(String user) {
        if (chatEntityRepository.findAllByUser1Username(user).isEmpty()) {
            return !chatEntityRepository.findAllByUser2Username(user).isEmpty();
        }

        return true;
    }

    @Override
    public void startChat(String loggedUser, String user) {
        if (loggedUser.equals(user)) {
            log.error("It is not possible to chat with your own.");
            return;
        }

        UserEntity loggedUserEntity = userEntityRepository.findUserEntityByUsername(loggedUser);
        UserEntity blockedUserEntity = userEntityRepository.findUserEntityByUsername(user);

        ChatEntity chatEntity = new ChatEntity(loggedUserEntity, blockedUserEntity);

        chatEntityRepository.save(chatEntity);
    }

    @Override
    public String getReceiver(String loggedUser) {
        List<ChatEntity> chatEntities1 = chatEntityRepository.findAllByUser1Username(loggedUser);
        List<ChatEntity> chatEntities2 = chatEntityRepository.findAllByUser2Username(loggedUser);

        if (chatEntities1.size() == 1) {
            return chatEntities1.get(0).getUser2().getUsername();
        } else if (chatEntities2.size() == 1) {
            return chatEntities2.get(0).getUser1().getUsername();
        } else if (chatEntities1.size() > 1 || chatEntities2.size() > 1) {
            log.error("Users cant chat with more friends! Chat ending of user " + loggedUser + ".");
            for (ChatEntity chatEntity : chatEntities1) {
                chatEntityRepository.delete(chatEntity);
            }
            for (ChatEntity chatEntity : chatEntities2) {
                chatEntityRepository.delete(chatEntity);
            }
        }
        return null;

    }

    @Override
    public String removeChat(String loggedUser) {
        String receiver = getReceiver(loggedUser);
        if (receiver == null) {
            return null;
        }
        List<ChatEntity> loggedUserEntities = chatEntityRepository.findAllByUser1UsernameAndUser2Username(loggedUser, receiver);
        List<ChatEntity> friendEntities = chatEntityRepository.findAllByUser1UsernameAndUser2Username(receiver, loggedUser);

        for (ChatEntity chatEntity : loggedUserEntities) {
            chatEntityRepository.delete(chatEntity);
        }
        for (ChatEntity chatEntity : friendEntities) {
            chatEntityRepository.delete(chatEntity);
        }

        return receiver;
    }

    @Override
    public boolean isOnline(String user) {
        List <String> onlineUsersStr = simpUserRegistry.getUsers().stream().map(SimpUser::getName).collect(Collectors.toList());

        for(String onlineUser : onlineUsersStr) {
            if (user != null) {
                if (onlineUser.equals(user))
                    return true;
            }
        }

        return false;
    }

    @Override
    public List<BlockedDTO> getBlocks(String loggedUser) {
        List<BlockedDTO> blocksWithoutStatus = new ArrayList<>();

        for (UserDomain block : getBlockedUsers(loggedUser)) {
            blocksWithoutStatus.add(new BlockedDTO(block.getUsername()));
        }

        return blocksWithoutStatus;
    }

    @Override
    public boolean isRequested(String loggedUser, String username) {
        for (RequestFriendEntity requestFriendEntity : requestFriendEntityRepository.findAllByUser1Username(loggedUser)) {
            UserEntity userEntity = requestFriendEntity.getUser2();

            if (userEntity.getUsername().equals(username)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean areFriends(String username1, String username2) {
        for (FriendEntity friendEntity : friendEntityRepository.findAllByUser1Username(username1)) {
            UserEntity userEntity = friendEntity.getUser2();

            if (userEntity.getUsername().equals(username2)) {
                return true;
            }

        }

        return false;
    }

    @Override
    public void acceptFriend(UserDomain friend) {
        acceptFriend(loggedUserService.getUser(), friend);
    }

    @Override
    public void declineFriend(UserDomain user) { removeReq(user, loggedUserService.getUser()); }

    @Override
    public void addFriend(UserDomain user) { addFriend(loggedUserService.getUser(), user); }

    @Override
    public void removeFriend(UserDomain friend) {
        removeFriend(loggedUserService.getUser(), friend);
    }

    @Override
    public void blockUser(UserDomain user) {
        blockUser(loggedUserService.getUser(), user);
    }

    @Override
    public void removeBlock(UserDomain user) {
        removeBlock(loggedUserService.getUser(), user);
    }

    @Override
    public void removeReq(UserDomain user)  { removeReq(loggedUserService.getUser(), user); }

    @Transactional
    public void acceptFriend(UserDomain loggedUser, UserDomain friend) {
        List<UserDomain> friendList1 = getFriends(loggedUser.getUsername());
        List<UserDomain> friendList2 = getFriends(friend.getUsername());

        if (loggedUser.equals(friend)) {
            log.error("It is not possible to be friend with your own.");
            return;
        }

        if (friendList1.contains(friend) || friendList2.contains(loggedUser)) {
            log.error("Users " + loggedUser.getUsername() + " and " + friend.getUsername() + " are already friends.");
            return;
        }

        removeReq(friend, loggedUser);

        UserEntity loggedUserEntity = userEntityRepository.findUserEntityByUsername(loggedUser.getUsername());
        UserEntity friendUserEntity = userEntityRepository.findUserEntityByUsername(friend.getUsername());


        FriendEntity friendEntity1 = new FriendEntity(loggedUserEntity, friendUserEntity);
        FriendEntity friendEntity2 = new FriendEntity(friendUserEntity, loggedUserEntity);

        friendEntityRepository.save(friendEntity1);
        friendEntityRepository.save(friendEntity2);
    }

    @Transactional
    public void addFriend(UserDomain loggedUser, UserDomain user) {
        List<UserDomain> sentRequests1 = getSentRequestsToUsers(loggedUser.getUsername());
        List<UserDomain> sentRequests2 = getSentRequestsToUsers(user.getUsername());

        if (loggedUser.equals(user)) {
            log.error("It is not possible to be friend with your own.");
            return;
        }

        if (sentRequests1.contains(user) || sentRequests2.contains(loggedUser)) {
            log.error("Users " + loggedUser.getUsername() + " and " + user.getUsername() + " are already pending.");
            return;
        }

        UserEntity loggedUserEntity = userEntityRepository.findUserEntityByUsername(loggedUser.getUsername());
        UserEntity friendUserEntity = userEntityRepository.findUserEntityByUsername(user.getUsername());


        RequestFriendEntity requestFriendEntity = new RequestFriendEntity(loggedUserEntity, friendUserEntity);

        requestFriendEntityRepository.save(requestFriendEntity);
    }

    @Transactional
    public void removeFriend(UserDomain loggedUser, UserDomain friend) {
        List<UserDomain> friendList1 = getFriends(loggedUser.getUsername());

        if (!friendList1.contains(friend)) {
            log.error("Required user is not in friend list.");
            return;
        }

        List<FriendEntity> loggedUserEntities = friendEntityRepository.findAllByUser1UsernameAndUser2Username(loggedUser.getUsername(), friend.getUsername());
        List<FriendEntity> friendEntities = friendEntityRepository.findAllByUser1UsernameAndUser2Username(friend.getUsername(), loggedUser.getUsername());

        for (FriendEntity friendEntity : loggedUserEntities) {
            friendEntityRepository.delete(friendEntity);
        }

        for (FriendEntity friendEntity : friendEntities) {
            friendEntityRepository.delete(friendEntity);
        }

    }

    @Transactional
    public void blockUser(UserDomain loggedUser, UserDomain user) {
        List<UserDomain> blockList = getBlockedUsers(loggedUser.getUsername());

        if (loggedUser.equals(user)) {
            log.error("It is not possible to block yourself.");
            return;
        }

        if (blockList.contains(user)) {
            log.error("Users " + user.getUsername() + " is already blocked.");
            return;
        }

        UserEntity loggedUserEntity = userEntityRepository.findUserEntityByUsername(loggedUser.getUsername());
        UserEntity blockedUserEntity = userEntityRepository.findUserEntityByUsername(user.getUsername());

        BlockEntity blockEntity = new BlockEntity(loggedUserEntity, blockedUserEntity);

        blockEntityRepository.save(blockEntity);
    }

    @Transactional
    public void removeBlock(UserDomain loggedUser, UserDomain user) {
        List<UserDomain> blockList = getBlockedUsers(loggedUser.getUsername());

        if (!blockList.contains(user)) {
            log.error("Required user is not in block list.");
            return;
        }

        List<BlockEntity> loggedUserEntities = blockEntityRepository.findAllByUser1UsernameAndUser2Username(loggedUser.getUsername(), user.getUsername());

        for (BlockEntity blockEntity : loggedUserEntities) {
            blockEntityRepository.delete(blockEntity);
        }

    }

    @Transactional
    private void removeReq(UserDomain loggedUser, UserDomain user) {
        List<UserDomain> requestsList = getSentRequestsToUsers(loggedUser.getUsername());

        if (!requestsList.contains(user)) {
            log.error("Required user is not in requests list.");
            return;
        }

        List<RequestFriendEntity> loggedUserEntities = requestFriendEntityRepository.findAllByUser1UsernameAndUser2Username(loggedUser.getUsername(), user.getUsername());

        for (RequestFriendEntity requestFriendEntity : loggedUserEntities) {
            requestFriendEntityRepository.delete(requestFriendEntity);
        }

    }
}
