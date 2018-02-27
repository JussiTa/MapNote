package com.example.gui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.gui.test.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class TextInput extends DialogFragment implements TextWatcher {
    private TextView textview;
    private EditText editText2;
    private EditText editText3;

    private Geocoder geocoder;
    private MediaPlayer mediaPalyer;
    private MediaRecorder mediaRecorder;
    private  static String recordFolder ="aanitteet";
    private static final String LOG_TAG = "TexInput";
    private boolean rec=true;
    public RecordButton rb =null;
    private ImageButton button3= null;

    private  File

    //file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
    pathToRecord=Environment.getExternalStorageDirectory();
    private File records = new File(pathToRecord,recordFolder);


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View promptsView = inflater.inflate(R.layout.activity_text_input, null);
        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
       File file;
        file = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_ALARMS);
         if(file ==null) {
             file = new File(Environment.getExternalStoragePublicDirectory(
                     Environment.DIRECTORY_ALARMS), recordFolder);
             if (!file.mkdirs()) {
                 Log.e(LOG_TAG, "Directory not created");
             }

         }
        builder.setView(promptsView);
        textview = (TextView) promptsView
                .findViewById(R.id.note);
        editText2 = (EditText) promptsView
                .findViewById(R.id.note2);
        editText3 =(EditText) promptsView.findViewById(R.id.note3);


        editText3.addTextChangedListener(this);
         button3= (ImageButton) promptsView.findViewById(R.id.button_record);
        
        button3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
               if(rec)
                    textview.setText("Start recording");
                else
                    textview.setText("Stop recording");
                onRecord(rec);
            }
        });
        

        builder.setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                    //sendMessage();
                    // When button is clicked, call up to owning activity.
                    ((MapsActivity)getActivity()).positiveBuotton();
            }
        })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        TextInput.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
    //Record to wanted note
    private void onRecord(boolean rec) {
        if(rec)
        startRecording();
        else
            stopRecording();

    }

    private void stopRecording() {
      try {
          mediaRecorder.stop();
          mediaRecorder.reset();
          mediaRecorder.release();
          mediaRecorder=null;
        rec =true;
      }catch(IllegalStateException is){

          Log.e(LOG_TAG,"Stop is not enable");
      }

    }

    private void startRecording() {
          //TimeStamp for right mediafile name
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM:dd:HH:mm");
        String currentTimeStamp = dateFormat.format(new Date());
        String outputfile;
        outputfile= pathToRecord.getAbsolutePath()+"/" +currentTimeStamp+".3gp";
         //Conf recorder
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(outputfile);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try{
            mediaRecorder.prepare();



        }catch (IOException e){
            e.printStackTrace();
            Log.e(LOG_TAG, "prepare() failed ");
        }
        mediaRecorder.start();
        rec=false;
    }

    public void setgeoCoder(Geocoder geo){
        this.geocoder = geo;

    }
   /* private void sendMessage() throws IOException {
        mListener.onDialogPositiveClick(this);
    }*/

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        String text =s.toString();
        List<Address> addresses = null;
        try {
            addresses =  geocoder.getFromLocationName(text,1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lat ="";
        String longi ="";
        if(addresses.size()>0) {
            try {
                double latitude = addresses.get(0).getLatitude();
                double longitude = addresses.get(0).getLongitude();
                lat = Double.toString(latitude);
                longi = Double.toString(longitude);
            }catch(NullPointerException e){
                e.printStackTrace();
            }
        }
        textview.setText(lat+ " , "+ longi);

        /*String text = s.toString();
        Pattern p = Pattern.compile("[a-zA-Z]");
        Matcher m = p.matcher(text);
        boolean b = m.matches();

        if (b)
            editText2.setError("Käytä vain koordinaatteja x.yyyy,x.yyyy");*/
    }

    public EditText getEditText() {
        return editText2;
    }

    public TextView getTextView(){
        return textview;
    }


 /*   /*//* The activity that creates an instance of this dialog fragment must
 // implement this interface in order to receive event callbacks.
 /*//* Each method passes the DialogFragment in case the host needs to query it. *//**//*
    public interface NoticeDialogListener {
        void onDialogPositiveClick(TextInput dialog) throws IOException;
        void onDialogNegativeClick(TextInput dialog);
    }



    // Use this instance of the interface to deliver action events
    NoticeDialogListener mListener;*/

  /*  // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }*/

    class RecordButton extends android.support.v7.widget.AppCompatButton {
        boolean mStartRecording = true;

        OnClickListener clicker = new OnClickListener() {
            public void onClick(View v) {
                startRecording();
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };

        public RecordButton(Context ctx){
            super(ctx);

            setText("Start recording");
            setOnClickListener(clicker);

        }


    }


}
