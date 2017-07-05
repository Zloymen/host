package ru.host.servlet;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.host.AppBootStrap;
import ru.host.dto.MetaDto;

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
 * DBWebService
 * Created by Zloy on 04.07.2017.
 */
@WebService(endpointInterface = "ru.host.servlet.IDBWebService")
public class DBWebService implements IDBWebService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DBWebService.class);

    private static final String ERROR_MESSAGE = "таблица не найдена или произошла ошибка";

    private HikariDataSource pool = null;

    @Override
    public String getCreate(String schemaName, String tableName) {
        MetaDto metaDto = getMetaData(schemaName, tableName);
        if(metaDto == null) return ERROR_MESSAGE;
        StringBuilder sql = new StringBuilder("create table ");
        sql.append(metaDto.getSchemaName()).append(".").append(metaDto.getTableName()).append("(");
        for(int i = 0; i < metaDto.getColumns().size() - 1; i++ ){
            sql.append(i == 0 ? "" : ", ").append(metaDto.getColumns().get(i).getSpecColomn());
        }
        if(!metaDto.getConstaints().isEmpty()) {
            for (int i = 0; i < metaDto.getConstaints().size() - 1; i++) {

            }
        }

        sql.append(")");

        return sql.toString();
    }

    @Override
    public String getSelect(String schemaName, String tableName) {
        MetaDto metaDto = getMetaData(schemaName, tableName);
        if(metaDto == null) return ERROR_MESSAGE;
        StringBuilder sql = new StringBuilder("select ").append(getColumns(metaDto));
        sql.append(" from ").append(metaDto.getSchemaName()).append(".").append(metaDto.getTableName());

        StringBuilder where = getWhere(metaDto);
        if(!where.toString().isEmpty()){
            sql.append(" where ").append(where);
        }

        return sql.toString();
    }

    @Override
    public String getUpdate(String schemaName, String tableName) {
        MetaDto metaDto = getMetaData(schemaName, tableName);
        if(metaDto == null) return ERROR_MESSAGE;
        StringBuilder sql = new StringBuilder("update ");
        sql.append(metaDto.getSchemaName()).append(".").append(metaDto.getTableName()).append(" set ");
        StringBuilder where = new StringBuilder();
        int k = 1, n = 1;
        for(int i = 0; i < metaDto.getColumns().size() - 1; i++ ){
            MetaDto.Column column = metaDto.getColumns().get(i);
            MetaDto.PKConstraint constraint = metaDto.getConstaints().stream()
                    .filter(item -> item.getColumnName().equalsIgnoreCase(column.getColumnName()))
                    .findFirst()
                    .orElse(null);
            if(constraint == null){
                sql.append(k == 1 ? "" : ", ").append(column.getColumnName()).append("=?").append(k++);
            }else{
                sql.append(n == 1 ? "" : " and ").append(column.getColumnName()).append("=?").append(n++);
            }
        }

        if(!where.toString().isEmpty()){
            sql.append(" where ").append(where);
        }

        return sql.toString();
    }

    @Override
    public String getInsert(String schemaName, String tableName) {
        MetaDto metaDto = getMetaData(schemaName, tableName);
        if(metaDto == null) return ERROR_MESSAGE;
        StringBuilder sql = new StringBuilder("insert into ");
        sql.append(metaDto.getSchemaName()).append(".").append(metaDto.getTableName()).append("(");
        sql.append(getColumns(metaDto)).append(")values(");

        for(int i = 1; metaDto.getColumns().size() <= i;i++ ){
            sql.append(i == 1 ? "?" : ", ?").append(i);
        }

        sql.append(")");

        return sql.toString();
    }

    @Override
    public String getDelete(String schemaName, String tableName) {
        MetaDto metaDto = getMetaData(schemaName, tableName);
        if(metaDto == null) return ERROR_MESSAGE;
        StringBuilder sql = new StringBuilder("delete from ");
        sql.append(metaDto.getSchemaName()).append(".").append(metaDto.getTableName());
        StringBuilder where = getWhere(metaDto);
        if(!where.toString().isEmpty()){
            sql.append(" where ").append(where);
        }

        return sql.toString();
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

    private StringBuilder getWhere(MetaDto metaDto){
        StringBuilder where = new StringBuilder();
        for(int i = 0; i < metaDto.getConstaints().size()-1;i++ ){
            MetaDto.PKConstraint constraint = metaDto.getConstaints().get(i);
            where.append(i == 0 ? "(" : " and (").append(constraint.getColumnName()).append("=?").append(i).append(")");
        }
        return where;
    }

    private StringBuilder getColumns(MetaDto metaDto) {
        StringBuilder columns = new StringBuilder();
        for(int i = 0; i < metaDto.getColumns().size()-1;i++ ){
            MetaDto.Column column = metaDto.getColumns().get(i);
            columns.append(i == 0 ? "" : ", ").append(column.getColumnName());
        }
        return columns;
    }

    private MetaDto getMetaData(String schemaName, String tableName){
        try(Connection conn = pool.getConnection()){

            DatabaseMetaData mt = conn.getMetaData();

            MetaDto dto = new MetaDto(schemaName, tableName);

            try(ResultSet columns = mt.getColumns(null,schemaName, tableName, null)){
                while (columns.next()) {
                    dto.getColumns().add(MetaDto.createColumn(
                            columns.getString("COLUMN_NAME"),
                            columns.getString("TYPE_NAME"),
                            columns.getInt("COLUMN_SIZE"),
                            columns.getInt("NULLABLE") == 0
                            ));
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
                    dto.getConstaints().add(MetaDto.createConstraint(
                            primaryKeys.getString("COLUMN_NAME"),
                            primaryKeys.getString("PK_NAME")
                    ));
                    LOGGER.debug(
                                    "  "+primaryKeys.getString("TABLE_CAT")
                                    + ", "+primaryKeys.getString("TABLE_SCHEM")
                                    + ", "+primaryKeys.getString("TABLE_NAME")
                                    + ", "+primaryKeys.getString("COLUMN_NAME")
                                    + ", "+primaryKeys.getString("PK_NAME"));
                }
            }

            return dto;

        }catch (SQLException ex){
            LOGGER.error("getMetaData", ex);
            return  null;
        }
    }
}
