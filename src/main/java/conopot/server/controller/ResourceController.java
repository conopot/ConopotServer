package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.service.CrawlingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static conopot.server.config.BaseResponseStatus.SUCCESS;

@RestController
public class ResourceController {

    private final CrawlingService crawlingService;

    @Autowired
    public ResourceController(CrawlingService crawlingService) {
        this.crawlingService = crawlingService;
    }

    @GetMapping("/music/update")
    public BaseResponse<String> updateMusic() throws BaseException, IOException {
        try{
            crawlingService.crawlingLatest();
            crawlingService.crawlingFamous();
        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>(SUCCESS);
    }
}
