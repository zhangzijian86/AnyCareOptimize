package com.fuer.anycare.chart.view;


import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class ChartView extends View {

    // 传递过来的数据
	public String[] XLabel;
    public String[] Data;
    public String XString;
    public String YString;
    public String XYString;//点击后的XY单位

    public float xPoint = 30; // (以320宽度的分辨率为准,其他分辨率*系数)
    public float YPoint = 220; // (以320宽度的分辨率为准,其他分辨率*系数)
    public float YLength = 200;// (以320宽度的分辨率为准,其他分辨率*系数)
    public float xLength;// X轴的刻度(屏幕的宽度-左右两边的间隔)

    public float xScale;// X轴的刻度长度
    public float YScale;// y轴的刻度长度
    

    public float eachYLabel;// Y轴应该显示的值
    public float halfXLabel;// X轴的刻度长度的2/3 用以显示X轴的坐标值

    private float selectX;// 选中区域要显示的圆点的 x坐标
    private float xyExtra = 20;// XY轴空余的距离
    private float displacement = 5;
    private float textSize = 12;
    private float circleSize = 5;// 圆点的大小
    private float radio;// 不同分辨率的 比率(以320为基数)

    private String selectData = "";// 选中之后需要显示的数据
    private String selectBushu = "";//选中之后需要显示的步数

    private int screenWidth;
    private int screenHeight;
    private RectF rectF;
    private List<PointBean> allpoint = new ArrayList<PointBean>();
    private Paint textPaint;
    private Paint linePaint;
    private Paint dataPaint;
    private Paint selectBkgPaint;
    private Paint selectCirclePaint;
    private Paint selectTextPaint;
    
    public String[] BData;
    private String sign;
    private TextView xingzouvalue;
    private TextView tixingvalue;
//    private Paint selectInforPaint;

    public ChartView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
    }

    public void SetInfo(String[] AllData,Display display,String []AllXLabel,String XSign,String YSign,String XYUnit,String[] buShuArr,String signstr,TextView xingzou,TextView tixing) {
//        XLabel = new String[] {
//                "1", "", "3", "", "5", "", "7", "", "9", "", "11", "","13","","15","","17","","19","","21","","23",""
//        };//x轴刻度标识
        Data = AllData;
        XLabel = AllXLabel;
        XString = XSign;
        YString = YSign;
        XYString = XYUnit;
        this.screenWidth = display.getWidth();//得到屏幕的宽度
        this.screenHeight = display.getHeight();//得到屏幕的高度
        radio = screenWidth / 320;// (以320的分辨率为基准)
        xPoint = xPoint * radio;// X轴左右两边的间隔
        xLength = screenWidth - xPoint * 2;// X轴的长度 （屏幕宽度减去左右两边空余xPoint的刻度）
        xyExtra = xyExtra * radio;//xy轴空余的距离
        xScale = (xLength - xyExtra) / XLabel.length; // X轴的刻度间隔    为长度-最右边空余的距离除以个数
        halfXLabel = xScale * 2 / 3;// X轴刻度的一半值(X轴刻度值在中间显示)

        YPoint = YPoint * radio;// y轴上下两边的间隔
        YLength = YLength * radio;//y轴的长度
        displacement = displacement * radio;// Y轴显示的文字(金额)
        YScale = (YLength - xyExtra) / 5;// Y轴的刻度为长度-最上边的距离 除以个数
        eachYLabel = max(Data) / 5;// Y轴的刻度值 为(最大数/5)

        textSize = textSize * radio;//文本的大小
        circleSize = circleSize * radio;//圆圈的大小
        
        BData = buShuArr;
        sign = signstr;
        xingzouvalue = xingzou;
        tixingvalue = tixing;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);// 重写onDraw方法
        // 文本的画笔
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setAntiAlias(true);// 去锯齿
        textPaint.setColor(Color.WHITE);// 颜色
        textPaint.setTextSize(textSize); // 设置轴文字大小
        // 线的画笔
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);// 去锯齿
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);// 去锯齿
        linePaint.setColor(Color.parseColor("#808b98"));// 颜色
        // 曲线和未选中圆点的画笔
        dataPaint = new Paint();
        dataPaint.setStyle(Paint.Style.FILL);
        dataPaint.setAntiAlias(true);// 去锯齿
        dataPaint.setColor(Color.parseColor("#FFFFFF"));// 颜色
        dataPaint.setTextSize(textSize); // 设置轴文字大小
        dataPaint.setStrokeWidth(4);
        // 选中之后 字体的画笔
        selectTextPaint = new Paint();
        selectTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectTextPaint.setAntiAlias(true);// 去锯齿
        selectTextPaint.setColor(Color.WHITE);// 颜色
        selectTextPaint.setTextSize(textSize); // 设置轴文字大小
        // 选中之后月份背景的画笔
        selectBkgPaint = new Paint();
        selectBkgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectBkgPaint.setAntiAlias(true);// 去锯齿
        selectBkgPaint.setColor(Color.TRANSPARENT);// 颜色
        selectBkgPaint.setTextSize(textSize); // 设置轴文字大小
        // 选中之后 显示数据的画笔
        selectCirclePaint = new Paint();
        selectCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectCirclePaint.setAntiAlias(true);// 去锯齿
        selectCirclePaint.setColor(Color.RED);// 颜色
        selectCirclePaint.setTextSize(textSize);
//        // 填写选中之后详细信息
//        selectInforPaint = new Paint();
//        selectInforPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        selectInforPaint.setAntiAlias(true);// 去锯齿
//        selectInforPaint.setColor(Color.WHITE);// 颜色
//        selectInforPaint.setTextSize(textSize*5/3);
        
        
        if (!selectData.equals("")) {// 点击某个刻度的时候 矩形区域颜色值改变(最高的一条线为top)
            canvas.drawRect(rectF.left, YPoint - YLength + xyExtra, rectF.right, rectF.bottom,selectBkgPaint);
        }

        // 设置Y轴
        canvas.drawLine(xPoint, YPoint - YLength, xPoint, YPoint, linePaint);
        // Y轴的箭头
        canvas.drawLine(xPoint, YPoint - YLength, xPoint - 3, YPoint - YLength + 6, linePaint);
        canvas.drawLine(xPoint, YPoint - YLength, xPoint + 3, YPoint - YLength + 6, linePaint);
        // 设置X轴
        canvas.drawLine(xPoint, YPoint, xPoint + xLength, YPoint, linePaint);
        // X轴的箭头
        canvas.drawLine(xPoint + xLength, YPoint, xPoint + xLength - 6, YPoint - 3, linePaint);
        canvas.drawLine(xPoint + xLength, YPoint, xPoint + xLength - 6, YPoint + 3, linePaint);
        canvas.drawText(XString, xPoint + xLength, YPoint + displacement * 3, textPaint); // 月份
        canvas.drawText(YString, xPoint + displacement, YPoint - YLength, textPaint); // 金额

        // Y轴线
        for (int i = 0; i < 5; i++) {
            // Y轴的每条线,X轴为xPoint-xPoint + xLength Y轴固定高度 YPoint - (i + 1) *
            // YScale(根据圆点计算出的值)
            canvas.drawLine(xPoint, YPoint - (i + 1) * YScale, xPoint + xLength, YPoint - (i + 1)* YScale, linePaint); // 刻度
            try {
                // Y轴的刻度值,值为平均分配之后算出来的
                canvas.drawText(String.valueOf(eachYLabel * (i + 1)), 5, YPoint - (i + 1) * YScale+ 5, textPaint); // 文字
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
        canvas.drawText("0", 10, YPoint + displacement * 3, textPaint); // 文字
        for (int i = 0; i < XLabel.length; i++) {
            try {
                // X轴的每条刻度线
                canvas.drawLine(xPoint + (i + 1) * xScale, YPoint, xPoint + (i + 1) * xScale,YPoint - displacement, linePaint);
                // X轴显示的刻度值
                canvas.drawText(XLabel[i], xPoint + (i + 1) * xScale - halfXLabel, YPoint+ displacement* 3, textPaint); // 文字
                PointBean bean = new PointBean();
                // 点击之后的矩形取悦
                bean.rectF = new RectF(xPoint + i * xScale, 0, xPoint + (i + 1) * xScale, YPoint);
                bean.dushu = Data[i];
                bean.bushu = BData[i];
                bean.x = xPoint + (i + 1) * xScale - halfXLabel;
                allpoint.add(bean);

                if (i == 0) {
                    canvas.drawLine(xPoint + (i) * xScale, YPoint, xPoint + (i + 1) * xScale - halfXLabel, YCoord(Data[i]), dataPaint);
                    canvas.drawCircle(xPoint, YPoint, circleSize, dataPaint);
                    canvas.drawCircle(xPoint + (i + 1) * xScale - halfXLabel, YCoord(Data[i]),circleSize,dataPaint);
                } else {
                    canvas.drawLine(xPoint + (i) * xScale - halfXLabel, YCoord(Data[i - 1]), xPoint+ (i + 1) * xScale - halfXLabel, YCoord(Data[i]), dataPaint);
                    canvas.drawCircle(xPoint + (i + 1) * xScale - halfXLabel, YCoord(Data[i]),circleSize,dataPaint);
                }
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }

        if (!selectData.equals("")) {
            // 点击的时候,画出红点 和显示数据
            canvas.drawCircle(selectX, YCoord(selectData), circleSize, selectCirclePaint);
            canvas.drawText( selectData+XYString, selectX + displacement, YCoord(selectData)- displacement, selectCirclePaint);
//            canvas.drawText("时间：12:27 心率：80次/分钟", xPoint, YPoint+80, selectInforPaint);
            //活动量更新步数
            if("H".equals(sign)){
            	xingzouvalue.setText(selectBushu);
            //静止更新提醒次数
            }else if("J".equals(sign)){
            	tixingvalue.setText(selectData);
            }
        }
    }

    /**
     * 计算Y坐标
     * 
     * @param y0
     * @return
     */
    private float YCoord(String y0) // 计算绘制时的Y坐标，无数据时返回-999
    {
        float y;
        try {
            y = Float.parseFloat(y0);
        } catch (Exception e) {
            return -999; // 出错则返回-999
        }
        try {
            // YScale/eachYLabel为比率 乘以y得到距离圆点的距离
            return (float) (YPoint - YScale * y / eachYLabel);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return y;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // TODO Auto-generated method stub

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                for (int i = 0; i < allpoint.size(); i++) {
                    if (allpoint.get(i).rectF.contains(event.getX(), event.getY())) {
                        PointBean bean = allpoint.get(i);
                        selectX = bean.x;
                        selectData = bean.dushu;
                        selectBushu = bean.bushu;
                        rectF = bean.rectF;
                        postInvalidate();
                    }
                }
                break;

            case MotionEvent.ACTION_MOVE:

                break;
            case MotionEvent.ACTION_UP:

                break;
            default:
                break;
        }
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // TODO Auto-generated method stub

        if (xLength > screenWidth) {
            int with = (int) (XLabel.length * xScale + xPoint + 25 * 2);
            setMeasuredDimension(with, screenHeight);
        } else {
            setMeasuredDimension(screenWidth, screenHeight);
        }
    }

    /**
     * 计算Y轴坐标的最大值
     * 
     * @param p
     * @return
     */
    public static float max(String[] p) {
        float max = 0;
        for (int i = 0; i < p.length; i++) {
            if (Float.parseFloat(p[i]) - max > 0) {
                max = Float.parseFloat(p[i]);
            }
        }
        int length = (int) (max) / 20 + 1;// 为了取整数 比如最大值为39的时候 返回40
        return length * 20;
    }

}
