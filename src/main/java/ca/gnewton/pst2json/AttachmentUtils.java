package ca.gnewton.pst2json;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.Base64.Encoder;
import java.util.Base64;

import javax.xml.bind.DatatypeConverter;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.BodyContentHandler;
import org.xml.sax.SAXException;
import org.apache.commons.io.IOUtils;

public class AttachmentUtils{
    String contentBase64;
    byte[] content;
    String contentSha256Hex;


    public AttachmentUtils(){
        
    }

    public AttachmentUtils(InputStream is)throws IOException{

        this.content = IOUtils.toByteArray(is);
        /*
        BufferedInputStream bis = new BufferedInputStream(is);
            
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();

	byte[] data = new byte[65536];
        int bytesRead= -1;
	while ((bytesRead = bis.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, bytesRead);
	}
	buffer.flush();
        this.content = buffer.toByteArray();

        bis.close();
        is.close();
        buffer.close();
        */
    }
    
    // Derived from: http://www.baeldung.com/convert-input-stream-to-array-of-bytes
    public final void convertToBase64() throws IOException, NoSuchAlgorithmException{
	MessageDigest sha = MessageDigest.getInstance("SHA-256");
	
        sha.update(content, 0, content.length);
	byte[] shaBytes = sha.digest();
        this.contentSha256Hex = bytesToHex(shaBytes);
	this.contentBase64 = Base64.getEncoder().encodeToString(this.content);
    }

        private static String bytesToHex(byte[] hash) {
            return DatatypeConverter.printHexBinary(hash);
        }

    private static String bytesToHex2(byte[] hash) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xff & hash[i]);
            if(hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
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
        System.err.println("Extracting text for: " + filename + "   mime=" + mime);
	BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(content));
	AutoDetectParser parser = new AutoDetectParser();
	BodyContentHandler handler = new BodyContentHandler(-1);
	Metadata metadata = new Metadata();

	try{
	    parser.parse(bis, handler, metadata);
	}
        catch(org.apache.tika.exception.EncryptedDocumentException err){
	    err.printStackTrace();
            return "<<Encrypted document>>";
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
	catch(Throwable t){
	    t.printStackTrace();
	}

	return handler.toString();
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
