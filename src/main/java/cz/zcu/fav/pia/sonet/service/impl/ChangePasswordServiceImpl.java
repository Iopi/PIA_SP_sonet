package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.entity.UserEntity;
import cz.zcu.fav.pia.sonet.repository.UserEntityRepository;
import cz.zcu.fav.pia.sonet.service.ChangePasswordService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.annotation.RequestScope;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import java.util.Objects;

@Service("changePasswordService")
@Getter
@Setter
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
@RequestScope
public class ChangePasswordServiceImpl implements ChangePasswordService {

	private static final String CHANGE_PASSWORD_FORM_NEW_PASSWORD = ":changePasswordForm:newPassword1";
	private static final String CHANGE_PASSWORD_FORM_OLD_PASSWORD = ":changePasswordForm:oldPassword";
	private static final String CHANGE_PASSWORD_FORM = ":changePasswordForm";
	private final UserEntityRepository userEntityRepository;
	private final PasswordEncoder encoder;

	private String oldPassword;
	private String newPassword1;
	private String newPassword2;

	@Override
	public void changePassword(String old, String new1, String new2) {
		if (!Objects.equals(new1, new2)) {
			FacesContext.getCurrentInstance().addMessage(CHANGE_PASSWORD_FORM_NEW_PASSWORD,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "New passwords don't match!", null));
			return;
		}
		if (!StringUtils.hasText(new1)) {
			FacesContext.getCurrentInstance().addMessage(CHANGE_PASSWORD_FORM_NEW_PASSWORD,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "New password cannot be empty!", null));
			return;
		}

		SecurityContext context = SecurityContextHolder.getContext();
		Authentication authentication = context.getAuthentication();
		String storedPassword = ((UserDomain) authentication.getPrincipal()).getPassword();

		if (!encoder.matches(old, storedPassword)) {
			FacesContext.getCurrentInstance().addMessage(CHANGE_PASSWORD_FORM_OLD_PASSWORD,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, "Old password doesn't match!", null));
			return;
		}

		String username = authentication.getName();
		UserEntity userEntity = userEntityRepository.findUserEntityByUsername(username);

		if (userEntity == null) {
			throw new RuntimeException("Logged user doesn't exist!");
		}

		userEntity.setPassword(encoder.encode(new1));

		userEntityRepository.save(userEntity);

		FacesContext.getCurrentInstance().addMessage(CHANGE_PASSWORD_FORM,
				new FacesMessage(FacesMessage.SEVERITY_INFO, "Password successfully changed.", null));
	}

	public void changePassword() {
		this.changePassword(oldPassword, newPassword1, newPassword2);
	}

}
