package bischof.raphael.channelmessaging.activity;

import android.content.res.Configuration;
import android.os.Bundle;

import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.fragment.MessageFragment;


public class ChannelActivity extends GPSActivity {

    private int channelID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel);
        if (this.getResources().getConfiguration().orientation== Configuration.ORIENTATION_LANDSCAPE){
            finish();
        }

        MessageFragment fragA= (MessageFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentA_ID);
            fragA.showMessages(getIntent().getIntExtra("channelid", 0));
    }


}
