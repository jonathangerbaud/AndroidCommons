package fr.jonathangerbaud.rest;


import java.io.EOFException;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.UnsupportedCharsetException;
import java.util.concurrent.TimeUnit;

import okhttp3.Connection;
import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

class ErrorLoggerInterceptor implements Interceptor
{
    private static final String TAG = "ErrorLoggerInterceptor";

    public ErrorLoggerInterceptor()
    {

    }


    @Override
    public Response intercept(Chain chain) throws IOException
    {
        Request request = chain.request();

        RequestBody requestBody    = request.body();
        boolean     hasRequestBody = requestBody != null;
        Charset     UTF8           = Charset.forName("UTF-8");

        Connection connection = chain.connection();
        Protocol   protocol   = connection != null ? connection.protocol() : Protocol.HTTP_1_1;
        String     message    = "--> " + request.method() + ' ' + request.url() + ' ' + protocol;
        if (hasRequestBody)
        {
            message += "\n (" + requestBody.contentLength() + "-byte body)";
        }

        if (hasRequestBody)
        {
            // Request body headers are only present when installed as a network interceptor. Force
            // them to be included (when available) so there values are known.
            if (requestBody.contentType() != null)
            {
                message += "\nContent-Type: " + requestBody.contentType();
            }
            if (requestBody.contentLength() != -1)
            {
                message += "\nContent-Length: " + requestBody.contentLength();
            }
        }

        Headers headers = request.headers();
        for (int i = 0, count = headers.size(); i < count; i++)
        {
            String name = headers.name(i);
            // Skip headers from the request body as they are explicitly logged above.
            if (!"Content-Type".equalsIgnoreCase(name) && !"Content-Length".equalsIgnoreCase(name))
            {
                message += "\n" + name + ": " + headers.value(i);
            }
        }

        if (bodyEncoded(request.headers()))
        {
            message += "\n--> END " + request.method() + " (encoded body omitted)";
        }
        else
        {
            Buffer buffer = new Buffer();
            requestBody.writeTo(buffer);

            Charset   charset     = UTF8;
            MediaType contentType = requestBody.contentType();
            if (contentType != null)
            {
                charset = contentType.charset(UTF8);
            }

            message += "\n";
            if (isPlaintext(buffer))
            {
                message += buffer.readString(charset);
                message += "\n--> END " + request.method()
                           + " (" + requestBody.contentLength() + "-byte body)";
            }
            else
            {
                message += "\n--> END " + request.method() + " (binary"
                           + requestBody.contentLength() + "-byte body omitted)";
            }
        }


        long     startNs = System.nanoTime();
        Response response;
        try
        {
            response = chain.proceed(request);
        }
        catch (Exception e)
        {
            message += "\n<-- HTTP FAILED: " + e;
            reportRequest(message);
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);

        ResponseBody responseBody  = response.body();
        long         contentLength = responseBody.contentLength();
        String       bodySize      = contentLength != -1 ? contentLength + "-byte" : "unknown-length";
        message += "\n<-- " + response.code() + ' ' + response.message() + ' '
                   + response.request().url() + " (" + tookMs + "ms" + ", " + bodySize + " body" + ')';

        headers = response.headers();
        for (int i = 0, count = headers.size(); i < count; i++)
        {
            message += "\n" + headers.name(i) + ": " + headers.value(i);
        }

        if (!HttpHeaders.hasBody(response))
        {
            message += "\n<-- END HTTP";
        }
        else if (bodyEncoded(response.headers()))
        {
            message += "\n<-- END HTTP (encoded body omitted)";
        }
        else
        {
            BufferedSource source = responseBody.source();
            source.request(Long.MAX_VALUE); // Buffer the entire body.
            Buffer buffer = source.buffer();

            Charset   charset     = UTF8;
            MediaType contentType = responseBody.contentType();
            if (contentType != null)
            {
                try
                {
                    charset = contentType.charset(UTF8);
                }
                catch (UnsupportedCharsetException e)
                {
                    message += "\n";
                    message += "\nCouldn't decode the response body; charset is likely malformed.";
                    message += "\n<-- END HTTP";
                    reportRequest(message);
                    return response;
                }
            }

            if (!isPlaintext(buffer))
            {
                message += "\n";
                message += "\n<-- END HTTP (binary " + buffer.size() + "-byte body omitted)";
                reportRequest(message);
                return response;
            }

            if (contentLength != 0)
            {
                message += "\n";
                message += buffer.clone().readString(charset);
            }

            message += "\n<-- END HTTP (" + buffer.size() + "-byte body)";
        }

        reportRequest(message);
        return response;
    }

    /**
     * Returns true if the body in question probably contains human readable text. Uses a small sample
     * of code points to detect unicode control characters commonly used in binary file signatures.
     */
    static boolean isPlaintext(Buffer buffer)
    {
        try
        {
            Buffer prefix    = new Buffer();
            long   byteCount = buffer.size() < 64 ? buffer.size() : 64;
            buffer.copyTo(prefix, 0, byteCount);
            for (int i = 0; i < 16; i++)
            {
                if (prefix.exhausted())
                {
                    break;
                }
                int codePoint = prefix.readUtf8CodePoint();
                if (Character.isISOControl(codePoint) && !Character.isWhitespace(codePoint))
                {
                    return false;
                }
            }
            return true;
        }
        catch (EOFException e)
        {
            return false; // Truncated UTF-8 sequence.
        }
    }

    private boolean bodyEncoded(Headers headers)
    {
        String contentEncoding = headers.get("Content-Encoding");
        return contentEncoding != null && !contentEncoding.equalsIgnoreCase("identity");
    }

    private void reportRequest(String message)
    {
//        Log.d(TAG, "would have reported: " + message);
        try
        {
            throw new Exception(message);
        }
        catch (Exception e)
        {
            //Crashlytics.logException(e);
        }
    }
}
