package Integration.integration.controller;

import Integration.integration.dto.OAuthUserInfo;
import Integration.integration.entity.Provider;
import Integration.integration.entity.Users;
import Integration.integration.jwt.JwtTokenProvider;
import Integration.integration.service.OAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class LoginSuccessController {

    private final OAuthService oauthService;
    private final JwtTokenProvider jwtTokenProvider;

    @GetMapping("/loginSuccess")
    public ResponseEntity<?> loginSuccess(@AuthenticationPrincipal OAuth2User oAuth2User) {
        if (oAuth2User == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("OAuth2User가 없습니다.");
        }

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

        OAuthUserInfo userInfo = new OAuthUserInfo(provider, providerId, email, name, picture);
        Users user = oauthService.handleOAuthLogin(userInfo);

        // ✅ JWT 생성
        String token = jwtTokenProvider.createToken(user.getEmail(), user.getRole());

        // ✅ 토큰 클라이언트로 응답
        return ResponseEntity.ok(Map.of(
                "accessToken", token,
                "email", user.getEmail(),
                "nickname", user.getNickname()
        ));
    }

}
