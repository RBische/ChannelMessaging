package bischof.raphael.channelmessaging.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.model.Message;
import bischof.raphael.channelmessaging.model.User;
import bischof.raphael.channelmessaging.utils.ImageHelper;
import bischof.raphael.channelmessaging.utils.ImageRequest;
import bischof.raphael.channelmessaging.utils.OnImageContentLoadedListener;

/**
 * Created by biche on 01/02/2015.
 */
public class GridviewFriendAdapter extends ArrayAdapter<User> {
    public GridviewFriendAdapter(Context context, int resource) {
        super(context, resource);
    }

    public GridviewFriendAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId);
    }

    public GridviewFriendAdapter(Context context, int resource, User[] objects) {
        super(context, resource, objects);
    }

    public GridviewFriendAdapter(Context context, int resource, int textViewResourceId, User[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public GridviewFriendAdapter(Context context, int resource, List<User> objects) {
        super(context, resource, objects);
    }

    public GridviewFriendAdapter(Context context, int resource, int textViewResourceId, List<User> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.cell_layout, null);
        }
        User message = getItem(position);
        if (message!=null){
            TextView textView = (TextView)view.findViewById(R.id.tvUsername);
            textView.setText(message.username);
            view.setTag(message);
            final ImageView imageView = (ImageView)view.findViewById(R.id.ivPhoto);
            ImageRequest imageRequest = new ImageRequest(getContext(),message.imageUrl,0);
            imageRequest.setOnContentLoaded(new OnImageContentLoadedListener() {
                @Override
                public void onImageContentLoaded(Bitmap news, int requestCode) {
                    imageView.setImageBitmap(news);
                }
            });
            imageRequest.execute();
        }
        return view;
    }
}
