package com.adverse.newsreader.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ShareCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.adverse.newsreader.R;
import com.adverse.newsreader.model.Article;
import com.adverse.newsreader.util.GlideApp;
import com.adverse.newsreader.util.OnRecyclerViewItemClickListener;

import java.util.List;

public class MainArticleAdapter extends RecyclerView.Adapter<MainArticleAdapter.ViewHolder> {
    private List<Article> articleArrayList;
//    private Context context;
    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    private Context context;

    public MainArticleAdapter(List<Article> articleArrayList) {
        this.articleArrayList = articleArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.news_recycler_layout1, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        context = viewHolder.itemView.getContext();
        final Article articleModel = articleArrayList.get(position);
        String date = "";
        String author = "";
        if (!TextUtils.isEmpty(articleModel.getTitle())) {
            viewHolder.titleText.setText(articleModel.getTitle());
        }
        if (!TextUtils.isEmpty(articleModel.getAuthor())) {
            author = articleModel.getAuthor();
        }
        if (!TextUtils.isEmpty(articleModel.getPublishedAt())) {
            date = articleModel.getPublishedAt();
        }
        String publishedDate =  date.substring(0, 10) + " " + date.substring(11,16);
        viewHolder.author.setText(author + " ~ " + publishedDate);
//        if (!TextUtils.isEmpty(articleModel.getPublishedAt())) {
//            String date = articleModel.getPublishedAt();
//            viewHolder.published_at.setText("~ " + date.substring(0, 10) + " " + date.substring(11,16));
//        }
        if (!TextUtils.isEmpty(articleModel.getDescription())) {
            viewHolder.news_description.setText(articleModel.getDescription().trim());
        } else {
            viewHolder.news_description.setText(articleModel.getTitle());
        }
        viewHolder.share_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareCompat.IntentBuilder.from((Activity) context)
                .setType("text/plain")
                        .setChooserTitle("News Reader")
                        .setText("News Reader: \n" + articleModel.getTitle() + " \n" + articleModel.getUrl())
                        .startChooser();
            }
        });
        if (!TextUtils.isEmpty(articleModel.getUrlToImage())) {
            GlideApp.with(context).load(articleModel.getUrlToImage()).into(viewHolder.news_image);
        } else {
            viewHolder.news_image.setImageResource(R.drawable.logo_blue_bg);
        }
        viewHolder.artilceAdapterParentLinear.setTag(articleModel);
    }

    @Override
    public int getItemCount() {
        return articleArrayList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private TextView titleText, author, published_at, news_description;
        private ImageView news_image, share_news;
        private LinearLayout artilceAdapterParentLinear;

        ViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.news_title);
            author = view.findViewById(R.id.news_author);
//            published_at = view.findViewById(R.id.published_at);
            news_image = view.findViewById(R.id.news_image);
            news_description = view.findViewById(R.id.news_description);
            share_news = view.findViewById(R.id.share_news);
            artilceAdapterParentLinear = view.findViewById(R.id.article_adapter_ll_parent);
            artilceAdapterParentLinear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (onRecyclerViewItemClickListener != null) {
                        onRecyclerViewItemClickListener.onItemClick(getAdapterPosition(), view);
                    }
                }
            });
        }
    }

    public void setOnRecyclerViewItemClickListener(OnRecyclerViewItemClickListener onRecyclerViewItemClickListener) {
        this.onRecyclerViewItemClickListener = onRecyclerViewItemClickListener;
    }
}