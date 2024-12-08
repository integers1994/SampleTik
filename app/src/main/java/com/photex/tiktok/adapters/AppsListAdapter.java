package com.photex.tiktok.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.photex.tiktok.R;
import com.photex.tiktok.interfaces.AppClickListener;
import com.photex.tiktok.models.AppsInfo;
import com.photex.tiktok.utils.Util;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class AppsListAdapter extends RecyclerView.Adapter<AppsListAdapter.ViewHolder> {

    private Context context;
    ArrayList<AppsInfo> allApps;
    private AppClickListener appClickListener;
    private int cellSize;

    public AppsListAdapter setAppClickListener(AppClickListener appClickListener) {
        this.appClickListener = appClickListener;
        return this;
    }

    public AppsListAdapter(Context context) {
        this.context = context;
        cellSize = Util.getScreenWidth(context) / 4;
        allApps = new ArrayList<>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View v = LayoutInflater.from(context).inflate(R.layout.item_apps,
                parent, false);

        GridLayoutManager.LayoutParams layoutParams =
                (GridLayoutManager.LayoutParams) v.getLayoutParams();
        layoutParams.height = cellSize;
        layoutParams.width = cellSize;
        v.setLayoutParams(layoutParams);

        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ViewHolder commentViewHolder = (ViewHolder) viewHolder;
        int position = commentViewHolder.getAdapterPosition();

        viewHolder.tvAppName.setText(allApps.get(position).getName());
        viewHolder.ivAppIcon.setImageDrawable(allApps.get(position).getIcon());

        viewHolder.itemView.setOnClickListener(v ->
                appClickListener.onAppClick(allApps.get(position)));
    }

    @Override
    public int getItemCount() {
        return (allApps != null) ? allApps.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivAppIcon;
        TextView tvAppName;


        public ViewHolder(View view) {
            super(view);
            initView(view);
        }

        private void initView(View view) {
            ivAppIcon = (CircleImageView) view.findViewById(R.id.iv_app_icon);
            tvAppName = (TextView) view.findViewById(R.id.tv_app_name);
        }
    }

    public void setData(ArrayList<AppsInfo> comments) {
        if (comments != null && comments.size() > 0)
            allApps = comments;
    }

}
