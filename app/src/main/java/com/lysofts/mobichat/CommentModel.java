package com.lysofts.mobichat;

public class CommentModel {
    int id;
    String body;
    int like;
    String updated_at;

    public CommentModel(int id, String body, int like, String updated_at) {
        this.id = id;
        this.body = body;
        this.like = like;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public String getBody() {
        return body;
    }

    public int getLike() {
        return like;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
