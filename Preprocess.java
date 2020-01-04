package rankingmrf;

import org.tartarus.snowball.ext.PorterStemmer;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.CharArraySet;
import org.apache.lucene.analysis.StopFilter;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import ReadWrite.*;

public class Preprocess {
	//public static final CharArraySet ENGLISH_STOP_WORDS_SET;
	static List<String> stop_words=new ArrayList<String>();
	
	public static ArrayList<String> tokenizeStringRemovingStop(String str,String stopword) throws IOException{
		ArrayList<String> result = new ArrayList<String>();
        Analyzer analyzer = new StandardAnalyzer();
        StringReader reader = new StringReader(str);             
        stop_words=ReadWrite.readLine(stopword);
       // System.out.println(stop_words);
        
        try {
            TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));
            stream.reset();
            final CharArraySet stopSet = new CharArraySet(stop_words, false);
            stream =new StopFilter(stream, stopSet);
            
            while (stream.incrementToken()) {
              result.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
          } catch (IOException e) {
            // not thrown b/c we're using a string reader...
            throw new RuntimeException(e);
          }
      
      
        
        System.out.println(result);
        
        
        return result;
	}
	
	public static ArrayList<String> tokenizeString(String str) throws IOException{
		ArrayList<String> result = new ArrayList<String>();
        Analyzer analyzer = new StandardAnalyzer();
        StringReader reader = new StringReader(str);             
       
       // System.out.println(stop_words);
        
        try {
            TokenStream stream  = analyzer.tokenStream(null, new StringReader(str));
            stream.reset();
            final CharArraySet stopSet = new CharArraySet(stop_words, false);
            stream =new StopFilter(stream, stopSet);
            
            while (stream.incrementToken()) {
              result.add(stream.getAttribute(CharTermAttribute.class).toString());
            }
          } catch (IOException e) {
            // not thrown b/c we're using a string reader...
            throw new RuntimeException(e);
          }
      
      
        
        System.out.println(result);
        
        
        return result;
	}
	
	
	
	public static void main(String[] args) throws IOException {
		//tokenizeStringRemovingStop("What is the rain? How do you leave? It is been long we met. I am a girl");
		tokenizeString("What is the rain? How do you leave? It is been long we met. I am a girl");
	}

}
