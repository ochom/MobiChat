package com.lysofts.mobichat;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class Posts extends AppCompatActivity {
    ShimmerFrameLayout shimmerFrameLayout;

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
                                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        String post_id = String.valueOf(viewList.get(position).getId());
                                        Intent intent = new Intent(Posts.this,PostDetails.class);
                                        intent.putExtra("post_id",post_id);
                                        startActivity(intent);
                                        Posts.this.finish();
                                    }
                                });
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
}
