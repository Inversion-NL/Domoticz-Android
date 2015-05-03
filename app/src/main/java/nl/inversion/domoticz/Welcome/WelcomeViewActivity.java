package nl.inversion.domoticz.Welcome;

import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.black));
        }

        buildLayout();
    }

    @Override
    public void onBackPressed() {}

    private void buildLayout() {

        List<Fragment> fragments = getFragments();
        mAdapter = new WelcomePageAdapter(getSupportFragmentManager(), fragments);

        mPager = (ViewPager) findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);

        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        float radius = mIndicator.getRadius();
        mIndicator.setRadius(radius + 4);
        mIndicator.setFillColor(getResources().getColor(android.R.color.darker_gray));
        mIndicator.setOnPageChangeListener(this);

        buttonPrev = (TextView) findViewById(R.id.btn_prev);
        buttonPrev.setOnClickListener(this);

        buttonNext = (TextView) findViewById(R.id.btn_next);
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
                    super.onBackPressed();
                }
                break;

            case R.id.btn_prev:
                if (mPager.getCurrentItem() != 0) mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        pageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    private void pageSelected(int position) {

        if (position == 0) {
            buttonPrev.setVisibility(View.INVISIBLE);
        } else if (position == fList.size() - 1) {
            buttonNext.setText("finish");
        } else {
            buttonPrev.setVisibility(View.VISIBLE);
            buttonNext.setText("next");
        }
    }
}