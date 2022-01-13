package cz.zcu.fav.pia.sonet.service;

import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.dto.BlockedDTO;
import cz.zcu.fav.pia.sonet.dto.FriendDTO;

import java.util.List;

public interface FriendService {

    void acceptFriend(UserDomain loggedUser, UserDomain friend);

    void acceptFriend(UserDomain friend);

    void declineFriend(UserDomain user);

    void addFriend(UserDomain user);

    void removeFriend(UserDomain friend);

    void blockUser(UserDomain loggedUser, UserDomain user);

    void blockUser(UserDomain user);

    void removeBlock(UserDomain user);

    void removeReq(UserDomain user);

    boolean areFriends(String username1, String username2);

    List<UserDomain> getFriends(String user);

    List<FriendDTO> getFriendsWithStatus(String user);

    List<UserDomain> getBlockedUsers(String user);

    List<UserDomain> getUsersToBeBlockWith(String user);

    boolean isRequested(String loggedUser, String username);

    List<UserDomain> getSentRequestsToUsers(String loggedUser);

    List<UserDomain> getAcceptableRequests(String loggedUser);

    boolean isChatting(String user);

    void startChat(String loggedUser, String user);

    String getReceiver(String loggedUser);

    String removeChat(String username);

    boolean isOnline(String user);

    List<BlockedDTO> getBlocks(String loggedUser);

}
