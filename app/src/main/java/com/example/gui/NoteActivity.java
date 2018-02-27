package com.example.gui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.example.gui.test.R;

public class NoteActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note);
        String text;
        Intent intent = getIntent();

        text = intent.getStringExtra("text");

        TextView output=(TextView) findViewById(R.id.output);
        // Assuming that 'output' is the id of your TextView
        output.setText(text );
    }

}
