package com.huawei.music.utils;

import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MapUtil {
	public static Map<String, String> sortMapByKey(Map<String, String> oriMap) {
		if (oriMap == null || oriMap.isEmpty()) {
			return null;
		}
		
		Map<String, String> sortedMap = new TreeMap<String, String>(new Comparator<String>() {
			public int compare(String key1, String key2) {
				long intKey1 = 0, intKey2 = 0;
				try {
					intKey1 = getLong(key1);
					intKey2 = getLong(key2);
				} catch (Exception e) {
					intKey1 = 0; 
					intKey2 = 0;
				}
				return (int) (intKey1 - intKey2);
			}});
		
		sortedMap.putAll(oriMap);
		return sortedMap;
	}
	
	
	private static long getLong(String str) {
		long i = 0;
		try {
			Pattern p = Pattern.compile("^\\d+");
			Matcher m = p.matcher(str);
			if (m.find()) {
				i = Long.valueOf(m.group());
			}
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		return i;
	}
	
	
	public static String getLrcByBeginTime(long time, TreeMap<String,String> map){
		Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
		long beforeBeginTime = 0;
		String lrcLine = "";
		
		for (String key : map.keySet()) {
			   System.out.println("key= "+ key + " and value= " + map.get(key));
			  }
		
		while (it.hasNext()) {
			   Map.Entry<String, String> entry = it.next();
			   if(time >= beforeBeginTime && time < Long.valueOf(entry.getKey())){
				   return lrcLine;
			   }
			   beforeBeginTime = Long.valueOf(entry.getKey());
			   lrcLine = entry.getValue();
			   System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());
			  }
		

		return null;
		
	}
}

