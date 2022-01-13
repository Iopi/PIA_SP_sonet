package cz.zcu.fav.pia.sonet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FriendDTO {

    String username;
    int status; // 0 - offline, 1 - online

}
