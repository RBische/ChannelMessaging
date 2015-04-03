package bischof.raphael.channelmessaging.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.model.WSResponse;
import bischof.raphael.channelmessaging.utils.OnContentLoadedListener;
import bischof.raphael.channelmessaging.utils.PostRequest;


public class LoginActivity extends ActionBarActivity implements View.OnClickListener {

    public static final String PREFS_NAME = "preferences";
    private Button mBtnValider;
    private EditText mEtLogin;
    private EditText mEtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mBtnValider = (Button)findViewById(R.id.btnValidate);
        mEtLogin = (EditText)findViewById(R.id.etIdentifiant);
        mEtPassword = (EditText)findViewById(R.id.etPassword);
        mBtnValider.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnValidate){
            List<NameValuePair> pairs = new ArrayList<>();
            NameValuePair nameValuePair = new NameValuePair() {
                @Override
                public String getName() {
                    return "username";
                }

                @Override
                public String getValue() {
                    return mEtLogin.getText().toString();
                }
            };
            NameValuePair nvp = new NameValuePair() {
                @Override
                public String getName() {
                    return "password";
                }

                @Override
                public String getValue() {
                    return mEtPassword.getText().toString();
                }
            };
            pairs.add(nameValuePair);
            pairs.add(nvp);
            PostRequest pr = new PostRequest(getApplicationContext(),"http://www.raphaelbischof.fr/messaging/?function=connect",pairs,0);
            pr.setOnContentLoaded(new OnContentLoadedListener() {
                @Override
                public void onContentLoaded(String news, int requestCode) {
                    if (requestCode==0){
                        Gson gson = new Gson();
                        WSResponse response = gson.fromJson(news, WSResponse.class);
                        if (response.code.equalsIgnoreCase("200")){
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString("accesstoken", response.accesstoken); // Commit the edits!
                            editor.commit();
                            Intent i = new Intent(LoginActivity.this,ChannelListActivity.class);
                            startActivity(i);
                        }else{
                            Toast.makeText(getApplicationContext(),"Non connecté",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            });
            pr.execute();
        }
    }
}
