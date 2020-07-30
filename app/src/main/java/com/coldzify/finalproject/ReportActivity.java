package com.coldzify.finalproject;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.ExifInterface;

import android.net.Uri;

import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;

import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.content.res.AppCompatResources;
import android.util.Log;
import android.view.View;

import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.coldzify.finalproject.Dialog.DuplicateReportDialog;
import com.coldzify.finalproject.Dialog.SelectTypeDialog;
import com.coldzify.finalproject.Dialog.UploadProgressDialog;
import com.coldzify.finalproject.adapter.SlidingImageAdapter;
import com.coldzify.finalproject.dataobject.Report;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.viewpagerindicator.CirclePageIndicator;


import java.io.File;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {
    public static final String IMAGE_DIRECTORY_NAME = "TUSCI Report";
    private static final String TAG = "ReportActivity";
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_IMAGE_PICK = 2;
    private static final int MAX_PICTURES = 4;
    private UploadProgressDialog dialog;

    private FirebaseFirestore db;

    private LocationManager locationManager;
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient fusedLocationClient;
    private Location currentLocation;
    private Uri picUri;
    private String currentPath;
    private String picName;
    private File storageDir;
    private TextView problem_type_textView,place_textView
            ,change_place_textView,change_type_textView;
    private ImageView select_type_imageView;
    private AutoCompleteTextView room_autoComplete;
    private FirebaseStorage storage;
    private EditText detail_editText;
    private ViewPager mPager;
    private SlidingImageAdapter adapter;
    private ArrayList<String> imagesPath;
    private ArrayList<String> picturesName;
    private ImageView delete_imageView;
    private CirclePageIndicator indicator;
    private static int nPictures = 0;
    private int progress = 0,place_code=-1;
    private SelectTypeDialog selectTypeDialog;
    private SelectTypeDialog.onItemClickListener listener;
    private ProblemType problemType;
    private ArrayList<Report> reports;
    private LinearLayout room_layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        db = FirebaseFirestore.getInstance();
        mPager = findViewById(R.id.image_pager);
        indicator = findViewById(R.id.indicator);
        imagesPath = new ArrayList<>();
        picturesName = new ArrayList<>();
        delete_imageView = findViewById(R.id.delete_imageView);
        room_layout = findViewById(R.id.room_layout);
        reports = new ArrayList<>();

        storage = FirebaseStorage.getInstance();
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);

        room_autoComplete = findViewById(R.id.room_autoComplete);
        String[] arr = getResources().getStringArray(R.array.br2_room);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,arr);
        room_autoComplete.setAdapter(adapter);


        detail_editText = findViewById(R.id.detail_editText);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        select_type_imageView = findViewById(R.id.select_type_imageView);
        problem_type_textView = findViewById(R.id.problem_type_textView);
        place_textView = findViewById(R.id.place_textView);

        change_type_textView = findViewById(R.id.change_type_textView);
        change_place_textView = findViewById(R.id.change_place_textView);

        if(getIntent().getExtras() != null){
            String type = getIntent().getExtras().getString("type");
            problemType =  ProblemType.valueOf(type);
            problem_type_textView.setText(toThaiString(problemType));
            select_type_imageView.setImageDrawable(problemTypeToDrawable(problemType));
            change_type_textView.setVisibility(View.VISIBLE);
        }

        getReports();
        getTokenId();

        listener = new SelectTypeDialog.onItemClickListener() {
            @Override
            public void onItemClick(ProblemType type) {
                problemType = type;
                problem_type_textView.setText(toThaiString(type));
                select_type_imageView.setImageDrawable(problemTypeToDrawable(type));
                change_type_textView.setVisibility(View.VISIBLE);
                if(currentLocation != null){
                    checkDuplicateReport(currentLocation);
                }
            }
        };

        if (savedInstanceState != null) {
            selectTypeDialog = (SelectTypeDialog) getSupportFragmentManager()
                    .findFragmentByTag("select problem type");
            if (selectTypeDialog != null) {
                selectTypeDialog.setOnClickListener(listener);
            }
        }


        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(7 * 1000); // 7 secs
        mLocationRequest.setFastestInterval(4 * 1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }


    public void onDetect(View v) {

        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


               /* if (currentLocation != null) {
                    place_code = LocationHandle.findPlace(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude()));
                    String place = LocationHandle.locationCodeToString(place_code);
                    place_textView.setText(place);
                    change_place_textView.setVisibility(View.VISIBLE);
                    user_location = currentLocation;
                    room_autoComplete.setEnabled(LocationHandle.isInBR(place_code));

                } else {
                    place_textView.setText("กำลังค้นหา . . .");
                    fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                }*/
                place_textView.setText("กำลังค้นหา . . .");
                fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

            } else {
                checkLocationPermission();
            }
        } else {
            showRequestGPSDialog();
        }

    }


    public static final int PERMISSIONS_REQUEST_LOCATION = 99;
    public static final int PERMISSIONS_REQUEST_STORAGE = 98;

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setTitle("ต้องการการอนุญาต")
                        .setMessage("แอปพลิเคชันนี้ต้องการเข้าถึงตำแหน่งของคุณ")
                        .setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(ReportActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST_LOCATION);
            }
        }
    }

    private static final int REQUEST_CHECK_SETTINGS = 31;

    public void showRequestGPSDialog() {
        /*Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);*/
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        Task<LocationSettingsResponse> task =
                LocationServices.getSettingsClient(this).checkLocationSettings(builder.build());
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);

                    // All location settings are satisfied. The client can initialize location
                    // requests here.

                } catch (ApiException exception) {
                    switch (exception.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the
                            // user a dialog.
                            try {
                                // Cast to a resolvable exception.
                                ResolvableApiException resolvable = (ResolvableApiException) exception;
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                resolvable.startResolutionForResult(
                                        ReportActivity.this,
                                        REQUEST_CHECK_SETTINGS);
                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            } catch (ClassCastException e) {
                                // Ignore, should be an impossible error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.

                            break;
                    }
                }
            }
        });


    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            List<Location> locationList = locationResult.getLocations();
            if (locationList.size() > 0) {
                //The last location in the list is the newest
                Location location = locationList.get(locationList.size() - 1);
                //Log.i("ReportActivity", "Location: " + location.getLatitude() + " " + location.getLongitude());

                place_code = LocationHandle.findPlace(new LatLng(location.getLatitude(),location.getLongitude()));
                String place = LocationHandle.locationCodeToString(place_code);
                place_textView.setText(place);
                change_place_textView.setVisibility(View.VISIBLE);
                //room_autoComplete.setEnabled(LocationHandle.isInBR(place_code));
                if(LocationHandle.isInBR(place_code)){
                    room_layout.setVisibility(View.VISIBLE);
                }
                else{
                    room_layout.setVisibility(View.GONE);
                }
                fusedLocationClient.removeLocationUpdates(mLocationCallback);
                currentLocation = location;
                checkDuplicateReport(location);

            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fusedLocationClient.removeLocationUpdates(mLocationCallback);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {
                        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "การขออนุญาตถูกปฏิเสธ", Toast.LENGTH_LONG).show();
                }
                return;
            }
            case PERMISSIONS_REQUEST_STORAGE: {

            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void pickImageFromGallery() {
        verifyStoragePermissions(this);
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    public void onClickAddImage(View view) {

        if(nPictures >= MAX_PICTURES){
            Toast.makeText(this,"เพิ่มได้สูงสุด 4 รูป",Toast.LENGTH_LONG).show();
            return;
        }
        String[] list = {"กล้องถ่ายรูป", "อัลบั้มรูปภาพ"};
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.addPicture_th)
                .setItems(list, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            takePhoto();
                        } else {
                            pickImageFromGallery();
                        }
                    }
                });
        Dialog list_dialog = builder.create();
        list_dialog.show();
    }



    private void takePhoto() {
        int sdk_version = Build.VERSION.SDK_INT;
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        verifyStoragePermissions(this);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            if (photoFile != null) {
                //fileUri = FileProvider.getUriForFile(MainActivity.this,
                //       BuildConfig.APPLICATION_ID + ".provider", f);
                picName = photoFile.getName();
                if (sdk_version <= 23)
                    picUri = Uri.fromFile(photoFile);
                else
                    picUri = FileProvider.getUriForFile(this,
                            "com.coldzify.finalproject",
                            photoFile);


                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, picUri);
                //filePath = fileUri.toString();
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    private void getReports(){
        db.collection("reports")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(QuerySnapshot snapshots,FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case ADDED:{
                                    Report report  = dc.getDocument().toObject(Report.class);
                                    Log.d(TAG, "New Report: " + report);
                                    reports.add(report);
                                    break;
                                }

                                case MODIFIED:{
                                    Log.d(TAG, "Modified Report: " + dc.getDocument().toObject(Report.class));
                                    break;
                                }

                                case REMOVED:{
                                    Log.d(TAG, "Removed Report: " + dc.getDocument().toObject(Report.class));
                                    break;
                                }

                            }
                        }
                    }
                });

    }
    private void checkDuplicateReport(Location location){
        if(reports.isEmpty()){
            return;
        }
        LatLng user_location = new LatLng(location.getLatitude(),location.getLongitude());
        float min = 30;
        int indexMin = -1;
        for(int i = 0 ; i < reports.size() ; i++){
            Report report = reports.get(i);
            GeoPoint geoPoint = report.getGeoPoint();
            if(geoPoint != null){
                LatLng report_location = new LatLng(geoPoint.getLatitude(),geoPoint.getLongitude());
                float distance = LocationHandle.distance(user_location,report_location);
                ProblemType report_type = ProblemType.valueOf(report.getType());
                Log.d(TAG,"distance : "+distance+" type1 "+problemType+" type2 "+report_type);
                if(distance < 15 && problemType != null && problemType == report_type) {
                    //Toast.makeText(this,"close location",Toast.LENGTH_SHORT).show();

                    if(distance < min){
                        min = distance;
                        indexMin = i ;
                    }

                }
            }
        }
        if(indexMin != -1){
            DuplicateReportDialog duplicateReportDialog = new DuplicateReportDialog();
            Bundle bundle = new Bundle();
            bundle.putString("reportID",reports.get(indexMin).getReportID());
            duplicateReportDialog.setArguments(bundle);
            duplicateReportDialog.show(getSupportFragmentManager(),"DuplicateReportDialog");
        }

    }

    public void onTakePhoto(View v) {
        takePhoto();
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }

    private File createImageFile() throws IOException {
        // Create an image file name

        String timeStamp = new SimpleDateFormat("ddMMyyyy_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "Report_picture_" + timeStamp + "_";

        storageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), IMAGE_DIRECTORY_NAME);

        if (!storageDir.exists())
            storageDir.mkdirs();

        // Save a file: path for use with ACTION_VIEW intents
        //currentPhotoPath = image.getAbsolutePath();
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );
        currentPath = image.getAbsolutePath();
        return image;
    }

    private void setPic() {
        // Get the dimensions of the View
        //Uri uri = FileProvider.getUriForFile(this,"com.coldzify.finalproject",new File(currentPath));
        int targetW = 720;
        int targetH = 1280;

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bitmap = BitmapFactory.decodeFile(currentPath, bmOptions);
        bitmap = Bitmap.createScaledBitmap(bitmap, photoW / 2, photoH / 2, true);
        //Toast.makeText(this,"width : "+bitmap.getWidth()+" height : "+bitmap.getHeight(),Toast.LENGTH_LONG).show();
        try {

            //Bitmap saveScale = Bitmap.createScaledBitmap(bitmap,bitmap.getWidth()/2,bitmap.getHeight()/2,true);
            ExifInterface exif = new ExifInterface(currentPath);
            int rotation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
            int rotationInDegrees = exifToDegrees(rotation);
            Matrix matrix = new Matrix();
            if (rotation != 0) {
                matrix.preRotate(rotationInDegrees);
            }
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
            FileOutputStream bmpFile = new FileOutputStream(currentPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, bmpFile);
            bmpFile.flush();
            bmpFile.close();


        } catch (Exception e) {
            e.printStackTrace();
        }

        //Bitmap bitmap = BitmapFactory.decodeFile(picUri.getPath());
        //picImageView.setImageBitmap(Bitmap.createScaledBitmap(bitmap,targetW,targetH,true));
        //picImageView.setImageBitmap(bitmap);
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public void report(View v) {
        if(imagesPath.size() == 0){
            Toast.makeText(this,"กรุณาเพิ่มรูปภาพอย่างน้อย 1 รูป",Toast.LENGTH_SHORT).show();
            return;
        }
        if(problemType == null ){
            Toast.makeText(this,"กรุณาเลือกประเภทของปัญหา",Toast.LENGTH_SHORT).show();
            return;
        }
        if(currentLocation == null ){
            Toast.makeText(this,"กรุณาระบุสถานที่ของปัญหา",Toast.LENGTH_SHORT).show();
            return;
        }
        if(detail_editText.getText().toString().length() == 0){
            Toast.makeText(this,"กรุณาพิมพ์รายละเอียดของปัญหา",Toast.LENGTH_SHORT).show();
            detail_editText.requestFocus();
            return;
        }
        GeoPoint geoPoint =new GeoPoint(currentLocation.getLatitude(),currentLocation.getLongitude());
        progress = 0;
        showProgressDialog();
        String detail = detail_editText.getText().toString();
        final String uid = FirebaseAuth.getInstance().getUid();

        String room="";
        if(room_autoComplete.isEnabled())
            room = room_autoComplete.getText().toString();
        Report report = new Report(picturesName, problemType.name(), detail
                ,geoPoint, place_code,room, uid);
        if (FirebaseAuth.getInstance() != null) {
            DocumentReference ref = db.collection("reports").document();
            final String reportID = ref.getId();
            Map<String , Object> map = report.toMap();
            map.put("timestamp", FieldValue.serverTimestamp());
            map.put("reportID",reportID);
            ref.set(map)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "Added report !");

                                subscribe(uid,reportID);
                                FirebaseMessaging.getInstance().subscribeToTopic(reportID);

                            }
                            else{
                                Log.w(TAG, "Fail Adding report !",task.getException());
                            }
                        }
                    });



            for (int i = 0; i < imagesPath.size(); i++) {
                Uri uri = Uri.fromFile(new File(imagesPath.get(i)));
                uploadPic(uri);
            }
        }

    }


    private void uploadPic(Uri uriPic) {
        //showProgressDialog();
        //Uri file = Uri.fromFile(new File("path/to/images/rivers.jpg"));
        StorageReference storageRef = storage.getReference();
        StorageReference riversRef = storageRef.child("images/").child("reports/" + uriPic.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(uriPic);

        // Register observers to listen for when the download is done or if it fails
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Upload Fail : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.

                progress++;
                dialog.setProgress(progress);
                if (progress == imagesPath.size()+1) {
                    //Toast.makeText(getApplicationContext(), "Upload Success", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    Intent feed = new Intent(ReportActivity.this, ReportFinishActivity.class);
                    startActivity(feed);
                    finish();
                }

            }
        })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot snapshot) {
                        //System.out.println(snapshot.getBytesTransferred());

                    }
                });
    }
    public void onClickSelectType(View view){
        selectTypeDialog = new SelectTypeDialog();
        selectTypeDialog.setOnClickListener(listener);
        selectTypeDialog.show(getSupportFragmentManager(),"select problem type");
    }

    private void showProgressDialog() {
        dialog = new UploadProgressDialog();

        dialog.show(getSupportFragmentManager(), "Progress");
        Bundle bundle = new Bundle();
        bundle.putInt("max", imagesPath.size()+1);
        dialog.setArguments(bundle);


    }


    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT > 16) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE
                    , Manifest.permission.READ_EXTERNAL_STORAGE
                    , Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_STORAGE);
        }
    }

    public void onClickShortcutText(View view){
        Button b = (Button)view;
        detail_editText.append(b.getText());
    }
    public void onDeleteImageItem(View view) {
        if (imagesPath.size() != 0 && adapter != null) {
            int index = mPager.getCurrentItem();
            imagesPath.remove(index);
            picturesName.remove(index);
            setImagesAdapter();
            indicator.notifyDataSetChanged();
            nPictures--;
        }
    }

    private void setImagesAdapter() {
        adapter = new SlidingImageAdapter(this, imagesPath);
        mPager.setAdapter(adapter);
        indicator.setViewPager(mPager);
        if (imagesPath.size() == 0)
            delete_imageView.setVisibility(View.INVISIBLE);
        else
            delete_imageView.setVisibility(View.VISIBLE);


    }
    private void getTokenId(){
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        sendRegistrationToServer(token);
                        // Log and toast

                        String msg = "Token : "+token;
                        Log.d(TAG, msg);

                    }
                });
    }
    private void sendRegistrationToServer(String token) {
        String uid = FirebaseAuth.getInstance().getUid();

        if(uid == null)
            return;
        db.collection("users").document(uid)
                .update("tokenId",token)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "Update token success");
                        }
                        else{
                            Log.d(TAG,"error ",task.getException());
                        }
                    }
                });
    }
    public String toThaiString(ProblemType type){
        String str ="Unknown";
        switch (type){
            case ELECTRICS:
                str = "ปัญหาไฟฟ้า";
                break;
            case WATER:
                str = "ปัญหาการประปา";
                break;
            case POLLUTION:
                str = "ปัญหามลพิษ";
                break;
            case MATERIAL:
                str = "ปัญหาวัสดุชำรุด";
                break;
            case CLEAN:
                str = "ปัญหาความสะอาด";
                break;
            case SECURITY:
                str = "ปัญหาความปลอดภัย";
                break;
            case ENVIRONMENT:
                str = "ปัญหาสิ่งแวดล้อม";
                break;
            case TRAFFIC:
                str = "ปัญหาการจราจร";
                break;
            case BUILDING:
                str = "ปัญหาสิ่งก่อสร้าง";
                break;

        }
        return str;
    }
    public Drawable problemTypeToDrawable(ProblemType type){
        Drawable str =null;
        switch (type){
            case ELECTRICS:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_electrics);
                break;
            case WATER:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_water);
                break;
            case POLLUTION:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_pollution);
                break;
            case MATERIAL:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_material);
                break;
            case CLEAN:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_clean);
                break;
            case SECURITY:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_security);
                break;
            case ENVIRONMENT:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_environment);
                break;
            case TRAFFIC:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_traffic);
                break;
            case BUILDING:
                str = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_building);
                break;
        }
        return str;
    }
    private void subscribe(final String uid, final String reportID){
        Log.d(TAG, "subscribing: "+reportID);
        Map<String, Object> data = new HashMap<>();
        data.put("reportID", reportID);
        db.collection("users")
                .document(uid)
                .collection("subscribe")
                .document(reportID)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "subscribed : "+reportID);
                        progress++;
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error to subscribe", e);
                        progress++;
                    }
                });

    }
    public void copyImageToFolder(File src, File dst) throws IOException {

        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {

            setPic();
            galleryAddPic();
            imagesPath.add(currentPath);
            picturesName.add(picName);
            setImagesAdapter();
            mPager.setCurrentItem(imagesPath.size() - 1);
            nPictures++;
        }
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            File pic = new File(picturePath);
            //picName = new File(picturePath).getName();
            //picImageView.setImageBitmap(BitmapFactory.decodeFile(picturePath));
            //imagesPath.add(picturePath);
            if (pic.getAbsolutePath().contains(IMAGE_DIRECTORY_NAME)) {
                picName = pic.getName();
                imagesPath.add(picturePath);
                picturesName.add(picName);
                Log.d(TAG, "Project folder!");
            } else {
                try {
                    File newFile = createImageFile();
                    copyImageToFolder(new File(picturePath), newFile);
                    setPic();
                    imagesPath.add(currentPath);
                    picturesName.add(newFile.getName());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            setImagesAdapter();
            mPager.setCurrentItem(imagesPath.size() - 1);
            nPictures++;

        }
        if (requestCode == REQUEST_CHECK_SETTINGS ) {
            if(resultCode == RESULT_OK){
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                place_textView.setText("กำลังค้นหา ...");
                fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
            }else{
                place_textView.setText(R.string.problem_place_th);
                change_place_textView.setVisibility(View.INVISIBLE);
            }


        }
        //Toast.makeText(this,"size : "+imagesPath.size(),Toast.LENGTH_LONG).show();

    }
}
