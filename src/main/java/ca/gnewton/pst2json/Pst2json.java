package ca.gnewton.pst2json;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonFactory;
import com.pff.*;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Pst2json {
    boolean jsonOut = true;
    
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
		//processFolder(pstFile.getRootFolder(), gen, foldersPath);
		
	    }
	    Stack<String> foldersPath = new Stack<String>();
	    writer.process(pstFile.getRootFolder(), foldersPath);
        } catch (Exception err) {
            err.printStackTrace();
        }
    }


	public final void stringOut(JsonGenerator gen, String key, String value) throws IOException{
	    if (value != null && value.length()>0){
		gen.writeStringField(key, value);
	    }

	}
}

