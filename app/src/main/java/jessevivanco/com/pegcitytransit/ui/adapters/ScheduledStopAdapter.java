package jessevivanco.com.pegcitytransit.ui.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.List;

import jessevivanco.com.pegcitytransit.ui.presenters.BusStopSchedulePresenter;
import jessevivanco.com.pegcitytransit.ui.view_holders.ScheduledStopViewHolder;
import jessevivanco.com.pegcitytransit.ui.view_models.ScheduledStopViewModel;

// TODO generify this!
public class ScheduledStopAdapter extends RecyclerView.Adapter<ScheduledStopViewHolder> {

    private BusStopSchedulePresenter schedulePresenter;
    private List<ScheduledStopViewModel> scheduledStops;

    public ScheduledStopAdapter(BusStopSchedulePresenter schedulePresenter) {
        this.schedulePresenter = schedulePresenter;
    }

    @Override
    public ScheduledStopViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ScheduledStopViewHolder(parent);
    }

    @Override
    public void onBindViewHolder(ScheduledStopViewHolder holder, int position) {
        holder.bind(scheduledStops.get(position));
    }

    @Override
    public int getItemCount() {
        return scheduledStops != null ? scheduledStops.size() : 0;
    }

    public BusStopSchedulePresenter getSchedulePresenter() {
        return schedulePresenter;
    }

    public void setScheduledStops(List<ScheduledStopViewModel> scheduledStops) {
        this.scheduledStops = scheduledStops;
        notifyDataSetChanged();
    }

    public List<ScheduledStopViewModel> getScheduledStops() {
        return scheduledStops;
    }
}
