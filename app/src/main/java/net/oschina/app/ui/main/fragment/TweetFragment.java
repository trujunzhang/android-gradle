package net.oschina.app.ui.main.fragment;

import android.support.v4.app.Fragment;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.adapter.ListViewTweetAdapter;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetList;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.main.fragment.base.ListViewUtils;
import net.oschina.app.widget.PullToRefreshListView;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.frame_tweet_menu)
public class TweetFragment extends Fragment {

    @App
    public AppContext appContext;

    public int lvTweetSumData;

    public List<Tweet> lvTweetData = new ArrayList<Tweet>();

    public View lvTweet_footer;

    public TextView lvTweet_foot_more;

    public ProgressBar lvTweet_foot_progress;

    @ViewById(R.id.frame_btn_tweet_lastest)
    public Button framebtn_Tweet_lastest;
    @ViewById(R.id.frame_btn_tweet_hot)
    public Button framebtn_Tweet_hot;
    @ViewById(R.id.frame_btn_tweet_my)
    public Button framebtn_Tweet_my;

    public int curTweetCatalog = TweetList.CATALOG_LASTEST;


    public PullToRefreshListView lvTweet;



    public ListViewTweetAdapter lvTweetAdapter;


    protected void init() {

        // 设置首选择项
        framebtn_Tweet_lastest.setEnabled(false);

        // 动弹
        framebtn_Tweet_lastest.setOnClickListener(frameTweetBtnClick(
                framebtn_Tweet_lastest, TweetList.CATALOG_LASTEST));
        framebtn_Tweet_hot.setOnClickListener(frameTweetBtnClick(
                framebtn_Tweet_hot, TweetList.CATALOG_HOT));
        framebtn_Tweet_my.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // 判断登录
                int uid = appContext.getLoginUid();
                if (uid == 0) {
//                    UIHelper.showLoginDialog(Main.this);
                    return;
                }

                framebtn_Tweet_lastest.setEnabled(true);
                framebtn_Tweet_hot.setEnabled(true);
                framebtn_Tweet_my.setEnabled(false);

                curTweetCatalog = uid;
                loadLvTweetData(curTweetCatalog, 0, 
                        UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
            }
        });

    }


    private View.OnClickListener frameTweetBtnClick(final Button btn,
                                                    final int catalog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (btn == framebtn_Tweet_lastest)
                    framebtn_Tweet_lastest.setEnabled(false);
                else
                    framebtn_Tweet_lastest.setEnabled(true);
                if (btn == framebtn_Tweet_hot)
                    framebtn_Tweet_hot.setEnabled(false);
                else
                    framebtn_Tweet_hot.setEnabled(true);
                if (btn == framebtn_Tweet_my)
                    framebtn_Tweet_my.setEnabled(false);
                else
                    framebtn_Tweet_my.setEnabled(true);

                curTweetCatalog = catalog;
                loadLvTweetData(curTweetCatalog, 0, 
                        UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
            }
        };
    }


    public void loadLvTweetData(final int catalog, final int pageIndex, final int action) {
        boolean isRefresh = false;
        if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
            isRefresh = true;
        }

        String url = ApiClient.getTweetListUrl(catalog, pageIndex, ApiClient.PAGE_SIZE);

        new AQuery(getActivity().getApplicationContext()).ajax(url, InputStream.class, new AjaxCallback<InputStream>() {
            @Override
            public void callback(String url, InputStream inputStream, AjaxStatus status) {
                try {
                    TweetList list = TweetList.parse(inputStream);
                    int listCount = list.getPageSize();

                    if (listCount >= 0) {
                        Notice notice = getSucessNotice(listCount, list, UIHelper.LISTVIEW_DATATYPE_POST, action);
                        ListViewUtils.setSucessInfoForListView(listCount, ApiClient.PAGE_SIZE, lvTweet, lvTweetAdapter, lvTweet_foot_more, notice);
                    } else if (listCount == -1) {
                        ListViewUtils.setFailureInfoForListView(lvTweet, lvTweet_foot_more);
                    }

                    ListViewUtils.endRestEffect(lvTweetAdapter, lvTweet, lvTweet_foot_more, lvTweet_foot_progress);
                    ListViewUtils.setListViewStatusByRest(getActivity().getApplicationContext(), action, lvTweet);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Notice getSucessNotice(int what, TweetList plist, int objtype, int actiontype) {
        Notice notice = null;
        switch (actiontype) {
            case UIHelper.LISTVIEW_DATATYPE_POST:
                notice = plist.getNotice();
                lvTweetSumData = what;
                if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                    if (lvTweetData.size() > 0) {
                        for (Tweet post1 : plist.getTweetlist()) {
                            boolean b = false;
                            for (Tweet post2 : lvTweetData) {
                                if (post1.getId() == post2.getId()) {
                                    b = true;
                                    break;
                                }
                            }
//                            if (!b)
//                                newdata++;
                        }
                    } else {
//                        newdata = what;
                    }
                }

                break;
        }

        lvTweetData.clear();// 先清除原有数据
        lvTweetData.addAll(plist.getTweetlist());

        return notice;
    }

    /**
     * 初始化动弹列表
     */
    public void initTweetListView() {
        lvTweetAdapter = new ListViewTweetAdapter(getActivity().getApplicationContext(), lvTweetData, R.layout.tweet_listitem);
        lvTweet_footer = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listview_footer, null);
        lvTweet_foot_more = (TextView) lvTweet_footer.findViewById(R.id.listview_foot_more);
        lvTweet_foot_progress = (ProgressBar) lvTweet_footer.findViewById(R.id.listview_foot_progress);
        lvTweet.addFooterView(lvTweet_footer);// 添加底部视图 必须在setAdapter前
        lvTweet.setAdapter(lvTweetAdapter);
        lvTweet.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvTweet_footer)
                    return;

                Tweet tweet = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    tweet = (Tweet) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.tweet_listitem_username);
                    tweet = (Tweet) tv.getTag();
                }
                if (tweet == null)
                    return;
                // 跳转到动弹详情&评论页面
                UIHelper.showTweetDetail(view.getContext(), tweet.getId());
            }
        });
        lvTweet.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvTweet.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvTweetData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvTweet_footer) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvTweet.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvTweet.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvTweet_foot_more.setText(R.string.load_ing);
                    lvTweet_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvTweetSumData / AppContext.PAGE_SIZE;
                    loadLvTweetData(curTweetCatalog, pageIndex,  UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvTweet.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvTweet.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view,
                                           int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvTweet_footer)
                    return false;

                Tweet _tweet = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    _tweet = (Tweet) view.getTag();
                } else {
                    TextView tv = (TextView) view.findViewById(R.id.tweet_listitem_username);
                    _tweet = (Tweet) tv.getTag();
                }
                if (_tweet == null)
                    return false;

                final Tweet tweet = _tweet;

                // 删除操作
                // if(appContext.getLoginUid() == tweet.getAuthorId()) {
                final Handler handler = new Handler() {
                    public void handleMessage(Message msg) {
                        if (msg.what == 1) {
                            Result res = (Result) msg.obj;
                            if (res.OK()) {
                                lvTweetData.remove(tweet);
                                lvTweetAdapter.notifyDataSetChanged();
                            }
//                            UIHelper.ToastMessage(Main.this,res.getErrorMessage());
                        } else {
//                            ((AppException) msg.obj).makeToast(Main.this);
                        }
                    }
                };
                Thread thread = new Thread() {
                    public void run() {
                        Message msg = new Message();
                        try {
                            Result res = appContext.delTweet(
                                    appContext.getLoginUid(), tweet.getId());
                            msg.what = 1;
                            msg.obj = res;
                        } catch (AppException e) {
                            e.printStackTrace();
                            msg.what = -1;
                            msg.obj = e;
                        }
                        handler.sendMessage(msg);
                    }
                };
//                UIHelper.showTweetOptionDialog(Main.this, thread);
                // } else {
                // UIHelper.showTweetOptionDialog(Main.this, null);
                // }
                return true;
            }
        });
        lvTweet.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                loadLvTweetData(curTweetCatalog, 0, 
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }


}
