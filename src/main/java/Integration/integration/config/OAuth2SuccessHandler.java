package Integration.integration.config;

import Integration.integration.dto.OAuthUserInfo;
import Integration.integration.entity.Provider;
import Integration.integration.entity.Users;
import Integration.integration.jwt.JwtTokenProvider;
import Integration.integration.service.OAuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final OAuthService oauthService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        Map<String, Object> attributes = oAuth2User.getAttributes();

        String providerId, email, name, picture;
        Provider provider;

        if (attributes.containsKey("kakao_account")) {
            provider = Provider.KAKAO;
            providerId = String.valueOf(attributes.get("id"));

            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            email = (String) kakaoAccount.get("email");

            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");
            name = (String) profile.get("nickname");
            picture = (String) profile.get("profile_image_url");
        } else {
            provider = Provider.GOOGLE;
            providerId = (String) attributes.get("sub");
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
            picture = (String) attributes.get("picture");
        }

        // DB 저장 또는 기존 유저 조회
        OAuthUserInfo userInfo = new OAuthUserInfo(provider, providerId, email, name, picture);
        Users user = oauthService.handleOAuthLogin(userInfo);

        // JWT 발급
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());

        // JSON 응답
        response.setContentType("application/json;charset=UTF-8");
        new ObjectMapper().writeValue(response.getWriter(), Map.of(
                "accessToken", token,
                "email", user.getEmail(),
                "nickname", user.getNickname()
        ));
    }
}

