package cz.zcu.fav.pia.sonet.service;

import cz.zcu.fav.pia.sonet.domain.UserInfoDomain;

public interface UserService {

    boolean addUser(String username, String password, String firstName, String lastName, String... roles);

    UserInfoDomain updateUser(String username, UserInfoDomain userInfoDomain);

}
