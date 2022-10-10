package conopot.server.dto;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@DynamoDBTable(tableName="Conopot_Lyric_Search")
@Getter
@AllArgsConstructor
public class Lyric {

    @DynamoDBHashKey
    private String id;

    @DynamoDBAttribute
    private String lyrics;
}
