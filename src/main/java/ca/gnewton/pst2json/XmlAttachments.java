package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attachments")
public class XmlAttachments{
    @XmlElement(name="attachment")
    protected XmlAttachment[] attachments;
}
