package com.jackxiong.scrollsetvaluecircleprogressbar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import static java.lang.Math.PI;

/**
 * @author xiongwenjie
 * @time 2017/3/20 9:42
 * @updateAuthor $Author$
 * @updateDate $Date$
 * 可滑动设置值的类太阳圆形进度条
 */
public class ScrollSetValueProgressCircle extends View {

    private static final String TAG = "ScrollSetValueProgressCircle";
    private int mFinalHeight;//控件最终高度
    private int mFinalWidth;//控件最终宽度

    private int circleWidth = 5;//中间圆圈线条宽度
    private int mCircleRadius;//中间圆圈的半径
    private DrawFilter mDrawFilter;

    private int mPaintDefaultColor;//射线和圆圈的默认颜色
    private int mCircleSolidColor;//内部实体圆的颜色
    private int mSolidCircleRadius;//内部实体圆的半径

    private float mCentreX;//画布中点x坐标
    private int mCentreY;//画布中点Y坐标

    private Context mContext;

    private int mRayOutRadius;//射线外半径
    private Region mRayInnerRegion;//射线内侧区域坐标值集合
    private Region mRayOuterRegion;//射线外侧区域坐标值集合
    private float mRadianByPos;


    private SweepGradient mLowValueSg;
    private SweepGradient mNormalValueSg;
    private SweepGradient mHightValueSg;

    private float mUnitScale = 10.8f;//单位刻度值(度)

    private float mTargetLowValue_small = 3.9f;//低范围的低值,低于或等于该值为低血糖
    private float mTargetLowValue_big = 4.4f;//低范围的高值,小于该值为偏低
    /**
     *
     */
    private float mTargetHightValue_small = 7.0f;//高范围的低值,等于或小于该值为正常
    private float mTargetHightValue_big = 16.7f;//偏高范围的高值,等于或大于该值为偏高
    private int mValueUnitColor;

    enum _Quadrant {
        eQ_NONE,                                    //  在坐标轴上
        eQ_ONE,                                        //  第一象限
        eQ_TWO,                                        //	第二象限
        eQ_THREE,                                    //	第三象限
        eQ_FOUR                                        //	第四象限
    }

    public ScrollSetValueProgressCircle(Context context) {
        super(context);
    }

    public ScrollSetValueProgressCircle(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;

        mPaintDefaultColor = context.getResources().getColor(R.color.bg_gray);
        mCircleSolidColor = context.getResources().getColor(R.color
                .scroll_set_value_progress_bar_solid);

        int mLowStartColor = context.getResources().getColor(R.color.blood_sugar_low_color_small)
                ;//低血糖颜色
        int mLowEndColor = context.getResources().getColor(R.color.blood_sugar_low_color_big);//偏低颜色

        int mNormalStartColor = Color.parseColor("#ff60cc73");
        int mNormalEndColor = Color.parseColor("#ff40c8b0");//正常值结束颜色

        int mHightStartColor = context.getResources().getColor(R.color
                .blood_sugar_hight_color_small);//偏高颜色
        int mHightEndColor = context.getResources().getColor(R.color.blood_sugar_hight_color_big)
                ;//高血糖颜色

        mValueUnitColor = Color.parseColor("#AAAAAA");

        mDrawFilter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);

        //偏低范围渐变颜色组
        mLowValueSg = new SweepGradient(0, 0, new int[]{mLowStartColor, mLowEndColor}, null);
        //正常范围渐变颜色组
        mNormalValueSg = new SweepGradient(0, 0, new int[]{mNormalStartColor, mNormalEndColor},
                null);
        //偏高范围渐变颜色组
        mHightValueSg = new SweepGradient(0, 0, new int[]{mHightStartColor, mHightEndColor}, null);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        setMeasuredDimension(Math.max(widthSize, heightSize) / 2, Math.max(widthSize, heightSize)
                / 2);//控制view的大小

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //onMeasure 后控件的最终大小
        mFinalWidth = w;
        mFinalHeight = h;
        setMeasuredDimension(Math.min(mFinalWidth, mFinalHeight), Math.min(mFinalWidth,
                mFinalHeight));

        mRayOutRadius = (mFinalHeight - 4 * circleWidth) / 2;
        mSolidCircleRadius = mRayOutRadius - 110;
        mCircleRadius = mRayOutRadius - 85;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 从canvas层面去除绘制时锯齿
        canvas.setDrawFilter(mDrawFilter);
        //移动画布原点到控件中点
        mCentreX = mFinalWidth / 2;
        mCentreY = mFinalHeight / 2;
        canvas.translate(mCentreX, mCentreY);

        //画默认射线
        drawRayAndCircle(canvas);
        //画默认圆圈
        baseCircli(canvas);
        //画实体圆
        drawSolidCircle(canvas);
        //画值和单位
        drawValueText(canvas);

    }

    private void drawValueText(Canvas canvas) {
        Paint mPaint = new Paint();
        // 去除画笔锯齿
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextSize(mCircleRadius / 2);

        // 画值
        float value = mRadianByPos / mUnitScale;
        BigDecimal bigDecimal = BigDecimal.valueOf(value);
        int lowCompare = bigDecimal.compareTo(BigDecimal.valueOf(mTargetLowValue_big));
        int highCompare = bigDecimal.compareTo(BigDecimal.valueOf(mTargetHightValue_small));

        if (lowCompare == -1) {
            //偏低或低血糖
            mPaint.setShader(mLowValueSg);
        } else if (highCompare == 1) {
            //偏高或高血糖
            mPaint.setShader(mHightValueSg);
        } else {
            mPaint.setShader(mNormalValueSg);
        }
        //        String valueStr = String.valueOf(value);
        String valueStr =round(BigDecimal.valueOf(value), "0.0");

        int textWidth = getTextWidth(mPaint, valueStr);
        //使值始终在正中
        canvas.drawText(valueStr, -(textWidth / 2), 0, mPaint);
        //画值单位
        mPaint.setShader(null);
        mPaint.setColor(mValueUnitColor);
        mPaint.setTextSize(mCircleRadius / 4);
        String unit = "mmol/L";
        int unitWidth = getTextWidth(mPaint, unit);

        Rect rect = new Rect();
        mPaint.getTextBounds(unit, 0, unit.length(), rect);//用一个矩形去"套"字符串,获得能完全套住字符串的最小矩形
        float height = rect.height();//字符串的高度
        canvas.drawText(unit, -(unitWidth / 2), height * 2, mPaint);
    }

    private void drawSolidCircle(Canvas canvas) {
        Paint mPaint = new Paint();
        // 去除画笔锯齿
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.WHITE);
        mPaint.setStyle(Paint.Style.FILL);

        canvas.drawCircle(0, 0, mSolidCircleRadius, mPaint);
    }


    private void drawRayAndCircle(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(mPaintDefaultColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(circleWidth);
        // 去除画笔锯齿
        mPaint.setAntiAlias(true);

        Path rayInnerPath = new Path();
        Path rayOuterPath = new Path();
        mRayInnerRegion = new Region();
        mRayOuterRegion = new Region();

        Region viewRegion = new Region(-mRayOutRadius, -mRayOutRadius, mRayOutRadius,
                mRayOutRadius);//整个控件区域内的所有点坐标集合

        //画默认射线
        for (float i = 0; i < 360; i += 10) {

            double rad = i * PI / 180;
            //射线内侧起点
            float startX = (float) (((mRayOutRadius - 35) - circleWidth) *
                    Math.sin(rad));
            float startY = -(float) (((mRayOutRadius - 35) - circleWidth) *
                    Math.cos(rad));
            //射线外侧终点,所以射线长度为 35px
            float stopX = (float) (mRayOutRadius * Math.sin(rad) + 1);
            float stopY = -(float) (mRayOutRadius * Math.cos(rad) + 1);

            //取的是射线区域内侧100px的区域的所有点坐标
            rayInnerPath.addCircle(0, 0, mRayOutRadius - 100, Path.Direction.CW);
            mRayInnerRegion.setPath(rayInnerPath, viewRegion);
            //取的是射线区域外侧50px的区域的所有点坐标
            rayOuterPath.addCircle(0, 0, mRayOutRadius + 50, Path.Direction.CW);
            mRayOuterRegion.setPath(rayOuterPath, viewRegion);

            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
        }


        // 画值射线和值圆弧
        //避免与中间圆圈重合而显示太浅
        //                RectF rf = new RectF(-mCircleRadius - 5, -mCircleRadius - 5,
        // mCircleRadius + 5,
        //                        mCircleRadius + 5);
        RectF rf = new RectF(-mCircleRadius, -mCircleRadius, mCircleRadius,
                mCircleRadius);
        for (float i = 0; i <= mRadianByPos; i += 10) {
            BigDecimal bigDecimal = BigDecimal.valueOf(mRadianByPos);
            int lowCompare = bigDecimal.compareTo(BigDecimal.valueOf(mTargetLowValue_big *
                    mUnitScale));
            int highCompare = bigDecimal.compareTo(BigDecimal.valueOf(mTargetHightValue_small *
                    mUnitScale));

            if (lowCompare == -1) {
                //偏低或低血糖
                mPaint.setShader(mLowValueSg);
            } else if (highCompare == 1) {
                //偏高或高血糖
                mPaint.setShader(mHightValueSg);
            } else {
                //正常值
                mPaint.setShader(mNormalValueSg);
            }

            double rad = i * PI / 180;
            double deg = rad * 180 / PI;

            float startX = (float) (((mRayOutRadius - 35) - circleWidth) *
                    Math.sin(rad));
            float startY = -(float) (((mRayOutRadius - 35) - circleWidth) *
                    Math.cos(rad));

            float stopX = (float) (mRayOutRadius * Math.sin(rad) + 1);
            float stopY = -(float) (mRayOutRadius * Math.cos(rad) + 1);
            //值射线
            canvas.drawLine(startX, startY, stopX, stopY, mPaint);
            //值圆弧
            canvas.drawArc(rf, -90, (float) deg, false, mPaint);
        }
    }

    private void baseCircli(Canvas canvas) {
        Paint mPaint = new Paint();
        mPaint.setColor(mPaintDefaultColor);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(0.5f);
        // 去除画笔锯齿
        mPaint.setAntiAlias(true);
        canvas.drawCircle(0, 0, mCircleRadius, mPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        //由于画布原点移到了控件中心，所以要矫正触点坐标
        int eventX = (int) (event.getX() - mCentreX);
        int eventY = (int) (mCentreY - event.getY());

        //只有当触摸在控制的射线区域才处理滑动事件
        if (mRayOuterRegion != null && mRayInnerRegion != null) {
            if (mRayOuterRegion.contains(eventX, eventY) && !mRayInnerRegion.contains(eventX,
                    eventY)) {
                Point point = new Point(eventX, eventY);
                mRadianByPos = GetRadianByPos(point);
                invalidate();

                if (mValueChangeListener != null) {
                    mValueChangeListener.currentValue(mRadianByPos / mUnitScale);
                }
            }
            return true;
        }
            return false;
    }


    /**
     * @param point
     * @return 获得点所在角度（点与坐标轴原点连线与Y正半轴的顺时针夹角）单位为度数
     */
    public int GetRadianByPos(Point point) {
        double dAngle = GetRadianByPosEx(point);

        return (int) (dAngle * (360 / (2 * PI)));
    }

    /**
     * @param point
     * @return 获得点所在角度（点与坐标轴原点连线与Y正半轴的顺时针夹角）单位为弧度
     */
    private static double GetRadianByPosEx(Point point) {

        if (point.x == 0 && point.y == 0) {
            return 0;
        }


        double Sin = point.x / Math.sqrt(point.x * point.x + point.y * point.y);
        double dAngle = Math.asin(Sin);

        switch (GetQuadrant(point)) {
            case eQ_NONE: {
                if (point.x == 0 && point.y == 0) {
                    return 0;
                }

                if (point.x == 0) {
                    if (point.y > 0) {
                        return 0;
                    } else {
                        return PI;
                    }
                }

                if (point.y == 0) {
                    if (point.x > 0) {
                        return PI / 2;
                    } else {
                        return (float) (1.5 * PI);
                    }
                }
            }
            break;
            case eQ_ONE: {
                return dAngle;
            }
            case eQ_TWO: {
                dAngle = PI - dAngle;
            }
            break;
            case eQ_THREE: {
                dAngle = PI - dAngle;
            }
            break;
            case eQ_FOUR: {
                dAngle += 2 * PI;
            }
            break;
        }

        return dAngle;

    }

    /**
     * @param point
     * @return 获得Point点所在象限
     */
    public static _Quadrant GetQuadrant(Point point) {
        if (point.x == 0 || point.y == 0) {
            return _Quadrant.eQ_NONE;
        }

        if (point.x > 0) {
            if (point.y > 0) {
                return _Quadrant.eQ_ONE;
            } else {
                return _Quadrant.eQ_TWO;
            }

        } else {
            if (point.y < 0) {
                return _Quadrant.eQ_THREE;
            } else {
                return _Quadrant.eQ_FOUR;
            }
        }
    }

    /**
     * 获取字符串长度
     *
     * @param mPaint
     * @param str
     * @return
     */
    public int getTextWidth(Paint mPaint, String str) {
        float iSum = 0;
        if (str != null && !str.equals("")) {
            int len = str.length();
            float widths[] = new float[len];
            mPaint.getTextWidths(str, widths);
            for (int i = 0; i < len; i++) {
                iSum += Math.ceil(widths[i]);
            }
        }
        return (int) iSum;
    }

    /**
     * 值保留一位小数
     * @param target
     * @param decimalDigits
     * @return
     */
    private String round(BigDecimal target,String decimalDigits){
        DecimalFormat myformat = new DecimalFormat(decimalDigits);
        return myformat.format(target);
    }

    /**
     * 初始化显示的值
     *
     * @param targetValue
     */
    public void setValue(float targetValue) {
        if (targetValue < 0) {
            targetValue = 0;
        }
        //最大值为33.3
        if (targetValue > 33.3f) {
            targetValue = 33.3f;
        }
        //一个刻度为10.8度
        mRadianByPos = targetValue * mUnitScale;

        invalidate();
    }

    /**
     * 设置偏高范围的低值
     *
     * @param targetHightValue
     */
    public void setTargetHightValue_small(float targetHightValue) {
        mTargetHightValue_small = targetHightValue;
    }

    public ValueChangeListener mValueChangeListener;

    public interface ValueChangeListener {
        void currentValue(float value);
    }

    public void setValueChangeListener(ValueChangeListener listener) {
        mValueChangeListener = listener;
    }
}
