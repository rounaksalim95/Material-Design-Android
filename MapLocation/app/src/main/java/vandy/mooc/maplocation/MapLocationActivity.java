package vandy.mooc.maplocation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.widget.EditText;
import android.widget.ImageButton;

import vandy.mooc.maplocation.LifecycleLoggingActivity;
import vandy.mooc.maplocation.R;
import vandy.mooc.maplocation.UiUtils;

/**
 * An Activity that uses an Intent to map a location from an address
 * given by the user.
 */
public class MapLocationActivity
        extends LifecycleLoggingActivity {
    /**
     * Debugging tag used by the Android logger.
     */
    private String TAG = getClass().getSimpleName();

    /**
     * Holds a reference to the EditText where the user adds the
     * address.
     */
    private EditText mEditTextReveal;


    /**
     * Keeps track of whether the EditText is visible.
     */
    private boolean mIsEditTextVisible;


    /**
     * Holds a reference to the ImageButton that is used as a floating
     * action button.
     */
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

        // Cache the EditText and ImageButton objects in their
        // respective fields.
        mAddButton = (ImageButton) findViewById(R.id.btn_add);
        mEditTextReveal = (EditText) findViewById(R.id.location);

        // Make EditText invisible and set the boolean accordingly
        mEditTextReveal.setVisibility(View.INVISIBLE);
        mIsEditTextVisible = false;
    }

    /**
     * Called by the Android Activity framework after the user adds an
     * address to map.
     */
    private void showMap() {
        try {
            // Get the address entered by the user.
            String address = mEditTextReveal.getText().toString();

            // Replace spaces (' ') with '+' signs to make the browser
            // happy.
            address = address.replace(' ', '+');

            // Launch the activity by sending an intent.  Android will
            // choose the right one or let the user choose if more
            // than one Activity can handle it.

            // Create an Intent that will launch the "Maps" app.
            final Intent geoIntent = makeMapsIntent(address);

            // Check to see if there's a Map app to handle the "geo"
            // intent.
            if (geoIntent.resolveActivity(getPackageManager()) != null)
                startActivity(geoIntent);
            else
                // Start the Browser app instead.
                startActivity(makeBrowserIntent(address));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Factory method that returns an Intent that designates the "Map"
     * app.
     */
    private Intent makeMapsIntent(String address) {
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
    private Intent makeBrowserIntent(String address) {
        // Note the "loose coupling" between the Intent and the app(s)
        // that handle this Intent.
        return new Intent(Intent.ACTION_VIEW,
                          Uri.parse("http://maps.google.com/?q=" 
                                    + address));
    }

    /**
     * Reveals or hides the EditText as required.
     * @param view The view
     */
    public void addAddress(View view) {
        Animatable mAnimatable;

        // Check if the EditText is visible.
        if (mIsEditTextVisible) {
            // If visible, hide the EditText and set the boolean to false.
            UiUtils.hideEditText(mEditTextReveal);
            mIsEditTextVisible = false;

            // Set Image Resource to start the morph animation of the FAB icon
            // from the tick mark to +.
            mAddButton.setImageResource(R.drawable.icon_morph_reverse);
            mAnimatable = (Animatable) (mAddButton).getDrawable();
            mAnimatable.start();

            // Show the map.
            showMap();
        } else {
            // If EditText is invisible then reveal it using the animation
            // and set boolean to true.
            UiUtils.revealEditText(mEditTextReveal);
            mIsEditTextVisible = true;

            mEditTextReveal.requestFocus();

            // Set Image Resource to start the morph animation of the FAB icon
            // from + to the tick mark.
            mAddButton.setImageResource(R.drawable.icon_morph);
            mAnimatable = (Animatable) (mAddButton).getDrawable();
            mAnimatable.start();
        }
    }
}
