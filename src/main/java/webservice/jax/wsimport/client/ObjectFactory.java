
package webservice.jax.wsimport.client;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the com.java.webservice.jax.wsimport.client package.
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

    private final static QName _ContactResponse_QNAME = new QName("http://service.wsimport.jax.webservice.java.com/", "contactResponse");
    private final static QName _Contact_QNAME = new QName("http://service.wsimport.jax.webservice.java.com/", "contact");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: com.java.webservice.jax.wsimport.client
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link ContactResponse }
     */
    public ContactResponse createContactResponse() {
        return new ContactResponse();
    }

    /**
     * Create an instance of {@link Contact }
     */
    public Contact createContact() {
        return new Contact();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link ContactResponse }{@code >}}
     */
    @XmlElementDecl(namespace = "http://service.wsimport.jax.webservice.java.com/", name = "contactResponse")
    public JAXBElement<ContactResponse> createContactResponse(ContactResponse value) {
        return new JAXBElement<ContactResponse>(_ContactResponse_QNAME, ContactResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Contact }{@code >}}
     */
    @XmlElementDecl(namespace = "http://service.wsimport.jax.webservice.java.com/", name = "contact")
    public JAXBElement<Contact> createContact(Contact value) {
        return new JAXBElement<Contact>(_Contact_QNAME, Contact.class, null, value);
    }

}
