package Integration.integration.controller;

import Integration.integration.dto.request.LoginRequest;
import Integration.integration.dto.request.RegisterRequest;
import Integration.integration.dto.response.ApiResponse;
import Integration.integration.jwt.JwtTokenProvider;
import Integration.integration.service.UsersService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UsersController {

    private final UsersService userService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<?>> register(@RequestBody @Valid RegisterRequest request) {
        userService.register(request);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@RequestBody LoginRequest request) {
        String token = userService.login(request);
        return ResponseEntity.ok(ApiResponse.success(Map.of("accessToken", token)));
    }

    // 이메일 찾기
    @GetMapping("/find-email")
    public ResponseEntity<ApiResponse<?>> findEmail(@RequestParam String nickname) {
        String email = userService.findEmailByNickname(nickname);
        return ResponseEntity.ok(ApiResponse.success(Map.of("email", email)));
    }

    // 인증코드 전송
    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<?>> sendCode(@RequestBody Map<String, String> request) {
        userService.sendVerificationCode(request.get("email"));
        return ResponseEntity.ok(ApiResponse.success("메일로 인증코드가 전송되었습니다."));
    }
}
