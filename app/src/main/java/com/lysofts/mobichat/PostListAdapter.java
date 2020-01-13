package com.lysofts.mobichat;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
        LinearLayout btn_view, btn_edit, btn_delete;
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
            holder.btn_view = convertView.findViewById(R.id.btn_view);
            holder.btn_edit = convertView.findViewById(R.id.btn_edit);
            holder.btn_delete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        }else{
            holder = (PostListAdapter.POSTS_Holder) convertView.getTag();
        }

        holder.title.setText(list.get(position).getTitle());
        holder.post_time.setText(list.get(position).getUpdated_at());
        holder.body.setText(list.get(position).getBody());

        holder.btn_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Posts) context).ViewPost(list.get(position).getId());
            }
        });
        holder.btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Posts) context).EditPost(list.get(position).getId(),list.get(position).getTitle(),list.get(position).getBody());
            }
        });
        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Posts) context).DeletePost(list.get(position).getId());
            }
        });
        return convertView;
    }

}
