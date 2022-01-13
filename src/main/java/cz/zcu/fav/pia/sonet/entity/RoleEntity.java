package cz.zcu.fav.pia.sonet.entity;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "role_tab")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class RoleEntity extends AbstractEntity {

    @NotNull
    @Size(min = 1, max = 20)
    @Column(length = 20, nullable = false, unique = true)
    private String code;

    @NotNull
    @Size(min = 1, max = 50)
    @Column(length = 50, nullable = false)
    private String name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.EAGER)
    private List<UserEntity> users;

    public RoleEntity(String code, String name) {
        this.code = code;
        this.name = name;
    }

}
