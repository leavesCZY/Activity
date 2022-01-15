package github.leavesczy.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.activity.R
import github.leavesczy.activity.model.ApplicationLocal

/**
 * @Author: leavesCZY
 * @Date: 2019/1/16 21:08
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class AppRecyclerAdapter(private val appList: MutableList<ApplicationLocal>) :
    RecyclerView.Adapter<AppRecyclerAdapter.AppViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return if (appList.isNullOrEmpty()) 0 else appList.size
    }

    override fun onBindViewHolder(viewHolder: AppViewHolder, position: Int) {
        viewHolder.ivAppIcon.setImageDrawable(appList[position].icon)
        viewHolder.tvAppName.text = appList[position].name
        viewHolder.tvAppPackageName.text = appList[position].packageName
        viewHolder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_app, viewGroup, false)
        )
    }

    class AppViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {

        val ivAppIcon = view.findViewById<ImageView>(R.id.ivAppIcon)
        val tvAppName = view.findViewById<TextView>(R.id.tvAppName)
        val tvAppPackageName = view.findViewById<TextView>(R.id.tvAppPackageName)

    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

}