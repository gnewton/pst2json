package ca.gnewton.pst2json;

import com.pff.PSTFile;
import com.pff.PSTException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import javax.swing.JEditorPane;
import java.io.Reader;
import javax.swing.text.EditorKit;
import java.io.StringWriter;
import javax.swing.text.BadLocationException;

public class Util{

    public static final PSTFile openPSTFile(String filename)throws FileNotFoundException,PSTException, IOException{
        System.err.println("Opening PST file: " + filename);
        return new PSTFile(filename);
        
    }
    final static SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");


    public static final String tenNumber(int n){
        StringBuilder sb = new StringBuilder();
        if (n<10){
            sb.append("0");
        }
        sb.append(String.valueOf(n));
        return sb.toString();
    }


    // From: https://www.codeproject.com/Tips/136483/Java-How-to-convert-RTF-into-HTML
    public static String rtfToHtml(Reader rtf) throws IOException {
    JEditorPane p = new JEditorPane();
    p.setContentType("text/rtf");
    EditorKit kitRtf = p.getEditorKitForContentType("text/rtf");
    try {
        kitRtf.read(rtf, p.getDocument(), 0);
        kitRtf = null;
        EditorKit kitHtml = p.getEditorKitForContentType("text/html");
        java.io.Writer writer = new StringWriter();
        kitHtml.write(writer, p.getDocument(), 0, p.getDocument().getLength());
        return writer.toString();
    } catch (BadLocationException e) {
        e.printStackTrace();
    }
    return null;
}

    public static final String cleanId(String id){
        id = id.replaceAll("@", "_at_");
        id = id.replaceAll("<", "");
        return id.replaceAll(">", "");
    }
}
