package conopot.server.controller;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.config.FilePath;
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
    private FilePath filePath;

    @Autowired
    public ResourceController(CrawlingService crawlingService, FileService fileService) {
        this.crawlingService = crawlingService;
        this.fileService = fileService;
        filePath = new FilePath();
    }

    @GetMapping("/music/update")
    public BaseResponse<String> updateMusic() throws BaseException, IOException {
        try{
            crawlingService.crawlingLatest();
            crawlingService.crawlingFamous();
            fileService.makeZip(filePath.ZIP_FILE);

        } catch(BaseException e){
            return new BaseResponse<>(e.getStatus());
        }
        return new BaseResponse<>(SUCCESS);
    }
}
