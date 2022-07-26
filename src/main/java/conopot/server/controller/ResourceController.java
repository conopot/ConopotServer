package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.config.FilePath;
import conopot.server.service.AwsS3Service;
import conopot.server.service.CrawlingService;
import conopot.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static conopot.server.config.BaseResponseStatus.SUCCESS;

@RestController
public class ResourceController {

    private final CrawlingService crawlingService;
    private final FileService fileService;
    private final AwsS3Service awsS3Service;
    private FilePath filePath;

    @Autowired
    public ResourceController(CrawlingService crawlingService, FileService fileService, AwsS3Service awsS3Service) {
        this.crawlingService = crawlingService;
        this.fileService = fileService;
        this.awsS3Service = awsS3Service;
        this.filePath = new FilePath();
    }

    @GetMapping("/music/update")
    public BaseResponse<String> updateMusic() throws Exception {
        try{
            crawlingService.crawlingLatest();
            crawlingService.crawlingFamous();
            fileService.makeZip(filePath.ZIP_FILE);
            awsS3Service.uploadZipFile();
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>(SUCCESS);
    }
}
