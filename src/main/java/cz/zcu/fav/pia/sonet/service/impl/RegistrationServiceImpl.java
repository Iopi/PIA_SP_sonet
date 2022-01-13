package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.domain.RoleEnum;
import cz.zcu.fav.pia.sonet.service.RegistrationService;
import cz.zcu.fav.pia.sonet.service.UserService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Objects;

@Service("registrationService")
@Getter
@Setter
@RequiredArgsConstructor
@RequestScope
@Slf4j
public class RegistrationServiceImpl implements RegistrationService {

    private static final String CHANGE_PASSWORD_FORM_NEW_PASSWORD = ":changePasswordForm:password1";

    private final UserService userService;

    private final PasswordEncoder encoder;

    private String email;
    private String newPassword1;
    private String newPassword2;
    private String firstName;
    private String lastName;

    @Override
    public void registerUser(String email, String newPassword1, String newPassword2, String firstName, String lastName) {

        if (!Objects.equals(newPassword1, newPassword2)) {
            FacesContext.getCurrentInstance().addMessage(CHANGE_PASSWORD_FORM_NEW_PASSWORD,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Passwords don't match!", null));
            return;
        }
        if (!StringUtils.hasText(newPassword1)) {
            FacesContext.getCurrentInstance().addMessage(CHANGE_PASSWORD_FORM_NEW_PASSWORD,
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Password cannot be empty!", null));
            return;
        }

        if (userService.addUser(email, encoder.encode(newPassword1), firstName, lastName, RoleEnum.USER.getCode())) {
            FacesContext.getCurrentInstance().addMessage(null,
                    new FacesMessage(FacesMessage.SEVERITY_INFO, "Successfully signed up.", null));
        }
    }

    public void registerUser() {
        registerUser(email, newPassword1, newPassword2, firstName, lastName);
    }

}
