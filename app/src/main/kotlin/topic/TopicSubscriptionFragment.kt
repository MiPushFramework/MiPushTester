package moe.yuuta.mipushtester.topic

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import com.annimon.stream.Collectors
import com.annimon.stream.Stream
import com.annimon.stream.function.Consumer
import com.elvishew.xlog.XLog
import moe.yuuta.mipushtester.R
import moe.yuuta.mipushtester.api.APIManager
import moe.yuuta.mipushtester.databinding.FragmentTopicSubscriptionBinding
import moe.yuuta.mipushtester.multi_state.State
import moe.yuuta.mipushtester.push.internal.PushSdkWrapper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TopicSubscriptionFragment : Fragment() {
    private val logger = XLog.tag(TopicSubscriptionFragment::class.simpleName).build()

    private lateinit var mAdapter: TopicListAdapter
    private var mGetTopicListCall: Call<MutableList<Topic>>? = null
    private lateinit var mLoadingState: State

    @Override
    override fun onCreate(@Nullable savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mAdapter = TopicListAdapter(object : TopicListAdapter.OnSelectedListener{
            override fun trigger(topic: Topic?, selected: Boolean) {
                if (topic == null) return
                if (selected) {
                    PushSdkWrapper.subscribe(requireContext(), topic.id)
                    TopicStore.get(requireContext()).subscribe(topic.id)
                } else {
                    PushSdkWrapper.unsubscribe(requireContext(), topic.id)
                    TopicStore.get(requireContext()).unsubscribe(topic.id)
                }
            }
        })
    }

    @Nullable
    @Override
    override fun onCreateView(@NonNull inflater: LayoutInflater, @Nullable container: ViewGroup?, @Nullable savedInstanceState: Bundle?): View {
        val binding = DataBindingUtil.inflate(inflater, R.layout.fragment_topic_subscription, container, false) as FragmentTopicSubscriptionBinding
        val recyclerView = binding.recyclerTopic
        recyclerView.adapter = mAdapter
        mLoadingState = State()
        mLoadingState.onRetryListener = object : View.OnClickListener {
            override fun onClick(p0: View?) {
                call()
            }
        }
        binding.state = mLoadingState
        return binding.root
    }

    @Override
    override fun onViewCreated(@NonNull view: View, @Nullable savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        call()
    }

    private fun call () {
        if (mGetTopicListCall != null) {
            (mGetTopicListCall as Call<MutableList<Topic>>).cancel()
            mGetTopicListCall = null
        }
        mGetTopicListCall = APIManager.getAvailableTopics()
        mLoadingState.showProgress()
        (mGetTopicListCall as Call<MutableList<Topic>>).enqueue(object : Callback<MutableList<Topic>> {
            @Override
            override fun onResponse(@NonNull call: Call<MutableList<Topic>>, @NonNull response: Response<MutableList<Topic>>) {
                if (call.isCanceled) return
                mLoadingState.hideProgress()
                if (!response.isSuccessful) {
                    onFailure(call, Exception("Unsuccessful code " + response.code()))
                    return
                }
                displayTopicsToUI(response.body())
            }

            @Override
            override fun onFailure(@NonNull call: Call<MutableList<Topic>>, @NonNull t: Throwable) {
                logger.e("Cannot retain topics", t)
                if (call.isCanceled) return
                mLoadingState.hideProgress()
                mLoadingState.icon.set(ContextCompat.getDrawable(requireContext(), R.mipmap.illustration_fetal_error))
                mLoadingState.text.set(getString(R.string.error_load_topics))
                mLoadingState.description.set(getString(R.string.error_description_global))
            }
        })
    }

    private fun displayTopicsToUI (originalList: MutableList<Topic>?) {
        if (originalList == null || originalList.size <= 0) {
            mLoadingState.icon.set(ContextCompat.getDrawable(requireContext(), R.mipmap.illustration_list_is_empty))
            mLoadingState.text.set(getString(R.string.topic_empty_title))
            mLoadingState.showRetry.set(false)
            mLoadingState.description.set(getString(R.string.topic_empty_description))
            return
        }
        mLoadingState.hideAll()
        val localSubscribedTopics = PushSdkWrapper.getAllTopic(requireContext())
        val list = Stream.of(originalList)
                .peek(object : Consumer<Topic> {
                    override fun accept(topic: Topic?) {
                        topic!!.subscribed = (localSubscribedTopics.contains(topic.id))
                    }
                })
                .collect(Collectors.toList())
        val result = DiffUtil.calculateDiff(object : DiffUtil.Callback() {
            @Override
            override fun getOldListSize(): Int =
                mAdapter.itemCount

            @Override
            override fun getNewListSize(): Int =
                list.size

            @Override
            override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                mAdapter.getItemAt(oldItemPosition)::class.java.name
                        .equals(list.get(newItemPosition)::class.java.name)

            @Override
            override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                mAdapter.getItemAt(oldItemPosition)
                        .equals(list.get(newItemPosition))
        })
        mAdapter.setItems(list)
        result.dispatchUpdatesTo(mAdapter)
    }

    @Override
    override fun onDestroyView() {
        if (mGetTopicListCall != null) {
            (mGetTopicListCall as Call<MutableList<Topic>>).cancel()
            mGetTopicListCall = null
        }
        super.onDestroyView()
    }
}