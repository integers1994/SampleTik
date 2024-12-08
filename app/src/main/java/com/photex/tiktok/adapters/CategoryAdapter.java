package com.photex.tiktok.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.photex.tiktok.R;
import com.photex.tiktok.models.CategoryInfo;
import com.photex.tiktok.utils.Constants;

import java.util.ArrayList;

public class CategoryAdapter extends BaseAdapter {
    ArrayList<CategoryInfo> data;
    LayoutInflater inflter;
    Context context;

    public CategoryAdapter(Context context, ArrayList<CategoryInfo> data) {
        this.data = data;
        this.context = context;
        this.inflter = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        view = inflter.inflate(R.layout.services_spinner_item, null);

        TextView categoryName = view.findViewById(R.id.tv_service_title);
        ImageView categoryImage = view.findViewById(R.id.service_logo);

        try {
            Glide.with(context)
                    .load(Constants.BASE_URL +"category_images/"+ data.get(position).getLogo())
                    .apply(new RequestOptions()
                            .placeholder(R.drawable.category_icon).dontAnimate()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                    .into(categoryImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        categoryName.setText(data.get(position).getName());
        return view;
    }

    public void setData(ArrayList<CategoryInfo> data) {
        this.data = data;
    }
}

