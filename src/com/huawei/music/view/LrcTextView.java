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


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.TextView;


public class LrcTextView extends TextView {
    private List<LrcInfo> mWordsList = new ArrayList<LrcInfo>();
    private Paint mLoseFocusPaint;
    private Paint mOnFocusePaint;
    private float mX = 0;
    private float mMiddleY = 0;
    private float mY = 0;
    private static final int DY = 50;
    private int mIndex = 0;

    public LrcTextView(Context context) throws IOException {
        super(context);
        init();
    }

    public LrcTextView(Context context, AttributeSet attrs) throws IOException {
        super(context, attrs);
        init();
    }

    public LrcTextView(Context context, AttributeSet attrs, int defStyle)
            throws IOException {
        super(context, attrs, defStyle);
        init();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if(mWordsList.size() > 0 && mIndex < mWordsList.size()){
        	 canvas.drawColor(Color.BLACK);
             //now playing
             canvas.drawText(mWordsList.get(mIndex).getLrcLine(), mX, mMiddleY, mOnFocusePaint);

             //already played
             int alphaValue = 15;
             float tempY = mMiddleY;
             for (int i = mIndex - 1; i >= 0; i--) {
                 tempY -= DY;
                 if (tempY < 0) {
                     break;
                 }
                 mLoseFocusPaint.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
                 canvas.drawText(mWordsList.get(i).getLrcLine(), mX, tempY, mLoseFocusPaint);
                 alphaValue += 15;
             }
             
             //to be play
             alphaValue = 15;
             tempY = mMiddleY;
             for (int i = mIndex + 1, len = mWordsList.size(); i < len; i++) {
                 tempY += DY;
                 if (tempY > mY) {
                     break;
                 }
                 mLoseFocusPaint.setColor(Color.argb(255 - alphaValue, 245, 245, 245));
                 canvas.drawText(mWordsList.get(i).getLrcLine(), mX, tempY, mLoseFocusPaint);
                 alphaValue += 15;
             }
        }
       
       
    }

    @Override
    protected void onSizeChanged(int w, int h, int ow, int oh) {
        super.onSizeChanged(w, h, ow, oh);

        mX = w * 0.5f;
        mY = h;
        mMiddleY = h * 0.4f;
    }

    @SuppressLint("SdCardPath")
    private void init() throws IOException {
        setFocusable(true);

        mLoseFocusPaint = new Paint();
        mLoseFocusPaint.setAntiAlias(true);
        mLoseFocusPaint.setTextSize(30);
        mLoseFocusPaint.setColor(Color.WHITE);
        mLoseFocusPaint.setTypeface(Typeface.SERIF);
        mLoseFocusPaint.setTextAlign(Paint.Align.CENTER);

        mOnFocusePaint = new Paint();
        mOnFocusePaint.setAntiAlias(true);
        mOnFocusePaint.setColor(Color.YELLOW);
        mOnFocusePaint.setTextSize(40);
        mOnFocusePaint.setTypeface(Typeface.SANS_SERIF);
        mOnFocusePaint.setTextAlign(Paint.Align.CENTER);
    }

	public void setWordsList(List<LrcInfo> mWordsList) {
		this.mWordsList = mWordsList;
	}
	
	
	public void update(long pos, String lrcPath){
		LrcHandle lrcHandle = new LrcHandle();
	    lrcHandle.readLRC(lrcPath);
	    getLrcIndexByBeginTime(pos,lrcHandle.getLrcList());
		postInvalidate();
	}


	private void getLrcIndexByBeginTime(long pos, List<LrcInfo> lrcList) {
		// TODO Auto-generated method stub
		setWordsList(lrcList);
		for(int i = 0; i< lrcList.size(); i++){
//			Log.i("LrcTextView", lrcList.get(i).getLrcLine());
			if(i < lrcList.size() -1 && pos >= lrcList.get(i).getBegin() && pos < lrcList.get(i+1).getBegin()){
				mIndex = i;
			}
		}

	}
	
	
	class LrcHandle {	
		
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
//				hashMap.put("0", "没有歌词文件，赶紧去下载");
			} catch (IOException e) {
				e.printStackTrace();
//				hashMap.put("0", "没有读取到歌词");
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
    
    
}