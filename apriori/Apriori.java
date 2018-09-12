import java.util.*;
import java.io.*;
import java.lang.StringBuilder;

public class Apriori{

	public static void candidate2frequent(Map<List<String>, Float> C, float min_sup){
		Iterator<Map.Entry<List<String>, Float>> it = C.entrySet().iterator();

		//find all nonfrequent itemsets
		while(it.hasNext()){
			Map.Entry<List<String>, Float> pair = it.next();
			if(pair.getValue() < min_sup){
				it.remove();
			}
		}
	}

	public static void supcnt2sup(Map<List<String>, Float> C, int D){
		//turn the support count in the map into support
		for(List<String> k : C.keySet()){
			C.put(k, C.get(k)/(float)D);
		}
	}

	public static Map<List<String>, Float> first_scan(String filename){
		//return value: 
		Map<List<String>, Float> C = new HashMap<List<String>, Float>();
		int cnt = 0;
		try{
			File f = new File(filename);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line = "";

			while((line=b.readLine()) != null){
				cnt++;
				String[] split_line = line.split(", ");
				split_line = Util.delete_repetition(split_line);

				//update table of candidate itemsets(C)
				for(String s : split_line){
					ArrayList<String> key = new ArrayList<String>();
					key.add(s);
					if(C.containsKey(key)){
						C.put(key, C.get(key)+1);
					}else{
						C.put(key, 1.0f);
					}
				}
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		supcnt2sup(C, cnt);
		return C;	
	}

	public static boolean canUnite(List<String> s1, List<String> s2){
		if(s1.size()!=s2.size()) return false;
		List<String> s1_copy = new ArrayList<String>(s1);
		List<String> s2_copy = new ArrayList<String>(s2);
		s1_copy.removeAll(s2);
		s2_copy.removeAll(s1);
		if(s1_copy.size()==1 && s2_copy.size()==1 && !s1_copy.get(0).equals(s2_copy.get(0))){
			return true;
		}
		return false;
	}

	public static List<String> unite(List<String> s1, List<String> s2){
		List<String> s2_copy = new ArrayList<String>(s2);
		s2_copy.removeAll(s1);
		String diff_s2 = s2_copy.get(0);
		
		List<String> new_s = new ArrayList<String>(s1);
		new_s.add(diff_s2);
		new_s = Util.delete_repetition(new_s);
		Util.sort(new_s);
		if(new_s.size()-s1.size() != 1) System.out.println("[error] wrong length.");
		return new_s;
	}

	public static List<List<String>> genCandidate(Map<List<String>, Float> L){
		List<List<String>> candidates = new ArrayList<List<String>>();
		int itemset_len = L.keySet().iterator().next().size();
		int key_num = L.size();
		//System.out.println(key_num+" keys with length of itemset = "+itemset_len);
		
		List<List<String>> keys = new ArrayList<List<String>>(L.keySet());
		
		for(int i=0; i<key_num-1; i++){
			for(int j=i+1; j<key_num; j++){
				if(canUnite(keys.get(i), keys.get(j))){
					candidates.add(unite(keys.get(i), keys.get(j)));
				}
			}
		}
		//System.out.println("# of candidate: " + candidates.size());
		return Util.delete_repetition_l(candidates);
	}

	public static List<List<String>> gen_subset(List<String> itemset){
		//func: find all possible combinations of the itemset
		//return value: all possible combinations of the itemset
		int n = itemset.size();
		List<List<String>> subsets = new ArrayList<List<String>>();

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
		Iterator<List<String>> it = subsets.iterator();
		while(it.hasNext()){
			List<String> i = it.next();
			if(i.isEmpty() || i.size()==n){
				it.remove();
			}else{
		}
		}
		return subsets;
	}

	public static void rm_c_with_infrequent_subset(List<Map<List<String>, Float>> Ls, List<List<String>> candidates){
		Iterator<List<String>> it = candidates.iterator();
		while(it.hasNext()){
			List<List<String>> subsets = gen_subset(it.next());
			Iterator<List<String>> sit = subsets.iterator();
			while(sit.hasNext()){
				List<String> subset = sit.next();
				Map<List<String>, Float> L = Ls.get(subset.size()-1);
				if(!L.containsKey(subset)){
					it.remove();
					break;
				}
			}
		}
		//System.out.println("# of candidate after deleting infrequent subset: " + candidates.size());
	}

	public static boolean string_contains_list(List<String> strs, String line){
		List<String> split = Arrays.asList(line.split(", "));
		for(String str : strs){
			if(split.contains(str)) continue;
			else return false;
		}
		return true;
	}

	public static Map<List<String>, Float> cnt_support(String filename, List<List<String>> candidates){
		//return: table w/ support count
		Map<List<String>, Float> C = new HashMap<List<String>, Float>();
		int cnt = 0;
		try{
			File f = new File(filename);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String line = "";

			while((line=b.readLine()) != null){
				cnt++;
				Iterator<List<String>> it = candidates.iterator();
				while(it.hasNext()){
					List<String> candidate = it.next();
					if(string_contains_list(candidate, line)){
						if(C.containsKey(candidate)){
							C.put(candidate, C.get(candidate)+1.0f);
						}else{
							C.put(candidate, 1.0f);
						}
					}
				}
				
			}
		}catch(IOException ex){
			ex.printStackTrace();
		}
		supcnt2sup(C, cnt);
		return C;
	}

	public static void print_frequent_itemset(List<Map<List<String>, Float>> all_L){
		Iterator<Map<List<String>, Float>> it = all_L.iterator();
		int amount_fitemset = 0;
		while(it.hasNext()){
			System.out.println("\nFrequent itemsets.");
			Map<List<String>, Float> L = it.next();
			Iterator<Map.Entry<List<String>, Float>> pairit = L.entrySet().iterator();
			while(pairit.hasNext()){
				Map.Entry<List<String>, Float> pair = pairit.next();
				System.out.println("\t"+pair.getKey()+"\t"+pair.getValue());
			}
			amount_fitemset += (L.size());
		}
		System.out.println("\ntotal count of frequent itemset: " + amount_fitemset);
		//return amount_fitemset;
	}

	public static int count_itemset(List<Map<List<String>, Float>> all_L){
		Iterator<Map<List<String>, Float>> it = all_L.iterator();
		int amount_fitemset = 0;
		while(it.hasNext()){
			Map<List<String>, Float> L = it.next();
			amount_fitemset += (L.size());
		}
		return amount_fitemset;
	}

	public static int find_association_rule(List<Map<List<String>, Float>> all_L, float min_cfd){
		List<List<List<String>>> rules = new ArrayList<List<List<String>>>();
		for(Map<List<String>, Float> L : all_L){
			Set<List<String>> itemsets = L.keySet();
			for(List<String> itemset : itemsets){
				if(itemset.size()==1) continue;
				List<List<String>> subsets = gen_subset(itemset);
				for(List<String> subset : subsets){
					if( ((L.get(itemset)) / (all_L.get(subset.size()-1).get(subset))) > min_cfd ){
						// it's association rule
						List<List<String>> rule = new ArrayList<List<String>>();
						List<String> subset_rest = new ArrayList<String>(itemset);
						subset_rest.removeAll(subset);
						rule.add(subset);
						rule.add(subset_rest);
						rules.add(rule);
					}
				}
			}
		}
		return rules.size();
	}

	public static void Apriori(float min_sup, String filename){
		long startTime = System.currentTimeMillis();
		long startMemory = Util.performance();

		List<List<List<String>>> candidates = new ArrayList<List<List<String>>>();	//C(2)~C(n)
		List<Map<List<String>, Float>> all_L = new ArrayList<Map<List<String>, Float>>();	//L(1)~L(n)
		
		Map<List<String>, Float> C1 = first_scan(filename);
		candidate2frequent(C1, min_sup);
		all_L.add(C1);
		int i=0;
		while(true){
			if(all_L.get(all_L.size()-1).isEmpty()) break;
			candidates.add(genCandidate(all_L.get(all_L.size()-1)));
			rm_c_with_infrequent_subset(all_L, candidates.get(candidates.size()-1));
			if(candidates.get(candidates.size()-1).isEmpty()) break;
			Map<List<String>, Float> new_C = cnt_support(filename, candidates.get(candidates.size()-1));
			candidate2frequent(new_C, min_sup);
			//System.out.println(new_C.size());
			all_L.add(new_C);
		}

		System.out.println("amount of frequent itemset: " + count_itemset(all_L));
		long stopTime = System.currentTimeMillis();
		long stopMemory = Util.performance();
		System.out.println("[Memory] " + (stopMemory-startMemory) + " bytes");
		System.out.println("[Time] " + (stopTime-startTime) + " ms");

		//// association rule
		startTime = System.currentTimeMillis();
		startMemory = Util.performance();
		int rule_cnt = 0;
		for(float k=0.1f; k<1.1; k+=0.1f){
			//System.out.println("Amount of association rule: " + find_association_rule(all_L, k));
			rule_cnt += find_association_rule(all_L, k);
		}
		System.out.println("Amount of association rule: " + rule_cnt);
		stopTime = System.currentTimeMillis();
		stopMemory = Util.performance();
		System.out.println("[Memory] [association rule] " + (stopMemory-startMemory) + " bytes");
		System.out.println("[Time] [association rule] " + (stopTime-startTime) + " ms");
	}

	public static void main(String args[]){
		//D100kT10N1k.txt D10kT10N1k.txt  D1kT10N500.txt  Mushroom.txt
		try{
			PrintStream out = new PrintStream(new FileOutputStream("result/D1kT10N500_result.txt"));
			System.setOut(out);
		}catch(IOException ex){
			ex.printStackTrace();
		}
		String filename = "Dataset/D1kT10N500.txt";
		System.out.println("File: " + filename);

		for(float i=0.01f; i>=0.001; i-=0.001){
			System.out.println("* min support: " + i);
			Apriori(i, filename);
		}
	}
}