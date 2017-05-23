package com.loughmasta.loughapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static EditText titleTextEdit, commentTextEdit, albumIdTextEdit;
    public FloatingActionButton sendButton;
    public TelephonyManager tm;
    public String imei;
    public static RequestQueue queue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Used to get IMEI for verification
        tm = (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        //imei = tm.getDeviceId(); **NOT WORKING**

        // Volley
        queue = Volley.newRequestQueue(this);

        titleTextEdit = (EditText) findViewById(R.id.title);
        albumIdTextEdit = (EditText) findViewById(R.id.albumId);
        commentTextEdit = (EditText) findViewById(R.id.comment);

        sendButton = (FloatingActionButton) findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SendContentToServer(albumIdTextEdit.getText().toString(), view);
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

    private static void SendContentToServer(final String albumId, final View view) {

        String url = "http://loughmasta.com/submitImgurAlbum.php";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Snackbar.make(view, "Success", Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Snackbar.make(view, "Failed to Send: " + error.getMessage(), Snackbar.LENGTH_LONG).setAction("Action", null).show();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("albumId", albumId);

                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(stringRequest);
    }
}
