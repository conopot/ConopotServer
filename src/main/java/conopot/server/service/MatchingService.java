package conopot.server.service;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponseStatus;
import conopot.server.config.FilePath;
import conopot.server.dto.MatchingMusic;
import conopot.server.dto.Music;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
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

            ArrayList<Music> cmpTJ = makeCmpArr(nonMatchingTJ);
            ArrayList<Music> cmpKY = makeCmpArr(nonMatchingKY);

            matchingAlgorithm(nonMatchingTJ, nonMatchingKY, matchingSingers, matchingMusics);

            // 파일들 내보내기
            fileService.savedText(fileService.changeMatchingMusicArr(matchingMusics), filePath.MATCHING_MUSICS);
            fileService.savedText(fileService.changeMusicArr(nonMatchingTJ), filePath.NON_MATCHING_TJ);
            fileService.savedText(fileService.changeMusicArr(nonMatchingKY), filePath.NON_MATCHING_KY);

        } catch(BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    public void matchingAlgorithm(ArrayList<Music> nonTJ, ArrayList<Music> nonKY,
                                  Map<String, String> matchingSingers, ArrayList<MatchingMusic> matchingMusics) throws BaseException{
        try{
            int matchingCnt = 0;
            String name = "", singer = "";

            for(Music tj : nonTJ) {
                singer = changeString(tj.getSinger()); name = changeString(tj.getName());

                if(singer.equals("") || name.equals("")) {
                    matchingMusics.add(new MatchingMusic(tj, new Music("?", "?", "?")));
                    continue;
                }

                // 가수 변환
                if(matchingSingers.containsKey(singer)) {
                    singer = matchingSingers.get(singer);
                }

                // KY과 매칭시키기
                Music temp = new Music("", "", "");

                boolean check = false;
                for(Music ky : nonKY){
                    if(vsName(name, changeString(ky.getName())) && vsSinger(singer, changeString(ky.getSinger()))){
                        matchingCnt++;
                        matchingMusics.add(new MatchingMusic(tj, ky));
                        // 매칭된거는 삭제
                        nonKY.remove(ky);
                        check = true;
                        break;
                    }
                }
                if(!check) matchingMusics.add(new MatchingMusic(tj, new Music("?", "?", "?")));
            }

            Collections.sort(matchingMusics);

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
            ret.add(new Music(changeString(m.getName()), changeString(m.getSinger()), m.getNumber()));
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
