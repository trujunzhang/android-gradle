package net.oschina.app.ui.main;

import android.support.v4.app.FragmentActivity;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.FragmentById;

/**
 * 应用程序首页
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */

@EActivity(R.layout.main)
public class Main extends FragmentActivity {

    @App
    public AppContext appContext;

    @FragmentById(R.id.fragment_header_tab)
    MainHeaderFragment fragmentHeaderTab;
    @FragmentById(R.id.fragment_footer_tab)
    MainFooterFragment fragmentFooterTab;


    @AfterViews
    protected void calledAfterViewInjection() {
        fragmentFooterTab.initNewsPanel();
    }

    public void setHeaderTitle(int index) {
        this.fragmentHeaderTab.setHeaderTitle(index);
    }
}
