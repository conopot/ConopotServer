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

import java.util.ArrayList;
import java.util.Map;


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
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    // TJ 전 곡
    public ArrayList<Music> getMusicBookTJ() throws BaseException{
        try{
            return fileRepository.getMusicBook(filePath.MUSIC_BOOK_TJ);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    // KY 전 곡
    public ArrayList<Music> getMusicBookKY() throws BaseException{
        try{
            return fileRepository.getMusicBook(filePath.MUSIC_BOOK_KY);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    // TJ 2년치 기준 인기곡 100곡
    public ArrayList<Music> getLegend() throws BaseException {
        try{
            return fileRepository.getMusicBook(filePath.ALL_TIME_LEGEND);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    // 최고음
    public ArrayList<Highest> getHighest() throws BaseException {
        try{
            return fileRepository.getHighest(filePath.MUSIC_HIGHEST_KEY);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    // TJ + KY 매칭 완료된 곡들
    public ArrayList<MatchingMusic> getMatchingMusics() throws BaseException {
        try{
            return fileRepository.getMatchingMusics(filePath.MATCHING_MUSICS);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }

    // 가수 변환
    public Map<String, String> getMatchingSingers() throws BaseException {
        try{
            return fileRepository.getMatchingSingers(filePath.MATCHING_SINGERS);
        } catch (Exception e) {
            throw new BaseException(BaseResponseStatus.RESPONSE_ERROR);
        }
    }
}
