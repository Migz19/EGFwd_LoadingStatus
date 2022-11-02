package com.example.loadapp

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import androidx.core.content.res.ResourcesCompat
import kotlin.math.min
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defstyleAtrr: Int = 0
) : View(context, attrs, defstyleAtrr) {
    private var widthSize = 0
    private var heightSize = 0
    private val valueAnimator = ValueAnimator.ofInt(0, 360).setDuration(3000)
    private var radius = 0.0f
    private var loadingrange=0
    private val oldBGColor = ResourcesCompat.getColor(resources, R.color.colorPrimaryDark, null)
    private val newBGColor = ResourcesCompat.getColor(resources, R.color.colorPrimary, null)
    private val textColor = ResourcesCompat.getColor(resources, R.color.white, null)
    private var buttonText: String = resources.getString(R.string.button_loading)
    private val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textAlign = Paint.Align.CENTER
        textSize = 40.0f
        typeface = Typeface.create("Downloading", Typeface.BOLD)
        color = textColor
    }
     var buttonState: ButtonState by Delegates.observable(ButtonState.Completed) { property, oldValue, newValue ->
        when (newValue) {
            ButtonState.Loading -> {
                valueAnimator.start()
                buttonText = resources.getString(R.string.button_loading)
            }
            ButtonState.Completed -> {
                valueAnimator.cancel()
                buttonText=resources.getString(R.string. button_completed)
                loadingrange=0
            }
            else -> {
                valueAnimator.cancel()
                buttonText=context.getString(R.string.button_name)
                loadingrange=0
            }
        }
         invalidate()
    }
    private lateinit var extraCanvas: Canvas
    private lateinit var extraBitmap: Bitmap

    init {
        isClickable = true
        buttonState=ButtonState.Completed
        buttonText = resources.getString(R.string.button_name)
        valueAnimator.apply {
            addUpdateListener { animator ->
                loadingrange = animator.animatedValue as Int
                invalidate()
            }
        }
    }


    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        paint.color= oldBGColor
        canvas?.drawArc(widthSize-100f, 20f,widthSize-100f,140f,loadingrange.toFloat(),0f, true, paint)
        paint.color=oldBGColor
        canvas?.drawRect(0f, 0f, widthSize.toFloat(), heightSize.toFloat(), paint)
       paint.color=newBGColor
        canvas?.drawRect(0f, 0f, widthSize * loadingrange/360f, heightSize.toFloat(), paint)
        paint.color = textColor
        canvas?.drawText(buttonText, (width / 2).toFloat(), ((height + 30) / 2).toFloat(),
            paint)
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val minW: Int = paddingLeft + paddingRight + suggestedMinimumHeight
        val wid: Int = resolveSizeAndState(minW, widthMeasureSpec, 1)
        val height: Int = resolveSizeAndState(
            MeasureSpec.getSize(wid), heightMeasureSpec, 0)
        widthSize = wid
        heightSize = height
        setMeasuredDimension(wid, height)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        radius = (min(width, height) / 30.0).toFloat()
        if (::extraBitmap.isInitialized)
            extraBitmap.recycle()
        extraBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
        extraCanvas = Canvas(extraBitmap)
        extraCanvas.drawColor(oldBGColor)
    }

}