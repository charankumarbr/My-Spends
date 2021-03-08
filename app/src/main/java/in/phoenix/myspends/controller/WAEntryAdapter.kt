package `in`.phoenix.myspends.controller

import `in`.phoenix.myspends.R
import `in`.phoenix.myspends.model.WAEntity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * Created by Charan on March 07, 2021
 */
class WAEntryAdapter(private val entries: MutableList<WAEntity>,
                     private val waEntryListener: WAEntryListener?
): RecyclerView.Adapter<WAEntryViewHolder>() {

    override fun getItemCount(): Int {
        return entries.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WAEntryViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_wa_entry_item, parent, false)
        return WAEntryViewHolder(view, clickListener)
    }

    override fun onBindViewHolder(holder: WAEntryViewHolder, position: Int) {
        holder.setData(entries[position], position)
    }

    fun addNewEntry(newEntry: WAEntity) {
        entries.add(0, newEntry)
        notifyDataSetChanged()
    }

    fun deleteEntryAtPosition(position: Int): Boolean {
        entries.removeAt(position)
        return if (entries.isNotEmpty()) {
            notifyDataSetChanged()
            true
        } else {
            false
        }
    }

    private val clickListener = View.OnClickListener {
        when (it.id) {
            R.id.lweiIvWA -> {
                val position = it.tag as Int
                waEntryListener?.onWA(entries[position], position)
            }

            R.id.lweiIvDial -> {
                val position = it.tag as Int
                waEntryListener?.onDial(entries[position], position)
            }

            R.id.lweiIvDelete -> {
                val position = it.tag as Int
                waEntryListener?.onDelete(entries[position], position)
            }
        }
    }
}

interface WAEntryListener {

    fun onDelete(waEntity: WAEntity, position: Int)
    fun onDial(waEntity: WAEntity, position: Int)
    fun onWA(waEntity: WAEntity, position: Int)
}