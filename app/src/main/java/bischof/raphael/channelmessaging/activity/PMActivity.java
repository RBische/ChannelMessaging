package bischof.raphael.channelmessaging.activity;

import android.content.SharedPreferences;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.List;

import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.adapter.UserMessageAdapter;
import bischof.raphael.channelmessaging.model.UserMessages;
import bischof.raphael.channelmessaging.model.WSResponse;
import bischof.raphael.channelmessaging.utils.OnContentLoadedListener;
import bischof.raphael.channelmessaging.utils.PostRequest;


public class PMActivity extends ActionBarActivity implements OnContentLoadedListener {
    private ListView mListview;
    private int userid;
    private EditText mEtMessage;
    private Button mBtnSend;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userid = getIntent().getIntExtra("userid",0);
        setContentView(R.layout.activity_pm);
        mListview = (ListView)findViewById(R.id.listView);
        mEtMessage = (EditText)findViewById(R.id.etMessage);
        refreshMessages();
        mBtnSend = (Button)findViewById(R.id.btnSend);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEtMessage.getText().toString().equalsIgnoreCase("")){
                    sendMessage(mEtMessage.getText().toString());
                }
            }
        });
    }

    private void refreshMessages(){
        List<NameValuePair> pairs = new ArrayList<>();
        NameValuePair nvp = new NameValuePair() {
            @Override
            public String getName() {
                return "accesstoken";
            }

            @Override
            public String getValue() {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String token = settings.getString("accesstoken", "");
                return token;
            }
        };
        pairs.add(nvp);
        NameValuePair nvpChannel = new NameValuePair() {
            @Override
            public String getName() {
                return "userid";
            }

            @Override
            public String getValue() {
                return ""+userid;
            }
        };
        pairs.add(nvpChannel);
        PostRequest pr = new PostRequest(getApplicationContext(),"http://www.raphaelbischof.fr/messaging/?function=getmessages",pairs,0);
        pr.setOnContentLoaded(this);
        pr.execute();
    }

    @Override
    public void onContentLoaded(String news, int requestCode) {
        if (requestCode==0){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
            UserMessages messages = gson.fromJson(news,UserMessages.class);
            mListview.setAdapter(new UserMessageAdapter(getApplicationContext(),0,messages.messages));
            /*ArrayList<HashMap<String,String>> ar = new ArrayList<HashMap<String,String>>();
            for(Message f:messages.messages){
                HashMap<String,String> hp = new HashMap<String, String>();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                hp.put("titre", sdf.format(f.date));
                hp.put("annee", f.username+" : "+f.message);
                ar.add(hp);
            }
            mMessages = messages;
            int[] to = { android.R.id.text1, android.R.id.text2 };
            mLvMessages.setAdapter(new SimpleAdapter(getApplicationContext(), ar, R.layout.custom_row, new String[]{"annee", "titre"}, to));*/
        }
        else if (requestCode==1){
            Gson gson = new Gson();
            WSResponse response = gson.fromJson(news, WSResponse.class);
            if (response.code.equalsIgnoreCase("200"))
                mEtMessage.setText("");
        }
        //.setAdapter(new UserMessageAdapter(getApplicationContext(),0,));
    }

    private void sendMessage(final String s) {
        List<NameValuePair> pairs = new ArrayList<>();
        NameValuePair nvp = new NameValuePair() {
            @Override
            public String getName() {
                return "accesstoken";
            }

            @Override
            public String getValue() {
                SharedPreferences settings = getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String token = settings.getString("accesstoken", "");
                return token;
            }
        };
        pairs.add(nvp);
        NameValuePair nvpChannel = new NameValuePair() {
            @Override
            public String getName() {
                return "userid";
            }

            @Override
            public String getValue() {
                return ""+userid;
            }
        };
        pairs.add(nvpChannel);
        NameValuePair nvpMessage = new NameValuePair() {
            @Override
            public String getName() {
                return "message";
            }

            @Override
            public String getValue() {
                return s;
            }
        };
        pairs.add(nvpMessage);
        PostRequest pr = new PostRequest(getApplicationContext(),"http://www.raphaelbischof.fr/messaging/?function=sendmessage",pairs,1);
        pr.setOnContentLoaded(this);
        pr.execute();
    }
}
