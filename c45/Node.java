package c45;

import java.io.*;
import java.util.*;

public class Node{
	private String name;
	private List<Node> children;
	private List<String> value_to_children;
	private boolean is_leaf;

	public Node(){
		this.name = "";
		this.children = new ArrayList<Node>();
		this.value_to_children = new ArrayList<String>();
		this.is_leaf = false;
	}

	public Node(String name){
		this.name = name;
		this.children = new ArrayList<Node>();
		this.value_to_children = new ArrayList<String>();
		this.is_leaf = false;
	}

	public String get_name(){
		return name;
	}

	public boolean is_leaf(){
		return this.is_leaf;
	}

	public List<String> get_value_to_children(){
		return value_to_children;
	}

	public List<Node> get_children(){
		return children;
	}

	public void set_leaf(){
		this.is_leaf = true;
		this.children = null;
		this.value_to_children = null;
	}

	public void set_name(String name){
		this.name = name;
	}

	public void add_edge(String edge){
		value_to_children.add(edge);
	}

	public void add_child(Node child){
		children.add(child);
	}

	public Node get_child_by_value(String value){
		for(int i=0; i<children.size(); i++){
			if(C45_util.compare(value, value_to_children.get(i))){
				return children.get(i);
			}
		}
		return null;
	}

	public void print_subtree(){
		if(this.children!=null && !this.children.isEmpty()){
			System.out.println("----------------------------");
			System.out.println(this.name);
			for(String v : this.value_to_children){
				System.out.print(v+", ");
			}
			System.out.println();
			for(Node n : this.children){
				if(n==null){
					System.out.println("[error] node name: " + this.name + " has null child.");
				}
				System.out.print(n.get_name()+", ");
			}
			System.out.println();
			for(Node n : this.children){
				n.print_subtree();
			}
		}
	}
}