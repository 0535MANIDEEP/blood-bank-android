package com.manideep.bloodbank.ui;

import android.view.View;

import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/**
 * Window inset handling.
 *
 * From API 35 Android draws apps edge to edge by default, so without this the
 * first line of every screen sits underneath the status bar.
 */
public final class SystemBars {

    private SystemBars() {
    }

    /** Pads the view by the status and navigation bar insets, plus any cutout. */
    public static void pad(View view) {
        ViewCompat.setOnApplyWindowInsetsListener(view, (v, windowInsets) -> {
            Insets bars = windowInsets.getInsets(
                    WindowInsetsCompat.Type.systemBars() | WindowInsetsCompat.Type.displayCutout());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return WindowInsetsCompat.CONSUMED;
        });
        ViewCompat.requestApplyInsets(view);
    }
}
