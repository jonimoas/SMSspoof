package com.moas.smsspoof;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Telephony;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Context context;
    Button button;
    Button button2;
    EditText editText2;
    EditText editText;
    String sender,body,defaultSmsApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get current context
        context = this;

        //Set composant
        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        editText = (EditText) findViewById(R.id.editText);
        editText2 = (EditText) findViewById(R.id.editText2);
        //Get default sms app
        defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(context);



        //Button to receive to the default sms app
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Get the package name and check if my app is not the default sms app
                final String myPackageName = getPackageName();
                if (!Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {

                    //Change the default sms app to my app
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
                    startActivityForResult(intent, 1);
                }
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                //Get the package name and check if my app is not the default sms app
                final String myPackageName = getPackageName();
                if (!Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {

                    //Change the default sms app to my app
                    Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
                    intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, context.getPackageName());
                    startActivityForResult(intent, 2);
                }
            }
        });
    }

    //Write to the default sms app
    private void SendSms(String message, String phoneNumber) {

        //Put content values
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, phoneNumber);
        values.put(Telephony.Sms.DATE, System.currentTimeMillis());
        values.put(Telephony.Sms.BODY, message);

        //Insert the message
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getContentResolver().insert(Telephony.Sms.Sent.CONTENT_URI, values);
        }
        else {
            context.getContentResolver().insert(Uri.parse("content://sms/sent"), values);
        }

        //Change my sms app to the last default sms
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
        context.startActivity(intent);
    }

    private void ReceiveSms(String message, String phoneNumber) {

        //Put content values
        ContentValues values = new ContentValues();
        values.put(Telephony.Sms.ADDRESS, phoneNumber);
        values.put(Telephony.Sms.DATE, System.currentTimeMillis());
        values.put(Telephony.Sms.BODY, message);

        //Insert the message
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            context.getContentResolver().insert(Telephony.Sms.Inbox.CONTENT_URI, values);
        }
        else {
            context.getContentResolver().insert(Uri.parse("content://sms/inbox"), values);
        }

        //Change my sms app to the last default sms
        Intent intent = new Intent(Telephony.Sms.Intents.ACTION_CHANGE_DEFAULT);
        intent.putExtra(Telephony.Sms.Intents.EXTRA_PACKAGE_NAME, defaultSmsApp);
        context.startActivity(intent);
    }

    //Get result from default sms dialog pops up
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

                final String myPackageName = getPackageName();
                if (Telephony.Sms.getDefaultSmsPackage(context).equals(myPackageName)) {

                    //Write to the default sms app
                    //Set the number and the body for the sms
                    sender = editText.getText().toString();
                    body = editText2.getText().toString();
                    if (requestCode == 1) {
                        ReceiveSms(body, sender);
                    }else{
                        if (requestCode == 2) {
                            SendSms(body, sender);
                        }
                    }
                }
            }
    }
}
