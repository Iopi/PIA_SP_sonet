package cz.zcu.fav.pia.sonet.service;

import cz.zcu.fav.pia.sonet.dto.PossibleUserDTO;

import java.util.List;

public interface PossibleUsersService {
    List<PossibleUserDTO> getPossibleUsers(String loggedUser);

    List<PossibleUserDTO> getFindPossibleUsers(String loggedUser, String usernameStart);
}
