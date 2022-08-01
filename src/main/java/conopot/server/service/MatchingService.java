package conopot.server.service;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponseStatus;
import conopot.server.config.FilePath;
import conopot.server.dto.CmpMusic;
import conopot.server.dto.MatchingMusic;
import conopot.server.dto.Music;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service @Slf4j
public class MatchingService {
    
    private final FileService fileService;
    private FilePath filePath;

    @Autowired
    public MatchingService(FileService fileService) {
        this.fileService = fileService;
        filePath = new FilePath();
    }

    public void matchingMusicByAlgorithm(ArrayList<Music> latestTJ, ArrayList<Music> latestKY) throws BaseException, IOException {
        try{
            // nonMatching 파일 받아오기
            ArrayList<Music> nonMatchingTJ = fileService.getNonMatchingTJ();
            ArrayList<Music> nonMatchingKY = fileService.getNonMatchingKY();

            // 신곡들을 nonMatching에 추가
            nonMatchingTJ.addAll(latestTJ);
            nonMatchingKY.addAll(latestKY);

            // 매칭 시키기
            Map<String, String> matchingSingers = fileService.getMatchingSingers();
            ArrayList<MatchingMusic> matchingMusics = fileService.getMatchingMusics();

            matchingAlgorithm(nonMatchingTJ, nonMatchingKY, matchingSingers, matchingMusics);

            // 파일들 내보내기
            fileService.savedText(fileService.changeMatchingMusicArr(matchingMusics), "/matching_Musics.txt");
            fileService.savedText(fileService.changeMusicArr(nonMatchingTJ), "/nonMatchingTJ.txt");
            fileService.savedText(fileService.changeMusicArr(nonMatchingKY), "/nonMatchingKY.txt");

        } catch(BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    public ArrayList<CmpMusic> changeMusicToCmp(ArrayList<Music> arr){
        ArrayList<CmpMusic> ret = new ArrayList<>();
        for(Music m : arr) {
            ret.add(new CmpMusic(m.getName(), m.getSinger(), m.getNumber()));
        }
        return ret;
    }

    public void matchingAlgorithm(ArrayList<Music> nonMatchingTJ, ArrayList<Music> nonMatchingKY,
                                  Map<String, String> matchingSingers, ArrayList<MatchingMusic> matchingMusics) throws BaseException{

        // 비교할 배열 생성 - 문자열 정규화
        ArrayList<CmpMusic> cmpTJ = changeMusicToCmp(makeCmpArr(nonMatchingTJ));
        ArrayList<CmpMusic> cmpKY = changeMusicToCmp(makeCmpArr(nonMatchingKY));

        Collections.sort(cmpTJ);
        Collections.sort(cmpKY);

        log.info("CMP nonMatchingTJ size : {}", nonMatchingTJ.size());
        log.info("CMP nonMatchingKY size : {}", nonMatchingKY.size());

        // 정규화 풀어주기 위한 map 생성
        Map<String, Music> mapTJ = makeMap(nonMatchingTJ);
        Map<String, Music> mapKY = makeMap(nonMatchingKY);

        try{
            int matchingCnt = 0;
            String name = "", singer = "";

            // 이름 이분탐색 -> 시작점 찾기 -> 주변 탐색
            for(int i=0; i<cmpTJ.size(); i++){
                CmpMusic tj = cmpTJ.get(i);

                String ts = String.valueOf(tj.getName().charAt(0));

                int l = 0, r = cmpKY.size() - 1, start = 7777777;
                while(l <= r) {
                    int mid = (l + r) / 2;
                    CmpMusic ky = cmpKY.get(mid);
                    String ks = String.valueOf(ky.getName().charAt(0));
                    int cmp = ts.compareTo(ks);
                    if(cmp >= 1) {
                        l = mid + 1;
                    }
                    else{
                        if(cmp == 0){
                            start = Math.min(start, mid);
                        }
                        r = mid - 1;
                    }
                }
                if(start == 7777777) start = 0;

                // 가수 변환
                if(matchingSingers.containsKey(singer)) {
                    singer = matchingSingers.get(singer);
                }

                // KY과 매칭시키기
                boolean check = false;
                Music temp = new Music("", "", "");

                for(int j=start; j<cmpKY.size(); j++){
                    CmpMusic ky = cmpKY.get(j);
                    if(ky.getName().charAt(0) != ts.charAt(0)) break;
                    if(vsName(tj.getName(), ky.getName()) && vsSinger(tj.getSinger(), ky.getSinger())){
                        matchingCnt++;
                        // log.info(tj.toString() + " " + ky.toString());
                        for(MatchingMusic matchingMusic : matchingMusics){
                            if(matchingMusic.getTJ().getNumber().equals(tj.getNumber())){
                                matchingMusic.setKY(mapKY.get(ky.getNumber()));
                                break;
                            }
                        }
                        // 매칭된거는 삭제
                        cmpKY.remove(ky);
                        nonMatchingTJ.remove(mapTJ.get(tj.getNumber()));
                        nonMatchingKY.remove(mapKY.get(ky.getNumber()));
                        check = true;
                        break;
                    }
                }
            }

            log.info("MatchingMusic Count After Matching : {}", matchingMusics.size());
            log.info("Matching Count : {}", matchingCnt);

        } catch(Exception e) {
            e.printStackTrace();
            throw new BaseException(BaseResponseStatus.MATCHING_ALGORITHM_ERROR);
        }
    }

    /**
     * String 내부의 특수기호를 빼고, 전부 대문자로 만든다
     * @param str
     * @return
     */
    public String changeString(String str){
        String ret = "";
        for(int i=0; i<str.length(); i++) {
            char c = str.charAt(i);
            if('a' <= c && c <= 'z') {
                ret += String.valueOf(c).toUpperCase();
            }
            else if (c == '(') break;
            else if (!String.valueOf(c).matches("[a-zA-Z0-9ㄱ-ㅎㅏ-ㅣ가-힣]") || c == ' '){ // 특수문자 제거
                continue;
            }
            else{
                ret += c;
            }
        }
        return ret;
    }

    /**
     * TJ, KY 배열을 통해
     * 각 노래의 제목과 가수를 정규화 시킨 배열 cmpTJ, cmpKY을 만든다
     */
    public ArrayList<Music> makeCmpArr(ArrayList<Music> arr){
        // String을 정규화 해서 가지고 있기
        ArrayList<Music> ret = new ArrayList<>();
        for(Music m : arr) {
            if(m.getSinger().equals("(여자)아이들")){
                ret.add(m);
                continue;
            }
            String cName = changeString(m.getName());
            String cSinger = changeString(m.getSinger());
            if(cName.equals("") || cSinger.equals("")) {
                continue;
            }
            ret.add(new Music(cName, cSinger, m.getNumber()));
        }
        return ret;
    }

    public Map<String, Music> makeMap(ArrayList<Music> arr){
        Map<String, Music> ret = new HashMap<>();
        for(Music m : arr) {
            ret.put(m.getNumber(), m);
        }
        return ret;
    }

    public boolean vsSinger(String a, String b){
        // 한 쪽에서 포함하고 있는지 확인
        if(a.contains(b) || b.contains(a)) {
            return true;
        }
        else return false;
    }

    public boolean vsName(String a, String b){
        if(a.equals(b)) return true;
        else return false;
    }
}
