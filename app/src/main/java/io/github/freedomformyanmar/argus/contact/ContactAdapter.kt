package io.github.freedomformyanmar.argus.contact

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import io.github.freedomformyanmar.argus.ContactTable
import io.github.freedomformyanmar.argus.R
import io.github.freedomformyanmar.argus.databinding.ItemContactBinding

class ContactAdapter(
    private val onDeleteClick: (ContactTable) -> Unit,
    private val onEditClick: (ContactTable) -> Unit
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
        return ContactViewHolder(binding).also {
            it.itemContactBinding.ivMore.setOnClickListener { view ->
                with(PopupMenu(it.itemView.context, view)) {
                    menuInflater.inflate(R.menu.popup_menu_contact, menu)
                    setOnMenuItemClickListener { item ->
                        if (item.itemId == R.id.action_delete) {
                            onDeleteClick.invoke(getItem(it.adapterPosition))
                            return@setOnMenuItemClickListener true
                        } else if (item.itemId == R.id.action_edit_name) {
                            onEditClick.invoke(getItem(it.adapterPosition))
                            return@setOnMenuItemClickListener true
                        }

                        return@setOnMenuItemClickListener false
                    }
                    show()
                }
            }
        }
    }

    override fun onBindViewHolder(holder: ContactViewHolder, position: Int) {
        val itemAtIndex = getItem(position)
        holder.itemContactBinding.apply {
            tvNumber.text = itemAtIndex.phoneNumber
            tvName.text = itemAtIndex.name
        }
    }

    class ContactViewHolder(val itemContactBinding: ItemContactBinding) :
        RecyclerView.ViewHolder(itemContactBinding.root)

}