package ca.gnewton.pst2json;

import com.pff.*;
import java.io.IOException;
import java.util.*;
import java.io.OutputStream;

interface Writer{

    //public void process(PSTFolder folder,Stack<String>foldersPath, int filesource_id) throws Exception;
    //public void processM(int filesource_id) throws Exception;
    public void process(String filename, int filesource_id) throws Exception;
    public void close() throws Exception;
}
