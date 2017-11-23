
package main.java.webservice.contract.client;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.xml.bind.annotation.XmlSeeAlso;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 * 
 */
@WebService(name = "MyService", targetNamespace = "http://www.example.org/myService/")
@SOAPBinding(parameterStyle = SOAPBinding.ParameterStyle.BARE)
@XmlSeeAlso({
    ObjectFactory.class
})
public interface MyService {


    /**
     * 
     * @param addRequest
     * @return
     *     returns main.java.webservice.contract.client.AddResponse
     */
    @WebMethod
    @WebResult(name = "addResponse", targetNamespace = "http://www.example.org/myService/", partName = "addResponse")
    public AddResponse add(
        @WebParam(name = "addRequest", targetNamespace = "http://www.example.org/myService/", partName = "addRequest")
        AddRequest addRequest);

    /**
     * 
     * @param divideRequest
     * @return
     *     returns main.java.webservice.contract.client.DivideResponse
     * @throws ServiceException
     */
    @WebMethod
    @WebResult(name = "divideResponse", targetNamespace = "http://www.example.org/myService/", partName = "divideResponse")
    public DivideResponse divide(
        @WebParam(name = "divideRequest", targetNamespace = "http://www.example.org/myService/", partName = "divideRequest")
        DivideRequest divideRequest)
        throws ServiceException
    ;

}
