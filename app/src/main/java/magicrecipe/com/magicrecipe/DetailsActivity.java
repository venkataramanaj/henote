package magicrecipe.com.magicrecipe;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import magicrecipe.com.magicrecipe.adapter.DetailsAdapter;
import magicrecipe.com.magicrecipe.adapter.ReceipeAdapter;
import magicrecipe.com.magicrecipe.network.Api;
import magicrecipe.com.magicrecipe.network.RetrofitClient;
import magicrecipe.com.magicrecipe.pojo.Main;
import magicrecipe.com.magicrecipe.pojo.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Ramana on 5/26/2018.
 */

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.detailslist)
    RecyclerView rv_details;
    @BindView(R.id.load_progress)
    ProgressBar pbr;

    private ProgressDialog progress;
    private Api apiService;
    private DetailsAdapter mAdapter;
    private String str_ingredeients, str_receipe;
    private boolean scrool_flg = true, loading;
    private int spageno = 1;
    private List<Result> result, result_total = new ArrayList<Result>();
    ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        ButterKnife.bind(this);

        progress = new ProgressDialog(this);
        pbr.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                PorterDuff.Mode.MULTIPLY);

        apiService = RetrofitClient.getClient().create(Api.class);
        str_ingredeients = getIntent().getStringExtra("ingredients");
        str_receipe = getIntent().getStringExtra("receipe");
        callingAPI(str_ingredeients, str_receipe, spageno);
    }

    public void callingAPI(String ingredients, String receipe, final int spage) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("i", ingredients);
        queryParams.put("q", receipe);
        queryParams.put("p", "" + spage);

        progress.setMessage("Downloading...");
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        scrool_flg = true;
        loading = true;

        if (spageno == 1) {
            progress.show();
        } else {
            pbr.setVisibility(View.VISIBLE);
        }

        // Fetching all notes
        apiService.getData(queryParams)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeWith(getObserverData(ingredients, spage));
                .subscribe(getAnimalsObserver(ingredients, spage));

      /*  Call<Main> call = apiService.getData(queryParams);
        call.enqueue(new Callback<Main>() {
            @Override
            public void onResponse(Call<Main> call, Response<Main> response) {

                Log.e("response is", "<><><" + response);
                if (response.message().equals("OK")) {
                    Main e = response.body();
                    result = e.getResults();
                    result_total.addAll(result);
                    if (result.size() > 0) {
                        if (result.size() < 10) {
                            scrool_flg = false;
                        }
                        loading = false;
                        if (spage == 1) {
                            progress.dismiss();
                            mAdapter = new DetailsAdapter(result_total, DetailsActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            rv_details.setLayoutManager(mLayoutManager);
                            rv_details.setItemAnimator(new DefaultItemAnimator());
                            rv_details.setAdapter(mAdapter);
                            rv_details.addOnScrollListener(new EndlessScrollListener(rv_details));
                        } else {
                            pbr.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }
                    } else {
                        pbr.setVisibility(View.GONE);
                        progress.dismiss();
                        loading = false;
                        scrool_flg = false;
                        Toast.makeText(DetailsActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pbr.setVisibility(View.GONE);
                    progress.dismiss();
                    Toast.makeText(DetailsActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Main> call, Throwable t) {
                Log.e("data", t.toString());
                Log.e("fail is", "<><><" + call.request().toString());
                progress.dismiss();
            }
        });*/
    }

    private Observer<Main> getAnimalsObserver(final String ingredients, final int spage) {
        return new Observer<Main>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d("TAG", "onSubscribe");
            }

            @Override
            public void onNext(Main s) {
                Log.d("TAG", "Name: " + s);
                result = s.getResults();
                result_total.addAll(result);
                if (result.size() > 0) {
                    if (result.size() < 10) {
                        scrool_flg = false;
                    }
                    loading = false;
                    if (spage == 1) {
                        progress.dismiss();
                        mAdapter = new DetailsAdapter(result_total, DetailsActivity.this);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        rv_details.setLayoutManager(mLayoutManager);
                        rv_details.setItemAnimator(new DefaultItemAnimator());
                        rv_details.setAdapter(mAdapter);
                        rv_details.addOnScrollListener(new EndlessScrollListener(rv_details));
                    } else {
                        pbr.setVisibility(View.GONE);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    pbr.setVisibility(View.GONE);
                    progress.dismiss();
                    loading = false;
                    scrool_flg = false;
                    Toast.makeText(DetailsActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e("TAG", "onError: " + e.getMessage());
                Toast.makeText(DetailsActivity.this, "" + e.toString(), Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }

            @Override
            public void onComplete() {
                Log.d("TAG", "All items are emitted!");
            }
        };
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    public class EndlessScrollListener extends RecyclerView.OnScrollListener {

        private RecyclerView listView;

        public EndlessScrollListener(RecyclerView listView) {
            this.listView = listView;
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }

        @Override
        public void onScrollStateChanged(RecyclerView view, int scrollState) {
            LinearLayoutManager layoutManager = ((LinearLayoutManager) view.getLayoutManager());
            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
            if (scrollState == 0 && lastVisiblePosition == listView.getAdapter().getItemCount() - 1 && scrool_flg) {
                if (!loading) {
                    loading = true;
                    spageno++;
//                    callingAPI(str_ingredeients, str_receipe, spageno);
                }
            }
        }
    }
}
