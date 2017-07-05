package ru.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * загрузка
 * Created by Zloy on 03.07.2017.
 */

//@WebListener
public class AppBootStrap implements ServletContextListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(AppBootStrap.class);
    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {

        Properties property = new Properties();

        try(InputStream fis = getClass().getClassLoader().getResourceAsStream("config.properties")){
            property.load(fis);

            String host = property.getProperty("host");
            String login = property.getProperty("login");
            String password = property.getProperty("password");

            LOGGER.debug("HOST: " + host
                    + ", LOGIN: " + login
                    + ", PASSWORD: " + password);

            DatabasePool.createPool(host, login, password);

        } catch (Exception e) {
            LOGGER.error("Error run", e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {

    }
}
