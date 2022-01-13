package cz.zcu.fav.pia.sonet.controller.stomp;

import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.dto.*;
import cz.zcu.fav.pia.sonet.service.FriendService;
import cz.zcu.fav.pia.sonet.service.LoggedUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class FriendController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final LoggedUserService loggedUserService;
    private final FriendService friendService;
    private final UserDetailsService userDetailsService;

    @MessageMapping("/friend/add")
    public void addFriend(UserDTO message) {
        friendService.addFriend((UserDomain) userDetailsService.loadUserByUsername(message.getUsername()));
    }

    @MessageMapping("/friend/block")
    public void blockUser(UserDTO message) {
        friendService.blockUser((UserDomain) userDetailsService.loadUserByUsername(message.getUsername()));
    }

    @MessageMapping("/friend/remove")
    public void removeFriend(UserDTO message) {

        friendService.removeFriend((UserDomain) userDetailsService.loadUserByUsername(message.getUsername()));

        simpMessagingTemplate.convertAndSendToUser(message.getUsername(),
                "/friend/removed", new UserDTO(loggedUserService.getUser().getUsername()));
    }

    @MessageMapping("/friend/remove-block")
    public void removeBlock(UserDTO message) {
        friendService.removeBlock((UserDomain) userDetailsService.loadUserByUsername(message.getUsername()));
    }

    @MessageMapping("/friend/remove-req")
    public void removeReq(UserDTO message) {
        friendService.removeReq((UserDomain) userDetailsService.loadUserByUsername(message.getUsername()));
    }

    @MessageMapping("/friend/accept")
    public void acceptFriend(UserDTO message) {
        friendService.acceptFriend((UserDomain) userDetailsService.loadUserByUsername(message.getUsername()));
    }

    @MessageMapping("/friend/decline")
    public void declineFriend(UserDTO message) {
        friendService.declineFriend((UserDomain) userDetailsService.loadUserByUsername(message.getUsername()));
    }

    @MessageMapping("/client/friends")
    public void getFriends() {
        String loggedUser = loggedUserService.getUser().getUsername();
        List<FriendDTO> friendsWithStatus = friendService.getFriendsWithStatus(loggedUser);

        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/friends", friendsWithStatus);
    }

    @MessageMapping("/client/blocked")
    public void getBlocks() {
        String loggedUser = loggedUserService.getUser().getUsername();
        List<BlockedDTO> blocksWithoutStatus = friendService.getBlocks(loggedUser);

        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/blocked", blocksWithoutStatus);
    }

    @MessageMapping("/client/sent-req")
    public void getSentRequests() {
        String loggedUser = loggedUserService.getUser().getUsername();
        List<UserDTO> sentRequests = new ArrayList<>();

        for (UserDomain sentRequest : friendService.getSentRequestsToUsers(loggedUser)) {
            sentRequests.add(new UserDTO(sentRequest.getUsername()));
        }

        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/sent-req", sentRequests);
    }

    @MessageMapping("/client/accept-req")
    public void getAcceptRequests() {
        String loggedUser = loggedUserService.getUser().getUsername();
        List<UserDTO> acceptRequests = new ArrayList<>();

        for (UserDomain acceptRequest : friendService.getAcceptableRequests(loggedUser)) {
            acceptRequests.add(new UserDTO(acceptRequest.getUsername()));
        }

        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/accept-req", acceptRequests);
    }

    @MessageMapping("/client/invite-chat")
    public void inviteToChat(UserDTO message) {
        String loggedUser = loggedUserService.getUser().getUsername();

        boolean chattingLoggedUser = friendService.isChatting(message.getUsername());

        if (chattingLoggedUser) {
            simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/chatting", new UserDTO(loggedUser));
            return;
        }

        boolean chatting = friendService.isChatting(message.getUsername());

        if (!chatting) {
            friendService.startChat(loggedUser, message.getUsername());
            simpMessagingTemplate.convertAndSendToUser(message.getUsername(), "/client/invite-chat", new FriendChatDTO(message.getUsername(), false));

        }

        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/invite-chat", new FriendChatDTO(message.getUsername(), chatting));

    }

    @MessageMapping("/client/send")
    public void sendChatMessage(ChatMessageDTO chatMessage) {
        String loggedUser = loggedUserService.getUser().getUsername();
        String receiver = friendService.getReceiver(loggedUser);

        if (receiver == null) {
            log.error("There is nobody to chat with.");
            return;
        }
        simpMessagingTemplate.convertAndSendToUser(receiver, "/client/send", new ChatMessageDTO(chatMessage.getMessage(), loggedUserService.getUser().getUsername()));

    }

    @MessageMapping("/client/chat-discon")
    public void endChat() {
        String loggedUser = loggedUserService.getUser().getUsername();
        String receiver = friendService.removeChat(loggedUser);

        if (receiver == null) {
            return;
        }

        if (friendService.isOnline(receiver)) {
            simpMessagingTemplate.convertAndSendToUser(receiver, "/client/chat-discon", new UserDTO(loggedUserService.getUser().getUsername()));
        }

    }
}
