package com.coldzify.finalproject.Dialog;


import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.content.res.AppCompatResources;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coldzify.finalproject.ChecklistActivity;
import com.coldzify.finalproject.EditroleActivity;
import com.coldzify.finalproject.FinishActivity;
import com.coldzify.finalproject.OneChecklistActivity;
import com.coldzify.finalproject.SearchHousekeeperActivity;
import com.coldzify.finalproject.FeedActivity;
import com.coldzify.finalproject.LoginActivity;
import com.coldzify.finalproject.ProfileActivity;
import com.coldzify.finalproject.R;

import com.coldzify.finalproject.SettingActivity;
import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MenuDialog extends DialogFragment {
    private String role = "ผู้ใช้ทั่วไป";
    private String user_name, uid;
    private RecyclerView recyclerView;
    ArrayList<String> menu = new ArrayList<>();
    ArrayList<Integer> drawables = new ArrayList<>();
    Map<String, Boolean> viewProfileMenu = new HashMap<>();
    Map<String, Boolean> allProblemMenu = new HashMap<>();
    Map<String, Boolean> finishProblemMenu = new HashMap<>();
    Map<String, Boolean> checkListMenu = new HashMap<>();
    Map<String, Boolean> contactMenu = new HashMap<>();
    Map<String, Boolean> editUserTypeMenu = new HashMap<>();
    Map<String, Boolean> settingMenu = new HashMap<>();
    Map<String, Boolean> logoutMenu = new HashMap<>();
    Boolean round = true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            role = getArguments().getString("role");
            user_name = getArguments().getString("user_name");
            uid = getArguments().getString("uid");
        }
        createcheckMenu();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setCancelable(false);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MenuDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.menu_dialog, null);
        builder.setView(view);
        init(view);

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

    public void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        ImageView close_imageView = view.findViewById(R.id.close_button);
        close_imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();

            }
        });

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        if(round == true) {



            if (viewProfileMenu.get(role) == true) {
                menu.add(getString(R.string.view_profile_th));
                drawables.add(R.drawable.ic_profile);
            }

            if (allProblemMenu.get(role) == true) {
                menu.add(getString(R.string.finish_problem_in_faculty));
                drawables.add(R.drawable.ic_finishreport_icon);
            }

            if (finishProblemMenu.get(role) == true) {
                menu.add(getString(R.string.check_list_th));
                drawables.add(R.drawable.ic_report_status);
            }

            if (checkListMenu.get(role) == true) {
                menu.add(getString(R.string.contact_housekeeper_th));
                drawables.add(R.drawable.ic_housekeeper);
            }

            if (contactMenu.get(role) == true) {
                menu.add(getString(R.string.check_list_th));
                drawables.add(R.drawable.ic_report_status);
            }

            if (editUserTypeMenu.get(role) == true) {
                menu.add(getString(R.string.edit_user_type_th));
                drawables.add(R.drawable.ic_edit_user_type);
            }

            if (settingMenu.get(role) == true) {
                menu.add(getString(R.string.setting_th));
                drawables.add(R.drawable.ic_settings);
            }

            if (logoutMenu.get(role) == true) {
                menu.add(getString(R.string.logout_th));
                drawables.add(R.drawable.ic_logout);
            }

            round = false;
        }

        recyclerView.setAdapter(new MyAdapter(menu,drawables));

    }

    void logout() {
        String logout = getResources().getString(
                R.string.com_facebook_loginview_log_out_action);
        String cancel = getResources().getString(
                R.string.com_facebook_loginview_cancel_action);
        String message;

        message = String.format(
                getResources().getString(
                        R.string.com_facebook_loginview_logged_in_as),
                user_name);
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getContext());
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton(logout, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        //getFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        Intent intent = new Intent(getContext(), LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();
                    }
                })
                .setNegativeButton(cancel, null);
        builder.create().show();
    }

    class MyAdapter extends RecyclerView.Adapter<MyViewHolder> {
        ArrayList<String> list;
        ArrayList<Integer> drawables;


        public MyAdapter(ArrayList<String> dataSet, ArrayList<Integer> drawables) {
            this.list = dataSet;
            this.drawables = drawables;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            TextView tileView = (TextView) LayoutInflater.from(parent.getContext()).inflate(R.layout.menu_dialog_item, parent, false);


            return new MyViewHolder(tileView);
        }

        @Override
        public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
            holder.view.setText(list.get(position));
            Context context = holder.view.getContext();
            Drawable icon = AppCompatResources.getDrawable(context, drawables.get(position));
            holder.view.setCompoundDrawablesWithIntrinsicBounds(null, icon, null, null);
            holder.view.setOnClickListener(onClickListener);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView) view;
                String text = textView.getText().toString();
                //String report_menu = getString(R.string.report_problem2_th);
                String profile_menu = getString(R.string.view_profile_th);
                String all_problem_menu = getString(R.string.all_problem_in_faculty);
                String finish_problem_menu = getString(R.string.finish_problem_in_faculty);
                String check_list_menu = getString(R.string.check_list_th);
                String housekeeper_menu = getString(R.string.contact_housekeeper_th);
                String edit_role_menu = getString(R.string.edit_user_type_th);
                String setting_menu = getString(R.string.setting_th);
                //String logout_menu = getString(R.string.logout_th);
                if (text.equals(profile_menu)) {
                    dismiss();
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                } else if (text.equals(all_problem_menu)) {
                    dismiss();
                    Intent intent = new Intent(getContext(), FeedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if (text.equals(finish_problem_menu)) {
                    dismiss();
                    Intent intent = new Intent(getContext(), FinishActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if (text.equals(check_list_menu)) {
                    dismiss();
                    Intent intent = new Intent(getContext(), ChecklistActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if (text.equals(housekeeper_menu)) {
                    dismiss();
                    Intent intent = new Intent(getContext(), SearchHousekeeperActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if (text.equals(edit_role_menu)) {
                    dismiss();
                    Intent intent = new Intent(getContext(), EditroleActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else if (text.equals(setting_menu)) {
                    dismiss();
                    Intent intent = new Intent(getContext(), SettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                } else {
                    logout();
                }
            }
        };


        @Override
        public int getItemCount() {
            return list.size();
        }
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView view;

        MyViewHolder(TextView itemView) {
            super(itemView);
            view = itemView;
        }
    }

    void createcheckMenu() {

        viewProfileMenu.put("ผู้ใช้ทั่วไป", false);
        viewProfileMenu.put("ผู้ดูแลห้องเรียน", false);
        viewProfileMenu.put("เจ้าหน้าที่", false);
        viewProfileMenu.put("หัวหน้างาน", false);
        viewProfileMenu.put("ผู้บริหาร", false);
        viewProfileMenu.put("ผู้ดูแลระบบ", true);

        allProblemMenu.put("ผู้ใช้ทั่วไป", true);
        allProblemMenu.put("ผู้ดูแลห้องเรียน", true);
        allProblemMenu.put("เจ้าหน้าที่", true);
        allProblemMenu.put("หัวหน้างาน", true);
        allProblemMenu.put("ผู้บริหาร", true);
        allProblemMenu.put("ผู้ดูแลระบบ", true);

        finishProblemMenu.put("ผู้ใช้ทั่วไป", true);
        finishProblemMenu.put("ผู้ดูแลห้องเรียน", true);
        finishProblemMenu.put("เจ้าหน้าที่", true);
        finishProblemMenu.put("หัวหน้างาน", true);
        finishProblemMenu.put("ผู้บริหาร", true);
        finishProblemMenu.put("ผู้ดูแลระบบ", true);

        checkListMenu.put("ผู้ใช้ทั่วไป", false);
        checkListMenu.put("ผู้ดูแลห้องเรียน", true);
        checkListMenu.put("เจ้าหน้าที่", false);
        checkListMenu.put("หัวหน้างาน", false);
        checkListMenu.put("ผู้บริหาร", false);
        checkListMenu.put("ผู้ดูแลระบบ", false);

        contactMenu.put("ผู้ใช้ทั่วไป", true);
        contactMenu.put("ผู้ดูแลห้องเรียน", true);
        contactMenu.put("เจ้าหน้าที่", true);
        contactMenu.put("หัวหน้างาน", true);
        contactMenu.put("ผู้บริหาร", true);
        contactMenu.put("ผู้ดูแลระบบ", true);

        editUserTypeMenu.put("ผู้ใช้ทั่วไป", false);
        editUserTypeMenu.put("ผู้ดูแลห้องเรียน", false);
        editUserTypeMenu.put("เจ้าหน้าที่", false);
        editUserTypeMenu.put("หัวหน้างาน", false);
        editUserTypeMenu.put("ผู้บริหาร", false);
        editUserTypeMenu.put("ผู้ดูแลระบบ", true);

        settingMenu.put("ผู้ใช้ทั่วไป", true);
        settingMenu.put("ผู้ดูแลห้องเรียน", true);
        settingMenu.put("เจ้าหน้าที่", true);
        settingMenu.put("หัวหน้างาน", true);
        settingMenu.put("ผู้บริหาร", true);
        settingMenu.put("ผู้ดูแลระบบ", true);

        logoutMenu.put("ผู้ใช้ทั่วไป", true);
        logoutMenu.put("ผู้ดูแลห้องเรียน", true);
        logoutMenu.put("เจ้าหน้าที่", true);
        logoutMenu.put("หัวหน้างาน", true);
        logoutMenu.put("ผู้บริหาร", true);
        logoutMenu.put("ผู้ดูแลระบบ", true);

    }
}








