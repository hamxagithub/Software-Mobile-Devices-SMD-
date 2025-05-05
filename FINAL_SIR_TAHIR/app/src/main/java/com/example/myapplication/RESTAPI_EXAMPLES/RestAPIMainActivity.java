package com.example.myapplication.RESTAPI_EXAMPLES;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myapplication.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RestAPIMainActivity extends AppCompatActivity {
MyRetrofit myRetrofit;
RecyclerView recyclerView;
MyControllerInterface myController;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_rest_apimain);
        recyclerView=findViewById(R.id.myrestrecyclerview);

        //GetRecords();
       // GetComments();
        //GetRecordAgaimstID();
        //getCommentsQuery();
     //   getCommentsNameQuery();
        creatMeyPost();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    private void GetRecordAgaimstID() {
        myController=MyRetrofit.ConnectRetrofit().create(MyControllerInterface.class);
        Call<List<MyCommentsModel>> list =myController.getComments(2);
        list.enqueue(new Callback<List<MyCommentsModel>>() {
            @Override
            public void onResponse(Call<List<MyCommentsModel>> call, Response<List<MyCommentsModel>> response) {
                if(response.isSuccessful())
                {
                    for(MyCommentsModel comments : response.body())
                    {
                        Log.d("TAG","Post ID "+comments.getPostId()+"ID : "+comments.getId()+"User Name : "+comments.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyCommentsModel>> call, Throwable t) {

            }
        });

            }

    private void GetComments() {
        myController=MyRetrofit.ConnectRetrofit().create(MyControllerInterface.class);
        Call<List<MyCommentsModel>> list =myController.getComments();
        list.enqueue(new Callback<List<MyCommentsModel>>() {
            @Override
            public void onResponse(Call<List<MyCommentsModel>> call, Response<List<MyCommentsModel>> response) {
               if(response.isSuccessful())
               {
                 for(MyCommentsModel comments : response.body())
                 {
                     Log.d("TAG","Post ID "+comments.getPostId()+"ID : "+comments.getId()+"User Name : "+comments.getName());
                 }
               }

            }

            @Override
            public void onFailure(Call<List<MyCommentsModel>> call, Throwable t) {

            }
        });
    }

    private void GetRecords() {
        myController=MyRetrofit.ConnectRetrofit().create(MyControllerInterface.class);
        myController.getAllPost().enqueue(new Callback<List<MyModelClass>>() {
            @Override
            public void onResponse(Call<List<MyModelClass>> call, Response<List<MyModelClass>> response) {
              if(response.body().size()>0)
              {
                  Toast.makeText(RestAPIMainActivity.this, "DATA RECEIVED", Toast.LENGTH_SHORT).show();
                  List<MyModelClass> list=response.body();
                  MyRestAdapter myRestAdapter=new MyRestAdapter(RestAPIMainActivity.this,list);
                  LinearLayoutManager linearLayoutManager=new LinearLayoutManager(RestAPIMainActivity.this);
                  recyclerView.setLayoutManager(linearLayoutManager);
                  recyclerView.setAdapter(myRestAdapter);
              }
            }

            @Override
            public void onFailure(Call<List<MyModelClass>> call, Throwable t) {

            }
        });
    }
    private void getCommentsQuery()
    {
        myController=MyRetrofit.ConnectRetrofit().create(MyControllerInterface.class);
        myController.getCommentsByQuery(1).enqueue(new Callback<List<MyCommentsModel>>() {
            @Override
            public void onResponse(Call<List<MyCommentsModel>> call, Response<List<MyCommentsModel>> response) {
                if(response.isSuccessful())
                {
                    for(MyCommentsModel comments : response.body())
                    {
                        Log.d("TAG","Post ID "+comments.getPostId()+"ID : "+comments.getId()+"User Name : "+comments.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyCommentsModel>> call, Throwable t) {

            }
        });
    }
    private void getCommentsNameQuery()
    {
        myController=MyRetrofit.ConnectRetrofit().create(MyControllerInterface.class);
        myController.getCommentsNameByQuery("id labore ex et quam laborum").enqueue(new Callback<List<MyCommentsModel>>() {
            @Override
            public void onResponse(Call<List<MyCommentsModel>> call, Response<List<MyCommentsModel>> response) {
                if(response.isSuccessful())
                {
                    for(MyCommentsModel comments : response.body())
                    {
                        Log.d("TAG","Post ID "+comments.getPostId()+"ID : "+comments.getId()+"User Name : "+comments.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyCommentsModel>> call, Throwable t) {

            }
        });
    }
private void getCommentsBySort()
{
    //Map<String,String> arguments=new HashMap<>();
   // arguments.put("postId","1");
   // arguments.put("_order","id");
   // //arguments.put("_sort","desc");
    Call<List<MyCommentsModel>> list=myController.getCommentsBySortQuery(1,"id","desc");
    list.enqueue(new Callback<List<MyCommentsModel>>() {
    @Override
    public void onResponse(Call<List<MyCommentsModel>> call, Response<List<MyCommentsModel>> response) {
        if(response.isSuccessful())
        {
            for(MyCommentsModel comments : response.body())
            {
                Log.d("TAG","Post ID "+comments.getPostId()+"ID : "+comments.getId()+"User Name : "+comments.getName());
            }
        }
    }

    @Override
    public void onFailure(Call<List<MyCommentsModel>> call, Throwable t) {

    }
});
}
    private void getCommentsByMap()
    {
        Map<String,String> arguments=new HashMap<>();
        arguments.put("postId","1");
        arguments.put("_order","id");
        arguments.put("_sort","desc");
        Call<List<MyCommentsModel>> list=myController.getCommentsMapByQuery(arguments);
        list.enqueue(new Callback<List<MyCommentsModel>>() {
            @Override
            public void onResponse(Call<List<MyCommentsModel>> call, Response<List<MyCommentsModel>> response) {
                if(response.isSuccessful())
                {
                    for(MyCommentsModel comments : response.body())
                    {
                        Log.d("TAG","Post ID "+comments.getPostId()+"ID : "+comments.getId()+"User Name : "+comments.getName());
                    }
                }
            }

            @Override
            public void onFailure(Call<List<MyCommentsModel>> call, Throwable t) {

            }
        });
    }
    private void creatMeyPost()
    {
        MyModelClass obj =new MyModelClass(10,2,"Title","Body");

        Call<MyModelClass> list=myController.createpost(obj);
        list.enqueue(new Callback<MyModelClass>() {
            @Override
            public void onResponse(Call<MyModelClass> call, Response<MyModelClass> response) {
                if(response.isSuccessful())
                {


                        Log.d("TAG","Post ID "+response.body().getId()+"ID : "+response.body().getId()+"User Name : "+response.body().getTitle());
                }
            }

            @Override
            public void onFailure(Call<MyModelClass> call, Throwable t) {

            }
        });




    }
}