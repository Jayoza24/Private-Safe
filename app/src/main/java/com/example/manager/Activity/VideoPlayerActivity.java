package com.example.manager.Activity;

import static android.content.ContentValues.TAG;
import static com.example.manager.Activity.VideoFilesActivity.MY_PREF;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.AESUtils;
import com.example.manager.Adapter.VideoFilesAdapter;
import com.example.manager.Model.MediaFiles;
import com.example.manager.PlaylistDialog;
import com.example.manager.R;
import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.ConcatenatingMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class VideoPlayerActivity extends AppCompatActivity implements View.OnClickListener {

    ArrayList<MediaFiles> mVideoFiles = new ArrayList<>();
    PlayerView playerView;
    SimpleExoPlayer player;
    int position;
    String videoTitle;
    ConcatenatingMediaSource concatenatingMediaSource;

    TextView title;
    ImageView nextButton,previousButton,VideoBack,lock,unlock,scaling,VideoList;
    RelativeLayout root;

    VideoFilesAdapter videoFilesAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setFullScreen();
        setContentView(R.layout.activity_video_player);
        getSupportActionBar().hide();

        playerView = findViewById(R.id.exoplayer_view);
        title = findViewById(R.id.video_title);
        nextButton = findViewById(R.id.exo_next);
        previousButton = findViewById(R.id.exo_prev);
        VideoBack = findViewById(R.id.video_back);
        lock = findViewById(R.id.lock);
        unlock = findViewById(R.id.unlock);
        root = findViewById(R.id.root_layout);
        scaling = findViewById(R.id.scaling);
        VideoList = findViewById(R.id.video_list);

        position = getIntent().getIntExtra("position", 1);
        videoTitle = getIntent().getStringExtra("video_title");
        mVideoFiles = getIntent().getExtras().getParcelableArrayList("videoArrayList");

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREF,MODE_PRIVATE).edit();
//        editor.putString("videoName",getIntent().getStringArrayListExtra("last").toString());


        title.setText(videoTitle);
        nextButton.setOnClickListener(this);
        previousButton.setOnClickListener(this);
        VideoBack.setOnClickListener(this);
        lock.setOnClickListener(this);
        unlock.setOnClickListener(this);
        root.setOnClickListener(this);
        scaling.setOnClickListener(firstListener);
        VideoList.setOnClickListener(this);

        if(getIntent().getIntExtra("flag",0)==1){
            playHistory();
            title.setText(getIntent().getStringExtra("title"));
            VideoList.setVisibility(View.GONE);
        }else {
            playVideo();
            VideoList.setVisibility(View.VISIBLE);
            editor.putString("id",mVideoFiles.get(position).getId());
            editor.putString("videoName",mVideoFiles.get(position).getDisplayName());
            editor.putString("path",mVideoFiles.get(position).getPath());
            editor.putString("size",mVideoFiles.get(position).getSize());
            editor.putString("duration",mVideoFiles.get(position).getDuration());
            editor.apply();
        }
    }

    private void playHistory(){
        String hPath = getIntent().getStringExtra("historyPath");
        Uri uri = Uri.parse(hPath);
        player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this,Util.getUserAgent(this,"app"));

        concatenatingMediaSource = new ConcatenatingMediaSource();
        for(int i=0;i<1;i++){
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        playError();
    }

    private void playVideo() {

        String path = mVideoFiles.get(position).getPath();
        Uri uri = Uri.parse(path);
        player = new SimpleExoPlayer.Builder(this).build();
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, Util.getUserAgent(this, "app"));

        concatenatingMediaSource = new ConcatenatingMediaSource();
        for (int i = 0; i < mVideoFiles.size(); i++) {
            new File(String.valueOf(mVideoFiles.get(i)));
            MediaSource mediaSource = new ProgressiveMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(Uri.parse(String.valueOf(uri))));
            concatenatingMediaSource.addMediaSource(mediaSource);
        }
        playerView.setPlayer(player);
        playerView.setKeepScreenOn(true);
        player.prepare(concatenatingMediaSource);
        player.seekTo(position, C.TIME_UNSET);
        playError();
    }

    private void playError() {
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                Toast.makeText(VideoPlayerActivity.this, "Video Playing Error", Toast.LENGTH_SHORT).show();
            }
        });
        player.setPlayWhenReady(true);
    }

    @Override
    public void onBackPressed() {
        if (player.isPlaying()) {
            player.stop();
            if(getIntent().getIntExtra("enc",0)==1){
                encrypt();
            }
        }else if(!player.isPlaying()){
            player.stop();
            if(getIntent().getIntExtra("enc",0)==1){
                encrypt();
            }
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
        player.stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        player.setPlayWhenReady(true);
        player.getPlaybackState();
    }

    private void setFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.video_back:
                if(player!=null){
                    if(getIntent().getIntExtra("enc",0)==1){
                        encrypt();
                    }
                    player.release();
                }
                finish();
                break;
            case R.id.video_list:
                PlaylistDialog playlistDialog = new PlaylistDialog(mVideoFiles,videoFilesAdapter);
                playlistDialog.show(getSupportFragmentManager(),playlistDialog.getTag());
                break;
            case R.id.lock:
                root.setVisibility(View.INVISIBLE);
                unlock.setVisibility(View.VISIBLE);
                break;
            case R.id.unlock:
                unlock.setVisibility(View.GONE);
                root.setVisibility(View.VISIBLE);
                break;
            case R.id.exo_next:
                try {
                    player.stop();
                    position++;
                    playVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "no Next Video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.exo_prev:
                try {
                    player.stop();
                    position--;
                    playVideo();
                } catch (Exception e) {
                    Toast.makeText(this, "no Previous Video", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }
    View.OnClickListener firstListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FILL);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scaling.setImageResource(R.drawable.ic_fullscreen);
            scaling.setOnClickListener(secondListener);
        }
    };
    View.OnClickListener secondListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_ZOOM);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scaling.setImageResource(R.drawable.ic_baseline_zoom_out_map_24);
            scaling.setOnClickListener(ThirdListener);
        }
    };
    View.OnClickListener ThirdListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            playerView.setResizeMode(AspectRatioFrameLayout.RESIZE_MODE_FIT);
            player.setVideoScalingMode(C.VIDEO_SCALING_MODE_DEFAULT);
            scaling.setImageResource(R.drawable.ic_fit);
            scaling.setOnClickListener(firstListener);
        }
    };

    private void encrypt(){
        File file = new File(getExternalFilesDir(null) + "/.private");

        try {
            String path = getIntent().getStringExtra("historyPath");
            String name = getIntent().getStringExtra("title");
            InputStream in = new FileInputStream(path);
            OutputStream out = new FileOutputStream(file + "/" + AESUtils.encrypt(name));

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);

            }
            in.close();
            in = null;

            // write the output file
            out.flush();
            out.close();
            out = null;

            // delete the original file
            new File(path).delete();


        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
    }
}