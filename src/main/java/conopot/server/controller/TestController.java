package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.config.BaseResponseStatus;
import conopot.server.config.FilePath;
import conopot.server.repository.FileRepository;
import conopot.server.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static conopot.server.config.BaseResponseStatus.DOCKER_MAKE_IMAGE_ERROR;

@RestController
public class TestController{

    private final FileService fileService;
    private final CrawlingService crawlingService;
    private final MatchingService matchingService;
    private final AwsS3Service awsS3Service;
    private final MailService mailService;
    private final FileRepository fileRepository;
    private FilePath filePath;

    @Autowired
    public TestController(FileService fileService, CrawlingService crawlingService, MatchingService matchingService, AwsS3Service awsS3Service, MailService mailService, FileRepository fileRepository) {
        this.fileService = fileService;
        this.crawlingService = crawlingService;
        this.matchingService = matchingService;
        this.awsS3Service = awsS3Service;
        this.mailService = mailService;
        this.fileRepository = fileRepository;
        filePath = new FilePath();
    }


    @GetMapping("/testFileIO")
    public BaseResponse<String> testFileApi(){
        try{
            fileService.getAllData();
            return new BaseResponse<String>("PASS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testCrawling")
    public BaseResponse<String> testCrawlingApi() throws IOException {
        try{
            crawlingService.crawlingLatest();
            crawlingService.crawlingFamous();
            return new BaseResponse<String>("PASS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testMatching")
    public BaseResponse<String> testMatchingApi() throws IOException {
        try{
            crawlingService.crawlingLatest(); // 이 안에 matching 들어감
            return new BaseResponse<String>("PASS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testZip")
    public BaseResponse<String> testZipApi() {
        try{
            fileService.makeZip(filePath.ZIP_FILE);
            return new BaseResponse<String>("PASS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testS3")
    public BaseResponse<String> testS3Api() throws Exception{
        try{
            awsS3Service.uploadZipFile();
            return new BaseResponse<String>("PASS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testMail")
    public BaseResponse<String> testMailApi() throws Exception{
        try{
            mailService.mailSend();
            return new BaseResponse<String>("PASS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testFileSize")
    public BaseResponse<String> testFileSizeApi() throws Exception{
        try{
            String ret = fileService.checkFileSize() ? "PASS" : "FAIL";
            return new BaseResponse<String>(ret);
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testDocker")
    public BaseResponse<String> testDockerApi(){
        return new BaseResponse<String>("Docker를 정상적으로 실행했습니다.");
    }

    @GetMapping("/testCF")
    public BaseResponse<String> testCloudFront(){
        try{
            fileRepository.getZipFileFromS3();
            return new BaseResponse<String>("CloudFront로부터 zip 파일을 다운로드받았습니다.");
        }
        catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}
