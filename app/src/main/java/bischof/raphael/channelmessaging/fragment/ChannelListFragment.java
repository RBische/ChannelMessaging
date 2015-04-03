package bischof.raphael.channelmessaging.fragment;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.google.gson.Gson;

import org.apache.http.NameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bischof.raphael.channelmessaging.activity.FriendsActivity;
import bischof.raphael.channelmessaging.activity.LoginActivity;
import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.model.Channel;
import bischof.raphael.channelmessaging.model.Channels;
import bischof.raphael.channelmessaging.utils.OnContentLoadedListener;
import bischof.raphael.channelmessaging.utils.PostRequest;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ChannelListFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ChannelListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ChannelListFragment extends Fragment implements OnContentLoadedListener {
    private ListView mListView;
    private Channels mChannels;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ChannelListFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ChannelListFragment newInstance(String param1, String param2) {
        ChannelListFragment fragment = new ChannelListFragment();
        return fragment;
    }

    public ChannelListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_channel_list, container, false);
        Button btn = (Button)v.findViewById(R.id.btnFriend);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), FriendsActivity.class));
            }
        });
        mListView = (ListView)v.findViewById(R.id.lvChannels);
        List<NameValuePair> pairs = new ArrayList<>();
        NameValuePair nvp = new NameValuePair() {
            @Override
            public String getName() {
                return "accesstoken";
            }

            @Override
            public String getValue() {
                SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String token = settings.getString("accesstoken", "");
                return token;
            }
        };
        pairs.add(nvp);
        PostRequest pr = new PostRequest(getActivity(),"http://www.raphaelbischof.fr/messaging/?function=getchannels",pairs,0);
        pr.setOnContentLoaded(this);
        pr.execute();
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onContentLoaded(String news, int requestCode) {
        Gson gson = new Gson();
        Channels channels = gson.fromJson(news,Channels.class);
        if (channels!=null){
            if (channels.channels!=null){
                ArrayList<HashMap<String,String>> ar = new ArrayList<HashMap<String,String>>();
                for(Channel f:channels.channels){
                    HashMap<String,String> hp = new HashMap<String, String>();
                    hp.put("annee", f.name);
                    hp.put("titre", "Nombre d'utilisateurs connectés : "+f.connectedusers);
                    ar.add(hp);
                }
                mChannels = channels;
                int[] to = { android.R.id.text1, android.R.id.text2 };
                mListView.setAdapter(new SimpleAdapter(getActivity(), ar, R.layout.custom_row, new String[]{"annee","titre"}, to));
                mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        mListener.startChannel(mChannels.channels.get(position).channelID);
                    }
                });
            }
        }
    }

    public void search(String searchText) {
        ArrayList<HashMap<String,String>> ar = new ArrayList<HashMap<String,String>>();
        for(Channel f:mChannels.channels){
            if (f.name.toLowerCase().contains(searchText.toLowerCase())){
                HashMap<String,String> hp = new HashMap<String, String>();
                hp.put("annee", f.name);
                hp.put("titre", "Nombre d'utilisateurs connectés : "+f.connectedusers);
                ar.add(hp);
            }
        }
        int[] to = { android.R.id.text1, android.R.id.text2 };
        mListView.setAdapter(new SimpleAdapter(getActivity(), ar, R.layout.custom_row, new String[]{"annee","titre"}, to));
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
        public void startChannel(int channelID);
    }

}
