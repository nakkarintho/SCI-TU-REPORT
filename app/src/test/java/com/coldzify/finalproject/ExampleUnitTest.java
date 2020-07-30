package com.coldzify.finalproject;



import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void addReportTest(){
        FirestoreController fCon = new FirestoreController();
       // fCon.addReport(new Report("pic1","1","testDetail",new LatLng(10,10),"2063439437107254"));

    }
}