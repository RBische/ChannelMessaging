package bischof.raphael.channelmessaging.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import bischof.raphael.channelmessaging.activity.GPSActivity;
import bischof.raphael.channelmessaging.activity.LoginActivity;
import bischof.raphael.channelmessaging.adapter.MessageAdapter;
import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.activity.MapsActivity;
import bischof.raphael.channelmessaging.activity.SoundActivity;
import bischof.raphael.channelmessaging.bdd.UserDatasource;
import bischof.raphael.channelmessaging.model.Message;
import bischof.raphael.channelmessaging.model.Messages;
import bischof.raphael.channelmessaging.model.WSResponse;
import bischof.raphael.channelmessaging.utils.OnContentLoadedListener;
import bischof.raphael.channelmessaging.utils.PostRequest;
import bischof.raphael.channelmessaging.utils.UploadFileToServer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * to handle interaction events.
 * create an instance of this fragment.
 */
public class MessageFragment extends Fragment implements OnContentLoadedListener,AdapterView.OnItemClickListener,View.OnClickListener, UploadFileToServer.OnUploadFileListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CHANNEL_ID = "channelid";
    private static final int PICTURE_REQUEST_CODE = 0;

    private int channelID;
    private ListView mLvMessages;
    private EditText mEtMessage;
    private Button mBtnSend;
    private Handler mHandler;
    private Button mBtnImage;
    private UUID mImageID;


    public MessageFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            channelID = getArguments().getInt(CHANNEL_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_message, container, false);
        mLvMessages = (ListView)v.findViewById(R.id.lvMessage);
        mEtMessage = (EditText)v.findViewById(R.id.etMessage);
        mBtnSend = (Button)v.findViewById(R.id.btnSend);
        mBtnImage = (Button)v.findViewById(R.id.btnImage);
        mLvMessages.setOnItemClickListener(this);
        mBtnImage.setOnClickListener(this);
        v.findViewById(R.id.btnSound).setOnClickListener(this);
        return v;
    }

    public void showMessages(int channelID){
        this.channelID = channelID;
        mHandler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if (getActivity()!=null){
                    refreshMessages();
                    mHandler.postDelayed(this, 60000);
                }
            }
        };

        mHandler.postDelayed(r, 100);
        mBtnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEtMessage.getText().toString().equalsIgnoreCase("")){
                    sendMessage(mEtMessage.getText().toString());
                }
            }
        });
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
                SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String token = settings.getString("accesstoken", "");
                return token;
            }
        };
        pairs.add(nvp);
        if (((GPSActivity) getActivity()).mCurrentLocation!=null){
            pairs.add(new BasicNameValuePair("latitude",""+((GPSActivity) getActivity()).mCurrentLocation.getLatitude()));
            pairs.add(new BasicNameValuePair("longitude",""+((GPSActivity) getActivity()).mCurrentLocation.getLongitude()));
        }
        NameValuePair nvpChannel = new NameValuePair() {
            @Override
            public String getName() {
                return "channelid";
            }

            @Override
            public String getValue() {
                return ""+channelID;
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
        PostRequest pr = new PostRequest(getActivity(),"http://www.raphaelbischof.fr/messaging/?function=sendmessage",pairs,1);
        pr.setOnContentLoaded(this);
        pr.execute();
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
                SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
                String token = settings.getString("accesstoken", "");
                return token;
            }
        };
        pairs.add(nvp);
        NameValuePair nvpChannel = new NameValuePair() {
            @Override
            public String getName() {
                return "channelid";
            }

            @Override
            public String getValue() {
                return ""+channelID;
            }
        };
        pairs.add(nvpChannel);
        PostRequest pr = new PostRequest(getActivity(),"http://www.raphaelbischof.fr/messaging/?function=getmessages",pairs,0);
        pr.setOnContentLoaded(this);
        pr.execute();
    }

    @Override
    public void onContentLoaded(String news, int requestCode) {
        if (requestCode==0){
            Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd' 'HH:mm:ss").create();
            Messages messages = gson.fromJson(news,Messages.class);
            if (getActivity()!=null&&messages!=null&&messages.messages!=null)
            mLvMessages.setAdapter(new MessageAdapter(getActivity(),messages.messages));
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
    }

    @Override
    public void onItemClick(AdapterView<?> parent,final View view, int position, long id) {
        String[] arr = {getString(R.string.add_friend),getString(R.string.view_on_map)};
            new AlertDialog.Builder(getActivity())
                    .setIcon(android.R.drawable.ic_dialog_alert)//drawable de l'icone à gauche du titre
                    .setTitle(R.string.make_a_choice)//Titre de l'alert dialog
                    .setItems(arr, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {//which = la position de l'item appuyé
                            if (which == 0) {
                                new AlertDialog.Builder(getActivity())
                                        .setIcon(android.R.drawable.ic_dialog_alert)
                                        .setTitle(R.string.add_friend)
                                        .setMessage(R.string.add_friend_message)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {

                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Message m = (Message) view.getTag();
                                                UserDatasource userDatasource = new UserDatasource(getActivity());
                                                userDatasource.open();
                                                userDatasource.createFriend(m.imageUrl, m.username, m.userID);
                                                userDatasource.close();
                                                //Stop the activity
                                            }

                                        })
                                        .setNegativeButton(R.string.no, null)
                                        .show();
                            } else {
                                Intent i = new Intent(getActivity(), MapsActivity.class);
                                i.putExtra("latitude",((Message)view.getTag()).latitude);
                                i.putExtra("longitude",((Message)view.getTag()).longitude);
                                i.putExtra("user",((Message)view.getTag()).username);
                                startActivity(i);
                            }
                        }
                    })//items de l'alert dialog
                    .show();
    }

    @Override
    public void onClick(View v) {
        if (v.getId()==R.id.btnImage){
            mImageID = UUID.randomUUID();
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+mImageID.toString() +".jpg")));
            startActivityForResult(intent, PICTURE_REQUEST_CODE);
        }else if (v.getId()==R.id.btnSound){
            Intent i = new Intent(getActivity(), SoundActivity.class);
            i.putExtra("channelid",channelID);
            startActivity(i);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            resizeFile(new File(getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+mImageID.toString() +".jpg"),getActivity());
        } catch (IOException e) {
        }
        SharedPreferences settings = getActivity().getSharedPreferences(LoginActivity.PREFS_NAME, 0);
        String token = settings.getString("accesstoken", "");
        ArrayList<NameValuePair> arrayList = new ArrayList<>();
        arrayList.add(new BasicNameValuePair("accesstoken",token));
        arrayList.add(new BasicNameValuePair("channelid",""+channelID));
        new UploadFileToServer(getActivity(),getActivity().getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)+"/"+mImageID.toString() +".jpg",arrayList,this).execute();

    }

    /// /decodes image and scales it to reduce memory consumption
    private void resizeFile(File f,Context context) throws IOException {
        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(new FileInputStream(f),null,o);

        //The new size we want to scale to
        final int REQUIRED_SIZE=400;

        //Find the correct scale value. It should be the power of 2.
        int scale=1;
        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
            scale*=2;

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize=scale;
        Bitmap bitmap = BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        int i = getCameraPhotoOrientation(context, Uri.fromFile(f),f.getAbsolutePath());
        if (o.outWidth>o.outHeight)
        {
            Matrix matrix = new Matrix();
            matrix.postRotate(i); // anti-clockwise by 90 degrees
            bitmap = Bitmap.createBitmap(bitmap , 0, 0, bitmap .getWidth(), bitmap .getHeight(), matrix, true);
        }
        try {
            f.delete();
            FileOutputStream out = new FileOutputStream(f);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static int getCameraPhotoOrientation(Context context, Uri imageUri, String imagePath) throws IOException {
        int rotate = 0;
        context.getContentResolver().notifyChange(imageUri, null);
        File imageFile = new File(imagePath);
        ExifInterface exif = new ExifInterface(
                imageFile.getAbsolutePath());
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);

        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        return rotate;
    }

    @Override
    public void onResponse(String result) {
        Toast.makeText(getActivity(),"Message envoyé",Toast.LENGTH_LONG).show();
    }

    @Override
    public void onFailed(IOException error) {

    }
}
