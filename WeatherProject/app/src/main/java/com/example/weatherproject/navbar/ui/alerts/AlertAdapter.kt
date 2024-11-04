import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.weatherproject.databinding.ItemEventBinding
import com.example.weatherproject.model.pojos.EventAlerts

class AlertAdapter : ListAdapter<EventAlerts, AlertAdapter.EventViewHolder>(
    EventDiffCallback()
) {

    class EventViewHolder(val binding: ItemEventBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(event: EventAlerts) {

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EventViewHolder {
        val binding = ItemEventBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EventViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EventViewHolder, position: Int) {
        val event = getItem(position)
        holder.bind(event)
        holder.binding.Time.text=event.date.toString()
        holder.binding.country.text=event.title.toString()

    }
}