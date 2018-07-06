package magicrecipe.com.magicrecipe.network;

/**
 * Created by Ramana on 5/25/2018.
 */

import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.Single;
import magicrecipe.com.magicrecipe.pojo.Main;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.QueryMap;

public interface Api {

    @GET("api?")
    Observable<Main> getData(@QueryMap Map<String, String> fields);
}
