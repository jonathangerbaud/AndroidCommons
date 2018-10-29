package fr.jonathangerbaud.rest;

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory;
import fr.jonathangerbaud.BaseApp;
import fr.jonathangerbaud.BuildConfig;
import okhttp3.Cache;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;

import java.io.File;
import java.util.concurrent.TimeUnit;

import static fr.jonathangerbaud.utils.LogKt.e;

public class ServiceBuilder
{
    public static long SECOND = 1;
    public static int  MINUTE = 60;
    public static int  HOUR   = 60 * MINUTE;
    public static int  DAY    = 24 * HOUR;

    public static int KB = 1024;
    public static int MB = 1024 * KB;

    private static int DEFAULT_CACHE_DURATION = 10 * MINUTE;
    private static int DEFAULT_CACHE_SIZE     = 10 * MB;

    private final String server;

    private int     cacheDuration;
    private long    cacheSize = DEFAULT_CACHE_SIZE;
    private boolean useCacheIfNoConnection;
    private boolean useCacheIfConnection = true;

    private boolean                      enableLogging = false;
    private HttpLoggingInterceptor.Level loggingLevel  = HttpLoggingInterceptor.Level.BODY;

    private boolean reportResult;

    private int connectTimeout = 60;
    private int readTimeout    = 60;
    private int writeTimeout   = 60;

    public ServiceBuilder(String server)
    {
        this.server = server;
    }

    /*public static <T> T parseError(ResponseBody errorBody, T clazz)
    {
        Retrofit retrofit = retrofitBuilder.build();

        Converter<ResponseBody, T> converter = retrofit.responseBodyConverter(clazz.getClass(), clazz.getClass()
                .getAnnotations());

        try
        {
            return converter.convert(errorBody);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        return clazz;
    }*/

    /**
     * Define the max valid age before cache is expired
     *
     * @param duration in seconds. Use <code>SECONDS</code>, <code>MINUTES</code>, <code>HOURS</code> and
     *                 <code>DAYS</code> constants to
     *                 help
     * @return The ServiceBuilder instance for method chaining
     */
    public ServiceBuilder cacheDuration(int duration)
    {
        cacheDuration = duration;
        return this;
    }

    /**
     * Define the max cache size
     *
     * @param size in byte. Use <code>KB</code> and <code>MB</code> constants to help
     * @return The ServiceBuilder instance for method chaining
     */
    public ServiceBuilder cacheSize(long size)
    {
        cacheSize = size;
        return this;
    }

    /**
     * Set the cache duration to a default 10 minutes
     * and enable cache use when no connection available
     *
     * @return The ServiceBuilder instance for method chaining
     */
    public ServiceBuilder defaultCache()
    {
        cacheDuration = DEFAULT_CACHE_DURATION;
        cacheSize = DEFAULT_CACHE_SIZE;
        useCacheIfNoConnection = true;
        return this;
    }

    public ServiceBuilder enableLogs()
    {
        enableLogging = true;
        return this;
    }

    public ServiceBuilder enableLogs(HttpLoggingInterceptor.Level loggingLevel)
    {
        enableLogging = true;
        this.loggingLevel = loggingLevel;
        return this;
    }

    public ServiceBuilder reportResult()
    {
        reportResult = true;
        return this;
    }

    /**
     * Enable cache use when no connection available
     *
     * @return The ServiceBuilder instance for method chaining
     */
    public ServiceBuilder useCacheIfNoConnection()
    {
        useCacheIfNoConnection = true;
        return this;
    }

    public ServiceBuilder noCacheIfConnection()
    {
        useCacheIfConnection = false;
        return this;
    }

    public ServiceBuilder setConnectTimeout(int connectTimeout)
    {
        this.connectTimeout = connectTimeout;
        return this;
    }

    public ServiceBuilder setReadTimeout(int readTimeout)
    {
        this.readTimeout = readTimeout;
        return this;
    }

    public ServiceBuilder setWriteTimeout(int writeTimeout)
    {
        this.writeTimeout = writeTimeout;
        return this;
    }

    public <T> T build(final Class<T> service)
    {
        return buildRetrofit().create(service);
    }

    private Retrofit buildRetrofit()
    {
        Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                .client(buildOkHttpClient())
                .baseUrl(server)
                .addCallAdapterFactory(CoroutineCallAdapterFactory.create());

        configureRetrofit(retrofitBuilder);

        return retrofitBuilder.build();
    }

    /**
     * Override this method to add additional options to Retrofit
     * Things like call adapters, converters, callback executors, ...
     *
     * @param retrofitBuilder
     */
    protected void configureRetrofit(Retrofit.Builder retrofitBuilder)
    {

    }

    private OkHttpClient buildOkHttpClient()
    {
        OkHttpClient.Builder okHttpBuilder = new OkHttpClient.Builder();

        okHttpBuilder.connectTimeout(connectTimeout, TimeUnit.SECONDS)
                .readTimeout(readTimeout, TimeUnit.SECONDS)
                .writeTimeout(writeTimeout, TimeUnit.SECONDS);

        if (useCacheIfConnection || useCacheIfNoConnection)
        {
            try
            {
                okHttpBuilder.cache(new Cache(new File(BaseApp.Companion.get().getCacheDir(), "http-cache"), cacheSize))
                ; // 10 MB
            }
            catch (Exception e)
            {
                e(e, "Could not create Cache!");
            }
        }

        okHttpBuilder.addInterceptor(chain -> {
            Request request = alterRequest(chain.request());

            Request.Builder builder = request.newBuilder();

            addHeaders(builder);

            return chain.proceed(builder.build());
        });

        if (enableLogging && BuildConfig.DEBUG)
            okHttpBuilder.addInterceptor(new HttpLoggingInterceptor().setLevel(loggingLevel));

        if (useCacheIfNoConnection)
            okHttpBuilder.addInterceptor(new OfflineCachingControlInterceptor(cacheDuration, useCacheIfNoConnection));

        if (useCacheIfConnection)
            okHttpBuilder.addInterceptor(new OnlineCachingControlInterceptor(cacheDuration, useCacheIfConnection));

        if (reportResult)
            okHttpBuilder.addInterceptor(new ErrorLoggerInterceptor());

        configureOkHttpClient(okHttpBuilder);

        return okHttpBuilder.build();
    }

    /**
     * Override this method to add additional options to OkHttpClient
     * Set things like interceptors, certificates, authenticator, caching, ...
     *
     * @param okHttpBuilder
     */
    protected void configureOkHttpClient(OkHttpClient.Builder okHttpBuilder)
    {

    }

    /**
     * Allow to modify the original request, to add automatic uri parameters for example
     *
     * @param request
     * @return
     */
    protected Request alterRequest(Request request)
    {
        return request;
    }

    /**
     * Override this method to add headers to your request
     *
     * @param builder
     */
    protected void addHeaders(Request.Builder builder)
    {
        /**
         * Example
         *
         * builder.addHeader("Accept-Language", "fr");
         *
         * if (accessToken != null)
         builder.addHeader("Authorization", tokenType + " " + accessToken);
         */
    }

    public static void clearCache()
    {
        try
        {
            File dir = new File(BaseApp.Companion.get().getCacheDir(), "http-cache");

            for (File file : dir.listFiles())
            {
                file.delete();
            }

            dir.delete();
        }
        catch (Exception e)
        {

        }
    }
}