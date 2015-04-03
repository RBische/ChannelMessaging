package bischof.raphael.channelmessaging.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.model.UserMessage;
import bischof.raphael.channelmessaging.utils.ImageHelper;
import bischof.raphael.channelmessaging.utils.ImageRequest;
import bischof.raphael.channelmessaging.utils.OnImageContentLoadedListener;

/**
 * Created by biche on 01/02/2015.
 */
public class UserMessageAdapter extends ArrayAdapter<UserMessage> {
    public UserMessageAdapter(Context context, int resource) {
        super(context, resource);
    }

    public UserMessageAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public UserMessageAdapter(Context context, int resource, UserMessage[] objects) {
        super(context, resource, objects);
    }

    public UserMessageAdapter(Context context, int resource, int textViewResourceId, UserMessage[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public UserMessageAdapter(Context context, int resource, List<UserMessage> objects) {
        super(context, resource, objects);
    }

    public UserMessageAdapter(Context context, int resource, int textViewResourceId, List<UserMessage> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.custom_row_usermessage, null);
        }
        UserMessage message = getItem(position);
        if (message!=null){
            view.setTag(message);
            if (message.sendbyme==0){
                final ImageView imageView = (ImageView)view.findViewById(R.id.ivStart);
                ImageRequest imageRequest = new ImageRequest(getContext(),message.imageUrl,0);
                imageRequest.setOnContentLoaded(new OnImageContentLoadedListener() {
                    @Override
                    public void onImageContentLoaded(Bitmap news, int requestCode) {
                        imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(news, 500));
                    }
                });
                imageRequest.execute();
            }else{
                final ImageView imageView = (ImageView)view.findViewById(R.id.ivEnd);
                ImageRequest imageRequest = new ImageRequest(getContext(),message.imageUrl,0);
                imageRequest.setOnContentLoaded(new OnImageContentLoadedListener() {
                    @Override
                    public void onImageContentLoaded(Bitmap news, int requestCode) {
                        imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(news, 500));
                    }
                });
                imageRequest.execute();
            }
            TextView textView = (TextView)view.findViewById(R.id.textView);
            textView.setText(message.message);
            if (message.everRead!=1){
                textView.setTypeface(null, Typeface.BOLD);
            }
            TextView textView1 = (TextView)view.findViewById(R.id.textView2);
            textView1.setText(message.username);
        }
        return view;
    }
}
