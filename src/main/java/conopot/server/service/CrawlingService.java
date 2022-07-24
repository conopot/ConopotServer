package conopot.server.service;

import conopot.server.config.BaseException;
import conopot.server.config.FilePath;
import conopot.server.dto.Music;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import static conopot.server.config.BaseResponseStatus.*;

@Service @Slf4j
public class CrawlingService {

    private final FileService fileService;
    private FilePath filePath;
    boolean checkTJ[] = new boolean[100001];
    boolean checkKY[] = new boolean[100001];

    @Autowired
    public CrawlingService(FileService fileService) {
        this.fileService = fileService;
        this.filePath = new FilePath();
    }

    /**
     * TJ 신곡 크롤링
     * @return
     * @throws BaseException
     */
    public ArrayList<Music> crawlLatestTJ() throws BaseException {

        ArrayList<Music> ret = new ArrayList<Music>();

        Connection conn = Jsoup.connect("https://www.tjmedia.com/tjsong/song_monthNew.asp");

        try {
            Document document = conn.get();
            Elements elements = document.select("td");

            int cnt = 0, id = 0;
            String name = "", singer = "", num = "";
            for (Element element : elements) {
                String txt = element.text();
                if (txt.equals("")) continue;
                else {
                    if (cnt == 0) { // 곡 번호
                        num = txt;
                        cnt++;
                    } else if (cnt == 1) { // 곡 제목
                        name = txt;
                        cnt++;
                    } else if (cnt == 2) { // 곡 가수
                        singer = txt;

                        ret.add(new Music(name, singer, num));

                        cnt++;
                    } else {
                        cnt++;
                        if (cnt == 5) {
                            cnt = 0;
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new BaseException(CRAWL_LATEST_TJ_ERROR);
        }

        log.info("TJ Latest Before Size : {}", ret.size());

        return ret;
    }

    /**
     * 금영 신곡 크롤링
     * @return
     * @throws BaseException
     */
    public ArrayList<Music> crawlLatestKY() throws BaseException{

        ArrayList<Music> ret = new ArrayList<>();

        for(int i=1; i<=30; i++){
            String url = "https://kysing.kr/latest/?s_page=" + i;

            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();

                // 더 이상 없는 페이지인지 확인
                Elements elements = document.select("div[class=search_daily_chart_wrap] li");

                int cnt = 0, row = 0; String num = "", name = "", singer = "";
                for (Element element : elements) {
                    if(cnt == 2) {
                        element = element.select("span").get(0);
                    }

                    String text = element.text();

                    if(cnt == 1) {
                        num = text;
                    }
                    else if(cnt == 2) {
                        name = text;
                    }
                    else if(cnt == 3) {
                        singer = text;
                    }

                    cnt++;

                    if(cnt == 11) {
                        cnt = 0;
                        if(row == 0) {
                            row++;
                            continue;
                        }
                        ret.add(new Music(name, singer, num));
                    }
                }
            } catch (Exception e) {
                throw new BaseException(CRAWL_LATEST_KY_ERROR);
            }
        }

        log.info("KY Latest Before Size : {}", ret.size());

        return ret;
    }

    /**
     * TJ 신곡 저장 및 반환
     * @throws BaseException
     */
    public ArrayList<Music> savedLatestTJ() throws BaseException, IOException{
        try{
            // TJ 크롤링
            ArrayList<Music> latestTJ = crawlLatestTJ();
            ArrayList<Music> musicBookTJ = fileService.getMusicBookTJ();

            // 기존 TJ 데이터와 비교하여 넣기
            ArrayList<Music> temp = new ArrayList<>();
            for (Music music : musicBookTJ) {
                checkTJ[Integer.valueOf(music.getNumber())] = true;
            }

            for (Music music : latestTJ) {
                if (!checkTJ[Integer.valueOf(music.getNumber())]) {
                    temp.add(music);
                    musicBookTJ.add(music); // 신 곡 추가
                }
            }

            latestTJ = temp; // 기존에 있던 곡 제거한 최신곡
            log.info("TJ Latest After Size : {}", latestTJ.size());

            // 번호순 정렬
            Collections.sort(musicBookTJ);

            // .txt 파일 저장
            savedTxt(musicBookTJ, filePath.MUSIC_BOOK_TJ);

            return latestTJ;

        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    /**
     * KY 신곡 저장 및 반환
     * @throws BaseException
     */
    public ArrayList<Music> savedLatestKY() throws BaseException, IOException{
        try {
            // 금영 크롤링
            ArrayList<Music> latestKY = crawlLatestKY();
            ArrayList<Music> musicBookKY = fileService.getMusicBookKY();

            // 기존 금영 데이터와 비교하여 넣기
            ArrayList<Music> temp = new ArrayList<>();
            for (Music music : musicBookKY) {
                checkKY[Integer.valueOf(music.getNumber())] = true;
            }

            for (Music music : latestKY) {
                if (!checkKY[Integer.valueOf(music.getNumber())]) {
                    temp.add(music);
                    musicBookKY.add(music); // 신 곡 추가
                }
            }

            latestKY = temp;
            log.info("KY Latest After Size : {}", latestKY.size());

            Collections.sort(musicBookKY);

            // .txt 파일 저장
            savedTxt(musicBookKY, filePath.MUSIC_BOOK_KY);

            return latestKY;

        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    public void savedTxt(ArrayList<Music> arr, String path) throws BaseException, IOException {
        try{
            String output = "";
            for(Music m : arr) {
                output += m.toString() + "\n";
            }
            fileService.savedText(output, path);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // main run
    public void crawlingLatest() throws BaseException, IOException{

        // 이번에 새롭게 추가된 최신곡들 저장
        ArrayList<Music> latestTJ = savedLatestTJ();
        ArrayList<Music> latestKY = savedLatestKY();


    }
}
