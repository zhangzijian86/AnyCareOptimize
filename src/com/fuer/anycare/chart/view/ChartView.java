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

    // ���ݹ���������
	public String[] XLabel;
    public String[] Data;
    public String XString;
    public String YString;
    public String XYString;//������XY��λ

    public float xPoint = 30; // (��320��ȵķֱ���Ϊ׼,�����ֱ���*ϵ��)
    public float YPoint = 220; // (��320��ȵķֱ���Ϊ׼,�����ֱ���*ϵ��)
    public float YLength = 200;// (��320��ȵķֱ���Ϊ׼,�����ֱ���*ϵ��)
    public float xLength;// X��Ŀ̶�(��Ļ�Ŀ��-�������ߵļ��)

    public float xScale;// X��Ŀ̶ȳ���
    public float YScale;// y��Ŀ̶ȳ���
    

    public float eachYLabel;// Y��Ӧ����ʾ��ֵ
    public float halfXLabel;// X��Ŀ̶ȳ��ȵ�2/3 ������ʾX�������ֵ

    private float selectX;// ѡ������Ҫ��ʾ��Բ��� x����
    private float xyExtra = 20;// XY�����ľ���
    private float displacement = 5;
    private float textSize = 12;
    private float circleSize = 5;// Բ��Ĵ�С
    private float radio;// ��ͬ�ֱ��ʵ� ����(��320Ϊ����)

    private String selectData = "";// ѡ��֮����Ҫ��ʾ������
    private String selectBushu = "";//ѡ��֮����Ҫ��ʾ�Ĳ���

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
//        };//x��̶ȱ�ʶ
        Data = AllData;
        XLabel = AllXLabel;
        XString = XSign;
        YString = YSign;
        XYString = XYUnit;
        this.screenWidth = display.getWidth();//�õ���Ļ�Ŀ��
        this.screenHeight = display.getHeight();//�õ���Ļ�ĸ߶�
        radio = screenWidth / 320;// (��320�ķֱ���Ϊ��׼)
        xPoint = xPoint * radio;// X���������ߵļ��
        xLength = screenWidth - xPoint * 2;// X��ĳ��� ����Ļ��ȼ�ȥ�������߿���xPoint�Ŀ̶ȣ�
        xyExtra = xyExtra * radio;//xy�����ľ���
        xScale = (xLength - xyExtra) / XLabel.length; // X��Ŀ̶ȼ��    Ϊ����-���ұ߿���ľ�����Ը���
        halfXLabel = xScale * 2 / 3;// X��̶ȵ�һ��ֵ(X��̶�ֵ���м���ʾ)

        YPoint = YPoint * radio;// y���������ߵļ��
        YLength = YLength * radio;//y��ĳ���
        displacement = displacement * radio;// Y����ʾ������(���)
        YScale = (YLength - xyExtra) / 5;// Y��Ŀ̶�Ϊ����-���ϱߵľ��� ���Ը���
        eachYLabel = max(Data) / 5;// Y��Ŀ̶�ֵ Ϊ(�����/5)

        textSize = textSize * radio;//�ı��Ĵ�С
        circleSize = circleSize * radio;//ԲȦ�Ĵ�С
        
        BData = buShuArr;
        sign = signstr;
        xingzouvalue = xingzou;
        tixingvalue = tixing;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);// ��дonDraw����
        // �ı��Ļ���
        textPaint = new Paint();
        textPaint.setStyle(Paint.Style.STROKE);
        textPaint.setAntiAlias(true);// ȥ���
        textPaint.setColor(Color.WHITE);// ��ɫ
        textPaint.setTextSize(textSize); // ���������ִ�С
        // �ߵĻ���
        linePaint = new Paint();
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);// ȥ���
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setAntiAlias(true);// ȥ���
        linePaint.setColor(Color.parseColor("#808b98"));// ��ɫ
        // ���ߺ�δѡ��Բ��Ļ���
        dataPaint = new Paint();
        dataPaint.setStyle(Paint.Style.FILL);
        dataPaint.setAntiAlias(true);// ȥ���
        dataPaint.setColor(Color.parseColor("#FFFFFF"));// ��ɫ
        dataPaint.setTextSize(textSize); // ���������ִ�С
        dataPaint.setStrokeWidth(4);
        // ѡ��֮�� ����Ļ���
        selectTextPaint = new Paint();
        selectTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectTextPaint.setAntiAlias(true);// ȥ���
        selectTextPaint.setColor(Color.WHITE);// ��ɫ
        selectTextPaint.setTextSize(textSize); // ���������ִ�С
        // ѡ��֮���·ݱ����Ļ���
        selectBkgPaint = new Paint();
        selectBkgPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectBkgPaint.setAntiAlias(true);// ȥ���
        selectBkgPaint.setColor(Color.TRANSPARENT);// ��ɫ
        selectBkgPaint.setTextSize(textSize); // ���������ִ�С
        // ѡ��֮�� ��ʾ���ݵĻ���
        selectCirclePaint = new Paint();
        selectCirclePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        selectCirclePaint.setAntiAlias(true);// ȥ���
        selectCirclePaint.setColor(Color.RED);// ��ɫ
        selectCirclePaint.setTextSize(textSize);
//        // ��дѡ��֮����ϸ��Ϣ
//        selectInforPaint = new Paint();
//        selectInforPaint.setStyle(Paint.Style.FILL_AND_STROKE);
//        selectInforPaint.setAntiAlias(true);// ȥ���
//        selectInforPaint.setColor(Color.WHITE);// ��ɫ
//        selectInforPaint.setTextSize(textSize*5/3);
        
        
        if (!selectData.equals("")) {// ���ĳ���̶ȵ�ʱ�� ����������ɫֵ�ı�(��ߵ�һ����Ϊtop)
            canvas.drawRect(rectF.left, YPoint - YLength + xyExtra, rectF.right, rectF.bottom,selectBkgPaint);
        }

        // ����Y��
        canvas.drawLine(xPoint, YPoint - YLength, xPoint, YPoint, linePaint);
        // Y��ļ�ͷ
        canvas.drawLine(xPoint, YPoint - YLength, xPoint - 3, YPoint - YLength + 6, linePaint);
        canvas.drawLine(xPoint, YPoint - YLength, xPoint + 3, YPoint - YLength + 6, linePaint);
        // ����X��
        canvas.drawLine(xPoint, YPoint, xPoint + xLength, YPoint, linePaint);
        // X��ļ�ͷ
        canvas.drawLine(xPoint + xLength, YPoint, xPoint + xLength - 6, YPoint - 3, linePaint);
        canvas.drawLine(xPoint + xLength, YPoint, xPoint + xLength - 6, YPoint + 3, linePaint);
        canvas.drawText(XString, xPoint + xLength, YPoint + displacement * 3, textPaint); // �·�
        canvas.drawText(YString, xPoint + displacement, YPoint - YLength, textPaint); // ���

        // Y����
        for (int i = 0; i < 5; i++) {
            // Y���ÿ����,X��ΪxPoint-xPoint + xLength Y��̶��߶� YPoint - (i + 1) *
            // YScale(����Բ��������ֵ)
            canvas.drawLine(xPoint, YPoint - (i + 1) * YScale, xPoint + xLength, YPoint - (i + 1)* YScale, linePaint); // �̶�
            try {
                // Y��Ŀ̶�ֵ,ֵΪƽ������֮���������
                canvas.drawText(String.valueOf(eachYLabel * (i + 1)), 5, YPoint - (i + 1) * YScale+ 5, textPaint); // ����
            } catch (Exception e) {
            	e.printStackTrace();
            }
        }
        canvas.drawText("0", 10, YPoint + displacement * 3, textPaint); // ����
        for (int i = 0; i < XLabel.length; i++) {
            try {
                // X���ÿ���̶���
                canvas.drawLine(xPoint + (i + 1) * xScale, YPoint, xPoint + (i + 1) * xScale,YPoint - displacement, linePaint);
                // X����ʾ�Ŀ̶�ֵ
                canvas.drawText(XLabel[i], xPoint + (i + 1) * xScale - halfXLabel, YPoint+ displacement* 3, textPaint); // ����
                PointBean bean = new PointBean();
                // ���֮��ľ���ȡ��
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
            // �����ʱ��,������� ����ʾ����
            canvas.drawCircle(selectX, YCoord(selectData), circleSize, selectCirclePaint);
            canvas.drawText( selectData+XYString, selectX + displacement, YCoord(selectData)- displacement, selectCirclePaint);
//            canvas.drawText("ʱ�䣺12:27 ���ʣ�80��/����", xPoint, YPoint+80, selectInforPaint);
            //������²���
            if("H".equals(sign)){
            	xingzouvalue.setText(selectBushu);
            //��ֹ�������Ѵ���
            }else if("J".equals(sign)){
            	tixingvalue.setText(selectData);
            }
        }
    }

    /**
     * ����Y����
     * 
     * @param y0
     * @return
     */
    private float YCoord(String y0) // �������ʱ��Y���꣬������ʱ����-999
    {
        float y;
        try {
            y = Float.parseFloat(y0);
        } catch (Exception e) {
            return -999; // �����򷵻�-999
        }
        try {
            // YScale/eachYLabelΪ���� ����y�õ�����Բ��ľ���
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
     * ����Y����������ֵ
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
        int length = (int) (max) / 20 + 1;// Ϊ��ȡ���� �������ֵΪ39��ʱ�� ����40
        return length * 20;
    }

}
