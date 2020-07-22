package com.example.collab.shared;

import android.app.Activity;
import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.parse.ParseFile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class Helper {

    public static void loadImage(Context context, ImageView imageView, ParseFile file) {
        if (file != null) {
            Glide.with(context)
                    .load(file.getUrl())
                    .into(imageView);
        }
    }

    public static void loadCircleCropImage(Context context, ImageView imageView, ParseFile file) {
        if (file != null) {
            Glide.with(context)
                    .load(file.getUrl())
                    .transform(new CircleCrop())
                    .into(imageView);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static String getRelativeTimeAgo(String rawJsonDate) {
        String twitterFormat = "EEE MMM dd HH:mm:ss ZZZZZ yyyy";
        SimpleDateFormat sf = new SimpleDateFormat(twitterFormat, Locale.ENGLISH);
        sf.setLenient(true);

        String relativeDate = "";
        try {
            long dateMillis = sf.parse(rawJsonDate).getTime();
            relativeDate = DateUtils.getRelativeTimeSpanString(dateMillis,
                    System.currentTimeMillis(), DateUtils.SECOND_IN_MILLIS).toString();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return relativeDate;
    }

    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty())
            return "";
        String str = "";
        for (int i=0; i<list.size(); i++) {
            if (i == 0)
                str = list.get(i);
            else str += ", " + list.get(i);
        }
        return str;
    }
}