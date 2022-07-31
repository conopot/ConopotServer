package conopot.server.service;

import conopot.server.config.BaseException;
import conopot.server.config.FilePath;
import conopot.server.dto.MatchingMusic;
import conopot.server.dto.Music;
import conopot.server.repository.LyricsRepository;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static conopot.server.config.BaseResponseStatus.*;

@Service @Slf4j
public class CrawlingService {

    private final FileService fileService;
    private final LyricsRepository lyricsRepository;
    private final MatchingService matchingService;
    private FilePath filePath;
    boolean checkTJ[] = new boolean[100001];
    boolean checkKY[] = new boolean[100001];

    public CrawlingService(FileService fileService, LyricsRepository lyricsRepository, MatchingService matchingService) {
        this.fileService = fileService;
        this.lyricsRepository = lyricsRepository;
        this.matchingService = matchingService;
        this.filePath = new FilePath();
    }

    /**
     * 이번에 새롭게 추가된 최신곡들 저장
     */
    public void crawlingLatest() throws BaseException, IOException{
        ArrayList<Music> latestTJ = savedLatestTJ();
        ArrayList<Music> latestKY = savedLatestKY();

        // TJ 신곡에 대한 가사 크롤링
        crawlingLyrics(latestTJ);

        // 신곡 matching 하기
        log.info("Matching Start!");
        matchingService.matchingMusicByAlgorithm(latestTJ, latestKY);
    }

    /**
     * 인기차트 크롤링 및 저장
     */
    public void crawlingFamous() throws BaseException, IOException{
        savedFamousTJ();
        savedFamousKY();
    }

    /**
     * TJ 신곡 가사 크롤링
     */
    public void crawlingLyrics(ArrayList<Music> latestTJ) throws BaseException{
        try{
            for(Music m : latestTJ) {
                if(lyricsRepository.checkAlreadyLyricsTJ(m.getNumber()) > 0) continue; // 이미 크롤링 했다면 건너뛰기
                crawlingLyricByNumberTJ(m.getNumber());
            }
        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    /**
     * TJ 한 곡에 대한 가사 크롤링
     * @param number
     * @return
     */
    public boolean crawlingLyricByNumberTJ(String number) throws BaseException{

        String lyrics = "";

        // crawling logic
        Connection conn = Jsoup.connect("https://www.tjmedia.co.kr/2006_renew/ZillerGasaService/gasa_view2.asp?pro=" + number);

        try {
            Document document = conn.get();

            Element element = document.select("pre").get(3);

            lyrics = element.text();

        } catch (Exception e) {
            return false; // TJ는 가사가 없는 것들도 있기 때문에 continue 해주어야 한다.
            // throw new BaseException(CRAWL_LYRICS_TJ_ERROR);
        }

        // 결과값 DB에 저장하기
        lyricsRepository.saveLyricsTJ(number, lyrics);

        return true;
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
            savedTxt(musicBookTJ, "/musicbook_TJ.txt");

            // matchingMusics에 신곡들 추가
            addMatchingMusics(latestTJ);

            return latestTJ;

        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    public void addMatchingMusics(ArrayList<Music> latestTJ) throws BaseException, IOException{

        // matchingMusics에 신곡 추가
        ArrayList<MatchingMusic> matchingMusics = fileService.getMatchingMusics();
        for(Music m : latestTJ) {
            matchingMusics.add(new MatchingMusic(m, new Music("", "", "")));
        }

        // matchingMusics 번호 순 정렬
        Collections.sort(matchingMusics);

        // Legend 곡 들 앞으로 빼주기
        // 인기곡 100곡 앞으로 정렬
        ArrayList<Music> legend = fileService.getLegend();
        ArrayList<MatchingMusic> temp = new ArrayList<>();

        for(Music lm : legend) {
            String lNum = lm.getNumber();
            for(MatchingMusic m : matchingMusics) {
                if(m.getTJ().getNumber().equals(lNum)) {
                    temp.add(m);
                    matchingMusics.remove(m);
                    break;
                }
            }
        }

        for(MatchingMusic m : matchingMusics) {
            temp.add(m);
        }

        matchingMusics = temp;

        fileService.savedText(fileService.changeMatchingMusicArr(matchingMusics), filePath.MATCHING_MUSICS);
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
            savedTxt(musicBookKY, "/musicbook_KY.txt");

            return latestKY;

        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    /**
     * TJ 인기차트 크롤링 및 저장
     * @throws BaseException
     * @throws IOException
     */
    public void savedFamousTJ() throws BaseException, IOException {

        ArrayList<Music> famousTJ = new ArrayList<>();

        // 하루 전 기준 크롤링

        Date now = new Date();

        Calendar cal = Calendar.getInstance();
        cal.setTime(now);

        cal.add(Calendar.DAY_OF_MONTH, -1); // 1일 빼기

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH ) + 1;
        int day = cal.get(Calendar.DAY_OF_MONTH);

        log.info("TJ Famous Date : {}/{}/{}", year, month, day);

        String url = "http://tjmedia.com/tjsong/song_monthPopular.asp?strType=1&SYY=" + year + "&SMM=" + month + "&SDD=" + day + "&EYY=" + year + "&EMM=" + month + "&EDD=" + day;

        Connection conn = Jsoup.connect(url);

        try {
            Document document = conn.get();
            Elements elements = document.select("td");

            int cnt = 0, id = 0; String name = "", singer = "", num = "";
            for (Element element : elements) {
                String txt = element.text();
                if(txt.equals("")) continue;
                else {
                    if(cnt == 0) { // 순위는 필요 없음
                        cnt++;
                        continue;
                    }
                    else if(cnt == 1) { // 곡 번호
                        num = txt;
                        cnt++;
                    }
                    else if(cnt == 2) { // 곡 제목
                        name = txt;
                        cnt++;
                    }
                    else if(cnt == 3) { // 가수
                        singer = txt;

                        famousTJ.add(new Music(name, singer, num));

                        cnt = 0;
                    }
                }
            }
        } catch (Exception e) {
            throw new BaseException(CRAWL_FAMOUS_TJ_ERROR);
        }

        savedTxt(famousTJ, "/chart_TJ.txt");
    }


    /**
     * KY 인기차트 크롤링 및 저장
     * @throws BaseException
     * @throws IOException
     */
    public void savedFamousKY() throws BaseException, IOException{

        ArrayList<Music> musicBookKY = fileService.getMusicBookKY();
        ArrayList<Music> famousKY = new ArrayList<>();

        for(int i=1; i<=2; i++){

            String url = "https://kysing.kr/popular/?period=&range=" + i;

            Connection conn = Jsoup.connect(url);

            try {
                Document document = conn.get();

                // 더 이상 없는 페이지인지 확인
                Elements elements = document.select("div[class=popular_daily_chart_wrap] li");

                int cnt = 0, id = 0, row = 0;
                for (Element element : elements) {

                    String text = element.text();

                    // 1 : 곡번호만 크롤링
                    if(cnt == 1){
                        cnt++;
                        if(row == 0) continue;
                        else{
                            for(Music music : musicBookKY) {
                                if(music.getNumber().equals(text)){
                                    famousKY.add(music);
                                    break;
                                }
                            }
                        }
                    }
                    else {
                        cnt++;
                        if(cnt == 11){
                            cnt = 0;
                            row++;
                            if(row == 51) break;
                        }
                    }
                }
            } catch (Exception e) {
                throw new BaseException(CRAWL_FAMOUS_KY_ERROR);
            }
        }

        savedTxt(famousKY, "/chart_KY.txt");
    }

    /**
     * Text File 저장
     * @param arr
     * @param path
     * @throws BaseException
     * @throws IOException
     */
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
}
