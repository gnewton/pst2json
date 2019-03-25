package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attachment")
public class XmlAttachment{
    @XmlAttribute
     String filename;
    @XmlAttribute
     String mime = null;
    //@XmlAttribute
    //String sha256Hex;
    @XmlAttribute
     String attachment_content_disposition;
    @XmlAttribute
     int attach_type;
    @XmlElement
    XmlMeta[] meta;
    @XmlElement
    XmlContent content;
    @XmlElement
    XmlContentExtracted contentTextExtracted;
    @XmlElement
     int size;


}
