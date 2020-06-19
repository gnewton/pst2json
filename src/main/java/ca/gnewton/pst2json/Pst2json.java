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
	Getopt g = new Getopt("", argv, "+jzTBe");
	//
	int c;
	String arg;
	while ((c = g.getopt()) != -1)
	    {
		switch(c)
		    {
		    case 'j':
			Config.output = Outputs.JSON;
			break;

		    case 'B':
			Config.noBase64Encode = true;
			break;

		    case 'T':
			Config.extractTextFromAttachments = false;
			break;

                    case 'e':
			Config.includeAttachments = false;
			break;

		    case 'z':
			Config.gzipOut = true;
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
        System.err.println("filenames");
        System.err.println(filenames);
        Writer writer = null;
        OutputStream out;

        try{
            out = makeOutputStream();
        } catch (Exception err) {
            err.printStackTrace();
            return;
        }


        
        switch(Config.output){
        case XML:
            try{
                writer = new XmlWriter(out, filenames);
            }catch (Exception err) {
                err.printStackTrace();
                return;
            }
            break;
        case JSON:
            try{
                writer = new JsonWriter(out, Config.extractTextFromAttachments);
            }
            catch (Exception err) {
                err.printStackTrace();
                return;
            }
            break;
        }
        
        
        int i;
        for(i=0; i<filenames.length; i++){
            String filename = filenames[i];
            System.err.println("*****************************");
            System.err.println(filenames);
            System.err.println(filename);
            System.err.println(filenames.length);
            System.err.flush();

            //if (true){
            //return;
            //}
            
            try {
                writer.process(filename, i);
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
	if (Config.gzipOut){
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

