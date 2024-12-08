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
import com.photex.tiktok.models.AudioInfo;

import java.util.ArrayList;

public class AllAudiosAdapter extends RecyclerView.Adapter<AllAudiosAdapter.ViewHolder> {
    private OnItemClickListner onItemClickListner;
    private ArrayList<AudioInfo> audioList = new ArrayList<>();
    Context context;
    public static int lastPositionClicked = -1;

    public AllAudiosAdapter(Context context) {
        this.context = context;
    }

    public AllAudiosAdapter(int context) {


    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.server_audios_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        holder.tvTitle.setText(audioList.get(position).getTitle());
        holder.tvOwner.setText(audioList.get(position).getOwner());
        holder.tvDuration.setText(audioList.get(position).getDuration() + " sec");

        if (audioList.get(position).isShootBtnVisible()) {
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

    public ArrayList<AudioInfo> getAudioList() {
        return audioList;
    }

    public void setAudioList(ArrayList<AudioInfo> audioList) {
        this.audioList = audioList;
    }

    public AllAudiosAdapter setOnItemClickListner(OnItemClickListner onItemClickListner) {
        this.onItemClickListner = onItemClickListner;
        return this;
    }

    public void updateItem(int position) {
        audioList.get(position).setShootbtnVisible(!audioList.get(position).isShootBtnVisible());
        notifyItemChanged(position);
    }

    public interface OnItemClickListner {
        void onShootBtnClicked(AudioInfo audioInfo, int position);

        void onItemClicked(AudioInfo audioInfo, int position);
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




