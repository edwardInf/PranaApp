package pranaproject.pranaapp.fragments;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;

import pranaproject.pranaapp.R;
import pranaproject.pranaapp.adapters.SongsListAdapter;
import pranaproject.pranaapp.dataloaders.PlaylistLoader;
import pranaproject.pranaapp.models.Playlist;
import pranaproject.pranaapp.subfragments.PlaylistListFragment;
import pranaproject.pranaapp.subfragments.PlaylistPagerFragment;
import pranaproject.pranaapp.utils.Constants;
import pranaproject.pranaapp.widgets.MultiViewPager;

/**
 */
public class ListaRepFragment extends Fragment {

    int playlistcount;
    FragmentStatePagerAdapter adapter;
    MultiViewPager pager;
    long playlistID;
    public static HashMap<String, Runnable> playlistsMap = new HashMap<>();

    public static Context mContext;
    public static SongsListAdapter mAdapter;
    RecyclerView recyclerView;
    TextView playlistname;
    public ImageButton btnaddS;
    public static TextView playlistN;
    public static TextView canPlaylistN;
    public static RecyclerView recyclerList;
    private SongsListAdapter lAdapter;
    public static String action;
    public static boolean playlistFF=false;
    public ListaRepFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_lista_rep, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_listrep);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("LISTA");

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), true);
        playlistcount = playlists.size();

        pager = (MultiViewPager) rootView.findViewById(R.id.playlistpager_li);

        adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return playlistcount;
            }

            @Override
            public Fragment getItem(int position) {
                return PlaylistPagerFragment.newInstance(position);
            }

        };
        pager.setAdapter(adapter);
        pager.setOffscreenPageLimit(5);


        playlistN = (TextView) rootView.findViewById(R.id.txt_namePlaylistList);
        canPlaylistN = (TextView) rootView.findViewById(R.id.txt_cancPlaylist);
        btnaddS = (ImageButton) rootView.findViewById(R.id.btn_addPistPLay);
        btnaddS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Fragment fragment = new SongsFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_playlist, fragment).commitAllowingStateLoss();
            }
        });


        fragmentManager = getFragmentManager();
        activity= getActivity();

        return rootView;
    }

    public void updatePlaylists(final long id) {
        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), true);
        playlistcount = playlists.size();
        adapter.notifyDataSetChanged();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < playlists.size(); i++) {
                    long playlistid = playlists.get(i).id;
                    if (playlistid == id) {
                        pager.setCurrentItem(i);
                        break;
                    }
                }
            }
        }, 200);

    }


    public static String c;
    public static long idplay;
    public static Activity activity;
    public static FragmentManager fragmentManager;


    public static void showDetallePlaylist(String playlistName, String canciones,String action,
                                           long firstAlbumID,long playlistID,Context context) {
        playlistN.setText(playlistName);
        canPlaylistN.setText(canciones);
        c = action;
        String idplay = Long.toString(playlistID);


        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle=new Bundle();
        bundle.putLong(Constants.PLAYLIST_ID, playlistID);
        bundle.putLong(Constants.ALBUM_ID, firstAlbumID);
        bundle.putString(Constants.PLAYLIST_NAME, playlistName);

        switch (action){
            case "navigate_playlist_lastadded":

                bundle.putString("action", action);


                break;
            case "navigate_playlist_recent":
                bundle.putString("action", action);

                /*PlaylistListFragment fragobj2 = new PlaylistListFragment();
                fragobj2.setArguments(bundle);
                fragmentTransaction.add(R.id.framelayout_listplay, fragobj2);
                fragmentTransaction.commit();*/
                break;
            case "navigate_playlist_toptracks":
                bundle.putString("action", action);

                break;
            case "navigate_playlist":
                bundle.putString("action", action);

                break;
        }
        PlaylistListFragment fragobj=new PlaylistListFragment();
        fragobj.setArguments(bundle);

        fragmentTransaction.add(R.id.framelayout_listplay, fragobj);
        fragmentTransaction.commit();

    }

}
