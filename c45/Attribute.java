package c45;

import java.io.*;
import java.util.*;

public class Attribute{
	private List<String> possible_values;
	public List<String> data;
	private String name;

	public Attribute(){
		this.possible_values = new ArrayList<String>();
		this.data = new ArrayList<String>();
		this.name = "";
	}

	public Attribute(String name){
		this.possible_values = new ArrayList<String>();
		this.data = new ArrayList<String>();
		this.name = name;
	}

	public Attribute(String name, List<String> pvalues){
		this.possible_values = new ArrayList<String>(pvalues);
		this.data = new ArrayList<String>();
		this.name = name;
	}

	public String get_name(){
		return name;
	}

	public List<String> get_possible_values(){
		return possible_values;
	}

	public void set_values(List<String> values){
		this.possible_values = values;
	}

	public void add_value(String value){
		this.possible_values.add(value);
	}

	public void add_data(String data){
		this.data.add(data);
	}

	public boolean contains_value(String value){
		return possible_values.contains(value);
	}

	public void print_values(){
		for(String v : possible_values){
			System.out.print(v+", ");
		}
		System.out.println("");
	}

	public void print_data(){
		for(String v : data){
			System.out.print(v+", ");
		}
		System.out.println("");
	}

	public int data_length(){
		return data.size();
	}


}