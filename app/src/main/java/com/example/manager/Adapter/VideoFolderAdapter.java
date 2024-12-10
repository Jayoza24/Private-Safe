package com.example.manager.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager.Activity.VideoFilesActivity;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;

import java.util.ArrayList;

public class VideoFolderAdapter extends RecyclerView.Adapter<VideoFolderAdapter.ViewHolder> {

    private ArrayList<MediaFiles> mediaFiles;
    private ArrayList<String> folderPath;
    private Context context;

    public VideoFolderAdapter(ArrayList<MediaFiles> mediaFiles, ArrayList<String> folderPath, Context context) {
        this.mediaFiles = mediaFiles;
        this.folderPath = folderPath;
        this.context = context;
    }

    @NonNull
    @Override
    public VideoFolderAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.folder_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoFolderAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        int indexPath = folderPath.get(position).lastIndexOf("/");
        String nameOfFolder = folderPath.get(position).substring(indexPath+1);
        holder.folderName.setText(nameOfFolder);
        holder.folderPath.setText(folderPath.get(position));
        holder.noOfFiles.setText(noOfFiles(folderPath.get(position))+" Videos");

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context, VideoFilesActivity.class);
                intent.putExtra("folderName",nameOfFolder);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return folderPath.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView folderName,folderPath,noOfFiles;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            folderName = itemView.findViewById(R.id.folderName);
            folderPath = itemView.findViewById(R.id.folderPath);
            noOfFiles = itemView.findViewById(R.id.noOfFiles);

        }
    }

    int noOfFiles(String folder_name){
        int files_no = 0;
        for(MediaFiles mediaFiles : mediaFiles){
            if(mediaFiles.getPath().substring(0,mediaFiles.getPath().lastIndexOf("/")).endsWith(folder_name)){
                files_no++;
            }
        }
        return files_no;
    }
}
