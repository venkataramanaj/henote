package magicrecipe.com.magicrecipe.shared;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class ChromeTabActionBroadcastReceiver extends BroadcastReceiver {
    public static final String KEY_ACTION_SOURCE = "org.chromium.customtabsdemos.ACTION_SOURCE";

    public static final int ACTION_MENU_ITEM_1 = 1;
    public static final int ACTION_MENU_ITEM_2 = 2;
    public static final int ACTION_ACTION_BUTTON = 3;


    @Override
    public void onReceive(Context context, Intent intent) {
        String data = intent.getDataString();

        if (data != null) {

//            Toast.makeText(context, toastText, Toast.LENGTH_SHORT).show();
        }
    }
}
