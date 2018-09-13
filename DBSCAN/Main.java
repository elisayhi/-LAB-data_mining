package DBSCAN;

import java.io.*;
import java.util.*;

public class Main{
	public static void main(String args[]){
		//args[0]: radius; args[1]: MinPts
		//Dbscan dbscan = new Dbscan(1, 15);
		Dbscan dbscan = new Dbscan(Double.parseDouble(args[0]), Integer.parseInt(args[1]));
		dbscan.run("DBSCAN/Dataset/clustering_test.txt", "DBSCAN/result/clustering_test_result.txt");
	}
}