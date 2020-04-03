package com.bphilip.botree

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.threeten.bp.format.DateTimeFormatter
import kotlin.math.abs

class MeditationListAdapter internal constructor(
    context: Context
) : RecyclerView.Adapter<MeditationListAdapter.MeditationViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var meditations = emptyList<Meditation>() // Cached copy of words

    inner class MeditationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleItemView: TextView = itemView.findViewById(R.id.title_meditation)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeditationViewHolder {
        val itemView = inflater.inflate(R.layout.recyclerview_meditations, parent, false)
        return MeditationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MeditationViewHolder, position: Int) {
        val current = meditations[position]
        holder.titleItemView.text = String.format("%s | Duration: %s",
            current.date?.format(DateTimeFormatter.ISO_DATE),
            Utility.timeFormatter(current.duration?.toMillis() as Long)
        )
    }

    internal fun setWords(meditations: List<Meditation>) {
        this.meditations = meditations
        notifyDataSetChanged()
    }

    override fun getItemCount() = meditations.size
}