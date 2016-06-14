package course.maplocation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;

/**
 * An Activity that maps a location from an address given by the user.
 */
public class MapLocationActivity extends Activity {
    /**
     * Debugging tag used by the Android logger.
     */
    private String TAG = getClass().getSimpleName();

    /**
     * Address entered by the user.
     */
    private EditText mAddrText;

    private EditText mEditTextReveal;

    private boolean isEditTextVisible;

    private ImageButton mAddButton;

    /**
     * Hook method called when a new instance of Activity is created.
     * One time initialization code goes here, e.g., UI layout and
     * class scope variable initialization.
     *
     * @param savedInstanceState that contains saved state information.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Always call super class for necessary
        // initialization/implementation.
        super.onCreate(savedInstanceState);

        // Set the default layout.
        setContentView(R.layout.main);

        // Cache the EditText object in a field.
        mAddrText = (EditText) findViewById(R.id.location);
        mAddButton = (ImageButton) findViewById(R.id.btn_add);
        mEditTextReveal = (EditText) findViewById(R.id.location);
        mEditTextReveal.setVisibility(View.INVISIBLE);
        isEditTextVisible = false;
    }

    /**
     * Hook method called after onCreate() or after onRestart() (when
     * the activity is being restarted from stopped state).  Should
     * re-acquire resources relinquished when activity was stopped
     * (onStop()) or acquire those resources for the first time after
     * onCreate().
     */
    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "The activity is about to become visible.");
    }

    /**
     * Hook method called after onRestoreStateInstance(Bundle) only if
     * there is a prior saved instance state in Bundle object.
     * onResume() is called immediately after onStart().  onResume()
     * is called when user resumes activity from paused state
     * (onPause()) User can begin interacting with activity.  Place to
     * start animations, acquire exclusive resources, such as the
     * camera.
     */
    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "The activity has become visible (it is now \"resumed\")");
    }

    /**
     * Hook method called when an Activity loses focus but is still
     * visible in background. May be followed by onStop() or
     * onResume().  Delegate more CPU intensive operation to onStop
     * for seamless transition to next activity.  Save persistent
     * state (onSaveInstanceState()) in case app is killed.  Often
     * used to release exclusive resources.
     */
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG,
                "Another activity is taking focus (this activity is about to be \"paused\")");
    }

    /**
     * Called when Activity is no longer visible.  Release resources
     * that may cause memory leak. Save instance state
     * (onSaveInstanceState()) in case activity is killed.
     */
    @Override
    protected void onStop() {
        super.onStop();
        Log.i(TAG, "The activity is no longer visible (it is now \"stopped\")");
    }

    /**
     * Hook method called when user restarts a stopped activity.  Is
     * followed by a call to onStart() and onResume().
     */
    @Override
    protected void onRestart(){
        // Always call super class for necessary
        // initialization/implementation.
        super.onRestart();
        Log.d(TAG, "The activity is about to be restarted()");
    }

    /**
     * Hook method that gives a final chance to release resources and
     * stop spawned threads.  onDestroy() may not always be
     * called-when system kills the hosting process.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "The activity is about to be destroyed.");
    }

    /**
     * Called by the Android Activity framework when the user clicks
     * the "Show Map" button.
     */
    public void showMap() {
        try {
            // Get the address entered by the user.
            String address = mAddrText.getText().toString();

            // Replace spaces (' ') with '+' signs to make the browser
            // happy.
            address = address.replace(' ', '+');

            // Hide the keyboard.
            hideKeyboard();

            // Launch the activity by sending an intent.  Android will
            // choose the right one or let the user choose if more
            // than one Activity can handle it.

            // Create an Intent that will launch the "Maps" app.
            final Intent geoIntent = makeGeoIntent(address);

            // Check to see if there's a Map app to handle the "geo"
            // intent.
            if (geoIntent.resolveActivity(getPackageManager()) != null)
                startActivity(geoIntent);
            else
                // Start the Browser app instead.
                startActivity(makeMapsIntent(address));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addAddress(View v) {
        Animatable mAnimatable;
        if (!isEditTextVisible) {
            revealEditText(mEditTextReveal);
            mEditTextReveal.requestFocus();
            mAddButton.setImageResource(R.drawable.icon_morph);
            mAnimatable = (Animatable) (mAddButton).getDrawable();
            mAnimatable.start();

        } else {
            hideEditText(mEditTextReveal);
            mAddButton.setImageResource(R.drawable.icon_morph_reverse);
            mAnimatable = (Animatable) (mAddButton).getDrawable();
            mAnimatable.start();
            showMap();
        }
    }


    public void revealEditText (EditText text) {
        int cx = text.getRight() - 30;
        int cy = text.getBottom() - 60;
        int finalRadius = Math.max(text.getWidth(), text.getHeight());
        Animator anim = ViewAnimationUtils.createCircularReveal(text, cx, cy, 0, finalRadius);
        text.setVisibility(View.VISIBLE);
        isEditTextVisible = true;
        anim.start();
    }


    public void hideEditText(final EditText text) {
        int cx = text.getRight() - 30;
        int cy = text.getBottom() - 60;
        int initialRadius = text.getWidth();
        Animator anim = ViewAnimationUtils.createCircularReveal(text, cx, cy, initialRadius, 0);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                text.setVisibility(View.INVISIBLE);
            }
        });
        isEditTextVisible = false;
        anim.start();
    }


    /**
     * Hide the keyboard after a user has finished typing the acronym
     * they want expanded.
     */
    protected void hideKeyboard() {
        InputMethodManager mgr =
                (InputMethodManager) getSystemService
                        (Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(mAddrText.getWindowToken(),
                0);
    }


    /**
     * Factory method that returns an Intent that designates the "Map"
     * app.
     */
    private Intent makeGeoIntent(String address) {
        // Note the "loose coupling" between the Intent and the app(s)
        // that handle this Intent.
        return new Intent(Intent.ACTION_VIEW,
                Uri.parse("geo:0,0?q="
                        + address));
    }

    /**
     * Factory method that returns an Intent that designates the
     * "Browser" app.
     */
    private Intent makeMapsIntent(String address) {
        // Note the "loose coupling" between the Intent and the app(s)
        // that handle this Intent.
        return new Intent(Intent.ACTION_VIEW,
                Uri.parse("http://maps.google.com/?q="
                        + address));
    }
}
