package vocabulary.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.security.core.context.SecurityContextHolder;

@Entity(name = "users")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    //private Long id;
    private String username;
    private String password;
    private Boolean enabled;

    public static String getCurrent() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }
}
