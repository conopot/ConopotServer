package conopot.server.service;

import conopot.server.config.BaseException;
import conopot.server.repository.VersionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
public class VersionService {

    private final VersionRepository versionRepository;

    @Autowired
    public VersionService(VersionRepository versionRepository) {
        this.versionRepository = versionRepository;
    }

    public void savedVersion(String status) throws BaseException{
        try{
            Date now = new Date();
            versionRepository.savedVersion(now.toString(), status);
        } catch(BaseException e){
            throw new BaseException(e.getStatus());
        }
    }
}
