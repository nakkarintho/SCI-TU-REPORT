package com.coldzify.finalproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.Arrays;

public class EditUserTypeActivity extends AppCompatActivity {
    private Spinner userType_spinner;
    private AutoCompleteTextView email_autoComplete;
    private ArrayList<String> room_br2,room_br3,room_br4,room_br5;
    private ArrayAdapter<String>userType_adapter,br2_adapter,br3_adapter,br4_adapter,br5_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_user_type);
        userType_spinner = findViewById(R.id.userType_spinner);
//
        email_autoComplete = findViewById(R.id.email_autoComplete);
        String[] arr = getResources().getStringArray(R.array.userType);
//        room_br2 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.br2_room)));
//        room_br3 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.br3_room)));
//        room_br4 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.br4_room)));
//        room_br5 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.br5_room)));
        userType_adapter = new ArrayAdapter<>(this,android.R.layout.simple_dropdown_item_1line,arr);
//        br2_adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,room_br2);
//        br3_adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,room_br3);
//        br4_adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,room_br4);
//        br5_adapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,room_br5);
//
        userType_spinner.setAdapter(userType_adapter);
//        email_autoComplete.setAdapter(br2_adapter);
//
//        building_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//
//                switch (i){
//                    case 0:
//                        room_autoComplete.setAdapter(br2_adapter);
//                        break;
//                    case 1:
//                        room_autoComplete.setAdapter(br3_adapter);
//                        break;
//                    case 2:
//                        room_autoComplete.setAdapter(br4_adapter);
//                        break;
//                    case 3:
//                        room_autoComplete.setAdapter(br5_adapter);
//                        break;
//
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//    }
//    private boolean queryInList(String text,ArrayList<String> list){
//        return list.contains(text);
//    }
//    public void onClickChange(View view){
//            String text_room = room_autoComplete.getText().toString();
//            String building = building_spinner.getSelectedItem().toString();
//            //Toast.makeText(this,building+" "+text_room,Toast.LENGTH_SHORT).show();
//            if (text_room.equals("ห้อง 101")) {
//                Intent intent = new Intent(SearchHousekeeperActivity.this, ContactHousekeeperActivity.class);
//                intent.putExtra("housekeeper_id", housekeeper_101);
//                intent.putExtra("building", building);
//                intent.putExtra("room", text_room);
//                startActivity(intent);
//            } else if (text_room.equals("ห้อง 102")) {
//                Intent intent = new Intent(SearchHousekeeperActivity.this, ContactHousekeeperActivity.class);
//                intent.putExtra("housekeeper_id", housekeeper_102);
//                intent.putExtra("building", building);
//                intent.putExtra("room", text_room);
//                startActivity(intent);
//            }
//        }
    }

}
