package com.aa.cdemo.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.View
import android.view.WindowManager


fun View.getMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int, defaultWidth: Int, defaultHeight: Int): Array<Int> {
    val widthMode = View.MeasureSpec.getMode(widthMeasureSpec)   //获取宽的模式
    val heightMode = View.MeasureSpec.getMode(heightMeasureSpec) //获取高的模式
    val widthSize = View.MeasureSpec.getSize(widthMeasureSpec)   //获取宽的尺寸
    val heightSize = View.MeasureSpec.getSize(heightMeasureSpec) //获取高的尺寸
    val width = if (widthMode == View.MeasureSpec.EXACTLY) {
        //如果match_parent或者具体的值，直接赋值
        widthSize
    } else {
        //如果是wrap_content，我们要得到控件需要多大的尺寸
        //控件的宽度就是文本的宽度加上两边的内边距。内边距就是padding值，在构造方法执行完就被赋值
        paddingLeft + defaultWidth + paddingRight
    }
    //高度跟宽度处理方式一样
    val height = if (heightMode == View.MeasureSpec.EXACTLY) {
        heightSize
    } else {
        paddingTop + defaultHeight + paddingBottom
    }
    return arrayOf(width, height)
}

/**
 * 获取屏幕宽高
 */
fun getScreenWidthAndHeight(context: Context): Array<Int> {
    val manager = context.applicationContext.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val outMetrics = DisplayMetrics()
    manager.defaultDisplay.getMetrics(outMetrics)
    val width = outMetrics.widthPixels
    val height = outMetrics.heightPixels
    return arrayOf(width, height)
}

//dp px
fun dp2px(context: Context, dpval: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dpval.toFloat(),
        context.applicationContext.resources.displayMetrics
    ).toInt()
}
