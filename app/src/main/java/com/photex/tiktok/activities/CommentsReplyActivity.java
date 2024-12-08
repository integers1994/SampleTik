package com.photex.tiktok.activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.MenuInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.photex.tiktok.R;
import com.photex.tiktok.adapters.CommentsReplyAdapter;
import com.photex.tiktok.endlessrecycle.EndlessRecyclerViewScrollListener;
import com.photex.tiktok.interfaces.CommentClickListener;
import com.photex.tiktok.models.CommentReply;
import com.photex.tiktok.models.restmodels.DeleteCommentReply;
import com.photex.tiktok.models.restmodels.GetAllReplies;
import com.photex.tiktok.rest.CallbackWithRetry;
import com.photex.tiktok.rest.RestClient;
import com.photex.tiktok.setting.SettingManager;
import com.photex.tiktok.utils.Constants;
import com.photex.tiktok.utils.Util;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import retrofit2.Call;
import retrofit2.Response;

public class CommentsReplyActivity extends AppCompatActivity implements
        View.OnClickListener,
        CommentClickListener {

    private ImageView closeCommentsBtn, addCommentBtn;
    private EditText etComment;
    private TextView tvNoComment, tvToolbar;
    private AVLoadingIndicatorView commentLoadingIndicator;
    private RecyclerView rvComments;
    private Animation shakeAnimation;

    private String userComment;
    private String commentId;
    private String lastId;
    int totalReplies = 0;
    boolean isCommentChanged;

    CommentsReplyAdapter commentsReplyAdapter;
    ArrayList<CommentReply> allReplies;
    private LinearLayoutManager layoutManager;
    private String commentById;
    private String postId;
    private String postById;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_reply);
        initializeView();

        Intent intent = getIntent();
        if (intent != null &&
                intent.hasExtra(Constants.EXTRA_POST_INFO) &&
                intent.hasExtra(Constants.postById) &&
                intent.hasExtra(Constants.commentId) &&
                intent.hasExtra(Constants.totalReplies)) {

            postId = intent.getStringExtra(Constants.EXTRA_POST_INFO);
            postById = intent.getStringExtra(Constants.postById);
            commentId = intent.getStringExtra(Constants.commentId);
            commentById = intent.getStringExtra(Constants.commentById);
            totalReplies = (int) intent.getExtras().get(Constants.totalReplies);
            allReplies = new ArrayList<>();

            commentsReplyAdapter = new CommentsReplyAdapter(this)
                    .setCommentClickListener(this);
            rvComments.setAdapter(commentsReplyAdapter);
            rvComments.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    Log.i("loadMore", "Loading" + page + "  " + totalItemsCount);
                    if (commentsReplyAdapter.getItemCount() >= 10)
                        getReplies();
                }
            });

            getReplies();
        }
        shakeAnimation = AnimationUtils.loadAnimation(CommentsReplyActivity.this, R.anim.shake_error);
    }

    private void initializeView() {
        etComment = findViewById(R.id.et_comment);
        closeCommentsBtn = findViewById(R.id.close_comment_btn);
        addCommentBtn = findViewById(R.id.add_comment_btn);
        commentLoadingIndicator = findViewById(R.id.comments_loading_indicator);
        tvNoComment = findViewById(R.id.tv_no_comment);
        tvToolbar = findViewById(R.id.tvToolbar);
        tvToolbar.setText("All Replies");

        rvComments = findViewById(R.id.rv_comment_list);
        layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        rvComments.setLayoutManager(layoutManager);
        rvComments.setHasFixedSize(true);


        closeCommentsBtn.setOnClickListener(this);
        addCommentBtn.setOnClickListener(this);
    }


    private void getReplies() {
        if (Util.isNetworkAvailable(this)) {
            commentLoadingIndicator.setVisibility(View.VISIBLE);
            int totalItem = commentsReplyAdapter.getItemCount();
            if (totalItem > 0) {
                lastId = commentsReplyAdapter.getCommentId(totalItem - 1);
            } else {
                lastId = "0";
            }

            GetAllReplies getAllReplies = new GetAllReplies();
            getAllReplies.setParentCommentId(commentId);
            getAllReplies.setLastId(lastId);

            Call<String> call = new RestClient(Constants.BASE_URL,
                    CommentsReplyActivity.this).get()
                    .getCommentReplies(getAllReplies);

            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    commentLoadingIndicator.setVisibility(View.GONE);
                    JSONArray jsonArray = null;
                    boolean success = false;
                    if (response.body() != null && response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body());
                            success = jsonObject.getBoolean("success");
                            jsonArray = jsonObject.getJSONArray("replies");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (success && jsonArray != null) {
                            Gson gson = new Gson();
                            List<CommentReply> comments;
                            comments = Arrays.asList(gson.fromJson(jsonArray.toString(), CommentReply[].class));

                            if (comments.size() > 0) {
                                lastId = comments.get(comments.size() - 1).get_id();

                                allReplies.addAll(comments);
                                commentsReplyAdapter.setData(allReplies);
                                commentsReplyAdapter.notifyDataSetChanged();
                            }

                            tvNoComment.setText("0 Replies");
                            if (allReplies.size() <= 0) {
                                tvNoComment.setVisibility(View.VISIBLE);
                            } else {
                                tvNoComment.setVisibility(View.GONE);
                            }

                        } else {
                            Toast.makeText(CommentsReplyActivity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }

                @Override
                public void onFinallyFail() {
                    Log.e("getReplies", "onFinallyFail");
                }
            });

        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }

    private boolean isCommentValid() {
        userComment = etComment.getText().toString().trim();
        if (userComment.isEmpty()) {
            Toast.makeText(CommentsReplyActivity.this, "Write your comment first!", Toast.LENGTH_SHORT).show();
            addCommentBtn.startAnimation(shakeAnimation);
            return false;
        }

        return true;
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
    }

    private void addReply() {
        if (Util.isNetworkAvailable(CommentsReplyActivity.this)) {
            commentLoadingIndicator.setVisibility(View.VISIBLE);

            CommentReply commentReply = new CommentReply();
            commentReply.setPostId(postId);
            commentReply.setUserId(SettingManager.getUserId(CommentsReplyActivity.this));
            commentReply.setFullName(SettingManager.getUserFullName(CommentsReplyActivity.this));
            commentReply.setParentCommentId(commentId);
            commentReply.setUserDisplayPicture(SettingManager.getUserPictureURL(CommentsReplyActivity.this));
            commentReply.setUserName(SettingManager.getUserName(CommentsReplyActivity.this));
            commentReply.setComment(userComment);
            commentReply.setParentCommentUserId(commentById);
            commentReply.setPostBy(postById);

            Call<String> call = new RestClient(Constants.BASE_URL, CommentsReplyActivity.this).get()
                    .addCommentReply(commentReply);

            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        etComment.setText("");
                        commentLoadingIndicator.setVisibility(View.GONE);
                        tvNoComment.setVisibility(View.GONE);

                        commentReply.setDate("just now");
                        allReplies.add(0, commentReply);
                        commentsReplyAdapter.notifyDataSetChanged();

//                        commentsReplyAdapter.addItem(commentReply);
                        rvComments.smoothScrollToPosition(0);

                        isCommentChanged = true;
                        totalReplies++;
                        onBackPressed();
                    }
                }

                @Override
                public void onFinallyFail() {
                    commentLoadingIndicator.setVisibility(View.GONE);

                    Toast.makeText(CommentsReplyActivity.this,
                            R.string.error_while_posting_comment,
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Util.internetNotAvailableDialouge(CommentsReplyActivity.this);
        }
    }

    private void deleteReply(final int index) {
        if (Util.isNetworkAvailable(CommentsReplyActivity.this)) {

            DeleteCommentReply deleteCommentReply = new DeleteCommentReply();
            CommentReply comment = allReplies.get(index);
            deleteCommentReply.setParentCommentId(commentId);
            deleteCommentReply.setUserId(comment.getUserId());
            deleteCommentReply.setCommentReplyId(comment.get_id());

            if (SettingManager.getUserId(CommentsReplyActivity.this).equals(comment.getUserId())
                    || SettingManager.getUserId(CommentsReplyActivity.this).equals(commentById)) {
                commentLoadingIndicator.setVisibility(View.VISIBLE);

                Call<String> call = new RestClient(Constants.BASE_URL, CommentsReplyActivity.this).get()
                        .deleteCommentReply(deleteCommentReply);
                call.enqueue(new CallbackWithRetry<String>(call) {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        commentLoadingIndicator.setVisibility(View.GONE);
                        commentsReplyAdapter.removeItem(index);

                        isCommentChanged = true;
                        totalReplies--;
                        onBackPressed();
                    }

                    @Override
                    public void onFinallyFail() {
                        Toast.makeText(CommentsReplyActivity.this, "Error while deleting", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {

                new AlertDialog.Builder(CommentsReplyActivity.this, R.style.AppCompatAlertDialogStyle)
                        .setTitle(R.string.can_delete_own_comment)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            // continue with delete
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        } else {
            Util.internetNotAvailableDialouge(CommentsReplyActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == closeCommentsBtn) {
            onBackPressed();

        } else if (v == addCommentBtn) {
            hideKeyBoard();

            if (isCommentValid()) {
                addReply();
            }
        }
    }

    @Override
    public void onClickUserProfile(int position) {

    }

    @Override
    public void onClickUserName(int position) {

    }

    @Override
    public void onClickMore(View v, int position) {
        PopupMenu popup = new PopupMenu(CommentsReplyActivity.this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.comment_overflow_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            new AlertDialog.Builder(CommentsReplyActivity.this, R.style.AppCompatAlertDialogStyle)
                    .setTitle(R.string.delete_comment)
                    .setMessage(R.string.are_you_want_to_delete)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteReply(position);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // do nothing
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        });
        popup.show();
    }

    @Override
    public void onClickReply(int position) {

    }

    @Override
    public void onBackPressed() {
        if (isCommentChanged) {
            setResult(RESULT_OK, new Intent()
                    .putExtra(Constants.totalReplies, totalReplies)
                    .putExtra(Constants.commentReplies, allReplies));
            finish();
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }
}
