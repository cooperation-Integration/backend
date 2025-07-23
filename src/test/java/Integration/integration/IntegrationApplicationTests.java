package Integration.integration;

import Integration.integration.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;


@ActiveProfiles("test")
@SpringBootTest(classes = { IntegrationApplication.class, TestSecurityConfig.class })
class IntegrationApplicationTests {
}
