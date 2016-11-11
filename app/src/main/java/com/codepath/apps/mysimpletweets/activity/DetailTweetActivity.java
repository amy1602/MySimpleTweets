package com.codepath.apps.mysimpletweets.activity;

import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.codepath.apps.mysimpletweets.R;
import com.codepath.apps.mysimpletweets.ReplyDialog;
import com.codepath.apps.mysimpletweets.activity.TimelineActivity;
import com.codepath.apps.mysimpletweets.adapter.TweetsArrayAdapter;
import com.codepath.apps.mysimpletweets.models.Tweet;
import com.codepath.apps.mysimpletweets.models.TwitterApplication;
import com.codepath.apps.mysimpletweets.models.TwitterClient;
import com.google.gson.Gson;
import com.loopj.android.http.JsonHttpResponseHandler;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class DetailTweetActivity extends AppCompatActivity {

    private ImageView ivProfileImage;
    private TextView tvUserName;
    private TextView tvTimeStamp;
    private TextView tvBody;
    private TextView tvRetweet;
    private TextView tvLike;
    private ImageButton btnReply;
    private ImageButton btnRetweet;
    private ImageButton btnLike;
    private TwitterClient mClient;
    private long id;
    boolean isFavourited, isRetweeted;
    Tweet tweet;
    Gson mGson;
    boolean btnRetweetClicked = false;
    boolean btnLikeClicked = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_tweet2);
        setUp();
        setData();
        mGson = new Gson();
        mClient = TwitterApplication.getRestClient();


        btnReply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getBaseContext(),"ok",Toast.LENGTH_SHORT).show();
                ReplyDialog replyDialog = new ReplyDialog(tweet);
                replyDialog.shoDialog(DetailTweetActivity.this);
            }
        });

        btnRetweet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               if (!isRetweeted) {

                   mClient.retweetStatus(id,new JsonHttpResponseHandler(){
                       @Override
                       public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                           super.onSuccess(statusCode, headers, response);
                           Tweet tweet1 = mGson.fromJson(response.toString(),Tweet.class);
                           tvRetweet.setText(String.valueOf(tweet1.getRetweetCount()));
                           btnRetweet.setImageResource(R.drawable.ic_retweeted);
                           isRetweeted = true;
                       }
                   });
                } else {
                   mClient.unRetweetStatus(id,new JsonHttpResponseHandler(){
                       @Override
                       public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                           super.onSuccess(statusCode, headers, response);
                           Tweet tweet1 = mGson.fromJson(response.toString(),Tweet.class);
                           tvRetweet.setText(String.valueOf(tweet1.getRetweetCount()));
                           btnRetweet.setImageResource(R.drawable.ic_retweet);
                           isRetweeted = false;
                       }
                   });
                }
            }
        });


        btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isFavourited){
                    mClient.favouriteStatus(id,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Tweet tweet1 = mGson.fromJson(response.toString(),Tweet.class);
                            tvLike.setText(String.valueOf(tweet1.getFavouritesCount()));
                            btnLike.setImageResource(R.drawable.ic_liked);
                            isFavourited=true;
                        }
                    });

                }
                else {
                    mClient.unFavouriteStatus(id,new JsonHttpResponseHandler(){
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            super.onSuccess(statusCode, headers, response);
                            Tweet tweet1 = mGson.fromJson(response.toString(),Tweet.class);
                            tvLike.setText(String.valueOf(tweet1.getFavouritesCount()));
                            btnLike.setImageResource(R.drawable.ic_like);
                            isFavourited= false;
                        }
                    });

                }
            }
        });
    }
    public void setUp(){

        ivProfileImage = (ImageView) findViewById(R.id.ivProfileImage);
        tvUserName = (TextView) findViewById(R.id.tvUserName);
        tvTimeStamp = (TextView) findViewById(R.id.tvRelativeTimestamp);
        tvBody = (TextView) findViewById(R.id.tvBody);
        tvRetweet = (TextView) findViewById(R.id.tvDetailReTweet);
        tvLike = (TextView) findViewById(R.id.tvDetailNumberOfLike);
        btnRetweet = (ImageButton) findViewById(R.id.btnDetailRetweet);
        btnLike = (ImageButton) findViewById(R.id.btnDetailLike);
        btnReply = (ImageButton) findViewById(R.id.btnDetailLike);

    }

    public void setData()
    {

        Intent intent = getIntent();
        String imageUrl, username, timeStamp, body;
        int like, retweet;
        id = intent.getLongExtra("id",0);
        imageUrl = intent.getStringExtra("profileImage");
        username = intent.getStringExtra("userName");
        timeStamp = intent.getStringExtra("timeStamp");
        body = intent.getStringExtra("body");
        like = intent.getIntExtra("like",0);
        retweet = intent.getIntExtra("retweet",0);
        isFavourited = intent.getBooleanExtra("liked",false);
        isRetweeted = intent.getBooleanExtra("retweeted",false);
        Glide.with(getApplicationContext())
                .load(imageUrl)
                .into(ivProfileImage);

        tvUserName.setText(username);
        tvTimeStamp.setText(timeStamp);
        tvBody.setText(body);
        tvRetweet.setText(String.valueOf(retweet));
        tvLike.setText(String.valueOf(like));
        if (isFavourited){
            btnLike.setImageResource(R.drawable.ic_liked);
        }
        else {
            btnLike.setImageResource(R.drawable.ic_like);
        }

        if (isRetweeted){
            btnRetweet.setImageResource(R.drawable.ic_retweeted);
        }
        else {
            btnRetweet.setImageResource(R.drawable.ic_retweet);
        }
    }
}
