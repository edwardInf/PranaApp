package pranaproject.pranaapp.subfragments;


import android.annotation.TargetApi;
import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Transition;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import pranaproject.pranaapp.R;
import pranaproject.pranaapp.activities.PlaylistDetalleActivity;
import pranaproject.pranaapp.adapters.SongsListAdapter;
import pranaproject.pranaapp.dataloaders.LastAddedLoader;
import pranaproject.pranaapp.dataloaders.PlaylistSongLoader;
import pranaproject.pranaapp.dataloaders.SongLoader;
import pranaproject.pranaapp.dataloaders.TopTracksLoader;
import pranaproject.pranaapp.fragments.PlaylistFragment;
import pranaproject.pranaapp.listeners.SimplelTransitionListener;
import pranaproject.pranaapp.models.Song;
import pranaproject.pranaapp.utils.Constants;
import pranaproject.pranaapp.utils.PreferencesUtility;
import pranaproject.pranaapp.utils.TimberUtils;
import pranaproject.pranaapp.widgets.DividerItemDecoration;


public class PlaylistListFragment extends Fragment{

    public String action;
    Long playlistID;
    String a;

    HashMap<String, Runnable> playlistsMap = new HashMap<>();
    Runnable playlistLastAdded = new Runnable() {
        public void run() {
            new loadLastAdded().execute("");
        }
    };
    Runnable playlistRecents = new Runnable() {
        @Override
        public void run() {
            new loadRecentlyPlayed().execute("");

        }
    };
    Runnable playlistToptracks = new Runnable() {
        @Override
        public void run() {
            new loadTopTracks().execute("");
        }
    };
    Runnable playlistUsercreated = new Runnable() {
        @Override
        public void run() {
            new loadUserCreatedPlaylist().execute("");

        }
    };
    private AppCompatActivity mContext;
    private SongsListAdapter mAdapter;
    private RecyclerView recyclerView;
    private ImageView blurFrame;
    private TextView playlistname;
    private View foreground;
    public static Activity activity;
    public static FragmentManager fragmentManager;
    public static boolean band = false;
    public String tipo;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist_list, container, false);
        fragmentManager = getFragmentManager();
        action = getArguments().getString("action");
            playlistsMap.put(Constants.NAVIGATE_PLAYLIST_LASTADDED, playlistLastAdded);
            playlistsMap.put(Constants.NAVIGATE_PLAYLIST_RECENT, playlistRecents);
            playlistsMap.put(Constants.NAVIGATE_PLAYLIST_TOPTRACKS, playlistToptracks);
            playlistsMap.put(Constants.NAVIGATE_PLAYLIST_USERCREATED, playlistUsercreated);


        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_listSongsPlaylist);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));


        if (TimberUtils.isLollipop() && PreferencesUtility.getInstance(getActivity()).getAnimations()) {
        } else {


        }
        setUpSongs();

        return rootView;
    }

    private void setUpSongs() {
        Runnable navigation = playlistsMap.get(action);
        if (navigation != null) {
            navigation.run();
        } else {
            Log.d("PlaylistDetail", "mo action specified");

        }
    }
    private void setRecyclerViewAapter() {
        recyclerView.setAdapter(mAdapter);
        if (TimberUtils.isLollipop() && PreferencesUtility.getInstance(getActivity()).getAnimations()) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                            DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_black));
                }
            }, 250);
        } else
            recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                    DividerItemDecoration.VERTICAL_LIST, R.drawable.item_divider_black));

    }

    private class loadLastAdded extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            List<Song> lastadded = LastAddedLoader.getLastAddedSongs(getActivity());
            mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), lastadded, true);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadRecentlyPlayed extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            TopTracksLoader loader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.RecentSongs);
            List<Song> recentsongs = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
            mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), recentsongs, true);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();

        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadTopTracks extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            TopTracksLoader loader = new TopTracksLoader(getActivity(), TopTracksLoader.QueryType.TopTracks);
            List<Song> toptracks = SongLoader.getSongsForCursor(TopTracksLoader.getCursor());
            mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), toptracks, true);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class loadUserCreatedPlaylist extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            playlistID = getArguments().getLong(Constants.PLAYLIST_ID);
            List<Song> playlistsongs = PlaylistSongLoader.getSongsInPlaylist(getActivity(), playlistID);
            mAdapter = new SongsListAdapter((AppCompatActivity) getActivity(), playlistsongs, true);
            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {
            setRecyclerViewAapter();
        }

        @Override
        protected void onPreExecute() {
        }
    }

    private class EnterTransitionListener extends SimplelTransitionListener {

        @TargetApi(21)
        public void onTransitionEnd(Transition paramTransition) {
            setUpSongs();
        }

        public void onTransitionStart(Transition paramTransition) {
        }

    }

}
