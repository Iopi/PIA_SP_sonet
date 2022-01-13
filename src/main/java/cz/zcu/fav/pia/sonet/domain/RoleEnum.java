package cz.zcu.fav.pia.sonet.domain;

public enum RoleEnum {
    ADMIN("ADMIN", "Administrator"),
    USER("USER", "Basic user");

    private final String code;
    private final String name;

    RoleEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
