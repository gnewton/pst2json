package ca.gnewton.pst2json;

import java.io.IOException;
import com.pff.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import java.util.Base64;
import java.util.Stack;
import java.util.Vector;
import java.util.Date;
import java.util.Iterator;
import java.util.Calendar;
import java.util.Properties;

import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;

public class XmlWriter implements Writer{

    private Marshaller jaxbMarshaller = null;
    OutputStream out = null;

    public void init(Properties p) throws Exception{
        
    }
    
    public XmlWriter(OutputStream out, String []filenames) throws Exception{
	this.out = out;

        this.writeHeader(out, filenames.length);

        JAXBContext fsContext = JAXBContext.newInstance(XmlFileSource.class);
        Marshaller fsMarshaller = fsContext.createMarshaller();
        this.setMarshalProperties(fsMarshaller);

        int i;
        for(i=0; i<filenames.length; i++){
            XmlFileSource fs = new XmlFileSource(i,filenames[i]);
            fsMarshaller.marshal(fs, out);
        }
    }

    public void setMarshalProperties(Marshaller m) throws Exception{
	m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	m.setProperty(Marshaller.JAXB_FRAGMENT, true);
	m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
    }
    
    public void writeHeader(OutputStream out, int numFiles) throws Exception{
        Stack<String> foldersPath = new Stack<String>();

	JAXBContext jaxbContext = JAXBContext.newInstance(XmlRecord.class);
        jaxbMarshaller = jaxbContext.createMarshaller();
        this.setMarshalProperties(jaxbMarshaller);
	out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>".getBytes(StandardCharsets.UTF_8));
	out.write("<messages>".getBytes(StandardCharsets.UTF_8));


	JAXBContext metaContext = JAXBContext.newInstance(XmlMeta.class);
	Marshaller metaMarshaller = metaContext.createMarshaller();
        this.setMarshalProperties(metaMarshaller);
	//


	Calendar calendar = Calendar.getInstance();

	XmlMeta m = new XmlMeta("timestamp", Util.dateFormat.format(calendar.getTime()));
	metaMarshaller.marshal(m, new OutputStreamWriter(out, "UTF-8"));

	m = new XmlMeta("created_by", "ca.gnewton.pst2json");
	metaMarshaller.marshal(m, out);

	m = new XmlMeta("base64_body", Config.noBase64Encode);
	metaMarshaller.marshal(m, out);
        
        m = new XmlMeta("num_filesources", numFiles + "");
        metaMarshaller.marshal(m, out);



	
    }
    
    public void close() throws Exception{
	out.write("</messages>".getBytes(StandardCharsets.UTF_8));
	out.flush();
	out.close();
    }

    



    public final void processMessage(PSTMessage email, Stack<String> foldersPath, final int filesource_id, int depth) throws PSTException {
        Worker worker = new Worker(jaxbMarshaller,out,email, foldersPath, filesource_id, depth);
        worker.run();
    }
    //// mime4j
    /*
    public final void mime4j(){
        CharsetEncoder ENCODER = Charset.forName("UTF-8").newEncoder();
        final File mbox = new File(mboxPath);
        
        for (CharBufferWrapper message : MboxIterator.fromFile(mbox).charset(ENCODER.charset()).build()) {
            System.out.println(message);
        }
    }
    */


}

