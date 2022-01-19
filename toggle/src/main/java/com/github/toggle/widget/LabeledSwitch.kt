/*
 * Copyright (C) 2022 Vikesh Dass
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.toggle.widget

import android.animation.ValueAnimator
import android.content.Context
import android.content.res.TypedArray
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.animation.AccelerateDecelerateInterpolator
import androidx.core.content.ContextCompat
import com.github.toggle.R
import com.github.toggle.model.ToggleableView

/**
 * Created by vikesh Dass on 20/01/2022
 */
class LabeledSwitch : ToggleableView {
    private var padding = 0
    private var colorTrackOff = 0
    private var colorTrackOn = 0
    private var colorThumbOff = 0
    private var colorThumbOn = 0
    private var outerRadii = 0
    private var thumbRadii = 0
    private lateinit var paint: Paint
    private var startTime: Long = 0
    private lateinit var thumbBounds: RectF
    private lateinit var leftBgArc: RectF
    private lateinit var rightBgArc: RectF
    private lateinit var leftFgArc: RectF
    private lateinit var rightFgArc: RectF

    private lateinit var outlineFgArc: RectF
    private var hasTransparentBackground: Boolean = false

    private var thumbOnCenterX = 0f
    private var thumbOffCenterX = 0f

    /**
     * Simple constructor to use when creating a switch from code.
     *
     * @param context The Context the switch is running in, through which it can
     * access the current theme, resources, etc.
     */
    constructor(context: Context) : super(context) {
        initView()
    }

    /**
     * Constructor that is called when inflating a switch from XML.
     *
     * @param context The Context the switch is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the switch.
     */
    constructor(context: Context?, attrs: AttributeSet) : super(context, attrs) {
        initView()
        initProperties(attrs)
    }

    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute.
     *
     * @param context      The Context the switch is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs        The attributes of the XML tag that is inflating the switch.
     * @param defStyleAttr An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     * the switch. Can be 0 to not look for defaults.
     */
    constructor(context: Context?, attrs: AttributeSet, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    ) {
        initView()
        initProperties(attrs)
    }

    private fun initView() {
        isOn = false
        enabled = true
        paint = Paint()
        paint.isAntiAlias = true
        leftBgArc = RectF()
        rightBgArc = RectF()
        leftFgArc = RectF()
        rightFgArc = RectF()
        thumbBounds = RectF()
        outlineFgArc = RectF()
        colorTrackOff = ContextCompat.getColor(context, R.color.colorTrackDisabled)
        colorTrackOn = ContextCompat.getColor(context, R.color.colorTrackEnabled)
        colorThumbOff = ContextCompat.getColor(context, R.color.colorThumbDisabled)
        colorThumbOn = ContextCompat.getColor(context, R.color.colorThumbEnabled)
    }

    private fun initProperties(attrs: AttributeSet) {
        val attr: TypedArray = context.theme.obtainStyledAttributes(attrs, R.styleable.Toggle, 0, 0)
        for (i in 0 until attr.indexCount) {
            when (attr.getIndex(i)) {
                R.styleable.Toggle_noBackground -> {
                    hasTransparentBackground =
                        attr.getBoolean(R.styleable.Toggle_noBackground, false)
                }
                R.styleable.Toggle_on -> {
                    isOn = attr.getBoolean(R.styleable.Toggle_on, false)
                }

                R.styleable.Toggle_colorThumbDisabled -> {
                    colorThumbOff =
                        attr.getColor(
                            R.styleable.Toggle_colorThumbDisabled,
                            ContextCompat.getColor(context, R.color.colorThumbDisabled)
                        )
                }
                R.styleable.Toggle_colorThumbEnabled -> {
                    colorThumbOn =
                        attr.getColor(
                            R.styleable.Toggle_colorThumbEnabled,
                            ContextCompat.getColor(context, R.color.colorThumbEnabled)
                        )
                }
                R.styleable.Toggle_colorTrackDisabled -> {
                    colorTrackOff =
                        attr.getColor(
                            R.styleable.Toggle_colorTrackDisabled,
                            ContextCompat.getColor(context, R.color.colorTrackDisabled)
                        )
                }
                R.styleable.Toggle_colorTrackEnabled -> {
                    colorTrackOn =
                        attr.getColor(
                            R.styleable.Toggle_colorTrackEnabled,
                            ContextCompat.getColor(context, R.color.colorTrackDisabled)
                        )
                }
                R.styleable.Toggle_android_enabled -> {
                    enabled = attr.getBoolean(R.styleable.Toggle_android_enabled, false)
                }
            }
        }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        //Drawing Switch background here
        drawTrack(canvas, paint)

        //Drawing Switch Thumb here
        drawThumb(canvas, paint)
    }

    //Drawing Switch background i.e the track
    //Read more about anatomy here : https://material.io/components/switches/android#anatomy-and-key-properties
    private fun drawTrack(canvas: Canvas, paint: Paint) {
        //for center part of rectangle
        if (isOn) {
            paint.color = colorTrackOn
        } else {
            paint.color = colorTrackOff
        }
        if (!hasTransparentBackground) {
            //draws left and right part of wedge
            canvas.drawArc(leftBgArc, 90f, 180f, false, paint)
            canvas.drawArc(rightBgArc, 90f, -180f, false, paint)

            //for center rectangle part of switch/view
            canvas.drawRect(
                outerRadii.toFloat(), 0f,
                (width - outerRadii).toFloat(), height.toFloat(), paint
            )

            paint.color = colorTrackOff
            canvas.drawArc(leftFgArc, 90f, 180f, false, paint)
            canvas.drawArc(rightFgArc, 90f, -180f, false, paint)
            canvas.drawRect(
                outerRadii.toFloat(), (padding / 10).toFloat(),
                (width - outerRadii).toFloat(), (height - padding / 10).toFloat(), paint
            )
        }

        //For drawing for turned on state
        var alpha =
            ((thumbBounds.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX) * 255).toInt()
        alpha = if (alpha < 0) 0 else alpha.coerceAtMost(255)
        val onColor: Int = if (isEnabled) {
            Color.argb(
                alpha,
                Color.red(colorTrackOn),
                Color.green(colorTrackOn),
                Color.blue(colorTrackOn)
            )
        } else {
            Color.argb(
                alpha, Color.red(colorTrackOff),
                Color.green(colorTrackOff), Color.blue(colorTrackOff)
            )
        }
        paint.color = onColor
        if (!hasTransparentBackground) {
            //draws left and right part of wedge (when turned on)
            canvas.drawArc(leftBgArc, 90f, 180f, false, paint)
            canvas.drawArc(rightBgArc, 90f, -180f, false, paint)

            //draws background when switch is turned on
            canvas.drawRect(
                outerRadii.toFloat(), 0f,
                (width - outerRadii).toFloat(), height.toFloat(), paint
            )
        }

        //For drawing for turned off state
        alpha =
            ((thumbOnCenterX - thumbBounds.centerX()) / (thumbOnCenterX - thumbOffCenterX) * 255).toInt()
        alpha = if (alpha < 0) 0 else alpha.coerceAtMost(255)
        val offColor =
            Color.argb(
                alpha,
                Color.red(colorTrackOff),
                Color.green(colorTrackOff),
                Color.blue(colorTrackOff)
            )
        paint.color = offColor

        val strokePaint = Paint().apply {
            color = colorTrackOff
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }

        if (!hasTransparentBackground) {
            //draws left and right part of wedge (when turned off)
            canvas.drawArc(leftFgArc, 90f, 180f, false, paint)
            canvas.drawArc(rightFgArc, 90f, -180f, false, paint)

            //draws background when switch is turned off
            canvas.drawRect(
                outerRadii.toFloat(), (padding / 10).toFloat(),
                (width - outerRadii).toFloat(), (height - padding / 10).toFloat(), paint
            )
        } else {
            canvas.drawRoundRect(
                outlineFgArc, outerRadii.toFloat(),
                outerRadii.toFloat(), strokePaint
            )
        }
    }

    private fun drawThumb(canvas: Canvas, paint: Paint) {
        var alpha =
            ((thumbBounds.centerX() - thumbOffCenterX) / (thumbOnCenterX - thumbOffCenterX) * 255).toInt()
        alpha = if (alpha < 0) 0 else alpha.coerceAtMost(255)
        val offColor =
            Color.argb(
                alpha,
                Color.red(colorThumbOff),
                Color.green(colorThumbOff),
                Color.blue(colorThumbOff)
            )
        paint.color = offColor
        canvas.drawCircle(
            thumbBounds.centerX(), thumbBounds.centerY(),
            thumbRadii.toFloat(), paint
        )
        alpha =
            ((thumbOnCenterX - thumbBounds.centerX()) / (thumbOnCenterX - thumbOffCenterX) * 255).toInt()
        alpha = if (alpha < 0) 0 else alpha.coerceAtMost(255)
        val onColor: Int = if (isEnabled) {
            Color.argb(
                alpha,
                Color.red(colorThumbOn),
                Color.green(colorThumbOn),
                Color.blue(colorThumbOn)
            )
        } else {
            Color.argb(
                alpha, Color.red(colorThumbOff),
                Color.green(colorThumbOff), Color.blue(colorThumbOff)
            )
        }
        paint.color = onColor
        canvas.drawCircle(
            thumbBounds.centerX(), thumbBounds.centerY(),
            thumbRadii.toFloat(), paint
        )
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = resources.getDimensionPixelSize(R.dimen.default_width)
        val desiredHeight = resources.getDimensionPixelSize(R.dimen.default_height)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        width = when (widthMode) {
            MeasureSpec.EXACTLY -> widthSize
            MeasureSpec.AT_MOST -> desiredWidth.coerceAtMost(widthSize)
            else -> desiredWidth
        }

        height = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> desiredHeight.coerceAtMost(heightSize)
            else -> desiredHeight
        }

        setMeasuredDimension(width, height)
        outerRadii = width.coerceAtMost(height) ushr 1
        thumbRadii = (width.coerceAtMost(height) / 2.88f).toInt()
        padding = height - thumbRadii ushr 1
        thumbBounds[(width - padding - thumbRadii).toFloat(), padding.toFloat(), (width - padding).toFloat()] =
            (height - padding).toFloat()
        thumbOnCenterX = thumbBounds.centerX()
        thumbBounds[padding.toFloat(), padding.toFloat(), (padding + thumbRadii).toFloat()] =
            (height - padding).toFloat()
        thumbOffCenterX = thumbBounds.centerX()
        if (isOn) {
            thumbBounds[(width - padding - thumbRadii).toFloat(), padding.toFloat(), (width - padding).toFloat()] =
                (height - padding).toFloat()
        } else {
            thumbBounds[padding.toFloat(), padding.toFloat(), (padding + thumbRadii).toFloat()] =
                (height - padding).toFloat()
        }
        leftBgArc[0f, 0f, (outerRadii shl 1).toFloat()] = height.toFloat()
        rightBgArc[(width - (outerRadii shl 1)).toFloat(), 0f, width.toFloat()] = height.toFloat()
        leftFgArc[(padding / 10).toFloat(), (padding / 10).toFloat(), ((outerRadii shl 1) - padding / 10).toFloat()] =
            (height - padding / 10).toFloat()
        rightFgArc[(width - (outerRadii shl 1) + padding / 10).toFloat(), (padding / 10).toFloat(), (width - padding / 10).toFloat()] =
            (height - padding / 10).toFloat()

        outlineFgArc[(padding / 10).toFloat(), (padding / 10).toFloat(), (width - padding / 10).toFloat()] =
            (height - padding / 10).toFloat()
    }

    /**
     * Call this view's OnClickListener, if it is defined.  Performs all normal
     * actions associated with clicking: reporting accessibility event, playing
     * a sound, etc.
     *
     * @return True there was an assigned OnClickListener that was called, false
     * otherwise is returned.
     */
    override fun performClick(): Boolean {
        super.performClick()
        if (isOn) {
            val switchColor =
                ValueAnimator.ofFloat((width - padding - thumbRadii).toFloat(), padding.toFloat())
            switchColor.addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Float
                thumbBounds[value, thumbBounds.top, value + thumbRadii] = thumbBounds.bottom
                invalidate()
            }
            switchColor.interpolator = AccelerateDecelerateInterpolator()
            switchColor.duration = 250
            switchColor.start()
        } else {
            val switchColor =
                ValueAnimator.ofFloat(padding.toFloat(), (width - padding - thumbRadii).toFloat())
            switchColor.addUpdateListener { animation: ValueAnimator ->
                val value = animation.animatedValue as Float
                thumbBounds[value, thumbBounds.top, value + thumbRadii] = thumbBounds.bottom
                invalidate()
            }
            switchColor.interpolator = AccelerateDecelerateInterpolator()
            switchColor.duration = 250
            switchColor.start()
        }
        isOn = !isOn
        onToggledListener?.onSwitched(this, isOn)
        return true
    }

    /**
     * Method to handle touch screen motion events.
     *
     * @param event The motion event.
     * @return True if the event was handled, false otherwise.
     */
    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (isEnabled) {
            val x = event.x
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    startTime = System.currentTimeMillis()
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    if (x - (thumbRadii ushr 1) > padding && x + (thumbRadii ushr 1) < width - padding) {
                        thumbBounds[x - (thumbRadii ushr 1), thumbBounds.top, x + (thumbRadii ushr 1)] =
                            thumbBounds.bottom
                        invalidate()
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val endTime = System.currentTimeMillis()
                    val span = endTime - startTime
                    if (span < 200) {
                        performClick()
                    } else {
                        if (x >= width ushr 1) {
                            val switchColor = ValueAnimator.ofFloat(
                                if (x > (width - padding - thumbRadii))
                                    (width - padding - thumbRadii).toFloat()
                                else x, (width - padding - thumbRadii).toFloat()
                            )
                            switchColor.addUpdateListener { animation: ValueAnimator ->
                                val value = animation.animatedValue as Float
                                thumbBounds[value, thumbBounds.top, value + thumbRadii] =
                                    thumbBounds.bottom
                                invalidate()
                            }
                            switchColor.interpolator = AccelerateDecelerateInterpolator()
                            switchColor.duration = 250
                            switchColor.start()
                            isOn = true
                        } else {
                            val switchColor = ValueAnimator.ofFloat(
                                if (x < padding) padding.toFloat() else x,
                                padding.toFloat()
                            )
                            switchColor.addUpdateListener { animation: ValueAnimator ->
                                val value = animation.animatedValue as Float
                                thumbBounds[value, thumbBounds.top, value + thumbRadii] =
                                    thumbBounds.bottom
                                invalidate()
                            }
                            switchColor.interpolator = AccelerateDecelerateInterpolator()
                            switchColor.duration = 250
                            switchColor.start()
                            isOn = false
                        }
                        if (onToggledListener != null) {
                            onToggledListener.onSwitched(this, isOn)
                        }
                    }
                    invalidate()
                    true
                }
                else -> super.onTouchEvent(event)
            }
        } else {
            false
        }
    }

    /**
     *
     * Changes the boolean state of this Switch.
     *
     * @param on true to turn switch on, false to turn it off.
     */
    override fun setOn(on: Boolean) {
        super.setOn(on)
        if (isOn) {
            thumbBounds[(width - padding - thumbRadii).toFloat(), padding.toFloat(), (width - padding).toFloat()] =
                (height - padding).toFloat()
        } else {
            thumbBounds[padding.toFloat(), padding.toFloat(), (padding + thumbRadii).toFloat()] =
                (height - padding).toFloat()
        }
        invalidate()
    }

    /**
     *
     * Returns the color value for colorOn.
     *
     * @return color value for thumb in on state.
     */
    fun getColorThumbOn(): Int {
        return colorThumbOn
    }

    /**
     *
     * Changes the on color value of this Switch.
     *
     * @param ColorThumbOn color value for thumb in on state.
     */
    fun setColorThumbOn(colorOn: Int) {
        this.colorThumbOn = colorOn
        invalidate()
    }

    /**
     *
     * Returns the color value for track colorTrackDisabled.
     *
     * @return color value for track in on state and background in off state.
     */
    fun getColorTrackOff(): Int {
        return colorTrackOff
    }

    /**
     *
     * Returns the color value for colorThumbDisabled.
     *
     * @return color value for thumb in off state and background in off state.
     */
    fun getColorThumbOff(): Int {
        return colorThumbOff
    }

    /**
     *
     * Changes the off color value of this Switch.
     *
     * @param colorOff color value for thumb in off state.
     */
    fun setColorThumbOff(colorOff: Int) {
        this.colorThumbOff = colorOff
        invalidate()
    }

    /**
     *
     * Changes the off color value of this Switch.
     *
     * @param colorOff color value for label and thumb in on state and background in off state.
     */
    fun setColorTrackOff(colorOff: Int) {
        this.colorTrackOff = colorOff
        invalidate()
    }
}