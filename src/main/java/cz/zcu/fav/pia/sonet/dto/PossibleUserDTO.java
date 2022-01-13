package cz.zcu.fav.pia.sonet.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PossibleUserDTO {

    private String username;
    private boolean asking;
    private boolean requested;

}
