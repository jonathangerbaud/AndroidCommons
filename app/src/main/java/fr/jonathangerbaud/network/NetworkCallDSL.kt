package fr.jonathangerbaud.network

import android.util.Log
import androidx.lifecycle.MutableLiveData
import fr.jonathangerbaud.utils.Timber
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.android.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Response



class CallHandler<RESPONSE : Any, DATA: Any> {
    lateinit var client: Deferred<Response<RESPONSE>>

    fun makeCall() : MutableLiveData<Resource<DATA>>
    {
        val result = MutableLiveData<Resource<DATA>>()
        result.setValue(Resource.loading(null))

        launch {
            try {
                val response = client.awaitResult().getOrThrow() as DataResponse<DATA>
                withContext(Dispatchers.Main) { result.value = Resource.success(response.retrieveData()) }
            }
            catch (e: Exception) {
                Timber.e(e)
                withContext(Dispatchers.Main) { result.value = Resource.error(0) }
            }
        }

        return result
    }
}

fun <RESPONSE: DataResponse<*>, DATA: Any> networkCall(block: CallHandler<RESPONSE, DATA>.() -> Unit): MutableLiveData<Resource<DATA>> = CallHandler<RESPONSE, DATA>().apply(block).makeCall()

interface DataResponse<T> {
    fun retrieveData(): T
}