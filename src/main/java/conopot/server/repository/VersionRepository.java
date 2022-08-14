package conopot.server.repository;

import conopot.server.config.BaseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;

import static conopot.server.config.BaseResponseStatus.DATABASE_VERSION_SAVED_ERROR;

@Repository
public class VersionRepository {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void savedVersion(String date, String status) throws BaseException{
        try{
            String query = "INSERT VERSION VALUES (0, ?, ?)";
            Object[] param = new Object[]{date, status};
            this.jdbcTemplate.update(query, param);
        } catch(Exception e){
            throw new BaseException(DATABASE_VERSION_SAVED_ERROR);
        }
    }
}
