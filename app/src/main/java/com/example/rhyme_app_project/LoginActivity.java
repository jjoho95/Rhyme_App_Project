package com.example.rhyme_app_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//database
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;


public class LoginActivity extends AppCompatActivity {
    private EditText idedit; //ID editbox
    private EditText pwedit; //비밀번호 editbox
    private EditText compidedit; //회사정보 editbox
    private String url;
    private User user;
    static  String strJson = "";
    DBHelper dbHelper;

    final static String dbName = "menuauth.db";
    final static int dbVersion = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        //url 설정
        url = "http://doho.truds.kr/index.php?login=";

        idedit = (EditText) findViewById(R.id.IDEdit);
        pwedit = (EditText) findViewById(R.id.PWEdit);
        compidedit = (EditText) findViewById(R.id.CompIDEdit);

        SQLiteDatabase db;
        String sql;

        //database
        dbHelper = new DBHelper(this, dbName, null, dbVersion);

        db = dbHelper.getWritableDatabase();

        sql = "DELETE FROM menuauth;";
        db.execSQL(sql);

    }



    public void onLoginBtnClicked(View view) { //로그인 버튼 클릭시 각 editbox에서 값을 받아옴
        String id_str = idedit.getText().toString();
        String pw_str = pwedit.getText().toString();
        String compid_str = compidedit.getText().toString();
        // call AsynTask to perform network operation on separate thread
        HttpAsyncTask httpTask = new HttpAsyncTask(LoginActivity.this);
        httpTask.execute(url, id_str,pw_str,compid_str);

    }


//쓰레드를 추가로 만들어서 처리함
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private LoginActivity LoginAct;

        HttpAsyncTask(LoginActivity loginActivity) {//초기화
            this.LoginAct = loginActivity;
        }

        @Override

        protected String doInBackground(String... urls) {//백그라운드에서 쓰레드 돌아감

            user = new User();//User객체생성후
            user.setUserID(urls[1]);
            user.setPW(urls[2]);
            user.setCompID(urls[3]);//값 지정
            return POST(urls[0], user);//POST 함수를 실행하면서 받아온값 반환
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {//왔을때의 구문
            super.onPostExecute(result);



            strJson = result;//결과값 strJson으로



            LoginAct.runOnUiThread(new Runnable() {//병렬로처리
                @Override
                public void run() {
                    //Toast.makeText(LoginAct, "Received!", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject json = new JSONObject(strJson);//strJson을 다시 JSON객체로 변환

                        //로그인성공시
                        if(json.getString("Result").equals("valid"))
                        {
                            Toast.makeText(LoginAct, "로그인 성공!", Toast.LENGTH_SHORT).show();

                            //funclist는 MenuAuth 객체(각 메뉴들의 이름 변수와 권한 값을 갖고있음)를 담는 arraylist임

                            ArrayList<MenuAuth> funclist=new ArrayList<MenuAuth>();
                            JSONArray authorarray = json.getJSONArray("Authorization");
                            for(int i = 0; i<authorarray.length();i++) {
                                MenuAuth f= new MenuAuth();
                                String KeyStr = authorarray.getJSONObject(i).names().getString(0);
                                Log.d("iterate", KeyStr+authorarray.getJSONObject(i).getString(KeyStr));
                                f.setFuncName(KeyStr);
                                f.setValue(authorarray.getJSONObject(i).getString(KeyStr));
                                funclist.add(f);
                            }

                            SQLiteDatabase db;
                            String sql;

                            for(int i = 0; i< funclist.size();i++)
                            {

                                String menu = funclist.get(i).getFuncName();
                                String author = funclist.get(i).getValue();
                                db = dbHelper.getWritableDatabase();
                                sql = String.format("INSERT INTO menuauth values ( '" + menu + "', '" + author + "')");

                                db.execSQL(sql);
                                //db에 넣기
                            }
                            dbHelper.close();

                            db = dbHelper.getReadableDatabase();
                            sql = "SELECT * FROM menuauth;";
                            Cursor cursor = db.rawQuery(sql, null);
                            if (cursor.getCount() > 0) {
                                while (cursor.moveToNext()) {
                                    Log.d("Yeongwon2",String.format("기능 = %s, 권한 = %s", cursor.getString(0), cursor.getString(1)));

                                    //result.append(String.format("\n이름 = %s, 메모 = %s, 우선순위 = %s, 날짜 = %s, finish=%s",
                                      //      cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4)));
                                }
                            } else {
                                //result.append("\n조회결과가 없습니다.");
                            }
                            cursor.close();



                        dbHelper.close();

                            Intent intent1 = new Intent(LoginAct, MainActivity.class);
                            startActivity(intent1);
                            finish();
                        }

                        //로그인실패시
                        else{
                            String casestr = json.getString("InvalidCase");
                            String errorstr = "";
                            if(casestr.charAt(0)=='1')
                            {
                                errorstr += "아이디가 없습니다";
                            }
                            if(casestr.charAt(1)=='1')
                            {
                                errorstr += "\n비밀번호가 틀렸습니다";
                            }
                            if(casestr.charAt(2)=='1')
                            {
                                errorstr += "\n회사정보가 틀렸습니다.";
                            }
                            Toast.makeText(LoginAct, errorstr, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }


    public  String POST(String url, User user){
        InputStream is = null;
        String result = "";
        try {
            URL urlCon = new URL(url);
            HttpURLConnection httpCon = (HttpURLConnection)urlCon.openConnection();//http방식으로 연결

            String json = "";

            // build jsonObject
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("UserID", user.getUserID());
            jsonObject.put("PW", user.getPW());
            jsonObject.put("CompID", user.getCompID());


            // convert JSONObject to JSON to String
            json = jsonObject.toString();

            // Set some headers to inform server about the type of the content
            httpCon.setRequestMethod("POST");
            httpCon.setRequestProperty("Accept-Charset", "UTF-8");
            httpCon.setRequestProperty("Content-type", "application/x-www-form-urlencoded;cahrset=UTF-8");

            // OutputStream으로 POST 데이터를 넘겨주겠다는 옵션.
            httpCon.setDoOutput(true);
            // InputStream으로 서버로 부터 응답을 받겠다는 옵션.
            httpCon.setDoInput(true);

            OutputStream os = httpCon.getOutputStream();
            //write함
            os.write(json.getBytes("UTF-8"));
            os.flush();
            os.close();

            // receive response as inputStream
            try {
                is = httpCon.getInputStream();
                // convert inputstream to string
                if(is != null) {
                    result = convertInputStreamToString(is);

                }
                else
                    result = "Did not work!";
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                httpCon.disconnect();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)//Inputstream으로 값을 읽어들여와서 string으로 만듬
            result += line;

        inputStream.close();

        return result;

    }

    static class DBHelper extends SQLiteOpenHelper {

        //생성자 - database 파일을 생성한다.
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //DB 처음 만들때 호출. - 테이블 생성 등의 초기 처리.
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE menuauth(menu TEXT, author TEXT);");
            //result.append("\nt3 테이블 생성 완료.");
        }

        //DB 업그레이드 필요 시 호출. (version값에 따라 반응)
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS menuauth");
            onCreate(db);
        }

    }
}
