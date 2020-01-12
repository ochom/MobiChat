package com.lysofts.mobichat;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class CommentAdapter extends BaseAdapter {

    Context context;
    LayoutInflater layoutInflater;
    ArrayList<CommentModel> list;

    public CommentAdapter(Context context, ArrayList<CommentModel> list) {
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

    public class COMMENT_Holder{
        TextView body,updated_at,like;
        LinearLayout btn_like,btn_delete;
        CardView card;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        CommentAdapter.COMMENT_Holder holder = new CommentAdapter.COMMENT_Holder();
        if (convertView==null){
            convertView = layoutInflater.inflate(R.layout.comment,parent,false);
            holder.card = convertView.findViewById(R.id.comment_card);
            holder.body = convertView.findViewById(R.id.comment_body);
            holder.updated_at = convertView.findViewById(R.id.updated_at);
            holder.like = convertView.findViewById(R.id.comment_like);
            holder.btn_like = convertView.findViewById(R.id.btn_like);
            holder.btn_delete = convertView.findViewById(R.id.btn_delete);
            convertView.setTag(holder);
        }else{
            holder = (CommentAdapter.COMMENT_Holder) convertView.getTag();
        }
        holder.body.setText(list.get(position).getBody());
        holder.updated_at.setText(list.get(position).getUpdated_at());
        holder.like.setText(list.get(position).getLike() + "");//(list.get(position).getLike()==1?" Like":" Likes")

        holder.btn_like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikeComment(list.get(position).getId());
            }
        });

        holder.btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteComment(list.get(position).getId());
            }
        });
        return convertView;
    }

    private void LikeComment(int comment_id) {
        ((PostDetails) context).LikeComment(comment_id);
    }

    private void DeleteComment(final int comment_id) {
        AlertDialog dialog;
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle("Delete comment");
        builder.setMessage("Are you sure you want to delete this comment?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ((PostDetails) context).DeleteComment(comment_id);
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
