package conopot.server.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import conopot.server.config.BaseException;
import conopot.server.config.BaseResponse;
import conopot.server.dto.Lyric;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static conopot.server.config.BaseResponseStatus.AWS_DYNAMODB_ERROR;

@Service
public class AwsDynamoDbService {

    private final AmazonDynamoDBClient amazonDynamoDBClient;

    @Autowired
    public AwsDynamoDbService(AmazonDynamoDBClient amazonDynamoDBClient) {
        this.amazonDynamoDBClient = amazonDynamoDBClient;
    }

    public void createItem(String id, String _lyric) throws BaseException{
        try{
            Lyric lyric = new Lyric(id, _lyric);

            // Save Lyric To DynamoDB
            DynamoDBMapper mapper = new DynamoDBMapper(amazonDynamoDBClient);
            mapper.save(lyric);
        } catch(Exception e){
            throw new BaseException(AWS_DYNAMODB_ERROR);
        }
    }
}
