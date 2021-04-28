package com.midfang.statusbar;


import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.Context
import android.graphics.Color
import android.graphics.PixelFormat
import android.os.Build
import android.util.AttributeSet
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import androidx.annotation.MainThread
import androidx.constraintlayout.widget.ConstraintLayout
import com.midfang.statusbar.utils.StatusBarStatusManager
import com.midfang.statusbar.utils.BarUtils
import kotlin.math.abs


/**
 * author : midFang
 * time   : 2021/04/15
 * desc   :
 * version: 1.0
 */
class StatusBarView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : ConstraintLayout(context, attrs, defStyleAttr) {

    companion object {
        private const val TAG = "StatusBarView"
    }

    /**
     * 展开收缩动画时长
     */
    private val mOpenOrCloseAnimatorDuration = 210L

    private val mStatusBarViewManager by lazy { StatusBarViewManager.instance }

    /**
     * 是否可以下拉
     */
    private var isDropDown = true

    /**
     * 处理手势（快速滑动）
     */
    private val mGestureDetector: GestureDetector


    /**
     * 按下的时候， childHeight 的 y 的位置
     */
    private var mDownCurrentChildHeight = 0


    /**
     * 可以触摸的范围
     */
    private var mTouchRange: Int = 0

    private var mDownY: Float = 0.0f
    private var mMoveY: Float = 0.0f

    /**
     * 本控件在window中的布局
     */
    private val mLayoutParamsInWindow: WindowManager.LayoutParams
    private val mWindowManager: WindowManager =
        context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val mStatusBarViewBackgroundColor: Int

    private var mScreenWidth: Int = 0
    private val mScreenHeight: Int

    // 是否已经完全展开
    private var isWholeOpened: Boolean = false

    /**
     * 是否正在展示
     */
    private var isShow = false

    /**
     * 状态栏高度，即隐藏时触摸有效区，展示时底部横条高度
     */
    private val statusBarHeight: Int

    /**
     * 整个通知栏最大高度
     */
    private val notificationSettingHeight: Int

    /**
     * 底部横线中间细线高度
     */
    private val bottomLineHeight: Float

    /**
     * 底部横线
     */
    private val bottom: ConstraintLayout

    /**
     * 底部横线布局
     */
    private val bottomLayoutParams: LayoutParams

    /**
     * 内容部分，通过xml实现
     */
    private val content: View

    /**
     * 内容部分布局
     */
    private val contentLayoutParams: LayoutParams

    /**
     * 本控件唯一子控件，伸缩为调节child的高度
     */
    private val child: ConstraintLayout

    /**
     * child的布局
     */
    private val childLayoutParams: LayoutParams

    /**
     * 展开与收缩的动画
     */
    private var mOpenOrCloseAnimator: ObjectAnimator? = null


    /**
     * 展开和收起差值器
     */
    private val mOpenOrCloseAnimatorInterpolator = LinearInterpolator()

    fun isDropDown() = isDropDown

    private fun dip2px(px: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            px.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private val mGestureListener = object : GestureDetector.SimpleOnGestureListener() {
        override fun onFling(
            e1: MotionEvent,
            e2: MotionEvent,
            velocityX: Float,
            velocityY: Float
        ): Boolean {

            val moveXDistance = abs(e2.rawX - e1.rawX)
            val moveYDistance = abs(e2.rawY - e1.rawY)
            if (isWholeOpened) {
                // 触发收起
                if (velocityY < 0 && moveYDistance > moveXDistance) {
                    close()
                    return true
                }
            } else {
                // 触发展开
                if (velocityY > 0 && moveYDistance > moveXDistance) {
                    open()
                    return true
                }
            }

            return super.onFling(e1, e2, velocityX, velocityY)
        }
    }

    init {
        mGestureDetector = GestureDetector(context, mGestureListener)
        statusBarHeight = BarUtils.getNavBarHeight()
        mTouchRange = statusBarHeight / 2
        mStatusBarViewBackgroundColor = resources.getColor(android.R.color.black)
        notificationSettingHeight =
            mWindowManager.defaultDisplay.height // 整个屏幕的高度
        mScreenWidth = mWindowManager.defaultDisplay.width
        mScreenHeight = mWindowManager.defaultDisplay.height
        bottomLineHeight = resources.getDimension(R.dimen.status_bar_bottom_line_height) // 底部线条的高度

        // 底部部分
        bottom = ConstraintLayout(context)
        bottomLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, statusBarHeight)
        bottomLayoutParams.bottomToBottom = LayoutParams.PARENT_ID
        bottom.setBackgroundColor(Color.TRANSPARENT)
        bottom.id = R.id.cl_notification_bar_bottom

        val bottomImageView = ImageView(context)
        bottomImageView.setBackgroundResource(R.drawable.notification_bottom_shape)
        val bottomImageViewLayoutParams = LayoutParams(dip2px(133), dip2px(4))
        bottomImageViewLayoutParams.topToTop = LayoutParams.PARENT_ID
        bottomImageViewLayoutParams.bottomToBottom = LayoutParams.PARENT_ID
        bottomImageViewLayoutParams.leftToLeft = LayoutParams.PARENT_ID
        bottomImageViewLayoutParams.rightToRight = LayoutParams.PARENT_ID

        bottom.addView(bottomImageView, bottomImageViewLayoutParams)

        // 内容部分
        content = LayoutInflater.from(context).inflate(R.layout.notification_bar, null)
        contentLayoutParams =
            LayoutParams(LayoutParams.MATCH_PARENT, (notificationSettingHeight - statusBarHeight))
        contentLayoutParams.bottomToTop = R.id.cl_notification_bar_bottom

        child = ConstraintLayout(context)
        child.addView(bottom, bottomLayoutParams)
        child.addView(content, contentLayoutParams)
        child.visibility = View.INVISIBLE
        childLayoutParams = LayoutParams(LayoutParams.MATCH_PARENT, 0)
        childLayoutParams.topToTop = LayoutParams.PARENT_ID
        child.setBackgroundColor(Color.parseColor("#2F72ED"))

        addView(child, childLayoutParams)

        if (Build.VERSION.SDK_INT >= 26) {//8.0新特性
            mLayoutParamsInWindow = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mTouchRange,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT
            )
        } else {
            mLayoutParamsInWindow = WindowManager.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                mTouchRange,
                WindowManager.LayoutParams.TYPE_SYSTEM_ALERT,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        or WindowManager.LayoutParams.FLAG_TOUCHABLE_WHEN_WAKING
                        or WindowManager.LayoutParams.FLAG_SPLIT_TOUCH
                        or WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                        or WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS,
                PixelFormat.TRANSLUCENT
            )
        }
        mLayoutParamsInWindow.gravity = Gravity.TOP

        initListener(content)
    }

    private fun initListener(content: View) {
        content.findViewById<View>(R.id.btnClose).setOnClickListener {
            Log.i(TAG, "initListener: btnClose")
            close()
        }
    }

    /**
     * 禁止下拉
     */
    fun disableStatusBar() {
        if (isShow) {
            close(300)
        }
        isDropDown = false
    }

    /**
     * 恢复下拉
     */
    fun enableStatusBar() {
        isDropDown = true
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isDropDown) { // 是否可以下拉
            return true
        }
        var isFling = false
        if (!isWholeOpened) {
            // 未全部展开，响应快速滑动
            isFling = true
        } else {
            // 按下的是可触摸滑动的区域
            if (mDownY >= (mScreenHeight - statusBarHeight)) {
                isFling = true
            }
        }
        if (isFling && mGestureDetector.onTouchEvent(event)) {
            return true
        }


        if (event.action == MotionEvent.ACTION_DOWN) {
            mDownY = event.rawY
            stopOpenOrCloseAnim()
            mDownCurrentChildHeight = child.height
            show()
        }

        if (event.action == MotionEvent.ACTION_MOVE) {
            mMoveY = event.rawY

            if (isWholeOpened && mDownY < (mScreenHeight - mScreenHeight / 4)) {//todo 调整到屏幕的下方 1/4 处可以滑动
                return true
            }

            mMoveY = mDownCurrentChildHeight - (mDownY - mMoveY)

            // 限制布局不再往下移动
            if (((mDownY - mMoveY) < 0) && mMoveY.toInt() >= mScreenHeight) {
                mMoveY = mScreenHeight.toFloat()
            }

            if (mMoveY.toInt() <= 0) {
                mMoveY = 0f
            }

            setChildHeight(mMoveY.toInt())
        }

        if (event.action == MotionEvent.ACTION_UP) {
            if (child.height > (mScreenHeight / 2)) open() else close()
        }

        return true
    }

    /**
     * 停止收起和展开的动画
     */
    private fun stopOpenOrCloseAnim() {
        if (mOpenOrCloseAnimator != null) {
            mOpenOrCloseAnimator?.cancel()
            mOpenOrCloseAnimator = null
        }
    }

    private fun setChildHeight(height: Int) {

        if (child.visibility != View.VISIBLE && height > 0) {
            child.visibility = View.VISIBLE
        }

        if (height == 0) {
            child.visibility = View.GONE
        }

        childLayoutParams.height = height
        child.requestLayout()
    }

    @MainThread
    fun expand() {
        // 全部展开
        show()
        open()
    }

    /**
     * 弹出
     */
    @MainThread
    fun open() {
        if (childLayoutParams.height < notificationSettingHeight) {
            mOpenOrCloseAnimator =
                ObjectAnimator.ofInt(
                    this,
                    "childHeight",
                    childLayoutParams.height,
                    notificationSettingHeight
                )
            mOpenOrCloseAnimator?.duration = mOpenOrCloseAnimatorDuration
            mOpenOrCloseAnimator?.interpolator = mOpenOrCloseAnimatorInterpolator
            var lastIsWholeOpened = isWholeOpened
            mOpenOrCloseAnimator?.addUpdateListener {
                val animatedValue = it.animatedValue as Int
                isWholeOpened = animatedValue >= mScreenHeight

                if (isWholeOpened && !lastIsWholeOpened) {
                    // 由非完全展开到完全展开
                    StatusBarStatusManager.onOpenedCallback()
                }
            }
            mOpenOrCloseAnimator?.start()
        }
    }

    fun isWholeOpened() = isWholeOpened

    /**
     * 收起
     */
    @MainThread
    fun close() {

        if (childLayoutParams.height >= 0) {
            var animatedValue = 0
            mOpenOrCloseAnimator =
                ObjectAnimator.ofInt(this, "childHeight", childLayoutParams.height, 0)
            mOpenOrCloseAnimator?.duration = mOpenOrCloseAnimatorDuration
            mOpenOrCloseAnimator?.interpolator = mOpenOrCloseAnimatorInterpolator
            mOpenOrCloseAnimator?.addListener(object : Animator.AnimatorListener {
                override fun onAnimationEnd(animation: Animator?) {
                    setChildHeight(0) //解决下拉页无法完全消失的问题
                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

                override fun onAnimationRepeat(animation: Animator?) {
                }
            })
            var lastIsWholeOpened = isWholeOpened
            mOpenOrCloseAnimator?.start()
            mOpenOrCloseAnimator?.addUpdateListener {
                animatedValue = it.animatedValue as Int
                isWholeOpened = animatedValue >= mScreenHeight
                if (animatedValue == 0) {
                    hide()
                    if (lastIsWholeOpened) {
                        // 由完全展开到收起
                        StatusBarStatusManager.onClosedCallback()
                    }
                }
            }
        }
    }


    fun addToWindow() {
        mWindowManager.addView(this, mLayoutParamsInWindow)
    }

    /**
     * 显示
     */
    fun show() {
        isShow = true
        mLayoutParamsInWindow.height = WindowManager.LayoutParams.MATCH_PARENT
        mWindowManager.updateViewLayout(this, mLayoutParamsInWindow)
    }


    /**
     * 隐藏
     */
    fun hide() {
        isShow = false
        child.visibility = View.GONE
        mLayoutParamsInWindow.height = mTouchRange
        mWindowManager.updateViewLayout(this, mLayoutParamsInWindow)
    }


    /**
     * 延长一定时间关闭
     */
    fun close(delayTime: Long) {
        handler.postDelayed({ close() }, delayTime)
    }


}