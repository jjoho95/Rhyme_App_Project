package com.example.rhyme_app_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
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

public class MainActivity extends AppCompatActivity {
    private EditText idedit; //ID editbox
    private EditText pwedit; //비밀번호 editbox
    private EditText compidedit; //회사정보 editbox
    private TextView tv_outPut;
    private String url;
    private User user;
    static  String strJson = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //url 설정
        url = "http://doho.truds.kr/index.php?login=";

        idedit = (EditText) findViewById(R.id.IDEdit);
        pwedit = (EditText) findViewById(R.id.PWEdit);
        compidedit = (EditText) findViewById(R.id.CompIDEdit);
        tv_outPut = (TextView) findViewById(R.id.tv_out);

    }



    public void onLoginBtnClicked(View view) { //로그인 버튼 클릭시 각 editbox에서 값을 받아옴
        String id_str = idedit.getText().toString();
        String pw_str = pwedit.getText().toString();
        String compid_str = compidedit.getText().toString();
        // call AsynTask to perform network operation on separate thread
        HttpAsyncTask httpTask = new HttpAsyncTask(MainActivity.this);
        httpTask.execute(url, id_str,pw_str,compid_str);

    }


//쓰레드를 추가로 만들어서 처리함
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        private MainActivity mainAct;

        HttpAsyncTask(MainActivity mainActivity) {//초기화
            this.mainAct = mainActivity;
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

            mainAct.runOnUiThread(new Runnable() {//병렬로처리
                @Override
                public void run() {
                    //Toast.makeText(mainAct, "Received!", Toast.LENGTH_LONG).show();
                    try {
                        JSONObject json = new JSONObject(strJson);//strJson을 다시 JSON객체로 변환

                        //로그인성공시
                        if(json.getString("Result").equals("valid"))
                        {
                            Toast.makeText(mainAct, "로그인 성공!", Toast.LENGTH_LONG).show();
                            
                            Intent intent1 = new Intent(mainAct, AnotherActivity.class);
                            startActivity(intent1);
                        }
                        //로그인실패시
                        else{
                            String casestr = json.getString("InvalidCase");
                            String errorstr = "";
//                            Log.d("casestr",casestr.charAt(0));
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
                            Toast.makeText(mainAct, errorstr, Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        tv_outPut.setText(e.toString());
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

}
