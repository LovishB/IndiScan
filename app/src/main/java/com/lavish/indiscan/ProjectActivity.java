package com.lavish.indiscan;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.scanlibrary.ScanActivity;
import com.scanlibrary.ScanConstants;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class ProjectActivity extends AppCompatActivity {

    private Toolbar mToolbar;
    private ImageView mImageView;
    private ImageButton btn,camerabtn,gallerybtn;
    int REQUEST_CODE = 99;
    private CardView card;
    private TextView card_text;
    private ImageView cancel_pdf,share_selected,download_selected;
    private RecyclerView mRecyclerView;
    private AddPicAdapter mAddPicAdapter;
    private ArrayList<ModelAddPic> list;
    private ArrayList<ModelAddPic> mlist;
    private String title, id;
    private SQLiteHelper mSQLiteHelper;
    private ArrayList<String> ids;
    private ArrayList<Integer> pic_nos;
    private ArrayList<byte[]> imgs;
    private TextView t1;
    private Animation btnanimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project);
        Intent intent = getIntent();
        title = intent.getStringExtra("PROJECT_NAME");
        id = intent.getStringExtra("PROJECT_ID");

        mToolbar = findViewById(R.id.project_toolbar);
        btn = findViewById(R.id.project_btn);
        mImageView = findViewById(R.id.project_img);
        mRecyclerView = findViewById(R.id.project_container);
        list = new ArrayList<>();
        mlist = new ArrayList<>();
        ids = new ArrayList<>();
        pic_nos = new ArrayList<>();
        imgs = new ArrayList<>();
        mSQLiteHelper = new SQLiteHelper(this);
        card =findViewById(R.id.card_project);
        card_text = findViewById(R.id.card_text);
        cancel_pdf=findViewById(R.id.appCompatImageView6);
        share_selected=findViewById(R.id.project_share_selected);
        download_selected=findViewById(R.id.project_download_selected);
        t1=findViewById(R.id.project_img_text);
        camerabtn=findViewById(R.id.select_camera);
        gallerybtn=findViewById(R.id.select_gallery);
        btnanimation= AnimationUtils.loadAnimation(this,R.anim.floatingbtn);

        addimagestolist();


        mToolbar.setTitle(title);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.inflateMenu(R.menu.project_menu);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                if(item.getItemId()==R.id.project_delete_btn)
                {
                   deleteproject();
                }
                else if(item.getItemId()== R.id.project_download)
                {
                    if(list.size()>0){
                        ArrayList<Bitmap> finalBitmap = new ArrayList<>();
                        for(int i =0;i<list.size();i++){
                            finalBitmap.add(list.get(i).mBitmap); }
                        downloadproject(finalBitmap);
                    }
                }
                else if(item.getItemId()== R.id.project_share){
                    if(list.size()>0){
                        ArrayList<Bitmap> finalBitmap = new ArrayList<>();
                        for(int i =0;i<list.size();i++){
                            finalBitmap.add(list.get(i).mBitmap); }
                        shareproject(finalBitmap);
                    }
                }

                return false;
            }
        });

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camerabtn.setVisibility(View.VISIBLE);
                gallerybtn.setVisibility(View.VISIBLE);
                camerabtn.startAnimation(btnanimation);
                gallerybtn.startAnimation(btnanimation);
                //
            }
        });

        camerabtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCamera();
            }
        });

        gallerybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        cancel_pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });

        share_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlist.size()>0){
                    ArrayList<Bitmap> finalBitmap = new ArrayList<>();
                    for(int i =0;i<mlist.size();i++){
                        finalBitmap.add(mlist.get(i).mBitmap); }
                   shareproject(finalBitmap);
                }
            }
        });

        download_selected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mlist.size()>0){
                    ArrayList<Bitmap> finalBitmap = new ArrayList<>();
                    for(int i =0;i<mlist.size();i++){
                        finalBitmap.add(mlist.get(i).mBitmap); }
                    download_selected_images(finalBitmap);
                }
            }
        });

    }

    public void openCamera() {
        int preference = ScanConstants.OPEN_CAMERA;
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE, preference);
        startActivityForResult(intent, REQUEST_CODE);
    }

    public void openGallery(){
        int preference = ScanConstants.OPEN_MEDIA;
        Intent intent = new Intent(this,ScanActivity.class);
        intent.putExtra(ScanConstants.OPEN_INTENT_PREFERENCE,preference);
        startActivityForResult(intent,REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri uri = data.getExtras().getParcelable(ScanConstants.SCANNED_RESULT);
            Bitmap bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                getContentResolver().delete(uri, null, null);
                addimagetolist(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void addimagetolist(Bitmap bitmap) {
        addimagetodatabase(bitmap);
        list.clear();
        ids.clear();
        imgs.clear();
        pic_nos.clear();
        addimagestolist();
    }

    public void addimagestolist() {

        Cursor cursor = mSQLiteHelper.getImage(id);

        if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
            do{
                pic_nos.add(cursor.getInt(cursor.getColumnIndex(mSQLiteHelper.Table_Primary_TWO)));
                imgs.add(cursor.getBlob(cursor.getColumnIndex(mSQLiteHelper.IMAGE)));
            }while (cursor.moveToNext());
            cursor.close();
        }

       for(int i=0;i<pic_nos.size();i++){
            list.add(new ModelAddPic(id,pic_nos.get(i)+ "",getImage(imgs.get(i))));
        }

        mAddPicAdapter = new AddPicAdapter(list, ProjectActivity.this);
        if (mAddPicAdapter.getItemCount() == 0) {
            mImageView.setVisibility(View.VISIBLE);
            t1.setVisibility(View.VISIBLE);
        } else {
            mImageView.setVisibility(View.INVISIBLE);
            t1.setVisibility(View.INVISIBLE);
        }
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setAdapter(mAddPicAdapter);

        mAddPicAdapter.setOnItmeClickListener(new AddPicAdapter.OnItemClickListener() {
            @Override
            public void OnItemClick(int position) {
                Intent intent = new Intent(ProjectActivity.this, ImageViewer.class);
                intent.putExtra("PIC_NUMBER", list.get(position).PicId);
                startActivity(intent);
            }
        });

        mAddPicAdapter.setOnItemLongClickListener(new AddPicAdapter.OnItemLongClickListener() {
            @Override
            public boolean OnItemLongClick(View view, int position) {


                if(mlist.contains(list.get(position))){
                    mlist.remove(list.get(position));
                    view.findViewById(R.id.select_img).setVisibility(View.INVISIBLE);

                }else if(!mlist.contains(list.get(position))){
                    mlist.add(list.get(position));
                    view.findViewById(R.id.select_img).setVisibility(View.VISIBLE);

                }

                mToolbar.getMenu().clear();
                btn.setVisibility(View.INVISIBLE);
                card.setVisibility(View.VISIBLE);
                card_text.setText(mlist.size()+""+"/"+list.size()+"");

                return true;
            }
        });

    }

    public void addimagetodatabase(Bitmap bitmap) {
        byte[] img = getBytes(bitmap);
        mSQLiteHelper.addPic(id, img);
    }

    public static byte[] getBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, stream);
        return stream.toByteArray();
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);

    }

    @Override
    protected void onRestart() {
        super.onRestart();

    }

    public void deleteproject(){
        AlertDialog.Builder builder2 = new AlertDialog.Builder(ProjectActivity.this);
        builder2.setTitle("Delete project");
        builder2.setCancelable(false);
        builder2.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mSQLiteHelper.deleteProject(id);
                finish();
            }
        });

        builder2.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        AlertDialog dialog2 = builder2.create();
        dialog2.show();
    }


    //download complete project
    public void downloadproject(final ArrayList<Bitmap> finalBitmap){
        AlertDialog.Builder builder2 = new AlertDialog.Builder(ProjectActivity.this);
        builder2.setTitle("Download all files");
        builder2.setPositiveButton("Download All", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);
                View promptView = layoutInflater.inflate(R.layout.change_name, null);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectActivity.this);
                alertDialogBuilder.setView(promptView);
                final EditText input =  promptView.findViewById(R.id.userInput);
                input.setText(title);
                alertDialogBuilder
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                //function to start download
                                normalDownload(finalBitmap,input.getText()+"");
                            }
                        })
                        .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                AlertDialog alertD = alertDialogBuilder.create();
                alertD.show();
            }
        });

        builder2.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        AlertDialog dialog2 = builder2.create();
        dialog2.show();
    }


    //download selected images
    public  void download_selected_images(final ArrayList<Bitmap> finalBitmap){
        LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);
        View promptView = layoutInflater.inflate(R.layout.select_type, null);
        AlertDialog.Builder builder2 = new AlertDialog.Builder(ProjectActivity.this);
        builder2.setView(promptView);
        builder2.setTitle("Page Choice");
        final RadioGroup radioGroup = promptView.findViewById(R.id.RGroup);

        builder2.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if( radioGroup.getCheckedRadioButtonId()==R.id.radio_auto){
                    LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);
                    View promptView = layoutInflater.inflate(R.layout.change_name, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectActivity.this);
                    alertDialogBuilder.setView(promptView);
                    final EditText input =  promptView.findViewById(R.id.userInput);
                    input.setText(title);
                    alertDialogBuilder
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    normalDownload(finalBitmap,input.getText()+"");
                                }
                            })
                            .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    AlertDialog alertD = alertDialogBuilder.create();
                    alertD.show();
                }else if( radioGroup.getCheckedRadioButtonId()==R.id.radio_id) {
                    if (finalBitmap.size() < 3) {
                        LayoutInflater layoutInflater = LayoutInflater.from(ProjectActivity.this);
                        View promptView = layoutInflater.inflate(R.layout.change_name, null);
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectActivity.this);
                        alertDialogBuilder.setView(promptView);
                        final EditText input = promptView.findViewById(R.id.userInput);
                        input.setText(title);
                        alertDialogBuilder
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        IDDOwnload(finalBitmap, input.getText() + "");
                                    }
                                })
                                .setNegativeButton("Back", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        AlertDialog alertD = alertDialogBuilder.create();
                        alertD.show();
                    } else {
                        Toast.makeText(ProjectActivity.this, "Select upto 2 files for ID format", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

        builder2.setNegativeButton("Back", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {





            }
        });
        AlertDialog dialog2 = builder2.create();
        dialog2.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        camerabtn.setVisibility(View.INVISIBLE);
        gallerybtn.setVisibility(View.INVISIBLE);
    }

    //download pdf
    public void normalDownload(ArrayList<Bitmap> finalBitmap,String fileName){

    }

    //download Id pdf
    public void IDDOwnload(ArrayList<Bitmap> finalBitmap,String fileName){

    }

    //share images

    public void shareproject(final ArrayList<Bitmap> finalBitmap){

    }



}
