package rankingmrf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.lucene.benchmark.quality.QualityQuery;
import org.apache.lucene.benchmark.quality.trec.*;
public class TrecQueryParsing {
	public static ArrayList<Integer> qid=new ArrayList<Integer>();
	public static ArrayList<String> query=new ArrayList<String>();
	public static ArrayList<ArrayList<Float>> wght=new ArrayList<ArrayList<Float>>();
	public static ArrayList<ArrayList<String>>  terms=new ArrayList<ArrayList<String>>();
	
	public static ArrayList<String> getPhrase(ArrayList<String> terms){
		ArrayList<String> phrase=new ArrayList<String>();
        for(int i=0;i<terms.size()-1;i++) {
        	String phrs=terms.get(i)+" "+terms.get(i+1);
        	phrase.add(phrs);
        }
        return phrase;
	}
	public static void parseTrecQuery(String data_path, String query_file,String field_name) throws IOException {
		TrecTopicsReader ttr=new TrecTopicsReader();
		FileReader fr= new FileReader(new File(data_path+query_file));
		BufferedReader br= new BufferedReader(fr);
		QualityQuery[] trec_query=ttr.readQueries(br);
		for(int i=0;i<trec_query.length;i++) {
			
			qid.add(Integer.parseInt((trec_query[i].getQueryID().split(" ")[2])));
			query.add(trec_query[i].getValue(field_name));
			
		}
		br.close();
		fr.close();
	}
	
	
	public static void readQueryWeights(String data_path, String wght_file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(data_path+wght_file))) {
    	    String line;
    	    int i=1;
    	    while ((line = br.readLine()) != null) {
    	       if(i%2==0)
    	       {
    	    	   System.out.println("weights:  "+line);
    	    	   String[] temp=line.split(",");
    	    	   ArrayList<Float> float_temp=new ArrayList<Float>();
    	    	   for(int t=0;t<temp.length;t++){
    	    		   float_temp.add(Float.parseFloat(temp[t]));
    	    	   }
    	    	   System.out.println(float_temp);
    	    	   wght.add(float_temp);
    	       }
    	       else {
    	    	   System.out.println("query:  "+line);
    	    	   String[] temp=line.split(",");
    	    	   ArrayList<String> string_temp=new ArrayList<String>();
    	    	   for(int t=0;t<temp.length;t++){
    	    		   string_temp.add(temp[t]);
    	    		   if(temp[t].split(" ").length>1) {
    	    			   System.out.println(temp[t]);
    	    		   }
    	    	   }
    	    	   System.out.println(string_temp);
    	    	   
    	    	   System.out.println();
    	    	   terms.add(string_temp);
    	       }
    	       i++;
    	    }
    	    br.close();
    	    System.out.println(i);
    	}
		
	}
	public static void readQuery(String data_path, String query_file) throws IOException {
		try (BufferedReader br = new BufferedReader(new FileReader(data_path+query_file))) {
    	    String line;
    	    while ((line = br.readLine()) != null) {
    	    	   System.out.println("query:  "+line);
    	    	   String[] temp=line.split(",");
    	    	   ArrayList<String> string_temp=new ArrayList<String>();
    	    	   for(int t=0;t<temp.length;t++){
    	    		   string_temp.add(temp[t]);
    	    		   if(temp[t].split(" ").length>1) {
    	    			   System.out.println(temp[t]);
    	    		   }
    	    	   }
    	    	   System.out.println(string_temp);
    	    	   
    	    	   System.out.println();
    	    	   terms.add(string_temp);
    	      
    	    }
    	    br.close();
    	}
		
	}
	
	
	
	
	public static void main(String[] args) throws IOException {
		String data_path="/home/dips/my-data/gov2/";//"/home/dips/my-data/trec678/";
		String query_file="gov2.topics";
		//String stopword="/home/dips/nltk_data/stopwords/english";
		String stopword="/home/dips/my-data/stopwords/stopword-indri";//github";///home/dips/nltk_data/stopwords/english";
		parseTrecQuery(data_path, query_file,"description");//"narrative");"title"
		System.out.println(query);
		Preprocess preprocess_ins = new Preprocess();
		
		//readQueryWeights(data_path,"wght-trec678-2");
		
	/*	Write the query file */
	    File file = new File(data_path+"gov2-desc.txt");//narr.txt");
	    if(file.exists())
	    	file.delete();
	    
		FileWriter fr = null;
		BufferedWriter br = null;
		fr = new FileWriter(file, true);
		br = new BufferedWriter(fr);
		
		for(int i=0;i<query.size();i++)
        {
     	   
           String qstr=query.get(i);
     	   qstr=qstr.replace("'", "");
     	   qstr=qstr.replace(",", "");
     	   System.out.println(qstr);
     	   ArrayList<String> terms=new ArrayList<String>();
     	  //terms=preprocess_ins.tokenizeString(qstr);
     	   terms=preprocess_ins.tokenizeStringRemovingStop(qstr,stopword);
     	   System.out.println(terms);
     	   System.out.println();
     	   for(int j=0;j<terms.size();j++)
     	   {
     		  if(j>0)
    			   br.write(",");
     		   br.write(terms.get(j));
     	   }
     	   br.newLine();
     	   
        }
		br.close();
		fr.close();
		
	}
	

}
