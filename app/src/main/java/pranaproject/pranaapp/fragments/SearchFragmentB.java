package pranaproject.pranaapp.fragments;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import pranaproject.pranaapp.R;
import pranaproject.pranaapp.activities.BaseActivity;
import pranaproject.pranaapp.adapters.SearchAdapter;
import pranaproject.pranaapp.dataloaders.AlbumLoader;
import pranaproject.pranaapp.dataloaders.ArtistLoader;
import pranaproject.pranaapp.dataloaders.SongLoader;
import pranaproject.pranaapp.models.Album;
import pranaproject.pranaapp.models.Artist;
import pranaproject.pranaapp.models.Song;
import pranaproject.pranaapp.provider.SearchHistory;
import pranaproject.pranaapp.utils.PreferencesUtility;
import pranaproject.pranaapp.widgets.FastScroller;


public class SearchFragmentB extends Fragment implements SearchView.OnQueryTextListener, View.OnTouchListener{
    private SearchView mSearchView;
    private InputMethodManager mImm;
    private String queryString;
    public Context context;
    private SearchAdapter adapter;
    private PreferencesUtility mPreferences;
    private RecyclerView recyclerView;

    private List searchResults = Collections.emptyList();
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPreferences = PreferencesUtility.getInstance(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(

                R.layout.fragment_search_bilbioteca, container, false);

        mImm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

        Toolbar toolbar = (Toolbar) rootView.findViewById(R.id.toolbar_BSearch);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        final ActionBar ab = ((AppCompatActivity) getActivity()).getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("");

        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_Bsearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new SearchAdapter(getActivity());
        recyclerView.setAdapter(adapter);

        return rootView;
    }
    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.menu_search, menu);

        mSearchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.menu_search));

        mSearchView.setOnQueryTextListener(this);
        mSearchView.setQueryHint(getString(R.string.search_library));
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setIconified(false);

        menu.findItem(R.id.menu_search).expandActionView();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        onQueryTextChange(query);
        hideInputManager();

        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.equals(queryString)) {
            return true;
        }
        queryString = newText;
        if (!queryString.trim().equals("")) {
            this.searchResults = new ArrayList();
            List<Song> songList = SongLoader.searchSongs(getActivity(), queryString);
            List<Album> albumList = AlbumLoader.getAlbums(getActivity(), queryString);
            List<Artist> artistList = ArtistLoader.getArtists(getActivity(), queryString);

            if (!songList.isEmpty()) {
                searchResults.add("Pistas");
            }
            searchResults.addAll((Collection) (songList.size() < 10 ? songList : songList.subList(0, 10)));
            if (!albumList.isEmpty()) {
                searchResults.add("Albumes");
            }
            searchResults.addAll((Collection) (albumList.size() < 7 ? albumList : albumList.subList(0, 7)));
            if (!artistList.isEmpty()) {
                searchResults.add("Artistas");
            }
            searchResults.addAll((Collection) (artistList.size() < 7 ? artistList : artistList.subList(0, 7)));
        } else {
            searchResults.clear();
            adapter.updateSearchResults(searchResults);
            adapter.notifyDataSetChanged();
        }

        adapter.updateSearchResults(searchResults);
        adapter.notifyDataSetChanged();

        return true;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }

    public void hideInputManager() {
        if (mSearchView != null) {
            if (mImm != null) {
                mImm.hideSoftInputFromWindow(mSearchView.getWindowToken(), 0);
            }
            mSearchView.clearFocus();

            SearchHistory.getInstance(context).addSearchString(queryString);
        }
    }
}
