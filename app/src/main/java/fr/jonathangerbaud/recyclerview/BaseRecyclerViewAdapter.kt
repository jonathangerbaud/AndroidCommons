package fr.jonathangerbaud.recyclerview

import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlin.reflect.KClass


abstract class BaseRecyclerViewAdapter : Adapter<BaseViewHolder>()
{
    protected val DEFAULT_TYPE = 0

    private val viewHolderMap: MutableList<ViewHolderFactory> = mutableListOf()
    private val typeMap: MutableMap<KClass<*>, Int>

    var data: MutableList<Any> = mutableListOf()
        set(value) {
            field = value
            notifyDataSetChanged()
            liveData.value = value
        }

    val liveData: MutableLiveData<List<Any>>

    init
    {
        typeMap = HashMap()
        liveData = MutableLiveData()
    }

    fun addView(type: KClass<*>, factory: ViewHolderFactory)
    {
        viewHolderMap.add(factory)
        typeMap[type] = viewHolderMap.size - 1
    }

    override fun getItemViewType(position: Int): Int
    {
        val type: KClass<Any> = data[position].javaClass.kotlin

        if (typeMap.containsKey(type))
            return typeMap.getValue(type)

        return DEFAULT_TYPE
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        return viewHolderMap[viewType].build(parent)
    }

    override fun getItemCount(): Int {
        return data.size
    }

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        viewHolderMap[getItemViewType(position)].bind(holder, data[position], position)
    }
}