package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.config.FilePath;
import conopot.server.service.AwsS3Service;
import conopot.server.service.CrawlingService;
import conopot.server.service.FileService;
import conopot.server.service.MatchingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController{

    private final FileService fileService;
    private final CrawlingService crawlingService;
    private final MatchingService matchingService;
    private final AwsS3Service awsS3Service;
    private FilePath filePath;

    @Autowired
    public TestController(FileService fileService, CrawlingService crawlingService, MatchingService matchingService, AwsS3Service awsS3Service) {
        this.fileService = fileService;
        this.crawlingService = crawlingService;
        this.matchingService = matchingService;
        this.awsS3Service = awsS3Service;
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
}
