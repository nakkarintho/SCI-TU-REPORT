package com.coldzify.finalproject;

import android.annotation.SuppressLint;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.coldzify.finalproject.Dialog.ProgressDialog;
import com.coldzify.finalproject.dataobject.UserProfile;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    //private ProfilePictureView profilePictureView;
    private AccessToken accessToken;
    private boolean isLoggedIn;
    private Profile userProfile;
    private FirebaseAuth mAuth;
    private FirebaseStorage storage;
    private FirebaseFirestore db;
    private static final String TAG = "LoginActivity";
    private String id,first_name,last_name,gender,birthday,email;
    private FirestoreController fCon = new FirestoreController();
    private Button login_button;
    private ProgressDialog dialog;
    private EditText email_editText,password_editText;
    private TextView email_err_textView,password_err_textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        email_editText= findViewById(R.id.email_editText);
        password_editText = findViewById(R.id.password_editText);
        email_err_textView = findViewById(R.id.email_error_textView);
        password_err_textView = findViewById(R.id.password_error_textView);
        login_button = findViewById(R.id.login_button);
        //hideProgressBar();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        accessToken = AccessToken.getCurrentAccessToken();
        isLoggedIn = accessToken != null && !accessToken.isExpired();
        if (mAuth.getCurrentUser() != null){
            goNextActivity();
        }

        //profilePictureView =  findViewById(R.id.friendProfilePicture);
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d(TAG, "Login");
                        accessToken = loginResult.getAccessToken();
                        handleFacebookAccessToken(accessToken);

                    }

                    @Override
                    public void onCancel() {
                        Toast.makeText(LoginActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(LoginActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                        dialog.dismiss();
                    }
                });


       /* try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.coldzify.finalproject",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {
            Log.d("KeyHash:", e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            Log.d("KeyHash:", e.getMessage());
        }*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void goNextActivity(){
        Intent profileIntent = new Intent(LoginActivity.this,ReportActivity.class);
        startActivity(profileIntent);
        //profileIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
    }
    public void onClickLogin(View v){

            login();


    }


    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            final String uid = mAuth.getUid();
                            fCon.checkDuplicateUser(uid, new FirestoreCallBack() {
                                @Override
                                public void onQueryListComplete(ArrayList<?> list) { }
                                @Override
                                public void onCheckDuplicateComplete(boolean isDuplicate) {
                                    if(!isDuplicate){
                                        //Toast.makeText(getApplicationContext(),"aaa",Toast.LENGTH_SHORT).show();
                                        getDataProfile();
                                    }else{
                                        dialog.dismiss();
                                        goNextActivity();
                                    }
                                }
                            });
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());

                            dialog.dismiss();
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    public void getDataProfile(){
        GraphRequest request =  GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {

                        if (response.getError() != null) {
                            dialog.dismiss();
                            Toast.makeText(getBaseContext(),response.getError().getErrorMessage(),Toast.LENGTH_LONG).show();
                        } else {

                            id = object.optString("id");
                            first_name = object.optString("first_name");
                            last_name = object.optString("last_name");
                            gender = object.optString("gender");
                            birthday = object.optString("birthday");
                            email =response.getJSONObject().optString("email");

                            String urlPic;
                            try {
                                urlPic = object.getJSONObject("picture").getJSONObject("data").getString("url");
                                downloadUserPicture(urlPic);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            //UserProfile user = new UserProfile(id,name,gender,email,birthday,)

                        }
                    }
                });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "first_name,last_name,gender,email,birthday,picture.type(normal)");
        request.setParameters(parameters);
        request.executeAsync();
    }
    @SuppressLint("CheckResult")
    private void downloadUserPicture(String url){
        GlideApp.with(this)
                .asBitmap()
                .load(url)
                .listener(new RequestListener<Bitmap>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Bitmap> target, boolean isFirstResource) {
                        Log.w(TAG,"Error: ",e);
                        dialog.dismiss();
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Bitmap bitmap, Object model, Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                        uploadUserPicture(bitmap);

                        return false;
                    }
                }).submit();


    }
    private void uploadUserPicture(Bitmap bitmap){
        String fileName = "user_"+id+".jpg";
        storage = FirebaseStorage.getInstance();
        StorageReference imageRef = storage.getReference().child("images/").child("users/"+fileName);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = imageRef.putBytes(data);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.w(TAG,"Error: ",exception);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                String picName = taskSnapshot.getMetadata().getName();
                final UserProfile user = new UserProfile(mAuth.getUid(),first_name,last_name,email,birthday,picName,"normal");
                addUser(user);
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
                        goNextActivity();
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


    private void login(){
        showProgressDialog();
        LoginManager.getInstance().logInWithReadPermissions(this,Arrays.asList(
                "public_profile","user_gender","user_birthday","email"));
    }

    private void showProgressDialog(){
        dialog = new ProgressDialog();
        dialog.show(getSupportFragmentManager(), "Progress");

    }

    public void onLoginWithEmail(View view){
        String email= email_editText.getText().toString();
        String password = password_editText.getText().toString();
        showProgressDialog();
        if(validateForm()){
            signIn(email,password);
        }
        else{
            dialog.dismiss();
        }
    }
    private void signIn(String email,String password){
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        dialog.dismiss();
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "loginUserWithEmail:success");
                            goNextActivity();

                        } else {
                            // If sign in fails, display a message to the user.

                            Log.w(TAG, "loginUserWithEmail:failure", task.getException());

                            if(task.getException() instanceof FirebaseAuthInvalidCredentialsException){
                                password_err_textView.setVisibility(View.VISIBLE);
                            }
                            else if(task.getException() instanceof FirebaseNetworkException){
                                Toast.makeText(getApplicationContext(),"ไม่สามารถเชื่อมมต่อได้",Toast.LENGTH_LONG).show();
                            }
                            else{
                                email_err_textView.setVisibility(View.VISIBLE);
                            }

                        }

                        // ...
                    }
                });
    }
    private boolean validateForm(){
        clearErrorText();

        String email = email_editText.getText().toString();
        String password = password_editText.getText().toString();


        if(!isValidEmail(email)){
            email_err_textView.setVisibility(View.VISIBLE);

            return false;
        }
        if(password.length() == 0 ){
            password_err_textView.setVisibility(View.VISIBLE);
            return false;
        }

        return  true;
    }
    private void clearErrorText(){
        email_err_textView.setVisibility(View.INVISIBLE);
        password_err_textView.setVisibility(View.INVISIBLE);

    }
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
    public void onClickSignUp(View view){
        Intent intent = new Intent(this,SignUpActivity.class);
        startActivity(intent);

    }

}
