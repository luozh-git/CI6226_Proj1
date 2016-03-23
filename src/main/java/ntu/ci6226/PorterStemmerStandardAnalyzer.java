package ntu.ci6226;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import java.io.IOException;
import java.io.Reader;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.core.LowerCaseFilter;
import org.apache.lucene.analysis.core.StopAnalyzer;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.en.PorterStemFilter;
import org.apache.lucene.analysis.standard.StandardFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.apache.lucene.analysis.standard.std40.StandardTokenizer40;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.StopwordAnalyzerBase;
import org.apache.lucene.util.Version;

public final class PorterStemmerStandardAnalyzer extends StopwordAnalyzerBase {
    public static final int DEFAULT_MAX_TOKEN_LENGTH = 255;
    private int maxTokenLength;
    public static final CharArraySet STOP_WORDS_SET;

    public PorterStemmerStandardAnalyzer(CharArraySet stopWords) {
        super(stopWords);
        this.maxTokenLength = 255;
    }

    public PorterStemmerStandardAnalyzer() {
        this(STOP_WORDS_SET);
    }

    public PorterStemmerStandardAnalyzer(Reader stopwords) throws IOException {
        this(loadStopwordSet(stopwords));
    }

    public void setMaxTokenLength(int length) {
        this.maxTokenLength = length;
    }

    public int getMaxTokenLength() {
        return this.maxTokenLength;
    }

    protected TokenStreamComponents createComponents(String fieldName) {
        final Object src;
        if(this.getVersion().onOrAfter(Version.LUCENE_4_7_0)) {
            StandardTokenizer tok = new StandardTokenizer();
            tok.setMaxTokenLength(this.maxTokenLength);
            src = tok;
        } else {
            StandardTokenizer40 tok1 = new StandardTokenizer40();
            tok1.setMaxTokenLength(this.maxTokenLength);
            src = tok1;
        }

        StandardFilter tok2 = new StandardFilter((TokenStream)src);
        LowerCaseFilter tok3 = new LowerCaseFilter(tok2);
        StopFilter tok4 = new StopFilter(tok3, this.stopwords);
        final PorterStemFilter tok5 = new PorterStemFilter(tok4);
        return new TokenStreamComponents((Tokenizer)src, tok5) {
            protected void setReader(Reader reader) {
                int m = PorterStemmerStandardAnalyzer.this.maxTokenLength;
                if(src instanceof StandardTokenizer) {
                    ((StandardTokenizer)src).setMaxTokenLength(m);
                } else {
                    ((StandardTokenizer40)src).setMaxTokenLength(m);
                }

                super.setReader(reader);
            }
        };
    }

    static {
        STOP_WORDS_SET = StopAnalyzer.ENGLISH_STOP_WORDS_SET;
    }
}
