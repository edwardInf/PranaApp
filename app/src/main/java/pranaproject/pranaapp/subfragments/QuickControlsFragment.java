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

package pranaproject.pranaapp.subfragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.appthemeengine.Config;
import pranaproject.pranaapp.MusicPlayer;
import pranaproject.pranaapp.MusicService;
import pranaproject.pranaapp.R;
import pranaproject.pranaapp.activities.BaseActivity;
import pranaproject.pranaapp.listeners.MusicStateListener;
import pranaproject.pranaapp.utils.Helpers;
import pranaproject.pranaapp.utils.ImageUtils;
import pranaproject.pranaapp.utils.TimberUtils;
import pranaproject.pranaapp.widgets.PlayPauseButton;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

public class QuickControlsFragment extends Fragment implements MusicStateListener {


    public static View topContainer;
    private ProgressBar mProgress;
    private SeekBar mSeekBar;
    public Runnable mUpdateProgress = new Runnable() {

        @Override
        public void run() {

            long position = MusicPlayer.position();
            mProgress.setProgress((int) position);
            mSeekBar.setProgress((int) position);

            if (MusicPlayer.isPlaying()) {
                mProgress.postDelayed(mUpdateProgress, 50);
            } else mProgress.removeCallbacks(this);

        }
    };
    private PlayPauseButton mPlayPause, mPlayPauseExpanded;
    private TextView mTitle, mTitleExpanded;
    private TextView mArtist, mArtistExpanded;
    private ImageView mAlbumArt, mBlurredArt, mPlaying, shuffle, repeat;
    private View rootView;
    private View playPauseWrapper, playPauseWrapperExpanded;
    private MaterialIconView previous, next;
    private boolean duetoplaypause = false;
    private String ateKey;
    public int accentColor;


    private final View.OnClickListener mPlayPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            } else {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                }
            }, 200);

        }
    };
    private final View.OnClickListener mPlayPauseExpandedListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            duetoplaypause = true;
            if (!mPlayPauseExpanded.isPlayed()) {
                mPlayPauseExpanded.setPlayed(true);
                mPlayPauseExpanded.startAnimation();
            } else {
                mPlayPauseExpanded.setPlayed(false);
                mPlayPauseExpanded.startAnimation();
            }
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    MusicPlayer.playOrPause();
                }
            }, 200);

        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_playback_controls, container, false);
        this.rootView = rootView;

        mPlayPause = (PlayPauseButton) rootView.findViewById(R.id.play_pause);
        mPlayPauseExpanded = (PlayPauseButton) rootView.findViewById(R.id.playpause);
        playPauseWrapper = rootView.findViewById(R.id.play_pause_wrapper);
        playPauseWrapperExpanded = rootView.findViewById(R.id.playpausewrapper);
        playPauseWrapper.setOnClickListener(mPlayPauseListener);
        playPauseWrapperExpanded.setOnClickListener(mPlayPauseExpandedListener);
        mProgress = (ProgressBar) rootView.findViewById(R.id.song_progress_normal);
        mSeekBar = (SeekBar) rootView.findViewById(R.id.song_progress);
        mTitle = (TextView) rootView.findViewById(R.id.title);
        mArtist = (TextView) rootView.findViewById(R.id.artist);
        mTitleExpanded = (TextView) rootView.findViewById(R.id.song_title);
        mArtistExpanded = (TextView) rootView.findViewById(R.id.song_artist);
        shuffle = (ImageView) rootView.findViewById(R.id.shuffle);
        repeat = (ImageView) rootView.findViewById(R.id.repeat);

        mPlaying = (ImageView) rootView.findViewById(R.id.imgSongPlaying);


        mAlbumArt = (ImageView) rootView.findViewById(R.id.album_art_nowplayingcard);
        mBlurredArt = (ImageView) rootView.findViewById(R.id.blurredAlbumart);
        next = (MaterialIconView) rootView.findViewById(R.id.next);
        previous = (MaterialIconView) rootView.findViewById(R.id.previous);
        topContainer = rootView.findViewById(R.id.topContainer);

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) mProgress.getLayoutParams();
        mProgress.measure(0, 0);
        layoutParams.setMargins(0, -(mProgress.getMeasuredHeight() / 2), 0, 0);
        mProgress.setLayoutParams(layoutParams);

        mPlayPause.setColor(Config.accentColor(getActivity(), Helpers.getATEKey(getActivity())));
        mPlayPauseExpanded.setColor(Color.WHITE);

        mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                if (b) {
                    MusicPlayer.seek((long) i);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.next();
                    }
                }, 200);

            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        MusicPlayer.previous(getActivity(), false);
                    }
                }, 200);

            }
        });


        ((BaseActivity) getActivity()).setMusicStateListenerListener(this);

        updateShuffleState();
        updateRepeatState();

        return rootView;
    }

    public void updateNowplayingCard() {
        mTitle.setText(MusicPlayer.getTrackName());
        mArtist.setText(MusicPlayer.getArtistName());

        mTitleExpanded.setText(MusicPlayer.getTrackName());
        mArtistExpanded.setText(MusicPlayer.getArtistName());
        if (!duetoplaypause) {
            ImageLoader.getInstance().displayImage(TimberUtils.getAlbumArtUri(MusicPlayer.getCurrentAlbumId()).toString(), mPlaying,
                    new DisplayImageOptions.Builder().cacheInMemory(true)
                            .showImageOnFail(R.drawable.ic_prueba2)
                            .resetViewBeforeLoading(true)
                            .build(), new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                            Bitmap failedBitmap = ImageLoader.getInstance().loadImageSync("drawable://" + R.drawable.ic_prueba2);
                            if (getActivity() != null)
                                new setBlurredAlbumArt().execute(failedBitmap);
                            mAlbumArt.setImageDrawable(mPlaying.getDrawable());

                        }

                        @Override
                        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                            if (getActivity() != null)
                                new setBlurredAlbumArt().execute(loadedImage);
                            mAlbumArt.setImageDrawable(mPlaying.getDrawable());

                        }

                        @Override
                        public void onLoadingCancelled(String imageUri, View view) {

                        }
                    });
        }
        duetoplaypause = false;
        mProgress.setMax((int) MusicPlayer.duration());
        mSeekBar.setMax((int) MusicPlayer.duration());
        mProgress.postDelayed(mUpdateProgress, 10);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();

    }

    @Override
    public void onResume() {
        super.onResume();
        topContainer = rootView.findViewById(R.id.topContainer);

    }

    public void updateState() {
        if (MusicPlayer.isPlaying()) {
            if (!mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(true);
                mPlayPause.startAnimation();
            }
            if (!mPlayPauseExpanded.isPlayed()) {
                mPlayPauseExpanded.setPlayed(true);
                mPlayPauseExpanded.startAnimation();
            }
        } else {
            if (mPlayPause.isPlayed()) {
                mPlayPause.setPlayed(false);
                mPlayPause.startAnimation();
            }
            if (mPlayPauseExpanded.isPlayed()) {
                mPlayPauseExpanded.setPlayed(false);
                mPlayPauseExpanded.startAnimation();
            }
        }
    }

    public void restartLoader() {

    }

    public void onPlaylistChanged() {

    }

    public void onMetaChanged() {
        updateNowplayingCard();
        updateState();
    }

    private class setBlurredAlbumArt extends AsyncTask<Bitmap, Void, Drawable> {

        @Override
        protected Drawable doInBackground(Bitmap... loadedImage) {
            Drawable drawable = null;
            try {
                drawable = ImageUtils.createBlurredImageFromBitmap(loadedImage[0], getActivity(), 6);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return drawable;
        }

        @Override
        protected void onPostExecute(Drawable result) {
            if (result != null) {
                if (mBlurredArt.getDrawable() != null) {
                    final TransitionDrawable td =
                            new TransitionDrawable(new Drawable[]{
                                    mBlurredArt.getDrawable(),
                                    result
                            });
                    mBlurredArt.setImageDrawable(td);
                    td.startTransition(400);

                } else {
                    mBlurredArt.setImageDrawable(result);
                }

            }
        }

        @Override
        protected void onPreExecute() {
        }
    }

    public void updateShuffleState() {
        if (shuffle != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setIcon(MaterialDrawableBuilder.IconValue.SHUFFLE)
                    .setSizeDp(30);

            if (getActivity() != null) {
                if (MusicPlayer.getShuffleMode() == 0) {
                    builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
                } else builder.setColor(Config.accentColor(getActivity(), ateKey));
            }

            shuffle.setImageDrawable(builder.build());
            shuffle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleShuffle();
                    updateShuffleState();
                    updateRepeatState();
                }
            });
        }
    }

    public void updateRepeatState() {
        if (repeat != null && getActivity() != null) {
            MaterialDrawableBuilder builder = MaterialDrawableBuilder.with(getActivity())
                    .setSizeDp(30);

            if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_NONE) {
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
                builder.setColor(Config.textColorPrimary(getActivity(), ateKey));
            } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_CURRENT) {
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT_ONCE);
                builder.setColor(Config.accentColor(getActivity(), ateKey));
            } else if (MusicPlayer.getRepeatMode() == MusicService.REPEAT_ALL) {
                builder.setColor(Config.accentColor(getActivity(), ateKey));
                builder.setIcon(MaterialDrawableBuilder.IconValue.REPEAT);
            }


            repeat.setImageDrawable(builder.build());
            repeat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    MusicPlayer.cycleRepeat();
                    updateRepeatState();
                    updateShuffleState();
                }
            });
        }
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ateKey = Helpers.getATEKey(getActivity());
        accentColor = Config.accentColor(getActivity(), ateKey);
    }

}
