package moe.yuuta.mipushtester.topic;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.elvishew.xlog.Logger;
import com.elvishew.xlog.XLog;
import com.google.android.material.snackbar.Snackbar;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;
import java.util.stream.Collectors;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import moe.yuuta.mipushtester.R;
import moe.yuuta.mipushtester.api.APIManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TopicSubscriptionFragment extends Fragment {
    private final Logger logger = XLog.tag(TopicSubscriptionFragment.class.getSimpleName()).build();

    private TopicListAdapter mAdapter;
    private Call<List<Topic>> mGetTopicListCall;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TopicListAdapter((topic, selected) -> {
            if (selected) {
                MiPushClient.subscribe(requireContext(), topic.getId(), null);
                TopicStore.create(requireContext()).subscribe(topic.getId());
            } else {
                MiPushClient.unsubscribe(requireContext(), topic.getId(), null);
                TopicStore.create(requireContext()).unsubscribe(topic.getId());
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_topic_subscription, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_topic);
        recyclerView.setAdapter(mAdapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mGetTopicListCall = APIManager.getInstance().getAvailableTopics();
        mGetTopicListCall.enqueue(new Callback<List<Topic>>() {
            @Override
            public void onResponse(@NonNull Call<List<Topic>> call, @NonNull Response<List<Topic>> response) {
                if (call.isCanceled()) return;
                if (!response.isSuccessful()) {
                    onFailure(call, new Exception("Unsuccessful code " + response.code()));
                    return;
                }
                displayTopicsToUI(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<Topic>> call, @NonNull Throwable t) {
                logger.e("Cannot retain topics", t);
                if (call.isCanceled()) return;
                Snackbar.make(getView(), R.string.error_load_topics, Snackbar.LENGTH_INDEFINITE).show();
            }
        });
    }

    private void displayTopicsToUI (List<Topic> originalList) {
        List<String> localSubscribedTopics = MiPushClient.getAllTopic(requireContext());
        List<Topic> list = originalList.stream()
                .peek(topic -> topic.setSubscribed(localSubscribedTopics.contains(topic.getId())))
                .collect(Collectors.toList());
        DiffUtil.DiffResult result = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return mAdapter.getItemCount();
            }

            @Override
            public int getNewListSize() {
                return list.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return mAdapter.getItemAt(oldItemPosition).getClass().getName()
                        .equals(list.get(newItemPosition).getClass().getName());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                return mAdapter.getItemAt(oldItemPosition)
                        .equals(list.get(newItemPosition));
            }
        });
        mAdapter.setItems(list);
        result.dispatchUpdatesTo(mAdapter);
    }

    @Override
    public void onDestroyView() {
        if (mGetTopicListCall != null) {
            mGetTopicListCall.cancel();
            mGetTopicListCall = null;
        }
        super.onDestroyView();
    }
}
