package com.coldzify.finalproject.Dialog;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.coldzify.finalproject.ProblemType;
import com.coldzify.finalproject.R;

public class SelectTypeDialog extends DialogFragment implements View.OnClickListener{
    private TextView electric,water,pollution,material,clean,security,environment,traffic,building;
    private onItemClickListener listener;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.select_type_layout,null);
        //upload_textView = view.findViewById(R.id.upload_textView);
        electric = view.findViewById(R.id.electric_textView);
        water = view.findViewById(R.id.water_textView);
        pollution = view.findViewById(R.id.pollution_textView);
        material = view.findViewById(R.id.material_textView);
        clean = view.findViewById(R.id.clean_textView);
        security = view.findViewById(R.id.security_textView);
        environment = view.findViewById(R.id.environment_textView);
        traffic = view.findViewById(R.id.traffic_textView);
        building = view.findViewById(R.id.building_textView);

        Drawable water_ic = AppCompatResources.getDrawable(getContext(), R.drawable.ic_water);
        water.setCompoundDrawablesWithIntrinsicBounds(null,water_ic,null,null);
        Drawable material_ic = AppCompatResources.getDrawable(getContext(), R.drawable.ic_material);
        material.setCompoundDrawablesWithIntrinsicBounds(null,material_ic,null,null);
        Drawable clean_ic = AppCompatResources.getDrawable(getContext(), R.drawable.ic_clean);
        clean.setCompoundDrawablesWithIntrinsicBounds(null,clean_ic,null,null);
        Drawable animal_ic = AppCompatResources.getDrawable(getContext(), R.drawable.ic_environment);
        environment.setCompoundDrawablesWithIntrinsicBounds(null,animal_ic,null,null);

        electric.setOnClickListener(this);
        water.setOnClickListener(this);
        pollution.setOnClickListener(this);
        material.setOnClickListener(this);
        clean.setOnClickListener(this);
        security.setOnClickListener(this);
        environment.setOnClickListener(this);
        traffic.setOnClickListener(this);
        building.setOnClickListener(this);
        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        // handles https://code.google.com/p/android/issues/detail?id=17423
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View view) {
        if(listener == null)
            return;
        dismiss();
        int id = view.getId();
        switch (id){
            case R.id.electric_textView:
                listener.onItemClick(ProblemType.ELECTRICS);
                break;
            case R.id.water_textView:
                listener.onItemClick(ProblemType.WATER);
                break;
            case R.id.pollution_textView:
                listener.onItemClick(ProblemType.POLLUTION);
                break;
            case R.id.material_textView:
                listener.onItemClick(ProblemType.MATERIAL);
                break;
            case R.id.clean_textView:
                listener.onItemClick(ProblemType.CLEAN);
                break;
            case R.id.security_textView:
                listener.onItemClick(ProblemType.SECURITY);
                break;
            case R.id.environment_textView:
                listener.onItemClick(ProblemType.ENVIRONMENT);
            break;
            case R.id.traffic_textView:
                listener.onItemClick(ProblemType.TRAFFIC);
                break;
            case R.id.building_textView:
                listener.onItemClick(ProblemType.BUILDING);
                break;
        }
    }

    public void setOnClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public interface onItemClickListener{
        void onItemClick(ProblemType type);
    }
}
