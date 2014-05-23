package net.oschina.app.ui.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RadioButton;
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
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;

/**
 * 应用程序首页
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */

@EActivity(R.layout.main)
public class MainScroll extends FragmentActivity {


    @App
    public AppContext appContext;


    private FragmentManager fragmentManager;

    NewsFragment newsFragment = new NewsFragment_();
    ActiveFragment activeFragment;
    QuestionFragment questionFragment;
    TweetFragment tweetFragment;

    public static final int QUICKACTION_LOGIN_OR_LOGOUT = 0;
    public static final int QUICKACTION_USERINFO = 1;
    public static final int QUICKACTION_SOFTWARE = 2;
    public static final int QUICKACTION_SEARCH = 3;
    public static final int QUICKACTION_SETTING = 4;
    public static final int QUICKACTION_EXIT = 5;

    private RadioButton[] mButtons;
    private String[] mHeadTitles;
    private int mViewCount;
    private int mCurSel;


    private int curNewsCatalog = NewsList.CATALOG_ALL;
    private int curQuestionCatalog = PostList.CATALOG_ASK;
    private int curTweetCatalog = TweetList.CATALOG_LASTEST;
    private int curActiveCatalog = ActiveList.CATALOG_LASTEST;


    public static BadgeView bv_active;
    public static BadgeView bv_message;
    public static BadgeView bv_atme;
    public static BadgeView bv_review;

    private QuickActionWidget mGrid;// 快捷栏控件

    private boolean isClearNotice = false;
    private int curClearNoticeType = 0;

    @AfterViews
    protected void calledAfterViewInjection() {
        fragmentManager = getSupportFragmentManager();

        // 网络连接判断
        if (!appContext.isNetworkConnected())
            UIHelper.ToastMessage(this, R.string.network_not_connected);
        // 初始化登录
        appContext.initLoginInfo();

        this.initHeadView();
        this.initPageScroll();
        this.initFrameButton();
        this.initBadgeView();
        this.initQuickActionGrid();

        setNewsFragment();
    }

    private void toggleFragment(Fragment fragment) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.content, fragment);
        transaction.commit();
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (mViewCount == 0)
            mViewCount = 4;
//        if (mCurSel == 0 && !fbNews.isChecked()) {
//            fbNews.setChecked(true);
//            fbQuestion.setChecked(false);
//            fbTweet.setChecked(false);
//            fbactive.setChecked(false);
//        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    /**
     * 初始化快捷栏
     */
    private void initQuickActionGrid() {
        mGrid = new QuickActionGrid(this);
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_login,
                R.string.main_menu_login));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_myinfo,
                R.string.main_menu_myinfo));
        mGrid.addQuickAction(new MyQuickAction(this,
                R.drawable.ic_menu_software, R.string.main_menu_software));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_search,
                R.string.main_menu_search));
        mGrid.addQuickAction(new MyQuickAction(this,
                R.drawable.ic_menu_setting, R.string.main_menu_setting));
        mGrid.addQuickAction(new MyQuickAction(this, R.drawable.ic_menu_exit,
                R.string.main_menu_exit));

        mGrid.setOnQuickActionClickListener(mActionListener);
    }

    /**
     * 快捷栏item点击事件
     */
    private OnQuickActionClickListener mActionListener = new OnQuickActionClickListener() {
        public void onQuickActionClicked(QuickActionWidget widget, int position) {
            switch (position) {
                case QUICKACTION_LOGIN_OR_LOGOUT:// 用户登录-注销
                    UIHelper.loginOrLogout(MainScroll.this);
                    break;
                case QUICKACTION_USERINFO:// 我的资料
                    UIHelper.showUserInfo(MainScroll.this);
                    break;
                case QUICKACTION_SOFTWARE:// 开源软件
                    UIHelper.showSoftware(MainScroll.this);
                    break;
                case QUICKACTION_SEARCH:// 搜索
                    UIHelper.showSearch(MainScroll.this);
                    break;
                case QUICKACTION_SETTING:// 设置
                    UIHelper.showSetting(MainScroll.this);
                    break;
                case QUICKACTION_EXIT:// 退出
                    UIHelper.Exit(MainScroll.this);
                    break;
            }
        }
    };


    /**
     * 初始化头部视图
     */
    private void initHeadView() {
//        mHead_search.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                UIHelper.showSearch(v.getContext());
//            }
//        });
//        mHeadPub_post.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                UIHelper.showQuestionPub(v.getContext());
//            }
//        });
//        mHeadPub_tweet.setOnClickListener(new View.OnClickListener() {
//            public void onClick(View v) {
//                UIHelper.showTweetPub(Main.this);
//            }
//        });
    }

    /**
     * 初始化底部栏
     */
    @Click(R.id.main_footbar_setting)
    void main_footbar_settingOnClick(View view) {
        // 展示快捷栏&判断是否登录&是否加载文章图片
        UIHelper.showSettingLoginOrLogout(MainScroll.this,
                mGrid.getQuickAction(0));
        mGrid.show(view);
    }

    /**
     * 初始化通知信息标签控件
     */
    private void initBadgeView() {
//        bv_active = new BadgeView(this, fbactive);
//        bv_active.setBackgroundResource(R.drawable.widget_count_bg);
//        bv_active.setIncludeFontPadding(false);
//        bv_active.setGravity(Gravity.CENTER);
//        bv_active.setTextSize(8f);
//        bv_active.setTextColor(Color.WHITE);

//        bv_atme = new BadgeView(this, framebtn_Active_atme);
//        bv_atme.setBackgroundResource(R.drawable.widget_count_bg);
//        bv_atme.setIncludeFontPadding(false);
//        bv_atme.setGravity(Gravity.CENTER);
//        bv_atme.setTextSize(8f);
//        bv_atme.setTextColor(Color.WHITE);
//
//        bv_review = new BadgeView(this, framebtn_Active_comment);
//        bv_review.setBackgroundResource(R.drawable.widget_count_bg);
//        bv_review.setIncludeFontPadding(false);
//        bv_review.setGravity(Gravity.CENTER);
//        bv_review.setTextSize(8f);
//        bv_review.setTextColor(Color.WHITE);
//
//        bv_message = new BadgeView(this, framebtn_Active_message);
//        bv_message.setBackgroundResource(R.drawable.widget_count_bg);
//        bv_message.setIncludeFontPadding(false);
//        bv_message.setGravity(Gravity.CENTER);
//        bv_message.setTextSize(8f);
//        bv_message.setTextColor(Color.WHITE);
    }

    @Click({R.id.main_footbar_news, R.id.main_footbar_question, R.id.main_footbar_tweet, R.id.main_footbar_active})
    void main_footbar_newsOnClick(View view) {
        int pos = (Integer) (view.getTag());
        // 点击当前项刷新
        if (mCurSel != pos) {
            switch (pos) {
                case 0:// 资讯
                    setNewsFragment();
//                    if (lvListView.getVisibility() == View.VISIBLE) {
//                        if (lvData.isEmpty()) {
//                            loadLvNewsData(curNewsCatalog, 0,
//                                    lvNewsHandler,
//                                    UIHelper.LISTVIEW_ACTION_INIT);
//                        }
//                    }
                    break;
                case 1:// 问答
//                    if (lvQuestionData.isEmpty()) {
//                        loadLvQuestionData(curQuestionCatalog, 0,
//                                lvQuestionHandler,
//                                UIHelper.LISTVIEW_ACTION_INIT);
//                    }
                    break;
                case 2:// 动弹
//                    if (lvTweetData.isEmpty()) {
//                        loadLvTweetData(curTweetCatalog, 0,
//                                lvTweetHandler,
//                                UIHelper.LISTVIEW_ACTION_INIT);
//                    }
                    break;
                case 3:// 动态
                    // 判断登录
//                    if (!appContext.isLogin()) {
//                        if (lvActive.getVisibility() == View.VISIBLE
//                                && lvActiveData.isEmpty()) {
//                            lvActive_foot_more
//                                    .setText(R.string.load_empty);
//                            lvActive_foot_progress
//                                    .setVisibility(View.GONE);
//                        } else if (lvMsg.getVisibility() == View.VISIBLE
//                                && lvMsgData.isEmpty()) {
//                            lvMsg_foot_more
//                                    .setText(R.string.load_empty);
//                            lvMsg_foot_progress
//                                    .setVisibility(View.GONE);
//                        }
//                        UIHelper.showLoginDialog(Main.this);
//                        break;
//                    }
            }
        }
        setCurPoint(pos);
    }

    private void setNewsFragment() {
//        toggleFragment(newsFragment);
//        newsFragment.checkEmptyAndInit();
    }


    /**
     * 初始化水平滚动翻页
     */
    private void initPageScroll() {
        mHeadTitles = getResources().getStringArray(R.array.head_titles);
        mViewCount = 4;
        mButtons = new RadioButton[mViewCount];

//        mButtons[0] = fbNews;
//        mButtons[1] = fbQuestion;
//        mButtons[2] = fbTweet;
//        mButtons[3] = fbactive;

        for (int i = 0; i < mViewCount; i++) {
            mButtons[i].setTag(i);
            mButtons[i].setChecked(false);
            mButtons[i].setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    int pos = (Integer) (v.getTag());
                    // 点击当前项刷新
                    if (mCurSel == pos) {
                        switch (pos) {
//                            case 0:// 资讯+博客
//                                if (lvListView.getVisibility() == View.VISIBLE)
//                                    lvListView.clickRefresh();
//                                else
//                                    lvBlog.clickRefresh();
//                                break;
//                            case 1:// 问答
//                                lvQuestion.clickRefresh();
//                                break;
//                            case 2:// 动弹
//                                lvTweet.clickRefresh();
//                                break;
//                            case 3:// 动态+留言
//                                if (lvActive.getVisibility() == View.VISIBLE)
//                                    lvActive.clickRefresh();
//                                else
//                                    lvMsg.clickRefresh();
//                                break;
                        }
                    }
                }
            });
        }

        // 设置第一显示屏
        mCurSel = 0;
        mButtons[mCurSel].setChecked(true);

//        mScrollLayout
//                .SetOnViewChangeListener(new ScrollLayout.OnViewChangeListener() {
//                    public void OnViewChange(int viewIndex) {
//                        //切换列表视图-如果列表数据为空：加载数据
//                        switch (viewIndex) {
//                            case 0:// 资讯
//                                if (lvListView.getVisibility() == View.VISIBLE) {
//                                    if (lvData.isEmpty()) {
//                                        loadLvNewsData(curNewsCatalog, 0,
//                                                lvNewsHandler,
//                                                UIHelper.LISTVIEW_ACTION_INIT);
//                                    }
//                                } else {
//                                    if (lvBlogData.isEmpty()) {
//                                        loadLvBlogData(curNewsCatalog, 0,
//                                                lvBlogHandler,
//                                                UIHelper.LISTVIEW_ACTION_INIT);
//                                    }
//                                }
//                                break;
//                            case 1:// 问答
//                                if (lvQuestionData.isEmpty()) {
//                                    loadLvQuestionData(curQuestionCatalog, 0,
//                                            lvQuestionHandler,
//                                            UIHelper.LISTVIEW_ACTION_INIT);
//                                }
//                                break;
//                            case 2:// 动弹
//                                if (lvTweetData.isEmpty()) {
//                                    loadLvTweetData(curTweetCatalog, 0,
//                                            lvTweetHandler,
//                                            UIHelper.LISTVIEW_ACTION_INIT);
//                                }
//                                break;
//                            case 3:// 动态
//                                // 判断登录
//                                if (!appContext.isLogin()) {
//                                    if (lvActive.getVisibility() == View.VISIBLE
//                                            && lvActiveData.isEmpty()) {
//                                        lvActive_foot_more
//                                                .setText(R.string.load_empty);
//                                        lvActive_foot_progress
//                                                .setVisibility(View.GONE);
//                                    } else if (lvMsg.getVisibility() == View.VISIBLE
//                                            && lvMsgData.isEmpty()) {
//                                        lvMsg_foot_more
//                                                .setText(R.string.load_empty);
//                                        lvMsg_foot_progress
//                                                .setVisibility(View.GONE);
//                                    }
//                                    UIHelper.showLoginDialog(Main.this);
//                                    break;
//                                }
//                                // 处理通知信息
//                                if (bv_atme.isShown())
//                                    frameActiveBtnOnClick(framebtn_Active_atme,
//                                            ActiveList.CATALOG_ATME,
//                                            UIHelper.LISTVIEW_ACTION_REFRESH);
//                                else if (bv_review.isShown())
//                                    frameActiveBtnOnClick(framebtn_Active_comment,
//                                            ActiveList.CATALOG_COMMENT,
//                                            UIHelper.LISTVIEW_ACTION_REFRESH);
//                                else if (bv_message.isShown())
//                                    frameActiveBtnOnClick(framebtn_Active_message,
//                                            0, UIHelper.LISTVIEW_ACTION_REFRESH);
//                                else if (lvActive.getVisibility() == View.VISIBLE
//                                        && lvActiveData.isEmpty())
//                                    loadLvActiveData(curActiveCatalog, 0,
//                                            lvActiveHandler,
//                                            UIHelper.LISTVIEW_ACTION_INIT);
//                                else if (lvMsg.getVisibility() == View.VISIBLE
//                                        && lvMsgData.isEmpty())
//                                    loadLvMsgData(0, lvMsgHandler,
//                                            UIHelper.LISTVIEW_ACTION_INIT);
//                                break;
//                        }
//                        setCurPoint(viewIndex);
//                    }
//                });
    }

    /**
     * 设置底部栏当前焦点
     *
     * @param index
     */
    private void setCurPoint(int index) {
        if (index < 0 || index > mViewCount - 1 || mCurSel == index)
            return;

        mButtons[mCurSel].setChecked(false);
        mButtons[index].setChecked(true);
//        mHeadTitle.setText(mHeadTitles[index]);
        mCurSel = index;

//        mHead_search.setVisibility(View.GONE);
//        mHeadPub_post.setVisibility(View.GONE);
//        mHeadPub_tweet.setVisibility(View.GONE);
//        // 头部logo、发帖、发动弹按钮显示
//        if (index == 0) {
//            mHeadLogo.setImageResource(R.drawable.frame_logo_news);
//            mHead_search.setVisibility(View.VISIBLE);
//        } else if (index == 1) {
//            mHeadLogo.setImageResource(R.drawable.frame_logo_post);
//            mHeadPub_post.setVisibility(View.VISIBLE);
//        } else if (index == 2) {
//            mHeadLogo.setImageResource(R.drawable.frame_logo_tweet);
//            mHeadPub_tweet.setVisibility(View.VISIBLE);
//        } else if (index == 3) {
//            mHeadLogo.setImageResource(R.drawable.frame_logo_active);
//            mHeadPub_tweet.setVisibility(View.VISIBLE);
//        }
    }

    /**
     * 初始化各个主页的按钮(资讯、问答、动弹、动态、留言)
     */
    private void initFrameButton() {
        // 特殊处理
//        framebtn_Active_atme.setText("@" + getString(R.string.frame_title_active_atme));
    }

    /**
     * 创建menu TODO 停用原生菜单
     */
    public boolean onCreateOptionsMenu(Menu menu) {
        return false;
        // MenuInflater inflater = getMenuInflater();
        // inflater.inflate(R.menu.main_menu, menu);
        // return true;
    }

    /**
     * 菜单被显示之前的事件
     */
    public boolean onPrepareOptionsMenu(Menu menu) {
        UIHelper.showMenuLoginOrLogout(this, menu);
        return true;
    }

    /**
     * 处理menu的事件
     */
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        switch (item_id) {
            case R.id.main_menu_user:
                UIHelper.loginOrLogout(this);
                break;
            case R.id.main_menu_about:
                UIHelper.showAbout(this);
                break;
            case R.id.main_menu_setting:
                UIHelper.showSetting(this);
                break;
            case R.id.main_menu_exit:
                UIHelper.Exit(this);
                break;
        }
        return true;
    }

    /**
     * 监听返回--是否退出程序
     */
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        boolean flag = true;
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // 是否退出应用
            UIHelper.Exit(this);
        } else if (keyCode == KeyEvent.KEYCODE_MENU) {
            // 展示快捷栏&判断是否登录
            UIHelper.showSettingLoginOrLogout(MainScroll.this,
                    mGrid.getQuickAction(0));
//            mGrid.show(fbSetting, true);
        } else if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            // 展示搜索页
            UIHelper.showSearch(MainScroll.this);
        } else {
            flag = super.onKeyDown(keyCode, event);
        }
        return flag;
    }
}
