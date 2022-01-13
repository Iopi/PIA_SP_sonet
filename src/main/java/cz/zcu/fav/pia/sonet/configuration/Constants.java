package cz.zcu.fav.pia.sonet.configuration;

import cz.zcu.fav.pia.sonet.domain.RoleEnum;

public final class Constants {

    public static final String INIT_ADMIN_USERNAME = "admin@init.com";
    public static final String INIT_USER_USERNAME = "user@init.com";

    public static final String INIT_ADMIN_PASSWORD = "admin";
    public static final String INIT_USER_PASSWORD = "user";

    public static final RoleEnum INIT_ROLE_ADMIN = RoleEnum.ADMIN;
    public static final RoleEnum INIT_ROLE_USER = RoleEnum.USER;

    public static final String LOGIN_REGEX = "(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9]))\\.){3}(?:(2(5[0-5]|[0-4][0-9])|1[0-9][0-9]|[1-9]?[0-9])|[a-z0-9-]*[a-z0-9]:(?:[\\x01-\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])";

    private Constants() {
    }
}
