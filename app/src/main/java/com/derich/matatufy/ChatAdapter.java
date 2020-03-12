package com.derich.matatufy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ViewHolder>  {
    private List<Chats> chatsList;
    private Context mContext;
    private ChatAdapter.OnChatClickListener onChatClickListener;
    private long secs;
    private int nanos;

    public ChatAdapter(List<Chats> chatsList, ChatAdapter.OnChatClickListener onChatClickListener){
        this.onChatClickListener = onChatClickListener;
        this.chatsList = chatsList;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public ChatAdapter.ViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_chat,parent,false);
        mContext = parent.getContext();
        return new ChatAdapter.ViewHolder(view,onChatClickListener);
    }
    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ViewHolder holder, int position) {
       // holder.setIsRecyclable(false);
        Chats rI = chatsList.get(position);
        String email = rI.getUserEmail();
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        Map<String, Object> timestamp = rI.getTimestamp();
        Object time = timestamp.get("timestamp");
        String timeS = String.valueOf(time);
        Pattern pattern = Pattern.compile("seconds=\\w+");
        Matcher matcher = pattern.matcher(timeS.toLowerCase());
        if (matcher.find()){
            secs = Long.parseLong(encodeSecs(matcher.group()));
        }
        Pattern SecondsP = Pattern.compile("nanoseconds=\\w+");
        Matcher matcherNano = SecondsP.matcher(timeS.toLowerCase());
        if (matcherNano.find()){
            nanos = Integer.parseInt(encodeNano(matcherNano.group()));
        }
        Timestamp tStamp = new Timestamp(secs,nanos);
        Date date = tStamp.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yy HH:mm");
        String dated = sdf.format(date);

            if (!(email.equals(mUser.getEmail()))) {
                holder.from.setText(rI.getMessage());
                holder.from_date.setText(dated);
                holder.sender.setText(rI.getUserEmail());
                holder.to.setVisibility(View.GONE);
                holder.to_date.setVisibility(View.GONE);
            } else {
                holder.to.setText(rI.getMessage());
                holder.to_date.setText(dated);
                holder.from.setVisibility(View.GONE);
                holder.from_date.setVisibility(View.GONE);
                holder.sender.setVisibility(View.GONE);
            }
    }
    @Override
    public int getItemCount() {
        return chatsList.size();
    }
    public static String encodeSecs(String coOrdns){
        return coOrdns.replace("seconds=","");
    }
    public static String encodeNano(String coOrdns){
        return coOrdns.replace("nanoseconds=","");
    }
    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView from,to,from_date,to_date,sender;
        ChatAdapter.OnChatClickListener onChatClickListener;
        public ViewHolder(View itemView, ChatAdapter.OnChatClickListener onChatClickListener) {
            super(itemView);
            this.onChatClickListener = onChatClickListener;
            from= itemView.findViewById(R.id.textview_chat_received);
            from_date= itemView.findViewById(R.id.textview_chat_received_date);
            to= itemView.findViewById(R.id.textview_chat_sent);
            sender = itemView.findViewById(R.id.tvSenderUsername);
            to_date= itemView.findViewById(R.id.textview_chat_sent_date);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onChatClickListener.onChatClick(getAdapterPosition());

        }
    }
    public interface OnChatClickListener{
        void onChatClick(int position);
    }
}
