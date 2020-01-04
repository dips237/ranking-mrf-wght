package rankingmrf;
import java.io.File;
import java.util.ArrayList;

import lemurproject.indri.QueryEnvironment;

public class StoreCollectionInformation {

	public static void getDocumentInfo(String index_path, String index_name) throws Exception {
		QueryEnvironment qev = new QueryEnvironment();
		qev.addIndex(index_path+index_name);
	
        long doc_count=qev.documentCount();
        long sum_doc_len=0;
        
        StringBuilder coll_info = new StringBuilder("");
        System.out.println(doc_count);
        for(int i=1;i<=doc_count;i++)
        {
        	int unq=0;
        	float avg_tf_of_doc=0;
        	int dl=qev.documentLength(i);
        	
        	int [] tmp= {i};
    		String docno[]=qev.documentMetadata(tmp, "docno");
        	unq=qev.documentVectors(tmp)[0].stems.length-1;
        	if(dl>0)
        	    avg_tf_of_doc=(float)dl/(float)unq;
        	else
        		avg_tf_of_doc=0;
        	//System.out.println((i)+" "+docno[0]+" "+dl+" "+avg_tf_of_doc);
        	
        	String str=docno[0]+","+dl+","+avg_tf_of_doc+"\n";
	    	
        	StringBuffer buffer=new StringBuffer(str);
        	coll_info.append(buffer);
        	        	
        	sum_doc_len+=dl;
        	
        }
        float avg_dl=(float) sum_doc_len/ (float) doc_count;
        
        /* write avg_dl on 0th line*/
        String col_file=index_path+index_name+"-avg-tf-dl";
        /** delete file for fresh write**/
        File f=new File(col_file);
        if(f.exists())
        	f.delete();
        
        
        ReadWrite.WriteChunk(Float.toString(avg_dl)+"\n",col_file );
        
        /* write docno, doc length, avg_tf and line number+1 correspond to indri doc id*/
        
        ReadWrite.WriteChunk(coll_info.toString(), col_file);
        
        
        
    	qev.close();
    	qev.delete();	
		
	}
	public static void main(String[] args) throws Exception {
		
		String index_path="/home/dips/my-inv-index/";
		String index_name="gov2-porter-stop-2";
		getDocumentInfo(index_path, index_name);
	}
}
