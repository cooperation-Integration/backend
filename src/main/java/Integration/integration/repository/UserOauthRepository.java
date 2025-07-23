package Integration.integration.repository;

import Integration.integration.entity.UserOauths;
import Integration.integration.entity.Provider;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserOauthRepository extends JpaRepository<UserOauths, Long> {
    Optional<UserOauths> findByProviderAndProviderId(Provider provider, String providerId);
}