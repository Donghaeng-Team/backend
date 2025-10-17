package com.bytogether.userservice.service;

import com.bytogether.userservice.model.VerifyType;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailSendException;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import java.util.UUID;


@Service
@RequiredArgsConstructor
@Slf4j
@SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
public class MailService {

    private final JavaMailSenderImpl mailSender;
    private final UserVerifyService userVerifyService;

    public void sendAuthEmailVerify(String email, String nickname) {
        String token = UUID.randomUUID().toString();
        String verifyUrl = "http://localhost:3000/verify/email?token=" + token + "&email=" + email;
        try {
            MimeMessage mimemessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimemessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("함께사요 서비스의 이메일 인증을 완료해주세요");
            mimeMessageHelper.setFrom("bytogethermaster@gmail.com");

            String htmlContent = """
                        <html>
                        <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                          <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px;">
                            <h2 style="color: #ff4d4f;"> "함께 사요" 서비스의 인증 메일입니다 </h2>
                            <p><strong>%s</strong>님, 이웃들과 부담은 덜고 서로 돕는 "함께 사요" 서비스에 가입해 주셔서 감사합니다.</p>
                            <p>오늘 하루도 행복하세요</p>
                            <div style="text-align: center; margin: 30px 0;">
                              <a href="%s" style="background-color: #ff4d4f; color: #fff; padding: 12px 24px; border-radius: 6px; text-decoration: none; font-weight: bold;">
                                이메일 인증하기
                              </a>
                            </div>
                            <p style="font-size: 14px; color: #555;">본 메일을 통한 인증은 수신 후 24시간 동안 가능합니다.</p>
                            <p style="font-size: 14px; color: #555;">이 메일 수신을 다시 받으시려면 로그인 페이지의 이메일 재발신을 이용해주세요</p>
                          </div>
                        </body>
                        </html>
                    """.formatted(nickname, verifyUrl);
            mimeMessageHelper.setText(htmlContent, true);
            mailSender.send(mimemessage);
            userVerifyService.save(email, token, VerifyType.EMAIL);
        } catch (MessagingException e) {
            throw new MailSendException("이메일 발송에 실패했습니다.", e);
        }
    }

    public void sendPasswordEmailVerify(String email, String nickname){
        String token = UUID.randomUUID().toString();
        String verifyUrl = "http://localhost:3000/verify/password?token=" + token + "&email=" + email;
        try{
            MimeMessage mimemessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimemessage, true, "UTF-8");
            mimeMessageHelper.setTo(email);
            mimeMessageHelper.setSubject("함께사요 서비스의 이메일 인증을 완료해주세요");
            mimeMessageHelper.setFrom("bytogethermaster@gmail.com");

            String htmlContent = """
              <html>
              <body style="font-family: Arial, sans-serif; background-color: #f9f9f9; padding: 20px;">
                <div style="max-width: 600px; margin: auto; background-color: #ffffff; padding: 30px; border-radius: 10px;">
                  <h2 style="color: #ff4d4f;">🔐 비밀번호 재설정 요청</h2>
                  <p><strong>%s</strong>님, 비밀번호 재설정을 위한 링크를 보내드립니다.</p>
                  <p>아래 버튼을 눌러 비밀번호 재설정 페이지로 이동해주세요.</p>
                  <div style="text-align: center; margin: 30px 0;">
                    <a href="%s" style="background-color: #ff4d4f; color: #fff; padding: 12px 24px; border-radius: 6px; text-decoration: none; font-weight: bold;">
                      비밀번호 재설정
                    </a>
                  </div>
                  <p style="font-size: 14px; color: #555;">본 메일을 통한 인증은 수신 후 1시간 동안 가능합니다.</p>
                  <p style="font-size: 13px; color: #888;">본인이 요청한 것이 아니라면 이 이메일을 무시하셔도 됩니다.</p>
                </div>
              </body>
              </html>
          """.formatted(nickname, verifyUrl);

            mimeMessageHelper.setText(htmlContent, true);
            mailSender.send(mimemessage);

            userVerifyService.save(email, token, VerifyType.PASSWORD);

        }catch(MessagingException e){
            throw new MailSendException("이메일 발송에 실패했습니다.", e);
        }
    }
}
