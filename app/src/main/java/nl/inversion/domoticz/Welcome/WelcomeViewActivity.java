package nl.inversion.domoticz.Welcome;

import android.animation.ArgbEvaluator;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import nl.inversion.domoticz.R;

public class WelcomeViewActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private WelcomePageAdapter mAdapter;
    private ViewPager mPager;
    private final List<Fragment> fList = new ArrayList<>();
    private TextView buttonPrev, buttonNext;
    private RelativeLayout navigation;
    private Integer[] background_colors;
    ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        /*
            Setting this makes sure we draw fullscreen, without this the transparent Activity shows
            the bright orange notification header from the main Activity below
        */
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            window.setFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        setUpBackgroundColors();
        buildLayout();
    }

    @Override
    public void onBackPressed() {

        if (mPager.getCurrentItem() > 0) mPager.setCurrentItem(mPager.getCurrentItem() - 1);

    }

    private void setUpBackgroundColors(){

        Integer color1 = getResources().getColor(R.color.welcome1_background);
        Integer color2 = getResources().getColor(R.color.welcome2_background);
        Integer color3 = getResources().getColor(R.color.welcome3_background);
        Integer color4 = getResources().getColor(R.color.welcome4_background);

        Integer[] colors_temp = {color1, color2, color3, color4};
        background_colors = colors_temp;

    }


    private void buildLayout() {

        List<Fragment> fragments = getFragments();
        mAdapter = new WelcomePageAdapter(getSupportFragmentManager(), fragments);

        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);

        navigation = (RelativeLayout) findViewById(R.id.navigation);
        navigation.setBackgroundResource(R.color.welcome1_background);

        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        float radius = mIndicator.getRadius();
        mIndicator.setRadius(radius + 4);
        mIndicator.setFillColor(getResources().getColor(android.R.color.darker_gray));
        mIndicator.setOnPageChangeListener(this);

        buttonPrev = (TextView) findViewById(R.id.btn_prev);
        buttonPrev.setText(getString(R.string.welcome_button_previous).toUpperCase());
        buttonPrev.setOnClickListener(this);

        buttonNext = (TextView) findViewById(R.id.btn_next);
        buttonNext.setText(getString(R.string.welcome_button_next).toUpperCase());
        buttonNext.setTextColor(getResources().getColor(R.color.white));
        buttonNext.setOnClickListener(this);
    }

    private List<Fragment> getFragments(){

        fList.add(WelcomePage1.newInstance());
        fList.add(WelcomePage2.newInstance());
        fList.add(WelcomePage3.newInstance());
        fList.add(WelcomePage4.newInstance());

        return fList;
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.btn_next:
                if (mPager.getCurrentItem() < fList.size() - 1) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                } else {
                    endWelcomeWizard();
                }
                break;

            case R.id.btn_prev:
                if (mPager.getCurrentItem() != 0) mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                break;
        }
    }

    private void endWelcomeWizard() {
        super.onBackPressed();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if (position < (mAdapter.getCount() -1) && position < (background_colors.length - 1)) {

            mPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, background_colors[position], background_colors[position + 1]));

        } else {

            // the last page color
            mPager.setBackgroundColor(background_colors[background_colors.length - 1]);

        }

    }

    @Override
    public void onPageSelected(int position) {
        pageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void pageSelected(int position) {

        if (position == 0) {
            // First page
            navigation.setBackgroundResource(R.color.welcome1_background);
            buttonPrev.setVisibility(View.INVISIBLE);
        } else if (position == fList.size() - 1) {
            // Last page
            buttonNext.setText(getString(R.string.welcome_button_finish).toUpperCase());
        } else {
            // Everything in between
            navigation.setBackgroundResource(R.color.default_background_color_light);
            buttonPrev.setVisibility(View.VISIBLE);
            buttonNext.setText(getString(R.string.welcome_button_next).toUpperCase());
            buttonNext.setTextColor(getResources().getColor(R.color.light_gray));
        }
    }
}