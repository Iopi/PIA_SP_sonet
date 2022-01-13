package cz.zcu.fav.pia.sonet.controller.stomp;

import cz.zcu.fav.pia.sonet.dto.CheckLengthDTO;
import cz.zcu.fav.pia.sonet.dto.PossibleUserDTO;
import cz.zcu.fav.pia.sonet.dto.UserDTO;
import cz.zcu.fav.pia.sonet.service.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
public class PossibleUsersController {

    private final SimpMessagingTemplate simpMessagingTemplate;
    private final PossibleUsersService possibleUsersService;
    private final LoggedUserService loggedUserService;

    @MessageMapping("/client/all")
    public void possibleUsers() {
        String loggedUser = loggedUserService.getUser().getUsername();
        List<PossibleUserDTO> possibleUsers = possibleUsersService.getPossibleUsers(loggedUser);

        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/all", possibleUsers);
    }

    @MessageMapping("/client/find")
    public void findPossibleUsers(UserDTO message) {
        if (message == null) {
            return;
        }
        String loggedUser = loggedUserService.getUser().getUsername();
        List<PossibleUserDTO> possibleUsers = possibleUsersService.getFindPossibleUsers(loggedUser, message.getUsername());

        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/find", possibleUsers);
    }

    @MessageMapping("/client/check")
    public void checkUsernameLength(UserDTO message) {
        if (message == null) {
            return;
        }
        CheckLengthDTO check = new CheckLengthDTO(message.getUsername(), true);
        try {
            if (message.getUsername().length() < 3) {
                check.setAccepted(false);
            }
        } catch (Exception e) {
            check.setAccepted(false);
        }


        simpMessagingTemplate.convertAndSendToUser(loggedUserService.getUser().getUsername(), "/client/check", check);
    }

}
