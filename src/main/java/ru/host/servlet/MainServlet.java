package ru.host.servlet;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.host.AppBootStrap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by Zloy on 03.07.2017.
 */

@WebServlet(urlPatterns = "/*")
public class MainServlet extends HttpServlet {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainServlet.class);
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }

}
