package com.example.rhyme_app_project;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class FuncListAct extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_func_list);

        ListView listview = (ListView)findViewById(R.id.FuncList);
        final ArrayList<String> list = new ArrayList<>();

        for(int i = 0; i < 20; i++){
            list.add("func " + i);
        }

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, android.R.layout.simple_list_item_1, list
        );

        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String selected_item = (String)adapterView.getItemAtPosition(position);
                //list.remove(selected_item);
                Intent intent1 = new Intent(FuncListAct.this, MainActivity.class);
                startActivity(intent1);
                adapter.notifyDataSetChanged();
            }
        });
    }

}
