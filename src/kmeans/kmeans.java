package kmeans;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class kmeans {

	private double [][] _data; // Array of all records in dataset
	  private int [] _label;  // generated cluster labels
	  private int [] _withLabel; // if original labels exist, load them to _withLabel
	                              // by comparing _label and _withLabel, we can compute accuracy. 
	                              // However, the accuracy function is not defined yet.
	  private double [][] _centroids; // centroids: the center of clusters
	  private int _nrows, _ndims; // the number of rows and dimensions
	  private int _numClusters; // the number of clusters;
	  
	  public kmeans(String fileName, String labelname) 
	  {
	    BufferedReader reader;
	  //  CSVHelper csv = new CSVHelper();
	   // String[] values=new String(); 
	    String line=" ";
	    try 
	    {
	      reader = new BufferedReader(new FileReader(fileName));
	      
	      // get the number of rows
	      _nrows =1;
	    //  values = csv.parseLine(reader);
	     
	      while((line=reader.readLine())!=null) {
	    	String[]  values=line.split(",");
	    	_ndims = values.length;
	    	  _nrows++;
	        
	      }
	      
	      reader.close();
	      System.out.println(_nrows + " "+_ndims);

	      // initialize the _data variable
	      _data = new double[_nrows][];
	      for (int i=0; i<_nrows; i++)
	        _data[i] = new double[_ndims];

	      // read records from the csv file
	      reader = new BufferedReader(new FileReader(fileName));
	      int nrow=0;
	      while ((line = reader.readLine())!=null){
	    String[]	  values=line.split(","); 
	        double [] dv = new double[values.length];
	        for (int i=0; i< values.length; i++){
	            dv[i] = Double.parseDouble(values[i]);
	        }
	        _data[nrow] = dv;
	        nrow ++;
	      }      
	      reader.close();
	      System.out.println("loaded data");

	      if (labelname!=null){
	        // load label file to _withLabel;
	        reader = new BufferedReader(new FileReader(labelname));
	        _withLabel = new int[_nrows];
	        int c=0;
	        while ((line =reader.readLine())!=null){
	        	
	        	
	         // _withLabel[c] = Integer.parseInt(values.get(0)); 
	        }
	        reader.close();
	        System.out.println("loaded labels");
	      } 
	    }
	    catch(Exception e) 
	    {
	      System.out.println( e );
	      System.exit( 0 ); 
	    }

	  }
	  
	  public void clustering(int numClusters, double [][] centroids) 
	  {
		  List[] a1 = null;
		  _numClusters = numClusters;
		  if (centroids !=null)
	          _centroids = centroids;
	      else{
	        // randomly selected centroids
	        _centroids = new double[_numClusters][];
	        ArrayList idx= new ArrayList();
	        for (int i=0; i<numClusters; i++){
	          int c;
	          do{
	            c = (int) (Math.random()*_nrows);
	          }while(idx.contains(c)); // avoid duplicates
	          idx.add(c);

	          // copy the value from _data[c]
	          _centroids[i] = new double[_ndims];
	          for (int j=0; j<_ndims; j++)
	            _centroids[i][j] = _data[c][j];
	        }
	        System.out.println("selected random centroids");

	      }
		  double [][] c1 = _centroids;
	      double threshold = 0.001;
	      int round=0;
	      while (round<5){
	          // update _centroids with the last round results
	    	   a1=new ArrayList[numClusters];
	    	  for(int i=0;i<numClusters;i++) {
	    		  a1[i]=new ArrayList();
	    	  }
	          _centroids = c1;

	          //assign record to the closest centroid
	          _label = new int[_nrows];
	          for (int i=0; i<_nrows; i++){
	            _label[i] = closest(_data[i]);
	            a1[closest(_data[i])].add(i+1);
	          }
	          
	          // recompute centroids based on the assignments  
	          c1 = updateCentroids();
	          round ++;
	          /*if ((niter >0 && round >=niter) || converge(_centroids, c1, threshold))
	            break;*/
	        }

	      //  System.out.println("Clustering converges at round " + round);
	        for(int i=0;i<a1.length;i++) {
	        	List l1=a1[i];
	        	Iterator i1=l1.iterator();
	        	System.out.println("cluster number" );
	        	System.out.println(i+1);
	        	System.out.println("points are :");
	        	while(i1.hasNext()) {
	        		
	        	System.out.print(i1.next());
	        	System.out.print(",");
	        	}
	        	System.out.println();
	        }
	        
	        double sum=0;
	        for(int i=0;i<a1.length;i++) {
	        	
	        	List l2=a1[i];
	        	Iterator i1=l2.iterator();
	        	while(i1.hasNext()) {
	        		int i3= (int) i1.next();
	        	double s=dist1(_data[i3-1],_centroids[i] );
	    sum+=s;
	        	}
	        }
	        System.out.println("SSE" );
	        System.out.println( sum );
	  }
	  private double [][] updateCentroids(){
		    // initialize centroids and set to 0
		    double [][] newc = new double [_numClusters][]; //new centroids 
		    int [] counts = new int[_numClusters]; // sizes of the clusters

		    // intialize
		    for (int i=0; i<_numClusters; i++){
		      counts[i] =0;
		      newc[i] = new double [_ndims];
		      for (int j=0; j<_ndims; j++)
		        newc[i][j] =0;
		    }


		    for (int i=0; i<_nrows; i++){
		      int cn = _label[i]; // the cluster membership id for record i
		      for (int j=0; j<_ndims; j++){
		        newc[cn][j] += _data[i][j]; // update that centroid by adding the member data record
		      }
		      counts[cn]++;
		    }

		    // finally get the average
		    for (int i=0; i< _numClusters; i++){
		      for (int j=0; j<_ndims; j++){
		        newc[i][j]/= counts[i];
		      }
		    } 

		    return newc;
		  }
	  private int closest(double [] v){
		    double mindist = dist(v, _centroids[0]);
		    int label =0;
		    for (int i=1; i<_numClusters; i++){
		      double t = dist(v, _centroids[i]);
		      if (mindist>t){
		        mindist = t;
		        label = i;
		      }
		    }
		    return label;
		  }
	  
	  
	  private boolean converge(double [][] c1, double [][] c2, double threshold){
		    // c1 and c2 are two sets of centroids 
		    double maxv = 0;
		    for (int i=0; i< _numClusters; i++){
		        double d= dist(c1[i], c2[i]);
		        if (maxv<d)
		            maxv = d;
		    } 

		    if (maxv <threshold)
		      return true;
		    else
		      return false;
		    
		  }
		  public double[][] getCentroids()
		  {
		    return _centroids;
		  }

		  public int [] getLabel()
		  {
		    return _label;
		  }

		  public int nrows(){
		    return _nrows;
		  }

		 /* public void printResults(){
		     // System.out.println("Label:");
		     for (int i=0; i<_nrows; i++)
		       // System.out.println(_label[i]);
		     // System.out.println("Centroids:");
		    // for (int i=0; i<_numClusters; i++){
		        for(int j=0; j<_ndims; j++)
		       //    System.out.print(_centroids[i][j] + " ");
		        // System.out.println();
		     }

		  }*/


	
	  private double dist(double [] v1, double [] v2){
		    double sum=0;
		    for (int i=0; i<_ndims; i++){
		      double d = v1[i]-v2[i];
		      sum += d*d;
		    }
		    return Math.sqrt(sum);
		  }
	  private double dist1(double [] v1, double [] v2){
		    double sum=0;
		    for (int i=0; i<_ndims; i++){
		      double d = v1[i]-v2[i];
		      sum += d*d;
		    }
		    return sum;
		  }
	  
	  
	public static void main(String[] args) {
		  kmeans KM = new kmeans( "out1.csv", null );
		 // int k=Integer.parseInt(args[0]);
		  int l=Integer.parseInt(args[0]);
		     KM.clustering(l, null); // 2 clusters, maximum 10 iterations
		  //   KM.printResults();
	}
}
