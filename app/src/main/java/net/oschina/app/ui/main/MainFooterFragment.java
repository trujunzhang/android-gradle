package net.oschina.app.ui.main;

/**
 * Created by djzhang on 5/14/14.
 */

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import greendroid.widget.MyQuickAction;
import greendroid.widget.QuickActionGrid;
import greendroid.widget.QuickActionWidget;
import net.oschina.app.R;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.main.fragment.AbstractMenuFragment;
import net.oschina.app.ui.main.fragment.NewsMenuFragment_;
import net.oschina.app.ui.main.fragment.QuestionMenuFragment_;
import net.oschina.app.ui.main.fragment.TweetMenuFragment_;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

@EFragment(R.layout.main_footer)
public class MainFooterFragment extends Fragment {
    private int mCurSel;
    private RadioButton lastRadioButton;

    public static final int QUICKACTION_LOGIN_OR_LOGOUT = 0;
    public static final int QUICKACTION_USERINFO = 1;
    public static final int QUICKACTION_SOFTWARE = 2;
    public static final int QUICKACTION_SEARCH = 3;
    public static final int QUICKACTION_SETTING = 4;
    public static final int QUICKACTION_EXIT = 5;

    private QuickActionWidget mGrid;// 快捷栏控件


    @ViewById(R.id.main_footbar_news)
    RadioButton fbNews;
    @ViewById(R.id.main_footbar_question)
    RadioButton fbQuestion;
    @ViewById(R.id.main_footbar_tweet)
    RadioButton fbTweet;
    @ViewById(R.id.main_footbar_active)
    RadioButton fbactive;
    @ViewById(R.id.main_footbar_setting)
    ImageView fbSetting;

    private AbstractMenuFragment currentFragment;

    @AfterViews
    protected void calledAfterViewInjection() {
        this.initQuickActionGrid();
        this.mCurSel = -1;
    }

    public void initNewsPanel() {
        this.setButtonClickEvent(fbNews, 0);
    }

    @Click(R.id.main_footbar_news)
    void main_footbar_newsOnClick(View view) {
        setButtonClickEvent(view, 0);
    }

    @Click(R.id.main_footbar_question)
    void main_footbar_questionOnClick(View view) {
        setButtonClickEvent(view, 1);
    }

    @Click(R.id.main_footbar_tweet)
    void main_footbar_tweetOnClick(View view) {
        setButtonClickEvent(view, 2);
    }

    @Click(R.id.main_footbar_active)
    void main_footbar_activeOnClick(View view) {
        setButtonClickEvent(view, 3);
    }

    void setButtonClickEvent(View view, int pos) {
        setTabFragment(pos);
        setCurPoint(view, pos);
    }

    private void setTabFragment(int pos) {
        // Fragments have access to their parent Activity's FragmentManager. You can
        // obtain the FragmentManager like this.
        FragmentManager fm = getFragmentManager();

        if (fm != null) {
            if (pos != this.mCurSel) {

                setCurrentFragment(pos, fm);
                initCurrentFragment(pos, fm);
                this.mCurSel = pos;
            }
        }
    }

    private void initCurrentFragment(int pos, FragmentManager fm) {
        this.currentFragment.initContentFragment(0);
    }

    private void setCurrentFragment(int pos, FragmentManager fm) {
        // Perform the FragmentTransaction to load in the list tab content.
        // Using FragmentTransaction#replace will destroy any Fragments
        // currently inside R.id.fragment_content and add the new Fragment
        // in its place.
        FragmentTransaction ft = fm.beginTransaction();
        currentFragment = getMenuFragmentByType(pos);
        ft.replace(R.id.fragment_sub_menu, currentFragment);
        ft.commit();
    }


    private AbstractMenuFragment getMenuFragmentByType(int pos) {
        AbstractMenuFragment fragment = null;
        switch (pos) {
            case 0:// 资讯
                fragment = new NewsMenuFragment_();
                break;
            case 1:// 问答
                fragment = new QuestionMenuFragment_();
                break;
            case 2:// 动弹
                fragment = new TweetMenuFragment_();
                break;
            case 3:// 动态
                break;
        }

        return fragment;
    }


    /**
     * 设置底部栏当前焦点
     *
     * @param view
     * @param index
     */
    private void setCurPoint(View view, int index) {
        RadioButton newButton = (RadioButton) view;

        if (lastRadioButton != null) {
            lastRadioButton.setChecked(false);
        }
        newButton.setChecked(true);
        Main activity = (Main) getActivity();
        activity.setHeaderTitle(index);
        mCurSel = index;

        lastRadioButton = newButton;
    }


    /**
     * 初始化底部栏
     */
    @Click(R.id.main_footbar_setting)
    void main_footbar_settingOnClick(View view) {
        // 展示快捷栏&判断是否登录&是否加载文章图片
        UIHelper.showSettingLoginOrLogout(getActivity(),
                mGrid.getQuickAction(0));
        mGrid.show(view);
    }


    /**
     * 初始化快捷栏
     */
    private void initQuickActionGrid() {
        mGrid = new QuickActionGrid(getActivity());
        mGrid.addQuickAction(new MyQuickAction(getActivity(), R.drawable.ic_menu_login,
                R.string.main_menu_login));
        mGrid.addQuickAction(new MyQuickAction(getActivity(), R.drawable.ic_menu_myinfo,
                R.string.main_menu_myinfo));
        mGrid.addQuickAction(new MyQuickAction(getActivity(),
                R.drawable.ic_menu_software, R.string.main_menu_software));
        mGrid.addQuickAction(new MyQuickAction(getActivity(), R.drawable.ic_menu_search,
                R.string.main_menu_search));
        mGrid.addQuickAction(new MyQuickAction(getActivity(),
                R.drawable.ic_menu_setting, R.string.main_menu_setting));
        mGrid.addQuickAction(new MyQuickAction(getActivity(), R.drawable.ic_menu_exit,
                R.string.main_menu_exit));

        mGrid.setOnQuickActionClickListener(mActionListener);
    }


    /**
     * 快捷栏item点击事件
     */
    private QuickActionWidget.OnQuickActionClickListener mActionListener = new QuickActionWidget.OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
            switch (position) {
                case QUICKACTION_LOGIN_OR_LOGOUT:// 用户登录-注销
                    UIHelper.loginOrLogout(getActivity());
                    break;
                case QUICKACTION_USERINFO:// 我的资料
                    UIHelper.showUserInfo(getActivity());
                    break;
                case QUICKACTION_SOFTWARE:// 开源软件
                    UIHelper.showSoftware(getActivity());
                    break;
                case QUICKACTION_SEARCH:// 搜索
                    UIHelper.showSearch(getActivity());
                    break;
                case QUICKACTION_SETTING:// 设置
                    UIHelper.showSetting(getActivity());
                    break;
                case QUICKACTION_EXIT:// 退出
                    UIHelper.Exit(getActivity());
                    break;
            }
        }
    };


}
