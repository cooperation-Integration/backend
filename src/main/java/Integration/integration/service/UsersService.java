package Integration.integration.service;

import Integration.integration.entity.Users;
import Integration.integration.exception.CustomException;
import Integration.integration.dto.request.LoginRequest;
import Integration.integration.dto.request.RegisterRequest;
import Integration.integration.jwt.JwtTokenProvider;
import Integration.integration.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class UsersService {
    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, String> redisTemplate;
    private final MailService mailService;

    public void register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new CustomException("EMAIL_EXISTS", "이미 사용 중인 이메일입니다.", HttpStatus.CONFLICT);
        }

        Users users = new Users();
        users.setEmail(request.getEmail());
        users.setPassword(passwordEncoder.encode(request.getPassword()));
        users.setNickname(request.getNickname());

        userRepository.save(users);
    }

    public String login(LoginRequest request) {
        Users users = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new CustomException("USER_NOT_FOUND", "이메일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        if (!passwordEncoder.matches(request.getPassword(), users.getPassword())) {
            throw new CustomException("INVALID_PASSWORD", "비밀번호가 일치하지 않습니다.", HttpStatus.UNAUTHORIZED);
        }

        // 실제 JWT 토큰 발급
        String token = jwtTokenProvider.createToken(users.getEmail(), "USER");

        return token;
    }

    // 닉네임으로 이메일 찾기
    public String findEmailByNickname(String nickname) {
        return userRepository.findByNickname(nickname)
                .map(Users::getEmail)
                .orElseThrow(() -> new CustomException("EMAIL_NOT_FOUND", "해당 닉네임의 이메일이 없습니다.", HttpStatus.NOT_FOUND));
    }

    // 인증코드 메일로 전송
    public void sendVerificationCode(String email) {
        Users users = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException("EMAIL_NOT_FOUND", "이메일을 찾을 수 없습니다.", HttpStatus.NOT_FOUND));

        String code = String.format("%06d", new Random().nextInt(1000000)); // 6자리 코드
        redisTemplate.opsForValue().set("pwd:verify:" + email, code, Duration.ofMinutes(10));

        mailService.sendVerificationCodeEmail(email, code);
    }
}
