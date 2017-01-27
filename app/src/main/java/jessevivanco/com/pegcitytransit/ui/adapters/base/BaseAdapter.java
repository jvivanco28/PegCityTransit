package jessevivanco.com.pegcitytransit.ui.adapters.base;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import org.parceler.Parcels;

import java.util.List;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.provider.base.ListProvider;
import jessevivanco.com.pegcitytransit.repositories.OnRepositoryDataRetrievedListener;
import jessevivanco.com.pegcitytransit.ui.view_holders.ErrorCellViewHolder;

/**
 * Generic way of handling list loading for our adapters. Handles <b>three</b> list states: error state, empty state,
 * normal state.
 *
 * @param <T> The adapter will contain a <code>List</code> of type <code>T</code>.
 */
public abstract class BaseAdapter<T>
        extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements OnRepositoryDataRetrievedListener<List<T>>,
        ErrorCellViewHolder.OnRetryClickListener {

    private static final String STATE_KEY_IS_LOADING = "is_loading";
    private static final String STATE_KEY_IS_ERROR = "is_error";
    private static final String STATE_KEY_LIST = "list";

    protected ListProvider.ListProviderViewContract onListLoadedCallback;

    private List<T> list;
    private ListProvider provider;

    private boolean isLoading = false;
    private boolean isError = false;


    public BaseAdapter(@Nullable Bundle savedInstanceState,
                       @Nullable ListProvider.ListProviderViewContract onListLoadedCallback,
                       @Nullable ListProvider provider) {

        this.provider = provider;

        onRestoreInstanceState(savedInstanceState);
        setOnListLoadedCallback(onListLoadedCallback);
        setHasStableIds(dataHasStableIds());
    }

    /**
     * @return The number of elements within the adapter's list (only data elements).
     */
    public int getDataCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * @return The count of <b>all</b> cells within the list (includes loading cell, error cells, etc.).
     */
    @Override
    final public int getItemCount() {
        int itemCount = list == null ? 0 : list.size();

        // If we're loading, then we need an extra cell for the loading cell.
        if (isLoading) {
            itemCount++;
        }

        // If there was an error, then we need an extra cell for the error cell.
        if (isError) {
            itemCount++;
        }

        // If there are no results, then we need an extra cell for the no results cell.
        if (itemCount == 0) {
            itemCount++;
        }

        return itemCount;
    }

    @Override
    final public long getItemId(int position) {
        if (getItemViewType(position) == getLoadingResultsLayout()) {
            return getLoadingResultsLayout();
        }

        if (getItemViewType(position) == getErrorResultsLayout()) {
            return getErrorResultsLayout();
        }

        if (getItemViewType(position) == getNoResultsLayout()) {
            return getNoResultsLayout();
        }

        return getDataId(position);
    }

    /**
     * Determines which view type should be displayed for the current state of the adapter.
     *
     * @param position
     * @return
     */
    @Override
    final public int getItemViewType(int position) {

        if (isLoading && position >= getDataCount()) {
            return getLoadingResultsLayout();
        }

        if (isError && position >= getDataCount()) {
            return getErrorResultsLayout();
        }

        if (getDataCount() == 0 && position == 0) {
            return getNoResultsLayout();
        }
        return getDataViewType(position);
    }

    @Override
    final public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == getLoadingResultsLayout()) {
            return onCreateLoadingResultsViewHolder(parent);
        }

        if (viewType == getErrorResultsLayout()) {
            return onCreateErrorResultsViewHolder(parent);
        }

        if (viewType == getNoResultsLayout()) {
            return onCreateNoResultsViewHolder(parent);
        }

        return onCreateDataViewHolder(parent, viewType);
    }

    @Override
    final public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (isLoading && getItemViewType(position) == getLoadingResultsLayout()) {
            onBindLoadingView(holder);
        } else if (isError && getItemViewType(position) == getErrorResultsLayout()) {
            onBindErrorView(holder);
        } else if (getItemViewType(position) == getNoResultsLayout()) {
            onBindNoResultsView(holder);
        } else {
            onBindDataViewHolder(holder, position);
        }
    }

    /**
     * Attempts to restore the state of the adapter from the given instance state bundle.
     *
     * @param state
     */
    public void onRestoreInstanceState(@Nullable Bundle state) {
        if (state != null) {
            setLoading(state.getBoolean(getClass().getSimpleName() + "_" + STATE_KEY_IS_LOADING));
            setError(state.getBoolean(getClass().getSimpleName() + "_" + STATE_KEY_IS_ERROR));

            List<T> restoredList = Parcels.unwrap(state.getParcelable(getClass().getSimpleName() + "_" +
                    STATE_KEY_LIST));
            setList(restoredList);

            if (provider != null) {
                provider.onRestoreInstanceState(state);
            }
        }
    }

    public void setOnListLoadedCallback(ListProvider.ListProviderViewContract onListLoadedCallback) {
        this.onListLoadedCallback = onListLoadedCallback;
    }

    /**
     * Saves the state of the adapter to the instance state bundle.
     *
     * @param outState
     * @return
     */
    public Bundle onSaveInstanceState(Bundle outState) {
        outState.putBoolean(getClass().getSimpleName() + "_" + STATE_KEY_IS_LOADING, isLoading());
        outState.putBoolean(getClass().getSimpleName() + "_" + STATE_KEY_IS_ERROR, isError());

        outState.putParcelable(getClass().getSimpleName() + "_" + STATE_KEY_LIST, Parcels.wrap(getList()));

        if (provider != null) {
            provider.onSaveInstanceState(outState);
        }

        return outState;
    }

    @Override
    public void onDataRetrieved(@Nullable List<T> data) {
        setList(data);
        setLoading(false);
        setError(false);
        notifyDataSetChanged();

        if (onListLoadedCallback != null) {
            onListLoadedCallback.onFinishedLoading();
        }
    }

    @Override
    public void onError(String message) {
        setLoading(false);
        setError(true);
        notifyDataSetChanged();

        if (onListLoadedCallback != null) {
            onListLoadedCallback.onListLoadError(message);
            onListLoadedCallback.onFinishedLoading();
        }
    }

    /**
     * The user tapped on the "try again" button. We'll just invoke a refresh of the list.
     */
    @Override
    public void onRetryLoad() {
        refreshList();
    }

    /**
     * Clears the list, displays a loading cell, and fetches the request.
     */
    public void refreshList() {
        setError(false);
        setLoading(true);
        setList(null);
        notifyDataSetChanged();

        fetchData();
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }

    public List<T> getList() {
        return list;
    }

    public void setList(@Nullable List<T> list) {
        this.list = list;
    }

    public ListProvider getProvider() {
        return provider;
    }

    public void setProvider(ListProvider provider) {
        this.provider = provider;
    }

    protected RecyclerView.ViewHolder onCreateLoadingResultsViewHolder(ViewGroup parent) {
        return new ViewHolder(parent, getLoadingResultsLayout());
    }

    protected RecyclerView.ViewHolder onCreateErrorResultsViewHolder(ViewGroup parent) {
        return new ErrorCellViewHolder(parent);
    }

    protected RecyclerView.ViewHolder onCreateNoResultsViewHolder(ViewGroup parent) {
        return new ErrorCellViewHolder(parent);
    }

    protected void onBindNoResultsView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ErrorCellViewHolder) {

            ((ErrorCellViewHolder) viewHolder).bind(viewHolder.itemView.getContext().getString(R.string
                    .list_no_results_message), this);
        }
    }

    protected void onBindLoadingView(RecyclerView.ViewHolder viewHolder) {
        // Nothing to do.
    }

    protected void onBindErrorView(RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof ErrorCellViewHolder) {
            ((ErrorCellViewHolder) viewHolder).bind(viewHolder.itemView.getContext().getString(R.string
                    .list_load_error_message), this);
        }
    }

    /**
     * Return the type of view for the element in this position.
     *
     * @param position
     * @return
     */
    public abstract int getDataViewType(int position);

    /**
     * @return <code>true</code> if each model has a unique identifier, <code>false</code> otherwise.
     */
    protected abstract boolean dataHasStableIds();

    /**
     * Signal that we need to fetch/refresh the content.
     */
    abstract public void fetchData();

    /**
     * Return the unique id for the element in this position.
     *
     * @param position
     * @return
     */
    public abstract long getDataId(int position);


    /**
     * Return an appropriate view holder for the given item type.
     *
     * @param parent
     * @param viewType
     * @return
     */
    protected abstract RecyclerView.ViewHolder onCreateDataViewHolder(ViewGroup parent, int viewType);

    /**
     * Perform appropriate binding of the view to the data in this position.
     *
     * @param holder
     * @param position
     */
    protected abstract void onBindDataViewHolder(RecyclerView.ViewHolder holder, int position);

    /**
     * The layout resource identifier to be displayed if there are no results.
     *
     * @return
     */
    protected
    @LayoutRes
    int getNoResultsLayout() {
        return R.layout.cell_list_load_error;
    }

    /**
     * The layout resource identifier to be displayed if results are loading.
     *
     * @return
     */
    protected
    @LayoutRes
    int getLoadingResultsLayout() {
        return R.layout.cell_list_loading_spinner;
    }

    /**
     * The layout resource identifier to be displayed if there is an error.
     *
     * @return
     */
    protected
    @LayoutRes
    int getErrorResultsLayout() {
        return R.layout.cell_list_load_error;
    }

    /**
     * Generic ViewHolder class.
     */
    private static class ViewHolder extends RecyclerView.ViewHolder {
        private ViewHolder(ViewGroup parent, @LayoutRes int layoutRes) {
            super(LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false));
        }
    }
}
