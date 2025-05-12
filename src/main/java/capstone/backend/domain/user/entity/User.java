package capstone.backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "users")
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userId")
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String role;

    @Builder
    public User(String username, String password, String role) {
        this.username = username;
        this.password = password;
        this.role = role;
    }
}
