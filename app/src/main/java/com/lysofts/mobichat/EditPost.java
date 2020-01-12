package com.lysofts.mobichat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class EditPost extends AppCompatActivity {
    ProgressDialog pDialog;
    TextInputEditText etTitle, etBody;
    String post_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("Edit Post");
        getSupportActionBar().setSubtitle(getIntent().getStringExtra("post_title"));

        post_id = getIntent().getStringExtra("post_id");
        etTitle = findViewById(R.id.post_title);
        etBody = findViewById(R.id.post_body);

        etTitle.setText(getIntent().getStringExtra("post_title"));
        etBody.setText(getIntent().getStringExtra("post_body"));
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
        Intent intent = new Intent(EditPost.this, PostDetails.class);
        intent.putExtra("post_id", post_id);
        startActivity(intent);
        EditPost.this.finish();
    }


    public void updatePost(View view) {
        final String post_title = (((EditText) findViewById(R.id.post_title)).getText()).toString();
        final String post_body = (((EditText) findViewById(R.id.post_body)).getText()).toString();

        if (post_title.isEmpty()) {
            Toast.makeText(EditPost.this, "Enter the title of your post", Toast.LENGTH_SHORT).show();
            return;
        }
        if (post_body.isEmpty()) {
            Toast.makeText(EditPost.this, "Enter the body of your post", Toast.LENGTH_SHORT).show();
            return;
        }

        pDialog = new ProgressDialog(EditPost.this);
        pDialog.setMessage("Updating your post...");
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("title", post_title);
            jsonParam.put("body", post_body);


            StringEntity entity = new StringEntity(jsonParam.toString(),"UTF-8");
            API.patchData(EditPost.this, "/posts/" + post_id, entity, "application/json", new AsyncHttpResponseHandler() {

                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    if (statusCode == 200) {
                        new CustomNotification().show(EditPost.this, 001, "Successfully updated post.");
                        Intent intent = new Intent(EditPost.this, PostDetails.class);
                        intent.putExtra("post_id", post_id);
                        startActivity(intent);
                        EditPost.this.finish();
                    }
                    if (statusCode == 404) {
                        Toast.makeText(EditPost.this,"Post not found.",Toast.LENGTH_SHORT).show();
                    }
                    if (statusCode == 500) {
                        Toast.makeText(EditPost.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if (pDialog.isShowing()) {
                        pDialog.dismiss();
                    }
                    Toast.makeText(EditPost.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}

