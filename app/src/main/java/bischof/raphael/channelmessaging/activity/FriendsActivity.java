package bischof.raphael.channelmessaging.activity;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import java.util.List;

import bischof.raphael.channelmessaging.adapter.GridviewFriendAdapter;
import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.bdd.UserDatasource;
import bischof.raphael.channelmessaging.model.User;


public class FriendsActivity extends ActionBarActivity implements AdapterView.OnItemClickListener{
    private GridView mLv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends);
        mLv = (GridView)findViewById(R.id.lvFriends);
        mLv.setOnItemClickListener(this);
        UserDatasource userDatasource = new UserDatasource(getApplicationContext());
        userDatasource.open();
        List<User> users = userDatasource.getAllHommes();
        userDatasource.close();
        mLv.setAdapter(new GridviewFriendAdapter(getApplicationContext(),0,users));
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        User user = (User)view.getTag();
        Toast.makeText(getApplicationContext(),"Utilisateur cliqué :"+user.userID,Toast.LENGTH_LONG).show();
        Intent i = new Intent(getApplicationContext(),PMActivity.class);
        i.putExtra("userid",user.userID);
        startActivity(i);
    }
}
