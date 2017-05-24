package jessevivanco.com.pegcitytransit.ui.adapters.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.view_holders.LoadingIndicatorCell;
import jessevivanco.com.pegcitytransit.ui.view_holders.NoResultsCellViewHolder;

abstract public class BaseAdapter<T, V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String STATE_KEY_LIST = "list";
    private static final String STATE_KEY_NO_RESULTS_MESSAGE = "schedule_no_results_message";

    private static final int LIST_ITEM_VIEW_TYPE = 0;

    private List<T> list;
    private NoResultsCellViewHolder.OnRefreshButtonClickedListener onRefreshButtonClickedListener;

    @Nullable
    private String noResultsMessage;

    public BaseAdapter(@Nullable Bundle savedInstanceState, NoResultsCellViewHolder.OnRefreshButtonClickedListener onRefreshButtonClickedListener) {
        this.onRefreshButtonClickedListener = onRefreshButtonClickedListener;

        if (savedInstanceState != null) {
            list = Parcels.unwrap(savedInstanceState.getParcelable(STATE_KEY_LIST));
            noResultsMessage = savedInstanceState.getString(STATE_KEY_NO_RESULTS_MESSAGE);
        }
    }

    @Override
    public int getItemViewType(int position) {

        if (list != null && list.size() > 0) {
            return LIST_ITEM_VIEW_TYPE;

        } else if ((list == null || list.size() == 0) && noResultsMessage != null) {
            return NoResultsCellViewHolder.getLayoutResId();

        } else {
            return LoadingIndicatorCell.getLayoutResId();
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        if (viewType == LIST_ITEM_VIEW_TYPE) {
            return createListItemViewHolder(parent);

        } else if (viewType == NoResultsCellViewHolder.getLayoutResId()) {
            return new NoResultsCellViewHolder(parent, onRefreshButtonClickedListener);

        } else if (viewType == LoadingIndicatorCell.getLayoutResId()) {
            return new LoadingIndicatorCell(parent);
        }
        // Shouldn't get to this point.
        return null;
    }

    abstract protected V createListItemViewHolder(ViewGroup parent);

    abstract protected void bindListItemViewHolder(T item, V holder);

    @SuppressWarnings("unchecked")
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof NoResultsCellViewHolder) {
            ((NoResultsCellViewHolder) holder).setNoResultsText(noResultsMessage);
        } else if (holder instanceof LoadingIndicatorCell) {
            // Do nothing.
        } else {
            // The holder can only be of type V at this point.
            bindListItemViewHolder(list.get(position), (V) holder);
        }
    }

    @Override
    public int getItemCount() {
        return list != null && list.size() > 0 ?
                list.size() :
                // We'll always show something. Either a loading indicator cell or no results cell.
                1;
    }

    public void onSaveInstanceState(Bundle outState) {
        if (list != null) {
            outState.putParcelable(STATE_KEY_LIST, Parcels.wrap(list));
        }
        outState.putString(STATE_KEY_NO_RESULTS_MESSAGE, noResultsMessage);
    }

    public void setNoResultsMessage(@Nullable String noResultsMessage) {
        this.noResultsMessage = noResultsMessage;
        notifyDataSetChanged();
    }

    public void setList(List<T> list) {
        this.list = list;
        notifyDataSetChanged();
    }
}
