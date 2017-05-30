package jessevivanco.com.pegcitytransit.ui.adapters.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

abstract public class SimpleBaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String STATE_KEY_LIST = "list";

    private List<T> list;

    public SimpleBaseAdapter(@Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            list = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_LIST));
        }
    }

    @Override
    public int getItemCount() {
        return list != null ? list.size() : 0;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return createListItemViewHolder(parent);
    }

    abstract protected V createListItemViewHolder(ViewGroup parent);

    abstract protected void bindListItemViewHolder(T item, V holder);

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        // The holder can only be of type V at this point.
        bindListItemViewHolder(list.get(position), (V) holder);
    }

    public void onSaveInstanceState(Bundle outState) {
        if (list != null) {
            outState.putParcelable(STATE_KEY_LIST, Parcels.wrap(list));
        }
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
