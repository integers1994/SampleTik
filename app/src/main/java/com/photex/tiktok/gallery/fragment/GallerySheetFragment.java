package com.photex.tiktok.gallery.fragment;


import android.app.Dialog;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.deep.videotrimmer.utils.FileUtils;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.photex.tiktok.R;
import com.photex.tiktok.gallery.VideoTrimmerActivity;
import com.photex.tiktok.gallery.adapter.MediaAdapter;
import com.photex.tiktok.gallery.filter.FilterMode;
import com.photex.tiktok.gallery.model.Album;
import com.photex.tiktok.gallery.model.Media;
import com.photex.tiktok.gallery.provider.CPHelper;
import com.photex.tiktok.misc.GridSpacingItemDecoration;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Measure;
import com.wang.avi.AVLoadingIndicatorView;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class GallerySheetFragment extends BottomSheetDialogFragment {


    private final String TAG = GallerySheetFragment.class.getSimpleName();

    private TextView txt_no_video_found;
    private RecyclerView rv_videos;
    private ImageView img_cancel;

    AVLoadingIndicatorView loading_indicator;

    private ArrayList<Media> mediaArrayList;
    private Album album = Album.getEmptyAlbum();
    private GridSpacingItemDecoration spacingDecoration;
    MediaAdapter mediaAdapter;

    public GallerySheetFragment() {
        // Required empty public constructor
    }

    public static GallerySheetFragment newInstance() {
        GallerySheetFragment sheetFragment = new GallerySheetFragment();


        return sheetFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
    }

    private void initData() {
        mediaArrayList = new ArrayList<>();

        mediaAdapter = new MediaAdapter(getActivity());
        album = Album.getAllMediaAlbum();
        //album.setName(getString(R.string.video));
        album.setFilterMode(FilterMode.VIDEO);
    }


    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        BottomSheetDialog dialog = (BottomSheetDialog) super.onCreateDialog(savedInstanceState);

        dialog.setOnShowListener(dialog1 -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog1;

            FrameLayout bottomSheet = (FrameLayout) d
                    .findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {

                BottomSheetBehavior.from(bottomSheet).setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
                    @Override
                    public void onStateChanged(@NonNull View view, int newState) {

                        switch (newState) {
                            case BottomSheetBehavior.STATE_HIDDEN:
                                dismiss(); //if you want the modal to be dismissed when user drags the bottomsheet down
                                break;
                            case BottomSheetBehavior.STATE_EXPANDED:
                                if (mediaAdapter.getMedias() == null
                                        ||
                                        mediaAdapter.getMedias().size() <= 0) {

                                    loadVideos();
                                }
                                break;
                            case BottomSheetBehavior.STATE_COLLAPSED:
                                break;
                            case BottomSheetBehavior.STATE_DRAGGING:
                                break;
                            case BottomSheetBehavior.STATE_SETTLING:
                                break;
                        }


                    }

                    @Override
                    public void onSlide(@NonNull View view, float v) {

                    }
                });
                BottomSheetBehavior.from(bottomSheet).
                        setState(BottomSheetBehavior.STATE_EXPANDED);
            }

        });

        // Do something with your dialog like setContentView() or whatever
        return dialog;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_gallery_sheet,
                container, false);
        txt_no_video_found = view.findViewById(R.id.txt_no_video_found);
        img_cancel = view.findViewById(R.id.img_cancel);
        rv_videos = view.findViewById(R.id.rv_videos);

        loading_indicator = view.findViewById(R.id.loading_indicator);
        spacingDecoration = new GridSpacingItemDecoration(4,
                Measure.pxToDp(0, getActivity()), true);
        rv_videos.setHasFixedSize(true);
        rv_videos.addItemDecoration(spacingDecoration);
        rv_videos.setLayoutManager(new GridLayoutManager(getContext(), 4));
        rv_videos.setAdapter(mediaAdapter);

        img_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setVideoClickListener();
        loadVideos();
        /*  Disposable disposable =*/


    }

    private void setVideoClickListener() {
        mediaAdapter.setOnMediaClickListener(media -> {
            startTrimActivity(media.getUri());
        });
    }

    private void startTrimActivity(@NonNull Uri uri) {
        Intent intent = new Intent(getActivity(), VideoTrimmerActivity.class);
        intent.putExtra(Constants.EXTRA_VIDEO_PATH, FileUtils.getPath(getActivity(), uri));
        startActivityForResult(intent, Constants.RC_VIDEO_TRIM);
        if (getActivity() != null)
            getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    private void loadVideos() {
        if (getActivity() != null) {
            CPHelper.getAllVideosFromStorage(getActivity(), album)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    /*.filter(media -> MediaFilter.getFilter(album.filterMode())
                            .accept(media))*/
                    .subscribe(new Observer<Media>() {
                        @Override
                        public void onSubscribe(Disposable d) {

                        }

                        @Override
                        public void onNext(Media media) {

                            loading_indicator.hide();
                            mediaAdapter.add(media);
                            // On Next
                        }

                        @Override
                        public void onError(Throwable e) {

                            // On Next
                            Log.d(TAG, "Error = " + e.getMessage());
                        }

                        @Override
                        public void onComplete() {
                            Log.d(TAG, "onComplete = ");

                            if (mediaAdapter.getMedias().size() <= 0) {
                                txt_no_video_found.setVisibility(View.VISIBLE);
                            } else {
                                txt_no_video_found.setVisibility(View.GONE);
                            }

                        }
                    });

        }
    }
}
