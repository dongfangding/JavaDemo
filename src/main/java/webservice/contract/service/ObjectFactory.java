
package webservice.contract.service;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the main.java.webservice.contract.service package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _DivideResponse_QNAME = new QName("http://www.example.org/myService/", "divideResponse");
    private final static QName _AddRequest_QNAME = new QName("http://www.example.org/myService/", "addRequest");
    private final static QName _DivideRequest_QNAME = new QName("http://www.example.org/myService/", "divideRequest");
    private final static QName _ServiceException_QNAME = new QName("http://www.example.org/myService/", "serviceException");
    private final static QName _AuthInfo_QNAME = new QName("http://www.example.org/myService/", "authInfo");
    private final static QName _AddResponse_QNAME = new QName("http://www.example.org/myService/", "addResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: main.java.webservice.contract.service
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link AddResponse }
     */
    public AddResponse createAddResponse() {
        return new AddResponse();
    }

    /**
     * Create an instance of {@link ServiceExceptionType }
     */
    public ServiceExceptionType createServiceExceptionType() {
        return new ServiceExceptionType();
    }

    /**
     * Create an instance of {@link DivideRequest }
     */
    public DivideRequest createDivideRequest() {
        return new DivideRequest();
    }

    /**
     * Create an instance of {@link AuthInfo }
     */
    public AuthInfo createAuthInfo() {
        return new AuthInfo();
    }

    /**
     * Create an instance of {@link AddRequest }
     */
    public AddRequest createAddRequest() {
        return new AddRequest();
    }

    /**
     * Create an instance of {@link DivideResponse }
     */
    public DivideResponse createDivideResponse() {
        return new DivideResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DivideResponse }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/myService/", name = "divideResponse")
    public JAXBElement<DivideResponse> createDivideResponse(DivideResponse value) {
        return new JAXBElement<DivideResponse>(_DivideResponse_QNAME, DivideResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddRequest }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/myService/", name = "addRequest")
    public JAXBElement<AddRequest> createAddRequest(AddRequest value) {
        return new JAXBElement<AddRequest>(_AddRequest_QNAME, AddRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link DivideRequest }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/myService/", name = "divideRequest")
    public JAXBElement<DivideRequest> createDivideRequest(DivideRequest value) {
        return new JAXBElement<DivideRequest>(_DivideRequest_QNAME, DivideRequest.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ServiceExceptionType }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/myService/", name = "serviceException")
    public JAXBElement<ServiceExceptionType> createServiceException(ServiceExceptionType value) {
        return new JAXBElement<ServiceExceptionType>(_ServiceException_QNAME, ServiceExceptionType.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AuthInfo }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/myService/", name = "authInfo")
    public JAXBElement<AuthInfo> createAuthInfo(AuthInfo value) {
        return new JAXBElement<AuthInfo>(_AuthInfo_QNAME, AuthInfo.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link AddResponse }{@code >}}
     */
    @XmlElementDecl(namespace = "http://www.example.org/myService/", name = "addResponse")
    public JAXBElement<AddResponse> createAddResponse(AddResponse value) {
        return new JAXBElement<AddResponse>(_AddResponse_QNAME, AddResponse.class, null, value);
    }

}
