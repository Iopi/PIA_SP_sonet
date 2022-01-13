package cz.zcu.fav.pia.sonet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostDTO {

    private String username;
    private String time;
    private String text;
    private boolean announcement;

}
