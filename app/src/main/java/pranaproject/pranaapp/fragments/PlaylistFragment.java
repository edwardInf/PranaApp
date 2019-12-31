/*
 * Copyright (C) 2015 Naman Dwivedi
 *
 * Licensed under the GNU General Public License v3
 *
 * This is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 */

package pranaproject.pranaapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.afollestad.appthemeengine.ATE;

import pranaproject.pranaapp.MainActivity;
import pranaproject.pranaapp.R;
import pranaproject.pranaapp.adapters.SongsListAdapter;
import pranaproject.pranaapp.dataloaders.PlaylistLoader;
import pranaproject.pranaapp.dialogs.AddPlaylistDialog;
import pranaproject.pranaapp.dialogs.CrearPlaylistDialog;
import pranaproject.pranaapp.dialogs.DelPlaylistDialog;
import pranaproject.pranaapp.models.Playlist;
import pranaproject.pranaapp.subfragments.PlaylistListFragment;
import pranaproject.pranaapp.subfragments.PlaylistPagerFragment;
import pranaproject.pranaapp.utils.Constants;
import pranaproject.pranaapp.widgets.MultiViewPager;

import java.util.HashMap;
import java.util.List;

public class PlaylistFragment extends Fragment {

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playlist, container, false);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_playlist);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("PLAYLIST");

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), true);
        playlistcount = playlists.size();

        pager = (MultiViewPager) rootView.findViewById(R.id.playlistpager);

        adapter = new FragmentStatePagerAdapter(getChildFragmentManager()) {

            @Override
            public int getCount() {
                return playlistcount;
            }

            @Override
            public Fragment getItem(int position) {
                return PlaylistPagerFragment.newInstance(position,true);
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

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        /*super.onViewCreated(view, savedInstanceState);
        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean("dark_theme", false)) {
            ATE.apply(this, "dark_theme");
        } else {
            ATE.apply(this, "light_theme");
        }*/
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_playlist, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_new_playlist:
                CrearPlaylistDialog.newInstance().show(getChildFragmentManager(), "CREATE_PLAYLIST");
                return true;
            /*case R.id.action_del_playlist:
                return true;*/
        }
        return super.onOptionsItemSelected(item);
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
    public static FragmentManager  fragmentManager;


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

