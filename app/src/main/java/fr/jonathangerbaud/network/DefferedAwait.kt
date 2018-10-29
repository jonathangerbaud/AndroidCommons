package fr.jonathangerbaud.network

import fr.jonathangerbaud.utils.Timber
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.HttpException
import retrofit2.Response
import kotlin.coroutines.resume


suspend fun <T : Any> Deferred<Response<T>>.awaitResult(): Result<T> {
    return suspendCancellableCoroutine { continuation ->

        launch {
            try {
                val response = await()

                continuation.resume(
                        if (response.isSuccessful) {
                            val body = response.body()
                            body?.let {
                                Result.Ok(it, response.raw())
                            } ?: "error".let {
                                Timber.e("error oh")
                                if (response.code() == 200){
                                    Result.Exception(Exception("body is empty"))
                                }
                                else{
                                    Result.Exception(NullPointerException("Response body is null"))
                                }
                            }

                        } else {
                            Result.Error(HttpException(response), response.raw())
                        }
                                   )
            }
            catch (e:Throwable){
                Timber.e(e)
                continuation.resume(Result.Exception(e))
            }

        }

        registerOnCompletion(continuation)
    }
}



private fun Deferred<Response<*>>.registerOnCompletion(continuation: CancellableContinuation<*>) {
    continuation.invokeOnCompletion {
        if (continuation.isCancelled)
            try {
                cancel()
            } catch (ex: Throwable) {
                Timber.e((ex))
                //Ignore cancel exception
                ex.printStackTrace()
            }
    }
}

