package com.lysofts.mobichat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class PostDetails extends AppCompatActivity {
    ProgressDialog pDialog;
    String post_id;
    int total_comments = 0;
    ProgressBar pBar;
    LinearLayout post_details_layout;
    TextView tvTitle, tvUpdate,tvBody,tvCount;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_details);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        post_id = getIntent().getStringExtra("post_id");

        post_details_layout = findViewById(R.id.post_detail);
        pBar = findViewById(R.id.progress_bar);
        tvTitle = findViewById(R.id.post_detail_title);
        tvUpdate = findViewById(R.id.update_time);
        tvBody = findViewById(R.id.post_detail_body);
        tvCount = findViewById(R.id.comment_count);

        post_details_layout.setVisibility(View.GONE);
        GetPostDetails();
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
        startActivity(new Intent(PostDetails.this,Posts.class));
        PostDetails.this.finish();
        super.onBackPressed();
    }

    private void GetPostDetails(){
        final String DATA = "data", ID = "id", TITLE = "title", BODY = "body", CREATED_AT = "created_at", UPDATED_AT = "updated_at";
        try{
            pDialog = new ProgressDialog(PostDetails.this);
            pDialog.setMessage("Getting post...");
            pDialog.setCancelable(true);
            pDialog.show();
            API.getData("/posts/"+post_id, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {               
                    if (statusCode == 404) {
                        Toast.makeText(PostDetails.this,"Post not found.",Toast.LENGTH_SHORT).show();
                    }
                    if (statusCode == 500) {
                        Toast.makeText(PostDetails.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                    }
                    if (statusCode==200) {
                        try {
                            JSONObject c = response.getJSONObject(DATA);

                            String title = c.getString(TITLE);
                            String body = c.getString(BODY);
                            //String created_at = c.getString(CREATED_AT);
                            String updated_at = c.getString(UPDATED_AT);

                            tvTitle.setText(title);
                            tvUpdate.setText(updated_at);
                            tvBody.setText(body);
                            if (pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            post_details_layout.setVisibility(View.VISIBLE);
                            GetPostComments();
                        }catch(Exception es){
                            es.printStackTrace();
                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    if (pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    Toast.makeText(PostDetails.this,"Could not retrieve post details",Toast.LENGTH_SHORT).show();
                    return;
                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private void GetPostComments(){
        final ArrayList<CommentModel> list = new ArrayList<>();
        final String DATA = "data", ID = "id", LIKE = "like", BODY = "body", CREATED_AT = "created_at", UPDATED_AT = "updated_at";
            pBar.setVisibility(View.VISIBLE);
            API.getData("/posts/comments/"+post_id, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    pBar.setVisibility(View.GONE);
                    if (statusCode==200) {
                        try {
                            JSONArray jsonArray = response.optJSONArray(DATA);
                            total_comments = jsonArray.length();
                            tvCount.setText(total_comments+ (total_comments==1?" Comment":" Comments"));
                            for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject c = jsonArray.getJSONObject(i);

                                    int id = c.getInt(ID);
                                    String body = c.getString(BODY);
                                    int like = c.getInt(LIKE);
                                    String updated_at = c.getString(UPDATED_AT);

                                    list.add(new CommentModel(id, body, like, updated_at));
                            }

                            ListView listView = findViewById(R.id.post_comments_list);
                            CommentAdapter adapter = new CommentAdapter(PostDetails.this,list);
                            listView.setAdapter(adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if (statusCode == 404) {
                        Toast.makeText(PostDetails.this,"Post not found.",Toast.LENGTH_SHORT).show();
                    }
                    if (statusCode == 500) {
                        Toast.makeText(PostDetails.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    pBar.setVisibility(View.GONE);
                    Toast.makeText(PostDetails.this,"Could not retrieve any comments",Toast.LENGTH_SHORT).show();
                }
            });
    }

    public void FocusOnEditText(View view){
        EditText etCommentFocus = findViewById(R.id.et_comment_body);
        etCommentFocus.requestFocus();
    }




    /*Comments actions*/
    public void CreateComment(View view) {
        final String comment_body = ((EditText)findViewById(R.id.et_comment_body)).getText().toString();
        ((EditText)findViewById(R.id.et_comment_body)).setText("");
        if (comment_body.isEmpty()){
            Toast.makeText(PostDetails.this,"Enter the comment text",Toast.LENGTH_SHORT).show();
            return;
        }

        pDialog = new ProgressDialog(PostDetails.this);
        pDialog.setMessage("Sending your comment...");
        pDialog.setCancelable(false);
        pDialog.show();

        try {
            JSONObject jsonParam = new JSONObject();
            jsonParam.put("body", comment_body);


            StringEntity entity = new StringEntity(jsonParam.toString(),"UTF-8");
            API.postData(PostDetails.this,"/comments/post/"+post_id, entity,"application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    if (statusCode==201){
                        new CustomNotification().show(PostDetails.this,006,"Successfully created comment.");
                    }
                    if (statusCode==500){
                        Toast.makeText(PostDetails.this, "An error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                    GetPostComments();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    Toast.makeText(PostDetails.this, "An error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void LikeComment(int comment_id){
        try{
            pDialog = new ProgressDialog(PostDetails.this);
            pDialog.setMessage("Liking this comment...");
            pDialog.setCancelable(true);
            pDialog.show();
            API.patchData(PostDetails.this,"/comments/like/"+comment_id, null,"application/json", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    if(statusCode==200){
                        new CustomNotification().show(PostDetails.this,011,"Successfully created comment");
                    }
                    if(statusCode==404){
                        Toast.makeText(PostDetails.this, "Comment not found", Toast.LENGTH_SHORT).show();
                    }
                    if(statusCode==500){
                        Toast.makeText(PostDetails.this, "An error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                    GetPostComments();
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    Toast.makeText(PostDetails.this, "An error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    public void DeleteComment(int comment_id){
        try{
            pDialog = new ProgressDialog(PostDetails.this);
            pDialog.setMessage("Deleting this comment...");
            pDialog.setCancelable(true);
            pDialog.show();
            API.deleteData(PostDetails.this,"/comments/"+comment_id, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    if(statusCode==200){
                        new CustomNotification().show(PostDetails.this,010,"Successfully deleted comment");
                        GetPostComments();
                    }
                    if(statusCode==404){
                        Toast.makeText(PostDetails.this, "Comment not found", Toast.LENGTH_SHORT).show();
                    }
                    if(statusCode==500){
                        Toast.makeText(PostDetails.this, "An error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    if(pDialog.isShowing()){
                        pDialog.dismiss();
                    }
                    Toast.makeText(PostDetails.this, "An error occurred. Try again later.", Toast.LENGTH_SHORT).show();
                }
            });
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }
}
