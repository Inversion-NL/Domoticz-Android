package nl.inversion.domoticz.Welcome;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.viewpagerindicator.CirclePageIndicator;

import java.util.ArrayList;
import java.util.List;

import nl.inversion.domoticz.R;

public class PageViewActivity extends FragmentActivity implements View.OnClickListener {

    private MyPageAdapter mAdapter;
    private ViewPager mPager;
    private final List<Fragment> fList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        List<Fragment> fragments = getFragments();

        mAdapter = new MyPageAdapter(getSupportFragmentManager(), fragments);

        mPager = (ViewPager)findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);
        mPager.setCurrentItem(0);

        CirclePageIndicator mIndicator = (CirclePageIndicator) findViewById(R.id.indicator);
        mIndicator.setViewPager(mPager);
        float radius = mIndicator.getRadius();
        mIndicator.setRadius(radius + 4);
        mIndicator.setFillColor(getResources().getColor(android.R.color.darker_gray));

        Button prev = (Button) findViewById(R.id.btn_prev);
        prev.setOnClickListener(this);

        Button next = (Button) findViewById(R.id.btn_next);
        next.setOnClickListener(this);

        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {

                switch (position) {
                    case 0:
                        // Toast.makeText(getApplicationContext(), "Page 1", Toast.LENGTH_SHORT).show();
                        break;

                    case 1:
                        // Toast.makeText(getApplicationContext(), "Page 2", Toast.LENGTH_SHORT).show();
                        break;

                    case 2:
                        // Toast.makeText(getApplicationContext(), "Page 3", Toast.LENGTH_SHORT).show();
                        break;
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private List<Fragment> getFragments(){

        fList.add(MyFragment.newInstance("Fragment 1"));
        fList.add(MyFragment.newInstance("Fragment 2"));
        fList.add(MyFragment.newInstance("Fragment 3"));

        return fList;
    }

    @Override
    public void onClick(View view) {

        switch(view.getId()) {
            case R.id.btn_next:
                if (mPager.getCurrentItem() != fList.size()) mPager.setCurrentItem(mPager.getCurrentItem()+1);
                break;

            case R.id.btn_prev:
                if (mPager.getCurrentItem() != 0) mPager.setCurrentItem(mPager.getCurrentItem()-1);
                break;
        }
    }
}