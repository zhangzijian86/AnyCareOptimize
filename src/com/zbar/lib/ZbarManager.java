package com.zbar.lib;

/**
 * ����: ����(1076559197@qq.com)
 * 
 * ʱ��: 2014��5��9�� ����12:25:46
 *
 * �汾: V_1.0.0
 *
 * ����: zbar������
 */
public class ZbarManager {

	static {
		System.loadLibrary("zbar");
	}

	public native String decode(byte[] data, int width, int height, boolean isCrop, int x, int y, int cwidth, int cheight);
}
