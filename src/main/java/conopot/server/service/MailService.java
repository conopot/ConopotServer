package conopot.server.service;

import conopot.server.config.BaseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import static conopot.server.config.BaseResponseStatus.MAIL_SEND_ERROR;

@Service @Slf4j
public class MailService {

    private JavaMailSender mailSender;
    private String[] mailTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.mailTo = new String[] {"kimtaehyun98@naver.com", "su7651@gmail.com", "songsuheon97@gmail.com"};
    }

    public void mailSend() throws BaseException {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(mailTo);
            message.setFrom(mailFrom);
            message.setSubject("코노팟 음악 데이터 갱신 에러 알림");
            message.setText("음악 데이터를 갱신하는데 에러가 발생했습니다. 백엔드 개발자들은 빠르게 수정 부탁드립니다^^");
            mailSender.send(message);
            log.info("에러 메일을 성공적으로 전송하였습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            throw new BaseException(MAIL_SEND_ERROR);
        }
    }
}
