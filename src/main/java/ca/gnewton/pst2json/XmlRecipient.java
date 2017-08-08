package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="recipient")
public class XmlRecipient{
    private String name;
    private String email;
    private String smtp;
    private String mapi;

    @XmlAttribute
    public final String getName() {
	return name;
    }
    public final void setName(final String name) {
	this.name = name;
    }

    @XmlAttribute
    public final String getEmail() {
	return email;
    }
    public final void setEmail(final String email) {
	this.email = email;
    }

    @XmlAttribute
    public final String getSmtp() {
	return smtp;
    }
    public final void setSmtp(final String smtp) {
	this.smtp = smtp;
    }

    @XmlAttribute
    public final String getMapi() {
	return mapi;
    }
    public final void setMapi(final String mapi) {
	this.mapi = mapi;
    }

}
