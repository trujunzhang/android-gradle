package net.oschina.app.ui.main.fragment;

import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.adapter.ListViewNewsAdapter;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Notice;
import net.oschina.app.common.UIHelper;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@EFragment(R.layout.frame_news_menu)
public class NewsMenuFragment extends AbstractMenuFragment {

    public int lvNewsSumData;
    public int curNewsCatalog = NewsList.CATALOG_ALL;

    public List<News> lvNewsData = new ArrayList<News>();

    @ViewById(R.id.frame_btn_news_lastest)
    public Button framebtn_News_lastest;

    @ViewById(R.id.frame_btn_news_recommend)
    public Button framebtn_News_recommend;
    private ListViewNewsAdapter lvNewsAdapter;


    @Click(R.id.frame_btn_news_lastest)
    void frame_btn_news_lastestOnClick(View view) {
        framebtn_News_lastest.setEnabled(false);
        framebtn_News_recommend.setEnabled(true);
//        loadLvNewsData(curNewsCatalog, 0, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
    }

    @Click(R.id.frame_btn_news_recommend)
    void frame_btn_news_recommendOnClick(View view) {
        framebtn_News_lastest.setEnabled(true);
        framebtn_News_recommend.setEnabled(false);
    }

    @AfterViews
    protected void calledAfterViewInjection() {
        // 设置首选择项
        framebtn_News_lastest.setEnabled(false);
    }

    @Override
    protected BaseAdapter getListAdapter() {
        if (lvNewsAdapter == null) {
            lvNewsAdapter = new ListViewNewsAdapter(getActivity().getApplicationContext(), lvNewsData, R.layout.news_listitem);
        }
        return lvNewsAdapter;
    }

    @Override
    /**
     * 1. 初始化listview数据（由服务器获得数据后生成adpater并显示）
     */
    public void initContentFragment(int pos) {
        setTabFragment(pos);
    }

    @Override
    protected void loadDataByRest(int pos) {
        loadLvNewsData(pos, 0, UIHelper.LISTVIEW_ACTION_INIT);
    }


    @Override
    public void showDetailsRedirect(Object object) {

    }

    @Override
    public int getListRowID() {
        return 0;
    }

    @Override
    public void loadMoreData() {

    }

    @Override
    public void refreshListView() {

    }

    @Override
    public boolean isEmpty() {
        return this.lvNewsData.isEmpty();
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
                    ListViewFragment_ fragment = getContentFragment(catalog);

                    NewsList list = NewsList.parse(inputStream);
                    int listCount = list.getPageSize();

                    if (listCount >= 0) {
                        Notice notice = getSucessNotice(listCount, list, UIHelper.LISTVIEW_DATATYPE_NEWS, action);
                        fragment.setSucessInfoForListView(listCount, ApiClient.PAGE_SIZE, notice);
//                        ListViewUtils.setSucessInfoForListView(listCount, ApiClient.PAGE_SIZE, lvNews, lvNewsAdapter, lvNews_foot_more, notice);
                    } else if (listCount == -1) {
                        fragment.setFailureInfoForListView();
//                        ListViewUtils.setFailureInfoForListView(lvNews, lvNews_foot_more);
                    }

                    fragment.endRestEffectWithListStatus(action);
//                    ListViewUtils.endRestEffect(lvNewsAdapter, lvNews, lvNews_foot_more, lvNews_foot_progress);
//                    ListViewUtils.setListViewStatusByRest(getActivity().getApplicationContext(), action, lvNews);

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


}
