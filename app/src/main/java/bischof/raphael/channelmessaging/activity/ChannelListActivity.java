package bischof.raphael.channelmessaging.activity;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.MenuItemCompat;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.fragment.ChannelListFragment;
import bischof.raphael.channelmessaging.fragment.MessageFragment;


public class ChannelListActivity extends GPSActivity implements ChannelListFragment.OnFragmentInteractionListener,SearchView.OnQueryTextListener {
    private int channelID=-1;
    private MenuItem searchMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_channel_list);
        if (savedInstanceState!=null)
        if(savedInstanceState.getInt("channelID",-1)!=-1){
            startChannel(savedInstanceState.getInt("channelID",-1));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (channelID!=-1)
        outState.putInt("channelID",channelID);
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
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void startChannel(int channelID) {

        MessageFragment fragA= (MessageFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentB_ID);
        if (fragA!=null&&fragA.isInLayout()){
            fragA.showMessages(channelID);
        }else{
            Intent i = new Intent(this,ChannelActivity.class);
            i.putExtra("channelid",channelID);
            startActivity(i);
        }
        this.channelID = channelID;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_channel_list, menu);
        searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchViewAction = (SearchView) MenuItemCompat.getActionView(searchMenuItem);
        searchViewAction.setOnQueryTextListener(this);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onQueryTextSubmit(String s) {
        MenuItemCompat.collapseActionView(searchMenuItem);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        ChannelListFragment fragA= (ChannelListFragment)getSupportFragmentManager().findFragmentById(R.id.fragmentA_ID);
        fragA.search(s);
        return false;
    }

}
