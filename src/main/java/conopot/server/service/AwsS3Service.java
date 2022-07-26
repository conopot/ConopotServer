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
    public void uploadZipFile() throws Exception {

        String keyName = "public/Musics.zip";

        try {
            TransferManager tm = TransferManagerBuilder.standard()
                    .withS3Client(amazonS3Client)
                    .build();

            // TransferManager processes all transfers asynchronously,
            // so this call returns immediately.
            Upload upload = tm.upload(bucket, keyName, new File(filePath.S3_ZIP_FILE));
            log.info("Object upload started");

            // Optionally, wait for the upload to finish before continuing.
            upload.waitForCompletion();
            log.info("Object upload complete");
        } catch (AmazonServiceException e) {
            // The call was transmitted successfully, but Amazon S3 couldn't process
            // it, so it returned an error response.
            e.printStackTrace();
            throw new BaseException(FILE_S3_UPLOAD_ERROR);
        } catch (SdkClientException e) {
            // Amazon S3 couldn't be contacted for a response, or the client
            // couldn't parse the response from Amazon S3.
            e.printStackTrace();
            throw new BaseException(FILE_S3_UPLOAD_ERROR);
        }
    }
}
