package moe.yuuta.mipushtester.topic

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView

import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import moe.yuuta.mipushtester.R

class TopicListAdapter(@NonNull listener: OnSelectedListener) : RecyclerView.Adapter<TopicListAdapter.ViewHolder>() {
    private var mItemList: MutableList<Topic> = mutableListOf()
    private val mSelected: MutableSet<String> = mutableSetOf()
    private var mSelectListener: OnSelectedListener = listener

    @FunctionalInterface
    interface OnSelectedListener {
        fun trigger (@NonNull topic: Topic?, selected: Boolean)
    }

    @NonNull
    @Override
    override fun onCreateViewHolder(@NonNull parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_topic, parent, false))
    }

    @Override
    override fun onBindViewHolder(@NonNull holder: ViewHolder, position: Int) {
        val topic = mItemList.get(position)
        if (topic.subscribed) mSelected.add(topic.id)
        else mSelected.remove(topic.id)
        holder.checkBox.isChecked = mSelected.contains(topic.id)
        holder.checkBox.setOnClickListener(object : View.OnClickListener {
            override fun onClick(p0: View?) {
                val checked = holder.checkBox.isChecked
                mSelectListener.trigger(topic, checked)
                if (checked) mSelected.add(topic.id)
                else mSelected.remove(topic.id)
            }
        })
        holder.title.text = topic.title
        holder.description.text = topic.description
    }

    @Override
    override fun getItemCount(): Int {
        return mItemList.size
    }

    fun getItemAt (position: Int): Topic {
        return mItemList.get(position)
    }

    fun setItems (@NonNull newList: MutableList<Topic>) {
        mItemList = newList
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(android.R.id.text1)
        val description: TextView = itemView.findViewById(android.R.id.text2)
        val checkBox: CheckBox = itemView.findViewById(R.id.check_subscribe)
    }
}
