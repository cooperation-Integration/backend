package Integration.integration.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import Integration.integration.entity.Provider;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OAuthUserInfo {
    private Provider provider;
    private String providerId;
    private String email;
    private String name;
    private String picture;
}
