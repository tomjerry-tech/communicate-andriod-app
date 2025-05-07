package com.derry.tom_client;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.derry.tom_client.api.RetrofitClient;
import com.derry.tom_client.api.UserApiService;
import com.derry.tom_client.model.CheckUsernameResponse;
import com.derry.tom_client.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";
    private EditText etUsername, etPassword, etEmail, etPhone;
    private Button btnRegister;
    private TextView btnToLogin;
    private UserApiService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 初始化视图
        etUsername = findViewById(R.id.etUsername);
        etPassword = findViewById(R.id.etPassword);
        etEmail = findViewById(R.id.etEmail);
        etPhone = findViewById(R.id.etPhone);
        btnRegister = findViewById(R.id.btnRegister);
        btnToLogin = findViewById(R.id.btnToLogin);

        // 获取API服务
        apiService = RetrofitClient.getUserApiService();

        // 设置注册按钮点击事件
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register();
            }
        });

        // 设置登录按钮点击事件
        btnToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 返回登录页面
                finish();
            }
        });

        // 设置用户名失去焦点事件，检查用户名是否存在
        etUsername.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String username = etUsername.getText().toString().trim();
                    if (!username.isEmpty()) {
                        checkUsername(username);
                    }
                }
            }
        });
    }

    private void register() {
        // 获取用户输入
        String username = etUsername.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();

        // 验证输入
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, "用户名和密码不能为空", Toast.LENGTH_SHORT).show();
            return;
        }

        // 创建注册请求
        User user = new User(username, password, email, phone);
        Call<User> call = apiService.register(user);

        // 发送请求
        call.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful() && response.body() != null) {
                    User registeredUser = response.body();
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    
                    // 注册成功后返回登录页面
                    finish();
                } else {
                    // 处理错误响应
                    Toast.makeText(RegisterActivity.this, "注册失败: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                // 处理网络错误
                Log.e(TAG, "注册请求失败", t);
                Toast.makeText(RegisterActivity.this, "网络错误: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void checkUsername(String username) {
        Call<CheckUsernameResponse> call = apiService.checkUsername(username);
        call.enqueue(new Callback<CheckUsernameResponse>() {
            @Override
            public void onResponse(Call<CheckUsernameResponse> call, Response<CheckUsernameResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    CheckUsernameResponse checkResponse = response.body();
                    if (checkResponse.isExists()) {
                        etUsername.setError("用户名已存在");
                    }
                }
            }

            @Override
            public void onFailure(Call<CheckUsernameResponse> call, Throwable t) {
                Log.e(TAG, "检查用户名请求失败", t);
            }
        });
    }
}