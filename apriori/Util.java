import java.io.*;
import java.util.*;

public class Util{
	public static String[] delete_repetition(String[] strs){
		//return: array w/ all elements not repeat
		Set<String> items = new HashSet<>();
		for(String str : strs){
			items.add(str);
		}
		return items.toArray(new String[items.size()]);
	}

	public static List<String> delete_repetition(List<String> strs){
		//return: list w/ all elements not repeat
		Set<String> items = new HashSet<>();
		items.addAll(strs);
		return new ArrayList<String>(items);
	}

	public static List<List<String>> delete_repetition_l(List<List<String>> lists){
		Set<List<String>> items = new HashSet<>();
		items.addAll(lists);
		return new ArrayList<List<String>>(items);
	}

	public static void sort(List<String> list){
		Collections.sort(list, new Comparator<String>(){
			@Override
			public int compare(String s1, String s2){
				return Integer.valueOf(s1).compareTo(Integer.valueOf(s2));
			}
		});
	}

	public static long performance(){
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		return memory;
	}

}