package com.example.myapplication.RESTAPI_EXAMPLES;

public class MyCommentsModel {
    private float postId;
    private float id;
    private String name;
    private String email;
    private String body;


    // Getter Methods

    public float getPostId() {
        return postId;
    }

    public float getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getBody() {
        return body;
    }

    // Setter Methods

    public void setPostId(float postId) {
        this.postId = postId;
    }

    public void setId(float id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
