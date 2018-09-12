package DBSCAN;

import java.io.*;
import java.util.*;

public class Data{
	private int no;
	private double attr1;
	private double attr2;
	private boolean core;
	private Set<Data> directly_reachable;
	private Integer cluster;
	private boolean cluster_of_all_reachable_set;


	public Data(int no, double a1, double a2){
		this.no = no;
		this.attr1 = a1;
		this.attr2 = a2;
		this.directly_reachable = new HashSet<Data>();
		this.core = false;
		this.cluster = null;
		this.cluster_of_all_reachable_set = false;
	}

	public int get_no(){
		return no;
	}

	public double get_attr(int n){
		if(n == 1){
			return attr1;
		}else if(n == 2){
			return attr2;
		}
		System.out.println("[error] no attribute, data no.: " + this.no);
		return 0;
	}

	public boolean is_core(){
		return core;
	}

	public Integer get_cluster(){
		return cluster;
	}

	public Set<Data> get_directly_reachable(){
		return directly_reachable;
	}

	public boolean if_cluster_of_all_reachable_set(){
		return cluster_of_all_reachable_set;
	}

	public void set_cluster(int cluster){
		this.cluster = cluster;
	}

	public void set_all_cluster(){
		for(Data d : directly_reachable){
			d.set_cluster(this.cluster);
		}
		this.cluster_of_all_reachable_set = true;
	}

	public void set_all_cluster(int cluster){
		this.cluster = cluster;
		for(Data d : directly_reachable){
			d.set_cluster(cluster);
		}
		this.cluster_of_all_reachable_set = true;
	}

	public void add_directly_reachable(Data data){
		directly_reachable.add(data);
	}

	public void check_core_or_not(int MinPts){
		if(directly_reachable.size() > MinPts) core = true;
		else core = false;
	}
}