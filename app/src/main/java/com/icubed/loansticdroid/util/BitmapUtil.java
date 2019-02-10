package com.icubed.loansticdroid.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.util.Base64;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;

import java.io.ByteArrayOutputStream;

public class BitmapUtil {
    public static byte[] getBytesFromBitmapInPNG(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, quality, stream);
        return stream.toByteArray();
    }

    public static byte[] getBytesFromBitmapInJPG(Bitmap bitmap, int quality) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream);
        return stream.toByteArray();
    }

    public static Bitmap stringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }

    public static Bitmap getBitMapFromBytes(byte[] bytes){
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String bitMapPNGToString(Bitmap bitmap, int quality){
        return Base64.encodeToString(getBytesFromBitmapInPNG(bitmap, quality), Base64.DEFAULT);
    }

    public static String bitMapJPGToString(Bitmap bitmap, int quality){
        return Base64.encodeToString(getBytesFromBitmapInJPG(bitmap, quality), Base64.DEFAULT);
    }

    public static RequestBuilder<Bitmap> getImageWithGlide(Context context, String uri){
        return Glide.with(context)
                .asBitmap()
                .load(uri);
    }

    public static RequestBuilder<Drawable> getImageAndThumbnailWithGlide(Context context, String uri, String thumbnailUri){
        return Glide.with(context).load(uri).thumbnail(
                Glide.with(context).load(thumbnailUri)
        );
    }

    public static RequestBuilder<Drawable> getImageAndThumbnailWithRequestOptionsGlide(Context context, String uri, String thumbnailUri, RequestOptions requestOptions){
        return Glide.with(context)
                .applyDefaultRequestOptions(requestOptions)
                .load(uri).thumbnail(
                Glide.with(context).load(thumbnailUri)
        );
    }

    public static Bitmap getBitmapFromDrawable(int drawableRes, Context context) {
        Drawable drawable = context.getResources().getDrawable(drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }
}
