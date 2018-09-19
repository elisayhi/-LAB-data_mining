package c45;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class C45_util{
    public static Attribute get_class_of_particular_value(List<Long> arr, Attribute category, Long val){
        List<String> remain_class = new ArrayList<String>();
        for(int i=0; i<arr.size(); i++){
            if(arr.get(i)!=val) continue;
            remain_class.add(category.data.get(i));
        }
        Attribute ret = new Attribute(category.get_name(), category.get_possible_values());
        ret.data = remain_class;
        return ret;
    }

    public static String get_class_of_attr(List<Long> arr, Attribute category, Long val){
        return C45.get_most_frequent_class(get_class_of_particular_value(arr, category, val));
    }

    public static void sort_arr_n_class(List<Long> nonrepeat_arr, List<String> corresponding_class){
        List<Long> sorted_arr = new ArrayList<Long>(nonrepeat_arr);
        List<String> sorted_class = new ArrayList<String>();
        Collections.sort(sorted_arr);
        for(Long x : sorted_arr){
            sorted_class.add(corresponding_class.get(nonrepeat_arr.indexOf(x)));
        }
        nonrepeat_arr = sorted_arr;
        corresponding_class = sorted_class;
    }

    public static List<Long> gen_split_point(List<Long> arr, Attribute category){
        List<Long> arr_nonrepeat = new ArrayList<Long>();
        List<String> corresponding_class = new ArrayList<String>();

        //delete repetition and sort
        for(Long x : arr){
            if(arr_nonrepeat.contains(x)) continue;
            arr_nonrepeat.add(x);
            corresponding_class.add(get_class_of_attr(arr, category, x));
        }
        sort_arr_n_class(arr_nonrepeat, corresponding_class);

        //get the point to split
        List<Long> split_points = new ArrayList<Long>();
        for(int i=0; i<arr_nonrepeat.size()-1; i++){
            if(corresponding_class.get(i)==corresponding_class.get(i+1)) continue;
            split_points.add(arr_nonrepeat.get(i));
        }
        split_points.add(arr_nonrepeat.get(arr_nonrepeat.size()-1));

        return split_points;
    }

	public static String remove_minus_sign(String str){
		String[] split = str.split("-");
		String ret = "";
		for(String s : split){
			ret += s;
		}
		return ret;
	}

	public static List<String> remove_minus_sign(List<String> data){	//V
		List<String> copy = new ArrayList<String>(data);
		for(int i=0; i<copy.size(); i++){
			copy.set(i, remove_minus_sign(copy.get(i)));
		}
		return copy;
	}

	public static List<String> leave_only_num(List<String> str){
		List<String> ret = new ArrayList<String>();
		Pattern p = Pattern.compile("\\d+");
		
		for(String s : str){
			Matcher m = p.matcher(s);
			if(m.find()){
				ret.add(m.group());
			}else{
				System.out.println("[error] continuous w/ no number: " + s);
			}
		}
		return ret;
	}

	public static String leave_only_num(String str){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(str);
		String ret = "";
		if(m.find()){
			ret = m.group();
		}else{
			System.out.println("[error] continuous w/ no number: " + str);
		}
		return ret;
	}

	public static List<Long> copy_strList_as_longList(List<String> strList){	//V
		List<Long> longList = new ArrayList<Long>();
		for(String d : strList){
			longList.add(Long.parseLong(d));
		}
		return longList;
	}

	public static boolean is_smaller(String str, int i){
		if(str.contains("-")){
			if(Integer.valueOf(remove_minus_sign(str)) < i) return true;
			else return false;
		}else{
			if(Integer.valueOf(str) < i) return true;
			else return false;
		}
	}

	public static boolean is_address(String s){
		Pattern p = Pattern.compile("\\d+");
		Matcher m = p.matcher(s);
		if(m.find()) return true;
		return false;
	}

	public static Attribute split_attr_into_2classes(Attribute attr, long split_num){
		Attribute c_attr = new Attribute(attr.get_name());

		List<String> values = new ArrayList<String>();
		values.add("<"+String.valueOf(split_num));
		values.add(">="+String.valueOf(split_num));
		c_attr.set_values(values);

		c_attr.data = new ArrayList<String>(attr.data);
		return c_attr;
	}

	public static int get_index(String d, List<String> values){
		if(d.matches("[\\d]*-[\\d]*-[\\d]*")) d = remove_minus_sign(d);
		if(is_address(d)) d = leave_only_num(d);
		if(values.get(0).matches("<.*")){
			if(Long.parseLong(d)<Long.parseLong(values.get(0).split("<")[1])) return 0;
			else if(Long.parseLong(d)>=Long.parseLong(values.get(1).split(">=")[1])) return 1;
		}else if(values.get(0).matches(">.*")){
			if(Long.parseLong(d)>=Long.parseLong(values.get(0).split(">=")[1])) return 0;
			else if(Long.parseLong(d)<Long.parseLong(values.get(1).split("<")[1])) return 1;
		}else{
			return values.indexOf(d);
		}
		return -1;
	}

	public static boolean compare(String data, String value){
		if(data.matches("[\\d]*-[\\d]*-[\\d]*")) data = remove_minus_sign(data);
		if(is_address(data)) data = leave_only_num(data);
		if(value.matches("<.*")){
			if(Long.parseLong(data) < Long.parseLong(value.split("<")[1])) return true;
		}else if(value.matches(">=.*")){
			if(Long.parseLong(data) >= Long.parseLong(value.split(">=")[1])) return true;
		}else{
			if(data.equals(value)) return true;
			else return false;
		}
		return false;
	}

}
