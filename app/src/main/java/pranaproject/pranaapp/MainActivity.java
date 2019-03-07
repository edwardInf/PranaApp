package pranaproject.pranaapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.appthemeengine.customizers.ATEActivityThemeCustomizer;
import pranaproject.pranaapp.MusicPlayer;
import pranaproject.pranaapp.R;
import pranaproject.pranaapp.activities.BaseActivity;
import pranaproject.pranaapp.activities.SearchActivity;
import pranaproject.pranaapp.adapters.OptionsAdapter;
import pranaproject.pranaapp.fragments.AlbumDetailFragment;
import pranaproject.pranaapp.fragments.AlbumFragment;
import pranaproject.pranaapp.fragments.ArtistDetailFragment;
import pranaproject.pranaapp.fragments.ArtistFragment;
import pranaproject.pranaapp.fragments.HomeFragment;
import pranaproject.pranaapp.fragments.ListaRepFragment;
import pranaproject.pranaapp.fragments.MainFragment;
import pranaproject.pranaapp.fragments.PlaylistFragment;
import pranaproject.pranaapp.fragments.QueueFragment;
import pranaproject.pranaapp.fragments.SearchFragmentB;
import pranaproject.pranaapp.fragments.SongsFragment;
import pranaproject.pranaapp.models.Option;
import pranaproject.pranaapp.permissions.Nammu;
import pranaproject.pranaapp.permissions.PermissionCallback;
import pranaproject.pranaapp.slidinguppanel.SlidingUpPanelLayout;
import pranaproject.pranaapp.utils.Constants;
import pranaproject.pranaapp.utils.Helpers;
import pranaproject.pranaapp.utils.NavigationUtils;
import pranaproject.pranaapp.utils.TimberUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends BaseActivity implements ATEActivityThemeCustomizer, AdapterView.OnItemClickListener{

    private static MainActivity sMainActivity;
    SlidingUpPanelLayout panelLayout;
    NavigationView navigationView;
    //TextView songtitle, songartist;
    ImageView albumart;
    String action;
    Map<String, Runnable> navigationMap = new HashMap<String, Runnable>();
    Handler navDrawerRunnable = new Handler();
    Runnable runnable;
    public ListView listMenuDraw,listMenuDrawMain;
    ListView lvHeaders,lvMain;
    static Context aContex;

    public static DrawerLayout mDrawerLayout;
    private boolean isDarkTheme;
    public static MainActivity getInstance() {
        return sMainActivity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        sMainActivity = this;
        action = getIntent().getAction();

        isDarkTheme = PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false);

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        navigationMap.put(Constants.NAVIGATE_LIBRARY, navigateLibrary);
        navigationMap.put(Constants.NAVIGATE_PLAYLIST, navigatePlaylist);

        navigationMap.put(Constants.NAVIGATE_ARTIST, navigateArtista);
        navigationMap.put(Constants.NAVIGATE_NOWPLAYING, navigateNowplaying);
        navigationMap.put(Constants.NAVIGATE_ALBUM, navigateAlbum);


        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        panelLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);

        lvHeaders = (ListView)findViewById(R.id.nav_view);
        lvMain = (ListView)findViewById(R.id.lvMain);

        initMenuLateral();
        setPanelSlideListeners(panelLayout);

        if (TimberUtils.isMarshmallow()) {
            checkPermissionAndThenLoad();
        } else {
            loadEverything();
        }

        addBackstackListener();

        if(Intent.ACTION_VIEW.equals(action)) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.clearQueue();
                    MusicPlayer.openFile(getIntent().getData().getPath());
                    MusicPlayer.playOrPause();
                    navigateNowplaying.run();
                }
            }, 350);
        }
    }

    private void loadEverything() {
        Runnable navigation = navigationMap.get(action);
        if (navigation != null) {
            navigation.run();

        } else {

            //showHome();
        }

        new initQuickControls().execute("");
    }

    private void checkPermissionAndThenLoad() {
        //check for permission
        if (Nammu.checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            loadEverything();
        } else {
            if (Nammu.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
                Snackbar.make(panelLayout, "SongsPro will need to read external storage to display songs on your device.",
                        Snackbar.LENGTH_INDEFINITE)
                        .setAction("OK", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Nammu.askForPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadstorageCallback);
                            }
                        }).show();
            } else {
                Nammu.askForPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE, permissionReadstorageCallback);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(GravityCompat.START);
                return true;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {

        if (panelLayout.isPanelExpanded())
            panelLayout.collapsePanel();
        else {
            super.onBackPressed();
        }

    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(final MenuItem menuItem) {
                        updatePosition(menuItem);
                        return true;

                    }
                });
    }

    List<Option> mMainOptions = new ArrayList<>();
    List<Option> mHeaderOptions = new ArrayList<>();
    private OptionsAdapter adapterHeaderOpts, adapterMainOpts;


    void initMenuLateral(){

        mHeaderOptions.add(new Option(R.drawable.img1, getString(R.string.all_songs)));
        mHeaderOptions.add(new Option(R.drawable.img2, getString(R.string.best_songs)));
        mHeaderOptions.add(new Option(R.drawable.item_playlist, getString(R.string.best_products)));
        adapterHeaderOpts = new OptionsAdapter(this, mHeaderOptions);

        mMainOptions.add(new Option(R.drawable.img10, getString(R.string.search)));
        mMainOptions.add(new Option(R.drawable.img11, getString(R.string.albums)));
        mMainOptions.add(new Option(R.drawable.img3, getString(R.string.artists)));
        mMainOptions.add(new Option(R.drawable.img4, getString(R.string.upload)));
        mMainOptions.add(new Option(R.drawable.img6, getString(R.string.type)));
        mMainOptions.add(new Option(R.drawable.img7, getString(R.string.playlist)));
        mMainOptions.add(new Option(R.drawable.img8, getString(R.string.folders)));
        adapterMainOpts = new OptionsAdapter(this, mMainOptions);

        lvHeaders.setAdapter(adapterHeaderOpts);
        lvMain.setAdapter(adapterMainOpts);

        lvHeaders.setOnItemClickListener(this);
        lvMain.setOnItemClickListener(this);

        setListViewHeightBasedOnChildren(lvHeaders);
        setListViewHeightBasedOnChildren(lvMain);


    }
    static void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        int desiredWidth = View.MeasureSpec.makeMeasureSpec(listView.getWidth(), View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        View view = null;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            view = listAdapter.getView(i, view, listView);
            if (i == 0)
                view.setLayoutParams(new ViewGroup.LayoutParams(desiredWidth, DrawerLayout.LayoutParams.WRAP_CONTENT));

            view.measure(desiredWidth, View.MeasureSpec.UNSPECIFIED);
            totalHeight += view.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.nav_view:
                adapterHeaderOpts.selectItem(position);
                adapterHeaderOpts.notifyDataSetChanged();

                adapterMainOpts.selectItem(-1);
                adapterMainOpts.notifyDataSetChanged();
                MainOption(position);
                break;
            case R.id.lvMain:
                adapterMainOpts.selectItem(position);
                adapterMainOpts.notifyDataSetChanged();

                adapterHeaderOpts.selectItem(-1);
                adapterHeaderOpts.notifyDataSetChanged();

                checkOption(position);

                break;

        }
        if (runnable != null) {
            lvHeaders.setItemChecked(position,true);
            lvMain.setItemChecked(position,true);
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
        mDrawerLayout.closeDrawers();

    }
    void MainOption(final int position) {

        runnable = null;
        switch (position) {
            case 0:
                runnable = navigateLibrary;
                break;
            case 1:
                break;
            case 2:
                runnable = navigatePlaylist;
                break;
        }



    }
    void checkOption(final int position) {
        runnable = null;
        final int SEARCHER_OPTION=0,ALBUM_OPTION = 1, ARTISTS_OPTION = 2, UPLOAD_OPTION=3,
                GENRES_OPTION = 4,LISTREP_OPTION = 5, CARPETA_OPTION = 6;


        switch (position) {
            case SEARCHER_OPTION:
                NavigationUtils.navigateToSearch(this);
                break;
            case ALBUM_OPTION:
                runnable = navigateAlbum;
                break;
            case ARTISTS_OPTION:
                runnable = navigateArtist;
                break;
            case UPLOAD_OPTION:
                break;
            case GENRES_OPTION:
                break;
            case LISTREP_OPTION:
                runnable = navigateListRep;
                break;
            case CARPETA_OPTION:
                break;
            default:
        }

    }

    private void updatePosition(final MenuItem menuItem) {
        runnable = null;

        switch (menuItem.getItemId()) {
            case R.id.nav_library:
                runnable = navigateLibrary;

                break;
            case R.id.nav_playlists:
                runnable = navigatePlaylist;
                break;
        }

        if (runnable != null) {
            menuItem.setChecked(true);
            mDrawerLayout.closeDrawers();
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    runnable.run();
                }
            }, 350);
        }
    }

   public void showHome() {

       Fragment fragment = new HomeFragment();
       FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
       transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();

   }
    Runnable navigateLibrary = new Runnable() {
        public void run() {
            //navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = new SongsFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();


        }
    };
    Runnable navigateArtista = new Runnable() {
        public void run() {
            //navigationView.getMenu().findItem(R.id.nav_library).setChecked(true);
            Fragment fragment = new ArtistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();


        }
    };

    Runnable navigateNowplaying = new Runnable() {
        public void run() {
            navigateLibrary.run();
            //startActivity(new Intent(MainActivity.this, NowPlayingActivity.class));
        }
    };
    final PermissionCallback permissionReadstorageCallback = new PermissionCallback() {
        @Override
        public void permissionGranted() {
            loadEverything();
        }

        @Override
        public void permissionRefused() {
            finish();
        }
    };
    Runnable navigatePlaylist = new Runnable() {
        public void run() {
            Fragment fragment = new PlaylistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        }
    };

    Runnable navigateAlbum = new Runnable() {
        public void run() {
            Fragment fragment = new AlbumFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        }
    };
    Runnable navigateArtist = new Runnable() {
        public void run() {
            Fragment fragment = new ArtistFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        }
    };

    Runnable navigateListRep = new Runnable() {
        public void run() {
            Fragment fragment = new ListaRepFragment();
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, fragment).commitAllowingStateLoss();
        }
    };
    @Override
    public void onMetaChanged() {
        super.onMetaChanged();
        //setDetailsToHeader();
    }

    @Override
    public void onResume() {
        super.onResume();
        sMainActivity = this;
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode, String[] permissions, int[] grantResults) {
        Nammu.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public boolean isNavigatingMain() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        return (currentFragment instanceof MainFragment || currentFragment instanceof QueueFragment
                || currentFragment instanceof PlaylistFragment);
    }

    private void addBackstackListener() {
        getSupportFragmentManager().addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                getSupportFragmentManager().findFragmentById(R.id.fragment_container).onResume();
            }
        });
    }


    @Override
    public int getActivityTheme() {
        return  R.style.AppThemeNormalLight;
    }

    public void selectedItem(int position) {
        listMenuDraw.setItemChecked(position,true);
    }
    public void selectedItemMain(int position) {
        listMenuDrawMain.setItemChecked(position,true);
    }


}
