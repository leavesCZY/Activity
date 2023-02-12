package github.leavesczy.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import github.leavesczy.activity.R
import github.leavesczy.activity.model.AppInfo

/**
 * @Author: leavesCZY
 * @Date: 2019/1/16 21:08
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class AppRecyclerAdapter(private val appInfoList: List<AppInfo>) :
    RecyclerView.Adapter<AppRecyclerAdapter.AppViewHolder>() {

    class AppViewHolder constructor(view: View) : RecyclerView.ViewHolder(view) {
        val ivAppIcon: ImageView = view.findViewById(R.id.ivAppIcon)
        val tvAppName: TextView = view.findViewById(R.id.tvAppName)
        val tvAppPackageName: TextView = view.findViewById(R.id.tvAppPackageName)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    override fun getItemCount(): Int {
        return appInfoList.size
    }

    override fun onBindViewHolder(viewHolder: AppViewHolder, position: Int) {
        viewHolder.ivAppIcon.setImageDrawable(appInfoList[position].icon)
        viewHolder.tvAppName.text = appInfoList[position].name
        viewHolder.tvAppPackageName.text = appInfoList[position].packageName
        viewHolder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_app, viewGroup, false)
        )
    }

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

}