package pranaproject.pranaapp.dialogs;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import pranaproject.pranaapp.MusicPlayer;
import pranaproject.pranaapp.R;
import pranaproject.pranaapp.fragments.PlaylistFragment;
import pranaproject.pranaapp.models.Song;

public class CrearPlaylistDialog extends DialogFragment {

    public static CrearPlaylistDialog newInstance() {
        return newInstance((Song) null);
    }

    public static CrearPlaylistDialog newInstance(Song song) {
        long[] songs;
        if (song == null) {
            songs = new long[0];
        } else {
            songs = new long[1];
            songs[0] = song.id;
        }
        return newInstance(songs);
    }

    public static CrearPlaylistDialog newInstance(long[] songList) {
        CrearPlaylistDialog dialog = new CrearPlaylistDialog();
        Bundle bundle = new Bundle();
        bundle.putLongArray("songs", songList);
        dialog.setArguments(bundle);
        return dialog;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new MaterialDialog.Builder(getActivity()).positiveText("Crear").
                negativeText("Cancelar").title("NUEVO PLAYLIST").input("Nombre del Playlist", "", false,
                new MaterialDialog.InputCallback() {
            @Override
            public void onInput(@NonNull MaterialDialog dialog, CharSequence input) {

                long[] songs = getArguments().getLongArray("songs");
                long playistId = MusicPlayer.createPlaylist(getActivity(), input.toString());

                if (playistId != -1) {
                    if (songs != null && songs.length != 0)
                        MusicPlayer.addToPlaylist(getActivity(), songs, playistId);
                    else
                        Toast.makeText(getActivity(), "Playlist Creado", Toast.LENGTH_SHORT).show();
                    if (getParentFragment() instanceof PlaylistFragment) {
                        ((PlaylistFragment) getParentFragment()).updatePlaylists(playistId);
                    }
                } else {
                    Toast.makeText(getActivity(), "No se puede crear Playlist", Toast.LENGTH_SHORT).show();
                }

            }
        }).build();
    }
}
