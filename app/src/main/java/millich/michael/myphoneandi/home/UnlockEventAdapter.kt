package millich.michael.myphoneandi.home

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import millich.michael.myphoneandi.database.ScreenEvent
import millich.michael.myphoneandi.databinding.ListItemUnlockEventBinding

class UnlockEventAdapter : ListAdapter<ScreenEvent, UnlockEventAdapter.ViewHolder>(UnlockEventDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return  ViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }



    class ViewHolder(val binding: ListItemUnlockEventBinding) : RecyclerView.ViewHolder(binding.root){
        fun bind (item: ScreenEvent){
            binding.unlockEvent =item
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup):ViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListItemUnlockEventBinding.inflate(layoutInflater,parent,false)
                return  ViewHolder(binding)
            }
        }
    }

}

class UnlockEventDiffCallback: DiffUtil.ItemCallback<ScreenEvent>(){
    override fun areItemsTheSame(oldItem: ScreenEvent, newItem: ScreenEvent): Boolean {
        return oldItem.eventId == newItem.eventId
    }

    override fun areContentsTheSame(oldItem: ScreenEvent, newItem: ScreenEvent): Boolean {
        return oldItem == newItem
    }

}