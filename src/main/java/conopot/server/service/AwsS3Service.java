package conopot.server.service;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.transfer.TransferManager;
import com.amazonaws.services.s3.transfer.TransferManagerBuilder;
import com.amazonaws.services.s3.transfer.Upload;
import conopot.server.config.BaseException;
import conopot.server.config.FilePath;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;

import static conopot.server.config.BaseResponseStatus.*;

@Service @Slf4j
public class AwsS3Service {

    // 버킷 이름 동적 할당
    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    private final AmazonS3Client amazonS3Client;
    private FilePath filePath;

    @Autowired
    public AwsS3Service(AmazonS3Client amazonS3Client) {
        this.amazonS3Client = amazonS3Client;
        this.filePath = new FilePath();
    }

    /**
     * AWS 공식 문서 S3 업로드 코드
     * @throws Exception
     */
    public void uploadFileToS3(String keyName, String path) throws BaseException {
        try {
            TransferManager tm = TransferManagerBuilder.standard()
                    .withS3Client(amazonS3Client)
                    .build();

            // TransferManager processes all transfers asynchronously,
            // so this call returns immediately.
            Upload upload = tm.upload(bucket, keyName, new File(path));
            log.info("Object upload started");

            // Optionally, wait for the upload to finish before continuing.
            upload.waitForCompletion();
            log.info("Object upload complete");
        } catch (Exception e) {
            throw new BaseException(FILE_S3_UPLOAD_ERROR);
        }
    }

    public void uploadMusicFiles() throws BaseException{
        try{
            // Zip 파일 upload
            uploadFileToS3("public/Musics.zip", filePath.DOCKER_MUSICS_ZIP_FILE);
            uploadFileToS3("public/MatchingFiles.zip", filePath.DOCKER_MATCHINGS_ZIP_FILE);

            // 다른 파일들 upload
            uploadFileToS3("public/musicbook_TJ.txt", filePath.DOCKER_MUSICBOOK_TJ_FILE);
            uploadFileToS3("public/matching_Musics.txt", filePath.DOCKER_MATCHING_MUSICS_FILE);

        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        }

    }

}
