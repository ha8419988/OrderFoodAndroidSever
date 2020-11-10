package com.example.orderfoodandroidsever;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SwitchCompat;

import com.example.orderfoodandroidsever.Common.Common;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import static com.example.orderfoodandroidsever.Common.Common.IS_FACE_ID;

public class BottomSheetFragment extends BottomSheetDialogFragment {

    private SharedPreferences sharedPreferences;
    SwitchCompat switchCompat;
    public static final String SHARED_PREFERENCE_NAME = "SettingGame";


    public BottomSheetFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragmenta
        return inflater.inflate(R.layout.dialog_setting, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        switchCompat = getView().findViewById(R.id.switchCompat);
        sharedPreferences = getActivity().
                getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        boolean isVolume = sharedPreferences.getBoolean(IS_FACE_ID, false);
        switchCompat.setChecked(isVolume);
        switchCompat.setTrackTintList(switchCompat.isChecked() ?
                (ColorStateList.valueOf(Color.parseColor("#0CEBF3"))) :
                (ColorStateList.valueOf(Color.parseColor("#929697"))));
        switchCompat.setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton compoundButton,
                                                 boolean b) {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        if (switchCompat.isChecked()) {
                            editor.putBoolean(IS_FACE_ID, true);
                            switchCompat.setTrackTintList
                                    (ColorStateList.valueOf(Color.parseColor("#0CEBF3")));
                            Common.saveData(getActivity(), Common.current_user);
                        } else {
                            editor.putBoolean(IS_FACE_ID, false);
                            switchCompat.setTrackTintList
                                    (ColorStateList.valueOf(Color.parseColor("#929697")));
                            Common.saveData(getActivity(), null);
                        }
                        editor.commit();

                    }
                });
    }
}
