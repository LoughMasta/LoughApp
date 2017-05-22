package com.loughmasta.loughapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.EOFException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

public class MainActivity extends AppCompatActivity {

    public EditText titleTextEdit, commentTextEdit, albumIdTextEdit;
    public FloatingActionButton sendButton;
    public TelephonyManager tm;
    public String imei;
    public SendContentToServer sendContentToServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Used to get IMEI for verification
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //imei = tm.getDeviceId(); **NOT WORKING**

        sendContentToServer = new SendContentToServer();

        titleTextEdit = (EditText) findViewById(R.id.title);
        albumIdTextEdit = (EditText) findViewById(R.id.albumId);
        commentTextEdit = (EditText) findViewById(R.id.comment);

        sendButton = (FloatingActionButton) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    Exception tempException = sendContentToServer.execute(albumIdTextEdit.getText().toString()).get();
                    if (tempException == null) {
                        Snackbar.make(view, "Success", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                    }
                } catch (Exception e){
                    Snackbar.make(view, "Failed To Send To Server: " + e.toString(), Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null){
            if ("text/plain".equals(type)){
                handleSendText(intent);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void handleSendText(Intent intent){
        String content = intent.getStringExtra("android.intent.extra.TEXT");
        ImgurPost contentIP = new ImgurPost(StringEditor.extractUrl(content));
        titleTextEdit.setText(contentIP.getTitle(), TextView.BufferType.EDITABLE);
        albumIdTextEdit.setText(contentIP.getPostId().toString(), TextView.BufferType.EDITABLE);
    }

    private class SendContentToServer extends AsyncTask<String, Integer, Exception>{

        @Override
        protected Exception doInBackground(String... albumId){

            try{
                //Creates JSON object
                JSONObject tempObj = new JSONObject();
                tempObj.put("albumId", albumId);

                //Creates connection to URL
                URL targetUrl = new URL("http://loughmasta.com/submitImgurAlbum.php");
                HttpURLConnection conn = (HttpURLConnection) targetUrl.openConnection();
                conn.setDoInput(true);
                conn.setRequestMethod("POST");
                conn.setConnectTimeout(500);
                conn.setRequestProperty("Content_Type", "application/json; charset=UTF-8");
                OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
                writer.write(tempObj.toString());
                writer.flush();
                writer.close();
            } catch (Exception e){
                return  e;
            }
            return null;
        }
    }
}
