package pranaproject.pranaapp.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import pranaproject.pranaapp.MusicPlayer;
import pranaproject.pranaapp.dataloaders.PlaylistLoader;
import pranaproject.pranaapp.models.Playlist;
import pranaproject.pranaapp.models.Song;

import java.util.List;

public class AddPlaylistDialog extends DialogFragment {

    public static AddPlaylistDialog newInstance(Song song) {
        long[] songs = new long[1];
        songs[0] = song.id;
        return newInstance(songs);
    }

    public static AddPlaylistDialog newInstance(long[] songList) {
        AddPlaylistDialog dialog = new AddPlaylistDialog();
        Bundle bundle = new Bundle();
        bundle.putLongArray("songs", songList);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final List<Playlist> playlists = PlaylistLoader.getPlaylists(getActivity(), false);
        CharSequence[] chars = new CharSequence[playlists.size() + 1];
        chars[0] = "Crear nuevo playlist";

        for (int i = 0; i < playlists.size(); i++) {
            chars[i + 1] = playlists.get(i).name;
        }
        return new MaterialDialog.Builder(getActivity()).title("Agregar a Playlist")
                .items(chars).itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        long[] songs = getArguments().getLongArray("songs");
                        if (which == 0) {
                            CrearPlaylistDialog.newInstance(songs).show(getActivity().getSupportFragmentManager(),
                                    "CREATE_PLAYLIST");
                            return;
                        }
                        MusicPlayer.addToPlaylist(getActivity(), songs, playlists.get(which - 1).id);
                        dialog.dismiss();

                    }
                }).build();
    }
}
