package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.service.CrawlingService;
import conopot.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
public class TestController{

    private final FileService fileService;
    private final CrawlingService crawlingService;

    public TestController(FileService fileService, CrawlingService crawlingService) {
        this.fileService = fileService;
        this.crawlingService = crawlingService;
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
            return new BaseResponse<String>("PASS");
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
    }
}
