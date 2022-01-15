package github.leavesczy.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.activity.R

/**
 * @Author: leavesCZY
 * @Date: 2019/1/22 16:18
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ActivityRecyclerAdapter : RecyclerView.Adapter<ActivityRecyclerAdapter.ActivityHolder>() {

    private var onItemClickListener: AppRecyclerAdapter.OnItemClickListener? = null

    lateinit var activityList: MutableList<String>

    override fun onBindViewHolder(holder: ActivityHolder, position: Int) {
        holder.tvActivityName.text = activityList[position]
        holder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun getItemCount() = activityList.size

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ActivityHolder {
        return ActivityHolder(
            LayoutInflater.from(viewGroup.context).inflate(
                R.layout.item_app_activity,
                viewGroup,
                false
            )
        )
    }

    class ActivityHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val tvActivityName: TextView = itemView.findViewById(R.id.tvActivityName)

    }

    fun setOnItemClickListener(onItemClickListener: AppRecyclerAdapter.OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

}