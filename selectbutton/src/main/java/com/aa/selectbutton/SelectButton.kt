package com.aa.selectbutton

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.text.TextPaint
import android.view.MotionEvent
import com.aa.cdemo.utils.getMeasure


class SelectButton : View {
    private lateinit var mPaint: Paint//主要画笔
    private lateinit var mTextPaint: TextPaint//文字画笔
    private lateinit var mRectF: RectF//圆角边框
    private var mTotalLeft = 0F//view的left
    private var mTotalTop = 0F//view的top
    private var mTotalRight = 0F//view的right
    private var mTotalBottom = 0F//view的bottom
    private var mTotalHeight = 0F//bottom－top
    private var mBaseLineY = 0F //文字剧中线条
    private var mOverlayRadius = 0F//滑块半径
    private var cx = 0F //滑块位置

    private var bg_color = Color.CYAN
    private var stroke_color = Color.BLACK
    private var slide_color = Color.RED
    private var text_color = Color.WHITE
    private var stroke_width = 5F

    private var misLeft = true // 当前选中tap
    private var isAnimation = false //是否正在切换

    private var mText = arrayOf("男", "女")//tab 文字内容
    private var mOnClickListener: OnClickListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.SelectButton)
        bg_color = typedArray.getColor(R.styleable.SelectButton_bg_color, Color.CYAN)
        stroke_color = typedArray.getColor(R.styleable.SelectButton_stroke_color, Color.BLACK)
        slide_color = typedArray.getColor(R.styleable.SelectButton_slide_color, Color.RED)
        text_color = typedArray.getColor(R.styleable.SelectButton_text_color, Color.WHITE)
        stroke_width = typedArray.getFloat(R.styleable.SelectButton_stroke_width, 5F)
        typedArray.recycle()
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.strokeCap = Paint.Cap.ROUND //圆角
        mPaint.isAntiAlias = true //抗锯齿

        mTextPaint = TextPaint()
        mTextPaint.color = text_color
        mTextPaint.typeface = Typeface.SERIF
        mTextPaint.isFakeBoldText = true
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER

        mRectF = RectF()
    }

    private lateinit var animator: ValueAnimator

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val arrayMeasure = getMeasure(widthMeasureSpec, heightMeasureSpec, 300, 150)
        val width = arrayMeasure[0]
        val height = width / 2
        mTotalLeft = stroke_width / 2
        mTotalTop = stroke_width / 2
        mTotalRight = width - stroke_width / 2
        mTotalBottom = height - stroke_width / 2
        mTotalHeight = height - stroke_width

        mRectF.left = mTotalLeft
        mRectF.top = mTotalTop
        mRectF.right = mTotalRight
        mRectF.bottom = mTotalBottom
        mOverlayRadius = (height / 2 - stroke_width)

        mTextPaint.textSize = width / 4.5F
        cx = if (misLeft) {
            (height / 2).toFloat()
        } else {
            (height * 3 / 2).toFloat()
        }
        val fontMetrics = mTextPaint.fontMetrics
        val top = fontMetrics.top//为基线到字体上边框的距离,即上图中的top
        val bottom = fontMetrics.bottom//为基线到字体下边框的距离,即上图中的bottom
        mBaseLineY = height / 2 - top / 2 - bottom / 2

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        drawBgColor(canvas)
        drawStroke(canvas)
        drawOverlay(canvas)
        drawText(canvas)
        setBackgroundColor(Color.TRANSPARENT)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        if (event!!.action == MotionEvent.ACTION_DOWN) {
            val position = if (event.x > width / 2) 1 else 0
            val isLeft = event.x < width / 2
            when (position) {
                1 -> animator = ValueAnimator.ofFloat((height / 2).toFloat(), (height * 3 / 2).toFloat())
                0 -> animator = ValueAnimator.ofFloat((height * 3 / 2).toFloat(), (height / 2).toFloat())
            }

            animator.duration = 300
            animator.addUpdateListener { animation ->
                cx = animation?.animatedValue as Float
                invalidate()

            }
            animator.addListener(object : Animator.AnimatorListener {
                override fun onAnimationStart(animation: Animator) {
                    isAnimation = true
                }

                override fun onAnimationEnd(animation: Animator) {
                    misLeft = !misLeft
                    isAnimation = false
                }

                override fun onAnimationCancel(animation: Animator) {
                    isAnimation = false
                }

                override fun onAnimationRepeat(animation: Animator) {

                }
            })

            if (misLeft == isLeft || isAnimation) {
                return true
            }
            animator.start()
            mOnClickListener?.onClick(position, mText[position])
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun drawBgColor(canvas: Canvas?) {
        mPaint.color = bg_color
        mPaint.style = Paint.Style.FILL //实心
        canvas?.drawRoundRect(mRectF, 10000F, 10000F, mPaint)
    }

    private fun drawStroke(canvas: Canvas?) {
        mPaint.strokeWidth = stroke_width
        mPaint.color = stroke_color
        mPaint.style = Paint.Style.STROKE //空心
        canvas?.drawRoundRect(mRectF, 10000F, 10000F, mPaint)
    }

    private fun drawOverlay(canvas: Canvas?) {
        mPaint.color = slide_color
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.strokeWidth = 1F
        canvas?.drawCircle(cx, (height / 2).toFloat(), mOverlayRadius, mPaint)
    }

    private fun drawText(canvas: Canvas?) {
        canvas?.drawText(mText[0], (width / 4).toFloat(), mBaseLineY, mTextPaint)
        canvas?.drawText(mText[1], (width / 4 * 3).toFloat(), mBaseLineY, mTextPaint)
    }

    fun setOnClickListener(listener: OnClickListener?) {
        mOnClickListener = listener
    }

    fun setTexts(texts: Array<String>) {
        mText = texts
    }

    fun setIsLeft(isLeft: Boolean) {
        misLeft = isLeft
    }

    interface OnClickListener {
        fun onClick(position: Int, text: String)
    }
}
