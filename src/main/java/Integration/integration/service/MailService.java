package Integration.integration.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender mailSender;

    public void sendVerificationCodeEmail(String toEmail, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 보내는 메일
            helper.setFrom("2whwhw@naver.com");

            helper.setTo(toEmail);
            helper.setSubject("[MyApp] 비밀번호 재설정 인증 코드");
            helper.setText(
                    "<p>인증코드: <strong>" + code + "</strong></p><p>10분 이내에 입력해주세요.</p>",
                    true
            );

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("메일 전송 실패", e);
        }
    }
}

