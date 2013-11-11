package AU.MightyFour.Sitcomizer;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity
{
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LayoutInflater inflater = LayoutInflater.from (this);
        List<View> pages = new ArrayList<View>();

        View page = inflater.inflate (R.layout.buttons_view, null);
        pages.add (page);

        page = inflater.inflate (R.layout.buttons_view, null);
        Button laugh_button = (Button) page.findViewById(R.id.central_button);
        laugh_button.setText("Laugh!");
        pages.add (page);

        page = inflater.inflate(R.layout.oneliners_view, null);
        pages.add (page);

        CustomPagerAdapter pagerAdapter = new CustomPagerAdapter(pages);
        ViewPager viewPager = new ViewPager(this);
        viewPager.setAdapter(pagerAdapter);

        viewPager.setCurrentItem(1);

        setContentView(viewPager);
    }
}
