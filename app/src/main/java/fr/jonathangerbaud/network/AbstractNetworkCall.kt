package fr.jonathangerbaud.network

import java.net.SocketTimeoutException


abstract class AbstractNetworkCall
{

    var networkError: NetworkError = NetworkError.NONE
    var error: Throwable? = null

    abstract fun execute()

    fun run()
    {
        try
        {
            execute()
        }
        catch (e: SocketTimeoutException)
        {
            networkError = NetworkError.NETWORK_TIMEOUT
            error = e

            onRequestError()
        }
        catch (throwable: Throwable)
        {
            networkError = NetworkError.UNKNOWN
            error = throwable

            onRequestError()
        }


    }

    abstract fun onRequestError()
}