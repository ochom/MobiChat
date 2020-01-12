package com.lysofts.mobichat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class CreatePost extends AppCompatActivity {
    ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Create Post");

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        CreatePost.this.finish();
        startActivity(new Intent(CreatePost.this, Posts.class));
    }

    public void createPost(View view) {
        final String post_title = (((EditText) findViewById(R.id.post_title)).getText()).toString();
        final String post_body = (((EditText) findViewById(R.id.post_body)).getText()).toString();

        if (post_title.isEmpty()){
            Toast.makeText(CreatePost.this,"Enter the title of your post",Toast.LENGTH_SHORT).show();
            return;
        }
        if (post_body.isEmpty()){
            Toast.makeText(CreatePost.this,"Enter the body of your post",Toast.LENGTH_SHORT).show();
            return;
        }

        pDialog = new ProgressDialog(CreatePost.this);
        pDialog.setMessage("Creating your post...");
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("title", post_title);
            jsonParam.put("body", post_body);


            StringEntity entity = new StringEntity(jsonParam.toString(), "UTF-8");
            API.postData(CreatePost.this,"/posts", entity,"application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    if (statusCode==201){
                        new CustomNotification().show(CreatePost.this,001,"Your post has been sent successfully");startActivity(new Intent(CreatePost.this,Posts.class));
                        CreatePost.this.finish();
                        startActivity(new Intent(CreatePost.this,Posts.class));
                    }
                    if (statusCode==500){
                        Toast.makeText(CreatePost.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    Toast.makeText(CreatePost.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
