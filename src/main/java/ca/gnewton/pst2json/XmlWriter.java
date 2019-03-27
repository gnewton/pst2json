package ca.gnewton.pst2json;

import java.io.IOException;
import com.pff.*;
//import java.util.*;
import java.text.SimpleDateFormat;

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
import java.text.SimpleDateFormat;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.io.OutputStreamWriter;

public class XmlWriter implements Writer{

    boolean extractTextFromAttachments = false;
    boolean base64Body = false;
    private Marshaller jaxbMarshaller = null;
    OutputStream out = null;
    
    public XmlWriter(OutputStream out,  final boolean extractTextFromAttachments, final boolean base64Body, String []filenames) throws Exception{
	this.out = out;
	this.base64Body = base64Body;
	this.extractTextFromAttachments = extractTextFromAttachments;

	Stack<String> foldersPath = new Stack<String>();

	JAXBContext jaxbContext = JAXBContext.newInstance(XmlRecord.class);
	jaxbMarshaller = jaxbContext.createMarshaller();
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

	out.write("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>".getBytes(StandardCharsets.UTF_8));
	out.write("<messages>".getBytes(StandardCharsets.UTF_8));


	JAXBContext metaContext = JAXBContext.newInstance(XmlMeta.class);
	Marshaller metaMarshaller = metaContext.createMarshaller();
	metaMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	metaMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
	metaMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	//

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
	Calendar calendar = Calendar.getInstance();

	XmlMeta m = new XmlMeta("timestamp", sdf.format(calendar.getTime()));
	metaMarshaller.marshal(m, new OutputStreamWriter(out, "UTF-8"));

	m = new XmlMeta("created_by", "ca.gnewton.pst2json");
	metaMarshaller.marshal(m, out);

	m = new XmlMeta("base64_body", this.base64Body);
	metaMarshaller.marshal(m, out);
        
        m = new XmlMeta("num_filesources", filenames.length + "");
        metaMarshaller.marshal(m, out);


        JAXBContext fsContext = JAXBContext.newInstance(XmlFileSource.class);
        Marshaller fsMarshaller = fsContext.createMarshaller();
	fsMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	fsMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
	fsMarshaller.setProperty(Marshaller.JAXB_ENCODING, "UTF-8");
	//
        int i;
        for(i=0; i<filenames.length; i++){
            XmlFileSource fs = new XmlFileSource(i,filenames[i]);
            fsMarshaller.marshal(fs, out);
        }
    }
    public void close() throws Exception{
	out.write("</messages>".getBytes(StandardCharsets.UTF_8));
	out.flush();
	out.close();
    }


    int depth = 0; 
    public final void process(PSTFolder folder,Stack<String>foldersPath, int filesource_id)
	throws PSTException, java.io.IOException
    {
        Worker worker = new Worker();
	String folderName = folder.getDisplayName();
	if (folderName != null && folderName.length() >0){
	    foldersPath.push(folder.getDisplayName());
	    ++depth;
	}
	    
        // go through the folders...
        if (folder.hasSubfolders()) {
            Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
                this.process(childFolder, foldersPath,filesource_id);
            }
        }

        // and now the emails for this folder
        if (folder.getContentCount() > 0) {
	    
            PSTMessage email = (PSTMessage)folder.getNextChild();
            while (email != null) {
                //printDepth();
                //System.out.println("Email: "+email.getSubject() + "|| " + email.getMessageDeliveryTime());
		try{
		    worker.print(jaxbMarshaller,out,email, foldersPath, filesource_id, depth, base64Body,extractTextFromAttachments);
		}catch(Exception  e){
		    throw new IOException();
		}
                email = (PSTMessage)folder.getNextChild();
            }
        }

	if (folderName != null && folderName.length() >0){
	    --depth;
	    foldersPath.pop();
	}
    }




}

