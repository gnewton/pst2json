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
    boolean gzipOut = false;
    
    public final static void main(String[] args)
    {
	Pst2json pj = new Pst2json();
	String []arg = pj.handleOpts(args);
	if (arg.length != 1){
	    System.err.println("Missing PST filename");
	    System.exit(1);
	}

	if (!exists(arg[0])){
	    System.err.println("Unable to read file: " + arg[0]);
	    System.exit(1);
	}
	pj.run(arg[0]);

    }

    public final static boolean exists(String filename){
	File f = new File(filename);
	return f.isFile() && f.canRead();
    }
    
    public final String[] handleOpts(String[] argv){
	Getopt g = new Getopt("", argv, "+jz");
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
    
    public final void run(String filename) {
        try {
            PSTFile pstFile = new PSTFile(filename);
	    Writer writer = null;
	    OutputStream out = makeOutputStream();
	    switch(output){
	    case XML:
		writer = new XmlWriter(out);
		break;
	    case JSON:
		writer = new JsonWriter(out);
		break;
	    }


	    
	    Stack<String> foldersPath = new Stack<String>();
	    writer.process(pstFile.getRootFolder(), foldersPath);
	    writer.close();
        } catch (Exception err) {
            //err.printStackTrace();
	    //System.err.println("***");
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

