package com.huawei.music.utils;

import android.util.Log;

public class CallStack {
	private static boolean DEBUG = false;

    public static void printCallStatck() {
        StackTraceElement[] stackElements = new Throwable().getStackTrace();
        if (stackElements != null && DEBUG) {
            for (int i = 0; i < stackElements.length; i++) {
            	Log.i("CallStack", stackElements[i].getClassName() + stackElements[i].getFileName() + stackElements[i].getLineNumber() + stackElements[i].getMethodName());
            }
        }
    }
    
}