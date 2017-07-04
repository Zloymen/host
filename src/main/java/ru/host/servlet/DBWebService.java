package ru.host.servlet;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.host.AppBootStrap;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.jws.WebService;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Created by Zloy on 04.07.2017.
 */
@WebService(endpointInterface = "ru.host.servlet.IDBWebService")
public class DBWebService implements IDBWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(AppBootStrap.class);

    private HikariDataSource pool = null;

    @Override
    public String getCreate(String schemaName, String tableName) {
        try(Connection conn = pool.getConnection()){

            DatabaseMetaData mt = conn.getMetaData();


            try(ResultSet columns = mt.getColumns(null,schemaName, tableName, null)){
                while (columns.next()) {
                    LOGGER.debug(
                            "  "+columns.getString("TABLE_SCHEM")
                                    + ", "+columns.getString("TABLE_NAME")
                                    + ", "+columns.getString("COLUMN_NAME")
                                    + ", "+columns.getString("TYPE_NAME")
                                    + ", "+columns.getInt("COLUMN_SIZE")
                                    + ", "+columns.getInt("NULLABLE"));
                }
            }

            try(ResultSet primaryKeys = mt.getPrimaryKeys(null,schemaName, tableName)){
                while (primaryKeys.next()) {
                    LOGGER.debug(
                            "  "+primaryKeys.getString("TABLE_SCHEM")
                                    + ", "+primaryKeys.getString("TABLE_NAME")
                                    + ", "+primaryKeys.getString("COLUMN_NAME")
                                    + ", "+primaryKeys.getString("TYPE_NAME")
                                    + ", "+primaryKeys.getInt("COLUMN_SIZE")
                                    + ", "+primaryKeys.getInt("NULLABLE"));
                }
            }

        }catch (SQLException ex){

        }

        return null;
    }

    @Override
    public String getSelect(String schemaName, String tableName) {
        return null;
    }

    @Override
    public String getUpdate(String schemaName, String tableName) {
        return null;
    }

    @Override
    public String getInsert(String schemaName, String tableName) {
        return null;
    }

    @Override
    public String getDelete(String schemaName, String tableName) {
        return null;
    }

    @PostConstruct
    void constructor() throws IOException {
        Properties property = new Properties();

        try(InputStream fis = getClass().getClassLoader().getResourceAsStream("config.properties")){
            property.load(fis);

            String host = property.getProperty("host");
            String login = property.getProperty("login");
            String password = property.getProperty("password");

            LOGGER.debug("HOST: " + host
                    + ", LOGIN: " + login
                    + ", PASSWORD: " + password);

            pool = new HikariDataSource();
            pool.setDriverClassName("org.postgresql.Driver");
            pool.setJdbcUrl(host);
            pool.setMaximumPoolSize(10);
            pool.setUsername(login);
            pool.setPassword(password);
            pool.setIdleTimeout(10000);
            pool.setLeakDetectionThreshold(3000);
            pool.setConnectionTimeout(60000);
            pool.setReadOnly(false);
            pool.addDataSourceProperty("stringtype", "unspecified");

        } catch (Exception e) {
            LOGGER.error("Error run", e);
            throw e;
        }
    }
    @PreDestroy
    void destroy(){
        if(pool != null) pool.close();
    }
}
