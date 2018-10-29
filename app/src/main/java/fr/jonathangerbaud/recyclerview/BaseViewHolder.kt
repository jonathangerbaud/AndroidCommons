package fr.jonathangerbaud.recyclerview

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.jonathangerbaud.ktx.inflate


open class BaseViewHolder(parent: ViewGroup, layout: Int) :
        RecyclerView.ViewHolder(parent.inflate(layout))
{
}