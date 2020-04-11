package leavesc.hello.activity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import leavesc.hello.activity.R
import leavesc.hello.activity.model.ApplicationLocal

/**
 * 作者：leavesC
 * 时间：2019/1/16 21:08
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class AppRecyclerAdapter(private val appList: MutableList<ApplicationLocal>) :
    RecyclerView.Adapter<AppRecyclerAdapter.AppViewHolder>() {

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    private var onItemClickListener: OnItemClickListener? = null

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        this.onItemClickListener = onItemClickListener
    }

    override fun getItemCount(): Int {
        return if (appList.isNullOrEmpty()) 0 else appList.size
    }

    override fun onBindViewHolder(viewHolder: AppViewHolder, position: Int) {
        viewHolder.iv_appIcon.setImageDrawable(appList[position].icon)
        viewHolder.tv_appName.text = appList[position].name
        viewHolder.tv_appPackageName.text = appList[position].packageName
        viewHolder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(
            LayoutInflater.from(viewGroup.context).inflate(R.layout.item_app, viewGroup, false)
        )
    }

    class AppViewHolder constructor(val view: View) : RecyclerView.ViewHolder(view) {

        val iv_appIcon = view.findViewById<ImageView>(R.id.iv_appIcon)
        val tv_appName = view.findViewById<TextView>(R.id.tv_appName)
        val tv_appPackageName = view.findViewById<TextView>(R.id.tv_appPackageName)

    }

}