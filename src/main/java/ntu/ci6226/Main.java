package ntu.ci6226;

import java.io.IOException;
import java.util.Iterator;

/**
 * Created by alexto on 2/3/16.
 */
public class Main {

    private static final String input = "dblp.xml";

    public static void main(String[] args) throws IOException {
        if (args.length < 1) {
            System.out.println("Usage: java Main [index]");
            System.exit(0);
        }
        Indexer indexer = new Indexer(args[0]);
        Parser p = new Parser(input, indexer);
        p.Parse();
        indexer.Close();
    }
}
