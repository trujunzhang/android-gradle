package net.oschina.app.ui.main.fragment;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.*;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.main.fragment.base.ListViewUtils;
import net.oschina.app.widget.PullToRefreshListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.frame_pulltorefresh_lisview)
public class NewsFragment extends Fragment {

//    @App
//    public AppContext appContext;

    public int lvNewsSumData;

    public List<News> lvNewsData = new ArrayList<News>();

    public View lvNews_footer;

    public TextView lvNews_foot_more;

    public ProgressBar lvNews_foot_progress;

    @ViewById(R.id.frame_btn_news_lastest)
    public Button framebtn_News_lastest;

    @ViewById(R.id.frame_btn_news_recommend)
    public Button framebtn_News_recommend;

    public int curNewsCatalog = NewsList.CATALOG_ALL;

    @ViewById(R.id.frame_listview_with_more)
    public PullToRefreshListView lvNews;

    public ListViewNewsAdapter lvNewsAdapter;

    @AfterViews
    protected void calledAfterViewInjection() {
        // 设置首选择项
        framebtn_News_lastest.setEnabled(false);

        // 资讯+博客
        framebtn_News_lastest.setOnClickListener(frameNewsBtnClick(
                framebtn_News_lastest, NewsList.CATALOG_ALL));
        framebtn_News_recommend.setOnClickListener(frameNewsBtnClick(
                framebtn_News_recommend, BlogList.CATALOG_RECOMMEND));

        this.initNewsListView();
    }


    public View.OnClickListener frameNewsBtnClick(final Button btn,
                                                  final int catalog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (btn == framebtn_News_lastest) {
                    framebtn_News_lastest.setEnabled(false);
                } else {
                    framebtn_News_lastest.setEnabled(true);
                }
                if (btn == framebtn_News_recommend) {
                    framebtn_News_recommend.setEnabled(false);
                } else {
                    framebtn_News_recommend.setEnabled(true);
                }

                curNewsCatalog = catalog;

                // 非新闻列表
                if (btn == framebtn_News_lastest) {
                    lvNews.setVisibility(View.VISIBLE);
                    loadLvNewsData(curNewsCatalog, 0,
                            UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
                }
            }
        };
    }


    public void loadLvNewsData(final int catalog, final int pageIndex, final int action) {
        boolean isRefresh = false;
        if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
            isRefresh = true;
        }

        String url = ApiClient.getNewsListUrl(catalog, pageIndex, ApiClient.PAGE_SIZE);

        new AQuery(getActivity().getApplicationContext()).ajax(url, InputStream.class, new AjaxCallback<InputStream>() {
            @Override
            public void callback(String url, InputStream inputStream, AjaxStatus status) {
                try {
                    NewsList list = NewsList.parse(inputStream);
                    int listCount = list.getPageSize();

                    if (listCount >= 0) {
                        Notice notice = getSucessNotice(listCount, list, UIHelper.LISTVIEW_DATATYPE_NEWS, action);
                        ListViewUtils.setSucessInfoForListView(listCount, ApiClient.PAGE_SIZE, lvNews, lvNewsAdapter, lvNews_foot_more, notice);
                    } else if (listCount == -1) {
                        ListViewUtils.setFailureInfoForListView(lvNews, lvNews_foot_more);
                    }

                    ListViewUtils.endRestEffect(lvNewsAdapter, lvNews, lvNews_foot_more, lvNews_foot_progress);
                    ListViewUtils.setListViewStatusByRest(getActivity().getApplicationContext(), action, lvNews);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Notice getSucessNotice(int what, NewsList nlist, int objtype, int actiontype) {
        Notice notice = null;
        switch (actiontype) {
            case UIHelper.LISTVIEW_ACTION_INIT:
            case UIHelper.LISTVIEW_ACTION_REFRESH:
            case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
                int newdata = 0;// 新加载数据-只有刷新动作才会使用到
                switch (objtype) {
                    case UIHelper.LISTVIEW_DATATYPE_NEWS:
                        notice = nlist.getNotice();
                        lvNewsSumData = what;
                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                            if (lvNewsData.size() > 0) {
                                for (News news1 : nlist.getNewslist()) {
                                    boolean b = false;
                                    for (News news2 : lvNewsData) {
                                        if (news1.getId() == news2.getId()) {
                                            b = true;
                                            break;
                                        }
                                    }
                                    if (!b)
                                        newdata++;
                                }
                            } else {
                                newdata = what;
                            }
                        }
                        lvNewsData.clear();// 先清除原有数据
                        lvNewsData.addAll(nlist.getNewslist());
                        break;
                }
        }
        return notice;
    }


    /**
     * 初始化新闻列表
     */
    public void initNewsListView() {
        lvNewsAdapter = new ListViewNewsAdapter(getActivity().getApplicationContext(), lvNewsData, R.layout.news_listitem);

        lvNews_footer = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listview_footer, null);
        lvNews_foot_more = (TextView) lvNews_footer
                .findViewById(R.id.listview_foot_more);
        lvNews_foot_progress = (ProgressBar) lvNews_footer
                .findViewById(R.id.listview_foot_progress);
        lvNews.addFooterView(lvNews_footer);// 添加底部视图 必须在setAdapter前
        lvNews.setAdapter(lvNewsAdapter);
        lvNews.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvNews_footer)
                    return;

                News news = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    news = (News) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.news_listitem_title);
                    news = (News) tv.getTag();
                }
                if (news == null)
                    return;

                // 跳转到新闻详情
                UIHelper.showNewsRedirect(view.getContext(), news);
            }
        });
        lvNews.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvNews.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvNewsData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvNews_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvNews.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvNews.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvNews_foot_more.setText(R.string.load_ing);
                    lvNews_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvNewsSumData / AppContext.PAGE_SIZE;
                    loadLvNewsData(curNewsCatalog, pageIndex,
                            UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvNews.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvNews.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                loadLvNewsData(curNewsCatalog, 0,
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }


    public void checkEmptyAndInit() {
        if (lvNewsData.isEmpty()) {
            loadLvNewsData(curNewsCatalog, 0, UIHelper.LISTVIEW_ACTION_INIT);
        }
    }
}
