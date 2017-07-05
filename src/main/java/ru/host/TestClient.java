package ru.host;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.host.servlet.DBWebService;
import ru.host.servlet.IDBWebService;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebServiceRef;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * TestClient
 *
 */
public class TestClient{

    private static final Logger LOGGER = LoggerFactory.getLogger(TestClient.class);
    public static void main(String[] args) throws MalformedURLException {
        Service testService = Service.create(
                new URL("http://localhost:8080/generateSQL?wsdl"),
                new QName("http://servlet.host.ru/", "DBWebServiceService"));
        IDBWebService calculator = testService.getPort(IDBWebService.class);

        LOGGER.info(calculator.getCreate("host","test_table"));
    }
}
