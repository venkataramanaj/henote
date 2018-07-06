package magicrecipe.com.magicrecipe.di;

import javax.inject.Singleton;

import dagger.Component;
import magicrecipe.com.magicrecipe.MainActivity;

/**
 * Created by Belal on 12/2/2017.
 */

@Singleton
@Component(modules = {AppModule.class, ApiModule.class})
public interface ApiComponent {
    void inject(MainActivity activity);
}
