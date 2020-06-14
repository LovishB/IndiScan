package com.lavish.indiscan;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageButton btn,menu;
    private ImageView mImageView;

    private RecyclerView mRecyclerView;
    private AddProjectAdapter mAddProjectAdapter;
    private ArrayList<ModelAddProject> list;

    private SQLiteHelper mSQLiteHelper;
    private Cursor cursor;

    private ArrayList<String> ID_Array;
    private ArrayList<String> Name_Array;
    private ArrayList<String> Date_Array;

    private TextView t1;
    private TextView NewProject,Scans,Rate,About,Bug,RemoveAds,Share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = findViewById(R.id.toolbar);
        btn=findViewById(R.id.main_btn);
        mImageView=findViewById(R.id.main_picback);
        mRecyclerView=findViewById(R.id.main_container);
        list=new ArrayList<>();
        ID_Array = new ArrayList<>();
        Name_Array = new ArrayList<>();
        Date_Array = new ArrayList<>();
        mSQLiteHelper = new SQLiteHelper(this);
        t1 = findViewById(R.id.main_picback_text);
        menu=findViewById(R.id.menu_btn);

        addingprojectstolist();

        mToolbar.setTitle(" My Projects");
        mToolbar.setLogo(R.drawable.ic_home_black_24dp);
        mToolbar.setTitleMarginStart(80);
        setSupportActionBar(mToolbar);

        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setupBottomSheet();
            }
        });


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar c = Calendar.getInstance();
                SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
                SimpleDateFormat ddf = new SimpleDateFormat("dd-MM-yyyy");
                final String formattedDate = "Project-"+df.format(c.getTime());
                final String date=ddf.format(c.getTime());

                LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
                View promptView = layoutInflater.inflate(R.layout.change_name, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
                alertDialogBuilder.setView(promptView);
                final EditText input =  promptView.findViewById(R.id.userInput);
                input.setText(formattedDate);
                alertDialogBuilder.setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                addprojecttodatabase(formattedDate,input.getText()+"",date);
                                Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                                intent.putExtra("PROJECT_ID", formattedDate);
                                intent.putExtra("PROJECT_NAME",input.getText()+"");
                                startActivity(intent);
                            }
                        })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();


            }
        });



    }
    public void addprojecttodatabase(String id,String name,String date){
        boolean result = mSQLiteHelper.addData(id,name,date);
    }

    public void addingprojectstolist(){
        readdatabse();
        mAddProjectAdapter=new AddProjectAdapter(list,MainActivity.this);
        if(mAddProjectAdapter.getItemCount()==0){
            mImageView.setVisibility(View.VISIBLE);
            t1.setVisibility(View.VISIBLE);
        }else{
            mImageView.setVisibility(View.INVISIBLE);
            t1.setVisibility(View.INVISIBLE);
        }
        mRecyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
        mRecyclerView.setAdapter(mAddProjectAdapter);

        mAddProjectAdapter.setOnItmeClickListener(new AddProjectAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(MainActivity.this, ProjectActivity.class);
                intent.putExtra("PROJECT_ID", list.get(position).project_id);
                intent.putExtra("PROJECT_NAME",list.get(position).project_name);
                startActivity(intent);
            }
        });
    }

    public void readdatabse(){

        list.clear();
        Date_Array.clear();
        Name_Array.clear();
        ID_Array.clear();

        cursor=mSQLiteHelper.getProjects();

        while (cursor.moveToNext()){
            ID_Array.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.Table_Column_ID)));
            Name_Array.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.Table_Column_Name)));
            Date_Array.add(cursor.getString(cursor.getColumnIndex(mSQLiteHelper.Table_Column_Date)));
        }

        for(int i=Name_Array.size()-1;i>=0;i--){
            list.add(new ModelAddProject(ID_Array.get(i),Name_Array.get(i),Date_Array.get(i)));
        }
        cursor.close();

    }

    public void setupBottomSheet(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MainActivity.this, R.style.BottomSheetDialogTheme);
        View bottomSheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet, (LinearLayout) findViewById(R.id.bottomsheetcontainer));
        TextView Rate,Share,About,Bugs,Adds;
        Rate =bottomSheetView.findViewById(R.id.drawer_rate_text);
        Share =bottomSheetView.findViewById(R.id.drawer_share_text);
        About=bottomSheetView.findViewById(R.id.drawer_about_text);
        Bugs=bottomSheetView.findViewById(R.id.drawer_report_bug_text);
        Adds=bottomSheetView.findViewById(R.id.drawer_remove_ad_text);

        Adds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this,"Add free version will be available with next update",Toast.LENGTH_LONG).show();
            }
        });

        bottomSheetDialog.setContentView(bottomSheetView);
        bottomSheetDialog.show();

        Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT,"IndiScan - Indian's #1 tool for scanning documents, pictures and IDs directly to phone. Share pdfs in three simple steps."
                +"\n"+"Download it Free :  <link>");
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Share with"));
            }
        });

        Bugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent (Intent.ACTION_VIEW , Uri.parse("mailto:" + "rapidcodeindia@gmail.com"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Bug Report");
                intent.putExtra(Intent.EXTRA_TEXT, "if possible, please attach screenshot of bug report ");
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        addingprojectstolist();
    }
}
