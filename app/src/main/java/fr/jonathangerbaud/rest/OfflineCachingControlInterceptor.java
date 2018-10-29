package fr.jonathangerbaud.rest;


import com.novoda.merlin.MerlinsBeard;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fr.jonathangerbaud.BaseApp;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class OfflineCachingControlInterceptor implements Interceptor
{
    private final int     cacheDuration;
    private final boolean useCache;

    /**
     * @param cacheDuration in seconds
     */
    OfflineCachingControlInterceptor(int cacheDuration, boolean useCache)
    {
        this.cacheDuration = cacheDuration;
        this.useCache = useCache;
    }

    static boolean isConnectionAvailable()
    {
        return MerlinsBeard.from(BaseApp.Companion.get()).isConnected();
    }

    @Override
    public Response intercept(Chain chain) throws IOException
    {
        // Add Cache Control only for GET methods
        if (chain.request().method().equals("GET"))
        {
            if (!isConnectionAvailable() && useCache)
            {
                Request request = chain.request();

                CacheControl cacheControl = new CacheControl.Builder()
                        .maxStale(cacheDuration, TimeUnit.SECONDS)
                        .build();

                request = request.newBuilder()
                        .cacheControl(cacheControl)
                        .build();

                return chain.proceed(request);
            }
        }

        return chain.proceed(chain.request());
    }
}
