package DBSCAN;

import java.io.*;
import java.util.*;
import java.lang.Math;

public class Util{
	public static double distance(Data data1, Data data2){
		double d1 = data1.get_attr(1)-data2.get_attr(1);
		double d2 = data1.get_attr(2)-data2.get_attr(2);
		return Math.sqrt(d1*d1+d2*d2);
	}
}