package com.photex.tiktok.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.signature.ObjectKey;
import com.photex.tiktok.R;
import com.photex.tiktok.interfaces.CommentClickListener;
import com.photex.tiktok.models.CommentReply;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.TimeUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsReplyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    //private int itemsCount = 0;
    private int lastAnimatedPosition = -1;
    private int avatarSize;
    ArrayList<CommentReply> allComments;
    private boolean animationsLocked = false;
    private boolean delayEnterAnimation = true;
    private CommentClickListener commentClickListener;

    public CommentsReplyAdapter setCommentClickListener(CommentClickListener commentClickListener) {
        this.commentClickListener = commentClickListener;
        return this;
    }

    public CommentsReplyAdapter(Context context) {
        this.context = context;
        avatarSize = context.getResources().getDimensionPixelSize(R.dimen.comment_avatar_size);
        allComments = new ArrayList<>();
        //typeface = Typeface.createFromAsset(context.getAssets(), "jameel.ttf");

    }

    @Override
    public ReplyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.item_comment_reply,
                parent, false);

        ReplyViewHolder replyViewHolder = new ReplyViewHolder(view);
        return replyViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int pos) {

        ReplyViewHolder replyViewHolder = (ReplyViewHolder) viewHolder;
        int position = replyViewHolder.getAdapterPosition();

        replyViewHolder.ivUserImage.setOnClickListener(v -> commentClickListener.onClickUserProfile(position));
        replyViewHolder.tvUserName.setOnClickListener(v -> commentClickListener.onClickUserName(position));
        replyViewHolder.optionsBtn.setOnClickListener(v -> commentClickListener.onClickMore(v, position));

        replyViewHolder.tvUserName.setText("@" + allComments.get(position).getUserName());
        replyViewHolder.tvUserComment.setText(allComments.get(position).getComment());

        String date = allComments.get(position).getDate();
        if (date != null && !date.isEmpty()) {
            if (!date.equals("just now")) {
                String result = changeDateLayout(allComments.get(position).getDate());
                if (result != null && !result.isEmpty()) {
                    replyViewHolder.tvTime.setText(result);
                }
            } else {
                replyViewHolder.tvTime.setText("just now");
            }
        } else {
            replyViewHolder.tvTime.setText("");
        }

        Glide.with(context)
                .load(Constants.BASE_URL + Constants.SERVER_DIRECTORY + allComments.get(position).getUserDisplayPicture())
                .apply(new RequestOptions()
                        .signature(new ObjectKey(SettingManager.getProfilePicTime(context)))
                        .placeholder(R.drawable.user_image_place_holder).dontAnimate()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE))
                .into(replyViewHolder.ivUserImage);
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

    public void addItem(CommentReply postComment) {
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

    public static class ReplyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView ivUserImage;
        TextView tvUserName, tvUserComment, tvTime;
        ImageView optionsBtn;


        public ReplyViewHolder(View view) {
            super(view);
            initView(view);
        }

        private void initView(View view) {
            ivUserImage = (CircleImageView) view.findViewById(R.id.ivUserImage);
            tvUserName = (TextView) view.findViewById(R.id.tvUserName);
            tvUserComment = (TextView) view.findViewById(R.id.tvUserComment);
            tvTime = (TextView) view.findViewById(R.id.tvTime);

            optionsBtn = (ImageView) view.findViewById(R.id.optionsBtn);
        }
    }

    public void setData(ArrayList<CommentReply> comments) {
        if (comments != null && comments.size() > 0)
            allComments = comments;
    }

    public CommentReply getComment(int index) {
        return allComments.get(index);
    }

}
