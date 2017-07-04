package ru.host.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * Created by Zloy on 03.07.2017.
 */
public class MapperService {

    private static final Logger LOGGER = LoggerFactory.getLogger(MapperService.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    static {
        MAPPER.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
    }

    public static <T> T read(String sourceStr, Class<T> clazz) {
        try {
            return MAPPER.readValue(sourceStr, clazz);
        }
        catch (IOException e) {
            LOGGER.error("error write request");
            return null;
        }
    }

    public static <T> void write(HttpServletResponse target, T object)  {
        try {
            MAPPER.writeValue(target.getWriter(), object);
            String strResponse = MAPPER.writeValueAsString(object);
            target.getWriter().write(strResponse);
        }
        catch (IOException e) {
            LOGGER.error("error write response");
        }
    }
}