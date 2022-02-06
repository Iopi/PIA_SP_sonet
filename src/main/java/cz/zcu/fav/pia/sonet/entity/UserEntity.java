package cz.zcu.fav.pia.sonet.entity;

import cz.zcu.fav.pia.sonet.configuration.Constants;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "user_tab")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class UserEntity extends AbstractEntity {

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 1, max = 100)
    @Column(name = "user_name", length = 100, nullable = false, unique = true)
    private String username;

    @NotNull
    @Column(name = "password_hash", nullable = false)
    private String password;

    //@NotNull
    @Size(min = 1, max = 100)
    @Column(name = "first_name", length = 100, nullable = true)
    private String firstName;

    //@NotNull
    @Size(min = 1, max = 100)
    @Column(name = "last_name", length = 100, nullable = true)
    private String lastName;

    @NotNull
    @Column(nullable = false)
    private boolean isDeleted = false;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role_tab",
            joinColumns = @JoinColumn(name = "user_tab_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_tab_id", referencedColumnName = "id")
    )
    private Set<RoleEntity> roles = new HashSet<>();

    /*@ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_post_tab",
            joinColumns = @JoinColumn(name = "user_tab_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "post_tab_id", referencedColumnName = "id")
    )
    private Set<PostEntity> likes = new HashSet<>();*/

    public UserEntity(@NotNull @Pattern(regexp = Constants.LOGIN_REGEX) @Size(min = 1, max = 100) String username, @NotNull String password, @NotNull @Size(min = 1, max = 100) String firstName, @NotNull @Size(min = 1, max = 100) String lastName) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
    }

}
