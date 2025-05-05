package com.example.myapplication.RESTAPI_EXAMPLES;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface MyControllerInterface {
    @GET("posts")
    Call<List<MyModelClass>> getAllPost();
    @GET("posts/1/comments")
    Call<List<MyCommentsModel>> getComments();
    @GET("posts/{id}/comments")
    Call<List<MyCommentsModel>> getComments(@Path("id") int id);
    @GET("comments")
    Call<List<MyCommentsModel>> getCommentsByQuery(@Query("postId") int id);
    @GET("comments")
    Call<List<MyCommentsModel>> getCommentsNameByQuery(@Query("name") String name);
    @GET("comments")
    Call<List<MyCommentsModel>> getCommentsBySortQuery(@Query("postId") int id,
                                                       @Query("_sort") String sort,
                                                       @Query("order" )String byorder);

    @GET("comments")
    Call<List<MyCommentsModel>> getCommentsMapByQuery(@QueryMap Map<String,String> arguments);
@POST("posts")
    Call<MyModelClass> createpost(@Body MyModelClass post);
}
