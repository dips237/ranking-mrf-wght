package rankingmrf;

public class SimilarityFunction {

	public  static float getDirichlet(Float alpha,int tf,float avg_tf,float dl, float avg_dl, float idf_t) {
		//System.out.println(alpha+"  "+tf+"  "+avg_tf+"  "+dl+"  "+avg_dl+"  "+ idf);
		float c=1; //c should be grater than 0
		float f1=(float) (Math.log(1+tf)/Math.log(c+avg_tf));
		float temp=(avg_dl/dl);
		float ft1=(float) (Math.log(1+temp)/Math.log(2));
		float f2=(tf*ft1);
		float t1=(f1/(1+f1));
		float t2=(f2/(1+f2));
		float val=(float) ((((1-alpha)*t1)+(alpha*t2))*idf_t);
		return val;
	}
	
}
