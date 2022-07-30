package conopot.server.config;

public class FilePath {


    /**
     * 앱에 사용중인 데이터
     */
    public String MUSIC_BOOK_TJ = "src/main/resources/static/MusicDB/musicbook_TJ.txt";
    public String MUSIC_BOOK_KY = "src/main/resources/static/MusicDB/musicbook_KY.txt";
    public String CHART_TJ = "src/main/resources/static/MusicDB/chart_TJ.txt";
    public String CHART_KY = "src/main/resources/static/MusicDB/chart_KY.txt";
    public String MATCHING_MUSICS = "src/main/resources/static/MusicDB/matching_Musics.txt";
    public String MUSIC_HIGHEST_KEY = "src/main/resources/static/MusicDB/music_highest_key.txt";

    /**
     * 앱에 사용중이진 않지만 필요한 데이터
     */
    public String MATCHING_SINGERS = "src/main/resources/static/Files/matchingSingers.txt";
    public String ALL_TIME_LEGEND = "src/main/resources/static/Files/AllTimeLegend.txt";
    public String NON_MATCHING_TJ = "src/main/resources/static/Files/nonMatchingTJ.txt";
    public String NON_MATCHING_KY = "src/main/resources/static/Files/nonMatchingKY.txt";

    /**
     * zip
     */
    public String ZIP_FILE = "src/main/resources/static/MusicDB";
    public String S3_ZIP_FILE = "src/main/resources/static/MusicDB/Musics.zip";

}
