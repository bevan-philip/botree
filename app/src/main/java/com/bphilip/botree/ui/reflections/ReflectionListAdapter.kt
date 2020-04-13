package com.bphilip.botree.ui.reflections

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bphilip.botree.R
import com.bphilip.botree.Reflection
import org.threeten.bp.format.DateTimeFormatter

class ReflectionListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<ReflectionListAdapter.WordViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var reflections = emptyList<Reflection>() // Cached copy of words

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleItemView: TextView = itemView.findViewById(R.id.textView)
        val dateItemView: TextView = itemView.findViewById(R.id.date)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_reflections, parent, false)
        return WordViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val current = reflections[position]
        holder.titleItemView.text = current.reflection
        holder.dateItemView.text = String.format("%s | %s",
            current.date?.format(DateTimeFormatter.ofPattern("EEEE")),
            current.date?.format(DateTimeFormatter.ofPattern("HH:mm")))
    }

    internal fun setWords(reflections: List<Reflection>) {
        this.reflections = reflections
        notifyDataSetChanged()
    }

    override fun getItemCount() = reflections.size

}