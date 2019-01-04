package moe.yuuta.mipushtester.accountAlias

import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.elvishew.xlog.XLog
import moe.yuuta.mipushtester.R
import moe.yuuta.mipushtester.databinding.FragmentSetListBinding
import moe.yuuta.mipushtester.multi_state.State

abstract class SetListAbsFragment : Fragment(), View.OnClickListener, Listener {
    private val logger = XLog.tag(SetListAbsFragment::class.simpleName).build()
    private lateinit var mRecycler: RecyclerView
    private lateinit var mAdapter: SingleListAdapter
    protected lateinit var mState: State

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentSetListBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_set_list, container, false) as FragmentSetListBinding
        binding.fabAdd.setOnClickListener(this)
        mState = State()
        binding.state = mState
        mAdapter = SingleListAdapter(this)
        mRecycler = binding.recyclerList
        mRecycler.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
        mRecycler.adapter = mAdapter
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateData()
    }

    private fun _updateData(netSet: Set<String>) {
        val result: DiffUtil.DiffResult =
                DiffUtil.calculateDiff(object : DiffUtil.Callback() {
                    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                            mAdapter.mSet.toList()[oldItemPosition]::class.equals(netSet.toList()[newItemPosition]::class)

                    override fun getOldListSize(): Int = mAdapter.mSet.size

                    override fun getNewListSize(): Int = netSet.size

                    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                            mAdapter.mSet.toList()[oldItemPosition].equals(netSet.toList()[newItemPosition])
                })
        mAdapter.mSet.clear()
        mAdapter.mSet.addAll(netSet)
        result.dispatchUpdatesTo(mAdapter)
    }

    override fun onClick(p0: View?) = showEdit(null)

    override fun onClicked(value: String) = showEdit(value)

    private fun showEdit(current: String?) {
        val contentView: View = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_set_value, null, false)
        val summary: String? = getDialogSummary(current == null)
        if (summary != null) contentView.findViewById<TextView>(android.R.id.message).text = (summary)
        else contentView.findViewById<TextView>(android.R.id.message).visibility = View.GONE
        val editText = contentView.findViewById<TextView>(android.R.id.edit)
        editText.text = current

        val builder = AlertDialog.Builder(requireContext())
                .setTitle(if(current == null) R.string.add else R.string.modify)
                .setView(contentView)
                .setPositiveButton(android.R.string.ok, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val value = editText.text
                        val set: MutableSet<String> = mutableSetOf()
                        set.addAll(mAdapter.mSet)
                        if (value.isEmpty() || value in set) return
                        set.add(value.toString())

                        _updateData(set)
                        handleAdd(value.toString())
                    }
                })
                .setNegativeButton(android.R.string.cancel, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        p0?.dismiss()
                    }
                })
        if (current != null)
                builder.setNeutralButton(R.string.remove, object : DialogInterface.OnClickListener {
                    override fun onClick(p0: DialogInterface?, p1: Int) {
                        val value = editText.text
                        if (value.isEmpty()) return
                        val set: MutableSet<String> = mutableSetOf()
                        set.addAll(mAdapter.mSet)
                        set.remove(value.toString())

                        _updateData(set)
                        handleRemove(current)
                    }
                })
        builder.show()
    }

    abstract fun loadData(): Set<String>
    abstract fun handleAdd(value: String)
    abstract fun handleRemove(value: String)
    open fun getDialogSummary(addNew: Boolean): String? = null

    fun updateData() = _updateData(loadData())
}