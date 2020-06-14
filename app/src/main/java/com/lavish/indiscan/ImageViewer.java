package com.lavish.indiscan;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class ImageViewer extends AppCompatActivity {

    private Toolbar mToolbar;
    private byte[] img;
    private String img_number;
    private ImageView iiii;
    private SQLiteHelper mSQLiteHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        Intent intent =getIntent();
        img_number =intent.getStringExtra("PIC_NUMBER");
        //img=intent.getByteArrayExtra("PIC_BITMAP");


        mToolbar=findViewById(R.id.toolbar2);
        iiii=findViewById(R.id.viewer_img);
        mSQLiteHelper = new SQLiteHelper(this);

        mToolbar.setTitle(img_number);
        mToolbar.setNavigationIcon(R.drawable.ic_arrow_back_black_24dp);
        mToolbar.inflateMenu(R.menu.viewer_menu);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        gettingImagefromdatabse(img_number);

        iiii.setImageBitmap(getImage(img));

        mToolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(MenuItem item) {

                 if(item.getItemId()== R.id.viewer_download)
                {
                    BitmapDrawable drawable = (BitmapDrawable) iiii.getDrawable();
                    Bitmap bit = drawable.getBitmap();
                    downloadSingleImage(bit);
                }
                else if(item.getItemId()== R.id.viewer_share){

                     BitmapDrawable drawable = (BitmapDrawable) iiii.getDrawable();
                     Bitmap bit = drawable.getBitmap();
                     shareSingleImage(bit);
                }

                return false;
            }
        });
    }

    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }

    public void gettingImagefromdatabse(String number){
        Cursor cursor = mSQLiteHelper.getView(Integer.parseInt(number));
        if(cursor!=null && cursor.getCount()>0 && cursor.moveToFirst()){
            do{
                img = cursor.getBlob(cursor.getColumnIndex(mSQLiteHelper.IMAGE));
            }while (cursor.moveToNext());
            cursor.close();
        }

    }

    //download pdf
    public void downloadSingleImage(Bitmap bitmap){

    }

    //share image
    public void shareSingleImage(Bitmap bitmap){

    }
}
