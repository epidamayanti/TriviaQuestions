package id.mncplay.triviaquestions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.extensions.LayoutContainer
import id.mncplay.triviaquestions.R.layout.item_history
import id.mncplay.triviaquestions.models.DataHistory
import kotlinx.android.synthetic.main.item_history.view.*

class HistoryAdapter(private val context: Context, private val items: List<DataHistory>, private val listener: (DataHistory) -> Unit)
    : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(item_history, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bindItem(items: DataHistory, listener: (DataHistory) -> Unit) {
            containerView.item_category.text = items.category
            containerView.item_mode.text = items.mode
            containerView.date.text = items.date
            containerView.setOnClickListener { listener(items) }
        }
    }

}

