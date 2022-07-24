package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController{

    private final FileService fileService;

    @Autowired
    public TestController(FileService fileService) {
        this.fileService = fileService;
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
}
