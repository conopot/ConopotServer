package conopot.server.service;

import conopot.server.config.BaseException;
import conopot.server.config.BaseResponseStatus;
import conopot.server.config.FilePath;
import conopot.server.dto.Highest;
import conopot.server.dto.MatchingMusic;
import conopot.server.dto.Music;
import conopot.server.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import static conopot.server.config.BaseResponseStatus.RESPONSE_ERROR;


@Service
public class FileService {

    private final FileRepository fileRepository;
    private FilePath filePath;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.filePath = new FilePath();
    }

    /**
     * 전체 데이터 받아오기 (test용)
     * @throws BaseException
     */
    public void getAllData() throws BaseException {
        try{
            ArrayList<Music> TJ = fileRepository.getMusicBook(filePath.MUSIC_BOOK_TJ);
            ArrayList<Music> KY = fileRepository.getMusicBook(filePath.MUSIC_BOOK_KY);
            ArrayList<Music> Legend = fileRepository.getMusicBook(filePath.ALL_TIME_LEGEND);
            ArrayList<Highest> highest = fileRepository.getHighest(filePath.MUSIC_HIGHEST_KEY);
            ArrayList<MatchingMusic> matchingMusics = fileRepository.getMatchingMusics(filePath.MATCHING_MUSICS);
            Map<String, String> matchingSingers = fileRepository.getMatchingSingers(filePath.MATCHING_SINGERS);
        } catch (Exception e) {
            throw new BaseException(RESPONSE_ERROR);
        }
    }

    // TJ 전 곡
    public ArrayList<Music> getMusicBookTJ() throws BaseException{
        try{
            return fileRepository.getMusicBook(filePath.MUSIC_BOOK_TJ);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // KY 전 곡
    public ArrayList<Music> getMusicBookKY() throws BaseException{
        try{
            return fileRepository.getMusicBook(filePath.MUSIC_BOOK_KY);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // TJ 2년치 기준 인기곡 100곡
    public ArrayList<Music> getLegend() throws BaseException {
        try{
            return fileRepository.getMusicBook(filePath.ALL_TIME_LEGEND);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // 최고음
    public ArrayList<Highest> getHighest() throws BaseException {
        try{
            return fileRepository.getHighest(filePath.MUSIC_HIGHEST_KEY);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // TJ + KY 매칭 완료된 곡들
    public ArrayList<MatchingMusic> getMatchingMusics() throws BaseException {
        try{
            return fileRepository.getMatchingMusics(filePath.MATCHING_MUSICS);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // 가수 변환
    public Map<String, String> getMatchingSingers() throws BaseException {
        try{
            return fileRepository.getMatchingSingers(filePath.MATCHING_SINGERS);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // TJ 매칭 안 된 곡들
    public ArrayList<Music> getNonMatchingTJ() throws BaseException {
        try{
            return fileRepository.getMusicBook(filePath.NON_MATCHING_TJ);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // KY 매칭 안 된 곡들
    public ArrayList<Music> getNonMatchingKY() throws BaseException {
        try{
            return fileRepository.getMusicBook(filePath.NON_MATCHING_KY);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // 텍스트 파일 저장
    public void savedText(String output, String path) throws BaseException, IOException {
        try{
            fileRepository.savedText(output, path);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    // MatchingMusic 배열을 String으로
    public String changeMatchingMusicArr(ArrayList<MatchingMusic> arr){
        String output = "";
        for(MatchingMusic m : arr) {
            output += m.toString() + "\n";
        }
        return output;
    }

    // Music 배열을 String으로
    public String changeMusicArr(ArrayList<Music> arr){
        String output = "";
        for(Music m : arr) {
            output += m.toString() + "\n";
        }
        return output;
    }
}
