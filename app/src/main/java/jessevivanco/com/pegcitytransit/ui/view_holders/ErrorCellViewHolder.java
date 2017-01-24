package jessevivanco.com.pegcitytransit.ui.view_holders;

import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import jessevivanco.com.pegcitytransit.R;
import jessevivanco.com.pegcitytransit.databinding.CellListLoadErrorBinding;

public class ErrorCellViewHolder extends RecyclerView.ViewHolder {

    private CellListLoadErrorBinding binding;

    public ErrorCellViewHolder(ViewGroup parent) {
        super(LayoutInflater.from(parent.getContext()).inflate(R.layout.cell_list_load_error, parent, false));
        this.binding = DataBindingUtil.bind(itemView);
    }

    public void bind(String errorMessage, ErrorCellViewHolder.OnRetryClickListener onRetryClickListener) {
        binding.setMessage(errorMessage);
        binding.setRetryHandler(onRetryClickListener);
    }

    /**
     * Listener for when the user clicks on the "try again" (or equivalent) button.
     */
    public interface OnRetryClickListener {

        /**
         * The user tapped on the "try again" (or equivalent) button. You should invoke a refresh of the list at this
         * point.
         */
        void onRetryLoad();
    }
}
