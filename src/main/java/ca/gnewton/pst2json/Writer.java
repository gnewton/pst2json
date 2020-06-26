package ca.gnewton.pst2json;

import com.pff.*;
import java.io.IOException;
import java.util.Properties;
import java.util.Stack;
import java.io.OutputStream;

interface Writer{

    //public void process(PSTFolder folder,Stack<String>foldersPath, int filesource_id) throws Exception;
    //public void processM(int filesource_id) throws Exception;
    //public void process(String filename, int filesource_id) throws Exception;
    public void init(Properties p) throws Exception;
    public void processMessage(PSTMessage email, Stack<String> foldersPath, final int filesource_id, int depth) throws PSTException;
    public void close() throws Exception;
}
