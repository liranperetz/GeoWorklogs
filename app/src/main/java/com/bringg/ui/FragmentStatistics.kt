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
import com.bringg.vm.WorkLogVM
import kotlinx.android.synthetic.main.fragment_statistics.*

class FragmentStatistics : Fragment() {

    private lateinit var workLogsVM: WorkLogVM

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_statistics, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        work_logs_recycler_view.layoutManager = LinearLayoutManager(context)
        val adapter = SimpleAdapter()
        work_logs_recycler_view.adapter = adapter

        workLogsVM = ViewModelProviders.of(this).get(WorkLogVM::class.java)
        workLogsVM.getAll().observe(this, Observer {
            adapter.updateData(it)
        })
    }


}

class SimpleAdapter : RecyclerView.Adapter<SimpleAdapter.Holder>() {

    private val asyncDiffer = AsyncListDiffer<WorkLog>(this, DIFFER_CALLBACK)

    fun updateData(data: List<WorkLog>) {
        asyncDiffer.submitList(data)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        return Holder(LayoutInflater.from(parent.context).inflate(R.layout.fragmet_stat_list_item, parent, false))
    }

    override fun getItemCount(): Int = asyncDiffer.currentList.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.textView?.text = asyncDiffer.currentList[holder.adapterPosition].toString()
    }

    inner class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val textView = view.findViewById<TextView?>(R.id.stat_text)
    }

    companion object {
        val DIFFER_CALLBACK = object : DiffUtil.ItemCallback<WorkLog>() {
            override fun areItemsTheSame(oldItem: WorkLog, newItem: WorkLog): Boolean = oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: WorkLog, newItem: WorkLog): Boolean = oldItem == newItem
        }
    }
}