package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attachment")
public class XmlAttachment{
    @XmlAttribute
     String filename;
    @XmlAttribute
     String mime;
    @XmlAttribute
     String sha256Hex;
    @XmlAttribute
     String attachment_content_disposition;
    @XmlAttribute
     int attach_type;
    @XmlElement
    XmlContent content;
    @XmlElement
    XmlContent contentTextExtracted;
    @XmlElement
     int size;


}
