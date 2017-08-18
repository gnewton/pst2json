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

public class XmlWriter implements Writer{

    private Marshaller jaxbMarshaller = null;

    public XmlWriter() throws Exception{


	Stack<String> foldersPath = new Stack<String>();

	JAXBContext jaxbContext = JAXBContext.newInstance(XmlRecord.class);
	jaxbMarshaller = jaxbContext.createMarshaller();
	jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	jaxbMarshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);

	System.out.println("<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\" ?>");
	System.out.println("<messages>");


	JAXBContext context = JAXBContext.newInstance(XmlMeta.class);
	Marshaller marshaller = context.createMarshaller();
	marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
	marshaller.setProperty(Marshaller.JAXB_FRAGMENT, true);
	XmlMeta m = new XmlMeta("filaname", "foobar");
	marshaller.marshal(m, System.out);

    }
    public void close() throws Exception{
	System.out.println("</messages>");
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
		    print(email, foldersPath, depth);
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

    public final void print(PSTMessage email, Stack<String> foldersPath, int depth) throws PSTException {

	Date rec = email.getMessageDeliveryTime();
	String receivedTime="";
	if (rec != null){
	    receivedTime  = dateFormat.format(rec);
	}
	try{
	    XmlRecord r = new XmlRecord();
	    r.setFolderDepth(depth);

	    StringBuilder sb = new StringBuilder();
	    // Write folder hierarchy as list of strings
	    //gen.writeArrayFieldStart("folder");
	    Iterator<String> it = foldersPath.iterator();
	    
	    while(it.hasNext()){
		if (sb.length() > 0){
		    sb.append("/");
		}
		sb.append(it.next().replaceAll("/", "_"));
	    }
	    r.setFoldersPath(sb.toString());

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

	    handleRecipients(r, email);
	    

	    //++Attachments
	    int numberAttachments = email.getNumberOfAttachments();

	    if (numberAttachments>0){
		//gen.writeArrayFieldStart("attachments");
		r.mattachments = new XmlAttachments();
		r.mattachments.attachments = new XmlAttachment[numberAttachments];

		XmlAttachment xat;
		for(int i=0; i<numberAttachments; i++){
		    try{
			xat = new XmlAttachment();
			r.mattachments.attachments[i] = xat;

			PSTAttachment att = email.getAttachment(i);
			xat.setFilename(att.getFilename());
			xat.setSize(att.getAttachSize());
			xat.setMime(att.getMimeTag());
			xat.setAttachment_content_disposition(att.getAttachmentContentDisposition());
			xat.setAttach_type(att.getAttachMethod());	
		
			java.io.InputStream is = null;
			try{
			    is = att.getFileInputStream();
			    if (is != null){
				AttachmentUtils au = new AttachmentUtils();
				au.convertToBase64(is);
				xat.setContent(au.contentBase64);
				xat.setSha1Base64(au.contentSha1Base64);
				xat.setContentTextExtracted(au.extractText(att.getFilename(), att.getMimeTag()));
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

    void handleRecipients(XmlRecord r, PSTMessage email) throws PSTException, IOException{
	    int n = email.getNumberOfRecipients();
	    if (n > 0){
		r.mrecipients = new XmlRecipients();
		r.mrecipients.recipients = new XmlRecipient[n];
		for (int i=0; i<n; i++){
		    XmlRecipient xrecip = new XmlRecipient();
		    r.mrecipients.recipients[i] = xrecip;
 		    
		    PSTRecipient recip = email.getRecipient(i);
		    xrecip.setName(recip.getDisplayName());
		    xrecip.setEmail(recip.getEmailAddress());
		    xrecip.setSmtp(recip.getSmtpAddress());
		    int mapi = recip.getRecipientFlags();
		    switch (mapi){
		    case com.pff.PSTRecipient.MAPI_BCC:
			xrecip.setMapi("BCC");
 			break;
		    case com.pff.PSTRecipient.MAPI_CC:
			xrecip.setMapi("CC");
			break;
		    case com.pff.PSTRecipient.MAPI_TO:
			xrecip.setMapi("TO");
			break;
			    
		    }
		}
	    }
    }
    


}

