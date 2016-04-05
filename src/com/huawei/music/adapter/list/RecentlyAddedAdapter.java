
package com.huawei.music.adapter.list;

import com.huawei.music.adapter.base.ListViewAdapter;
import com.huawei.music.utils.MusicUtils;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.MediaColumns;
import android.provider.MediaStore.Audio.AudioColumns;
import static com.huawei.music.Constants.TYPE_ALBUM;

public class RecentlyAddedAdapter extends ListViewAdapter {

    public RecentlyAddedAdapter(Context context, int layout, Cursor c, String[] from, int[] to,
            int flags) {
        super(context, layout, c, from, to, flags);
    }

    public void setupViewData( Cursor mCursor ){
    	mLineOneText = mCursor.getString(mCursor.getColumnIndexOrThrow(MediaColumns.TITLE));

    	mLineTwoText = mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.ARTIST));

        String albumName = mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.ALBUM));
        String albumId = mCursor.getString(mCursor.getColumnIndexOrThrow(AudioColumns.ALBUM_ID));
        mImageData = new String[]{ albumId , mLineTwoText, albumName };
        
        mPlayingId = MusicUtils.getCurrentAudioId();
        mCurrentId = mCursor.getLong(mCursor.getColumnIndexOrThrow(BaseColumns._ID));

        mListType = TYPE_ALBUM;
    	showContextEnabled = false;    	
    }
}
