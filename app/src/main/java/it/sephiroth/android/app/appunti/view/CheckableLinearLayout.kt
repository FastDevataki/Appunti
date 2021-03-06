package it.sephiroth.android.app.appunti.view

import android.content.Context
import android.util.AttributeSet
import android.widget.Checkable
import android.widget.LinearLayout

class CheckableLinearLayout(context: Context?, attrs: AttributeSet?) : LinearLayout(context, attrs), Checkable {

    private var checked: Boolean = false

    override fun isChecked(): Boolean {
        return checked
    }

    override fun setChecked(value: Boolean) {
        checked = value
        refreshDrawableState()
    }

    override fun toggle() {
        checked = !checked
    }

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (isChecked) {
            mergeDrawableStates(
                drawableState,
                CHECKED_STATE_SET
            )
        }
        return drawableState
    }

    companion object {
        private val CHECKED_STATE_SET = intArrayOf(android.R.attr.state_checked)
    }


}