package com.bringg.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bringg.R
import com.bringg.data.local.entity.WorkLog
import com.bringg.extensions.asEndOfDay
import com.bringg.extensions.asStartOfDay
import com.bringg.vm.WorkLogVM
import kotlinx.android.synthetic.main.fragment_statistics.*
import java.lang.StringBuilder
import java.text.DateFormat
import java.util.*

class FragmentStatistics : Fragment() {

    private lateinit var workLogsVM: WorkLogVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        work_logs_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = WorkLogsAdapter()
        work_logs_recycler_view.adapter = adapter

        workLogsVM = ViewModelProviders.of(this).get(WorkLogVM::class.java)

        val c = Calendar.getInstance()
        workLogsVM.getDateSummary(c.asStartOfDay(), c.asEndOfDay()).observe(this, Observer {
            adapter.updateData(it)
        })

    }


}

class WorkLogsAdapter : RecyclerView.Adapter<WorkLogsAdapter.Holder>() {

    private val asyncDiffer = AsyncListDiffer<WorkLog>(this, DIFFER_CALLBACK)

    fun updateData(data: List<WorkLog>) {
        asyncDiffer.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.fragmet_stat_list_item, parent, false))
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = asyncDiffer.currentList[holder.adapterPosition]

        val dateString = dateFormatter.format(item.arrivedAt)

        val duration = if (item.dwellTime > 0) {
            val durationStrBuilder = StringBuilder()
            androidx.core.util.TimeUtils.formatDuration(item.dwellTime, durationStrBuilder)
            durationStrBuilder.toString().also { durationStrBuilder.setLength(0) }
        } else {
            MISSING_DWELL
        }

        holder.textView?.text =
                String.format(
                    holder.itemView.context.getString(R.string.work_log_summary),
                    dateString,
                    duration
                )
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView?>(R.id.stat_text)
    }

    companion object {
        const val MISSING_DWELL = "-"

        private val dateFormatter = DateFormat.getDateInstance()

        val DIFFER_CALLBACK = object : DiffUtil.ItemCallback<WorkLog>() {
            override fun areItemsTheSame(oldItem: WorkLog, newItem: WorkLog): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WorkLog, newItem: WorkLog): Boolean = oldItem == newItem
        }
    }
}