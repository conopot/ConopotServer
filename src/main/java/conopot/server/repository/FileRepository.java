package conopot.server.repository;

import conopot.server.config.BaseException;
import conopot.server.dto.Highest;
import conopot.server.dto.MatchingMusic;
import conopot.server.dto.Music;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static conopot.server.config.BaseResponseStatus.*;

@Repository @Slf4j
public class FileRepository {

    /**
     * TJ, 금영 전체 곡 가져오기
     * @param path
     */
    public ArrayList<Music> getMusicBook(String path) throws BaseException{

        ArrayList<Music> ret = new ArrayList<Music>();

        try{
            //파일 객체 생성
            File file = new File(path);
            //입력 스트림 생성
            FileReader file_reader = new FileReader(file);
            int cur = 0, cnt = 0; String name = "", singer = "", number = "", temp = "";
            while((cur = file_reader.read()) != -1){

                char c = (char)cur;
                if(c == '\n') continue;
                if(c == '^') {
                    cnt++;
                    if (cnt == 1) {
                        name = temp;
                    }
                    else if (cnt == 2) {
                        singer = temp;
                    }
                    else {
                        number = temp;
                        cnt = 0;
                        ret.add(new Music(name, singer, number));
                    }
                    temp = "";
                }
                else {
                    temp += c;
                }
            }
            file_reader.close();

        } catch (FileNotFoundException e) {
            throw new BaseException(FILE_NOTFOUND_ERROR);
        } catch(IOException e){
            throw new BaseException(FILE_INPUT_ERROR);
        }

        log.info("MusicBook Size : {}", ret.size());

        return ret;
    }

    /**
     * 이미 매칭이 끝난 음악 데이터 가져오기 (TJ + KY)
     * @return
     * @throws IOException
     */
    public ArrayList<MatchingMusic> getMatchingMusics(String path) throws BaseException {
        ArrayList<MatchingMusic> ret = new ArrayList<>();

        try{
            String output = "";

            //파일 객체 생성
            File file = new File(path);
            //입력 스트림 생성
            FileReader file_reader = new FileReader(file);

            int cur = 0, cnt = 0;
            String temp = "", name = "", singer = "", number = "", sex = "", high = "", numHigh = "";

            Music tj = new Music("", "", "");
            Music ky = new Music("", "", "");

            while ((cur = file_reader.read()) != -1) {

                char c = (char) cur;
                if(c == '\n') continue;

                if (c == '^') {
                    cnt++;
                    if (cnt == 1) {
                        name = temp;
                    } else if (cnt == 2) {
                        singer = temp;
                    } else if (cnt == 3) {
                        number = temp;
                        tj = new Music(name, singer, number);
                    } else if (cnt == 4) {
                        name = temp;
                    } else if (cnt == 5) {
                        singer = temp;
                    } else if (cnt == 6) {
                        number = temp;
                        ky = new Music(name, singer, number);
                    } else if(cnt == 7) {
                        sex = temp;
                    } else if(cnt == 8) {
                        high = temp;
                    } else if(cnt == 9) {
                        numHigh = temp;
                        ret.add(new MatchingMusic(tj, ky, sex, high, numHigh));
                        cnt = 0;
                    }
                    temp = "";
                } else {
                    temp += c;
                }
            }

            file_reader.close();

        } catch (FileNotFoundException e) {
            throw new BaseException(FILE_NOTFOUND_ERROR);
        } catch(IOException e){
            throw new BaseException(FILE_INPUT_ERROR);
        }

        log.info("MatchingMusics Size : {}", ret.size());

        return ret;
    }

    /**
     * 가수 매칭 파일 가져오기
     * @return
     */
    public Map<String, String> getMatchingSingers(String path) throws BaseException{

        Map<String, String> ret =new HashMap<>();

        try{
            String output = "";

            //파일 객체 생성
            File file = new File(path);
            //입력 스트림 생성
            FileReader file_reader = new FileReader(file);

            int cur = 0, cnt = 0;
            String temp = "", tj = "", ky = "";

            while ((cur = file_reader.read()) != -1) {

                char c = (char) cur;
                if(c == '\n') continue;

                if (c == '^') {
                    cnt++;
                    if (cnt == 1) {
                        tj = temp;
                    } else {
                        cnt = 0;
                        ky = temp;
                        ret.put(tj, ky);
                    }
                    temp = "";
                } else {
                    temp += c;
                }
            }

            file_reader.close();


        } catch (FileNotFoundException e) {
            throw new BaseException(FILE_NOTFOUND_ERROR);
        } catch(IOException e){
            throw new BaseException(FILE_INPUT_ERROR);
        }

        log.info("MatchingSingers Size : {}", ret.size());

        return ret;
    }

    /**
     * 최고음 파일 받아오기
     * @param path
     * @return
     */
    public ArrayList<Highest> getHighest(String path){

        ArrayList<Highest> ret = new ArrayList<>();

        try{
            //파일 객체 생성
            File file = new File(path);

            //입력 스트림 생성
            FileReader file_reader = new FileReader(file);

            int cur = 0, cnt = 0;
            String tjName = "", tjSinger = "", tjNumber = "";
            String kyName = "", kySinger = "", kyNumber = "";
            String sex = "", high = "", highVal = "", temp = "";

            while ((cur = file_reader.read()) != -1) {

                char c = (char) cur;
                if(c == '\n') continue;

                if (c == '^') {
                    cnt++;
                    if (cnt == 1) {
                        tjName = temp;
                    } else if (cnt == 2) {
                        tjSinger = temp;
                    } else if (cnt == 3) {
                        tjNumber = temp;
                    } else if (cnt == 4) {
                        kyName = temp;
                    } else if (cnt == 5) {
                        kySinger = temp;
                    } else if (cnt == 6) {
                        kyNumber = temp;
                        if(kyNumber.equals("?")) {
                            kyName = "?"; kySinger = "?";
                        }
                    } else if (cnt == 7) {
                        sex = temp;
                    } else if (cnt == 8) {
                        high = temp;
                    } else {
                        highVal = temp;
                        ret.add(new Highest(new Music(tjName, tjSinger, tjNumber), new Music(kyName, kySinger, kyNumber), sex, high, highVal));
                        cnt = 0;
                    }
                    temp = "";
                } else {
                    temp += c;
                }
            }

            file_reader.close();

        } catch (FileNotFoundException e) {
            e.getStackTrace();
        } catch(IOException e){
            e.getStackTrace();
        }

        log.info("Highest Size : {}", ret.size());

        return ret;
    }

    /**
     * path에 txt 파일 생성
     * @param output
     * @param path
     * @throws IOException
     */
    public void makeText(String output, String path) throws BaseException, IOException{
        // 파일 출력
        BufferedOutputStream bs = null;
        try {
            bs = new BufferedOutputStream(new FileOutputStream(path));
            bs.write(output.getBytes()); //Byte형으로만 넣을 수 있음

        } catch (Exception e) {
            throw new BaseException(FILE_OUTPUT_ERROR);
        }finally {
            bs.close();
        }
    }
}
