package c45;

import java.io.*;
import java.util.*;
import java.lang.Math.*;

public class Id3{
	public static List<String> split_line(String line){
		String delimiter = "\",|\"";
		String[] splited_line = line.split(delimiter);
		List<String> ret = new ArrayList<String>();
		for(String s : splited_line){
			if(s.length() != 0 && !s.equals(" ")){
				if(s.matches("\\d.*")){
					for(String ss : s.split(",")){
						if(ss.length() != 0){ ret.add(ss); }
					}
				}else{
					ret.add(s);
				}
			}
		}
		return ret;
	}

	public static List<Attribute> readFile(String filename){
		List<Attribute> attributes = new ArrayList<Attribute>();
		try{
			File f = new File(filename);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line = "";

			// read header
			List<String> attribute_names = split_line(b.readLine());
			//System.out.println(attribute_names);
			for(String name : attribute_names){
				attributes.add(new Attribute(name));

			}

			//read data
			while((line = b.readLine()) != null){
				List<String> line_data = split_line(line);
				for(int i=0; i<line_data.size(); i++){
					if(!attributes.get(i).contains_value(line_data.get(i))) {
						attributes.get(i).add_value(line_data.get(i));
					}
					attributes.get(i).add_data(line_data.get(i));
				}
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return attributes;
	}

	public static List<List<Attribute>> devide_train_test(List<Attribute> attrs, int[] testing_index){
		//return [training dataset, testing dataset]
		if(!Util.check_attributes_have_same_amount_data(attrs)){ return null; }

		int batch_size = attrs.get(0).data.size()/10;
		List<Attribute> testing_dataset = new ArrayList<Attribute>();
		List<Attribute> training_dataset = new ArrayList<Attribute>();
		for(Attribute attr : attrs){
			//create testing data
			String name = attr.get_name();
			Attribute test_attr = new Attribute(name, attr.get_possible_values());
			Attribute train_attr = new Attribute(name, attr.get_possible_values());

			for(int i=0; i<3; i++){
				List<String> tmp = new ArrayList<String>(attr.data.subList(testing_index[i]*batch_size, testing_index[i+1]*batch_size));
				test_attr.data.addAll(tmp);
			}
			testing_dataset.add(test_attr);

			//create training dataset
			for(int i=0; i<10; i++){
				if(Arrays.asList(testing_index).contains(i)){ continue; }
				List<String> tmp = new ArrayList<String>(attr.data.subList(testing_index[i]*batch_size, testing_index[i+1]*batch_size));
				train_attr.data.addAll(tmp);
			}
		}

		List<List<Attribute>> ret = new ArrayList<>();
		ret.add(testing_dataset);
		ret.add(training_dataset);
		return ret;
	}

	public static boolean list_w_same_value(List<String> l){
		String tmp = l.get(0);
		for(int i=1; i<l.size(); i++){
			if(!tmp.equals(l.get(i))){
				return false;
			}
		}
		return true;
	}

	public static int[] cnt_of_each_class(Attribute category){
		List<String> classes = category.get_possible_values();
		int[] class_cnt = new int[classes.size()];
		for(int i=0; i<classes.size(); i++){
			class_cnt[i] = 0;
		}
		for(String d : category.data){
			class_cnt[classes.indexOf(d)]++;
		}

		return class_cnt;
	}

	public static String get_most_frequent_class(Attribute category){
		List<String> classes = category.get_possible_values();
		int[] class_cnt = cnt_of_each_class(category);
		int max_index = Util.get_max_index(class_cnt);
		return classes.get(max_index);
	}


	//-------------Gain-start----------------

	public static Attribute get_category_of_particular_value(Attribute attr, Attribute category, String value){
		List<String> ctgy_of_value = new ArrayList<String>();
		List<String> copy_category_data = new ArrayList<String>(category.data);
		for(int i=0; i<attr.data.size(); i++){
			if(attr.data.get(i).equals(value)){
				ctgy_of_value.add(copy_category_data.get(i));
			}
		}
		Attribute ret = new Attribute(category.get_name(), category.get_possible_values());
		ret.data = ctgy_of_value;
		return ret;
	}

	public static int ttl_of_array(int[] arr){
		int ttl = 0;
		for(int i : arr){
			ttl += i;
		}
		return ttl;
	}

	public static double I(int[] Cnt){
		double ret = 0;
		int ttl = ttl_of_array(Cnt);
		for(int c : Cnt){
			if(c==0){
				ret += 0;
			}else{
				double p = (double)c/ttl;
				ret -= p*(Math.log10(p)/Math.log10(2));
			}
		}
		return ret;
	}

	public static double E(Attribute attr, Attribute category){
		double ret = 0;
		int ttl_cnt = category.data.size();
		for(String value : attr.get_possible_values()){
			Attribute ctgy_of_value = get_category_of_particular_value(attr, category, value);
			int[] class_cnt = cnt_of_each_class(ctgy_of_value);
			int ttl = ttl_of_array(class_cnt);	//==ctgy_of_value.data.size()
			ret += ((double)ttl/ttl_cnt)*I(class_cnt);
		}
		return ret;
	}

	public static double Gain(Attribute attr, Attribute category){
		int[] class_cnt = cnt_of_each_class(category);
		return (I(class_cnt)-E(attr,category));
	}

	public static double SplitInfo(Attribute attr){
		int[] value_cnt = cnt_of_each_class(attr);
		return I(value_cnt);
	}

	public static double GainRatio(Attribute attr, Attribute category){
		return Gain(attr, category)/SplitInfo(attr);
	}

	//-------------Gain-end----------------
	//-------------ID3-start----------------
	public static int get_attr_w_largest_gainratio(List<Attribute> attrs, Attribute category){
		double[] gainratios = new double[attrs.size()];
		for(int i=0; i<attrs.size(); i++){
			Attribute attr = attrs.get(i);
			gainratios[i] = GainRatio(attr, category);
			//gainratios[i] = Gain(attr, category);
		}

		int max_index = Util.get_max_index(gainratios);
		//return attrs.get(max_index);
		return max_index;
	}

	public static List<List<Attribute>> split_attrs_by_value(List<Attribute> attrs, int target_attr_index, Attribute category){
		List<List<Attribute>> subdatasets = new ArrayList<List<Attribute>>();	//size() = values.size()
		List<String> target_values = attrs.get(target_attr_index).get_possible_values();
		List<String> data = attrs.get(target_attr_index).data;
		for(int i=0; i<target_values.size(); i++){
			String value = target_values.get(i);
			List<Attribute> subdataset = new ArrayList<Attribute>();
			Attribute category_subset = new Attribute(category.get_name(), category.get_possible_values());
			for(int j=0; j<attrs.size(); j++){
				subdataset.add(new Attribute(attrs.get(j).get_name(), attrs.get(j).get_possible_values()));
			}
			for(int j=0; j<data.size(); j++){
				if(data.get(j).equals(value)){
					for(int k=0; k<attrs.size(); k++){
						String to_add = attrs.get(k).data.get(j);
						subdataset.get(k).data.add(to_add);
					}
					String to_add = category.data.get(j);
					category_subset.data.add(to_add);
				}
			}

			//get rid of traget attr
			for(int j=0; j<subdataset.size(); j++){
				if(subdataset.get(j).get_name().equals(attrs.get(target_attr_index).get_name())){
					subdataset.remove(j);
				}
			}
			subdataset.add(category_subset);
			subdatasets.add(subdataset);
		}
		return subdatasets;	//subdatasets : [[attr1, attr2, ..., category],[attr1, attr2, ..., category],[...],...]
	}

	public static Node ID3(List<Attribute> non_c_attrs, Attribute category){
		Node node = new Node();

		if(!Util.check_attributes_have_same_amount_data(non_c_attrs)){ return null; }
		if(non_c_attrs.isEmpty()){
			node = new Node(get_most_frequent_class(category));
			node.set_leaf();
			return node;
		}
		if(non_c_attrs.get(0).data.size()==0){
			/*Failure*/
			node = new Node("Failure");
			node.set_leaf();
			return node;
		}
		if(list_w_same_value(category.data)) {
			node = new Node(category.data.get(0));
			node.set_leaf();
			return node;
		}

		int max_gain_attr_index = get_attr_w_largest_gainratio(non_c_attrs, category);
		List<String> values = non_c_attrs.get(max_gain_attr_index).get_possible_values();
		List<List<Attribute>> subdatasets = split_attrs_by_value(non_c_attrs, max_gain_attr_index, category);
		node.set_name(non_c_attrs.get(max_gain_attr_index).get_name());
		for(int i=0; i<values.size(); i++){
			node.add_edge(values.get(i));
			Attribute new_cate = subdatasets.get(i).remove(subdatasets.get(i).size()-1);
			node.add_child(ID3(subdatasets.get(i), new_cate));
		}

		return node;

	}
	//-------------ID3-end----------------
	//-------------print-start---------------
	public static void print_tree(Node root){
		while(root!=null){

		}
	}
	//-------------print-end----------------

	public static void main(String args[]){
		//List<Attribute> attributes = readFile("c45/data/CUSTOMER.TXT");
		List<Attribute> attributes = readFile("c45/data/test.txt");//------------debug

		int category_index = attributes.size()-1;
		Attribute category = attributes.remove(category_index);
		attributes.add(category);	//last on is the categorical attribute;

		/*List<int[]> testingset_indexes = new ArrayList<int[]>();
		Util.gen_testing_index(testingset_indexes, 3);
		for(int i=0; i<testingset_indexes.size(); i++){
			List<List<Attribute>> test_train = devide_train_test(attributes, testingset_indexes.get(i));
			List<Attribute> testing_dataset = test_train.get(0);
			List<Attribute> training_dataset = test_train.get(1);

			Node root = ID3(training_dataset.subList(1, training_dataset.size()-1), training_dataset.get(training_dataset.size()-1));
			break;
		}*/

		//////////test
		Node root = ID3(attributes.subList(0,attributes.size()-1), attributes.get(attributes.size()-1));
		root.print_subtree();



	}
}