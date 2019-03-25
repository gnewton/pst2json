package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="contentMetadata")
public class XmlContentMetadata{
    @XmlElement(name="m")
    XmlMetadataItem[] items;
}
