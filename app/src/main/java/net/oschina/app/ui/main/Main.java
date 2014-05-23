package net.oschina.app.ui.main;

import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.*;
import android.widget.*;
import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import greendroid.widget.QuickActionWidget.OnQuickActionClickListener;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.ActiveList;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.PostList;
import net.oschina.app.bean.TweetList;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.main.fragment.*;
import net.oschina.app.widget.BadgeView;
import org.androidannotations.annotations.*;

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

    }

    public void setHeaderTitle(int index) {
        this.fragmentHeaderTab.setHeaderTitle(index);
    }
}
