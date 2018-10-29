package fr.jonathangerbaud.uimanager

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import fr.jonathangerbaud.network.Resource
import java.lang.ref.WeakReference

class DataLoaderDelegate<T> : DefaultLifecycleObserver, Observer<Resource<T>>
{
    private val callback: WeakReference<DataLoaderCallback<T>>
    private var stateManager: UIStateManager? = null
    private val dataFactory: () -> MutableLiveData<Resource<T>>

    private var data: MutableLiveData<Resource<T>>? = null

    private var dataLoaded: Boolean = false

    constructor(
        callback: DataLoaderCallback<T>,
        dataFactory: () -> MutableLiveData<Resource<T>>
    )
    {
        this.callback = WeakReference(callback)
        this.dataFactory = dataFactory

        callback.lifecycle.addObserver(this)
    }

    constructor(
        callback: DataLoaderCallback<T>,
        stateManager: UIStateManager?,
        dataFactory: () -> MutableLiveData<Resource<T>>
    )
    {
        this.callback = WeakReference(callback)
        this.dataFactory = dataFactory

        if (stateManager != null)
            initViews(stateManager)

        callback.lifecycle.addObserver(this)
    }

    private fun initViews(stateManager: UIStateManager?)
    {
        this.stateManager = stateManager

        if (stateManager != null)
        {
            val dataView = stateManager.dataView
            if (dataView != null && dataView is SwipeRefreshLayout)
            {
                dataView.setOnRefreshListener { load(true) }
            }

            val dataStateView = stateManager.dataStateView
            dataStateView?.get()?.setRetryCallback(RetryCallback { load(true) })
        }
    }

    override fun onResume(owner: LifecycleOwner)
    {
        load(false)
    }

    fun load(force: Boolean)
    {
        if (!dataLoaded || force)
        {
            setState(if (dataLoaded) UIStateManager.State.LOADING else UIStateManager.State.LOADING_FIRST)

            if (data != null)
                data!!.removeObserver(this)

            data = dataFactory()

            // Might return null for whatever reasons
                data?.observe(callback.get()!!, this)
        }
    }

    fun setStateManager(stateManager: UIStateManager)
    {
        initViews(stateManager)

        if (dataLoaded)
            setState(UIStateManager.State.DATA)
    }

    override fun onChanged(tResource: Resource<T>)
    {
        when (tResource.status)
        {
            Resource.SUCCESS ->
            {
                if (callback.get() != null)
                {
                    if (tResource.data is Collection<*> && (tResource.data as Collection<*>).isEmpty())
                    {
                        setState(UIStateManager.State.EMPTY)
                    }
                    else
                    {
                        setState(UIStateManager.State.DATA)
                        callback.get()?.onDataLoaded(tResource.data)
                    }

                    dataLoaded = true
                }
            }
            Resource.LOADING ->
            {
            }
            Resource.ERROR   ->
            {
                setState(UIStateManager.State.ERROR)
            }
        }
    }

    private fun setState(state: UIStateManager.State)
    {
        stateManager?.state = state
    }


    interface DataLoaderCallback<T> : LifecycleOwner
    {
        fun onDataLoaded(data: T?)
    }
}
