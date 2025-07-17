package Integration.integration.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;

import org.springframework.http.HttpHeaders;

@RestController
public class LoginSuccessController {

    private final OAuth2AuthorizedClientService authorizedClientService;

    public LoginSuccessController(OAuth2AuthorizedClientService authorizedClientService) {
        this.authorizedClientService = authorizedClientService;
    }

    @GetMapping("/loginSuccess")
    @ResponseBody
    public String getUserInfo(@AuthenticationPrincipal OAuth2User oAuth2User,
                              OAuth2AuthenticationToken authentication) {

        OAuth2AuthorizedClient client = authorizedClientService.loadAuthorizedClient(
                authentication.getAuthorizedClientRegistrationId(),
                authentication.getName()
        );

        String accessToken = client.getAccessToken().getTokenValue();

        // Google People API 호출
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                "https://people.googleapis.com/v1/people/me?personFields=birthdays,genders",
                HttpMethod.GET,
                entity,
                String.class
        );

        // 기본 OAuth2User 정보 + 생일/성별 출력
        return """
                <h2>기본 로그인 정보</h2>
                <pre>%s</pre>
                <h2>Google 생일/성별 응답</h2>
                <pre>%s</pre>
                """.formatted(oAuth2User.getAttributes(), response.getBody());
    }
}
