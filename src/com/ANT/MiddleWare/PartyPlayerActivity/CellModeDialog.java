package com.ANT.MiddleWare.PartyPlayerActivity;

import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.ANT.MiddleWare.Celluar.CellularDown.CellType;

public class CellModeDialog extends DialogFragment {
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
		View v = inflater.inflate(R.layout.fragment_cellmode, null);
		getDialog().setTitle(R.string.alerttitle);

		radioGroup = (RadioGroup) v.findViewById(R.id.radioGroup3);
		btConfirm = (Button) v.findViewById(R.id.btalertconfirm3);

		CellType defCell = MainFragment.configureData.getDefCell();
		switch (defCell) {
		case Single:
			rbButton = (RadioButton) v.findViewById(R.id.single);
			rbButton.setChecked(true);
			break;
		case GROUP:
			rbButton = (RadioButton) v.findViewById(R.id.group);
			rbButton.setChecked(true);
			break;
		case DASH:
			rbButton = (RadioButton) v.findViewById(R.id.dash);
			rbButton.setChecked(true);
			break;
		case NOCELL:
			rbButton = (RadioButton) v.findViewById(R.id.nocell);
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
				case R.id.single:
					MainFragment.configureData
							.setDefCell(CellType.Single);
					break;
				case R.id.group:
					MainFragment.configureData
							.setDefCell(CellType.GROUP);
					break;
				case R.id.dash:
					MainFragment.configureData
							.setDefCell(CellType.DASH);
					break;
				case R.id.nocell:
					MainFragment.configureData
							.setDefCell(CellType.NOCELL);
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
