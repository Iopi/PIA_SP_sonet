package cz.zcu.fav.pia.sonet.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "post_tab")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PostEntity extends AbstractEntity {

    @NotNull
    @Size(min = 1, max = 50)
    private String time;

    @NotNull
    @Size(min = 1, max = 255)
    private String text;

    @NotNull
    private boolean announcement;

    @NotNull
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_1", referencedColumnName = "id")
    private UserEntity user1;

}
