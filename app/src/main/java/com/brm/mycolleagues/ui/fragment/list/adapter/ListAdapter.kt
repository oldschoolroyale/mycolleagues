package com.brm.mycolleagues.ui.fragment.list.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.RecyclerView
import com.brm.mycolleagues.databinding.WorkerListItemBinding
import com.brm.mycolleagues.ui.fragment.list.model.PersonModel

class ListAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {
    private var dataList = emptyList<PersonModel>()
    private var filteredDataList = emptyList<PersonModel>()

    fun newList(list: List<PersonModel>){
        this.dataList = list
        notifyDataSetChanged()
    }
    override fun getFilter(): Filter {
        return object : Filter(){
            override fun performFiltering(charSequence: CharSequence?): FilterResults {
                val charString: String = charSequence.toString()
                if (filteredDataList.isEmpty())
                    filteredDataList = dataList
                dataList = if (charString.isEmpty()) {
                    filteredDataList
                } else {
                    val filteredList: MutableList<PersonModel> = ArrayList()
                    filteredList.clear()
                    for (model in filteredDataList) {
                        if (model.name.toLowerCase().contains(charString.toLowerCase())) {
                            filteredList.add(model)
                        }
                    }
                    filteredList
                }

                val filterResults = FilterResults()
                filterResults.values = dataList
                return filterResults
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                notifyDataSetChanged()
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ListViewHolder.from(parent)
    }

    override fun getItemCount(): Int {
       return  dataList.count()
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ListViewHolder){
            holder.bind(dataList[position])
        }
    }

    private class ListViewHolder(private val binding: WorkerListItemBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(personModel: PersonModel){
            binding.model = personModel
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): ListViewHolder{
                val bind = WorkerListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return ListViewHolder(bind)
            }
        }
    }
}