package pranaproject.pranaapp.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import java.util.List;

import pranaproject.pranaapp.MusicPlayer;
import pranaproject.pranaapp.dataloaders.PlaylistLoader;
import pranaproject.pranaapp.models.Playlist;
import pranaproject.pranaapp.models.Song;

public class DelPlaylistDialog extends DialogFragment {



    public static DelPlaylistDialog newInstance(long[] songList) {
        DelPlaylistDialog dialog = new DelPlaylistDialog();

        return null;
    }



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), false);
        CharSequence[] chars = new CharSequence[playlists.size()];

        for (int i = 0; i < playlists.size(); i++) {
            chars[i] = playlists.get(i).name;
        }
        return new MaterialDialog.Builder(getActivity()).title("Agregar a Playlist")
                .items(chars).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        long[] songs = getArguments().getLongArray("songs");

                        //MusicPlayer.addToPlaylist(getActivity(), songs, playlists.get(which).id);

                        //Toast.makeText(getActivity(),playlists.get(which).name , Toast.LENGTH_SHORT).show();
                        MusicPlayer.deletePlaylist(getActivity(),playlists.get(which).name);
                        dialog.dismiss();




                    }
                }).build();
    }
}


