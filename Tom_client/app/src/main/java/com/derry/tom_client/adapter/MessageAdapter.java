package com.derry.tom_client.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.derry.tom_client.R;
import com.derry.tom_client.model.Message;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    
    private static final int VIEW_TYPE_MY_MESSAGE = 1;
    private static final int VIEW_TYPE_OTHER_MESSAGE = 2;
    private static final int VIEW_TYPE_SYSTEM_MESSAGE = 3;
    
    private List<Message> messageList;
    private String currentUsername;
    private Context context;
    private SimpleDateFormat dateFormat;
    
    public MessageAdapter(Context context, String currentUsername) {
        this.context = context;
        this.currentUsername = currentUsername;
        this.messageList = new ArrayList<>();
        this.dateFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
    }
    
    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        
        if (message.getType() != Message.MessageType.CHAT) {
            return VIEW_TYPE_SYSTEM_MESSAGE;
        } else if (message.getSender().equals(currentUsername)) {
            return VIEW_TYPE_MY_MESSAGE;
        } else {
            return VIEW_TYPE_OTHER_MESSAGE;
        }
    }
    
    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        
        if (viewType == VIEW_TYPE_MY_MESSAGE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_sent, parent, false);
        } else if (viewType == VIEW_TYPE_OTHER_MESSAGE) {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_received, parent, false);
        } else {
            view = LayoutInflater.from(context).inflate(R.layout.item_message_system, parent, false);
        }
        
        return new MessageViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messageList.get(position);
        
        if (getItemViewType(position) == VIEW_TYPE_SYSTEM_MESSAGE) {
            holder.tvMessage.setText(message.getContent());
        } else {
            holder.tvMessage.setText(message.getContent());
            holder.tvTime.setText(dateFormat.format(message.getCreateTime()));
            
            if (getItemViewType(position) == VIEW_TYPE_OTHER_MESSAGE) {
                holder.tvUsername.setText(message.getSender());
            }
        }
    }
    
    @Override
    public int getItemCount() {
        return messageList.size();
    }
    
    public void addMessage(Message message) {
        messageList.add(message);
        notifyItemInserted(messageList.size() - 1);
    }
    
    public void setMessages(List<Message> messages) {
        messageList.clear();
        messageList.addAll(messages);
        notifyDataSetChanged();
    }
    
    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMessage;
        TextView tvTime;
        TextView tvUsername;
        
        MessageViewHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
            tvUsername = itemView.findViewById(R.id.tvUsername);
        }
    }
}