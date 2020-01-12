package com.lysofts.mobichat;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class PostListAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<PostModel> list;

    public PostListAdapter(Context context, ArrayList<PostModel> list) {
        this.context = context;
        this.list = list;
        layoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class POSTS_Holder{
        TextView title,post_time,body;
        CardView card;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        PostListAdapter.POSTS_Holder holder = new PostListAdapter.POSTS_Holder();
        if (convertView==null){
            convertView = layoutInflater.inflate(R.layout.post,parent,false);
            holder.card = convertView.findViewById(R.id.post_card);
            holder.title = convertView.findViewById(R.id.post_title);
            holder.post_time = convertView.findViewById(R.id.post_time);
            holder.body = convertView.findViewById(R.id.post_body);
            convertView.setTag(holder);
        }else{
            holder = (PostListAdapter.POSTS_Holder) convertView.getTag();
        }

        holder.title.setText(list.get(position).getTitle());
        holder.post_time.setText(list.get(position).getUpdated_at());
        holder.body.setText(list.get(position).getBody());

        return convertView;
    }

}
