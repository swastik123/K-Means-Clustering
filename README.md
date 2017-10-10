# K-Means-Clustering

Instruction to run the application
----------------------------------

Below is the script to compile and run the application

javac ApplicationRunner.java
java ApplicationRunner 25 ../InitialSeeds.txt ../Tweets.json ../output.txt

Following are the understanding about the above script

1.	ApplicationRunner.java has the main method as starting point
2.	While executing, the application expects four parameter as argument 
		args0- number of clusters
		args1- Path where the text file which contains initial seeds information is present.  In the above example, ../Dataset is passed as argument. The file is residing one step back to the folder where ApplicationRunner.java is residing
		args2- Path where tweet dataset is present in json format
		args3- Path and file name where the output file needs to be generated. In the above example, output file will be output.txt which will be generated in folder one step back to the folder where ApplicationRunner.java is present	
