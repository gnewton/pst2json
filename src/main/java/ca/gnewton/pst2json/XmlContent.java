package ca.gnewton.pst2json;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="content")
public class XmlContent{
    public static final String Base64Encoding = "b64";
    
    @XmlElement
    String sha256Hex;
    @XmlAttribute
    String attachment_content_disposition;
    @XmlAttribute
    int attach_type;
    @XmlElement
    String content;
    @XmlAttribute
    String contentCompression;
    @XmlAttribute
    String contentEncoding;
}
