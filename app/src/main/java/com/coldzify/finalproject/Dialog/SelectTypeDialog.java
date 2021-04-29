package com.coldzify.finalproject.Dialog;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.coldzify.finalproject.ProblemType;
import com.coldzify.finalproject.R;

public class SelectTypeDialog extends DialogFragment implements View.OnClickListener{
    private TextView electric,water,conditioner,material,technology,internet,building_environ,clean_security,telephone, others;
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
        conditioner = view.findViewById(R.id.conditioner_textView);
        material = view.findViewById(R.id.material_textView);
        technology = view.findViewById(R.id.technology_textView);
        internet = view.findViewById(R.id.internet_textView);
        building_environ = view.findViewById(R.id.building_and_environment_textView);
        clean_security = view.findViewById(R.id.clean_and_security_textView);
        telephone = view.findViewById(R.id.telephone_textView);
        others = view.findViewById(R.id.others_textView);

        Drawable water_ic = AppCompatResources.getDrawable(getContext(), R.drawable.ic_water);
        water.setCompoundDrawablesWithIntrinsicBounds(null,water_ic,null,null);
        Drawable material_ic = AppCompatResources.getDrawable(getContext(), R.drawable.ic_material);
        material.setCompoundDrawablesWithIntrinsicBounds(null,material_ic,null,null);
        Drawable clean_security_ic = AppCompatResources.getDrawable(getContext(), R.drawable.ic_clean_security);
        clean_security.setCompoundDrawablesWithIntrinsicBounds(null,clean_security_ic,null,null);
        Drawable building_environ_ic = AppCompatResources.getDrawable(getContext(), R.drawable.ic_building_and_environment);
        building_environ.setCompoundDrawablesWithIntrinsicBounds(null,building_environ_ic,null,null);

        electric.setOnClickListener(this);
        water.setOnClickListener(this);
        conditioner.setOnClickListener(this);
        material.setOnClickListener(this);
        technology.setOnClickListener(this);
        internet.setOnClickListener(this);
        building_environ.setOnClickListener(this);
        clean_security.setOnClickListener(this);
        telephone.setOnClickListener(this);
        others.setOnClickListener(this);
//        internet_wiring.setOnClickListener(this);
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
            case R.id.conditioner_textView:
                listener.onItemClick(ProblemType.CONDITIONER);
                break;
            case R.id.material_textView:
                listener.onItemClick(ProblemType.MATERIAL);
                break;
            case R.id.technology_textView:
                listener.onItemClick(ProblemType.TECHNOLOGY);
                break;
            case R.id.internet_textView:
                listener.onItemClick(ProblemType.INTERNET);
                break;
            case R.id.building_and_environment_textView:
                listener.onItemClick(ProblemType.BUILDING_ENVIRON);
                break;
            case R.id.clean_and_security_textView:
                listener.onItemClick(ProblemType.CLEAN_SECURITY);
                break;
            case R.id.telephone_textView:
                listener.onItemClick(ProblemType.TELEPHONE);
                break;
            case R.id.others_textView:
                listener.onItemClick(ProblemType.OTHERS);
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
