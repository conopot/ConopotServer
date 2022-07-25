package conopot.server.repository;

import conopot.server.config.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import static conopot.server.config.BaseResponseStatus.*;

@Repository
public class LyricsRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int checkAlreadyLyricsTJ(String number) throws BaseException{
        try{
            String query = "SELECT EXISTS (SELECT id FROM TJLYRICS WHERE id = ?)";
            return this.jdbcTemplate.queryForObject(query, Integer.class, Integer.valueOf(number));
        } catch(Exception e){
            throw new BaseException(DATABASE_CHECK_ALREADY_ERROR);
        }
    }
    public void saveLyricsTJ(String number, String lyrics) throws BaseException {
        try{
            String query = "insert TJLYRICS VALUES (?, ?)";
            Object[] param = new Object[]{number, lyrics};
            this.jdbcTemplate.update(query, param);
        } catch(Exception e){
            throw new BaseException(DATABASE_LYRICS_SAVED_ERROR);
        }
    }
}