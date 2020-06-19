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

    private Marshaller jaxbMarshaller = null;
    OutputStream out = null;
    
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

	SimpleDateFormat sdf = new SimpleDateFormat("yyyy MMM dd HH:mm:ss");
	Calendar calendar = Calendar.getInstance();

	XmlMeta m = new XmlMeta("timestamp", sdf.format(calendar.getTime()));
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

    public void process(String filename, int filesource_id) throws Exception{
        System.err.println("Opening PST file: " + filename + "  " + filesource_id);

        PSTFile pstFile = Util.openPSTFile(filename);
        
        Stack<String> foldersPath = new Stack<String>();
        this.processPST(pstFile.getRootFolder(), foldersPath, filesource_id);
    }
    

    int depth = 0; 
    public final void processPST(PSTFolder folder,Stack<String>foldersPath, int filesource_id)
	throws PSTException, java.io.IOException
    {

	String folderName = folder.getDisplayName();
	if (folderName != null && folderName.length() >0){
	    foldersPath.push(folder.getDisplayName());
	    ++depth;
	}
	    
        // go through the folders...
        if (folder.hasSubfolders()) {
            Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
                this.processPST(childFolder, foldersPath,filesource_id);
            }
        }

        // and now the emails for this folder
        if (folder.getContentCount() > 0) {
	    
            PSTMessage email = (PSTMessage)folder.getNextChild();
            while (email != null) {
                //printDepth();
                //System.out.println("Email: "+email.getSubject() + "|| " + email.getMessageDeliveryTime());
		try{
                    Worker worker = new Worker(jaxbMarshaller,out,email, foldersPath, filesource_id, depth);
		    worker.run();
		}catch(Exception  e){
		    throw new IOException();
		}
                try{
                    email = (PSTMessage)folder.getNextChild();
                }catch(Throwable t){
                    // to deal with exception: java.lang.IndexOutOfBoundsException: Index: 95, Size: 95
                    email = null;
                }
            }
        }

	if (folderName != null && folderName.length() >0){
	    --depth;
	    foldersPath.pop();
	}
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

