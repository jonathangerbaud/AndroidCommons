package fr.jonathangerbaud.uimanager

import android.view.View
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import fr.jonathangerbaud.ktx.hide
import fr.jonathangerbaud.ktx.invisible
import fr.jonathangerbaud.ktx.show
import java.lang.ref.WeakReference

class UIStateManager @JvmOverloads constructor(dataView: View?, dataStateView: DataStateView, extraView: View? = null) {

    val dataView: WeakReference<View>?
    val dataStateView: WeakReference<DataStateView>?
    val extraView: WeakReference<View>?

    private val isSRL: Boolean

    var state: State? = null
        set(state) {
            if (dataView!!.get() != null) {
                if (state == State.DATA || state == State.LOADING && isSRL) {
                    dataView.get()!!.show()

                    if (isSRL)
                        (dataView.get() as SwipeRefreshLayout).isRefreshing = state == State.LOADING
                } else {
                    dataView.get()!!.invisible()
                }
            }

            if (dataStateView!!.get() != null) {
                if (state == State.DATA || state == State.EXTRA || state == State.LOADING && isSRL) {
                    dataStateView.get()!!.hide()
                } else {
                    dataStateView.get()!!.show()

                    if (state == State.LOADING || state == State.LOADING_FIRST)
                        dataStateView.get()!!.setStateLoading()
                    else if (state == State.EMPTY)
                        dataStateView.get()!!.setStateEmpty()
                    else if (state == State.ERROR)
                        dataStateView.get()!!.setStateError()
                }
            }

            if (extraView?.get() != null)
                extraView.get()!!.show(state == State.EXTRA)

            field = state
        }

    enum class State {
        DATA,
        LOADING,
        LOADING_FIRST,
        EMPTY,
        ERROR,
        EXTRA
    }

    init {
        this.dataView = WeakReference<View>(dataView)
        this.dataStateView = WeakReference(dataStateView)
        this.extraView = WeakReference<View>(extraView)

        isSRL = dataView != null && dataView is SwipeRefreshLayout
    }

    fun getDataView(): View? {
        return dataView?.get()

    }

    fun getDataStateView(): DataStateView? {
        return dataStateView?.get()

    }

    fun getExtraView(): View? {
        return extraView?.get()

    }
}