
package com.huawei.music.menu;

import static com.huawei.music.Constants.INTENT_ADD_TO_PLAYLIST;
import static com.huawei.music.Constants.INTENT_CREATE_PLAYLIST;
import static com.huawei.music.Constants.INTENT_PLAYLIST_LIST;
import static com.huawei.music.Constants.PLAYLIST_NEW;
import static com.huawei.music.Constants.PLAYLIST_QUEUE;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.huawei.music.utils.MusicUtils;
import com.huawei.music.R;
public class PlaylistPicker extends Activity implements DialogInterface.OnClickListener,
        DialogInterface.OnCancelListener {

    private AlertDialog mPlayListPickerDialog;

    List<Map<String, String>> mAllPlayLists = new ArrayList<Map<String, String>>();

    List<String> mPlayListNames = new ArrayList<String>();

    long[] mList = new long[] {};

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {

        long listId = Long.valueOf(mAllPlayLists.get(which).get("id"));
        String listName = mAllPlayLists.get(which).get("name");
        Intent intent;
        boolean mCreateShortcut = getIntent().getAction().equals(Intent.ACTION_CREATE_SHORTCUT);

        if (mCreateShortcut) {
            final Intent shortcut = new Intent();
//             shortcut.setAction(INTENT_PLAY_SHORTCUT);
            shortcut.putExtra("id", listId);

            intent = new Intent();
            intent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcut);
            intent.putExtra(Intent.EXTRA_SHORTCUT_NAME, listName);
            intent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                    Intent.ShortcutIconResource.fromContext(this, R.drawable.ic_launcher));
            setResult(RESULT_OK, intent);
        } else {
            if (listId >= 0) {
                MusicUtils.addToPlaylist(this, mList, listId);
            } else if (listId == PLAYLIST_QUEUE) {
                MusicUtils.addToCurrentPlaylist(this, mList);
            } else if (listId == PLAYLIST_NEW) {
                intent = new Intent(INTENT_CREATE_PLAYLIST);
                intent.putExtra(INTENT_PLAYLIST_LIST, mList);
                startActivity(intent);
            }
        }
        finish();
    }

    @Override
    public void onCreate(Bundle icicle) {

        super.onCreate(icicle);
        setContentView(new LinearLayout(this));
        Log.i("PlaylistPicker", " onCreate Action " +  getIntent().getAction());
        if (getIntent().getAction() != null) {

            if (INTENT_ADD_TO_PLAYLIST.equals(getIntent().getAction())
                    && getIntent().getLongArrayExtra(INTENT_PLAYLIST_LIST) != null) {

                MusicUtils.makePlaylistList(this, false, mAllPlayLists);
                mList = getIntent().getLongArrayExtra(INTENT_PLAYLIST_LIST);
                for (int i = 0; i < mAllPlayLists.size(); i++) {
                    mPlayListNames.add(mAllPlayLists.get(i).get("name"));
                }
                mPlayListPickerDialog = new AlertDialog.Builder(this)
                        .setTitle(R.string.add_to_playlist)
                        .setItems(mPlayListNames.toArray(new CharSequence[mPlayListNames.size()]),
                                this).setOnCancelListener(this).show();
            } else if (getIntent().getAction().equals(Intent.ACTION_CREATE_SHORTCUT)) {
                MusicUtils.makePlaylistList(this, true, mAllPlayLists);
                for (int i = 0; i < mAllPlayLists.size(); i++) {
                    mPlayListNames.add(mAllPlayLists.get(i).get("name"));
                }
                mPlayListPickerDialog = new AlertDialog.Builder(this)
                        .setItems(mPlayListNames.toArray(new CharSequence[mPlayListNames.size()]),
                                this).setOnCancelListener(this).show();
            }
        } else {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public void onPause() {

        if (mPlayListPickerDialog != null && mPlayListPickerDialog.isShowing()) {
            mPlayListPickerDialog.dismiss();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (mPlayListPickerDialog != null && !mPlayListPickerDialog.isShowing()) {
            mPlayListPickerDialog.show();
        }
    }

}
