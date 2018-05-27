package magicrecipe.com.magicrecipe.adapter;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import magicrecipe.com.magicrecipe.DetailsActivity;
import magicrecipe.com.magicrecipe.R;
import magicrecipe.com.magicrecipe.WebViewActivity;
import magicrecipe.com.magicrecipe.pojo.Result;
import magicrecipe.com.magicrecipe.shared.ChromeTabActionBroadcastReceiver;
import magicrecipe.com.magicrecipe.shared.CustomTabActivityHelper;
import magicrecipe.com.magicrecipe.utils.ImageFetch;

/**
 * Created by Ramana on 5/25/2018.
 */

public class ReceipeAdapter extends RecyclerView.Adapter<ReceipeAdapter.MyViewHolder> {
    List<Result> result;
    Context context;
    int imageItem_height_calculation = 0;
    float imageheight = 0;
    String ingredients;

    public ReceipeAdapter(String ingredients, List<Result> result, Context context) {
        this.result = result;
        this.context = context;
        this.ingredients = ingredients;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.receipe_item, parent, false);

        return new MyViewHolder(itemView);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, desc;
        ImageView images;
        RelativeLayout ll_image;
        CardView cardView;

        public MyViewHolder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.title);
            images = view.findViewById(R.id.images);
            ll_image = view.findViewById(R.id.image_layout);
            cardView = view.findViewById(R.id.card_view);
            desc = (TextView) view.findViewById(R.id.desc);
        }
    }

    public static int dpToPx(int dp, Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        int pixels = (int) (dp * scale + 0.5f);
        return pixels;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {


        if (imageItem_height_calculation == 0) {
            imageItem_height_calculation = 1;
            holder.images.getLayoutParams().height = (int) ((float) ((context
                    .getResources().getDisplayMetrics().widthPixels - dpToPx(10, context)) / 2) / 1.33);
            imageheight = (int) ((float) ((context
                    .getResources().getDisplayMetrics().widthPixels - dpToPx(10, context)) / 2) / 1.33);
        } else {
            holder.images.getLayoutParams().height = (int) imageheight;
        }


        if (result.get(position).getTitle() != null && result.get(position).getTitle().trim().length() > 0)
            holder.title.setText(result.get(position).getTitle().trim());

        holder.desc.setText(Html.fromHtml("<u>Description</u>"));
        ;
        if (result.get(position).getThumbnail().length() > 0) {
            ImageFetch.getInstance().loadImage(context, holder.images, result.get(position).getThumbnail());
        } else {
            holder.images.setImageResource(R.mipmap.ic_launcher);
        }

        holder.images.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("receipe", result.get(position).getTitle());
                intent.putExtra("ingredients", ingredients);
                context.startActivity(intent);
            }
        });
        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("receipe", result.get(position).getTitle());
                intent.putExtra("ingredients", ingredients);
                context.startActivity(intent);
            }
        });
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, DetailsActivity.class);
                intent.putExtra("receipe", result.get(position).getTitle());
                intent.putExtra("ingredients", ingredients);
                context.startActivity(intent);
            }
        });
        holder.desc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri myUri = Uri.parse(result.get(position).getHref());
                openCustomChromeTab(myUri);
            }
        });
    }

    @Override
    public int getItemCount() {
        return result.size();

    }

    private void openCustomChromeTab(Uri uri) {
        CustomTabsIntent.Builder intentBuilder = new CustomTabsIntent.Builder();

        // set toolbar colors
        intentBuilder.setToolbarColor(ContextCompat.getColor(context, R.color.colorPrimary));
        intentBuilder.setSecondaryToolbarColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        // set action button
        intentBuilder.setActionButton(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher), "Action Button",
                createPendingIntent(ChromeTabActionBroadcastReceiver.ACTION_ACTION_BUTTON));
        // build custom tabs intent
        CustomTabsIntent customTabsIntent = intentBuilder.build();
        // call helper to open custom tab
        CustomTabActivityHelper.openCustomTab((Activity) context, customTabsIntent, uri, new CustomTabActivityHelper.CustomTabFallback() {
            @Override
            public void openUri(Activity activity, Uri uri) {
                // fall back, call open open webview
                openWebView(uri);
            }
        });
    }

    private void openWebView(Uri uri) {
        Intent webViewIntent = new Intent(context, WebViewActivity.class);
        webViewIntent.putExtra(WebViewActivity.EXTRA_URL, uri.toString());
        context.startActivity(webViewIntent);
    }

    private PendingIntent createPendingIntent(int actionSource) {
        Intent actionIntent = new Intent(context, ChromeTabActionBroadcastReceiver.class);
        actionIntent.putExtra(ChromeTabActionBroadcastReceiver.KEY_ACTION_SOURCE, actionSource);
        return PendingIntent.getBroadcast(context, actionSource, actionIntent, 0);
    }

}
