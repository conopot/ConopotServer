package conopot.server.config;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {

    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),

    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),

    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    DATABASE_CHECK_ALREADY_ERROR(false, 4002, "이미 있는 데이터인지 확인하는데 실패하였습니다."),
    DATABASE_LYRICS_SAVED_ERROR(false, 4003, "가사를 저장하는데 실패하였습니다."),
    FAIL_MAIL_SEND_ERROR(false, 4004, "API 실패 메일을 전송하는데 실패하였습니다."),
    SUCCESS_MAIL_SEND_ERROR(false, 4005, "API 성공 전송하는데 실패하였습니다."),
    DATABASE_VERSION_SAVED_ERROR(false, 4006, "API 호출 결과를 저장하는데 실패하였습니다."),
    AWS_DYNAMODB_ERROR(false, 4007, "Dynamo DB에 가사 추가를 실패하였습니다."),

    /**
     * 5000 : File I/O 오류
     */
    FILE_NOTFOUND_ERROR(false, 5000, "해당 경로에 파일이 존재하지 않습니다."),
    FILE_INPUT_ERROR(false, 5001, "파일을 불러오는데 실패하였습니다."),
    FILE_OUTPUT_ERROR(false, 5002, "파일을 내보내는데 실패하였습니다."),
    FILE_ZIP_ERROR(false, 5003, "파일을 압축하는데 실패하였습니다."),
    FILE_S3_UPLOAD_ERROR(false, 5004, "S3에 파일을 업로드하는데 실패하였습니다."),
    FILE_CHECK_SIZE_ERROR(false, 5005, "파일 사이즈를 체크하는데 실패하였습니다."),
    FILE_SIZE_ERROR(false, 5006, "파일 사이즈가 적정 기준을 만족하지 못했습니다."),
    FILE_CLOUDFRONT_DOWNLOAD_ERROR(false, 5007, "Cloud Front로부터 파일을 다운로드하는데 실패하였습니다."),
    FILE_UNZIP_ERROR(false, 5008, "파일을 압축 해제하는데 실패하였습니다."),


    /**
     * 6000 : Crawling, Matching 오류
     */
    CRAWL_ERROR(false, 6000, "크롤링 작업에 실패하였습니다."),
    CRAWL_LATEST_TJ_ERROR(false, 6001, "TJ 신곡을 크롤링에 실패하였습니다."),
    CRAWL_LATEST_KY_ERROR(false, 6002, "KY 신곡을 크롤링에 실패하였습니다."),
    CRAWL_FAMOUS_TJ_ERROR(false, 6003, "TJ 인기차트 크롤링에 실패하였습니다."),
    CRAWL_FAMOUS_KY_ERROR(false, 6004, "KY 인기차트 크롤링에 실패하였습니다."),
    CRAWL_LYRICS_TJ_ERROR(false, 6005, "TJ 가사 크롤링에 실패하였습니다."),
    MATCHING_ALGORITHM_ERROR(false, 6006, "TJ KY 알고리즘 매칭에 실패하였습니다."),

    /**
     * 7000 : Docker 오류
     */
    DOCKER_MAKE_IMAGE_ERROR(false, 7000, "Docker image 생성에 실패하였습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) { //BaseResponseStatus 에서 각 해당하는 코드를 생성자로 맵핑
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
    public static BaseResponseStatus of(final String errorName){
        return BaseResponseStatus.valueOf(errorName);
    }
}