package com.adverse.newsreader.fragment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.adverse.newsreader.R;
import com.adverse.newsreader.activity.WebActivity;
import com.adverse.newsreader.adapter.MainArticleAdapter;
import com.adverse.newsreader.model.Article;
import com.adverse.newsreader.model.ResponseModel;
import com.adverse.newsreader.api.ApiClient;
import com.adverse.newsreader.api.ApiInterface;
import com.adverse.newsreader.util.OnRecyclerViewItemClickListener;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentDateSearch extends Fragment implements OnRecyclerViewItemClickListener {

    private OnRecyclerViewItemClickListener onRecyclerViewItemClickListener;
    Context context;
    RecyclerView recyclerView;
    private ProgressDialog progressDialog;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        progressDialog=new ProgressDialog(context);
        progressDialog.setMessage("Loading..");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setIndeterminate(true);
        progressDialog.setProgress(0);
        progressDialog.show();
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerView);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);

        ImageView errorImage = view.findViewById(R.id.errorImage);
        TextView errorText = view.findViewById(R.id.errorText);

        Bundle bundle = getArguments();
        String dateQuery = null;
        dateQuery = getArguments().getString("dateUser");
        Log.i("dateUser ", dateQuery);

        final ApiInterface apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<ResponseModel> call, callOnException;
        call = apiService.getEverythingByDate("india", dateQuery, dateQuery, ApiClient.API_KEY);
        callOnException = apiService.getLatestNews("in", ApiClient.API_KEY);

        String finalDateQuery = dateQuery;
        call.enqueue(new Callback<ResponseModel>() {
            @Override
            public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {

                try {
                    if (response.body().getStatus().equals("ok")) {
                        List<Article> articleList = response.body().getArticles();

                        if (articleList.size() > 0) {
                            final MainArticleAdapter mainArticleAdapter = new MainArticleAdapter(articleList);
                            mainArticleAdapter.setOnRecyclerViewItemClickListener(FragmentDateSearch.this);
                            recyclerView.setAdapter(mainArticleAdapter);
                            progressDialog.dismiss();
                            getActivity().setTitle("News from: " + finalDateQuery);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    progressDialog.dismiss();
                    AlertDialog.Builder enterNewPass = new AlertDialog.Builder(context);
                    enterNewPass.setTitle("404").setMessage("News could not be found for selected date!\nPlease try different date..")
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) { }
                            }).create().show();

                    callOnException.enqueue(new Callback<ResponseModel>() {


                        @Override
                        public void onResponse(Call<ResponseModel> call, Response<ResponseModel> response) {
                            progressDialog.show();
                            try {
                                if (response.body().getStatus().equals("ok")) {
                                    List<Article> articleList = response.body().getArticles();

                                    if (articleList.size() > 0) {
                                        final MainArticleAdapter mainArticleAdapter = new MainArticleAdapter(articleList);
                                        mainArticleAdapter.setOnRecyclerViewItemClickListener(FragmentDateSearch.this);
                                        recyclerView.setAdapter(mainArticleAdapter);
                                        progressDialog.dismiss();
                                        getActivity().setTitle("Top headlines");
                                    }
                                }
                            } catch (Exception e1) {
                                e.printStackTrace();
                                progressDialog.dismiss();
                                Toast.makeText(context, "Something went wrong!", Toast.LENGTH_LONG).show();
                                errorImage.setVisibility(View.VISIBLE);
                                errorText.setVisibility(View.VISIBLE);
                                getActivity().setTitle("Error");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResponseModel> call, Throwable t) {
                            progressDialog.dismiss();
                            Toast.makeText(context, "could not load", Toast.LENGTH_SHORT).show();
                            errorImage.setVisibility(View.VISIBLE);
                            errorText.setVisibility(View.VISIBLE);
                            getActivity().setTitle("Error");
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ResponseModel> call, Throwable t) {
                progressDialog.dismiss();
                Log.e("out", t.toString());
                Toast.makeText(context, "Failed to load news!\nPlease make sure your internet is working", Toast.LENGTH_LONG).show();
                errorImage.setVisibility(View.VISIBLE);
                errorText.setVisibility(View.VISIBLE);
                getActivity().setTitle("Error");
            }
        });

        return view;
    }

    @Override
    public void onItemClick(int adapterPosition, View view) {
        switch (view.getId()) {
            case R.id.article_adapter_ll_parent:
                Article article = (Article) view.getTag();
                if (!TextUtils.isEmpty(article.getUrl())) {
                    Log.e("clicked url", article.getUrl());
                    Intent webActivity = new Intent(context, WebActivity.class);
                    webActivity.putExtra("url", article.getUrl());
                    startActivity(webActivity);
                }
                break;
        }
    }
}