package Integration.integration.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import lombok.Getter;
import lombok.Setter;
import jakarta.persistence.Id;

@Getter
@Setter
@Entity
public class Users {
    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String password;

    @Column(unique = true, nullable = false)
    private String nickname;
}
