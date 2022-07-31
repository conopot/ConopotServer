package conopot.server.service;

import conopot.server.config.BaseException;
import conopot.server.config.FilePath;
import conopot.server.dto.Highest;
import conopot.server.dto.MatchingMusic;
import conopot.server.dto.Music;
import conopot.server.repository.FileRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Map;

import static conopot.server.config.BaseResponseStatus.*;


@Service @Slf4j
public class FileService {

    private final FileRepository fileRepository;
    private FilePath filePath;

    @Autowired
    public FileService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
        this.filePath = new FilePath();
    }

    /**
     * Cloud Front에서 zip 파일 다운로드
     * 다운로드 받은 zip 파일 압축 해제
     * @throws BaseException
     */
    public void initData() throws BaseException {
        try {
            fileRepository.initData();
        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    /**
     * 전체 데이터 받아오기 (test용)
     * @throws BaseException
     */
    public void getAllData() {
        ArrayList<Music> TJ = fileRepository.getTJ();
        log.info("TJ size : {}", TJ.size());

        ArrayList<Music> KY = fileRepository.getKY();
        log.info("KY size : {}", KY.size());

        ArrayList<Music> Legend = fileRepository.getLegend();
        log.info("Legend size : {}", Legend.size());

        ArrayList<MatchingMusic> matchingMusics = fileRepository.getMatchingMusics();
        log.info("MatchingMusics size : {}", matchingMusics.size());

        Map<String, String> matchingSingers = fileRepository.getMatchingSingers();
        log.info("MatchingSingers size : {}", matchingSingers.size());
    }

    // TJ 전 곡
    public ArrayList<Music> getMusicBookTJ() throws BaseException{
        return fileRepository.getTJ();
    }

    // KY 전 곡
    public ArrayList<Music> getMusicBookKY() throws BaseException{
        return fileRepository.getKY();
    }

    // TJ 2년치 기준 인기곡 100곡
    public ArrayList<Music> getLegend() throws BaseException {
        return fileRepository.getLegend();
    }

    // TJ + KY 매칭 완료된 곡들
    public ArrayList<MatchingMusic> getMatchingMusics() throws BaseException {
        return fileRepository.getMatchingMusics();
    }

    // 가수 변환
    public Map<String, String> getMatchingSingers() throws BaseException {
        return fileRepository.getMatchingSingers();
    }

    // TJ 매칭 안 된 곡들
    public ArrayList<Music> getNonMatchingTJ() throws BaseException {
        return fileRepository.getNonMatchingTJ();
    }

    // KY 매칭 안 된 곡들
    public ArrayList<Music> getNonMatchingKY() throws BaseException {
        return fileRepository.getNonMatchingKY();
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

    // zip 파일 생성
    public void makeZip(String path) throws BaseException{
        try{
            fileRepository.makeZipFiles();
            if(!checkFileSize(filePath.DOCKER_MUSICS_ZIP_FILE,3L*1024) ||
                    !checkFileSize(filePath.DOCKER_MATCHINGS_ZIP_FILE, 500L)) throw new BaseException(FILE_SIZE_ERROR);
        } catch (BaseException e) {
            throw new BaseException(e.getStatus());
        }
    }

    /**
     * fileSize가 3MB 이상이라면 true, 아니면 false 반환
     * @return
     * @throws BaseException
     */
    public boolean checkFileSize(String fp, long cmp) throws BaseException{
        try{
            Path path = Paths.get(fp);
            long kb = Files.size(path) / 1024;
            log.info("{} File Size : {}", fp, kb);
            return kb > cmp ? true : false;
        } catch(Exception e){
            throw new BaseException(FILE_CHECK_SIZE_ERROR);
        }
    }
}
