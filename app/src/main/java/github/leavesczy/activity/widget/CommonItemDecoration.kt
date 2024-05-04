package github.leavesczy.activity.widget

import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.drawable.Drawable
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

/**
 * @Author: leavesCZY
 * @Date: 2019/1/16 22:39
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class CommonItemDecoration(private var drawable: Drawable, private var orientation: Int) :
    RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
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
            val top = child.bottom
            val right = child.right
            val bottom = top + drawable.intrinsicHeight
            drawable.setBounds(left, top, right, bottom)
            drawable.draw(c)
        }
    }

}