package com.example.myapplication;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;


public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    private List<Message> messages;
    private Context context;
    private RecyclerView recyclerView;
    private final String TAG = "MessageAdapter";

    public MessageAdapter(List<Message> messages, Context context, RecyclerView recyclerView) {
        this.messages = messages;
        this.context = context;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public MessageAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.message_view, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MessageAdapter.ViewHolder holder, int position) {
        boolean isUser = messages.get(position).isUser();
        if (isUser) {
            holder.systemText.setVisibility(View.GONE);
            holder.userText.setVisibility(View.VISIBLE);
            holder.userText.setText(messages.get(position).getContent());
        } else {
            holder.userText.setVisibility(View.GONE);
            holder.systemText.setVisibility(View.VISIBLE);
            holder.systemText.setText(messages.get(position).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView userText;
        private TextView systemText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            userText = itemView.findViewById(R.id.userText);
            systemText = itemView.findViewById(R.id.systemText);
        }
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(getItemCount() - 1);
        recyclerView.scrollToPosition(messages.size() - 1);
        Log.d(TAG, "" + getItemCount());
    }
}
