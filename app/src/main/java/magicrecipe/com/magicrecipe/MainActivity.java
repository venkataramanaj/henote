package magicrecipe.com.magicrecipe;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import magicrecipe.com.magicrecipe.adapter.ReceipeAdapter;
import magicrecipe.com.magicrecipe.network.Api;
import magicrecipe.com.magicrecipe.network.RetrofitClient;
import magicrecipe.com.magicrecipe.pojo.Main;
import magicrecipe.com.magicrecipe.pojo.Result;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.ingredients)
    EditText edt_ingredients;
    @BindView(R.id.list)
    RecyclerView rv_receipes;
    @BindView(R.id.get)
    Button btn_get;
    @BindView(R.id.load_progress)
    ProgressBar pbr;

    private ProgressDialog progress;
    private Api apiService;
    private ReceipeAdapter mAdapter;

    private String str_ingredeients;
    private boolean scrool_flg = true, loading;
    private int spageno = 1;
    private List<Result> result, result_total;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        progress = new ProgressDialog(this);
        pbr.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.colorPrimaryDark),
                PorterDuff.Mode.MULTIPLY);

        rv_receipes.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

                return false;
            }
        });
    }

    @OnClick(R.id.get)
    public void submit() {
        if (edt_ingredients.getText().toString().length() > 0)
            if (isNetworkAvailable()) {
                result = new ArrayList<>();
                result_total = new ArrayList<Result>();
                spageno = 1;
                scrool_flg = true;
                loading = false;
                apiService = RetrofitClient.getClient().create(Api.class);
                str_ingredeients = edt_ingredients.getText().toString();
                callingAPI(str_ingredeients, spageno);
            } else
                Toast.makeText(MainActivity.this, "Please check your internet connection", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(MainActivity.this, "Please enter atleast one ingredient", Toast.LENGTH_SHORT).show();
    }

    public void callingAPI(final String ingredients, final int spage) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("i", ingredients);
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


        Call<Main> call = apiService.getData(queryParams);
        call.enqueue(new Callback<Main>() {
            @Override
            public void onResponse(Call<Main> call, Response<Main> response) {

//                Log.e("response is", "<><><" + response.body());
                Log.e("response is", "<><><" + response);
                if (response.message().equals("OK")) {
                    Main e = response.body();
//                String status=e.getStatus();
                    result = e.getResults();
                    result_total.addAll(result);
                    Log.e("result is", "<><><" + result.size());
                    if (result.size() > 0) {
                        if (result.size() < 10) {
                            scrool_flg = false;
                        }
                        loading = false;

                        if (spage == 1) {
                            progress.dismiss();

                            rv_receipes.setHasFixedSize(true);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            rv_receipes.setLayoutManager(mLayoutManager);
                            mAdapter = new ReceipeAdapter(ingredients, result_total, MainActivity.this);
                            rv_receipes.setAdapter(mAdapter);
                            rv_receipes.addOnScrollListener(new EndlessScrollListener(rv_receipes));

                        } else {
                            pbr.setVisibility(View.GONE);
                            mAdapter.notifyDataSetChanged();
                        }


                    } else {
                        pbr.setVisibility(View.GONE);
                        progress.dismiss();
                        loading = false;
                        scrool_flg = false;
                        Toast.makeText(MainActivity.this, "No result found", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    pbr.setVisibility(View.GONE);
                    progress.dismiss();
                    Toast.makeText(MainActivity.this, "" + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Main> call, Throwable t) {
                Log.e("data", t.toString());
                Log.e("fail is", "<><><" + call.request().toString());
                Toast.makeText(MainActivity.this, "" + t.toString(), Toast.LENGTH_SHORT).show();
                progress.dismiss();
            }
        });
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
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
                    if (isNetworkAvailable()) {
                        loading = true;
                        spageno++;
                        callingAPI(str_ingredeients, spageno);
                    }
                }
            }
        }
    }
}


