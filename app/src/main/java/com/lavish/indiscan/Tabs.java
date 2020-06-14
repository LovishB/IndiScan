package com.lavish.indiscan;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class Tabs extends AppCompatActivity {

    private TextView Scans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tabs);

        Scans=findViewById(R.id.drawer_share_text);

        Scans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(Tabs.this,"HALA",Toast.LENGTH_SHORT).show();
            }
        });
    }
}
