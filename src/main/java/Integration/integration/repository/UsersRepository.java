package Integration.integration.repository;

import Integration.integration.entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository extends JpaRepository<Users, Long> {
    Optional<Users> findByEmail(String email);
    Optional<Users> findByNickname(String nickname);
    boolean existsByEmail(String email);
}
