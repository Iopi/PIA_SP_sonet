package cz.zcu.fav.pia.sonet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendChatDTO {

    String username;
    boolean chatting;

}
