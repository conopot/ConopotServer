package conopot.server.service;

import conopot.server.config.BaseException;
import conopot.server.config.FilePath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

import java.io.File;

import static conopot.server.config.BaseResponseStatus.FAIL_MAIL_SEND_ERROR;

@Service @Slf4j
public class MailService {

    private JavaMailSender mailSender;
    private String[] mailTo;

    @Value("${spring.mail.username}")
    private String mailFrom;

    private FilePath filePath;

    @Autowired
    public MailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.mailTo = new String[] {"kimtaehyun98@naver.com", "su7651@gmail.com", "songsuheon97@gmail.com"};
        this.filePath = new FilePath();
    }

    public void failMailSend() throws BaseException {
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
            throw new BaseException(FAIL_MAIL_SEND_ERROR);
        }
    }

    public void successMailSend() throws BaseException {
        try {
            MimeMessage mail = mailSender.createMimeMessage();
            MimeMessageHelper mailHelper = new MimeMessageHelper(mail,true,"UTF-8");

            mailHelper.setFrom(mailFrom);
            mailHelper.setTo("kimtaehyun98@naver.com");
            mailHelper.setSubject("코노팟 음악 데이터 갱신 성공 알림");
            mailHelper.setText("이번 주에도 무사히 음악 데이터 갱신을 완료했습니다^^");
            mailHelper.addAttachment("Musics.zip", new File(filePath.DOCKER_MUSICS_ZIP_FILE));
            mailHelper.addAttachment("MatchingFiles.zip", new File(filePath.DOCKER_MATCHINGS_ZIP_FILE));

            mailSender.send(mail);
            log.info("성공 메일을 성공적으로 전송하였습니다.");
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
