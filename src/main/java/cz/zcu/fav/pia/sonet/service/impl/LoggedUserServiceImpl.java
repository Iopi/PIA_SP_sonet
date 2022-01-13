package cz.zcu.fav.pia.sonet.service.impl;

import cz.zcu.fav.pia.sonet.domain.UserDomain;
import cz.zcu.fav.pia.sonet.service.LoggedUserService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service("loggedUser")
public class LoggedUserServiceImpl  implements LoggedUserService {

	public UserDomain getUser() {
		final var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication != null) {
			return (UserDomain) authentication.getPrincipal();
		}

		return null;
	}

}
