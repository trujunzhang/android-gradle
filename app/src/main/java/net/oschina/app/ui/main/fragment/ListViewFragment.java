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
import net.oschina.app.bean.News;
import net.oschina.app.bean.NewsList;
import net.oschina.app.bean.Notice;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.main.fragment.base.ListViewUtils;
import net.oschina.app.widget.PullToRefreshListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.frame_pulltorefresh_lisview)
public class ListViewFragment extends Fragment {
    private OnListViewFragmentListener onListViewFragmentListener;
    private BaseAdapter lvAdapter;
    private int catalog;


    public void setOnListViewFragmentListener(OnListViewFragmentListener onListViewFragmentListener) {
        this.onListViewFragmentListener = onListViewFragmentListener;
    }

    public void setCatalog(int catalog) {
        this.catalog = catalog;
    }

    public int getCatalog() {
        return catalog;
    }


    public static interface OnListViewFragmentListener {
        void showDetailsRedirect(Object object);

        int getListRowID();

        void loadMoreData();

        void refreshListView();

        boolean isEmpty();

        void initListView(ListViewFragment fragment);
    }


    public View lvListView_footer;

    public TextView lvListView_foot_more;

    public ProgressBar lvListView_foot_progress;

    @ViewById(R.id.frame_listview_with_more)
    public PullToRefreshListView lvListView;

    @AfterViews
    protected void calledAfterViewInjection() {
        this.onListViewFragmentListener.initListView(this);
    }


    /**
     * 初始化新闻列表
     */
    public void initListView(BaseAdapter lvAdapter) {
        this.lvAdapter = lvAdapter;
        lvListView_footer = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listview_footer, null);
        lvListView_foot_more = (TextView) lvListView_footer.findViewById(R.id.listview_foot_more);
        lvListView_foot_progress = (ProgressBar) lvListView_footer.findViewById(R.id.listview_foot_progress);
        lvListView.addFooterView(lvListView_footer);// 添加底部视图 必须在setAdapter前
        lvListView.setAdapter(lvAdapter);

        lvListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id1) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvListView_footer)
                    return;


                Object object = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    object = view.getTag();
                } else {
                    TextView tv = (TextView) view.findViewById(onListViewFragmentListener.getListRowID());
                    object = tv.getTag();
                }
                if (object == null)
                    return;

                // 跳转到详情
//                UIHelper.showNewsRedirect(view.getContext(), object);

                onListViewFragmentListener.showDetailsRedirect(object);
            }
        });
        lvListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvListView.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (onListViewFragmentListener.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvListView_footer) == view.getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvListView.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvListView.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvListView_foot_more.setText(R.string.load_ing);
                    lvListView_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    onListViewFragmentListener.loadMoreData();
//                    int pageIndex = lvSumData / AppContext.PAGE_SIZE;
//                    loadLvNewsData(curNewsCatalog, pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                lvListView.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);
            }
        });
        lvListView.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                onListViewFragmentListener.refreshListView();
//                loadLvNewsData(curNewsCatalog, 0, UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }


    public void setSucessInfoForListView(int listCount, int pageSize, Notice notice) {
        ListViewUtils.setSucessInfoForListView(listCount, ApiClient.PAGE_SIZE, lvListView, lvAdapter, lvListView_foot_more, notice);
    }

    public void setFailureInfoForListView() {
        ListViewUtils.setFailureInfoForListView(lvListView, lvListView_foot_more);
    }

    public void endRestEffectWithListStatus(int action) {
        ListViewUtils.endRestEffect(lvAdapter, lvListView, lvListView_foot_more, lvListView_foot_progress);
        ListViewUtils.setListViewStatusByRest(getActivity().getApplicationContext(), action, lvListView);
    }

}
