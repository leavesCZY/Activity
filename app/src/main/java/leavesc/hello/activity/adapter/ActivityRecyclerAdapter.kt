package leavesc.hello.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import leavesc.hello.activity.R

/**
 * Created by：CZY
 * Time：2019/1/22 16:18
 * Desc：
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

        val tvActivityName: TextView = itemView.findViewById(R.id.tv_activityName)

    }

    fun setOnItemClickListener(onItemClickListener: AppRecyclerAdapter.OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

}