package fr.jonathangerbaud.recyclerview

import android.view.ViewGroup


interface ViewHolderFactory
{
    fun build(viewGroup: ViewGroup) : BaseViewHolder
    fun bind(holder: BaseViewHolder, item: Any, position: Int)
}