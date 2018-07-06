package magicrecipe.com.magicrecipe;

import android.app.Application;

import magicrecipe.com.magicrecipe.di.ApiComponent;
import magicrecipe.com.magicrecipe.di.ApiModule;
import magicrecipe.com.magicrecipe.di.AppModule;
import magicrecipe.com.magicrecipe.di.DaggerApiComponent;

/**
 * Created by Belal on 12/2/2017.
 */

public class MyApplication extends Application {

    private ApiComponent mApiComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        mApiComponent = DaggerApiComponent.builder()
                .appModule(new AppModule(this))
                .apiModule(new ApiModule("http://www.recipepuppy.com/"))
                .build();
    }

    public ApiComponent getNetComponent() {
        return mApiComponent;
    }
}