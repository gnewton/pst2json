package ca.gnewton.pst2json;

import java.io.StringReader;
import com.pff.*;
import java.io.PrintWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.text.Normalizer;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Properties;
import java.util.Stack;
import java.util.Vector;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FilesystemWriter implements Writer{
    final String ROOT = "./mail";
    final String CHRONO_ROOT = ROOT + File.separator + "chronological";
    final String ID_ROOT = ROOT + File.separator + "ById";

    private Marshaller jaxbMarshaller = null;
    OutputStream out = null;
    File root; 
    public void init(Properties p) throws Exception{
        root = new File(ROOT);
        root.mkdirs();
        root = new File(ID_ROOT);
        root.mkdirs();
    }

    public FilesystemWriter(OutputStream out, String []filenames) throws Exception{

    }

    public void close() throws Exception{

    }

    

    public final void processMessage(PSTMessage email, Stack<String> foldersPath, final int filesource_id, int depth) throws PSTException {
        // Thu May 09 14:42:58 EDT 2019
        //E MMM dd HH:mm:ss ZZZ yyyy 
        System.err.println("\n************************");
        System.err.println(email.getInternetArticleNumber());
        Date deliveryTime = email.getMessageDeliveryTime();
        System.err.println(email.getMessageDeliveryTime());


        System.err.println("FOLDER: " + email.getDisplayName());
        System.err.println("FOLDER: " + foldersPath.toString());
        Calendar cal = Calendar.getInstance();
        cal.setTime(deliveryTime);

        String msgDir;
        try{
            msgDir = makeChronological(email, cal, foldersPath.lastElement());
        }catch(Throwable t){
            t.printStackTrace();
            return;
        }

        try{
            makeId(msgDir, email.getInternetMessageId());
        }catch(IOException e){
            e.printStackTrace();
        }
        
        System.err.println(email.getSenderName());
        System.err.println(cleanString(email.getSenderName()));
        System.err.println(email.getSenderEmailAddress());
        System.err.println(cleanString(email.getSenderEmailAddress()));
    }

    public final String cleanString(String s){
        s =  Normalizer
            .normalize(s, Normalizer.Form.NFD)
            .replaceAll("[^\\p{ASCII}]", "");
        s = s.replaceAll("[()]", "");
        s = s.replaceAll("[@]", "_at_");
        s =s.replaceAll("[^a-zA-Z0-9@]", "_");
        return s;
    }

    public final String makeChronological(PSTMessage email, Calendar cal, String folder) throws IOException{
        String sender = cleanString(email.getSenderName());
        System.err.println(email.getMessageClass());
        System.err.println("in_reply_to_id" + email.getInReplyToId());
        System.err.println("getInternetMessageId=" + email.getInternetMessageId());
        if (sender.length() == 0){
            System.err.println("$$$");
            System.err.println("senderName= " + email.getSenderName());
            System.err.println("fromName= " + email.getSenderEmailAddress());
            System.err.println("internet_article_number= " + email.getInternetArticleNumber());
            System.err.println(email);
            sender = "outbox";
        }
        String msgDir = CHRONO_ROOT + File.separator + folder + File.separator + String.valueOf(cal.get(Calendar.YEAR)) + File.separator + String.valueOf(cal.get(Calendar.MONTH))+ File.separator + String.valueOf(cal.get(Calendar.DAY_OF_MONTH))
            + File.separator + sender
            + File.separator
            + Util.tenNumber(cal.get(Calendar.HOUR_OF_DAY))
            + "."
            + Util.tenNumber(cal.get(Calendar.MINUTE))
            + "."
            + Util.tenNumber(cal.get(Calendar.SECOND))
            //+  String.valueOf(email.getInternetArticleNumber())
            + "__" + cleanString(email.getSubject())  + "___" + email.getMessageClass() + "---" + email.getClass().getSimpleName()
            + "__numAttach_" + String.valueOf(email.getNumberOfAttachments());
        System.err.println(msgDir);

        makePath(msgDir);

        String msgPathAndFilename = writeChronoMessage(msgDir, email, cal);

        writeAttachments(msgDir, email);
        return msgDir;
    }

    
    public final String writeChronoMessage(String pathString, PSTMessage email, Calendar cal) throws IOException{
        String msgPath = pathString + File.separator + "index.html";
        PrintWriter writer = new PrintWriter(msgPath, "UTF-8");
        String body = email.getBody().replaceAll("\n", "\n<br>");
        
        if (body.length() == 0){
            body = email.getBodyHTML();
            if (body.length() == 0){
                try{
                    body = Util.rtfToHtml(new StringReader(email.getRTFBody()));
            }catch(Throwable t){
                t.printStackTrace();
                body = "Error: " + t.toString();
                }
            }
        }
        if (body.length() == 0){
                body = email.getSubject();
            }
        writer.println(body);
        writer.println("<hr>");
        if (email.getInReplyToId().length() > 0){
            writer.println("<br><a href=\"../../../../../../../../"+ ID_ROOT + "/" + Util.cleanId(email.getInReplyToId()) + "\">in_reply_to_id</a>");
        }
        writer.println("<br>message_id: " +  email.getInternetMessageId());
            
        writer.close();
        return msgPath;
    }
    
    public final void makePath(String s){
        Path path = Paths.get(s);
        //if directory exists?
        if (!Files.exists(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                //fail to create directory
                e.printStackTrace();
            }
        }

    }

    public static final String ATTACH_DIR = "attachments";
    public void writeAttachments(String msgDir, PSTMessage email){
	    int numberAttachments = email.getNumberOfAttachments();
            if (numberAttachments>0){
                String attachPath = msgDir + File.separator + ATTACH_DIR;
                File attachRoot = new File(attachPath);
                attachRoot.mkdirs();
		for(int i=0; i<numberAttachments; i++){
                    PSTAttachment att;
                    try{
			att = email.getAttachment(i);
                    }catch(IOException e){
                        e.printStackTrace();
                        continue;
                    }catch(com.pff.PSTException e){
                        e.printStackTrace();
                        continue;
                    }

                    OutputStream outputStream;
                    try{
                        outputStream = new FileOutputStream(attachPath + File.separator + att.getFilename());
                    }catch(java.io.FileNotFoundException e){
                        e.printStackTrace();
                        continue;
                    }
			    java.io.InputStream is = null;
			    try{
				is = att.getFileInputStream();
				if (is != null){
                                    int byteRead;
                                    
                                    while ((byteRead = is.read()) != -1) {
                                        outputStream.write(byteRead);
                                    }
                                }
			    }catch(IOException e){
				e.printStackTrace();
				continue;
			    }
			    catch( PSTException e){
				e.printStackTrace();
                                continue;
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
                                if (outputStream != null){
				    try{
					outputStream.close();
				    }catch(IOException e){
					e.printStackTrace();
				    }
				}
			    }
			}


            }
    }

    public void makeId(String msgDir, String msgId) throws IOException{
        if (msgId.length() == 0){
                return;
            }
        msgId = Util.cleanId(msgId);
        Path target = Paths.get("../..",msgDir);
        Path link = Paths.get(ID_ROOT + File.separator + msgId);
        if (Files.exists(link)) {
            Files.delete(link);
        }
        Files.createSymbolicLink(link, target);
    }
}
