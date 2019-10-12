package com.ghts.player.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.ghts.player.enumType.POS;
import com.ghts.player.utils.Const;
import com.ghts.player.utils.PubUtil;

import java.lang.ref.SoftReference;

/**
 * Created by lijingjing on 17-6-20.
 * 静态图片类
 */
public class MyImgView extends BaseModuleInfo{
    private Context context;
    private Bitmap bm;
    private ImageView imageView;
    String filePath;
    String LogoSofton;

    public View getView(Context context) {
        this.context = context;
        imageView = new ImageView(context);
        try {
            bm = PubUtil.fitSizeImg(filePath);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            //            imageView.setImageBitmap(bm);
            SoftReference<Bitmap> mReference = new SoftReference<Bitmap>(bm);
            if (bm != null && !bm.isRecycled()) {
                bm = null;
            }
            // 获取软引用中的位图变量
            Bitmap getBitmap = mReference.get();
            if (getBitmap != null) {
                Drawable bk_drawable = new BitmapDrawable(getBitmap);
                imageView.setBackgroundDrawable(bk_drawable);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        initPosition(imageView,pos);
        return imageView;
    }
    void initPosition(View view, POS pos) {
        view.setX(pos.getLeft());
        view.setY(Const.screenH - pos.getTop() - pos.getHeight());
        view.setLayoutParams(new ViewGroup.LayoutParams(pos.getWidth(), pos.getHeight()));
    }
    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public void setImageView(ImageView imageView) {
        this.imageView = imageView;
    }


    public String getLogoSofton() {
        return LogoSofton;
    }

    public void setLogoSofton(String logoSofton) {
        LogoSofton = logoSofton;
    }

}

