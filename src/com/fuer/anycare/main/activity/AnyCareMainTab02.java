package com.fuer.anycare.main.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.fuer.main.anycare.R;

@SuppressLint("NewApi")
public class AnyCareMainTab02 extends Fragment{

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.main_tab_02, container, false);
	}
}
