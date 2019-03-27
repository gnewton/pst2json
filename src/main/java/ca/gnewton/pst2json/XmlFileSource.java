package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="filesource")
public class XmlFileSource{
        public XmlFileSource(){

    }
    
    public XmlFileSource(int id, String filename){
        this.id = id;
        this.filename = filename;
    }
    @XmlAttribute
    public int id=-1;

    @XmlAttribute
    public String filename = null;


}
