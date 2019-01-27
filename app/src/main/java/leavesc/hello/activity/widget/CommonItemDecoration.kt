package leavesc.hello.activity.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View

/**
 * 作者：leavesC
 * 时间：2019/1/16 22:39
 * 描述：
 * GitHub：https://github.com/leavesC
 * Blog：https://www.jianshu.com/u/9df45b87cfdf
 */
class CommonItemDecoration(private var drawable: Drawable, private var orientation: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            outRect.set(0, 0, drawable.intrinsicWidth, 0)
        } else if (orientation == LinearLayoutManager.VERTICAL) {
            outRect.set(0, 0, 0, drawable.intrinsicHeight)
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        if (orientation == LinearLayoutManager.HORIZONTAL) {
            drawVerticalDivider(c, parent)
        } else if (orientation == LinearLayoutManager.VERTICAL) {
            drawHorizontalDivider(c, parent)
        }
    }

    private fun drawVerticalDivider(c: Canvas, parent: RecyclerView) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            //RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            //受 child layout_marginEnd 属性的影响
            //int left = child.getRight() + params.rightMargin;
            //不受 child layout_marginEnd 属性的影响，会直接绘制在 child 右侧
            val left = child.right
            val top = child.top
            val right = left + drawable.intrinsicWidth
            val bottom = child.bottom
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(c)
        }
    }

    private fun drawHorizontalDivider(c: Canvas, parent: RecyclerView) {
        for (i in 0 until parent.childCount) {
            val child = parent.getChildAt(i)
            val left = child.left
            //不受 child layout_marginBottom 属性的影响，会直接绘制在 child 底部
            val top = child.bottom
            val right = child.right
            val bottom = top + drawable.intrinsicHeight
            //会受 child layout_marginBottom 属性的影响
            //RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            //int top = child.getBottom() + params.bottomMargin;
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(c)
        }
    }

}