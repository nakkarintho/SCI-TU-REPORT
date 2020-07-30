package com.coldzify.finalproject;

import java.util.ArrayList;

interface FirestoreCallBack {

    void onQueryListComplete(ArrayList<?> list);
    void onCheckDuplicateComplete(boolean isDuplicate);
}

interface FirestoreCallBack2 {

    void onQueryListComplete(ArrayList<?> list);
    void onCheckDuplicateComplete(boolean isDuplicate);
}