package ru.host.servlet;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;

/**
 * Created by Zloy on 04.07.2017.
 */

@WebService
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface IDBWebService {
    @WebMethod
    String getCreate(String schemaName, String tableName);
    @WebMethod
    String getSelect(String schemaName, String tableName);
    @WebMethod
    String getUpdate(String schemaName, String tableName);
    @WebMethod
    String getInsert(String schemaName, String tableName);
    @WebMethod
    String getDelete(String schemaName, String tableName);
}