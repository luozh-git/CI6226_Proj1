package ntu.ci6226;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.FSDirectory;

import java.nio.file.*;

import java.io.IOException;

/**
 * Created by Luo Zhuohui - on 6/3/2016.
 */
public class MySearcher {

    private IndexSearcher searcher;

    // indexDir is the directory of the index built
    public MySearcher(String indexDir) throws IOException{
        Path path = FileSystems.getDefault().getPath(indexDir);
        IndexReader reader = DirectoryReader.open(FSDirectory.open(path));
        this.searcher = new IndexSearcher(reader);
    }

    private void printSearchResults(Query query) throws IOException{

        // TopDocs hits are the documents returned
        TopDocs hits = this.searcher.search(query, 10);

        System.out.println("totalHits: " + hits.totalHits);
        System.out.println("Documents returned:");

        for(ScoreDoc scoreDoc: hits.scoreDocs){
            Document doc = searcher.doc(scoreDoc.doc);
            float documentScore = scoreDoc.score;
            //System.out.println(doc);

            System.out.println(doc.get("key"));
            System.out.println(doc.get("venue"));
            System.out.println(doc.get("year"));
            //System.out.println(doc.get("key"));
            System.out.println(doc.get("title"));
            System.out.println("Document Score : " + documentScore);
            System.out.println("-----------------------------");
        }
    }

    //Search a specific field
    public void searchOneField(String field, String queryString) throws IOException, ParseException{

        QueryParser parser = new QueryParser(field, new StandardAnalyzer());
        Query query = parser.parse(queryString);
        printSearchResults(query);
    }

    // Search all fields
    public void searchAllFields(String q) throws IOException, ParseException{

        // Search for a term in multiple fields.
        // MultiFieldQueryParser
        String[] fields = {"title", "venue", "year"};
        QueryParser parser = new MultiFieldQueryParser(fields, new StandardAnalyzer());
        Query query = parser.parse(q);
        printSearchResults(query);
    }

    /*
    QueryParser parser = new QueryParser("title", new StandardAnalyzer());
    String q = "chinese characters";
     Query query = parser.parse(q);
    */
    public void searchPhraseInTitle(String[] terms) throws IOException, ParseException{

        PhraseQuery.Builder builder = new PhraseQuery.Builder();

        for(int i=0; i<terms.length; i++){
            builder.add(new Term("title", terms[i]), i);
        }

        PhraseQuery query = builder.build();
        printSearchResults(query);
    }

    public void searchYearRange(Integer start, Integer end) throws IOException, ParseException {

        // Search for year.
        Query query = NumericRangeQuery.newIntRange("year", start, end, true, true);
        printSearchResults(query);
    }

    public void searchVenueAndYear(String venueString, Integer start, Integer end) throws IOException, ParseException{

        QueryParser parser = new QueryParser("venue", new StandardAnalyzer());
        Query venue_query = parser.parse(venueString);

        Query year_query = NumericRangeQuery.newIntRange("year", start, end, true, true);

        BooleanQuery.Builder builder = new BooleanQuery.Builder();

        builder.add(venue_query, BooleanClause.Occur.MUST);
        builder.add(year_query, BooleanClause.Occur.MUST);

        BooleanQuery query = builder.build();
        printSearchResults(query);
    }

    // Wrapper methods of the search cases required in the assignments
    public static void main(String[] args) throws IOException, ParseException {

        MySearcher mySearcher = new MySearcher("index_allYES");

        //String queryString = "2010";
        //String queryString = "chinese characters";
        //String[] queryStrings = {"chinese", "characters"};

        //String[] queryStrings = {"fuzzy", "web", "mining"};
        //mySearcher.searchPhraseInTitle(queryStrings);
        //mySearcher.searchYearRange(2010, 2012);

        // Search SIGIR 2011, a.k.a both venue and year.
        mySearcher.searchVenueAndYear("SIGKDD", 2011, 2011);

        /*
        // Boolean query examples
        QueryParser parser = new QueryParser("venue", new StandardAnalyzer());
        //String q = "SIGIR OR SIGKDD";
        String q = "venue:SIGKDD AND title:data mining";
        Query query = parser.parse(q);
        */

        /*
        // Wildcard examples
        //String q = "conf/sigir/WangBC12";
        String q = "wangbc12*";
        QueryParser parser = new QueryParser("key", new StandardAnalyzer());
        Query query = parser.parse(q);
        */
    }
}
