
package webservice.jax.wsimport.client;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for contactResponse complex type.
 *
 * <p>The following schema fragment specifies the expected content contained within this class.
 *
 * <pre>
 * &lt;complexType name="contactResponse">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="contactResult" type="{http://www.w3.org/2001/XMLSchema}string" minOccurs="0"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "contactResponse", propOrder = {
        "contactResult"
})
public class ContactResponse {

    protected String contactResult;

    /**
     * Gets the value of the contactResult property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getContactResult() {
        return contactResult;
    }

    /**
     * Sets the value of the contactResult property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setContactResult(String value) {
        this.contactResult = value;
    }

}
