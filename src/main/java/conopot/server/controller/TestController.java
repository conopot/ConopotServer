package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.config.BaseResponseStatus;
import conopot.server.config.FilePath;
import conopot.server.dto.Music;
import conopot.server.repository.FileRepository;
import conopot.server.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static conopot.server.config.BaseResponseStatus.DOCKER_MAKE_IMAGE_ERROR;

@RestController @Slf4j
public class TestController{

    private final FileService fileService;
    private final CrawlingService crawlingService;
    private final MatchingService matchingService;
    private final AwsS3Service awsS3Service;
    private final MailService mailService;
    private final FileRepository fileRepository;
    private final VersionService versionService;
    private final AwsDynamoDbService awsDynamoDbService;
    private FilePath filePath;

    @Autowired
    public TestController(FileService fileService, CrawlingService crawlingService, MatchingService matchingService, AwsS3Service awsS3Service, MailService mailService, FileRepository fileRepository, VersionService versionService, AwsDynamoDbService awsDynamoDbService) {
        this.fileService = fileService;
        this.crawlingService = crawlingService;
        this.matchingService = matchingService;
        this.awsS3Service = awsS3Service;
        this.mailService = mailService;
        this.fileRepository = fileRepository;
        this.versionService = versionService;
        this.awsDynamoDbService = awsDynamoDbService;
        filePath = new FilePath();
    }


    @GetMapping("/testFileIO")
    public BaseResponse<String> testFileApi(){
        try{
            fileService.initData();
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

    @GetMapping("/testMail")
    public BaseResponse<String> testMailApi() throws Exception{
        try{
            mailService.failMailSend();
            return new BaseResponse<String>("PASS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testFileSize")
    public BaseResponse<String> testFileSizeApi() throws Exception{
        try{
            String ret = fileService.checkFileSize("/", 3*1024) ? "PASS" : "FAIL";
            return new BaseResponse<String>(ret);
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testDocker")
    public BaseResponse<String> testDockerApi(){
        return new BaseResponse<String>("Docker를 정상적으로 실행했습니다.");
    }


    @GetMapping("/rootDir")
    public void testRootDir() throws BaseException, IOException {
        fileRepository.savedText("Hello!", "/hello.txt");
        // 이미 파일이 존재하면 삭제하기
        File oldFile = new File("/hello.txt");
        if(oldFile.exists()) {
            log.info("해당 경로에 파일이 이미 존재하고 있습니다!");
        }

        FileReader file_reader = new FileReader(oldFile);
        int cur = 0; String temp = "";
        while ((cur = file_reader.read()) != -1) {
            char c = (char) cur;
            temp += c;
        }
        log.info("Temp is : {}", temp);
    }

    @GetMapping("/testSuccessMail")
    public BaseResponse<String> testSuccessMail() {
        try{
            mailService.successMailSend();
            return new BaseResponse<String>("SUCCESS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testSavedVersion")
    public BaseResponse<String> testSavedVersion() {
        try{
            versionService.savedVersion("SUCCESS");
            return new BaseResponse<String>("SUCCESS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }

    @GetMapping("/testKY")
    public void testKYCrawling(){
        try {
            crawlingService.savedFamousKY();
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    @GetMapping("/testDynamo")
    public void testDynamoDB(){
        try{
            awsDynamoDbService.createItem("100003", "test3");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
}
