package nl.inversion.domoticz.Welcome;

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

        buildLayout();
    }

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

        return fList;
    }

    private void pageSelected(int position) {

        switch (position) {

            case 0:
                buttonPrev.setVisibility(View.INVISIBLE);
                break;

            case 1:
                buttonPrev.setVisibility(View.VISIBLE);
                buttonNext.setText("next");
                break;

            case 2:
                buttonNext.setText("finish");
                break;
        }

    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.btn_next:
                if (mPager.getCurrentItem() != fList.size()) {
                    mPager.setCurrentItem(mPager.getCurrentItem() + 1);
                    super.onBackPressed();
                }
                break;

            case R.id.btn_prev:
                if (mPager.getCurrentItem() != 0) mPager.setCurrentItem(mPager.getCurrentItem() - 1);
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        pageSelected(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}