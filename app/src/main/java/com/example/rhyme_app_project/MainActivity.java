package com.example.rhyme_app_project;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mActionBarDrawerToggle;
    NavigationView mNavigationView;
    ArrayList<String> falsemenus;
    private static final String TAG = "LoginActivity";

    LoginActivity.DBHelper dbHelper;

    final static String dbName = "menuauth.db";
    final static int dbVersion = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //layout variables
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        mNavigationView = findViewById(R.id.main_nav_view);

        mActionBarDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener((mActionBarDrawerToggle));
        mActionBarDrawerToggle.syncState();

        SQLiteDatabase db;
        String sql;

        setSupportActionBar((Toolbar)findViewById(R.id.my_toolbar));
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_category);

        //Default fragment to be display
        getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment_content, new Fragment_Main()).commit(); //2nd parameter가 포함될 1st parameter viewGroup

        //권한에 따른 메뉴 제거
        //loadDB
        //falsemenus = SELECT * FROM Authorization where value = false;//false인 항목들 다받아와서 삭제
        Menu menu = mNavigationView.getMenu();
        db = dbHelper.getReadableDatabase();
        sql = "select menu from menuauth where author= 'false'";
        Cursor cursor = db.rawQuery(sql, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                Log.d("Yeongwon2",String.format("기능 = %s", cursor.getString(0)));
                String falsemenu = cursor.getString(0);
                for(int i = 0 ; i< menu.size();i++)
                {
                        Log.d("menusize", menu.getItem(i).getTitle().toString());
            if(menu.getItem(i).getTitle().toString().equals(falsemenu))
            {
                menu.removeItem(i);
            }
                    }
                }
            }
        else {
            //result.append("\n조회결과가 없습니다.");
        }
        cursor.close();



        dbHelper.close();




        //listener for navigation view
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                changeFragmentDisplay(item);
                return true;
            }
        });

    }





    private void changeFragmentDisplay(MenuItem item){
        Fragment fragment = null;

        ///in toolbar
        if(item.getItemId() == R.id.item_1){
            fragment = new Fragment_A();
        }
        else if(item.getItemId() == R.id.item_2){
            fragment = new Fragment_B();
        }
        else if(item.getItemId() == R.id.item_3){
            fragment = new Fragment_C();
        }
        else if(item.getItemId() == R.id.item_4){
            fragment = new Fragment_D();
        }
        else {
            Toast.makeText(MainActivity.this, "Error", Toast.LENGTH_LONG).show();
        }

        //hide naviagtion drawer
        mDrawerLayout.closeDrawer(GravityCompat.START);

        if(fragment != null){
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.main_fragment_content, fragment);
            ft.commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mActionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.main_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



}
