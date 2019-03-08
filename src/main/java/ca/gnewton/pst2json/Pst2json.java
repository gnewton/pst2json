package ca.gnewton.pst2json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import com.pff.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import gnu.getopt.Getopt;
import java.io.OutputStream;
import java.util.zip.GZIPOutputStream;

public class Pst2json {
    Outputs output = Outputs.XML;
    boolean base64Body = true;
    boolean gzipOut = false;
    boolean extractTextFromAttachments = false;
    
    public final static void main(String[] args)
    {
	Pst2json pj = new Pst2json();
	String []arg = pj.handleOpts(args);
	if (arg.length < 1){
	    System.err.println("Missing PST filename");
	    System.exit(1);
	}

        int i;
        for (i=0; i<arg.length; i++){
            if (!exists(arg[i])){
                System.err.println("Unable to read file: " + arg[i]);
                System.exit(1);
            }
        }
	pj.run(arg);

    }

    public final static boolean exists(String filename){
	File f = new File(filename);
	return f.isFile() && f.canRead();
    }
    
    public final String[] handleOpts(String[] argv){
	Getopt g = new Getopt("", argv, "+jztB");
	//
	int c;
	String arg;
	while ((c = g.getopt()) != -1)
	    {
		switch(c)
		    {
		    case 'j':
			output = Outputs.JSON;
			break;

		    case 'B':
			base64Body = false;
			break;

		    case 't':
			extractTextFromAttachments = true;
			break;

		    case 'z':
			gzipOut = true;
			break;
		    default:
			System.out.print("getopt() returned " + c + "\n");
		    }
	    }
	return Arrays.copyOfRange(argv, g.getOptind(), argv.length);

    }
    
    public Pst2json() {

    }
    
    public final void run(String []filenames) {
        Writer writer = null;
        OutputStream out;

        try{
            out = makeOutputStream();
        } catch (Exception err) {
            err.printStackTrace();
            return;
        }
        
        int i;
        for(i=0; i<filenames.length; i++){
            String filename = filenames[i];
            
            try {
                System.err.println("Opening PST file: " + filename);
                PSTFile pstFile = new PSTFile(filename);

                switch(output){
                case XML:
                    writer = new XmlWriter(out, extractTextFromAttachments, base64Body);
                    break;
                case JSON:
                    writer = new JsonWriter(out, extractTextFromAttachments);
                    break;
                }
                
                Stack<String> foldersPath = new Stack<String>();
                writer.process(pstFile.getRootFolder(), foldersPath);
                
            } catch (Exception err) {
                err.printStackTrace();
                return;
            }

        }
        try{
            writer.close();
        } catch (Exception err) {
            err.printStackTrace();
        }
    }
    public OutputStream makeOutputStream() throws IOException{
	if (gzipOut){
	    GZIPOutputStream go = new GZIPOutputStream(System.out);
	    return go;
	}
	return System.out;
    }
    

    public final void stringOut(JsonGenerator gen, String key, String value) throws IOException{
	if (value != null && value.length()>0){
	    gen.writeStringField(key, value);
	}
    }
}

