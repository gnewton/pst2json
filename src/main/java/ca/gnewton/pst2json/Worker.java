package ca.gnewton.pst2json;

import com.pff.*;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.Stack;
import java.util.concurrent.locks.ReentrantLock;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

public class Worker{
    static final ReentrantLock lock = new ReentrantLock();
    
    final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    public Worker(){

    }

    Marshaller jaxbMarshaller;
    OutputStream out;
    PSTMessage email;
    Stack<String> foldersPath;
    int filesource_id;
    int depth;

    public Worker(final Marshaller jaxbMarshaller, final OutputStream out, final PSTMessage email, final Stack<String> foldersPath, final int filesource_id, final int depth){
        
        this.jaxbMarshaller= jaxbMarshaller;  
        this.out= out;             
        this.email= email;           
        this.foldersPath= foldersPath;     
        this.filesource_id= filesource_id;   
        this.depth= depth;           
    }
    
    public final void run()  {

        XmlRecord rec = null;
        try{
            rec = prepare();
        }catch(Throwable t){
            t.printStackTrace();
            //return;
        }
        if (rec == null){
                return;
            }

        try {
            lock.lock();  // block until condition holds
            jaxbMarshaller.marshal(rec, out);
        }catch(Throwable t){
            t.printStackTrace();
        } finally {
            lock.unlock();
        }
        
    }
    
    public final XmlRecord prepare() throws PSTException, IOException {
        XmlRecord r = null;
	Date rec = email.getMessageDeliveryTime();
	String receivedTime="";
	if (rec != null){
	    receivedTime  = dateFormat.format(rec);
	}

	    r = new XmlRecord();
            r.filesource_id = filesource_id;
            r.body = new XmlContent();
            //r.body.content = "test content";
            //r.body.newContent = Base64Encoding;
	    r.folderDepth = depth;

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
	    r.foldersPath = sb.toString();
	    
	    //r.setBody(email.getBody().replaceAll(" ",""));
	    if (! Config.noBase64Encode){
                r.body.data = Base64.getEncoder().encodeToString(email.getBody().getBytes());
                r.body.contentEncoding = "b64";
                
	    }else{
                r.body.data = email.getBody();
                r.body.contentEncoding = "";
	    }
	    r.subject = email.getSubject();
	    r.received = receivedTime;
	    r.from_name = email.getSenderName();
	    r.from= email.getSenderEmailAddress();
	    r.message_id = email.getInternetMessageId();
	    r.in_reply_to_id = email.getInReplyToId();

	    if (email.getConversationId() != null){
                if (Config.noBase64Encode){
                    r.transport_message_headers = email.getTransportMessageHeaders();
                }else{
                    r.conversation_id = Base64.getEncoder().encodeToString(email.getConversationId());
                    r.transport_message_headers = email.getTransportMessageHeaders();
                }
            }
	    r.importance = email.getImportance();
	    r.internet_article_number = email.getInternetArticleNumber();
         
	    r.num_recipients= email.getNumberOfRecipients();
	    r.num_attachments = email.getNumberOfAttachments();
	    r.priority = email.getPriority();
            PSTConversationIndex pci = email.getConversationIndex();
            if (pci != null){
                java.util.UUID uuid = pci.getGuid();
                if (uuid != null){
                    r.uuid = uuid.toString();
                }
            }
	    r.sensitivity= email.getSensitivity();
	    if (email.getReturnPath() != null && email.getReturnPath().length() > 0){
		r.return_path = email.getReturnPath();
	    }


	    // All booleans
	    r.cc_me = email.getMessageCcMe();
	    r.message_recip_me = email.getMessageRecipMe();
	    r.message_to_me = email.getMessageToMe();
	    r.response_requested = email.getResponseRequested();
	    r.attachments = email.hasAttachments();
	    r.forwarded = email.hasForwarded();
	    r.replied = email.hasReplied();
	    r.from_me = email.isFromMe();
	    r.read = email.isRead();
	    r.reply_requested = email.isReplyRequested();
	    r.resent = email.isResent();
	    r.submitted = email.isSubmitted();
	    r.unmodified = email.isUnmodified();
	    r.unsent = email.isUnsent();

            try{
                handleRecipients(r, email);
            }catch(PSTException e){
                e.printStackTrace();
            }catch(IOException e){
                e.printStackTrace();
            }
	    

	    //++Attachments
	    int numberAttachments = email.getNumberOfAttachments();

	    if (Config.includeAttachments && numberAttachments>0){
		//gen.writeArrayFieldStart("attachments");
		r.mattachments = new XmlAttachments();
		r.mattachments.attachments = new XmlAttachment[numberAttachments];

		XmlAttachment xat;
		for(int i=0; i<numberAttachments; i++){
		    try{
			xat = new XmlAttachment();
                        xat.content = new XmlContent();
                        xat.contentTextExtracted = new XmlContentExtracted();
			r.mattachments.attachments[i] = xat;
                        
			PSTAttachment att = email.getAttachment(i);
			xat.filename = att.getFilename();
			xat.size = att.getFilesize();
			xat.mime = att.getMimeTag();
			xat.attachment_content_disposition = att.getAttachmentContentDisposition();
			xat.attach_type = att.getAttachMethod();	
                        
			java.io.InputStream is = null;
			try{
			    is = att.getFileInputStream();
			    if (is != null){
				AttachmentUtils au = new AttachmentUtils(is);
                                au.convertToBase64();
                                xat.content.data = au.contentBase64;

                                xat.content.contentEncoding = XmlContent.Base64Encoding;
                                //xat.content.sha256Hex = au.contentSha256Hex;
                                if (Config.extractTextFromAttachments){
                                    xat.contentTextExtracted = new XmlContentExtracted();
                                    xat.contentTextExtracted.content = new XmlContent();
                                    if (Config.noBase64Encode){
                                        
                                        xat.contentTextExtracted.content.data = au.extractText(xat.filename, xat.mime);
                                        
                                        xat.contentTextExtracted.content.contentEncoding = XmlContent.NoEncoding;
                                    }else{
                                        xat.contentTextExtracted.content.contentEncoding = XmlContent.Base64Encoding;
                                        xat.contentTextExtracted.content.data = Base64.getEncoder().encodeToString(au.extractText(xat.filename, xat.mime).getBytes());                                }
                                    //xat.contentTextExtracted.content.data = au.extractText(xat.filename, xat.mime);
                                    
                                    if (xat.contentTextExtracted.content.data.length() == 0){
                                        xat.contentTextExtracted = null;
                                    }
                                    
                                    xat.meta = au.metadata;
                                }
                                
                                if (xat.mime == null||xat.mime==""){
                                    xat.mime = au.tikaMime;
                                }
			    }
                            
			}catch(IOException e){
			    e.printStackTrace();
			    //throw e;
			}
			catch( PSTException e){
			    e.printStackTrace();
                            System.err.println(r.internet_article_number);
			    //throw e;
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
			//throw e;
		    }
		}
		//gen.writeEndArray();
	    }
	    //--Attachments
               return r;
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
