package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="meta")
public class XmlMeta{

    public XmlMeta(){

    }
    
     public XmlMeta(final String k, final String v){
     	setKey(k);
     	setValue(v);
     }

    public XmlMeta(final String k, final boolean b){
     	setKey(k);
	if (b){
	    setValue("true");
	}else{
	    	    setValue("false");
	}
     }
    
    String key;
    @XmlAttribute
    public void setKey(final String k){
	this.key = k;
    }
    public String getKey(){
	return this.key;
    }

    String value;
    @XmlAttribute
    public void setValue(final String v){
	this.value = v;
    }
    public String getValue(){
	return this.value;
    }


}
