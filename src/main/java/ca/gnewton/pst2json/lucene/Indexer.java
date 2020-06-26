package ca.gnewton.pst2json.lucene;

import com.pff.PSTMessage;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig.OpenMode;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

import org.apache.lucene.document.LongPoint;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;

import java.nio.file.Paths;

import java.io.IOException;

public class Indexer{

    Directory dir;
    Analyzer analyzer;
    IndexWriterConfig iwc;
    
    public void init(String luceneIndexPath) throws IOException{
        dir = FSDirectory.open(Paths.get(luceneIndexPath));
        analyzer = new StandardAnalyzer();
        iwc = new IndexWriterConfig(analyzer);
    }

    public void indexMsg(PSTMessage email){
        Document doc = new Document();

    }
}
