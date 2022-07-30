package conopot.server.repository;

import conopot.server.config.BaseException;
import conopot.server.config.FilePath;
import conopot.server.dto.Highest;
import conopot.server.dto.MatchingMusic;
import conopot.server.dto.Music;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import static conopot.server.config.BaseResponseStatus.*;

@Repository @Slf4j
public class FileRepository {

    @Value("${url.cloudfront}")
    private String cloudFrontUrl;

    private FilePath filePath;

    public FileRepository() throws BaseException{
        this.filePath = new FilePath();
    }

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
    public ArrayList<Highest> getHighest(String path) throws BaseException{

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
            throw new BaseException(FILE_NOTFOUND_ERROR);
        } catch(IOException e){
            throw new BaseException(FILE_INPUT_ERROR);
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
    }

    // zip 파일로 변환
    public void makeZip(String path) throws BaseException{

        // 이미 존재한다면, 삭제하기
        File oldFile = new File(path + "Musics.zip");
        if(oldFile.exists()) {
            oldFile.delete();
        }

        File file_ = new File(path);
        File[] listFiles = file_.listFiles();

        FileOutputStream fos = null;
        ZipOutputStream zipOut = null;
        FileInputStream fis = null;

        try {

            fos = new FileOutputStream(path + "/Musics.zip");
            zipOut = new ZipOutputStream(fos);

            for(File fileToZip :  listFiles) {

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
            getZipFileFromS3();
            unzipFile(filePath.S3_ZIP_FILE, filePath.ZIP_FILE);
        } catch (BaseException e){
            throw new BaseException(e.getStatus());
        }
    }

    // cloudfront에서 zip file 다운로드
    public void getZipFileFromS3() throws BaseException{
        try{
            String OUTPUT_FILE_PATH = "src/main/resources/static/MusicDB/Musics.zip";
            String FILE_URL = cloudFrontUrl;
            deleteOldFile(OUTPUT_FILE_PATH);
            try(InputStream in = new URL(FILE_URL).openStream()){
                Path imagePath = Paths.get(OUTPUT_FILE_PATH);
                Files.copy(in, imagePath);
            }
        } catch(Exception e){
            e.printStackTrace();
            throw new BaseException(FILE_CLOUDFRONT_DOWNLOAD_ERROR);
        }
    }

    public void unzipFile(String source, String target) throws BaseException {

        Path sourceZip = Paths.get(source);
        Path targetDir = Paths.get(target);

        try (ZipInputStream zis = new ZipInputStream(new FileInputStream(sourceZip.toFile()))) {

            // list files in zip
            ZipEntry zipEntry = zis.getNextEntry();
            while (zipEntry != null) {

                boolean isDirectory = false;
                if (zipEntry.getName().endsWith(File.separator)) {
                    isDirectory = true;
                }

                Path newPath = zipSlipProtect(zipEntry, targetDir);
                if (isDirectory) {
                    Files.createDirectories(newPath);
                } else {
                    if (newPath.getParent() != null) {
                        if (Files.notExists(newPath.getParent())) {
                            Files.createDirectories(newPath.getParent());
                        }
                    }
                    // copy files
                    Files.copy(zis, newPath, StandardCopyOption.REPLACE_EXISTING);
                }

                zipEntry = zis.getNextEntry();
            }
            zis.closeEntry();
        } catch (IOException e) {
            e.printStackTrace();
            throw new BaseException(FILE_UNZIP_ERROR);
        }
    }

    public Path zipSlipProtect(ZipEntry zipEntry, Path targetDir) throws IOException {

        // test zip slip vulnerability
        Path targetDirResolved = targetDir.resolve(zipEntry.getName());

        // make sure normalized file still has targetDir as its prefix
        // else throws exception
        Path normalizePath = targetDirResolved.normalize();
        if (!normalizePath.startsWith(targetDir)) {
            throw new IOException("Bad zip entry: " + zipEntry.getName());
        }
        return normalizePath;
    }

}
