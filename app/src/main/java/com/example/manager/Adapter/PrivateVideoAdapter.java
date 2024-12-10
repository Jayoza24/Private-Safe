package com.example.manager.Adapter;

import static android.content.ContentValues.TAG;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.manager.AESUtils;
import com.example.manager.Activity.VideoPlayerActivity;
import com.example.manager.Model.PrivateList;
import com.example.manager.R;
import com.makeramen.roundedimageview.RoundedImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

public class PrivateVideoAdapter extends RecyclerView.Adapter<PrivateVideoAdapter.ViewHolder> {

    private Context context;
    private ArrayList<PrivateList> privateLists = new ArrayList<>();

    public PrivateVideoAdapter(Context context, ArrayList<PrivateList> privateLists) {
        this.context = context;
        this.privateLists = privateLists;
    }

    @NonNull
    @Override
    public PrivateVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.private_videos_layout,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrivateVideoAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        try {
            holder.videoName.setText(AESUtils.decrypt(privateLists.get(position).getVideoName()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.videoSize.setText(privateLists.get(position).getSize());
        Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(context.getExternalFilesDir(null)+"/.private/"+privateLists.get(position).getVideoName(), MediaStore.Video.Thumbnails.MINI_KIND);
        holder.imgThumbnail.setImageBitmap(bitmap);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File file = new File(context.getExternalFilesDir(null) + "/.private");

                try {
                    InputStream in = new FileInputStream(file+"/"+privateLists.get(position).getVideoName());
                    OutputStream out = new FileOutputStream(file + "/" + AESUtils.decrypt(privateLists.get(position).getVideoName()));

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
                    new File(file+"/"+privateLists.get(position).getVideoName()).delete();
                    Intent intent = new Intent(context, VideoPlayerActivity.class);
                    intent.putExtra("flag",1);
                    intent.putExtra("historyPath",context.getExternalFilesDir(null)+"/.private/"+AESUtils.decrypt(privateLists.get(position).getVideoName()));
                    intent.putExtra("title",AESUtils.decrypt(privateLists.get(position).getVideoName()));
                    intent.putExtra("enc",1);
                    context.startActivity(intent);

                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                }

            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle("What you want");
                builder.setPositiveButton("Unhide", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        File file = new File(context.getExternalFilesDir(null) + "/.private");

                        try {
                            File ex = new File(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/DCIM/Videos");
                            if(!ex.exists()){
                                ex.mkdirs();
                            }

                            InputStream in = new FileInputStream(file+"/"+privateLists.get(position).getVideoName());
                            OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/DCIM/Videos/" + AESUtils.decrypt(privateLists.get(position).getVideoName()));
//                            OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsoluteFile() + "/DCIM/" + AESUtils.decrypt(privateLists.get(position).getVideoName()));

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
                            new File(file+"/"+privateLists.get(position).getVideoName()).delete();
                            privateLists.remove(position);
                            notifyItemRemoved(position);
                            notifyItemRangeChanged(position,privateLists.size());

                        } catch (Exception e) {
                            Log.d(TAG, e.toString());
                        }
                    }
                }).setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        boolean delete = new File(context.getExternalFilesDir(null) + "/.private/"+privateLists.get(position).getVideoName()).delete();
                        privateLists.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position,privateLists.size());
                        if(!delete){
                            Toast.makeText(context, "Can't delete", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).show();
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return privateLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView videoName,videoSize;
        RoundedImageView imgThumbnail;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoName = itemView.findViewById(R.id.videoName);
            videoSize = itemView.findViewById(R.id.videoSize);
            imgThumbnail = itemView.findViewById(R.id.imgThumbnail);
        }
    }
}
