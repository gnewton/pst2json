package ca.gnewton.pst2json;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="attachment")
public class XmlAttachment{
    private String filename;
    private String mime;
    private String sha256Hex;
    private String attachment_content_disposition;
    private int attach_type;
    private String content;
    private String contentTextExtracted;
    /**
     * Describe size here.
     */
    private int size;

    @XmlAttribute
    public final String getMime() {
	return mime;
    }
    public final void setMime(final String mime) {
	this.mime = mime;
    }

    @XmlAttribute
    public final String getFilename() {
	return filename;
    }
    public final void setFilename(final String filename) {
	this.filename = filename;
    }

    @XmlAttribute
    public final String getAttachment_content_disposition() {
	return attachment_content_disposition;
    }
    public final void setAttachment_content_disposition(final String attachment_content_disposition) {
	this.attachment_content_disposition = attachment_content_disposition;
    }

    @XmlAttribute
    public final int getAttach_type() {
	return attach_type;
    }
    public final void setAttach_type(final int attach_type) {
	this.attach_type = attach_type;
    }

    @XmlElement
    public final String getContent() {
	return content;
    }

    public final void setContent(final String content) {
	this.content = content;
    }


    @XmlElement
    public final String getSha256Hex() {
	return sha256Hex;
    }

    public final void setSha256Hex(final String sha256) {
	this.sha256Hex = sha256;
    }

        @XmlAttribute
    public final int getSize() {
	return size;
    }
    public final void setSize(final int size) {
	this.size = size;
    }


        @XmlElement
    public final String getContentTextExtracted() {
	return contentTextExtracted;
    }

    public final void setContentTextExtracted(final String contentTextExtracted) {
	this.contentTextExtracted = contentTextExtracted;
    }


}
