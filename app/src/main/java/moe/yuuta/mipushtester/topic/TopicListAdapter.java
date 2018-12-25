package moe.yuuta.mipushtester.topic;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import moe.yuuta.mipushtester.R;

public class TopicListAdapter extends RecyclerView.Adapter<TopicListAdapter.ViewHolder> {
    private List<Topic> mItemList = new ArrayList<>(0);
    private Set<String> mSelected = new HashSet<>(3);
    private OnSelectedListener mSelectListener;

    @FunctionalInterface
    interface OnSelectedListener {
        void trigger (@NonNull Topic topic, boolean selected);
    }

    TopicListAdapter (@NonNull OnSelectedListener listener) {
        super();
        mSelectListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_topic, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Topic topic = mItemList.get(position);
        if (topic.isSubscribed()) mSelected.add(topic.getId());
        else mSelected.remove(topic.getId());
        if (mSelected.contains(topic.getId())) {
            holder.checkBox.setChecked(true);
        } else {
            holder.checkBox.setChecked(false);
        }
        holder.checkBox.setOnClickListener(v -> {
            boolean checked = holder.checkBox.isChecked();
            mSelectListener.trigger(topic, checked);
            if (checked) mSelected.add(topic.getId());
            else mSelected.remove(topic.getId());
        });
        holder.title.setText(topic.getTitle());
        holder.description.setText(topic.getDescription());
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public Topic getItemAt (int position) {
        return mItemList.get(position);
    }

    public void setItems (@NonNull List<Topic> newList) {
        mItemList = newList;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView title;
        private TextView description;
        private CheckBox checkBox;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(android.R.id.text1);
            description = itemView.findViewById(android.R.id.text2);
            checkBox = itemView.findViewById(R.id.check_subscribe);
        }
    }
}
