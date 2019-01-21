package leavesc.hello.activity.adapter

import android.databinding.DataBindingUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import leavesc.hello.activity.R
import leavesc.hello.activity.databinding.ItemAppBinding
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
        viewHolder.itemAppBinding.ivAppIcon.setImageDrawable(appList[position].icon)
        viewHolder.itemAppBinding.tvAppName.text = appList[position].name
        viewHolder.itemAppBinding.tvAppPackageName.text = appList[position].packageName
        viewHolder.itemView.setOnClickListener {
            onItemClickListener?.onItemClick(position)
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): AppViewHolder {
        return AppViewHolder(
            DataBindingUtil.inflate(
                LayoutInflater.from(viewGroup.context),
                R.layout.item_app,
                viewGroup,
                false
            )
        )
    }

    class AppViewHolder constructor(val itemAppBinding: ItemAppBinding) : RecyclerView.ViewHolder(itemAppBinding.root)

}