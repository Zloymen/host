package ru.host;

import com.zaxxer.hikari.HikariDataSource;

/**
 * Created by Zloy on 03.07.2017.
 */
public class DatabasePool extends HikariDataSource {
    private static DatabasePool pool;

    private DatabasePool(){}

    protected static void  createPool(String url, String userName, String password){
        pool = new DatabasePool();
        pool.setDriverClassName("org.postgresql.Driver");
        pool.setJdbcUrl(url);
        pool.setMaximumPoolSize(10);
        pool.setUsername(userName);
        pool.setPassword(password);
        pool.setIdleTimeout(10000);
        pool.setLeakDetectionThreshold(3000);
        pool.setConnectionTimeout(60000);
        pool.setReadOnly(false);
        pool.addDataSourceProperty("stringtype", "unspecified");
    }

    public static DatabasePool getPool(){
        return pool;
    }

}
