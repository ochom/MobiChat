package com.lysofts.mobichat;

public class PostModel {
    int id;
    String title,body,created_at,updated_at;

    public PostModel(int id, String title, String body, String created_at, String updated_at) {
        this.id = id;
        this.title = title;
        this.body = body;
        this.created_at = created_at;
        this.updated_at = updated_at;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }
}
