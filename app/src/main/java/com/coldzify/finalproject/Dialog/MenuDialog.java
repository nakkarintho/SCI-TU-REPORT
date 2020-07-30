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
import android.view.LayoutInflater;
import android.view.View;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coldzify.finalproject.ChecklistActivity;
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

public class MenuDialog extends DialogFragment {
    private String userType = "normal";
    private String user_name;
    private RecyclerView recyclerView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null){
            userType = getArguments().getString("userType");
            user_name = getArguments().getString("user_name");
        }

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //setCancelable(false);
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MenuDialog);
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.menu_dialog,null);
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

    public void init(View view){
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
        ArrayList<String> menu = new ArrayList<>();
        ArrayList<Integer> drawables = new ArrayList<>();

        menu.add(getString(R.string.view_profile_th));
        drawables.add(R.drawable.ic_profile);
        menu.add(getString(R.string.all_problem_in_faculty));
        drawables.add(R.drawable.ic_allreport_icon);
        if(userType.equals("housekeeper")){
            menu.add(getString(R.string.check_list_th));
            drawables.add(R.drawable.ic_report_status);
        }
        menu.add(getString(R.string.contact_housekeeper_th));
        drawables.add(R.drawable.ic_housekeeper);
        menu.add(getString(R.string.setting_th));
        drawables.add(R.drawable.ic_settings);
        menu.add(getString(R.string.logout_th));
        drawables.add(R.drawable.ic_logout);


        recyclerView.setAdapter(new MyAdapter(menu,drawables));

    }
    void logout(){
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
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK |Intent.FLAG_ACTIVITY_CLEAR_TOP);
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


        public MyAdapter(ArrayList<String> dataSet,ArrayList<Integer> drawables) {
            this.list = dataSet;
            this.drawables= drawables;
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
            holder.view.setCompoundDrawablesWithIntrinsicBounds(null,icon,null,null);
            holder.view.setOnClickListener(onClickListener);
        }

        private View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView textView = (TextView)view;
                String text = textView.getText().toString();
                //String report_menu = getString(R.string.report_problem2_th);
                String profile_menu = getString(R.string.view_profile_th);
                String all_problem_menu = getString(R.string.all_problem_in_faculty);
                String check_list_menu = getString(R.string.check_list_th);
                String housekeeper_menu = getString(R.string.contact_housekeeper_th);
                String setting_menu = getString(R.string.setting_th);
                //String logout_menu = getString(R.string.logout_th);
                if(text.equals(profile_menu)){
                    dismiss();
                    Intent intent = new Intent(getContext(), ProfileActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);

                }
                else if(text.equals(all_problem_menu)){
                    dismiss();
                    Intent intent = new Intent(getContext(), FeedActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else if(text.equals(check_list_menu)){
                    dismiss();
                    Intent intent = new Intent(getContext(), ChecklistActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else if(text.equals(housekeeper_menu)){
                    dismiss();
                    Intent intent = new Intent(getContext(), SearchHousekeeperActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else if(text.equals(setting_menu)){
                    dismiss();
                    Intent intent = new Intent(getContext(), SettingActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                }
                else{
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
}
