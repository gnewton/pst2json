package ca.gnewton.pst2json;

import com.pff.*;
import java.io.IOException;
import java.util.*;
import java.io.OutputStream;

interface Writer{

    public void process(PSTFolder folder,Stack<String>foldersPath) throws Exception;
    public void close() throws Exception;
}
