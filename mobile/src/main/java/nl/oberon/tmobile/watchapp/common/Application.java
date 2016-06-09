package nl.oberon.tmobile.watchapp.common;

import java.io.File;

import nl.oberon.tmobile.watchapp.R;
import nl.oberon.tmobile.watchapp.common.network.CapiService;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 *
 * This apps application
 *
 */
public class Application extends android.app.Application {

    private static final int CACHE_SIZE_IN_MB = 5;
    private CapiService capiService;

    @Override
    public void onCreate() {
        super.onCreate();

        createCapiService();
    }

    /**
     * Creates a capi services with caching
     */
    private void createCapiService() {
        //setup cache
        File httpCacheDirectory = new File(getCacheDir(), "responses");
        int cacheSize = CACHE_SIZE_IN_MB * 1024 * 1024; // 5 MiB
        Cache cache = new Cache(httpCacheDirectory, cacheSize);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                .cache(cache)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(getString(R.string.capi_host))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build();

        capiService = retrofit.create(CapiService.class);
    }

    public CapiService getCapiService() {
        return capiService;
    }

}
