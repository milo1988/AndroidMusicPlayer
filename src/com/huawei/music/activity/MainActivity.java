package com.huawei.music.activity;


import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.audiofx.AudioEffect;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore.Audio;
import android.provider.MediaStore.Audio.AudioColumns;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import co.mobiwise.library.MusicPlayerView;
import static com.huawei.music.Constants.*;


import com.huawei.music.adapter.PagerAdapter;
import com.huawei.music.fragment.BottomActionBarFragment;
import com.huawei.music.fragment.grid.AlbumsFragment;
import com.huawei.music.fragment.grid.ArtistsFragment;
import com.huawei.music.fragment.list.GenresFragment;
import com.huawei.music.fragment.list.PlaylistsFragment;
import com.huawei.music.fragment.list.RecentlyAddedFragment;
import com.huawei.music.fragment.list.SongsFragment;
import com.huawei.music.service.ApolloService;
import com.huawei.music.service.ServiceToken;
import com.huawei.music.utils.MusicUtils;
import com.huawei.music.IApolloService;
import com.huawei.music.R;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelSlideListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout.PanelState;
import com.viewpagerindicator.TabPageIndicator;

public class MainActivity extends FragmentActivity {
    private static final String TAG = "MainActivity";

    private SlidingUpPanelLayout mLayout;
  
    BottomActionBarFragment mBActionbar;
    
    private ServiceToken mToken;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY); 
        // Scan for music
        setContentView(R.layout.activity_main);
        
//        setSupportActionBar((Toolbar) findViewById(R.id.main_toolbar));
        
        mBActionbar = (BottomActionBarFragment) getSupportFragmentManager().findFragmentById(R.id.bottomactionbar_new);
        mBActionbar.setUpQueueSwitch(this);
        
        mLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        mLayout.setAnchorPoint(0.0f);
        mLayout.addPanelSlideListener(new PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {
              if (slideOffset < 0.2) {
            	  //must FEATURE_ACTION_BAR_OVERLAY 
               	 mBActionbar.onCollapsed();
               	if (!getActionBar().isShowing()) {
                    getActionBar().show();
                }
               } else {
                   mBActionbar.onExpanded();
                   if (getActionBar().isShowing()) {
                       getActionBar().hide();
                   }
               }
                Log.i(TAG, "onPanelSlide, offset " + slideOffset);
            }

            @Override
            public void onPanelExpanded(View panel) {
                Log.i(TAG, "onPanelExpanded");            
            }

            @Override
            public void onPanelCollapsed(View panel) {
                Log.i(TAG, "onPanelCollapsed");
            	
            }

            @Override
            public void onPanelAnchored(View panel) {
                Log.i(TAG, "onPanelAnchored");
            }

            @Override
            public void onPanelHidden(View panel) {
                Log.i(TAG, "onPanelHidden");
            }
        });
        
        
      
        
        
//        mLayout.setFadeOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mLayout.setPanelState(PanelState.COLLAPSED);
//            }
//        });
        initPager();
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_top, menu);
        return true;
    }


    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_search:
				onSearchRequested();
				break;
	
			case R.id.action_settings:
				startActivityForResult(new Intent(this, SettingsActivity.class), 0);
				break;
	
//			case R.id.action_eqalizer:
//				final Intent intent = new Intent(
//						AudioEffect.ACTION_DISPLAY_AUDIO_EFFECT_CONTROL_PANEL);
//				if (getPackageManager().resolveActivity(intent, 0) == null) {
//					startActivity(new Intent(this, SimpleEq.class));
//				} else {
//					intent.putExtra(AudioEffect.EXTRA_AUDIO_SESSION,
//							MusicUtils.getCurrentAudioId());
//					startActivity(intent);
//				}
//				break;
	
			case R.id.action_shuffle_all:
				 shuffleAll();
				break;
	
//			case R.id.action_toggle: {
//				if (mLayout != null) {
//					if (mLayout.getPanelState() != PanelState.HIDDEN) {
//						mLayout.setPanelState(PanelState.HIDDEN);
//						item.setTitle("action_show");
//					} else {
//						mLayout.setPanelState(PanelState.COLLAPSED);
//						item.setTitle("action_hide");
//					}
//				}
//				return true;
//			}
		
		}
		return super.onOptionsItemSelected(item);
	}

    
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
    	Intent i = getBaseContext().getPackageManager().getLaunchIntentForPackage( getBaseContext().getPackageName() );
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		startActivity(i);
    }
    
    @Override
    public void onBackPressed() {
        if (mLayout != null &&
                (mLayout.getPanelState() == PanelState.EXPANDED || mLayout.getPanelState() == PanelState.ANCHORED)) {
            mLayout.setPanelState(PanelState.COLLAPSED);
        } else {
            super.onBackPressed();
        }
    }
    
    
    
    /**
     * Initiate ViewPager and PagerAdapter
     */
    public void initPager() {
    	
    
        // Initiate PagerAdapter
        PagerAdapter mPagerAdapter = new PagerAdapter(getSupportFragmentManager());

        //Get tab visibility preferences
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> defaults = new HashSet<String>(Arrays.asList(
        		getResources().getStringArray(R.array.tab_titles)
        	));
        Set<String> tabs_set = sp.getStringSet(TABS_ENABLED,defaults);
        //if its empty fill reset it to full defaults
        	//stops app from crashing when no tabs are shown
        	//TODO:rewrite activity to not crash when no tabs are chosen to show
        if(tabs_set.size()==0){
        	tabs_set = defaults;
        }
        
        
        Fragment fragment = null;
   	   
        //Only show tabs that were set in preferences
        // Recently added tracks
        if(tabs_set.contains(getResources().getString(R.string.tab_recent))){
        	fragment = new RecentlyAddedFragment();
         	Bundle args = new Bundle();  
            args.putString("title", getResources().getString(R.string.tab_recent));  
            fragment.setArguments(args);  
        	mPagerAdapter.addFragment(fragment);
        }
        
        //songs
        if(tabs_set.contains(getResources().getString(R.string.tab_songs))){
       	 fragment = new SongsFragment();
      	     Bundle args = new Bundle();  
            args.putString("title", getResources().getString(R.string.tab_songs));  
            fragment.setArguments(args);  
     	    mPagerAdapter.addFragment(fragment);
        }
   	     
        // Artists
		if(tabs_set.contains(getResources().getString(R.string.tab_artists))){
			fragment = new ArtistsFragment();
         	Bundle args = new Bundle();  
            args.putString("title", getResources().getString(R.string.tab_artists));  
            fragment.setArguments(args);  
        	mPagerAdapter.addFragment(fragment);
		  }
		
        // Albums
        if(tabs_set.contains(getResources().getString(R.string.tab_albums))){
        	fragment = new AlbumsFragment();
         	Bundle args = new Bundle();  
            args.putString("title", getResources().getString(R.string.tab_albums));  
            fragment.setArguments(args);  
        	mPagerAdapter.addFragment(fragment);
        }
		
        // // Playlists
        if(tabs_set.contains(getResources().getString(R.string.tab_playlists))){
        	fragment = new PlaylistsFragment();
         	Bundle args = new Bundle();  
            args.putString("title", getResources().getString(R.string.tab_playlists));  
            fragment.setArguments(args);  
        	mPagerAdapter.addFragment(fragment);
        }
        // // Genres
        if(tabs_set.contains(getResources().getString(R.string.tab_genres))){
        	fragment = new GenresFragment();
         	Bundle args = new Bundle();  
            args.putString("title", getResources().getString(R.string.tab_genres));  
            fragment.setArguments(args);  
        	mPagerAdapter.addFragment(fragment);
        }

        // Initiate ViewPager
        ViewPager mViewPager = (ViewPager)findViewById(R.id.pager);
        mViewPager.setPageMargin(getResources().getInteger(R.integer.viewpager_margin_width));
//        mViewPager.setPageMarginDrawable(R.drawable.viewpager_margin);
        mViewPager.setOffscreenPageLimit(mPagerAdapter.getCount());
        mViewPager.setAdapter(mPagerAdapter);
        mViewPager.setCurrentItem(0);

        TabPageIndicator indicator = (TabPageIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);
    }
    
    
    @Override
    protected void onStart() {
        // Bind to Service
        mToken = MusicUtils.bindToService(this, new ServiceConnection(){

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				// TODO Auto-generated method stub
				 Log.i(TAG, "onServiceConnected");
				 MusicUtils.mService = IApolloService.Stub.asInterface(service);
			}

			@Override
			public void onServiceDisconnected(ComponentName name) {
				// TODO Auto-generated method stub
				 Log.i(TAG, "onServiceDisconnected");
				 MusicUtils.mService = null;
			}
        	
        });
        super.onStart();
    }

    @Override
    protected void onStop() {
        // Unbind
        if (MusicUtils.mService != null)
            MusicUtils.unbindFromService(mToken);
        super.onStop();
    }
    
    
    /**
     * Shuffle all the tracks
     */
    public void shuffleAll() {
        Uri uri = Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection = new String[] {
            BaseColumns._ID
        };
        String selection = AudioColumns.IS_MUSIC + "=1";
        String sortOrder = "RANDOM()";
        Cursor cursor = MusicUtils.query(this, uri, projection, selection, null, sortOrder);
        if (cursor != null) {
            MusicUtils.shuffleAll(this, cursor);
            cursor.close();
            cursor = null;
        }
    }
    
}
