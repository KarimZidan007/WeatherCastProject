import androidx.recyclerview.widget.DiffUtil
import com.example.weatherproject.model.pojos.EventAlerts

class EventDiffCallback : DiffUtil.ItemCallback<EventAlerts>() {
    override fun areItemsTheSame(oldItem: EventAlerts, newItem: EventAlerts): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: EventAlerts, newItem: EventAlerts): Boolean {
        return oldItem == newItem
    }
}