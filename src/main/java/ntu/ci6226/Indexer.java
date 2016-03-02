package ntu.ci6226;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.IntField;
import org.apache.lucene.document.StringField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;

/**
 * Created by alexto on 2/3/16.
 */
public class Indexer {

    private String indexPath;
    private Analyzer analyzer;
    private IndexWriterConfig indexWriterConfig;
    private IndexWriter indexWriter;

    public Indexer(String indexPath) throws IOException {
        this.analyzer = new StandardAnalyzer();
        indexWriterConfig = new IndexWriterConfig(analyzer);
        indexWriterConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE);
        Directory dir = FSDirectory.open(Paths.get(indexPath));
        indexWriter = new IndexWriter(dir, indexWriterConfig);
    }

    public void Index(Publication publication) throws IOException {
        if (publication.getType() != "article" && publication.getType() != "inproceedings")
        {
            return;
        }
        Document doc = new Document();
        doc.add(new StringField("key", publication.getKey(), Field.Store.YES));
        doc.add(new StringField("title", publication.getTitle(), Field.Store.NO));
        doc.add(new IntField("year", publication.getYear(), Field.Store.NO));
        doc.add(new StringField("venue", publication.getVenue(), Field.Store.NO));
        doc.add(new StringField("type", publication.getType(), Field.Store.NO));
        Person[] authors = publication.getAuthors();
        for (Person author : authors) {
            doc.add(new StringField("author", author.getName(), Field.Store.NO));
        }
        indexWriter.addDocument(doc);
    }

    public void Close() throws IOException {
        if (indexWriter != null && indexWriter.isOpen())
            indexWriter.close();
    }
}
