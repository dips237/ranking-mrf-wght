package rankingmrf;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import lemurproject.indri.ParsedDocument;
import lemurproject.indri.QueryEnvironment;
import lemurproject.indri.ScoredExtentResult;

public class IndexStat {
	public static float idf_t;
	public static long no_total_doc;
	static long no_total_term;
	static long no_unique_term;

	public static class doc_freq{
		long did;
		long freq;
	}
	
	public static ArrayList<int[]> exprFreq;
	
	public static void printScoredExtentResult(ScoredExtentResult[] expl) {
		for(int i=0;i<expl.length;i++)
		{
			System.out.println(expl[i].document);
		}
		
	}
	
	
	public static void getTfIdf(String index_path, String index_name, String my_query) throws Exception {		
		exprFreq=new ArrayList<int[]>();
		QueryEnvironment qev = new QueryEnvironment();
		qev.addIndex(index_path+index_name);		
		no_total_doc=qev.documentCount();
		ScoredExtentResult[] expl = qev.expressionList(my_query);//"#uw5(no vol)");
		/** to print the expl or list of number of documents **/
		/* 
	    System.out.println("......................................");
		printScoredExtentResult(expl);
		*/
	   
	    long startTime = System.nanoTime();
	 	
		/** count the frequency of expression of document (occurance of documents in the list)**/
		int doc_itr=0;
		int prev_docid=0;
		//System.out.println(expl.length);
	    for (int i=0;i<expl.length;i++) { 
	    	int curr_docid=expl[i].document;
	    	//System.out.println(key_count+" "+curr_docid);
	    	
	    	if(curr_docid==prev_docid)
	    	{	    		    		
	    		int tmp[]= exprFreq.get(doc_itr-1);
	    		tmp[1]+=1;//{curr_docid,1};
	    		exprFreq.set(doc_itr-1,tmp);
	    	}	    	
    		else
    		{
    			int tmp[]= {curr_docid,1};
    			exprFreq.add(tmp);prev_docid=curr_docid; doc_itr+=1;
    		}
	    	  
	    } 
	    long endTime = System.nanoTime();
	    if(exprFreq.size()==0)
	    	System.out.println(my_query+"  "+exprFreq.size());
	    //System.out.println(my_query+"-------- my code --------"+ (float)(endTime-startTime)/100000+"  "+exprFreq.size());
	
		/*IDF: Inverse Document Frequency, which measures how important a term is.
		 * While computing TF, all terms are considered equally important.
		 * However it is known that certain terms, such as "is", "of", and "that",
		 *  may appear a lot of times but have little importance.
		 *  Thus we need to weigh down the frequent terms while scale up the rare ones,
		 *   by computing the following:
		 IDF(t) = log_e(Total number of documents / Number of documents with term t in it).
		*/
		//System.out.println(freqDoc.size());
	   // System.out.println(no_total_doc);
	   // System.out.println(exprFreq.size());
		if(exprFreq.size()==0)
			idf_t=0;
		else
		    idf_t= (float) Math.log((float)no_total_doc/(float)exprFreq.size());
		//System.out.println(idf_t);
		qev.close();
		qev.delete();
		
		
	}
	
	
	public void getStat(String index_path, String index_name) throws Exception {
		QueryEnvironment qev = new QueryEnvironment();
		qev.addIndex(index_path+index_name);
		no_total_doc= qev.documentCount();
		no_unique_term= qev.termCountUnique();
		no_total_term=qev.termCount();
		qev.close();
	}
	
	public static void main(String[] args) throws Exception {
		String index_path="/home/dips/my-inv-index/";
		String index_name="gov2-porter-stop-2";//clueweb09B-porter";//"trec-index";
		String my_query ="u.s";//"#uw2(no vol)";//"#uw2(no vol)";//"vol";
		IndexStat ti=new IndexStat();
		ti.getTfIdf(index_path, index_name, my_query);
		
		
		/** print number of total document , no of unique terms, number of total terms **/
        ti.getStat(index_path, index_name);
      //  System.out.println(ti.no_total_doc+"  "+ti.no_unique_term+"  "+ti.no_total_term);
      
        /** print the frequency of expression if every document**/
	    long startTime = System.nanoTime();
        for (int j=0;j<exprFreq.size();j++) {
            int[]tmp=exprFreq.get(j);
         //   System.out.println(tmp[0]+"  "+tmp[1]);
        }
        long endTime = System.nanoTime();
        System.out.println(exprFreq.size());
	    System.out.println("----- my code -----------"+ (float)(endTime-startTime)/100000);
       
	}

}
