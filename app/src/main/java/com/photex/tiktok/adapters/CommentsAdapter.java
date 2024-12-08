package com.photex.tiktok.adapters;

import android.content.Context;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.photex.tiktok.R;
import com.photex.tiktok.interfaces.CommentClickListener;
import com.photex.tiktok.models.Comment;
import com.photex.tiktok.models.CommentReply;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    //private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;
    ArrayList<Comment> allComments;
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;
    private CommentClickListener commentClickListener;

    //Typeface typeface;

    public CommentsAdapter setCommentClickListener(CommentClickListener commentClickListener) {
        this.commentClickListener = commentClickListener;
        return this;
    }

    public CommentsAdapter(Context context, ArrayList<Comment> allComments) {
        this.context = context;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
        this.allComments = allComments;
        //typeface = Typeface.createFromAsset(context.getAssets(), "jameel.ttf");

    }

    @Override
    public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment,
                parent, false);

        CommentViewHolder commentViewHolder = new CommentViewHolder(view);
        setupClickableViews(view, commentViewHolder);
        return commentViewHolder;
    }


    private void setupClickableViews(View view, CommentViewHolder commentViewHolder) {

        commentViewHolder.ivUserImage.setOnClickListener(v -> {
                    if (commentClickListener != null)
                        commentClickListener.onClickUserProfile(commentViewHolder.getAdapterPosition());
                }
        );
        commentViewHolder.tvUserName.setOnClickListener(v ->
                {
                    if (commentClickListener != null)
                        commentClickListener.onClickUserName(commentViewHolder.getAdapterPosition());
                }
        );
        commentViewHolder.optionsBtn.setOnClickListener(v ->
                {
                    if (commentClickListener != null)
                        commentClickListener.onClickMore(v, commentViewHolder.getAdapterPosition());
                }
        );
        commentViewHolder.replyBtn.setOnClickListener(v ->
                {
                    if (commentClickListener != null)
                        commentClickListener.onClickReply(commentViewHolder.getAdapterPosition());
                }
        );
        commentViewHolder.replyLayout.setOnClickListener(v ->
                {
                    if (commentClickListener != null)
                        commentClickListener.onClickReply(commentViewHolder.getAdapterPosition());
                }
        );

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int pos) {

        CommentViewHolder commentViewHolder = (CommentViewHolder) viewHolder;
        int position = commentViewHolder.getAdapterPosition();

        if (allComments.get(position).getCommentReply() != null && allComments.get(position).getCommentReply().size() > 0) {
            commentViewHolder.replyLayout.setVisibility(View.VISIBLE);
            commentViewHolder.tvRepliedUserName.setText(allComments.get(position).getCommentReply().get(0).getFullName());
            commentViewHolder.tvRepliedComment.setText(allComments.get(position).getCommentReply().get(0).getComment());
            if (allComments.get(position).getCommentReply().get(0).getUserId().equals(SettingManager.getUserId(context))) {
                Glide.with(context)
                        .load(Constants.BASE_URL + Constants.SERVER_DIRECTORY + allComments.get(position).getCommentReply().get(0).getUserDisplayPicture())
                        .apply(new RequestOptions()
                                .signature(new ObjectKey(SettingManager.getProfilePicTime(context)))
                                .placeholder(R.drawable.user_image_place_holder).dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(commentViewHolder.ivRepliedProfilePic);
            } else {
                Glide.with(context)
                        .load(Constants.BASE_URL + Constants.SERVER_DIRECTORY + allComments.get(position).getCommentReply().get(0).getUserDisplayPicture())
                        .apply(new RequestOptions()
                                .signature(new ObjectKey(SettingManager.getProfilePicTime(context)))
                                .placeholder(R.drawable.user_image_place_holder).dontAnimate()
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                        .into(commentViewHolder.ivRepliedProfilePic);
            }
        } else {
            commentViewHolder.replyLayout.setVisibility(View.GONE);
        }

        if (allComments.get(position).get_id() != null && !allComments.get(position).get_id().isEmpty()) {
            commentViewHolder.replyBtn.setVisibility(View.VISIBLE);
        } else {
            commentViewHolder.replyBtn.setVisibility(View.GONE);
        }

        if (allComments.get(position).getNumOfCommentReplies() > 1) {
            commentViewHolder.tvAllReplies.setVisibility(View.VISIBLE);
            commentViewHolder.tvAllReplies.setText("View all " + allComments.get(position).getNumOfCommentReplies() + " replies");
        } else {
            commentViewHolder.tvAllReplies.setVisibility(View.GONE);
        }

        commentViewHolder.tvUserName.setText("@" + allComments.get(position).getUserName());
        commentViewHolder.tvUserComment.setText(allComments.get(position).getComment());

        String date = allComments.get(position).getDate();
        if (date != null && !date.isEmpty()) {
            if (!date.equals("just now")) {
                String result = changeDateLayout(allComments.get(position).getDate());
                if (result != null && !result.isEmpty()) {
                    commentViewHolder.tvTime.setText(result);
                }
            } else {
                commentViewHolder.tvTime.setText("just now");
            }
        } else {
            commentViewHolder.tvTime.setText("");
        }

        Glide.with(context)
                .load(Constants.BASE_URL + Constants.SERVER_DIRECTORY + allComments.get(position).getUserDisplayPicture())
                .apply(new RequestOptions()
                        .signature(new ObjectKey(SettingManager.getProfilePicTime(context)))
                        .placeholder(R.drawable.user_image_place_holder).dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(commentViewHolder.ivUserImage);
    }

    private String changeDateLayout(String date) {
        String humanReadAbleFormate = date;
        if (date != null && !date.isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
            try {
                Date dt = format.parse(date);
                Calendar c = Calendar.getInstance();

                TimeZone fromTimeZone = c.getTimeZone();
                TimeZone toTimeZone = TimeZone.getTimeZone("CST");

                c.setTimeZone(fromTimeZone);
                c.add(Calendar.MILLISECOND, fromTimeZone.getRawOffset() * -1);
                if (fromTimeZone.inDaylightTime(c.getTime())) {
                    c.add(Calendar.MILLISECOND, c.getTimeZone().getDSTSavings() * -1);
                }

                c.add(Calendar.MILLISECOND, toTimeZone.getRawOffset());
                if (toTimeZone.inDaylightTime(c.getTime())) {
                    c.add(Calendar.MILLISECOND, toTimeZone.getDSTSavings());
                }
                long now = c.getTimeInMillis();
                c.add(Calendar.HOUR, 5);

                c.setTime(dt); // Now use today date.

                humanReadAbleFormate = TimeUtil.getTimeAgo(c.getTimeInMillis(), now);

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return humanReadAbleFormate;
    }


    @Override
    public int getItemCount() {
        return (allComments != null) ? allComments.size() : 0;
    }

    public void updateItems() {
        notifyDataSetChanged();
    }

    public String getCommentId(int position) {
        return allComments.get(position).get_id();
    }

    public void addItem(Comment postComment) {
        allComments.add(0, postComment);
        notifyDataSetChanged();
    }

    public void removeItem(int index) {
        allComments.remove(index);
        notifyDataSetChanged();
    }

    public void setAnimationsLocked(boolean animationsLocked) {
        this.animationsLocked = animationsLocked;
    }

    public void setDelayEnterAnimation(boolean delayEnterAnimation) {
        this.delayEnterAnimation = delayEnterAnimation;
    }

    public void updateItem(int itemPosition, int totalReplies, List<CommentReply> commentReply) {
        Comment comment = allComments.get(itemPosition);
        comment.setCommentReply(commentReply);
        comment.setNumOfCommentReplies(totalReplies);
        allComments.set(itemPosition, comment);

        notifyDataSetChanged();
    }

    public static class CommentViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivUserImage, ivRepliedProfilePic;
        TextView tvUserName, tvUserComment, tvTime, tvRepliedComment;
        ImageView optionsBtn;
        TextView replyBtn, tvRepliedUserName, tvAllReplies;
        LinearLayout replyLayout;


        public CommentViewHolder(View view) {
            super(view);
            initView(view);
        }

        private void initView(View view) {
            ivUserImage = (CircleImageView) view.findViewById(R.id.ivUserImage);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvUserComment = (TextView) view.findViewById(R.id.tvUserComment);
            tvTime = (TextView) view.findViewById(R.id.tvTime);

            optionsBtn = (ImageView) view.findViewById(R.id.optionsBtn);
            replyBtn = (TextView) view.findViewById(R.id.reply_btn);

            replyLayout = (LinearLayout) view.findViewById(R.id.replyLayout);
            ivRepliedProfilePic = (CircleImageView) view.findViewById(R.id.ivRepliedUserPic);
            tvRepliedUserName = (TextView) view.findViewById(R.id.tvRepliedUserName);
            tvRepliedComment = (TextView) view.findViewById(R.id.tvRepliedComment);
            tvAllReplies = (TextView) view.findViewById(R.id.tvAllReplies);
        }
    }

    public void setData(ArrayList<Comment> comments) {
        if (comments != null && comments.size() > 0)
            allComments = comments;
    }

    public Comment getComment(int index) {
        return allComments.get(index);
    }

}
