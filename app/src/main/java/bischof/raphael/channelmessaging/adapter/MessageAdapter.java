package bischof.raphael.channelmessaging.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import bischof.raphael.channelmessaging.R;
import bischof.raphael.channelmessaging.model.Message;
import bischof.raphael.channelmessaging.utils.ImageHelper;
import bischof.raphael.channelmessaging.utils.ImageRequest;
import bischof.raphael.channelmessaging.utils.OnImageContentLoadedListener;
import bischof.raphael.channelmessaging.utils.SoundRequest;

/**
 * Created by biche on 24/01/2015.
 */
public class MessageAdapter extends ArrayAdapter<Message> {
    public MessageAdapter(Context context, List<Message> objects) {
        super(context, 0, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view=null;
        Message message = getItem(position);
        if (message!=null){

            if (!message.messageImageUrl.equalsIgnoreCase("")){
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.adapter_image, null);
                view.setTag(message);
                final ImageView imageView = (ImageView)view.findViewById(R.id.ivProfil);
                ImageRequest imageRequest = new ImageRequest(getContext(),message.imageUrl,0);
                imageRequest.setOnContentLoaded(new OnImageContentLoadedListener() {
                    @Override
                    public void onImageContentLoaded(Bitmap news, int requestCode) {
                        imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(news,500));
                    }
                });
                imageRequest.execute();
                final ImageView imageViewProfil = (ImageView)view.findViewById(R.id.ivPhoto);
                ImageRequest imageRequestProfil = new ImageRequest(getContext(),message.messageImageUrl,0);
                imageRequestProfil.setOnContentLoaded(new OnImageContentLoadedListener() {
                    @Override
                    public void onImageContentLoaded(Bitmap news, int requestCode) {
                        imageViewProfil.setImageBitmap(news);
                    }
                });
                imageRequestProfil.execute();
            }else if (!message.soundUrl.equalsIgnoreCase("")) {
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.adapter_row_sound, null);
                view.setTag(message);
                final ImageView imageView = (ImageView)view.findViewById(R.id.ivProfil);
                ImageRequest imageRequest = new ImageRequest(getContext(),message.imageUrl,0);
                imageRequest.setOnContentLoaded(new OnImageContentLoadedListener() {
                    @Override
                    public void onImageContentLoaded(Bitmap news, int requestCode) {
                        imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(news, 500));
                    }
                });
                imageRequest.execute();
                File fDir = getContext().getExternalFilesDir(Environment.DIRECTORY_MUSIC);
                fDir.mkdirs();
                final ProgressBar mSeelBar = (ProgressBar)view.findViewById(R.id.sbPlay);
                final File path = new File(fDir, UUID.randomUUID().toString()+".3gp");
                final Button btnPlay = (Button)view.findViewById(R.id.btnPlay);
                SoundRequest sr = new SoundRequest(getContext(),message.soundUrl,path.getAbsolutePath(),0);
                sr.setOnContentLoaded(new SoundRequest.OnSoundContentLoadedListener() {
                    @Override
                    public void onSoundContentLoaded(String news, int requestCode) {
                        loadSound(path,mSeelBar,btnPlay);
                    }
                });
                sr.execute();
            }else{
                LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                view = inflater.inflate(R.layout.custom_row_with_image, null);
                view.setTag(message);
                final ImageView imageView = (ImageView)view.findViewById(R.id.imageView);
                ImageRequest imageRequest = new ImageRequest(getContext(),message.imageUrl,0);
                imageRequest.setOnContentLoaded(new OnImageContentLoadedListener() {
                    @Override
                    public void onImageContentLoaded(Bitmap news, int requestCode) {
                        imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(news,500));
                    }
                });
                imageRequest.execute();
                TextView textView = (TextView)view.findViewById(R.id.textView);
                textView.setText(message.message);
                TextView textView1 = (TextView)view.findViewById(R.id.textView2);
                textView1.setText(message.username);
            }
        }
        return view;
    }

    private void loadSound(final File file,final ProgressBar mSeelBar, final Button btnPlay) {

        btnPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnPlay.setBackground(getContext().getResources().getDrawable(R.drawable.ic_action_pause));
                final MediaPlayer mMediaPlayer = new MediaPlayer();
                try {
                    mMediaPlayer.setDataSource(file.getAbsolutePath());
                    mMediaPlayer.prepare();
                    mMediaPlayer.start();
                } catch (IOException e) {
                    Log.e("", "prepare() failed");
                }
                final int duration = mMediaPlayer.getDuration();
                final int amoungToupdate = duration / 100;
                final Timer mTimer = new Timer();
                mSeelBar.setProgress(0);
                mTimer.schedule(new TimerTask() {

                    @Override
                    public void run() {
                        ((Activity) getContext()).runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (!(amoungToupdate * (mSeelBar.getProgress()+1) >= duration)) {
                                    int p = mSeelBar.getProgress();
                                    p += 1;
                                    mSeelBar.setProgress(p);
                                } else {
                                    btnPlay.setBackground(getContext().getResources().getDrawable(R.drawable.ic_action_play));
                                    mMediaPlayer.release();
                                    mTimer.cancel();
                                }
                            }
                        });
                    }

                    ;
                }, amoungToupdate, amoungToupdate);
            }
        });
    }
}
