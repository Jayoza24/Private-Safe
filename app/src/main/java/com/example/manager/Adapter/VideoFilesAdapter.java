package com.example.manager.Adapter;

import static android.content.ContentValues.TAG;
import static com.example.manager.Activity.VideoFilesActivity.MY_PREF;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.manager.AESUtils;
import com.example.manager.Activity.PrivateActivity;
import com.example.manager.Activity.VideoPlayerActivity;
import com.example.manager.Model.MediaFiles;
import com.example.manager.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;

public class VideoFilesAdapter extends RecyclerView.Adapter<VideoFilesAdapter.ViewHolder> {

    private ArrayList<MediaFiles> mediaFiles;
    private Context context;
    BottomSheetDialog bottomSheetDialog;
    private int viewType;
    double milliSecond;

    private String one, two, three, four, five, six;

    public VideoFilesAdapter(ArrayList<MediaFiles> mediaFiles, Context context, int viewType) {
        this.mediaFiles = mediaFiles;
        this.context = context;
        this.viewType = viewType;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.videoName.setText(mediaFiles.get(position).getDisplayName());
        String size = mediaFiles.get(position).getSize();
        if(mediaFiles.get(position).getSize()!=null){
            holder.videoSize.setText(android.text.format.Formatter.formatFileSize(context,
                    Long.parseLong(size)));
        }
        if(mediaFiles.get(position).getDuration()!=null){
           milliSecond = Double.parseDouble(mediaFiles.get(position).getDuration());
        }
        holder.videoDuration.setText(timeConversion((long) milliSecond));

        Glide.with(context).load(new File(mediaFiles.get(position).getPath())).into(holder.thumbnail);

        if (viewType == 0) {
            holder.menu_more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bottomSheetDialog = new BottomSheetDialog(context, R.style.BottomSheetTheme);
                    View bsView = LayoutInflater.from(context).inflate(R.layout.video_bs_layout,
                            view.findViewById(R.id.bottom_sheet));
                    bsView.findViewById(R.id.bs_play).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            holder.itemView.performClick();
                            bottomSheetDialog.dismiss();
                        }
                    });

                    bsView.findViewById(R.id.bs_rename).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            AlertDialog.Builder aleBuilder = new AlertDialog.Builder(context);
                            aleBuilder.setTitle("Rename to");
                            EditText editText = new EditText(context);
                            String path = mediaFiles.get(position).getPath();
                            final File file = new File(path);
                            String videoName = file.getName();
                            videoName = videoName.substring(0, videoName.lastIndexOf("."));
                            editText.setText(videoName);
                            aleBuilder.setView(editText);
                            editText.requestFocus();

                            aleBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    if (TextUtils.isEmpty(editText.getText().toString())) {
                                        Toast.makeText(context, "please enter some name!", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    String onlyPath = file.getParentFile().getAbsolutePath();
                                    String ext = file.getAbsolutePath();
                                    ext = ext.substring(ext.lastIndexOf("."));
                                    String newPath = onlyPath + "/" + editText.getText().toString() + ext;
                                    File newFile = new File(newPath);
                                    boolean rename = file.renameTo(newFile);
                                    if (rename) {
                                        ContentResolver resolver = context.getApplicationContext().getContentResolver();
                                        resolver.delete(MediaStore.Files.getContentUri("external"),
                                                MediaStore.MediaColumns.DATA + "=?", new String[]
                                                        {file.getAbsolutePath()});
                                        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                                        intent.setData(Uri.fromFile(newFile));
                                        context.getApplicationContext().sendBroadcast(intent);

                                        notifyDataSetChanged();
                                        Toast.makeText(context, "Video Renamed", Toast.LENGTH_SHORT).show();

                                        SystemClock.sleep(200);
                                        ((Activity) context).recreate();
                                    } else {
                                        Toast.makeText(context, "video Rename Failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            aleBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.dismiss();
                                }
                            });
                            aleBuilder.create().show();
                            bottomSheetDialog.dismiss();
                        }
                    });

                    bsView.findViewById(R.id.bs_hide).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences sharedPreferences = context.getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
                            String password = sharedPreferences.getString("private", "no");
                            password.trim();
                            Log.d(TAG, "password " + password);

                            if (password == "no") {
                                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                builder.setTitle("Make Password");
                                builder.setMessage("Please first make private folder password form settings tab!");
                                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                            } else {
                                BottomSheetDialog bottomSheetDialog1 = new BottomSheetDialog(context, R.style.BottomSheetTheme);
                                View psDialog = LayoutInflater.from(context).inflate(R.layout.password_dialog, v.findViewById(R.id.layout_password));
                                EditText editText = psDialog.findViewById(R.id.edtPassword);

                                psDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bottomSheetDialog1.dismiss();
                                        bottomSheetDialog.show();
                                    }
                                });
                                psDialog.findViewById(R.id.btnCheck).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        String pswd = editText.getText().toString();
                                        Log.d(TAG, "onClick: " + password);
                                        Log.d(TAG, "pswd " + pswd.length() + " two " + password.length());
                                        if (!pswd.isEmpty()) {
                                            int one = Integer.parseInt(pswd);
                                            int two = Integer.parseInt(password);
                                            if (one == two) {
                                                File file = new File(context.getExternalFilesDir(null) + "/.private");

                                                try {
                                                    if (!file.exists()) {
                                                        file.mkdir();
                                                    }

                                                    InputStream in = new FileInputStream(mediaFiles.get(position).getPath());

                                                    String encryptedName = "";
                                                    try{
                                                        encryptedName = AESUtils.encrypt(mediaFiles.get(position).getDisplayName());
                                                    }catch (Exception e){
                                                        Toast.makeText(context, "Something Wrong : "+e, Toast.LENGTH_SHORT).show();
                                                    }

                                                    OutputStream out = new FileOutputStream(file + "/" + encryptedName);

                                                    byte[] buffer = new byte[1024];
                                                    int read;
                                                    while ((read = in.read(buffer)) != -1) {
                                                        out.write(buffer, 0, read);
                                                    }
                                                    Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
                                                    bottomSheetDialog1.dismiss();
                                                    bottomSheetDialog.dismiss();
                                                    in.close();
                                                    in = null;

                                                    // write the output file
                                                    out.flush();
                                                    out.close();
                                                    out = null;

                                                    // delete the original file
                                                    new File(mediaFiles.get(position).getPath()).delete();
                                                    mediaFiles.remove(position);
                                                    notifyItemRemoved(position);
                                                    notifyItemRangeChanged(position, mediaFiles.size());

                                                } catch (Exception e) {
                                                    Log.d(TAG, e.toString());
                                                }
                                            } else {
                                                Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                                vibrator.vibrate(200);
                                            }
                                        } else {
                                            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                            vibrator.vibrate(200);
                                        }
                                    }
                                });
                                psDialog.findViewById(R.id.btn0).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "0");
                                    }
                                });
                                psDialog.findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "1");
                                    }
                                });
                                psDialog.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "2");
                                    }
                                });
                                psDialog.findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "3");
                                    }
                                });
                                psDialog.findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "4");
                                    }
                                });
                                psDialog.findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "5");
                                    }
                                });
                                psDialog.findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "6");
                                    }
                                });
                                psDialog.findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "7");
                                    }
                                });
                                psDialog.findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "8");
                                    }
                                });
                                psDialog.findViewById(R.id.btn9).setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        editText.setText(editText.getText().toString() + "9");
                                    }
                                });
                                bottomSheetDialog1.setContentView(psDialog);
                                bottomSheetDialog1.show();
                            }
                        }
                    });

                    bsView.findViewById(R.id.bs_share).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Uri uri = Uri.parse(mediaFiles.get(position).getPath());
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("video/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            context.startActivity(Intent.createChooser(shareIntent, "Share Video via"));
                            bottomSheetDialog.dismiss();
                        }
                    });

                    bsView.findViewById(R.id.bs_delete).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Delete");
                            builder.setMessage("Do you want to delete this video");
                            builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Uri contentUri = ContentUris
                                            .withAppendedId(MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
                                                    Long.parseLong(mediaFiles.get(position).getId()));
                                    File file = new File(mediaFiles.get(position).getPath());
                                    boolean delete = file.delete();
                                    if (delete) {
                                        context.getContentResolver().delete(contentUri, null, null);
                                        mediaFiles.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, mediaFiles.size());
                                        Toast.makeText(context, "Video Deleted", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Video Can't deleted", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            bottomSheetDialog.dismiss();
                        }
                    });

                    bsView.findViewById(R.id.bs_properties).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(context);
                            builder.setTitle("Properties");

                            one = "File: " + mediaFiles.get(position).getDisplayName();

                            String path = mediaFiles.get(position).getPath();
                            int indexOfPath = path.lastIndexOf("/");
                            two = "Path: " + path.substring(0, indexOfPath);

                            three = "Size: " + android.text.format.Formatter.formatFileSize(context, Long.parseLong(mediaFiles.get(position).getSize()));

                            four = "Length: " + timeConversion((long) milliSecond);

                            String nameWithFormat = mediaFiles.get(position).getDisplayName();
                            int index = nameWithFormat.lastIndexOf(".");
                            String format = nameWithFormat.substring(index + 1);
                            five = "Format: " + format;

                            MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                            mediaMetadataRetriever.setDataSource(mediaFiles.get(position).getPath());
                            String height = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT);
                            String width = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH);
                            six = "Resolution: " + height + "x" + width;


                            builder.setMessage(one + "\n\n" + two + "\n\n" + three + "\n\n" + four + "\n\n" + five + "\n\n" + six);
                            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                }
                            });
                            builder.show();
                            bottomSheetDialog.dismiss();
                        }
                    });

                    bottomSheetDialog.setContentView(bsView);
                    bottomSheetDialog.show();
                }
            });

        } else {
            holder.menu_more.setVisibility(View.GONE);
            holder.videoName.setTextColor(Color.WHITE);
            holder.videoSize.setTextColor(Color.WHITE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, VideoPlayerActivity.class);
                intent.putExtra("position", position);
                intent.putExtra("video_title", mediaFiles.get(position).getDisplayName());
                Bundle bundle = new Bundle();
                bundle.putParcelableArrayList("videoArrayList", mediaFiles);
                intent.putExtras(bundle);
                context.startActivity(intent);

                if (viewType == 1) {
                    ((Activity) context).finish();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mediaFiles.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, menu_more;
        TextView videoName, videoSize, videoDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            thumbnail = itemView.findViewById(R.id.thumbnail);
            menu_more = itemView.findViewById(R.id.video_menu_more);
            videoName = itemView.findViewById(R.id.video_name);
            videoSize = itemView.findViewById(R.id.video_size);
            videoDuration = itemView.findViewById(R.id.video_duration);

        }
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

    public void updateVideoFiles(ArrayList<MediaFiles> files) {
        mediaFiles = new ArrayList<>();
        mediaFiles.addAll(files);
        notifyDataSetChanged();
    }

}
