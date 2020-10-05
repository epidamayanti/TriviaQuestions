package id.mncplay.triviaquestions.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.mncplay.triviaquestions.models.DataCategory
import kotlinx.android.extensions.LayoutContainer
import id.mncplay.triviaquestions.R.layout.item_category
import kotlinx.android.synthetic.main.item_category.view.*

class CategoryAdapter(private val context: Context, private val items: List<DataCategory>, private val listener: (DataCategory) -> Unit)
    : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(item_category, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        fun bindItem(items: DataCategory, listener: (DataCategory) -> Unit) {
            containerView.item_title.text = items.name
            containerView.setOnClickListener { listener(items) }
        }
    }

}

