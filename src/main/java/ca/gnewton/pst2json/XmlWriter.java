package ca.gnewton.pst2json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import java.io.IOException;
import com.pff.*;
import java.util.*;
import java.text.SimpleDateFormat;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import java.util.Base64.Encoder;

public class XmlWriter implements Writer{

    private Marshaller jaxbMarshaller = null;

    public XmlWriter() throws Exception{


	Stack<String> foldersPath = new Stack<String>();

	JAXBContext jaxbContext = JAXBContext.newInstance(XmlRecord.class);
	jaxbMarshaller = jaxbContext.createMarshaller();
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);


    }
    public void close() throws Exception{

    }


    int depth = 0;
    public final void process(PSTFolder folder,Stack<String>foldersPath)
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
                this.process(childFolder, foldersPath);
            }
        }

        // and now the emails for this folder
        if (folder.getContentCount() > 0) {
	    
            PSTMessage email = (PSTMessage)folder.getNextChild();
            while (email != null) {
                //printDepth();
                //System.out.println("Email: "+email.getSubject() + "|| " + email.getMessageDeliveryTime());
		try{
		    print(email, null, foldersPath, depth);
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
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    public final void print(PSTMessage email, JsonGenerator gen, Stack<String> foldersPath, int depth) throws PSTException {

	Date rec = email.getMessageDeliveryTime();
	String receivedTime="";
	if (rec != null){
	    receivedTime  = dateFormat.format(rec);
	}
	try{

	    
	    XmlRecord r = new XmlRecord();
	     
	    //gen.writeObjectFieldStart("message");
	    r.setFolderDepth(depth);
	    ////gen.writeNumberField("folder_depth", depth);
	    
	    // Write folder hierarchy as list of strings
	    //gen.writeArrayFieldStart("folder");
	    Iterator<String> it = foldersPath.iterator();
	    while(it.hasNext()){
		String f = it.next();
		//gen.writeString(f);
	    }
	    //gen.writeEndArray();

	    

	    r.setSubject(email.getSubject());
	    r.setReceived(receivedTime);
	    r.setFrom_name(email.getSenderName());
	    r.setFrom(email.getSenderEmailAddress());
	    r.setMessage_id(email.getInternetMessageId());
	    r.setIn_reply_to_id(email.getInReplyToId());

	    if (email.getConversationId() != null){
		r.setConversation_id(Base64.getEncoder().encodeToString(email.getConversationId()));
	    }
	    r.setImportance(email.getImportance());
	    r.setInternet_article_number(email.getInternetArticleNumber());
	    r.setNum_recipients(email.getNumberOfRecipients());
	    r.setNum_attachments(email.getNumberOfAttachments());
	    r.setPriority(email.getPriority());
	    r.setSensitivity(email.getSensitivity());
	    if (email.getReturnPath() != null && email.getReturnPath().length() > 0){
		r.setReturn_path(email.getReturnPath());
	    }
	    //r.setTransport_message_headers(email.getTransportMessageHeaders());

	    // All booleans
	    r.setCc_me(email.getMessageCcMe());
	    r.setMessage_recip_me(email.getMessageRecipMe());
	    r.setMessage_to_me(email.getMessageToMe());
	    r.setResponse_requested(email.getResponseRequested());
	    r.setAttachments(email.hasAttachments());
	    r.setForwarded(email.hasForwarded());
	    r.setReplied(email.hasReplied());
	    r.setFrom_me(email.isFromMe());
	    r.setRead(email.isRead());
	    r.setReply_requested(email.isReplyRequested());
	    r.setResent(email.isResent());
	    r.setSubmitted(email.isSubmitted());
	    r.setUnmodified(email.isUnmodified());
	    r.setUnsent(email.isUnsent());
		
	    //gen.writeArrayFieldStart("recipients");
	    try{
		int n = email.getNumberOfRecipients();

		for (int i=0; i<n; i++){
		    
		    PSTRecipient recip = email.getRecipient(i);
		    ////gen.writeString(recip.getEmailAddress());
		    //gen.writeStartObject();
		    ////gen.writeString(recip.getSmtpAddress());
		    //stringOut(gen,"name", recip.getDisplayName());
		    //stringOut(gen,"smtp", recip.getSmtpAddress());
		    //stringOut(gen,"email", recip.getEmailAddress());
		    int mapi = recip.getRecipientFlags();
		    switch (mapi){
		    case com.pff.PSTRecipient.MAPI_BCC: 
			//stringOut(gen, "mapi", "BCC");
			break;
		    case com.pff.PSTRecipient.MAPI_CC: 
			//stringOut(gen, "mapi", "CC");
			break;
		    case com.pff.PSTRecipient.MAPI_TO: 
			//stringOut(gen, "mapi", "TO");
			break;
			    
		    }

		    //gen.writeEndObject();

		}
	    }catch(PSTException e){
		e.printStackTrace();
	    }
    
	    //gen.writeEndArray();

	    //++Attachments
	    int numberAttachments = email.getNumberOfAttachments();

	    if (numberAttachments>0){
		//gen.writeArrayFieldStart("attachments");
		r.mattachments = new XmlAttachment[numberAttachments];
		XmlAttachment xat;
		for(int i=0; i<numberAttachments; i++){
		    try{
			xat = new XmlAttachment();
			r.mattachments[i] = xat;

			PSTAttachment att = email.getAttachment(i);
			xat.setFilename(att.getFilename());
			xat.setSize(att.getAttachSize());
			xat.setAttachment_content_disposition(att.getAttachmentContentDisposition());
			xat.setAttach_type(att.getAttachMethod());
			
			    java.io.InputStream is = null;
			    try{
				is = att.getFileInputStream();
				if (is != null){
				    byte[] contentBytes = inputStream2ByteArray(is);
				    xat.setContent(Base64.getEncoder().encodeToString(contentBytes));
				}

			    }catch(IOException e){
				e.printStackTrace();
				throw e;
			    }
			    catch( PSTException e){
				e.printStackTrace();
				throw e;
			    }
			    catch(Throwable t){
				t.printStackTrace();
			    }finally{
				if (is != null){
				    try{
					is.close();
				    }catch(IOException e){
					e.printStackTrace();
				    }
				}
			    }

			    
			    //gen.writeEndObject();
		    }catch(PSTException e){
			e.printStackTrace();
			throw e;
		    }
			
		}
		//gen.writeEndArray();
	    }
	    //--Attachments

	 
	    
	 
	 
	    
	    jaxbMarshaller.marshal(r, System.out);
	}catch(Exception e){
	    e.printStackTrace();
	    return;
	}

    }


    // Derived from: http://www.baeldung.com/convert-input-stream-to-array-of-bytes
    byte[] inputStream2ByteArray(InputStream is) throws IOException{
	ByteArrayOutputStream buffer = new ByteArrayOutputStream();
	int nRead;
	byte[] data = new byte[1024];
	while ((nRead = is.read(data, 0, data.length)) != -1) {
	    buffer.write(data, 0, nRead);
	}
	
	buffer.flush();
	return buffer.toByteArray();
    }

}

