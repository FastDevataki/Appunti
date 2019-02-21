package it.sephiroth.android.app.appunti.graphics

import android.animation.ArgbEvaluator
import android.content.res.ColorStateList
import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.*
import android.graphics.drawable.AnimatedStateListDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.RippleDrawable
import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.Shape
import android.util.AttributeSet
import android.util.TypedValue
import androidx.annotation.ColorInt
import it.sephiroth.android.app.appunti.R
import it.sephiroth.android.app.appunti.utils.DrawableUtils
import org.xmlpull.v1.XmlPullParser
import timber.log.Timber
import kotlin.math.roundToInt


class MaterialBackgroundDrawable {

    class Builder {
        private var ripple: RippleDrawable? = null
        private val selector = AnimatedStateListDrawable()
        private val evaluator = ArgbEvaluator()
        private val states = hashMapOf<Int, Int>()


        fun addState(stateSet: IntArray, drawable: ShapeDrawable): Builder {
            selector.addState(stateSet, drawable)
            return this
        }

        fun addPressed(drawable: MaterialShapeDrawable.Builder): Builder {
            return addState(intArrayOf(android.R.attr.state_pressed), drawable.build())
        }

        fun addChecked(drawable: MaterialShapeDrawable.Builder): Builder {
            return addState(intArrayOf(android.R.attr.state_checked), drawable.build())
        }

        fun addSelected(drawable: MaterialShapeDrawable.Builder): Builder {
            return addState(intArrayOf(android.R.attr.state_selected), drawable.build())
        }

        fun addNormal(drawable: MaterialShapeDrawable.Builder): Builder {
            return addState(intArrayOf(), drawable.build())
        }

        fun ripple(@ColorInt color: Int, drawable: MaterialShapeDrawable.Builder): Builder {
            ripple = RippleDrawable(
                ColorStateList.valueOf(color), selector, drawable.build()
            )
            return this
        }

        fun build(): Drawable {
            ripple?.let { return it } ?: run { return selector }
        }

        companion object {
            const val FRAME_DURATION = 16
        }
    }
}

class MaterialShapeDrawable(s: Shape?) : ShapeDrawable(s) {

    constructor() : this(null)

    override fun onBoundsChange(bounds: Rect?) {
        if (paint.style != Paint.Style.FILL && paint.strokeWidth > 0) {
            bounds?.inset(paint.strokeWidth.roundToInt() / 2, paint.strokeWidth.roundToInt() / 2)
        }
        super.onBoundsChange(bounds)
    }

    class Builder(type: MaterialShape.Type) {
        val shape: MaterialShape = MaterialShape(type)
        val drawable = MaterialShapeDrawable(shape)

        fun style(style: Paint.Style): Builder {
            drawable.paint.style = style
            return this
        }

        fun color(color: Int): Builder {
            drawable.paint.color = color
            return this
        }

        fun tint(color: Int): Builder {
            drawable.setTint(color)
            return this
        }

        fun strokeWidth(width: Float): Builder {
            drawable.paint.strokeWidth = width
            return this
        }

        fun build() = drawable

    }
}

class MaterialShape(private val type: Type) : Shape() {

    constructor() : this(Type.START)

    private val path = Path()
    private val bounds = RectF()

    private fun invalidatePath() {
        path.rewind()

        when (type) {
            Type.ALL -> invalidatePathAll()
            Type.START -> invalidatePathStart()
            Type.END -> invalidatePathEnd()
        }
    }

    private fun invalidatePathStart() {
        path.moveTo(0f, 0f)
        path.lineTo(bounds.right - bounds.height(), 0f)
        path.arcTo(RectF(bounds.right - bounds.height(), 0f, bounds.right, bounds.height()), 270f, 180f)
        path.lineTo(0f, bounds.height())
        path.close()
    }

    private fun invalidatePathAll() {
        path.moveTo(bounds.height(), 0f)
        path.lineTo(bounds.right - bounds.height(), 0f)
        path.arcTo(RectF(bounds.right - bounds.height(), 0f, bounds.right, bounds.height()), 270f, 180f)
        path.lineTo(bounds.height(), bounds.height())
        path.arcTo(RectF(0f, 0f, bounds.height(), bounds.height()), 90f, 180f)
        path.close()
    }

    private fun invalidatePathEnd() {
        path.moveTo(bounds.height(), 0f)
        path.lineTo(bounds.right, 0f)
        path.lineTo(bounds.right, bounds.height())
        path.lineTo(bounds.height(), bounds.height())
        path.arcTo(RectF(0f, 0f, bounds.height(), bounds.height()), 90f, 180f)
        path.close()
    }

    override fun onResize(width: Float, height: Float) {
        super.onResize(width, height)
        bounds.set(0f, 0f, width, height)
        invalidatePath()

    }

    override fun draw(canvas: Canvas, paint: Paint) {
        canvas.drawPath(path, paint)
    }

    enum class Type {
        ALL, START, END
    }

}