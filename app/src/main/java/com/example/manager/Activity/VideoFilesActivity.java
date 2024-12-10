package com.example.manager.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.manager.Adapter.VideoFilesAdapter;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;

import java.util.ArrayList;

public class VideoFilesActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{

    public static final String MY_PREF = "my_pref";
    RecyclerView recyclerVideos;
    private ArrayList<MediaFiles> videoFilesArrayList = new ArrayList<>();
    static VideoFilesAdapter videoFilesAdapter;
    public static String folderName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_files);

        recyclerVideos = findViewById(R.id.recyclerVideos);
        folderName = getIntent().getStringExtra("folderName");
        getSupportActionBar().setTitle(folderName);

        SharedPreferences.Editor editor = getSharedPreferences(MY_PREF,MODE_PRIVATE).edit();
        editor.putString("playlistFolderName",folderName);
        editor.apply();

        someVideoFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.upper_menu_videos,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView view = (SearchView) menuItem.getActionView();
        view.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }

    private void someVideoFiles() {
        videoFilesArrayList = fetchMedia(folderName);
        videoFilesAdapter = new VideoFilesAdapter(videoFilesArrayList,this,0);
        recyclerVideos.setAdapter(videoFilesAdapter);
        recyclerVideos.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
        videoFilesAdapter.notifyDataSetChanged();
    }

    private ArrayList<MediaFiles> fetchMedia(String folderName) {
        ArrayList<MediaFiles>videoFiles=new ArrayList<>();
        Uri uri=MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        String selection=MediaStore.Video.Media.DATA+" like?";
        String[]selectionArg=new String[]{"%"+folderName+"%"};
        Cursor cursor=getContentResolver().query(uri,null, selection,selectionArg,null);

        if(cursor != null && cursor.moveToNext()){
            do{
                @SuppressLint("Range") String id=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                @SuppressLint("Range") String title=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                @SuppressLint("Range") String displayName=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                @SuppressLint("Range") String size=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                @SuppressLint("Range") String duration=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                @SuppressLint("Range") String path=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                @SuppressLint("Range") String dateAdded=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                MediaFiles mediaFiles = new MediaFiles(id,title,displayName,size,duration,path,dateAdded);
                videoFiles.add(mediaFiles);

            }while (cursor.moveToNext());
        }

        return videoFiles;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String inputs = newText.toLowerCase();
        ArrayList<MediaFiles> mediaFiles = new ArrayList<>();
        for(MediaFiles files : videoFilesArrayList){
            if(files.getTitle().toLowerCase().contains(inputs)){
                mediaFiles.add(files);
            }
        }
        VideoFilesActivity.videoFilesAdapter.updateVideoFiles(mediaFiles);
        return true;
    }
}