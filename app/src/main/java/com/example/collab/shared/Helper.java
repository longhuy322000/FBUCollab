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
import java.util.Date;
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

    public static String getRelativeTimeAgo(Date date) {
        final int SECOND_MILLIS = 1000;
        final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
        final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
        final int DAY_MILLIS = 24 * HOUR_MILLIS;

        long time = date.getTime();

        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "a minute ago";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return diff / MINUTE_MILLIS + " minutes ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "an hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hours ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "yesterday";
        } else {
            return diff / DAY_MILLIS + " days ago";
        }
    }

    public static String listToString(List<String> list) {
        if (list == null || list.isEmpty())
            return "";
        String str = "";
        for (int i = 0; i < list.size(); i++) {
            if (i == 0)
                str = list.get(i);
            else str += ", " + list.get(i);
        }
        return str;
    }
}
