package ca.gnewton.pst2json;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="content")
public class XmlContent{
    public static final String Base64Encoding = "b64";
    public static final String NoEncoding = "";
    @XmlElement
    String data;
    @XmlAttribute
    String contentEncoding;
}
