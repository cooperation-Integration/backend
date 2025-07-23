package Integration.integration.service;

import Integration.integration.dto.OAuthUserInfo;
import Integration.integration.entity.UserOauths;
import Integration.integration.entity.Users;
import Integration.integration.repository.UserOauthRepository;
import Integration.integration.repository.UsersRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuthService {

    private final UsersRepository usersRepository;
    private final UserOauthRepository userOauthRepository;

    @Transactional
    public Users handleOAuthLogin(OAuthUserInfo oAuthInfo) {
        // 1. 기존 user_oauths 확인
        Optional<UserOauths> existing = userOauthRepository.findByProviderAndProviderId(
                oAuthInfo.getProvider(), oAuthInfo.getProviderId());

        if (existing.isPresent()) {
            Users user = existing.get().getUser();
            // 💡 LazyInitializationException 방지용 강제 초기화
            user.getEmail();
            user.getNickname();
            return user;
        }

        // 2. users 테이블에 새 유저 생성
        Users user = Users.builder()
                .email(oAuthInfo.getEmail())
                .nickname(oAuthInfo.getName())
                .profileImage(oAuthInfo.getPicture())
                .emailVerified(true)
                .role("USER")
                .isActive(true)
                .build();

        usersRepository.save(user);

        // 3. user_oauths 테이블에 연동 정보 저장
        UserOauths oauth = new UserOauths();
        oauth.setUser(user);
        oauth.setProvider(oAuthInfo.getProvider());
        oauth.setProviderId(oAuthInfo.getProviderId());
        oauth.setEmail(oAuthInfo.getEmail());

        userOauthRepository.save(oauth);

        return user;
    }

}

