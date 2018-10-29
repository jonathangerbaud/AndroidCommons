package fr.jonathangerbaud.rest;


import com.novoda.merlin.MerlinsBeard;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import fr.jonathangerbaud.BaseApp;
import okhttp3.CacheControl;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class OnlineCachingControlInterceptor implements Interceptor
{
    private final int     cacheDuration;
    private final boolean useCache;

    OnlineCachingControlInterceptor(int cacheDuration, boolean useCache)
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
            //Response response = chain.proceed(chain.request());

            if (isConnectionAvailable())
            {
                Request request = chain.request();

                CacheControl.Builder cacheControl = new CacheControl.Builder();

                if (!useCache || cacheDuration == 0)
                    cacheControl.noCache();
                else
                    cacheControl.maxAge(cacheDuration, TimeUnit.SECONDS);

                cacheControl.build();

                request = request.newBuilder()
                        .cacheControl(cacheControl.build())
                        .build();

                return chain.proceed(request);

                // re-write response header to force use of cache
                /*CacheControl cacheControl = new CacheControl.Builder()
                        .maxAge(useCache ? cacheDuration : 0, TimeUnit.MINUTES)
                        .build();

                return response.newBuilder()
                        .header("Cache-Control", cacheControl.toString())
                        .build();*/
            }
        }

        return chain.proceed(chain.request());
    }
}
