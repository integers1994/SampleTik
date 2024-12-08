package com.photex.tiktok.gallery.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.photex.tiktok.R;
import com.photex.tiktok.adapters.VideoViewHolder;
import com.photex.tiktok.gallery.model.Album;
import com.photex.tiktok.gallery.model.Media;
import com.photex.tiktok.gallery.sort.MediaComparators;
import com.photex.tiktok.gallery.sort.SortingMode;
import com.photex.tiktok.gallery.sort.SortingOrder;

import java.util.ArrayList;
import java.util.Collections;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;

public class MediaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public interface OnMediaClickListener {
        void onVideoClick(Media media);
    }

    private final PublishSubject<Integer>
            onClickSubject = PublishSubject.create();
    private final PublishSubject<Media>
            onChangeSelectedSubject = PublishSubject.create();


    private int selectedCount = 0;

    private SortingOrder sortingOrder;
    private SortingMode sortingMode;

    private ArrayList<Media> medias;

    OnMediaClickListener onMediaClickListener;


    private Context context;


    public MediaAdapter(Context context) {

        medias = new ArrayList<>();
        this.sortingMode = SortingMode.DATE;
        this.sortingOrder = SortingOrder.DESCENDING;
        /*for avoiding blinking*/
        /*setHasStableIds(true);*/
    }


    public OnMediaClickListener getOnMediaClickListener() {
        return onMediaClickListener;
    }

    public void setOnMediaClickListener(OnMediaClickListener onMediaClickListener) {
        this.onMediaClickListener = onMediaClickListener;
    }

    public MediaAdapter(Context context, SortingMode sortingMode,
                        SortingOrder sortingOrder) {

        medias = new ArrayList<>();
        this.context = context;
        this.sortingMode = sortingMode;
        this.sortingOrder = sortingOrder;
        setHasStableIds(true);
    }

    public ArrayList<Media> getMedias() {
        return medias;
    }

    /*Sorting*/

    public void sort() {
        Collections.sort(medias,
                MediaComparators.
                        getComparator(sortingMode, sortingOrder));
        notifyDataSetChanged();
    }

    public void changeSortingOrder(SortingOrder sortingOrder) {
        this.sortingOrder = sortingOrder;
        Collections.reverse(medias);
        notifyDataSetChanged();
    }

    public void changeSortingMode(SortingMode sortingMode) {
        this.sortingMode = sortingMode;
        sort();
    }

    public void changeSortingModeOrder(SortingMode sortingMode,
                                       SortingOrder order) {
        this.sortingMode = sortingMode;
        this.sortingOrder = order;
        Collections.reverse(medias);
        sort();
    }


    public SortingMode sortingMode() {
        return sortingMode;
    }

    public SortingOrder sortingOrder() {
        return sortingOrder;
    }

    public int add(Media album) {
        int i = Collections.binarySearch(
                medias, album,
                MediaComparators.
                        getComparator(sortingMode, sortingOrder));
        if (i < 0) i = ~i;
        medias.add(i, album);
        notifyItemInserted(i);
        return i;
    }

    /*Selection*/
    public ArrayList<Media> getSelected() {
        ArrayList<Media> arrayList = new ArrayList<>(selectedCount);
        for (Media m : medias)
            if (m.isSelected())
                arrayList.add(m);
        return arrayList;

    }

    public Media getFirstSelected() {
        if (selectedCount > 0) {
            for (Media m : medias)
                if (m.isSelected())
                    return m;
        }
        return null;
    }


    public int getSelectedCount() {
        return selectedCount;
    }

    public void selectAll() {
        for (int i = 0; i < medias.size(); i++)
            if (medias.get(i).setSelected(true))
                notifyItemChanged(i);
        selectedCount = medias.size();
        onChangeSelectedSubject.onNext(new Media());
    }

    public void clearSelected() {
        for (int i = 0; i < medias.size(); i++)
            if (medias.get(i).setSelected(false))
                notifyItemChanged(i);
        selectedCount = 0;
        onChangeSelectedSubject.onNext(new Media());
    }

    /*Clicks*/
    public Observable<Integer> getClicks() {
        return onClickSubject;
    }

    public Observable<Media> getSelectedClicks() {
        return onChangeSelectedSubject;
    }


    public void setupFor(Album album) {
        medias.clear();
        // TODO: 27-Nov-17 check again for this effect
     /*   changeSortingMode(album.settings.getSortingMode());
        changeSortingOrder(album.settings.getSortingOrder());*/
        notifyDataSetChanged();
    }

    /**
     * On longpress, it finds the last or the first selected image before or after the targetIndex
     * and selects them all.
     *
     * @param
     */
    public void selectAllUpTo(Media m) {
        int targetIndex = medias.indexOf(m);

        int indexRightBeforeOrAfter = -1;
        int indexNow;

        // TODO: 4/5/17 rewrite?
        for (Media sm : getSelected()) {
            indexNow = medias.indexOf(sm);
            if (indexRightBeforeOrAfter == -1) indexRightBeforeOrAfter = indexNow;

            if (indexNow > targetIndex) break;
            indexRightBeforeOrAfter = indexNow;
        }

        if (indexRightBeforeOrAfter != -1) {
            for (int index = Math.min(targetIndex, indexRightBeforeOrAfter); index <= Math.max(targetIndex, indexRightBeforeOrAfter); index++) {
                if (medias.get(index) != null) {
                    if (medias.get(index).setSelected(true)) {
                        notifySelected(true);
                        notifyItemChanged(index);
                    }
                }
            }

        }
    }

    public void remove(Media media) {
        int i = this.medias.indexOf(media);
        this.medias.remove(i);
        notifyItemRemoved(i);
    }

    private void notifySelected(boolean increase) {

        selectedCount += increase ? 1 : -1;
    }

    public boolean selecting() {
        return selectedCount > 0;
    }

    public void clear() {
        medias.clear();
        notifyDataSetChanged();
    }

    public void setData(ArrayList<Media> allObjects) {

        this.medias = allObjects;
    }

/*
    @Override
    public long getItemId(int position) {
        //todo check about this

        return medias.get(position).getUri().hashCode() ^ 1312;
    }
*/


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup,
                                                      int viewType) {

        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_video_info, viewGroup, false);
        VideoViewHolder viewHolder = new VideoViewHolder(view);
        setupClickableViews(view, viewHolder);
        return viewHolder;
    }

    private void setupClickableViews(View view,
                                     final VideoViewHolder viewHolder) {


        viewHolder.layout_video.setOnClickListener(v -> {

            if (onMediaClickListener != null) {
                onMediaClickListener.onVideoClick(medias.
                        get(viewHolder.getAdapterPosition()));

            }
        });

/*        viewHolder.mediaLayout.setOnClickListener(v -> {

            int position = viewHolder.getAdapterPosition();
            if (selecting()) {
                Media media = medias.get(position);
                notifySelected(media.toggleSelected());
                medias.set(position, media);
                notifyItemChanged(position);
                onChangeSelectedSubject.onNext(media);
            } else {
                onClickSubject.onNext(position);
            }


        });


        viewHolder.mediaLayout.setOnLongClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            Media media = medias.get(position);
            if (!selecting()) {
                // If it is the first long press
                notifySelected(media.toggleSelected());
                medias.set(position, media);
                notifyItemChanged(position);
                onChangeSelectedSubject.onNext(media);
            } else {
                selectAllUpTo(media);
                onChangeSelectedSubject.onNext(new Media());
            }

            return true;
        });*/
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        VideoViewHolder viewHolder = (VideoViewHolder) holder;
        viewHolder.bindView(medias.get(position));
    }

    @Override
    public int getItemCount() {
        return medias != null ?
                medias.size() : 0;
    }

    private class VideoViewHolder extends RecyclerView.ViewHolder {

        ConstraintLayout layout_video;
        ImageView img_video_thumbnail;
        TextView txt_duration;

        VideoViewHolder(View v) {
            super(v);
            img_video_thumbnail = v.findViewById(R.id.img_video_thumbnail);
            txt_duration = v.findViewById(R.id.txt_duration);
            layout_video = v.findViewById(R.id.layout_video);
        }

        void bindView(Media media) {

            RequestOptions options = new RequestOptions()
                    .signature(media.getSignature())
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .centerCrop()
                    .placeholder(R.color.gray)
                    //.animate(R.anim.fade_in)//TODO:DONT WORK WELL
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

            Glide.with(img_video_thumbnail.getContext())
                    .load(media.getFile())
                    .apply(options)
                    .into(img_video_thumbnail);

            txt_duration.setText(media.getHumanReadAbleTime());


        }

    }
}
