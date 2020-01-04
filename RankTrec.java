package rankingmrf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.stream.Collectors;
import javax.swing.JOptionPane;

class Node { 
    public int docid; 
    public float val; 
          
    // A parameterized node constructor 
    public Node(int did, float value) { 
      
        this.docid=did; 
        this.val = value; 
    } 
      
    public int getId() { 
    	//System.out.println(docid+" "+val);
        return docid; 
    }  
} 
class NodeComparator implements Comparator<Node>{ 
    
    // Overriding compare()method of Comparator for descending order of value
    public int compare(Node n1, Node n2) { 
        if (n1.val < n2.val) 
            return 1; 
        else if (n1.val > n2.val) 
            return -1; 
        return 0; 
    } 
}

public class RankTrec {
	static float avg_dl=(float) 0;
    static ArrayList<Float> avg_tf_D=new ArrayList<Float>(); // aberage tf of every docuement
    static ArrayList<Long> dl_D=new ArrayList<Long>(); //document length of every document
    static ArrayList<String> docno=new ArrayList<String>();
    static int flag_mrf;
    
    /**** parameters and files  *****/
    static int top=1000;
    static int window=15;
    static String dataset;
    static String query_type;
    static int qid=0;
    
    static String data_path;
    static String query_file; //the query file is parsed 
    static String index_path="/home/dips/my-inv-index/";
    static String index_name=dataset+"-porter-stop-2";
    static String result_file;
    static String model_type;
    /****************************/
    
   // static ArrayList<Node> main_score;//= new ArrayList<Node>();
    static float idf_term=0;
    
    public static ArrayList<Node> mergeArrayList(ArrayList<Node> main_score, ArrayList<Node> curr)
	{
    	ArrayList<Node> merged=new ArrayList<Node>();
    	//System.outprintln("in merge array");
		if(main_score.size()==0)
			return curr;
			//main_score=curr;
		else
		{
			int m=0,c=0;
			for(;m<main_score.size() && c<curr.size();)
			{
				Node node_tmp=main_score.get(m); Node node_curr=curr.get(c); 
				if(node_tmp.getId()==node_curr.getId())
				{
					Float val_n=node_curr.val+node_tmp.val;
					merged.add(new Node(node_curr.getId(), val_n));
					//main_score.set(m, new Node(node_curr.getId(), val_n));
					m++;c++;
				}
				else if(node_tmp.getId()>node_curr.getId())
				{			
					merged.add(node_curr);
				//	main_score.add(m, node_curr);
					c++;
				}
				else
				{
					merged.add(node_tmp);
					m++;
				}
			}
			while(c<curr.size())
			{
				merged.add(curr.get(c));
				//main_score.add(curr.get(c));	
				c++;
			}
			while(m<main_score.size())
			{
				merged.add(main_score.get(m));
				//main_score.add(curr.get(c));	
				m++;
			}
		}
		//System.outprintln("done");
		return merged;
		//main_score=merged;
	}
    
    
    
    
    public static void printStringArray(String str[])
    {
    	for(int i=0;i<str.length;i++)
    		System.out.println(str[i]);  	
    }
    
    public static ArrayList<String> getPhrase(ArrayList<String> terms){
		ArrayList<String> phrase=new ArrayList<String>();
        for(int i=0;i<terms.size()-1;i++) {
        	String phrs=terms.get(i)+" "+terms.get(i+1);
        	phrase.add(phrs);
        }
        return phrase;
	}
    
    
    public static ArrayList<Node> calculateFunc(String index_path, String index_name, String query,int flag, float wght) throws Exception {
    	query=query.replace(",", "");
    	float lambda = 1;
    	ArrayList<Node> curr_score= new ArrayList<Node>();
    	if(flag_mrf==1)
    	{
    		if(flag==0)
    			lambda=(float)  0.8;
    		else if(flag==1) //unordered
    			lambda=(float) 0.1;
    		else if(flag==2) //ordered
    			lambda=(float) 0.1;
    	}
    	else {
    		if(flag==0)
    			lambda=(float)  1;
    		else if(flag==1) //unordered
    			lambda=(float) 0;
    		else if(flag==2) //ordered
    			lambda=(float) 0;
    	}
	
		IndexStat ti=new IndexStat();
		IndexStat.getTfIdf(index_path, index_name, query);
		Float alpha =(float) 0.5;
		
		long df=ti.exprFreq.size();
		//System.out.println("-----------------"+df+" "+ti.exprFreq.get((int) (df-1))[0]);
		for(int i=0;i<df;i++) {
			int[] tmp=ti.exprFreq.get(i);
			int docid=tmp[0];
			int tf=tmp[1];
			float avg_tf=avg_tf_D.get(docid-1);
			
			Long dl=dl_D.get(docid-1);
			String key= docno.get(docid-1);
			
			float val_t=SimilarityFunction.getDirichlet(alpha,tf,avg_tf,dl,avg_dl, ti.idf_t);
		//	System.out.println(docid+" "+tf+" "+avg_tf+ " "+dl+" "+key+" "+val_t);
			if(val_t<=0)
			{
				System.out.println(query+" "+ti.exprFreq.size()+"  "+ti.no_total_doc);
				System.out.println(docid+" "+tf+" "+avg_tf+ " "+dl+" "+key+" "+val_t+" "+ti.idf_t+" "+avg_dl);
			    JOptionPane.showMessageDialog(null, "dcdjk");
			}
			//System.out.println(".."+val_t);
                  
            float b=(float) (ti.idf_t/(20+ti.idf_t));
           
          //  float val=val_t;
             float val=(float) (lambda*val_t*b);
            //float val=(float) (lambda*val_t*wght);
            Node node_temp=new Node(docid, val);
            curr_score.add(node_temp);
		}
		idf_term=ti.idf_t;
		
		return curr_score;
	    
    }
  
    public static void readCurrentAvgDlTf(String indexPath, String indexFile) throws FileNotFoundException, IOException {
    	avg_tf_D=new ArrayList<Float>();
    	try (BufferedReader br = new BufferedReader(new FileReader(indexPath+indexFile+"-avg-tf-dl"))) {
    	    String line;
    	    int i=0;
    	    while ((line = br.readLine()) != null) {
    	    	if(i==0)
    	    		avg_dl=Float.parseFloat(line);
    	    	else {
    	    		String temp[]=line.split(",");
    	    		docno.add(temp[0]);
    	    		dl_D.add(Long.parseLong(temp[1]));    	    		
    	    		avg_tf_D.add(Float.parseFloat(temp[2]));
    	    	}
    	    	
    	    	i++;
    	    }
    	    br.close();
    	}
    }
	
    public static void readParameters()
    {
    	/******** read parameters **********/
		Scanner dd = new Scanner(System.in);
		String[] vars = new String[3];
		System.out.println("Enter Dataset, query type, and model type");
		System.out.println("eg. of model type- term,mrf,mrf-wght");
		
		for(int i = 0; i <vars.length; i++) {
		  
		  vars[i] = dd.nextLine();
		}
		System.out.println(vars[0]+" "+vars[1]+" "+vars[2]);
		dataset=vars[0];
		query_type=vars[1];
		model_type=vars[2];
		/***********************************/
    }
    public static void createFileNames()
    {
    	data_path="/home/dips/my-data/"+dataset+"/";
        query_file=dataset+"-"+query_type+".txt"; //the query file is parsed 
        index_path="/home/dips/my-inv-index/";
        //String temp="/home/dips/indri-baseline/";
        index_name=dataset+"-porter-stop-2";//"trec-index";
        result_file="/home/dips/my-result/ranking-mrf/"+dataset+"-"+query_type+"-d-b"+"-"+model_type;
        
        File f=new File(result_file);
        if(f.exists())
        	f.delete();
        
        if(model_type.contains("mrf"))
        	flag_mrf=1;
        else
        	flag_mrf=0;
        
        if(dataset.compareTo("trec678")==0)
			qid=301;
		else if(dataset.compareTo("gov2")==0)
			qid=701;  
    }
	public static void main(String[] args) throws Exception {
		readParameters(); // read dataset, query type, model type
		createFileNames();
        readCurrentAvgDlTf(index_path,index_name);  //read current average dl and averafe tf which is required for potential function
        File f=new File(result_file);
        
   		System.out.println("///////////////");
   		System.out.println(avg_tf_D.size()+" "+avg_dl+" "+docno.size());
        

   		/**** read parsed query ***/
   		TrecQueryParsing tqp= new TrecQueryParsing();
   		tqp.readQuery(data_path, query_file); //read parsed query file
   		
        
   		ArrayList<ArrayList<String>>  all_query=tqp.terms; 
        ArrayList<ArrayList<String>> print_query=new ArrayList<ArrayList<String>>();
        ArrayList<ArrayList<Float>> print_idf=new ArrayList<ArrayList<Float>>();
        System.out.println("..........."+all_query.size());
        for(int i=0;i<all_query.size();i++)
        {
           System.out.println(qid);   	   
           ArrayList<String> temp=new ArrayList<String>();       
    	   ArrayList<Float> idf= new ArrayList<Float>();
    	   ArrayList<String> terms=new ArrayList<String>();
    	   
    	   ArrayList<Node> main_score= new ArrayList<Node>(); // initialize main_score for each query
    	   
    	   terms=all_query.get(i);   	   
     	   /****** calculate potential value *********/ 
           float term_wght=1;
           
           //System.out.println(qid+"  "+terms.size()+"  "+terms);
           long startTime = System.nanoTime();
           for(int k=0;k<terms.size();k++)
           {
        	   String t=terms.get(k);
        	   if(t.isEmpty())
        		   break;
        	 
        	   //ArrayList<Node> curr_score=new ArrayList<Node>();
        	   ArrayList<Node> curr_score=calculateFunc(index_path,index_name,t,0,term_wght);
        	   /** merge current score with original score **/
        	   main_score=mergeArrayList(main_score,curr_score);     
        	  // System.out.println("~~~~~~~~~~~"+main_score.size());
           }
           
           if(flag_mrf==1)
           {
        	   /**** for ordered and unordered phrase  ******/
        	   ArrayList<String> phrase= getPhrase(terms);
               //phrase = (ArrayList<String>) phrase.stream().distinct().collect(Collectors.toList()); 
               System.out.println(qid+"  "+phrase.size()+"  "+phrase);
               
               
      	       for(int j=0;j<phrase.size();j++) {
      	    	   String str="#uw"+window+"("+phrase.get(j)+")";
      	    	  // uwphrase.add(str);
      	    	   ArrayList<Node> curr_score=calculateFunc(index_path,index_name,str,1,term_wght);
          	       /** merge current score with original score **/
          	       main_score=mergeArrayList(main_score,curr_score);
      	    	 
      	    	   str="#"+1+"("+phrase.get(j)+")";
      	    	   curr_score=calculateFunc(index_path,index_name,str,2,term_wght);
        	       /** merge current score with original score **/
        	       main_score=mergeArrayList(main_score,curr_score);
      	       }	   
         	  
           }
           
           
          
           long endTime = System.nanoTime();
       	   //System.outprintln("-------- my code --------"+ (float)(endTime-startTime)/100000);
       	
     	  
     	   /*** Put the main_score into priority queue to rank the documents ***/
            PriorityQueue<Node> pq=new PriorityQueue<Node>(new NodeComparator());
            pq.addAll(main_score);
   		    
      	   /*******  write result to file *******/     
    	    StringBuilder query_res = new StringBuilder("");    	  
    	    for(int k=0;k<top && k<pq.size();k++) {
    	        Node node=pq.poll();
    	    	String str=qid+" Q0 "+docno.get(node.docid-1)+" "+(k+1)+" "+node.val+" "+"MRF\n";
    	    	StringBuffer buffer=new StringBuffer(str);
    	    	query_res.append(buffer);
   		     } 
    	     //System.outprintln();
    	     ReadWrite.WriteChunk(query_res.toString(), result_file);
    	     qid++;
    	  }
    //    writeIdf(data_path, idf_file, print_query, print_idf);
       
      }
	

	
}
