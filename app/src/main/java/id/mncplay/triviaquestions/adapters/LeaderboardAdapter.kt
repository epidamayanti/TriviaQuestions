package id.mncplay.triviaquestions.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.mncplay.triviaquestions.R.layout.*
import kotlinx.android.extensions.LayoutContainer
import id.mncplay.triviaquestions.models.DataUser
import kotlinx.android.synthetic.main.lyt_leaderboard.view.*

class LeaderboardAdapter(private val context: Context, private val items: List<DataUser>, private val listener: (DataUser) -> Unit)
    : RecyclerView.Adapter<LeaderboardAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(LayoutInflater.from(context).inflate(lyt_leaderboard, parent, false))


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindItem(items[position], listener)
    }

    override fun getItemCount(): Int = items.size


    class ViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
        LayoutContainer {

        @SuppressLint("SetTextI18n")
        fun bindItem(items: DataUser, listener: (DataUser) -> Unit) {
            containerView.tvName.text = items.name
            containerView.tvScore.text = ""+items.score
            containerView.tvRank.text = ""+(position+1)
            containerView.setOnClickListener { listener(items) }
        }
    }

}

