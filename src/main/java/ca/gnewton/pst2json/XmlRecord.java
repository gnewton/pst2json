package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="message")
public class XmlRecord{
    @XmlElement
    String uuid=null;
    
    @XmlElement
    String from_name=null;

    @XmlElement
    String from=null;

    @XmlElement
    String received=null;

    @XmlElement
    String subject=null;

    @XmlAttribute
    String message_id=null;

    @XmlAttribute
    int filesource_id=-1;

    @XmlAttribute
    String in_reply_to_id=null;

    @XmlAttribute
    String return_path=null;

    @XmlElement
    String transport_message_headers=null;

    @XmlAttribute
    String conversation_id=null;

    @XmlElement
    XmlContent body;

    @XmlAttribute
    boolean cc_me;

    @XmlAttribute
    boolean message_recip_me;

    @XmlAttribute
    boolean message_to_me;

    @XmlAttribute
    boolean response_requested;

    @XmlAttribute
    boolean attachments;

    @XmlAttribute
    boolean forwarded;


    @XmlAttribute        
    boolean replied;

    @XmlAttribute
    boolean from_me;

    @XmlAttribute
    boolean read;

    @XmlAttribute
    boolean reply_requested;

    @XmlAttribute
    boolean resent;

    @XmlAttribute
    boolean submitted;

    @XmlAttribute
    boolean unmodified;

    @XmlAttribute
    boolean unsent;

    @XmlAttribute
    int folderDepth;

    @XmlAttribute
    int importance;

    @XmlAttribute
    int internet_article_number;

    @XmlAttribute
    int num_recipients;

    @XmlAttribute
    int num_attachments;

    @XmlAttribute
    int priority;

    @XmlAttribute
    int sensitivity;


    @XmlAttribute
    String foldersPath;

    @XmlElement(name="attachments")
    protected XmlAttachments mattachments;

    @XmlElement(name="recipients")
    protected XmlRecipients mrecipients;




}
