package ca.gnewton.pst2json;

import com.pff.*;
import java.util.Properties;
import java.util.Stack;

interface MessageWriter{
    public void init(Properties p) throws Exception;
    public void writeMessage(final PSTMessage email, final Stack<String> foldersPath, final int filesource_id) throws Exception;
    public void close() throws Exception;
}
