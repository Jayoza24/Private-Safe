package com.example.manager.fragments;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.manager.Adapter.VideoFolderAdapter;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;

import java.util.ArrayList;

public class FragmentHome extends Fragment {

    private ArrayList<MediaFiles> mediaFiles = new ArrayList<>();
    private ArrayList<String> allFolderList = new ArrayList<>();
    RecyclerView recyclerView;
    VideoFolderAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home2, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        someFolders();

        return view;
    }

    private void someFolders() {
        mediaFiles = fetchMedia();
        adapter = new VideoFolderAdapter(mediaFiles, allFolderList, getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), RecyclerView.VERTICAL, false));
        adapter.notifyDataSetChanged();
    }

    private ArrayList<MediaFiles> fetchMedia() {
        ArrayList<MediaFiles>mediaFilesArrayList=new ArrayList<>();
        Uri uri= MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor=getActivity().getContentResolver().query(uri,null, null,null,null);
        if(cursor!=null && cursor.moveToNext()){
            do{
                @SuppressLint("Range") String id=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media._ID));
                @SuppressLint("Range") String title=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.TITLE));
                @SuppressLint("Range") String displayName=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME));
                @SuppressLint("Range") String size=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.SIZE));
                @SuppressLint("Range") String duration=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DURATION));
                @SuppressLint("Range") String path=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATA));
                @SuppressLint("Range") String dateAdded=cursor.getString(cursor.getColumnIndex(MediaStore.Video.Media.DATE_ADDED));
                MediaFiles mediaFiles = new MediaFiles(id,title,displayName,size,duration,path,dateAdded);

                int index=path.lastIndexOf("/");
                String substring=path.substring(0,index);
                if(!allFolderList.contains(substring)){
                    allFolderList.add(substring);
                }
                mediaFilesArrayList.add(mediaFiles);
            }while (cursor.moveToNext());
        }
        return mediaFilesArrayList;
    }
}