package ca.gnewton.pst2json;

import com.fasterxml.jackson.core.*;
import com.pff.*;
import java.io.IOException;
import java.util.*;
import java.text.SimpleDateFormat;

public class Pst2json {
    public static void main(String[] args)
    {
        new Pst2json(args[0]);
    }

    public  Pst2json(String filename) {
        try {
            PSTFile pstFile = new PSTFile(filename);
            System.out.println(pstFile.getMessageStore().getDisplayName());
	    JsonFactory jfactory = new JsonFactory();
	    JsonGenerator gen = jfactory.createJsonGenerator(System.out);
	    gen.useDefaultPrettyPrinter();
	    gen.writeStartObject();

	    Stack<String> foldersPath = new Stack<String>();

            processFolder(pstFile.getRootFolder(), gen, foldersPath);
	    gen.writeEndObject();
	    gen.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    int depth = 0;
    public final void processFolder(PSTFolder folder, JsonGenerator gen, Stack<String>foldersPath)
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
                processFolder(childFolder, gen, foldersPath);
            }
        }

        // and now the emails for this folder
        if (folder.getContentCount() > 0) {
	    
            PSTMessage email = (PSTMessage)folder.getNextChild();
            while (email != null) {
                //printDepth();
                //System.out.println("Email: "+email.getSubject() + "|| " + email.getMessageDeliveryTime());
		try{
		    print(email, gen, foldersPath, depth);
		}catch(PSTException  e){
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

	    gen.writeObjectFieldStart("message");

	    gen.writeArrayFieldStart("folder");
	    Iterator<String> it = foldersPath.iterator();
	    while(it.hasNext()){
		String f = it.next();
		gen.writeString(f);
	    }
	    gen.writeEndArray();
	    gen.writeNumberField("folder_depth", depth);
	    
	    stringOut(gen,"subject", email.getSubject());
	    stringOut(gen,"received", receivedTime);
	    stringOut(gen,"from_name", email.getSenderName());
	    
	    stringOut(gen, "from", email.getSenderEmailAddress());




	    stringOut(gen, "message_id", email.getInternetMessageId());
	    stringOut(gen, "in_reply_to_id", email.getInReplyToId());
	    
	    
	    gen.writeArrayFieldStart("recipients");
	    try{
		int n = email.getNumberOfRecipients();
		
		for (int i=0; i<n; i++){
		    PSTRecipient recip = email.getRecipient(i);
		    //gen.writeString(recip.getEmailAddress());
		    gen.writeStartObject();
		    //gen.writeString(recip.getSmtpAddress());
		    stringOut(gen,"name", recip.getDisplayName());
		    stringOut(gen,"smtp", recip.getSmtpAddress());
		    stringOut(gen,"email", recip.getEmailAddress());
		    gen.writeEndObject();
		}
	    }catch(PSTException e){
		    e.printStackTrace();
	    }
    
	    gen.writeEndArray();

	    //++Attachments
	    int numberAttachments = email.getNumberOfAttachments();

	    if (numberAttachments>0){
		    gen.writeArrayFieldStart("attachments");
		    for(int i=0; i<numberAttachments; i++){
			try{
			    PSTAttachment att = email.getAttachment(i);
			    gen.writeStartObject();
			    stringOut(gen,"filename", att.getFilename());
			    gen.writeNumberField("size", att.getAttachSize());
			    String mime = att.getMimeTag();
			    stringOut(gen,"mime", mime);
			    stringOut(gen,"disposition", att.getAttachmentContentDisposition());
			    gen.writeNumberField("attach_type", att.getAttachMethod());
			    if (att.getAttachMethod() >= 5){
				stringOut(gen,"content6", att.toString());
			    }

			    //if (att.getAttachMethod() == 5){
			    if (true || mime != null && mime.length() > 0){
			    java.io.InputStream is = null;
			    try{

				is = att.getFileInputStream();
				if (is != null){
					gen.writeFieldName("content");
					gen.writeBinary(is, -1);
					//gen.writeEndObject();
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
				}
			    
			    gen.writeEndObject();
			}catch(PSTException e){
			    e.printStackTrace();
			    throw e;
			}
			
		    }
		    gen.writeEndArray();
		}
	    //--Attachments

	    stringOut(gen,"body", email.getBodyPrefix());
	    
	    gen.writeEndObject();
	    
	    

	}catch(IOException e){
		e.printStackTrace();
	}

    }

	public final void stringOut(JsonGenerator gen, String key, String value) throws IOException{
	    if (value != null && value.length()>0){
		gen.writeStringField(key, value);
	    }

	}
}

