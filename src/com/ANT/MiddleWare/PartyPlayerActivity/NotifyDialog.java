package com.ANT.MiddleWare.PartyPlayerActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class NotifyDialog extends DialogFragment {
	private static final String TAG = SettingDialog.class.getSimpleName();

	private RadioGroup radioGroup;
	private Button btConfirm;
	private RadioButton rbButton;

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();

		Dialog dialog = getDialog();
		if (dialog != null) {
			DisplayMetrics dm = new DisplayMetrics();
			getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
			dialog.getWindow().setLayout((int) (dm.widthPixels * 0.80),
					ViewGroup.LayoutParams.WRAP_CONTENT);
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View v = inflater.inflate(R.layout.fragment_notify, null);
		getDialog().setTitle(R.string.alerttitle);

		radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup4);
		btConfirm = (Button) v.findViewById(R.id.btalertconfirm4);

		boolean notifySwitch = MainFragment.configureData.isNoNotify();
		int onOff = notifySwitch?0:1;
		switch (onOff) {
		case 1:
			rbButton = (RadioButton) v.findViewById(R.id.Notify_on);
			rbButton.setChecked(true);
			break;
		case 0:
			rbButton = (RadioButton) v.findViewById(R.id.Notify_off);
			rbButton.setChecked(true);
			break;
		default:
			break;
		}

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int selectedId = radioGroup.getCheckedRadioButtonId();

				switch (selectedId) {
				case R.id.Notify_on:
					MainFragment.configureData
							.setNoNotify(false);
//					MainFragment.configureData
//							.setNoWiFiSend(false);
					break;
				case R.id.Notify_off:
					MainFragment.configureData
							.setNoNotify(true);
//					MainFragment.configureData
//							.setNoWiFiSend(false);
					Log.v(TAG, ""+MainFragment.configureData.getDefMore());
					break;
				}
			}
		});

		btConfirm.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
			}
		});

		return v;
	}

}
