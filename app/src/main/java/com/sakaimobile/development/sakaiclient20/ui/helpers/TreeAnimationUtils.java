package com.sakaimobile.development.sakaiclient20.ui.helpers;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import androidx.recyclerview.widget.RecyclerView;

public final class TreeAnimationUtils {

    private static final float HEIGHT_TO_SPEED_RATIO = 3.5f;

    /**
     * Expands the {@link RecyclerView} by measuring its initial and target height,
     * then applying an {@link Animation} that increases the view height from the initial
     * height to the target height.
     */
    public static void expandRecyclerView(RecyclerView recyclerView) {
        // RecyclerView needs to be visible for the animation to take effect
        recyclerView.setVisibility(View.VISIBLE);

        // Measure the layout's desired height (this step does not affect the
        // actual height, just measures what it would be if the below dimensions were applied).
        recyclerView.measure(RecyclerView.LayoutParams.MATCH_PARENT,
                RecyclerView.LayoutParams.WRAP_CONTENT);
        final int targetHeight = recyclerView.getMeasuredHeight();

        // Initial height of 0 causes glitchy animation (presumably a height of 0
        // indicates that the view should fill its parent or the screen)
        recyclerView.getLayoutParams().height = 1;

        // Create the animation to expand the view
        Animation expansionAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                int newHeight = (int) ( targetHeight * interpolatedTime );
                recyclerView.getLayoutParams().height = newHeight;
                recyclerView.requestLayout();
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        // Duration of 200 milliseconds is the same as the default toggle
        // animation time for all nodes
        expansionAnimation.setDuration(getAnimationDuration(targetHeight));
        recyclerView.startAnimation(expansionAnimation);
    }

    /**
     * Performs the exact opposite of {@code expandRecyclerView}.
     */
    public static void collapseRecyclerView(RecyclerView recyclerView) {
        // See expandRecyclerView for how this type of animation works.
        final int initialHeight = recyclerView.getMeasuredHeight();

        Animation collapseAnimation = new Animation() {
            @Override
            protected void applyTransformation(float interpolatedTime, Transformation transformation) {
                if(interpolatedTime < 1.0) {
                    int newHeight = initialHeight - (int) (initialHeight * interpolatedTime);
                    recyclerView.getLayoutParams().height = newHeight;
                    recyclerView.requestLayout();
                } else {
                    recyclerView.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean willChangeBounds() {
                return true;
            }
        };

        collapseAnimation.setDuration(getAnimationDuration(initialHeight));
        recyclerView.startAnimation(collapseAnimation);
    }

    private static long getAnimationDuration(int height) {
        return (long) (height / HEIGHT_TO_SPEED_RATIO);
    }

}
