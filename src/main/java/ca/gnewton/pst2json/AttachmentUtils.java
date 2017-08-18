package ca.gnewton.pst2json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64.Encoder;
import java.util.Base64;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;


public class AttachmentUtils{
    String contentBase64;
    byte[] content;
    String contentSha1Base64;

    // Derived from: http://www.baeldung.com/convert-input-stream-to-array-of-bytes
    public final void convertToBase64(InputStream is) throws IOException, NoSuchAlgorithmException{
	MessageDigest sha = MessageDigest.getInstance("SHA-1");
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	int len;
	byte[] data = new byte[8192];
	while ((len = is.read(data, 0, data.length)) != -1) {
	    buffer.write(data, 0, len);
	    sha.update(data,0,len);
	}
	buffer.flush();
	byte[] shaDigest = sha.digest();
	
	this.content = buffer.toByteArray();
	this.contentSha1Base64 = Base64.getEncoder().encodeToString(shaDigest);
	this.contentBase64 = Base64.getEncoder().encodeToString(this.content);
    }


    public final String extractText(String filename, String mime){
	if (!hasAcceptableSuffix(filename)
	    &&
	    (
		mime == null
		|| mime.equals("")
		|| mime.equals("::")
		|| mime.startsWith("image/")
		|| mime.equals("::message/rfc822")
	    )
	    ){
	    return null;
	}
	ByteArrayInputStream bis = new ByteArrayInputStream(content);
	AutoDetectParser parser = new AutoDetectParser();
	BodyContentHandler handler = new BodyContentHandler(-1);
	Metadata metadata = new Metadata();

	try{
	    parser.parse(bis, handler, metadata);
	}
	catch(IOException err){
	    err.printStackTrace();
	}
	catch(SAXException err){
	    err.printStackTrace();
	}
	catch(TikaException err){
	    err.printStackTrace();
	}

	String tmp = handler.toString();
	//System.err.println(tmp);
	return tmp;
    }

    boolean hasAcceptableSuffix(String s){
	return s.endsWith(".doc")
	    || s.endsWith(".DOC")
	    || s.endsWith(".docx")
	    || s.endsWith(".DOCX")
	    || s.endsWith(".ppt")
	    || s.endsWith(".PPT")
	    || s.endsWith(".pdf")
	    || s.endsWith(".PDF")
	    || s.endsWith(".xls")
	    || s.endsWith(".XLS")
	    || s.endsWith(".xml")
	    || s.endsWith(".XML")
	    || s.endsWith(".html")
	    || s.endsWith(".HTML")
	    || s.endsWith(".htm")
	    || s.endsWith(".HTM")
	    || s.endsWith(".rtf")
	    || s.endsWith(".RFT");
    }
}
