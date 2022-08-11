package conopot.server.repository;

import conopot.server.config.BaseException;
import conopot.server.config.FilePath;
import conopot.server.dto.MatchingMusic;
import conopot.server.dto.Music;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import static conopot.server.config.BaseResponseStatus.*;

@Repository @Slf4j @Getter @Setter
public class FileRepository {

    @Value("${url.cloudfront}")
    private String cloudFrontUrl;
    private ArrayList<Music> TJ;
    private ArrayList<Music> KY;
    private ArrayList<MatchingMusic> matchingMusics;
    private ArrayList<Music> Legend;
    private Map<String, String>  matchingSingers;
    private ArrayList<Music> nonMatchingTJ;
    private ArrayList<Music> nonMatchingKY;

    private FilePath filePath;

    public FileRepository() {
        this.filePath = new FilePath();
        this.TJ = new ArrayList<>();
        this.KY = new ArrayList<>();
        this.matchingMusics = new ArrayList<>();
        this.Legend = new ArrayList<>();
        this.matchingSingers = new HashMap<>();
        this.nonMatchingTJ = new ArrayList<>();
        this.nonMatchingKY = new ArrayList<>();
    }

    /**
     * TJ, 금영 전체 곡 가져오기
     * @param path
     */
    public ArrayList<Music> getMusicBookFile(String path) throws BaseException{

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
    public ArrayList<MatchingMusic> getMatchingMusicsFile(String path) throws BaseException {
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
                        ret.add(new MatchingMusic(tj, ky));
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
    public Map<String, String> getMatchingSingersFile(String path) throws BaseException{

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
     * path에 txt 파일 생성
     * @param output
     * @param path
     * @throws IOException
     */
    public void savedText(String output, String path) throws BaseException, IOException{

        // 이미 파일이 존재하면 삭제하기
        File oldFile = new File(path);
        if(oldFile.exists()) {
            oldFile.delete();
        }

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

        log.info("Saved Text : {}", path);
    }

    // MatchingSingers 맵을 String으로
    public String changeMatchingSingerMap(Map<String, String> m){
        String output = "";
        for (Map.Entry<String, String> entrySet : m.entrySet()) {
            output += entrySet.getKey() + "^" + entrySet.getValue() + "^" + "\n";
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

    // Musics.zip 파일에 들어가야 되는 파일들 생성
    public void makeZipFiles() throws BaseException{
        try {
            // highest_Pitch.txt 파일을 받아온다
            getHighestFileFromS3();

            String[] musics = {"musicbook_KY.txt", "musicbook_TJ.txt", "chart_KY.txt", "chart_TJ.txt", "matching_Musics.txt", "highest_Pitch.txt"};
            makeZip("/Musics.zip", musics);

            // Legend 파일과 singers 파일 출력
            savedText(changeMusicArr(Legend), "/AllTimeLegend.txt");
            savedText(changeMatchingSingerMap(matchingSingers), "/matchingSingers.txt");
            String[] matchings = {"AllTimeLegend.txt", "matchingSingers.txt"};
            makeZip("/MatchingFiles.zip", matchings);

        } catch(BaseException e){
            throw new BaseException(e.getStatus());
        } catch (IOException e){
            throw new BaseException(FILE_ZIP_ERROR);
        }
    }

    // zip 파일로 변환
    public void makeZip(String zipFilePath, String[] cmp) throws BaseException{

        // 이미 존재한다면, 삭제하기
        File oldFile = new File(zipFilePath);
        if(oldFile.exists()) {
            oldFile.delete();
        }

        File file_ = new File("/");
        File[] listFiles = file_.listFiles();

        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;

        try {

            fos = new FileOutputStream(zipFilePath);
            zipOut = new ZipOutputStream(fos);

            for(File fileToZip : listFiles) {

                // 요구한 파일만 zip
                boolean check = false;
                for(String str : cmp) {
                    if(str.equals(fileToZip.getName())) {
                        check = true;
                        break;
                    }
                }

                if(!check) continue;

                // File Size가 0byte 일 시 에러 처리 (
                if(fileToZip.length() == 0L) {
                    log.info("{} size = 0byte Error", fileToZip.getName());
                    throw new BaseException(FILE_SIZE_ERROR);
                }

                fis = new FileInputStream(fileToZip);
                ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                zipOut.putNextEntry(zipEntry);

                byte[] bytes = new byte[1024];
                int length;
                while((length = fis.read(bytes)) >= 0) {
                    zipOut.write(bytes, 0, length);
                }

                fis.close();
                zipOut.closeEntry();

            }

            zipOut.close();
            fos.close();

        } catch (IOException e) {
            throw new BaseException(FILE_ZIP_ERROR);
        } finally {
            try { if(fis != null)fis.close(); } catch (IOException e1) {log.info(e1.getMessage());/*ignore*/}
            try { if(zipOut != null)zipOut.closeEntry();} catch (IOException e2) {log.info(e2.getMessage());/*ignore*/}
            try { if(zipOut != null)zipOut.close();} catch (IOException e3) {log.info(e3.getMessage());/*ignore*/}
            try { if(fos != null)fos.close(); } catch (IOException e4) {log.info(e4.getMessage());/*ignore*/}
        }
    }

    public void deleteOldFile(String path){
        File oldFile = new File(path);
        if(oldFile.exists()) {
            oldFile.delete();
        }
    }

    public void initData() throws BaseException{
        try {
            getMusicFilesFromS3();
            getMatchingZipFileFromS3();
        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    // cloudfront에서 Musics.zip 관련 파일들 다운로드 후 Init
    public void getMusicFilesFromS3() throws BaseException{

        String[] fileNames = {"musicbook_TJ.txt", "musicbook_KY.txt", "matching_Musics.txt", "chart_KY.txt"};

        try{
            int cnt = 0;
            for(String fileName : fileNames){
                String url = cloudFrontUrl + fileName;

                File file = new File(fileName);
                FileUtils.copyURLToFile(new URL(url), file);

                if(cnt == 0){
                    this.TJ = getMusicBookFile("/" + fileName);
                } else if(cnt == 1){
                    this.KY = getMusicBookFile("/" + fileName);
                } else if(cnt == 2){
                    this.matchingMusics = getMatchingMusicsFile("/" + fileName);
                }

                cnt++;
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new BaseException(FILE_CLOUDFRONT_DOWNLOAD_ERROR);
        }
    }

    // cloudfront에서 MatchingFiles.zip file 다운로드 후 init
    public void getMatchingZipFileFromS3() throws BaseException{
        try{
            String fileName = "MatchingFiles.zip";
            String url = cloudFrontUrl + fileName;

            File file = new File(fileName);
            FileUtils.copyURLToFile(new URL(url), file);

            ZipFile zipFile = new ZipFile(file);

            Enumeration<ZipArchiveEntry> entries = zipFile.getEntries();

            //zip 파일 리스트 목록 순환
            while (entries.hasMoreElements()) {
                ZipArchiveEntry entry = entries.nextElement();
                InputStream is = zipFile.getInputStream(entry);

                FileUtils.copyInputStreamToFile(is, new File("/" + entry.getName()));

                if(entry.getName().equals("AllTimeLegend.txt")){
                    this.Legend = getMusicBookFile("/AllTimeLegend.txt");
                }
                else if(entry.getName().equals("matchingSingers.txt")){
                    this.matchingSingers = getMatchingSingersFile("/matchingSingers.txt");
                }
                else continue;
            }
            //inputStream close
            zipFile.close();

        } catch(BaseException e){
            throw new BaseException(e.getStatus());
        } catch (MalformedURLException me){
            me.printStackTrace();
            throw new BaseException(FILE_CLOUDFRONT_DOWNLOAD_ERROR);
        } catch (IOException e){
            e.printStackTrace();
            throw new BaseException(FILE_UNZIP_ERROR);
        }
    }

    // cloudfront에서 highest_Pitch file 다운로드 후 init
    public void getHighestFileFromS3() throws BaseException{
        try{
            String fileName = "highest_Pitch.txt";
            String url = cloudFrontUrl + fileName;

            File file = new File(fileName);
            FileUtils.copyURLToFile(new URL(url), file);

        } catch (MalformedURLException me){
            me.printStackTrace();
            throw new BaseException(FILE_CLOUDFRONT_DOWNLOAD_ERROR);
        } catch (IOException e){
            e.printStackTrace();
            throw new BaseException(FILE_UNZIP_ERROR);
        }
    }
}
