package c45;

import java.io.*;
import java.util.*;
//import attribute.*;

public class Util{
	public static boolean check_attributes_have_same_amount_data(List<Attribute> attrs){
		int len = 0;
		for(Attribute attr : attrs){
			if(attr.data_length() == 0){
				System.out.println("[error] no data.");
				return false;
			}
			if(len==0){
				len = attr.data_length();
			}else{
				if(attr.data_length() != len){
					System.out.println("[error] data in different length.");
					return false;
				}
			}
		}
		return true;
	}

	public static void all_permutation(List<int[]> all_combination, int arr[], int data[], int start, int end, int index, int r){
		if(index == r){
			int tmp[] = new int[r];
			System.arraycopy(data, 0, tmp, 0, r);
			all_combination.add(tmp);
			return;
		}

		for(int i=start; i<=end && end-i+1>=r-index; i++){
			data[index] = arr[i];
			all_permutation(all_combination, arr, data, i+1, end, index+1, r);
		}
	}

	public static void gen_testing_index(List<int[]> all_combination, int r){
		int arr[] = new int[10];
		for(int i=0; i<arr.length; i++){
			arr[i] = i;
		}
		all_permutation(all_combination, arr, new int[r], 0, arr.length-1, 0, r);
	}

	public static int get_max_index(double[] arr){
		double max = 0;
		int max_index = 0;
		for(int i=0; i<arr.length; i++){
			if(arr[i]>max){
				max = arr[i];
				max_index = i;
			}
		}
		return max_index;
	}

	public static int get_max_index(int[] arr){
		int max = 0;
		int max_index = 0;
		for(int i=0; i<arr.length; i++){
			if(arr[i]>max){
				max = arr[i];
				max_index = i;
			}
		}
		return max_index;
	}

	public static void print_arr(int[] arr){
		for(int i=0; i<arr.length; i++){
			System.out.print(arr[i]+" ");
		}
		System.out.println();
	}

	public static void print_arr(double[] arr){
		for(int i=0; i<arr.length; i++){
			System.out.print(arr[i]+" ");
		}
		System.out.println();
	}
}
	