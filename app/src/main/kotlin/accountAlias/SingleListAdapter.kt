package moe.yuuta.mipushtester.accountAlias

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import moe.yuuta.mipushtester.R

class SingleListAdapter(listener: Listener) : RecyclerView.Adapter<SingleListAdapter.ViewHolder>() {
    val mSet: MutableSet<String> = mutableSetOf()
    private val mListener: Listener = listener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_single_list, parent, false))

    override fun getItemCount(): Int = mSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val value: String = mSet.toList()[position]
        holder.title.text = value
        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                mListener.onClicked(value)
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(android.R.id.text1)
    }
}