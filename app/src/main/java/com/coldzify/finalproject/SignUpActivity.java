package com.coldzify.finalproject;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.coldzify.finalproject.Dialog.ProgressDialog;
import com.coldzify.finalproject.dataobject.UserProfile;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class SignUpActivity extends AppCompatActivity {
    private final String TAG = "SignUpActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private EditText email_editText,password_editText,firstName_editText,lastName_editText
            ,birthdate_editText,confirm_password_editText;
    private TextView firstName_err_textView,lastName_err_textView,birthdate_err_textView,email_err_textView
            ,password_err_textView,confirm_password_err_textView;
    private RadioButton normal_radio,staff_radio;

    private ProgressDialog dialog;
    private Calendar myCalendar;
    private DatePickerDialog.OnDateSetListener date;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        email_editText= findViewById(R.id.email_editText);
        password_editText = findViewById(R.id.password_editText);
        confirm_password_editText = findViewById(R.id.confirmPassword_editText);
        firstName_editText = findViewById(R.id.firstname_editText);
        lastName_editText = findViewById(R.id.lastname_editText);
        birthdate_editText = findViewById(R.id.birthdate_editText);
        normal_radio = findViewById(R.id.normal_radio);
        staff_radio = findViewById(R.id.staff_radio);
        //female_radio_button = findViewById(R.id.female_radioButton);

        firstName_err_textView = findViewById(R.id.firstname_error_textView);
        lastName_err_textView = findViewById(R.id.lastname_error_textView);
        birthdate_err_textView = findViewById(R.id.birthdate_error_textView);
        email_err_textView = findViewById(R.id.email_error_textView);
        password_err_textView = findViewById(R.id.password_error_textView);
        confirm_password_err_textView = findViewById(R.id.confirmPassword_error_textView);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        dialog = new ProgressDialog();
        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "dd/MM/yyyy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
                birthdate_editText.setText(sdf.format(myCalendar.getTime()));
            }

        };

        birthdate_editText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(SignUpActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });


    }




    private void signUp(String email,String password){
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            String first_name = firstName_editText.getText().toString();
                            String last_name = lastName_editText.getText().toString();
                            String userType;
                            if(staff_radio.isChecked())
                                userType ="staff";
                            else if(normal_radio.isChecked())
                                userType = "normal";
                            else
                                userType ="housekeeper";


                            String birthdate = birthdate_editText.getText().toString();
                            String email = email_editText.getText().toString();
                            UserProfile user = new UserProfile(currentUser.getUid(),first_name,last_name
                                    ,email,birthdate,"user_default.jpg",userType);
                            addUser(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            dialog.dismiss();
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            if(task.getException() instanceof FirebaseAuthUserCollisionException){
                                email_err_textView.setVisibility(View.VISIBLE);
                                email_err_textView.setText(R.string.duplicate_email_th);
                            }

                        }

                        // ...
                    }
                });

    }
    private void addUser(final UserProfile user){
        db.collection("users").document(user.getUid())
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User : "+user.getFirstname()+ " successfully written!");
                        dialog.dismiss();
                        Toast.makeText(getApplicationContext(),"สมัครสมาชิกเรียบร้อยแล้ว",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(SignUpActivity.this,ReportActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                        //System.out.println("Success adding id :"+uid);

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Log.w(TAG, "Error adding user ", e);
                        //System.out.println(e.getMessage());
                    }
                });
    }
    public void onSignUp(View view){
        dialog.show(getSupportFragmentManager(),"Loading dialog");
        String email = email_editText.getText().toString();
        String password = password_editText.getText().toString();
        if(validateForm()){
            signUp(email,password);
        }
        else{
            dialog.dismiss();
        }
    }

    private boolean validateForm(){
        clearErrorText();
        String first_name = firstName_editText.getText().toString();
        String last_name = lastName_editText.getText().toString();
        String birthdate = birthdate_editText.getText().toString();
        String email = email_editText.getText().toString();
        String password = password_editText.getText().toString();
        String confirm_password = confirm_password_editText.getText().toString();
        if(first_name.length() == 0){
            firstName_err_textView.setVisibility(View.VISIBLE);
            return false;
        }
        if(last_name.length() == 0){
            lastName_err_textView.setVisibility(View.VISIBLE);
            return false;
        }
        if(birthdate.length() == 0){
            birthdate_err_textView.setVisibility(View.VISIBLE);
            return false;
        }
        if(!isValidEmail(email)){
            email_err_textView.setVisibility(View.VISIBLE);
            return false;
        }
        if(password.length() < 6 ){
            password_err_textView.setVisibility(View.VISIBLE);
            return false;
        }
        if(!confirm_password.equals(password)){
            confirm_password_err_textView.setVisibility(View.VISIBLE);
            return false;
        }
        return  true;
    }

    private void clearErrorText(){
        firstName_err_textView.setVisibility(View.INVISIBLE);
        lastName_err_textView.setVisibility(View.INVISIBLE);
        birthdate_err_textView.setVisibility(View.INVISIBLE);
        email_err_textView.setVisibility(View.INVISIBLE);
        password_err_textView.setVisibility(View.INVISIBLE);
        confirm_password_err_textView.setVisibility(View.INVISIBLE);
    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }

    public void onClickArrowBack(View view){
        finish();
    }


}
