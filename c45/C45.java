package c45;

import java.io.*;
import java.util.*;
import java.lang.Math.*;

public class C45{
	private static int ttl_data_cnt;

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
				ttl_data_cnt++;
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

			for(int i : testing_index){
				int from_index = i*batch_size;
				int to_index = (i+1)*batch_size;
				List<String> tmp = new ArrayList<String>(attr.data.subList(from_index, to_index));
				test_attr.data.addAll(tmp);
			}
			testing_dataset.add(test_attr);

			//create training dataset
			for(int i=0; i<10; i++){
				if(Arrays.asList(testing_index).contains(i)){ continue; }
				int from_index = i*batch_size;
				int to_index = (i+1)*batch_size;
				List<String> tmp = new ArrayList<String>(attr.data.subList(from_index, to_index));
				train_attr.data.addAll(tmp);
			}
			training_dataset.add(train_attr);
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
			class_cnt[C45_util.get_index(d, classes)]++;
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
			if(C45_util.compare(attr.data.get(i),value)){
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
	//-------------C45-start----------------
	public static List<String> find_best_split_point(Attribute attr, Attribute category){
		List<Long> data;
		if(attr.data.get(0).matches("[\\d]*-[\\d]*-[\\d]*")){
			List<String> tmp = C45_util.remove_minus_sign(attr.data);
			data = C45_util.copy_strList_as_longList(tmp);
		//}else if(attr.data.get(0).matches("[\\d]*\\ [a-zA-Z]+.*")){
		}else if(C45_util.is_address(attr.data.get(0))){
			List<String> tmp = C45_util.leave_only_num(attr.data);
			data = C45_util.copy_strList_as_longList(tmp);
		}else{
			data = C45_util.copy_strList_as_longList(attr.data);
		}
		
		double max_gainratio = 0;
		long max_split_num = 0;
		C45_util.delete_repetition(data);
		Collections.sort(data);
		//System.out.println(attr.get_name());
		for(long i : data){
			//System.out.println("split point: "+i);
			Attribute c_attr = C45_util.split_attr_into_2classes(attr, i);
			double gainratio = GainRatio(c_attr, category);
			//double gainratio = Gain(c_attr, category);
			if(gainratio>max_gainratio){
				max_gainratio = gainratio;
				max_split_num = i;
			}
		}
		//Attribute ret = C45_util.split_attr_into_2classes(attr, max_split_num);
		List<String> ret = new ArrayList<String>();
		ret.add(String.valueOf(max_gainratio));
		ret.add(String.valueOf(max_split_num));
		return ret;
	}

	public static int get_attr_w_largest_gainratio(List<Attribute> attrs, Attribute category){
		double[] gainratios = new double[attrs.size()];
		long[] split_points = new long[attrs.size()];
		for(int i=0; i<attrs.size(); i++){
			Attribute attr = attrs.get(i);
			if(attr.data.get(0).matches("\\d.*")){		//continuous
				System.out.println(attr.data.get(0));
				List<String> max = find_best_split_point(attr, category);
				gainratios[i] = Double.parseDouble(max.get(0));
				split_points[i] = Long.parseLong(max.get(1));
			}else{		//categorical
				gainratios[i] = GainRatio(attr, category);
				//gainratios[i] = Gain(attr, category);
			}
		}
		//Util.print_arr(gainratios);
		int max_index = Util.get_max_index(gainratios);
		if(split_points[max_index]!=0){
			List<String> values = new ArrayList<String>();
			values.add("<"+String.valueOf(split_points[max_index]));
			values.add(">="+String.valueOf(split_points[max_index]));
			attrs.get(max_index).set_values(values);
		}
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
				if(C45_util.compare(data.get(j), value)){
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

	public static Node c45_algo(List<Attribute> non_c_attrs, Attribute category){
		Node node = new Node();

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
		if(!Util.check_attributes_have_same_amount_data(non_c_attrs)){ return null; }
		if(non_c_attrs.get(0).data.size()<ttl_data_cnt/200){
			node = new Node(get_most_frequent_class(category));
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
			node.add_child(c45_algo(subdatasets.get(i), new_cate));
		}

		return node;

	}
	//-------------C45-end----------------
	//-------------testing-start----------------
	public static String get_value(List<Attribute> attrs, String node_name, int data_index){
		for(Attribute attr : attrs){
			if(attr.get_name().equals(node_name)){
				String ret = attr.data.get(data_index);
				return ret;
			}
		}
		System.out.println("[error] [testing] not found.");
		return null;
	}

	public static double test(List<Attribute> testing, Attribute testing_category, Node root){
		int correct = 0;
		int wrong = 0;
		for(int i=0; i<testing.get(0).data.size(); i++){
			Node node = root;
			while(!node.is_leaf()){
				node = node.get_child_by_value(get_value(testing, node.get_name(), i));
			}
			if(node.is_leaf()){
				if(node.get_name().equals(testing_category.data.get(i))) correct++;
				else wrong++;
			}
		}
		double accuracy = (double)correct/(correct+wrong);
		return accuracy;
	}
	//-------------testing-end----------------

	public static void main(String args[]){
		try{
			PrintStream out = new PrintStream(new FileOutputStream("c45/result/2.txt"));
			//PrintStream out = new PrintStream(new FileOutputStream("result/C50S10T2.5N10000_result.txt"));
			System.setOut(out);
		}catch(IOException ex){
			ex.printStackTrace();
		}


		List<Attribute> attributes = readFile("c45/data/CUSTOMER.TXT");
		//List<Attribute> attributes = readFile("c45/data/test2.txt");//------------debug

		int category_index = attributes.size()-3;
		Attribute category = attributes.remove(category_index);
		attributes.add(category);	//last on is the categorical attribute;

		List<int[]> testingset_indexes = new ArrayList<int[]>();
		Util.gen_testing_index(testingset_indexes, 3);
		//for(int i=0; i<testingset_indexes.size(); i++){
			int i=2;
			List<List<Attribute>> test_train = devide_train_test(attributes, testingset_indexes.get(i));
			List<Attribute> testing_dataset = test_train.get(0);
			List<Attribute> training_dataset = test_train.get(1);

			//-------------debug
			//System.out.println(training_dataset.size());
			//-------------debug


			Node root = c45_algo(training_dataset.subList(5, training_dataset.size()-1), training_dataset.get(training_dataset.size()-1));
			root.print_subtree();

			///testing
			double accu = test(testing_dataset.subList(5, testing_dataset.size()-1), testing_dataset.get(testing_dataset.size()-1), root);
			Util.print_arr(testingset_indexes.get(i));
			System.out.println(accu);
			//break;
		//}

		//------------test
		//Node root = ID3(attributes.subList(0,attributes.size()-1), attributes.get(attributes.size()-1));
		//root.print_subtree();



	}
}