
package webservice.contract.client;

import javax.xml.ws.WebFault;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 */
@WebFault(name = "serviceException", targetNamespace = "http://www.example.org/myService/")
public class ServiceException
        extends Exception {

    /**
     * Java type that goes as soapenv:Fault detail element.
     */
    private ServiceExceptionType faultInfo;

    /**
     * @param message
     * @param faultInfo
     */
    public ServiceException(String message, ServiceExceptionType faultInfo) {
        super(message);
        this.faultInfo = faultInfo;
    }

    /**
     * @param message
     * @param faultInfo
     * @param cause
     */
    public ServiceException(String message, ServiceExceptionType faultInfo, Throwable cause) {
        super(message, cause);
        this.faultInfo = faultInfo;
    }

    /**
     * @return returns fault bean: main.java.webservice.contract.client.ServiceExceptionType
     */
    public ServiceExceptionType getFaultInfo() {
        return faultInfo;
    }

}
