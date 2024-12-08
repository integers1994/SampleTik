package com.photex.tiktok.activities;

import android.app.Activity;
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
import com.photex.tiktok.adapters.CommentsAdapter;
import com.photex.tiktok.endlessrecycle.EndlessRecyclerViewScrollListener;
import com.photex.tiktok.interfaces.CommentClickListener;
import com.photex.tiktok.models.Comment;
import com.photex.tiktok.models.CommentReply;
import com.photex.tiktok.models.restmodels.DeleteComment;
import com.photex.tiktok.models.restmodels.GetComments;
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

public class CommentsActivity extends AppCompatActivity implements
        View.OnClickListener,
        CommentClickListener {

    private ImageView closeCommentsBtn, addCommentBtn;
    private EditText etComment;
    private TextView tvNoComment;
    private AVLoadingIndicatorView commentLoadingIndicator;
    private RecyclerView rvComments;
    private Animation shakeAnimation;

    private String userComment;
    private String postId;
    private String postById;
    private String lastId;
    int totalComments = 0;
    boolean isCommentChanged;

    CommentsAdapter commentsAdapter;
    ArrayList<Comment> allComments;
    private LinearLayoutManager layoutManager;

    private int commentPosition;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        initializeView();

        Intent intent = getIntent();
        if (intent != null &&
                intent.hasExtra(Constants.EXTRA_POST_INFO) &&
                intent.hasExtra(Constants.postById) &&
                intent.hasExtra(Constants.totalComments)) {

            postId = intent.getStringExtra(Constants.EXTRA_POST_INFO);
            postById = intent.getStringExtra(Constants.postById);
            totalComments = (int) intent.getExtras().get(Constants.totalComments);
            allComments = new ArrayList<>();

            commentsAdapter = new CommentsAdapter(this, allComments)
                    .setCommentClickListener(this);
            rvComments.setAdapter(commentsAdapter);
            rvComments.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                @Override
                public void onLoadMore(int page, int totalItemsCount) {
                    Log.i("loadMore", "Loading" + page + "  " + totalItemsCount);
                    if (commentsAdapter.getItemCount() >= 10)
                        getComments();
                }
            });

            getComments();
        }
        shakeAnimation = AnimationUtils.loadAnimation(CommentsActivity.this, R.anim.shake_error);
    }

    private void initializeView() {
        etComment = findViewById(R.id.et_comment);
        closeCommentsBtn = findViewById(R.id.close_comment_btn);
        addCommentBtn = findViewById(R.id.add_comment_btn);
        commentLoadingIndicator = findViewById(R.id.comments_loading_indicator);
        tvNoComment = findViewById(R.id.tv_no_comment);

        rvComments = findViewById(R.id.rv_comment_list);
        layoutManager = new LinearLayoutManager(getBaseContext(), LinearLayoutManager.VERTICAL, false);
        rvComments.setLayoutManager(layoutManager);
        rvComments.setHasFixedSize(true);


        closeCommentsBtn.setOnClickListener(this);
        addCommentBtn.setOnClickListener(this);
    }


    private void getComments() {
        if (Util.isNetworkAvailable(this)) {
            commentLoadingIndicator.setVisibility(View.VISIBLE);
            int totalItem = commentsAdapter.getItemCount();
            if (totalItem > 0) {
                lastId = commentsAdapter.getCommentId(totalItem - 1);
            } else {
                lastId = "0";
            }

            GetComments getComment = new GetComments();
            getComment.setPostId(postId);
            getComment.setLastId(lastId);

            Call<String> call = new RestClient(Constants.BASE_URL,
                    CommentsActivity.this).get()
                    .getComments(getComment);
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
                            jsonArray = jsonObject.getJSONArray("comments");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        if (success && jsonArray != null) {
                            Gson gson = new Gson();
                            List<Comment> comments;
                            comments = Arrays.asList(gson.fromJson(jsonArray.toString(), Comment[].class));

                            if (comments.size() > 0) {
                                lastId = comments.get(comments.size() - 1).get_id();

                                allComments.addAll(comments);
                                /*commentsAdapter.setData(allComments);*/
                                commentsAdapter.notifyDataSetChanged();
                            }

                            if (allComments.size() <= 0) {
                                tvNoComment.setVisibility(View.VISIBLE);
                            } else {
                                tvNoComment.setVisibility(View.GONE);
                            }

//                            rvComments.scrollToPosition(0);

                        } else {
                            Toast.makeText(CommentsActivity.this, "Some thing goes wrong please try again!", Toast.LENGTH_SHORT).show();


                            /*try {
                                JSONObject jsonObject = new JSONObject(response.body());
                                String message = jsonObject.getString("message");
                                if (message != null && message.equals(getString(R.string.jwt_expired))) {
                                    Toast.makeText(CommentsActivity.this, "Token expired login again please.", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(CommentsActivity.this, SignInActivity.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }*/
                        }
                    }
                }

                @Override
                public void onFinallyFail() {
                    Log.e("getComments", "onFinallyFail");
                }
            });

        } else {
            Util.internetNotAvailableDialouge(this);
        }
    }

    private boolean isCommentValid() {
        userComment = etComment.getText().toString().trim();
        if (userComment.isEmpty()) {
            Toast.makeText(CommentsActivity.this, "Write your comment first!", Toast.LENGTH_SHORT).show();
            addCommentBtn.startAnimation(shakeAnimation);
            return false;
        }

        return true;
    }

    private void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(etComment.getWindowToken(), 0);
    }

    private void addComment() {
        if (Util.isNetworkAvailable(CommentsActivity.this)) {
            commentLoadingIndicator.setVisibility(View.VISIBLE);

            Comment postComment = new Comment();
            postComment.setPostId(postId);
            postComment.setComment(userComment);
            postComment.setUserDisplayPicture(SettingManager.getUserPictureURL(CommentsActivity.this));
            postComment.setUserId(SettingManager.getUserId(CommentsActivity.this));
            postComment.setUserName(SettingManager.getUserName(CommentsActivity.this));
            postComment.setFullName(SettingManager.getUserFullName(CommentsActivity.this));
            postComment.setPostBy(postById);

            Call<String> call = new RestClient(Constants.BASE_URL, CommentsActivity.this).get()
                    .addComment(postComment);
            call.enqueue(new CallbackWithRetry<String>(call) {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()) {
                        etComment.setText("");
                        commentLoadingIndicator.setVisibility(View.GONE);
                        tvNoComment.setVisibility(View.GONE);

                        postComment.setDate("just now");
                        commentsAdapter.addItem(postComment);

                        rvComments.smoothScrollToPosition(0);
                        isCommentChanged = true;
                        totalComments++;
                    }
                }

                @Override
                public void onFinallyFail() {
                    commentLoadingIndicator.setVisibility(View.GONE);

                    Toast.makeText(CommentsActivity.this,
                            R.string.error_while_posting_comment,
                            Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Util.internetNotAvailableDialouge(CommentsActivity.this);
        }
    }

    private void deleteComment(final int index) {

        if (Util.isNetworkAvailable(CommentsActivity.this)) {

            DeleteComment deleteComment = new DeleteComment();
            Comment comment = allComments.get(index);
            deleteComment.setCommentId(comment.get_id());
            deleteComment.setPostId(comment.getPostId());

            if (SettingManager.getUserId(CommentsActivity.this).equals(comment.getUserId())
                    || SettingManager.getUserId(CommentsActivity.this).equals(postById)) {
                commentLoadingIndicator.setVisibility(View.VISIBLE);

                Call<String> call = new RestClient(Constants.BASE_URL, CommentsActivity.this).get()
                        .deleteComment(deleteComment);
                call.enqueue(new CallbackWithRetry<String>(call) {
                    @Override
                    public void onResponse(Call<String> call, Response<String> response) {
                        commentLoadingIndicator.setVisibility(View.GONE);
                        commentsAdapter.removeItem(index);
                        isCommentChanged = true;
                        totalComments--;
                    }

                    @Override
                    public void onFinallyFail() {
                        Toast.makeText(CommentsActivity.this, "Error while deleting", Toast.LENGTH_SHORT).show();
                    }
                });

            } else {

                new AlertDialog.Builder(CommentsActivity.this, R.style.AppCompatAlertDialogStyle)
                        .setTitle(R.string.can_delete_own_comment)
                        .setPositiveButton("Ok", (dialog, which) -> {
                            // continue with delete
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }

        } else {
            Util.internetNotAvailableDialouge(CommentsActivity.this);
        }
    }

    @Override
    public void onClick(View v) {
        if (v == closeCommentsBtn) {
            onBackPressed();

        } else if (v == addCommentBtn) {
            hideKeyBoard();

            if (isCommentValid()) {
                addComment();
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
        PopupMenu popup = new PopupMenu(CommentsActivity.this, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.comment_overflow_menu, popup.getMenu());
        popup.setOnMenuItemClickListener(item -> {
            new AlertDialog.Builder(CommentsActivity.this, R.style.AppCompatAlertDialogStyle)
                    .setTitle(R.string.delete_comment)
                    .setMessage(R.string.are_you_want_to_delete)
                    .setPositiveButton("Yes", (dialog, which) -> deleteComment(position))
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        // do nothing
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();

            return true;
        });
        popup.show();
    }

    @Override
    public void onClickReply(int position) {
        this.commentPosition = position;

        String postId, postById, commentId, commentById;
        int totalReplies;

        postId = allComments.get(position).getPostId();
        postById = allComments.get(position).getPostBy();
        commentId = allComments.get(position).get_id();
        commentById = allComments.get(position).getUserId();
        totalReplies = allComments.get(position).getNumOfCommentReplies();

        startActivityForResult(new Intent(CommentsActivity.this, CommentsReplyActivity.class)
                        .putExtra(Constants.EXTRA_POST_INFO, postId)
                        .putExtra(Constants.postById, postById)
                        .putExtra(Constants.commentId, commentId)
                        .putExtra(Constants.commentById, commentById)
                        .putExtra(Constants.totalReplies, totalReplies),
                Constants.COMMENT_REPLY_ACTIVITY_REQUEST);
        overridePendingTransition(R.anim.enter, R.anim.exit);

    }

    @Override
    public void onBackPressed() {
        if (isCommentChanged) {
            setResult(RESULT_OK, new Intent().putExtra(Constants.totalComments, totalComments));
            finish();
        } else {
            super.onBackPressed();
        }
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == Constants.COMMENT_REPLY_ACTIVITY_REQUEST) {
            if (resultCode == RESULT_OK &&
                    data != null &&
                    data.hasExtra(Constants.totalReplies) &&
                    data.hasExtra(Constants.commentReplies)) {

                commentsAdapter.updateItem(
                        commentPosition,
                        (int) data.getExtras().get(Constants.totalReplies),
                        (List<CommentReply>) data.getExtras().get(Constants.commentReplies)
                );
            }
        } else if (requestCode == Constants.SHARE_VIDEO_REQUEST) {

        }
    }
}
