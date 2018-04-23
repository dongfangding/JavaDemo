
package webservice.contract.client;

import javax.xml.namespace.QName;
import javax.xml.ws.*;
import java.net.MalformedURLException;
import java.net.URL;


/**
 * This class was generated by the JAX-WS RI.
 * JAX-WS RI 2.2.4-b01
 * Generated source version: 2.2
 */
@WebServiceClient(name = "MyServiceImplService", targetNamespace = "http://www.example.org/myService/", wsdlLocation = "http://localhost:8989/ms?wsdl")
public class MyServiceImplService
        extends Service {

    private final static URL MYSERVICEIMPLSERVICE_WSDL_LOCATION;
    private final static WebServiceException MYSERVICEIMPLSERVICE_EXCEPTION;
    private final static QName MYSERVICEIMPLSERVICE_QNAME = new QName("http://www.example.org/myService/", "MyServiceImplService");

    static {
        URL url = null;
        WebServiceException e = null;
        try {
            url = new URL("http://localhost:8989/ms?wsdl");
        } catch (MalformedURLException ex) {
            e = new WebServiceException(ex);
        }
        MYSERVICEIMPLSERVICE_WSDL_LOCATION = url;
        MYSERVICEIMPLSERVICE_EXCEPTION = e;
    }

    public MyServiceImplService() {
        super(__getWsdlLocation(), MYSERVICEIMPLSERVICE_QNAME);
    }

    public MyServiceImplService(WebServiceFeature... features) {
        super(__getWsdlLocation(), MYSERVICEIMPLSERVICE_QNAME, features);
    }

    public MyServiceImplService(URL wsdlLocation) {
        super(wsdlLocation, MYSERVICEIMPLSERVICE_QNAME);
    }

    public MyServiceImplService(URL wsdlLocation, WebServiceFeature... features) {
        super(wsdlLocation, MYSERVICEIMPLSERVICE_QNAME, features);
    }

    public MyServiceImplService(URL wsdlLocation, QName serviceName) {
        super(wsdlLocation, serviceName);
    }

    public MyServiceImplService(URL wsdlLocation, QName serviceName, WebServiceFeature... features) {
        super(wsdlLocation, serviceName, features);
    }

    /**
     * @return returns MyService
     */
    @WebEndpoint(name = "MyServiceImplPort")
    public MyService getMyServiceImplPort() {
        return super.getPort(new QName("http://www.example.org/myService/", "MyServiceImplPort"), MyService.class);
    }

    /**
     * @param features A list of {@link javax.xml.ws.WebServiceFeature} to configure on the proxy.  Supported features not in the <code>features</code> parameter will have their default values.
     * @return returns MyService
     */
    @WebEndpoint(name = "MyServiceImplPort")
    public MyService getMyServiceImplPort(WebServiceFeature... features) {
        return super.getPort(new QName("http://www.example.org/myService/", "MyServiceImplPort"), MyService.class, features);
    }

    private static URL __getWsdlLocation() {
        if (MYSERVICEIMPLSERVICE_EXCEPTION != null) {
            throw MYSERVICEIMPLSERVICE_EXCEPTION;
        }
        return MYSERVICEIMPLSERVICE_WSDL_LOCATION;
    }

}
