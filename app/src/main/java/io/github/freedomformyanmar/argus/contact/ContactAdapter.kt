package io.github.freedomformyanmar.argus.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.freedomformyanmar.argus.databinding.ItemContactBinding
import io.github.freedomformyanmar.argus.user.User

class ContactAdapter(
    private val onDeleteClick: (User) -> Unit
) : ListAdapter<User, ContactAdapter.ContactViewHolder>(
    object : DiffUtil.ItemCallback<User>() {

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.number == newItem.number
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }

    }
) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContactViewHolder {
        val binding = ItemContactBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ContactViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val itemAtIndex = getItem(position)
        holder.itemContactBinding.apply {
            tvNumber.text = itemAtIndex.number
            buttonDelete.setOnClickListener {
                onDeleteClick.invoke(itemAtIndex)
            }
        }
    }

    class ContactViewHolder(val itemContactBinding: ItemContactBinding) :
        RecyclerView.ViewHolder(itemContactBinding.root)

}