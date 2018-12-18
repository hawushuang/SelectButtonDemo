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

    private var misLeft = true // 当前选中tap
    private var isAnimation = false //是否正在切换

    private val mText = arrayOf("男", "女")//tab 文字内容
    private val colorRed = Color.rgb(0xff, 0x21, 0x10)
    private val colorPurple = Color.rgb(0x88, 0x88, 0xff)
    private var mOnClickListener: OnClickListener? = null

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }

    private fun init() {
        mPaint = Paint()
        mPaint.strokeCap = Paint.Cap.ROUND //圆角
        mPaint.isAntiAlias = true //抗锯齿

        mTextPaint = TextPaint()
        mTextPaint.color = Color.WHITE
        mTextPaint.textSize = 48F
        mTextPaint.typeface = Typeface.SERIF
        mTextPaint.isFakeBoldText = true
        mTextPaint.isAntiAlias = true
        mTextPaint.textAlign = Paint.Align.CENTER

        mRectF = RectF(5F, 5F, 295F, 95F)
    }

    private lateinit var animator: ValueAnimator

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val arrayMeasure = getMeasure(widthMeasureSpec, heightMeasureSpec, 300, 150)
        val width = arrayMeasure[0]
        val height = width / 2
        mTotalLeft = 5F
        mTotalTop = 5F
        mTotalRight = width - 5F
        mTotalBottom = height - 5F
        mTotalHeight = height - 10F

        mRectF.left = mTotalLeft
        mRectF.top = mTotalTop
        mRectF.right = mTotalRight
        mRectF.bottom = mTotalBottom
        mOverlayRadius = (height / 2 - 10).toFloat()
        cx = (height / 2).toFloat()
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
            mOnClickListener?.onClick(position, mText[position])

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
            return true
        }
        return super.onTouchEvent(event)
    }

    private fun drawBgColor(canvas: Canvas?) {
        mPaint.color = Color.CYAN
        mPaint.style = Paint.Style.FILL //实心
        canvas?.drawRoundRect(mRectF, 10000F, 10000F, mPaint)
    }

    private fun drawStroke(canvas: Canvas?) {
        mPaint.strokeWidth = 5F
        mPaint.color = Color.BLACK
        mPaint.style = Paint.Style.STROKE //空心
        canvas?.drawRoundRect(mRectF, 10000F, 10000F, mPaint)
    }

    private fun drawOverlay(canvas: Canvas?) {
        mPaint.color = colorRed
        mPaint.style = Paint.Style.FILL_AND_STROKE
        mPaint.strokeWidth = 1F
        canvas?.drawCircle(cx, (height / 2).toFloat(), mOverlayRadius, mPaint)
    }

    private fun drawText(canvas: Canvas?) {
        canvas!!.drawText(mText[0], (width / 4).toFloat(), mBaseLineY, mTextPaint);
        canvas.drawText(mText[1], (width / 4 * 3).toFloat(), mBaseLineY, mTextPaint);
    }

    /*
*
* 添加tab切换监听
*
* */
    fun setOnClickListener(listener: OnClickListener?) {
        mOnClickListener = listener
    }

    interface OnClickListener {
        fun onClick(position: Int, text: String)
    }
}
