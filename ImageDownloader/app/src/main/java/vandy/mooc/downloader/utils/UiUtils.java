package vandy.mooc.downloader.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.design.widget.FloatingActionButton;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Locale;

/**
 * Helper methods shared by various Activities.
 */
public class UiUtils {
    /**
     * Debugging tag.
     */
    private static final String TAG =
        UiUtils.class.getCanonicalName();

    /**
     * Return an uppercase version of the input or null if user gave
     * no input.  If user gave no input and @a showToast is true a
     * toast is displayed to this effect.
     */
    public static String uppercaseInput(Context context, 
                                        String input,
                                        boolean showToast) {
        if (input.isEmpty()) {
            if (showToast)
                UiUtils.showToast(context,
                                  "no input provided");
            return null;
        } else
            // Convert the input entered by the user so it's in
            // uppercase.
            return input.toUpperCase(Locale.ENGLISH);
    }

    /**
     * Show a toast message.
     */
    public static void showToast(Context context,
                                 String message) {
        Toast.makeText(context,
                       message,
                       Toast.LENGTH_SHORT).show();
    }

    /**
     * This method is used to hide a keyboard after a user has
     * finished typing the url.
     */
    public static void hideKeyboard(Activity activity,
                                    IBinder windowToken) {
        InputMethodManager mgr =
            (InputMethodManager) activity.getSystemService
            (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(windowToken, 0);
    }

    /**
     * Set the result of the Activity to indicate whether the
     * operation on the content succeeded or not.
     *
     * @param activity
     *          The Activity whose result is being set.
     * @param pathToContent
     *          The pathname to the content file.
     * @param failureReason
     *          String to add to add as an extra to the Intent passed
     *          back to the originating Activity if the @a
     *          pathToContent is null.
     */
    public static void setActivityResult(Activity activity,
                                         Uri pathToContent,
                                         String failureReason) {
        if (pathToContent == null)
            // Indicate why the operation on the content was
            // unsuccessful or was cancelled.
            activity.setResult
                (Activity.RESULT_CANCELED,
                 new Intent("").putExtra("reason",
                                         failureReason));
        else
            // Set the result of the Activity to designate the path to
            // the content file resulting from a successful operation.
            activity.setResult(Activity.RESULT_OK,
                               new Intent("",
                                          pathToContent));
    }

    /**
     * @return True if the caller is running on the UI thread, else
     * false.
     */
    public static boolean runningOnUiThread() {
        return Thread.currentThread() == Looper.getMainLooper().getThread();
    }

    /**
     * FAB animator that displays the FAB.
     * @param fab The FAB to be displayed
     */
    public static void showFab(FloatingActionButton fab) {
        fab.show();
        fab.animate()
           .translationY(0)
           .setInterpolator(new DecelerateInterpolator(2))
           .start();
    }

    /**
     * FAB animator that hides the FAB.
     * @param fab The FAB to be hidden
     */
    public static void hideFab (FloatingActionButton fab) {
        fab.hide();
        fab.animate()
           .translationY(fab.getHeight() + 100)
           .setInterpolator(new AccelerateInterpolator(2))
           .start();
    }

    /**
     * Reveals the EditText.
     * @param text EditText to be revealed
     */
    public static void revealEditText (EditText text) {
        // Get x and y positions of the view with a slight offset
        // to give the illusion of reveal happening from FAB.
        int cx = text.getRight() - 30;
        int cy = text.getBottom() - 60;

        // Radius gives the reveal the circular outline.
        int finalRadius = Math.max(text.getWidth(),
                text.getHeight());

        // This creates a circular reveal that is used starting from
        // cx and cy with a radius of 0 and then expanding to finalRadius.
        Animator anim =
                ViewAnimationUtils.createCircularReveal(text,
                        cx,
                        cy,
                        0,
                        finalRadius);
        text.setVisibility(View.VISIBLE);
        anim.start();
    }

    /**
     * Hides the EditText
     * @param text EditText to be hidden.
     */
    public static void hideEditText(final EditText text) {
        // Get x and y positions of the view with a slight offset
        // to give the illusion of reveal happening from FAB.
        int cx = text.getRight() - 30;
        int cy = text.getBottom() - 60;

        // Gets the initial radius for the circular reveal.
        int initialRadius = text.getWidth();

        // This creates a circular motion that appears to be going back into the
        // FAB from cx and cy with the initial radius as the width and final radius
        // as 0 since it is animating back into the FAB.
        Animator anim =
                ViewAnimationUtils.createCircularReveal(text,
                        cx,
                        cy,
                        initialRadius,
                        0);

        // Create a listener so that we can make the EditText
        // invisible once the circular animation is over.
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                text.setVisibility(View.INVISIBLE);
            }
        });

        anim.start();

        // Clear the text from the EditText when the user touches the X FAB
        text.getText().clear();
    }

    /**
     * Ensure this class is only used as a utility.
     */
    private UiUtils() {
        throw new AssertionError();
    } 
}
