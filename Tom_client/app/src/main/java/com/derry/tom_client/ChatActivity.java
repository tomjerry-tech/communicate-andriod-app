package com.derry.tom_client;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.derry.tom_client.adapter.MessageAdapter;
import com.derry.tom_client.api.RetrofitClient;
import com.derry.tom_client.model.Message;
import com.derry.tom_client.model.User;
import com.derry.tom_client.websocket.WebSocketClient;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatActivity extends AppCompatActivity implements WebSocketClient.WebSocketListener {

    private static final String TAG = "ChatActivity";
    
    private CircleImageView profileImage;
    private TextView tvUsername;
    private RecyclerView recyclerView;
    private EditText etMessage;
    private Button btnSend;
    
    private User currentUser;
    private MessageAdapter messageAdapter;
    private WebSocketClient webSocketClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        // 初始化视图
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        
        profileImage = findViewById(R.id.profileImage);
        tvUsername = findViewById(R.id.tvUsername);
        recyclerView = findViewById(R.id.recyclerView);
        etMessage = findViewById(R.id.etMessage);
        btnSend = findViewById(R.id.btnSend);

        // 获取传递的用户信息
        if (getIntent().hasExtra("user")) {
            currentUser = (User) getIntent().getSerializableExtra("user");
            tvUsername.setText(currentUser.getUsername());
        }

        // 使用Glide加载头像
        loadProfileImage();
        
        // 设置RecyclerView
        setupRecyclerView();
        
        // 设置发送按钮点击事件
        setupSendButton();
        
        // 连接WebSocket
        connectWebSocket();
        
        // 加载历史消息
        loadHistoryMessages();
    }
    
    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        messageAdapter = new MessageAdapter(this, currentUser.getUsername());
        recyclerView.setAdapter(messageAdapter);
    }
    
    private void setupSendButton() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = etMessage.getText().toString().trim();
                if (!content.isEmpty()) {
                    sendMessage(content);
                    etMessage.setText("");
                }
            }
        });
    }
    
    private void connectWebSocket() {
        webSocketClient = new WebSocketClient(this);
        webSocketClient.connect();
    }
    
    private void sendMessage(String content) {
        if (webSocketClient != null && webSocketClient.isConnected()) {
            Message message = new Message(content, currentUser.getUsername(), Message.MessageType.CHAT);
            webSocketClient.sendMessage(message);
        } else {
            Toast.makeText(this, "WebSocket未连接，请稍后再试", Toast.LENGTH_SHORT).show();
            // 尝试重新连接
            connectWebSocket();
        }
    }
    
    private void loadHistoryMessages() {
        RetrofitClient.getUserApiService().getMessages().enqueue(new Callback<List<Message>>() {
            @Override
            public void onResponse(Call<List<Message>> call, Response<List<Message>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    messageAdapter.setMessages(response.body());
                    // 滚动到最新消息
                    recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<List<Message>> call, Throwable t) {
                Log.e(TAG, "加载历史消息失败", t);
                Toast.makeText(ChatActivity.this, "加载历史消息失败: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadProfileImage() {
        // 使用其他可访问的图片URL
        String imageUrl = "https://tse1-mm.cn.bing.net/th/id/OIP-C.7GLMYPqMlt2LgkbPsOnDIAAAAA?cb=iwc1&rs=1&pid=ImgDetMain";
        
        // 添加日志输出
        Log.d(TAG, "正在加载图片: " + imageUrl);
        
        Glide.with(this)
                .load(imageUrl)
                .placeholder(R.drawable.ic_launcher_foreground) // 使用默认图片作为占位符
                .error(R.drawable.ic_launcher_foreground) // 加载失败时显示的图片
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "图片加载失败", e);
                        Toast.makeText(ChatActivity.this, "图片加载失败: " + (e != null ? e.getMessage() : "未知错误"), Toast.LENGTH_SHORT).show();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "图片加载成功");
                        return false;
                    }
                })
                .into(profileImage);
    }
    
    // WebSocketListener接口实现
    @Override
    public void onConnect() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, "已连接到聊天服务器", Toast.LENGTH_SHORT).show();
                // 发送加入消息
                Message joinMessage = new Message(currentUser.getUsername() + "加入了聊天", currentUser.getUsername(), Message.MessageType.JOIN);
                webSocketClient.sendMessage(joinMessage);
            }
        });
    }

    @Override
    public void onMessageReceived(final Message message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                messageAdapter.addMessage(message);
                recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
            }
        });
    }

    @Override
    public void onDisconnect(int code, String reason) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, "与聊天服务器断开连接: " + reason, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onError(Exception ex) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(ChatActivity.this, "WebSocket错误: " + ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 断开WebSocket连接
        if (webSocketClient != null) {
            // 发送离开消息
            Message leaveMessage = new Message(currentUser.getUsername() + "离开了聊天", currentUser.getUsername(), Message.MessageType.LEAVE);
            webSocketClient.sendMessage(leaveMessage);
            // 断开连接
            webSocketClient.disconnect();
        }
    }
}