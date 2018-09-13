package DBSCAN;

import java.io.*;
import java.util.*;

public class Dbscan{
	private List<Data> DB;
	private double R;
	private int MinPts;
	private int max_cluster_num;

	public Dbscan(double R, int MinPts){
		this.R = R;
		this.MinPts = MinPts;
	}

	public void readFile(String filename){
		try{
			File f = new File(filename);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line = "";
			this.DB = new ArrayList<Data>();

			int cnt = 0;
			while((line = b.readLine()) != null){
				String[] splited_line = line.split("\t");
				this.DB.add(new Data(cnt, Double.parseDouble(splited_line[0]), Double.parseDouble(splited_line[1])));
				cnt++;
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	public void find_core(){
		for(Data di : DB){
			for(Data dj : DB){
				if(Util.distance(di, dj) < R){
					di.add_directly_reachable(dj);
				}
			}
			di.check_core_or_not(MinPts);
		}
	}

	public void set_cluster(Data d){
		//set cluster num of the point
		if(d.is_core()){
			if(d.if_cluster_of_all_reachable_set()) return ;
			if(d.get_cluster()!=null){
				d.set_all_cluster();
			}else{
				d.set_all_cluster(max_cluster_num++);
			}
			for(Data dj : d.get_directly_reachable()){
				set_cluster(dj);
			}
		}
	}

	public void find_cluster(){
		for(Data di : DB){
			set_cluster(di);
		}
	}

	public void save_to_file(String output_filename){
		try{
			PrintStream out = new PrintStream(new FileOutputStream(output_filename));
			//PrintStream out = new PrintStream(new FileOutputStream("result/C50S10T2.5N10000_result.txt"));
			System.setOut(out);
		}catch(IOException ex){
			ex.printStackTrace();
		}

		System.out.println("no\tattr1\tattr2\ttype\tcluster");	//title

		for(Data d : DB){
			System.out.print(d.get_no()+"\t"+d.get_attr(1)+"\t"+d.get_attr(2)+"\t");
			if(d.is_core()) System.out.println("core\t"+d.get_cluster());
			else if(d.get_cluster()!=null) System.out.println("border\t"+d.get_cluster());
			else System.out.println("noise\t");
		}
	}

	public void run(String filename, String output_filename){
		readFile(filename);	//"Data/clustering_test.txt"
		find_core();
		find_cluster();
		save_to_file(output_filename);
	}
}