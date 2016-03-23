package ntu.ci6226;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.index.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.BytesRef;
import org.apache.lucene.misc.*;

import java.io.*;
import java.nio.file.Paths;

/**
 * Created by alexto on 2/3/16.
 */
public class IndexBuilder {

    private static final String input = "dblp.xml";
    private static final String newLine = System.getProperty("line.separator");
    public static void main(String[] args) throws IOException {
        indexCase1();
        indexCase2();
        indexCase3();
        indexCase4();
        indexCase5();
    }

    public static void indexCase1() throws IOException {

        //Case 1. Index with no stemming, no stop words, case sensitive.

        File file = new File("case1_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case1_report.txt"), "utf-8"));
        writer.write("Index without stemming, no stop words and case sensitive." + newLine);
        System.out.println("1. Index  without stemming, no stop words and case sensitive.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index1", new CaseSensitiveStandardAnalyzer(CharArraySet.EMPTY_SET));
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index1", writer);
        writer.close();
        System.out.println("\tReport written to case1_report.txt");
    }

    public static void indexCase2() throws IOException {

        //Case 2. Index with no stemming, no stop words, case insensitive.

        File file = new File("case2_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case2_report.txt"), "utf-8"));
        writer.write("Index without stemming, no stop words and case insensitive." + newLine);
        System.out.println("2. Index without stemming, no stop words and case insensitive.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index2", new StandardAnalyzer(CharArraySet.EMPTY_SET));
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index2", writer);

        writer.close();
        System.out.println("\tReport written to case2_report.txt");
    }

    public static void indexCase3() throws IOException {

        //Case 3. Index with no stemming, using stop words. When using stop words, we also lowercase all terms.

        File file = new File("case3_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case3_report.txt"), "utf-8"));
        writer.write("Index without stemming, using default set of stop words." + newLine);
        System.out.println("3. Index without stemming, using default set of stop words.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index3", new StandardAnalyzer());
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index3", writer);

        writer.close();
        System.out.println("\tReport written to case3_report.txt");
    }

    public static void indexCase4() throws IOException {

        //Case 4. Index with Porter stemmer, using stop words.

        File file = new File("case4_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case4_report.txt"), "utf-8"));
        writer.write("Index with Porter stemmer, using default set of stop words." + newLine);
        System.out.println("4. Index with Porter stemmer, using default set of stop words.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index4", new PorterStemmerStandardAnalyzer());
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        writer.write(newLine);

        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index4", writer);
        writer.close();
        System.out.println("\tReport written to case4_report.txt");
    }

    public static void indexCase5() throws IOException {

        //Case 5. Index with Porter stemmer, using stop words.

        File file = new File("case5_report.txt");
        if (file.exists())
            return;

        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("case5_report.txt"), "utf-8"));
        writer.write("Index with Porter stemmer, using default set of stop words." + newLine);
        System.out.println("5. Index with bigram.");

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Indexer indexer = new Indexer("Index5", new BiwordStandardAnalyzer());
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
        stopWatch.stop();

        writer.write("Elapsed time: " + stopWatch.toString() + newLine);
        writer.write(newLine);

        System.out.println("\tElapsed time: " + stopWatch.toString());
        writeTerms("Index5", writer);
        writer.close();
        System.out.println("\tReport written to case5_report.txt");
    }

    public static void writeTerms(String index, Writer writer) throws IOException{
        Directory dir = FSDirectory.open(Paths.get(index));
        IndexReader indexReader = DirectoryReader.open(dir);
        Terms terms = MultiFields.getTerms(indexReader, "title");
        TermsEnum iterator = terms.iterator();
        BytesRef bytesRef = null;
        int count = 0;
        writer.write("Terms in Title field: " + newLine);
        writer.write(newLine);
        while ((bytesRef = iterator.next()) != null) {
            writer.write(bytesRef.utf8ToString() + newLine);
            count++;
        }
        writer.write(newLine);
        writer.write("Total number of terms: " + count);
        System.out.println("\tTotal number of terms in Title field: " + count);

        // Top 10 terms with highest total term frequency
        HighFreqTerms.TotalTermFreqComparator cmp = new HighFreqTerms.TotalTermFreqComparator();
        TermStats[] highFreqTerms = null;

        try {
            highFreqTerms = HighFreqTerms.getHighFreqTerms(indexReader, 10, "title", cmp);
        }catch (Exception e){
        }

        for (TermStats ts : highFreqTerms) {
            writer.write(newLine);
            writer.write(ts.termtext.utf8ToString() + ": ");
            writer.write(newLine);
            writer.write("Document frequency: " + ts.docFreq );
            writer.write(newLine);
            writer.write("Total term frequency: " + ts.totalTermFreq);
            writer.write(newLine);
        }
    }
}
