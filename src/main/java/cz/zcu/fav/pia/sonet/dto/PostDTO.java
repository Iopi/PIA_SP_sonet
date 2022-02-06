package cz.zcu.fav.pia.sonet.dto;

import cz.zcu.fav.pia.sonet.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class PostDTO {

    private String uuid;
    private String username;
    private String time;
    private String text;
    private boolean announcement;
    private List<String> userslikes;

}
