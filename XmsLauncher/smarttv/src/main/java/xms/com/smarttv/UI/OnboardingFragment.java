package xms.com.smarttv.UI;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

import xms.com.smarttv.Player.TVPlayerActivity;
import xms.com.smarttv.R;

public class OnboardingFragment extends android.support.v17.leanback.app.OnboardingFragment {
    private String name;
    private String welcome_message;
    private String welcome_image;
    private static final int[] pageTitles = {
            R.string.onboarding_title_welcome,
            R.string.onboarding_title_design,
            R.string.onboarding_title_simple,
            R.string.onboarding_title_project
    };
    private static final int[] pageDescriptions = {
            R.string.onboarding_description_welcome,
            R.string.onboarding_description_design,
            R.string.onboarding_description_simple,
            R.string.onboarding_description_project
    };
    private final int[] pageImages = {
            R.drawable.tv_animation_a,
            R.drawable.tv_animation_b,
            R.drawable.tv_animation_c,
            R.drawable.tv_animation_d
    };
    private static final long ANIMATION_DURATION = 500;
    private Animator mContentAnimator;
    private ImageView mContentView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Set the logo to display a splash animation
        setLogoResourceId(R.drawable.fracarro);
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            name = bundle.getString("name");
            welcome_message = bundle.getString("welcome_message");
            welcome_image = bundle.getString("welcome_image");
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onFinishFragment() {
        super.onFinishFragment();
        // Start TVPlayerActivity
        startActivity(new Intent(getActivity(), TVPlayerActivity.class));
        getActivity().finish();
    }

    @Override
    protected int getPageCount() {
        return pageTitles.length;
    }

    @Override
    protected String getPageTitle(int pageIndex) {
        return (pageIndex == 0) ? name + " " + welcome_message : getString(pageTitles[pageIndex]);
    }

    @Override
    protected String getPageDescription(int pageIndex) {
        return getString(pageDescriptions[pageIndex]);
    }

    @Nullable
    @Override
    protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

//    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//    @Nullable
//    @Override
//    protected View onCreateBackgroundView(LayoutInflater inflater, ViewGroup container) {
//        final View bgView = new View(getActivity());
//        Glide.with(getActivity())
//                .load(welcome_image)
//                .into(new SimpleTarget<Drawable>() {
//                    @Override
//                    public void onResourceReady(Drawable resource, Transition<? super Drawable> transition) {
//                        Drawable drawable = new BitmapDrawable(resource);
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
//                            bgView.setBackground(drawable);
//                        }
//
//                    }
//                });
//        bgView.setBackgroundTintMode(android.graphics.PorterDuff.Mode.MULTIPLY);
//        bgView.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.WhiteSmokeTransparent)));
//        return bgView;
//    }

    @Nullable
    @Override
    protected View onCreateContentView(LayoutInflater inflater, ViewGroup container) {
        mContentView = new ImageView(getActivity());
        mContentView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mContentView.setPadding(0, 32, 0, 32);
        return mContentView;
    }

    @Nullable
    @Override
    protected View onCreateForegroundView(LayoutInflater inflater, ViewGroup container) {
        return null;
    }

    @Override
    protected void onPageChanged(final int newPage, int previousPage) {
        if (mContentAnimator != null) {
            mContentAnimator.end();
        }
        ArrayList<Animator> animators = new ArrayList<>();
        Animator fadeOut = createFadeOutAnimator(mContentView);

        fadeOut.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mContentView.setImageDrawable(getResources().getDrawable(pageImages[newPage]));
                ((AnimationDrawable) mContentView.getDrawable()).start();
            }
        });
        animators.add(fadeOut);
        animators.add(createFadeInAnimator(mContentView));
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animators);
        set.start();
        mContentAnimator = set;
    }
    @Override
    protected Animator onCreateEnterAnimation() {
        mContentView.setImageDrawable(getResources().getDrawable(pageImages[0]));
        ((AnimationDrawable) mContentView.getDrawable()).start();
        mContentAnimator = createFadeInAnimator(mContentView);
        return mContentAnimator;
    }

    private Animator createFadeInAnimator(View view) {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 0.0f, 1.0f).setDuration(ANIMATION_DURATION);
    }

    private Animator createFadeOutAnimator(View view) {
        return ObjectAnimator.ofFloat(view, View.ALPHA, 1.0f, 0.0f).setDuration(ANIMATION_DURATION);
    }
}