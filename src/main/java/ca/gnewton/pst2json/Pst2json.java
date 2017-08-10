package ca.gnewton.pst2json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import com.pff.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Pst2json {
    boolean jsonOut = false;
    
    public static void main(String[] args)
    {
        new Pst2json(args[0]);
    }

    public  Pst2json(String filename) {
        try {
            PSTFile pstFile = new PSTFile(filename);
            //System.err.println(pstFile.getMessageStore().getDisplayName());
	    Writer writer = null;
	    if (jsonOut){
		writer = new JsonWriter();
	    }else{
		writer = new XmlWriter();
	    }
	    Stack<String> foldersPath = new Stack<String>();
	    writer.process(pstFile.getRootFolder(), foldersPath);
	    writer.close();
        } catch (Exception err) {
            //err.printStackTrace();
	    //System.err.println("***");
        }
    }


	public final void stringOut(JsonGenerator gen, String key, String value) throws IOException{
	    if (value != null && value.length()>0){
		gen.writeStringField(key, value);
	    }

	}
}

