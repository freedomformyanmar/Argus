package io.github.freedomformyanmar.argus.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.freedomformyanmar.argus.ContactTable
import io.github.freedomformyanmar.argus.databinding.ItemContactBinding
import io.github.freedomformyanmar.argus.user.User

class ContactAdapter(
    private val onDeleteClick: (ContactTable) -> Unit
) : ListAdapter<ContactTable, ContactAdapter.ContactViewHolder>(
    object : DiffUtil.ItemCallback<ContactTable>() {

        override fun areItemsTheSame(oldItem: ContactTable, newItem: ContactTable): Boolean {
            return oldItem.phoneNumber == newItem.phoneNumber
        }

        override fun areContentsTheSame(oldItem: ContactTable, newItem: ContactTable): Boolean {
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
            tvNumber.text = itemAtIndex.phoneNumber
            tvName.text = itemAtIndex.name
            buttonDelete.setOnClickListener {
                onDeleteClick.invoke(itemAtIndex)
            }
        }
    }

    class ContactViewHolder(val itemContactBinding: ItemContactBinding) :
        RecyclerView.ViewHolder(itemContactBinding.root)

}