import java.util.*;
import java.io.*;
import java.lang.StringBuilder;

public class Apriori{

	public static String[] delete_repetition(String[] strs){
		//return: array w/ all elements not repeat
		Set<String> items = new HashSet<>();
		for(String str : strs){
			items.add(str);
		}
		return items.toArray(new String[items.size()]);
	}

	public static void candidate2frequent(Map<List, Integer> C, int min_sup){
		Iterator<Map.Entry<List, Integer>> it = C.entrySet().iterator();
		List<List> nonfrequent = new ArrayList<List>();

		//find all nonfrequent itemsets
		while(it.hasNext()){
			Map.Entry<List, Integer> pair = it.next();
			if(pair.getValue() < min_sup){
				nonfrequent.add(pair.getKey());
			}
		}

		//delete nonfrequent itemsets from C
		for(List<String> itemset : nonfrequent){
			C.remove(itemset);
		}
	}

	public static Map<List, Integer> first_scan(String filename){
		Map<List, Integer> C = new HashMap<List, Integer>();
		try{
			File f = new File(filename);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line = "";

			while((line=b.readLine()) != null){
				String[] split_line = line.split(", ");
				split_line = delete_repetition(split_line);

				//update table of candidate itemsets(C)
				for(String s : split_line){
					ArrayList<String> key = new ArrayList<String>();
					key.add(s);
					if(C.containsKey(key)){
						C.put(key, C.get(key)+1);
					}else{
						C.put(key, 1);
					}
				}
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return C;	
	}

	public static boolean canUnite(List<String> s1, List<String> s2){
		if(s1.size()!=s2.size()){
			System.out.println("[error] wrong length.");
			return false;
		}
		for(int i=0; i<s1.size()-1; i++){
			if(s1.get(i)!=s2.get(i)){
				return false;
			}
		}
		if(s1.get(s1.size()-1)==s2.get(s2.size()-1)){
			return false;
		}
		return true;
	}

	public static List<List> unite(List<String> s1, List<String> s2){
		List<String> n1 = new ArrayList<String>(s1);
		List<String> n2 = new ArrayList<String>(s2);
		n1.add(s2.get(s1.size()-1));
		n2.add(s1.get(s2.size()-1));
		List<List> n = new ArrayList<List>();
		n.add(n1);
		n.add(n2);
		return n;
	}

	public static List<List> genCandidate(Map<List, Integer> L){
		List<List> candidates = new ArrayList<List>();
		int itemset_len = L.keySet().iterator().next().size();
		int key_len = L.size();
		System.out.println(key_len+" keys with length of itemset = "+itemset_len);
		
		List<List> keys = new ArrayList<List>();
		keys.addAll(L.keySet());
		
		for(int i=0; i<key_len-1; i++){
			for(int j=i+1; j<key_len; j++){
				if(canUnite(keys.get(i), keys.get(j))){
					candidates.addAll(unite(keys.get(i), keys.get(j)));
				}
			}
		}
		return candidates;
	}

	public static List<List> gen_subset(List<String> itemset){
		int n = itemset.size();
		List<List> subsets = new ArrayList<List>();

		for(int i=0; i<(1<<n); i++){
			List<String> tmp = new ArrayList<String>();
			for(int j=0; j<n; j++){
				if((i&(1<<j)) > 0){
					tmp.add(itemset.get(j));
				}
			}
			subsets.add(tmp);
		}

		//delete null and full subset
		Iterator<List> it = subsets.iterator();
		while(it.hasNext()){
			List<String> i = it.next();
			if(i.isEmpty()){
				it.remove();
			}
		}
		subsets.remove(subsets.size()-1);
		return subsets;
	}

	public static void rm_c_with_infrequent_subset(List<Map<List, Integer>> Ls, List<List> candidates){
		Iterator<List> it = candidates.iterator();
		while(it.hasNext()){
			List<List> subsets = gen_subset(it.next());
			Iterator<List> sit = subsets.iterator();
			while(sit.hasNext()){
				List<String> subset = sit.next();
				Map<List, Integer> L = Ls.get(subset.size()-1);
				if(!L.containsKey(subset)){
					it.remove();
				}
			}
		}
	}

	public static Map<List, Integer> cnt_support(String filename, List<List> candidates){
		//return: table w/ support count
		Map<List, Integer> C = new HashMap<List, Integer>();
		try{
			File f = new File(filename);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line = "";

			while((line=b.readLine()) != null){
				Iterator<List> it = candidates.iterator();
				while(it.hasNext()){
					List<String> candidate = it.next();
					StringBuilder sb = new StringBuilder();
					sb.append(".*");
					for(String i : candidate){
						sb.append(i);
						sb.append(".*");
					}
					String reg = sb.toString();

					if(line.matches(reg)){
						if(C.containsKey(candidate)){
							C.put(candidate, C.get(candidate)+1);
						}else{
							C.put(candidate, 1);
						}
					}
				}
				
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		return C;
	}

	public static long performance(){
		Runtime runtime = Runtime.getRuntime();
		runtime.gc();
		long memory = runtime.totalMemory() - runtime.freeMemory();
		return memory;
	}

	public static void main(String args[]){
		//args = [min_sup]
		long startTime = System.currentTimeMillis();
		System.out.println("[Memory] " + performance());

		int min_sup = Integer.parseInt(args[0]);
		String filename = "Dataset/D100kT10N1k.txt";
		List<List> candidates = new ArrayList<List>();	//C(2)~C(n)
		List<Map<List, Integer>> all_L = new ArrayList<Map<List, Integer>>();	//L(1)~L(n)

		Map<List, Integer> C1 = first_scan(filename);
		candidate2frequent(C1, min_sup);
		all_L.add(C1);
		int i=0;
		while(true){
			System.out.println(i++);
			candidates.add(genCandidate(all_L.get(all_L.size()-1)));
			System.out.println("L size: " + all_L.size());
			rm_c_with_infrequent_subset(all_L, candidates.get(candidates.size()-1));
			//System.out.println(candidates.get(candidates.size()-1));
			if(candidates.get(candidates.size()-1).isEmpty()){
				break;
			}
			Map<List, Integer> new_C = cnt_support(filename, candidates.get(candidates.size()-1));
			candidate2frequent(new_C, min_sup);
			all_L.add(new_C);

		}
		long stopTime = System.currentTimeMillis();
		System.out.println("[Memory] " + performance());
		System.out.println("[Time] " + (stopTime-startTime));

	}
}