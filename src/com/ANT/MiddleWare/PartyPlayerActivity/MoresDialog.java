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

import com.ANT.MiddleWare.Celluar.CellularDown.CellType;

public class MoresDialog extends DialogFragment {
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
		View v = inflater.inflate(R.layout.fragment_mores, null);
		getDialog().setTitle(R.string.alerttitle);

		radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup1);
		btConfirm = (Button) v.findViewById(R.id.btalertconfirm1);

		CellType defMore = MainFragment.configureData.getDefMore();
		switch (defMore) {
		case CellMore:
			rbButton = (RadioButton) v.findViewById(R.id.cell_More);
			rbButton.setChecked(true);
			break;
		case WiFiMore:
			rbButton = (RadioButton) v.findViewById(R.id.wifi_More);
			rbButton.setChecked(true);
			break;
		case BothMore:
			rbButton = (RadioButton) v.findViewById(R.id.both_More);
			rbButton.setChecked(true);
			break;
		case NOCELL:
			rbButton = (RadioButton) v.findViewById(R.id.no_More);
			rbButton.setChecked(true);
		default:
			break;
		}

		radioGroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				int selectedId = radioGroup.getCheckedRadioButtonId();

				switch (selectedId) {
				case R.id.both_More:
					MainFragment.configureData
							.setDefMore(CellType.BothMore);
					MainFragment.configureData
							.setNoWiFiSend(false);
					break;
				case R.id.wifi_More:
					MainFragment.configureData
							.setDefMore(CellType.WiFiMore);
					MainFragment.configureData
							.setNoWiFiSend(false);
					Log.v(TAG, ""+MainFragment.configureData.getDefMore());
					break;
				case R.id.cell_More:
					MainFragment.configureData
							.setDefMore(CellType.CellMore);
					MainFragment.configureData
							.setNoWiFiSend(false);
					break;
				case R.id.no_More:
					MainFragment.configureData
							.setDefMore(CellType.NOCELL);
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
