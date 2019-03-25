package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="m")
public class XmlMetadataItem{
    
    @XmlAttribute
    String n;

    @XmlAttribute
    String v;
}
