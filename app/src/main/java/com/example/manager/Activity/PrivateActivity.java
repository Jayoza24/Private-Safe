package com.example.manager.Activity;


import static android.content.ContentValues.TAG;

import static com.example.manager.Activity.VideoFilesActivity.MY_PREF;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.AESUtils;
import com.example.manager.Adapter.PrivateVideoAdapter;
import com.example.manager.Adapter.VideoFilesAdapter;
import com.example.manager.MainActivity;
import com.example.manager.Model.MediaFiles;
import com.example.manager.Model.PrivateList;
import com.example.manager.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.Buffer;
import java.util.ArrayList;
import java.util.Formatter;


public class PrivateActivity extends AppCompatActivity {

    RecyclerView privateRecycler;
    File file;
    TextView txt;

    private ArrayList<PrivateList> privateLists = new ArrayList<>();
    private PrivateVideoAdapter privateVideoAdapter;

    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_private);

        privateRecycler = findViewById(R.id.privateRecycler);
        txt = findViewById(R.id.txtTitle);

        SharedPreferences sharedPreferences = getSharedPreferences(MY_PREF, Context.MODE_PRIVATE);
        password = sharedPreferences.getString("private", "no");
        password.trim();

        int mode = getIntent().getIntExtra("mode", 0);

        file = new File(getExternalFilesDir(null) + "/.private");
        File[] files = file.listFiles();
        try {

            for(int i=0; i<files.length; i++){
                File loc = new File(files[i].getAbsolutePath());
                String size = android.text.format.Formatter.formatFileSize(this,loc.length());
                PrivateList list = new PrivateList(files[i].getName(),size);
                privateLists.add(list);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(!privateLists.isEmpty()){
            privateVideoAdapter = new PrivateVideoAdapter(this,privateLists);
            privateRecycler.setAdapter(privateVideoAdapter);
            txt.setVisibility(View.GONE);
            privateRecycler.setVisibility(View.VISIBLE);
            privateRecycler.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL,false));
            privateVideoAdapter.notifyDataSetChanged();
        }else{
            privateRecycler.setVisibility(View.GONE);
            txt.setVisibility(View.VISIBLE);
        }

    }


}