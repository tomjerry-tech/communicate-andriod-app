package com.derry.tom_client.api;

import com.derry.tom_client.model.CheckUsernameResponse;
import com.derry.tom_client.model.LoginResponse;
import com.derry.tom_client.model.Message;
import com.derry.tom_client.model.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserApiService {
    @POST("/api/user/register")
    Call<User> register(@Body User user);

    @POST("/api/user/login")
    Call<LoginResponse> login(@Body User user);

    @GET("/api/user/check/{username}")
    Call<CheckUsernameResponse> checkUsername(@Path("username") String username);
    
    // 添加获取历史消息的接口
    @GET("/api/messages")
    Call<List<Message>> getMessages();
}