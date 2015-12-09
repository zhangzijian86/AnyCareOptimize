package com.zbar.lib.decode;

import android.app.Activity;
import android.content.DialogInterface;

/**
 * ����: ����(1076559197@qq.com)
 * 
 * ʱ��: 2014��5��9�� ����12:24:51
 *
 * �汾: V_1.0.0
 *
 */
public final class FinishListener
    implements DialogInterface.OnClickListener, DialogInterface.OnCancelListener, Runnable {

  private final Activity activityToFinish;

  public FinishListener(Activity activityToFinish) {
    this.activityToFinish = activityToFinish;
  }

  public void onCancel(DialogInterface dialogInterface) {
    run();
  }

  public void onClick(DialogInterface dialogInterface, int i) {
    run();
  }

  public void run() {
    activityToFinish.finish();
  }

}
