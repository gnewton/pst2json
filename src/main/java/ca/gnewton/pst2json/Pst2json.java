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

    public Pst2json(String filename) {
        try {
            PSTFile pstFile = new PSTFile(filename);
            System.out.println(pstFile.getMessageStore().getDisplayName());
	    JsonFactory jfactory = new JsonFactory();
	    JsonGenerator gen = jfactory.createJsonGenerator(System.out);
	    gen.useDefaultPrettyPrinter();
	    gen.writeStartObject();

            processFolder(pstFile.getRootFolder(), gen);
	    gen.writeEndObject();
	    gen.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }

    int depth = -1;
    public void processFolder(PSTFolder folder, JsonGenerator gen)
            throws PSTException, java.io.IOException
    {
        depth++;
        // the root folder doesn't have a display name
        if (depth > 0) {
            //printDepth();
            //System.out.println(folder.getDisplayName());
        }

        // go through the folders...
        if (folder.hasSubfolders()) {
            Vector<PSTFolder> childFolders = folder.getSubFolders();
            for (PSTFolder childFolder : childFolders) {
                processFolder(childFolder, gen);
            }
        }

        // and now the emails for this folder
        if (folder.getContentCount() > 0) {
            depth++;
            PSTMessage email = (PSTMessage)folder.getNextChild();
            while (email != null) {
                //printDepth();
                //System.out.println("Email: "+email.getSubject() + "|| " + email.getMessageDeliveryTime());
		try{
		    print(email, gen);
		}catch(PSTException  e){
		    throw new IOException();
		}
                email = (PSTMessage)folder.getNextChild();
            }
            depth--;
        }
        depth--;
    }

    public void printDepth() {
        for (int x = 0; x < depth-1; x++) {
            System.out.print(" | ");
        }
        System.out.print(" |- ");
    }

    public void print(PSTMessage email, JsonGenerator gen) throws PSTException {
	SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
	Date rec = email.getMessageDeliveryTime();
	String receivedTime="";
	if (rec != null){
		receivedTime  = dateFormat.format(rec);
	    }
	try{

	    gen.writeObjectFieldStart("message");
	    gen.writeStringField("subject", email.getSubject());
	    gen.writeStringField("FROM", email.getSentRepresentingEmailAddress());
	    gen.writeStringField("received", receivedTime);
	    gen.writeStringField("from_name", email.getSenderName());
	    gen.writeStringField("from", email.getSenderEmailAddress());

	    gen.writeArrayFieldStart("recipients");

	    try{
		int n = email.getNumberOfRecipients();
		
		for (int i=0; i<n; i++){
		    PSTRecipient recip = email.getRecipient(i);
		    //gen.writeString(recip.getEmailAddress());
		    gen.writeStartObject();
		    //gen.writeString(recip.getSmtpAddress());
		    gen.writeStringField("name", recip.getDisplayName());
		    gen.writeStringField("email", recip.getSmtpAddress());
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
			    gen.writeStringField("filename", att.getFilename());
			    gen.writeNumberField("size", att.getAttachSize());
			    String mime = att.getMimeTag();
			    gen.writeStringField("mime", mime);
			    gen.writeStringField("disposition", att.getAttachmentContentDisposition());
			    gen.writeNumberField("attach_type", att.getAttachMethod());

			    //if (att.getAttachMethod() == 5){
			    if (true || mime != null && mime.length() > 0){
			    java.io.InputStream is = null;
			    try{

				is = att.getFileInputStream();
				if (is != null){
					gen.writeFieldName("foobar");
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

	    gen.writeStringField("body", email.getBodyPrefix());
	    
	    gen.writeEndObject();
	    
	    

	}catch(IOException e){
		e.printStackTrace();
	}

    }
}
