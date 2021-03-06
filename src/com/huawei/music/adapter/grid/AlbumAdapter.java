
package com.huawei.music.adapter.grid;

import static com.huawei.music.Constants.TYPE_ALBUM;

import com.huawei.music.adapter.base.GridViewAdapter;
import com.huawei.music.utils.MusicUtils;

import android.content.Context;
import android.database.Cursor;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio.AlbumColumns;


public class AlbumAdapter extends GridViewAdapter {

    public AlbumAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    public void setupViewData(Cursor mCursor){
        
    	mLineOneText = mCursor.getString(mCursor.getColumnIndexOrThrow(AlbumColumns.ALBUM));
        mLineTwoText = mCursor.getString(mCursor.getColumnIndexOrThrow(AlbumColumns.ARTIST));     
        mGridType = TYPE_ALBUM;        
        mImageData =  new String[]{ mCursor.getString(mCursor.getColumnIndexOrThrow(BaseColumns._ID)) , mLineTwoText, mLineOneText };
        mPlayingId = MusicUtils.getCurrentAlbumId();
        mCurrentId = mCursor.getLong(mCursor.getColumnIndexOrThrow(BaseColumns._ID));
        
    }
}
