package com.thanongsine.liveat500px.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.thanongsine.liveat500px.R;

/**
 * Created by nuuneoi on 11/16/2014.
 */
public class PhotoListItem extends FrameLayout {

    TextView txtName;
    TextView txtDescription;
    ImageView ivImg;

    public PhotoListItem(Context context) {
        super(context);
        initInflate();
        initInstances();
    }

    public PhotoListItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initInflate();
        initInstances();
        initWithAttrs(attrs);
    }

    public PhotoListItem(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initInflate();
        initInstances();
        initWithAttrs(attrs);
    }

    private void initInflate() {
        LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.list_item_photo, this);
    }

    private void initInstances() {
        //findViewById here
        txtName = (TextView) findViewById(R.id.txt_name);
        txtDescription = (TextView) findViewById(R.id.txt_description);
        ivImg = (ImageView) findViewById(R.id.iv_img);

    }

    private void initWithAttrs(AttributeSet attrs) {

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int width = MeasureSpec.getSize(widthMeasureSpec); //width in px
        int height = width*2/3;
        int newHeightMeasureSpec = MeasureSpec
                    .makeMeasureSpec(
                        height,
                        MeasureSpec.EXACTLY
                );

        //send to child views
        super.onMeasure(widthMeasureSpec, newHeightMeasureSpec);
        //to self
        setMeasuredDimension(width, height);
    }

    public void  setTxtName(String text){
        txtName.setText(text);
    }

    public void setTxtDescription(String text){
        txtDescription.setText(text);
    }

    public void setIvImg(String url){
        //TODO: Load Image
        Glide.with(getContext())
                .load(url)
                .placeholder(R.drawable.loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(ivImg);
    }

    //TODO: Create selector.xml
}
