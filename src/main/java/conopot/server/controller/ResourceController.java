package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.config.FilePath;
import lombok.extern.java.Log;
import conopot.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.logging.Logger;

import static conopot.server.config.BaseResponseStatus.SUCCESS;

@RestController
public class ResourceController {

    private final CrawlingService crawlingService;
    private final FileService fileService;
    private final AwsS3Service awsS3Service;
    private final MailService mailService;
    private final VersionService versionService;
    private FilePath filePath;

    public ResourceController(CrawlingService crawlingService, FileService fileService, AwsS3Service awsS3Service, MailService mailService, VersionService versionService) {
        this.crawlingService = crawlingService;
        this.fileService = fileService;
        this.awsS3Service = awsS3Service;
        this.mailService = mailService;
        this.versionService = versionService;
        this.filePath = new FilePath();
    }


    @GetMapping("/music/update")
    public BaseResponse<String> updateMusic() throws Exception {
        try{
            fileService.initData();
            crawlingService.crawlingLatest();
            crawlingService.crawlingFamous();
            fileService.makeZip(filePath.ZIP_FILE);
            awsS3Service.uploadMusicFiles();
            versionService.savedVersion("SUCCESS");
            mailService.successMailSend();
        } catch(BaseException e){
            versionService.savedVersion("FAIL");
            mailService.failMailSend();
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>(SUCCESS);
    }
}
