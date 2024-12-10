package com.example.manager.fragments;

import static android.content.ContentValues.TAG;
import static com.example.manager.Activity.VideoFilesActivity.MY_PREF;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.Vibrator;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.manager.Activity.PrivateActivity;
import com.example.manager.BuildConfig;
import com.example.manager.MainActivity;
import com.example.manager.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.play.core.review.ReviewInfo;
import com.google.android.play.core.review.ReviewManager;
import com.google.android.play.core.review.ReviewManagerFactory;
import com.google.android.play.core.tasks.Task;

import org.w3c.dom.Text;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class SettingFragment extends Fragment implements View.OnClickListener {

    TextView txtVersion;
    RelativeLayout relReset,relPrivacy,relFeedback,relHelp,relPrivate,relPrivateShow;
    private ReviewManager reviewManager;

    BottomSheetDialog bottomSheetDialog1;
    View psDialog;
    EditText editText;

    TextView txtPrivate,txtPrivateShow;

    private String privateFilePassword;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);

        SharedPreferences sharedPreferences = getActivity().getSharedPreferences(MY_PREF,Context.MODE_PRIVATE);
        privateFilePassword = sharedPreferences.getString("private","0");
        Log.d(TAG, "privateFilePassword"+privateFilePassword);

        txtVersion = view.findViewById(R.id.txtVersion);
        relReset = view.findViewById(R.id.relReset);
        relPrivacy = view.findViewById(R.id.relPrivacy);
        relFeedback = view.findViewById(R.id.relFeedback);
        relHelp = view.findViewById(R.id.relHelp);
        relPrivate = view.findViewById(R.id.relPrivate);
        txtPrivate = view.findViewById(R.id.txtPrivate);
        relPrivateShow = view.findViewById(R.id.relPrivateShow);
        txtVersion.setText("V "+BuildConfig.VERSION_NAME);

        if(privateFilePassword!="0"){
            txtPrivate.setText("Reset Private File Password");
            relPrivateShow.setVisibility(View.VISIBLE);
        }else{
            txtPrivate.setText("Set Private Folder Password");
            relPrivateShow.setVisibility(View.GONE);
        }

        relPrivate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog1 = new BottomSheetDialog(getActivity(),R.style.BottomSheetTheme);
                psDialog = LayoutInflater.from(getActivity()).inflate(R.layout.password_dialog,v.findViewById(R.id.layout_password));
                editText = psDialog.findViewById(R.id.edtPassword);
                editText.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if(count==4){
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setTitle("Set Password");
                            builder.setMessage("Are you sure to set this password?");
                            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    SharedPreferences.Editor editor = sharedPreferences.edit();
                                    editor.putString("private",editText.getText().toString().trim());
                                    editor.apply();
                                    Toast.makeText(getActivity(), "Done", Toast.LENGTH_SHORT).show();
                                    bottomSheetDialog1.dismiss();
                                    startActivity(new Intent(getActivity(),MainActivity.class));
                                }
                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    bottomSheetDialog1.dismiss();
                                }
                            }).show();
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
                psDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog1.dismiss();
                    }
                });
                psDialog.findViewById(R.id.btn0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"0");
                    }
                });
                psDialog.findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"1");
                    }
                });
                psDialog.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"2");
                    }
                });
                psDialog.findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"3");
                    }
                });
                psDialog.findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"4");
                    }
                });
                psDialog.findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"5");
                    }
                });
                psDialog.findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"6");
                    }
                });
                psDialog.findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"7");
                    }
                });
                psDialog.findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"8");
                    }
                });
                psDialog.findViewById(R.id.btn9).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"9");
                    }
                });
                bottomSheetDialog1.setContentView(psDialog);
                bottomSheetDialog1.show();
            }
        });

        relPrivateShow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog1 = new BottomSheetDialog(getActivity(),R.style.BottomSheetTheme);
                psDialog = LayoutInflater.from(getActivity()).inflate(R.layout.password_dialog,v.findViewById(R.id.layout_password));
                editText = psDialog.findViewById(R.id.edtPassword);

                psDialog.findViewById(R.id.btnCheck).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String pswd = editText.getText().toString();
                        Log.d(TAG, "onClick: "+privateFilePassword);
                        Log.d(TAG, "pswd "+pswd.length()+" two "+privateFilePassword.length());
                        if(!pswd.isEmpty()){
                            int one = Integer.parseInt(pswd);
                            int two = Integer.parseInt(privateFilePassword);
                            if(one==two){
                                Intent intent = new Intent(getActivity(),PrivateActivity.class);
                                intent.putExtra("mode",2);
                                startActivity(intent);
                                bottomSheetDialog1.dismiss();
                            }else{
                                Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                                vibrator.vibrate(200);
                            }
                        }else{
                            Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
                            vibrator.vibrate(200);
                        }
                    }
                });

                psDialog.findViewById(R.id.btnCancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bottomSheetDialog1.dismiss();
                    }
                });
                psDialog.findViewById(R.id.btn0).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"0");
                    }
                });
                psDialog.findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"1");
                    }
                });
                psDialog.findViewById(R.id.btn2).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"2");
                    }
                });
                psDialog.findViewById(R.id.btn3).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"3");
                    }
                });
                psDialog.findViewById(R.id.btn4).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"4");
                    }
                });
                psDialog.findViewById(R.id.btn5).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"5");
                    }
                });
                psDialog.findViewById(R.id.btn6).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"6");
                    }
                });
                psDialog.findViewById(R.id.btn7).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"7");
                    }
                });
                psDialog.findViewById(R.id.btn8).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"8");
                    }
                });
                psDialog.findViewById(R.id.btn9).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        editText.setText(editText.getText().toString()+"9");
                    }
                });

                bottomSheetDialog1.setContentView(psDialog);
                bottomSheetDialog1.show();


            }
        });

        relReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle("Clear App Data");
                builder.setMessage("Are you sure to clear whole app data");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try{
                            // clearing app data
                            String packageName = getContext().getPackageName();
                            Runtime runtime = Runtime.getRuntime();
                            runtime.exec("pm clear "+packageName);

                        }catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        relPrivacy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package",getActivity().getPackageName(),null);
                intent.setData(uri);
                startActivity(intent);
            }
        });

        relFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                reviewManager = ReviewManagerFactory.create(getActivity());
                showRateApp();
            }
        });

        relHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] data = new String[]{"1. Video Not Showing\n-> Please check file permission to get video files or put some videos on internal storage.",
                        "2. App Getting Crash\n-> Try clear app data and clear app cache.",
                        "3. Some other crashes or bug spotted\n-> Please report us on feedback with screenshot attached email to our mail"};
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setMessage(data[0]+"\n\n"+data[1]+"\n\n"+data[2]);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();
            }
        });

        return view;
    }

    public void showRateApp() {
        Task<ReviewInfo> request = reviewManager.requestReviewFlow();
        request.addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                // We can get the ReviewInfo object
                ReviewInfo reviewInfo = task.getResult();

                Task<Void> flow = reviewManager.launchReviewFlow(getActivity(), reviewInfo);
                flow.addOnCompleteListener(task1 -> {
                    // The flow has finished. The API does not indicate whether the user
                    // reviewed or not, or even whether the review dialog was shown. Thus, no
                    // matter the result, we continue our app flow.
                });
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){

        }
    }
}