package pranaproject.pranaapp.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;

import pranaproject.pranaapp.activities.BaseActivity;
import pranaproject.pranaapp.adapters.CoverAdapter;
import pranaproject.pranaapp.R;

public class HomeFragment extends Fragment {

    View view;

    GridView  gvRecentMusic;
    CoverAdapter fmAdapter,rmAdapter;

    int[] mResources = {
            R.drawable.cover_mj,
            R.drawable.cover_guns,
            R.drawable.cover_1
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = getActivity().getLayoutInflater().inflate(R.layout.layout_home,null);

       /* Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar_home);

        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((BaseActivity) getActivity()).getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");*/

        //gvFavoriteMusic = (GridView) view.findViewById(R.id.gvFavoriteMusic);
        //gvRecentMusic = (GridView) view.findViewById(R.id.gvRecentMusic);

        fmAdapter = new CoverAdapter(getActivity(),mResources);
        rmAdapter = new CoverAdapter(getActivity(),mResources);

        //gvFavoriteMusic.setAdapter(fmAdapter);
        gvRecentMusic.setAdapter(rmAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_main, menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_search:
                return true;
            case R.id.action_listmusic:
                return true;
            case R.id.action_profile:
                return true;

        }
        return super.onOptionsItemSelected(item);
    }



}
