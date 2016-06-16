package vandy.mooc.downloader.framework.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.StringRes;
import android.support.annotation.UiThread;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/**
 * Provides some general utility helper methods.
 */
public final class ViewUtils {
    /**
     * Ensure this class is only used as a utility.
     */
    private ViewUtils() {
        throw new AssertionError();
    }

    /**
     * Logging tag.
     */
    private static final String TAG = "ViewUtils";

    /**
     * Static string containing the last toast message that was
     * displayed by calling either of the showToast helper methods.
     * This value is not thread-safe and is only used by the Testing
     * framework.
     */
    @SuppressWarnings("StaticNonFinalField")
    private static String sLastToast = null;

    /**
     * Helper to show a short toast message.
     *
     * @param context activity context
     * @param text    string to display
     */
    @SuppressWarnings({"SameParameterValue", "unused"})
    @UiThread
    public static void showToast(Context context, String text) {
        if (text == null || text.isEmpty()) {
            throw new IllegalArgumentException(
                    "showToast requires a valid string");
        }

        Toast.makeText(context, text, Toast.LENGTH_SHORT).show();

        // Also duplicate Toast message in log file for debugging.
        Log.d(TAG, text);
        sLastToast = text;
    }

    /**
     * Helper to show a short toast message.
     *
     * @param context activity context
     * @param id      resource id of string to display
     */
    @UiThread
    public static void showToast(Context context, @StringRes int id) {
        Toast.makeText(context, id, Toast.LENGTH_SHORT).show();

        // Also duplicate Toast message in log file fro debugging.
        Log.d(TAG, context.getResources().getString(id));
        sLastToast = context.getResources().getString(id);
    }

    /**
     * Returns the last toast message displayed by calling either of
     * the showToast() helper methods. This method is only used
     * by the UI testing framework.
     */
    @SuppressWarnings("unused")
    @UiThread
    public static String getLastToast() {
        return sLastToast;
    }

    /**
     * Clears the last toast message displayed by calling either of
     * the showToast() helper methods. This method is only used
     * by the UI testing framework.
     */
    @SuppressWarnings("unused")
    @UiThread
    public static void clearLastToast() {
        sLastToast = null;
    }

    /**
     * Returns the display metrics for the provided context.
     *
     * @param context Any context.
     * @return DisplayMetrics instance.
     */
    public static DisplayMetrics getDisplayMetrics(Context context) {
        DisplayMetrics displayMetrics = new DisplayMetrics();

        WindowManager windowManager =
                ((WindowManager) context.getSystemService(
                        Context.WINDOW_SERVICE));

        final Display defaultDisplay = windowManager.getDefaultDisplay();
        defaultDisplay.getMetrics(displayMetrics);

        return displayMetrics;
    }

    /**
     * Hides the soft keyboard for the provided view.
     *
     * @param view The target view for soft keyboard input.
     */
    public static void hideSoftKeyboard(View view) {
        InputMethodManager imm =
                (InputMethodManager) view.getContext().getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
