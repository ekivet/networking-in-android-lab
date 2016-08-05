package com.example.erickivet.networkinglab;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    TextView text1;
    Button button1, button2, button3;
    ListView mListView;
    ArrayList<String> apiList;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView) findViewById(R.id.listview1);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        text1 = (TextView)findViewById(android.R.id.text1);

        apiList = new ArrayList<>();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()){
            //use network
            button1.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    apiList.clear();
                    new ApiThread().execute("http://api.walmartlabs.com/v1/search?query=cereal&format=json&apiKey=6b3vpegtcjv7qr9xq4yzk837");
                    Log.v("api", "button1 api call successful");
                }
            });
            button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apiList.clear();
                    new ApiThread().execute("http://api.walmartlabs.com/v1/search?query=tea&format=json&apiKey=6b3vpegtcjv7qr9xq4yzk837");
                    Log.v("api","button2 api call successful");
                }
            });
            button3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    apiList.clear();
                    new ApiThread().execute("http://api.walmartlabs.com/v1/search?query=chocolate&format=json&apiKey=6b3vpegtcjv7qr9xq4yzk837");
                }
            });
        }else {
            Toast.makeText(MainActivity.this,"No Network Connection",Toast.LENGTH_LONG).show();
        }

    }

    private  void downloadUrl(String myUrl) throws IOException, JSONException{
        InputStream is = null;
        try{
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoInput(true);

            //Starts query
            conn.connect();
            is = conn.getInputStream();

            //Converts InputStream into a string

            String contentAsString = readIt(is);
            parseJson(contentAsString);

            //makes sure the inputstream is closed after the app is done using it.
        }finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private void parseJson(String content) throws JSONException{
        JSONObject object = new JSONObject(content);
        JSONArray array= object.getJSONArray("items");
        for (int i = 0; i < array.length(); i++){
            JSONObject product = array.getJSONObject(i);
            apiList.add(product.getString("name"));
        }
    }


    private String readIt(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        String read;

        while ((read = br.readLine()) != null) {
            sb.append(read);
        }
        return  sb.toString();
    }

    public class ApiThread extends AsyncTask<String,Void,String>{
        @Override
        protected String doInBackground(String... strings) {
            try{
                downloadUrl(strings[0]);
                return null;
            }catch (IOException e){
                e.printStackTrace();
                return "Unable to connect to webpage";
            }
            catch (JSONException e){
                e.printStackTrace();
                return "Parsing Failure";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            adapter = new ArrayAdapter<String>(MainActivity.this,android.R.layout.simple_list_item_1,apiList);
            mListView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
    }
}


/**
    private String performPost() throws IOException, JSONException{
        DataOutputStream os = null;
        InputStream is = null;
        try{
            URL url = new URL("http://httpbin.org/post");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            String urlParameters = "enterparams";
            byte[] postdata = urlParameters.getBytes(StandardCharsets.UTF_8);
            int postDataLength = postdata.length;
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-type","application/x-www-form-urlencoded");
            conn.setRequestProperty("charset","utf-8");
            conn.setRequestProperty("Content-Length",Integer.toString(postDataLength));
            os = new DataOutputStream(conn.getOutputStream());
            os.write(postdata);
            os.flush();
            is = conn.getInputStream();
            return readIt(is);
        }finally {
            if (is != null){
                is.close();
            }
        }   if (os != null){
                os.close();
        }
    }
}
*/