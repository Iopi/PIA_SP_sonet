package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.dto.PossibleUserDTO;
import cz.zcu.fav.pia.sonet.entity.UserEntity;
import cz.zcu.fav.pia.sonet.repository.UserEntityRepository;
import cz.zcu.fav.pia.sonet.service.FriendService;
import cz.zcu.fav.pia.sonet.service.PossibleUsersService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("possibleUsers")
@RequiredArgsConstructor
@Slf4j
public class PossibleUsersServiceImpl implements PossibleUsersService {

    private final SimpUserRegistry simpUserRegistry;
    private final UserEntityRepository userEntityRepository;
    private final FriendService friendService;

    @Override
    public List<PossibleUserDTO> getPossibleUsers(String loggedUser) {
        List<PossibleUserDTO> possibleUsers = new ArrayList<>();
        List<UserEntity> allUsers;
        boolean requestedToBeFriends;
        boolean askingToBeFriends;

        allUsers = userEntityRepository.findAll();

        List<UserDomain> friends = friendService.getFriends(loggedUser);
        List<UserDomain> blocked = friendService.getBlockedUsers(loggedUser);
        List<UserDomain> blockedBy = friendService.getUsersToBeBlockWith(loggedUser);

        for(UserEntity user : allUsers) {
            if (user.getUsername() == null) {
                continue;
            }

            if (loggedUser != null) {
                if (user.getUsername().equals(loggedUser))
                    continue;
            }

            if(friends.stream().map(UserDomain::getUsername).anyMatch(user.getUsername()::equals)) {
                continue;
            }

            if(blocked.stream().map(UserDomain::getUsername).anyMatch(user.getUsername()::equals)) {
                continue;
            }

            if(blockedBy.stream().map(UserDomain::getUsername).anyMatch(user.getUsername()::equals)) {
                continue;
            }

            requestedToBeFriends = friendService.isRequested(loggedUser, user.getUsername());
            askingToBeFriends = friendService.isRequested(user.getUsername(), loggedUser);

            possibleUsers.add(new PossibleUserDTO(user.getUsername(), askingToBeFriends, requestedToBeFriends));
        }

        return possibleUsers;
    }

    @Override
    public List<PossibleUserDTO> getFindPossibleUsers(String loggedUser, String usernameStart) {
        List<PossibleUserDTO> possibleUsers = getPossibleUsers(loggedUser);
        List<PossibleUserDTO> suitablePossibleUsers = new ArrayList<>();

        for (PossibleUserDTO possibleUser : possibleUsers) {
            if (possibleUser.getUsername().startsWith(usernameStart)) {
                suitablePossibleUsers.add(possibleUser);
            }
        }

        return suitablePossibleUsers;
    }
}
