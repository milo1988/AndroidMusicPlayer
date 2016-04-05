package com.huawei.music.view;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.huawei.music.bean.LrcInfo;

public class LrcHandle {	
	
	private List<LrcInfo> lrcList = new ArrayList<LrcInfo>();
	
	// read the LRC
	public void readLRC(String path) {
		lrcList.clear();
		File file = new File(path);
		try {
			FileInputStream fileInputStream = new FileInputStream(file);
			InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "utf-8");
			BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
			String s = "";
			while ((s = bufferedReader.readLine()) != null) {
				int timeBegin = addTimeToList(s);
				if ((s.indexOf("[ar:") != -1) || (s.indexOf("[ti:") != -1)|| (s.indexOf("[by:") != -1)) {
					s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
				} else {
					String ss = s.substring(s.indexOf("["), s.indexOf("]") + 1);
					s = s.replace(ss, "");
				}
				lrcList.add(new LrcInfo(timeBegin, s));
				
			}
			bufferedReader.close();
			inputStreamReader.close();
			fileInputStream.close();
			
			// sort by key
			Collections.sort(lrcList, new Comparator<LrcInfo>(){
				@Override
				public int compare(LrcInfo lhs, LrcInfo rhs) {
					// TODO Auto-generated method stub
					return (int) (lhs.getBegin() - rhs.getBegin());
				}
				
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
//			hashMap.put("0", "没有歌词文件，赶紧去下载");
		} catch (IOException e) {
			e.printStackTrace();
//			hashMap.put("0", "没有读取到歌词");
		}
	}

	// 分离出时间
	private int timeHandler(String string) {
		string = string.replace(".", ":");
		String timeData[] = string.split(":");
		// 分离出分、秒并转换为整型
		int minute = Integer.parseInt(timeData[0]);
		int second = Integer.parseInt(timeData[1]);
		int millisecond = Integer.parseInt(timeData[2]);
		// 计算上一行与下一行的时间转换为毫秒数
		int currentTime = (minute * 60 + second) * 1000 + millisecond * 10;
		return currentTime;
	}

	private int addTimeToList(String string) {
		Matcher matcher = Pattern.compile("\\[\\d{1,2}:\\d{1,2}([\\.:]\\d{1,2})?\\]").matcher(string);
		if (matcher.find()) {
			String str = matcher.group();
			return timeHandler(str.substring(1, str.length() - 1));
		} else {
			return 0;
		}
	}

	public List getLrcList() {
		return lrcList;
	}

}



