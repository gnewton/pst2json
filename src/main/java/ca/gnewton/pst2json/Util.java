package ca.gnewton.pst2json;

import com.pff.PSTFile;
import com.pff.PSTException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;

public class Util{

    public static final PSTFile openPSTFile(String filename)throws FileNotFoundException,PSTException, IOException{
        System.err.println("Opening PST file: " + filename);
        return new PSTFile(filename);
        
    }
    final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

}
