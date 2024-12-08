package com.photex.tiktok.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.photex.tiktok.R;
import com.photex.tiktok.models.ResponseAudioInfo;
import com.photex.tiktok.models.ResponseAudioInfoOld;

import java.util.ArrayList;

public class ServerAudiosAdapter extends RecyclerView.Adapter<ServerAudiosAdapter.ViewHolder> {
    private OnItemClickListner onItemClickListner;
    private ArrayList<ResponseAudioInfo.SingleAudioInfo> audioList = new ArrayList<>();
    Context context;
    public static int lastPositionClicked = -1;

    public ServerAudiosAdapter(Context context) {
        this.context = context;
    }

    public ServerAudiosAdapter(int context) {
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.server_audios_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
//        audioList.get(position).setAudioUrl(Constants.STREAMING_URL + "/tiktok/9NyUzayBA/islamic01.mp3");

        holder.tvTitle.setText(audioList.get(position).getTitle());
        holder.tvOwner.setText(audioList.get(position).getUserName());
        holder.tvDuration.setText(audioList.get(position).getDuration() + " sec");

        if (audioList.get(position).isShootbtnVisible()) {
            holder.shootBtn.setVisibility(View.VISIBLE);
            holder.playPauseBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.pause_icon));

        } else {
            holder.shootBtn.setVisibility(View.GONE);
            holder.playPauseBtn.setImageDrawable(context.getResources().getDrawable(R.drawable.play_icon));
        }

        holder.itemLayout.setOnClickListener(view -> {
            onItemClickListner.onItemClicked(audioList.get(position), position);
            updateItem(position);
        });

        holder.shootBtn.setOnClickListener(view ->
                onItemClickListner.onShootBtnClicked(audioList.get(position), position));
    }

    @Override
    public int getItemCount() {
        return audioList.size();
    }

    public String getCommentId(int position) {
        return audioList.get(position).get_id();
    }

    public ArrayList<ResponseAudioInfo.SingleAudioInfo> getAudioList() {
        return audioList;
    }

    public void setAudioList(ArrayList<ResponseAudioInfo.SingleAudioInfo> audioList) {
        this.audioList = audioList;
    }

    public ServerAudiosAdapter setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
        return this;
    }

    public void updateItem(int position) {
        audioList.get(position).setShootbtnVisible(!audioList.get(position).isShootbtnVisible());
        notifyItemChanged(position);
    }

    public interface OnItemClickListner {
        void onShootBtnClicked(ResponseAudioInfo.SingleAudioInfo audioInfo, int position);

        void onItemClicked(ResponseAudioInfo.SingleAudioInfo audioInfo, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView thumbnail, playPauseBtn;
        TextView tvTitle, tvOwner, tvDuration;
        RelativeLayout shootBtn;
        LinearLayout itemLayout;


        ViewHolder(View view) {
            super(view);
            thumbnail = view.findViewById(R.id.thumbnail_image);
            playPauseBtn = view.findViewById(R.id.play_pause_image);
            tvTitle = view.findViewById(R.id.tvPickSound);
            tvTitle.setSelected(true);
            tvOwner = view.findViewById(R.id.tv_audio_owner);
            tvOwner.setSelected(true);
            tvDuration = view.findViewById(R.id.tv_audio_duration);

            itemLayout = view.findViewById(R.id.item_layout);
            shootBtn = view.findViewById(R.id.shoot_btn);
        }
    }
}




