package com.example.manager.fragments;

import static android.content.ContentValues.TAG;
import static com.example.manager.Activity.VideoFilesActivity.MY_PREF;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.manager.Activity.VideoPlayerActivity;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Set;


public class HistoryFragment extends Fragment {

    TextView vname,vpath,vsize,vduration;
    String id,videoName,path,size,duration;
    ImageView btnPlay;
    RoundedImageView thumbnail;
    RelativeLayout rel1,rel2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);

        vname = view.findViewById(R.id.name);
        vpath = view.findViewById(R.id.path);
        vsize = view.findViewById(R.id.size);
        vduration = view.findViewById(R.id.duration);
        btnPlay = view.findViewById(R.id.btnPlay);
        thumbnail = view.findViewById(R.id.thumbnail);
        rel1 = view.findViewById(R.id.rel1);
        rel2 = view.findViewById(R.id.rel2);

        id = sharedPreferences.getString("id","abc");
        videoName = sharedPreferences.getString("videoName","abc");
        path = sharedPreferences.getString("path","abc");
        size = sharedPreferences.getString("size","abc");
        duration = sharedPreferences.getString("duration","abc");

        if(id!="abc"&&videoName!="abc"&&path!="abc"&&size!="abc"&&duration!="abc"){

            rel1.setVisibility(View.GONE);
            rel2.setVisibility(View.VISIBLE);

            vname.setText(videoName);
            vpath.setText(path);

            vsize.setText(android.text.format.Formatter.formatFileSize(getActivity(),Long.parseLong(size)));

            String length = timeConversion(Long.parseLong(duration));
            vduration.setText(length);

            Glide.with(getContext()).load(new File(path)).into(thumbnail);
        }else{
            rel2.setVisibility(View.GONE);
            rel1.setVisibility(View.VISIBLE);
        }

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), VideoPlayerActivity.class);
                intent.putExtra("flag",1);
                intent.putExtra("historyPath",path);
                intent.putExtra("title",videoName);
                startActivity(intent);
            }
        });

        return view;
    }

    public String timeConversion(long value) {
        String videoTime;
        int duration = (int) value;
        int hrs = (duration / 3600000);
        int mns = (duration / 60000) % 60000;
        int scs = duration % 60000 / 1000;
        if (hrs > 0) {
            videoTime = String.format("%02d:%02d:%02d", hrs, mns, scs);
        } else {
            videoTime = String.format("%02d:%02d", mns, scs);
        }
        return videoTime;
    }
}