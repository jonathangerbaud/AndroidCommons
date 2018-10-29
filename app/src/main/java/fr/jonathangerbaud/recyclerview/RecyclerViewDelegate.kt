package fr.jonathangerbaud.recyclerview

import android.view.ViewGroup
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView.Adapter
import kotlin.reflect.KClass


interface RecyclerViewDelegate
{
    val DEFAULT_TYPE
            get() = 0

    private val viewHolderMap: MutableList<ViewHolderFactory>
            get() = mutableListOf()
    private val typeMap: MutableMap<KClass<*>, Int>
        get() = HashMap()

    val data: List<Any>
        get() = mutableListOf()
        /*set(value) {
            field = value
            notifyDataSetChanged()
            liveData.value = value
        }*/

    private val liveData: MutableLiveData<List<Any>>
        get() = MutableLiveData()

    fun addView(type: KClass<*>, factory: ViewHolderFactory)
    {
        viewHolderMap.add(factory)
        typeMap[type] = viewHolderMap.size - 1
    }

    fun getItemViewType(position: Int): Int
    {
        val type: KClass<Any> = data[position].javaClass.kotlin

        if (typeMap.containsKey(type))
            return typeMap.getValue(type)

        return DEFAULT_TYPE
    }


    fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder
    {
        return viewHolderMap[viewType].build(parent)
    }

    fun getItemCount(): Int {
        return data.size
    }

    fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        viewHolderMap[getItemViewType(position)].bind(holder, data[position], position)
    }
}