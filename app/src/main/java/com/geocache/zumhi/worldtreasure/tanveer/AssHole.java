package com.geocache.zumhi.worldtreasure.tanveer;

import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;


import kotlin.Unit;

public class AssHole {
    public static void measureView(final View view, BiConsumer<Integer,Integer> listener) {
        ViewTreeObserver viewTreeObserver = view.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int viewWidth = view.getWidth();
                    int viewHeight = view.getHeight();
                    listener.accept(viewWidth,viewHeight);

                }
            });
        }
    }
}
