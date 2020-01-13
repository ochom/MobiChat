package com.lysofts.mobichat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Posts extends AppCompatActivity {
    ShimmerFrameLayout shimmerFrameLayout;
    ProgressDialog pDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_posts);

        getSupportActionBar().setTitle("MobiChat");
        getSupportActionBar().setSubtitle("All posts");

        shimmerFrameLayout = findViewById(R.id.posts_shimmer);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Posts.this,CreatePost.class));
                Posts.this.finish();
            }
        });

        GetAllPosts();
    }

    private void GetAllPosts(){
        final ArrayList<PostModel> viewList = new ArrayList<>();
        final String DATA = "data", ID = "id", TITLE = "title", BODY = "body", CREATED_AT = "created_at", UPDATED_AT = "updated_at";
        shimmerFrameLayout.startShimmer();
            API.getData("/posts", null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    if (statusCode==500) {
                        Toast.makeText(Posts.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (statusCode==200) {
                        JSONArray jsonArray = response.optJSONArray(DATA);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            try {
                                JSONObject c = jsonArray.getJSONObject(i);
                                int id = c.getInt(ID);
                                String title = c.getString(TITLE);
                                String body = c.getString(BODY);
                                String created_at = c.getString(CREATED_AT);
                                String updated_at = c.getString(UPDATED_AT);

                                viewList.add(new PostModel(id, title, body, created_at, updated_at));

                                ListView listView = findViewById(R.id.posts_list);
                                PostListAdapter adapter = new PostListAdapter(Posts.this,viewList);
                                listView.setAdapter(adapter);
                            }catch(Exception es){
                                es.printStackTrace();
                            }

                        }
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    shimmerFrameLayout.stopShimmer();
                    shimmerFrameLayout.setVisibility(View.GONE);
                    Toast.makeText(Posts.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                }
            });

    }

    public void ViewPost(int post_id) {
        Intent intent = new Intent(Posts.this,PostDetails.class);
        intent.putExtra("post_id",String.valueOf(post_id));
        startActivity(intent);
        Posts.this.finish();
    }

    public void EditPost(int post_id, String title, String body) {
        Intent intent = new Intent(Posts.this,EditPost.class);
        intent.putExtra("post_id", String.valueOf(post_id));
        intent.putExtra("post_title", title);
        intent.putExtra("post_body", body);
        startActivity(intent);
        Posts.this.finish();
    }

    public void DeletePost(final int post_id) {
        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(Posts.this);
        builder.setCancelable(true);
        builder.setTitle("Delete comment");
        builder.setMessage("Are you sure you want to delete this post?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                try{
                    pDialog = new ProgressDialog(Posts.this);
                    pDialog.setMessage("Deleting post...");
                    pDialog.setCancelable(false);
                    pDialog.show();
                    API.deleteData(Posts.this,"/posts/"+post_id, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (pDialog.isShowing()){
                                pDialog.dismiss();
                            }
                            if (statusCode==200){
                                new CustomNotification().show(Posts.this,001,"Your post has been deleted successfully");
                            }
                            if (statusCode == 404) {
                                Toast.makeText(Posts.this,"Post not found.",Toast.LENGTH_SHORT).show();
                            }
                            if (statusCode == 500) {
                                Toast.makeText(Posts.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                            }
                            GetAllPosts();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(Posts.this,"An error occurred. Try again later.",Toast.LENGTH_SHORT).show();
                        }
                    });
                }catch(Exception ex){
                    ex.printStackTrace();
                }
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        dialog = builder.create();
        dialog.show();
    }
}
