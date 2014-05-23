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
import net.oschina.app.adapter.ListViewActiveAdapter;
import net.oschina.app.adapter.ListViewMessageAdapter;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.*;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import net.oschina.app.ui.main.fragment.base.ListViewUtils;
import net.oschina.app.widget.PullToRefreshListView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.frame_active)
public class ActiveFragment extends Fragment {


    @App
    public AppContext appContext;


    public int lvActiveSumData;
    public int lvMsgSumData;

    public List<Active> lvActiveData = new ArrayList<Active>();
    public List<Messages> lvMsgData = new ArrayList<Messages>();


    public View lvActive_footer;
    public View lvMsg_footer;


    public TextView lvActive_foot_more;
    public TextView lvMsg_foot_more;

    public ProgressBar lvActive_foot_progress;
    public ProgressBar lvMsg_foot_progress;


    @ViewById(R.id.frame_btn_active_lastest)
    public Button framebtn_Active_lastest;
    @ViewById(R.id.frame_btn_active_atme)
    public Button framebtn_Active_atme;
    @ViewById(R.id.frame_btn_active_comment)
    public Button framebtn_Active_comment;
    @ViewById(R.id.frame_btn_active_myself)
    public Button framebtn_Active_myself;
    @ViewById(R.id.frame_btn_active_message)
    public Button framebtn_Active_message;


    @ViewById(R.id.frame_listview_active)
    public PullToRefreshListView lvActive;
    @ViewById(R.id.frame_listview_message)
    public PullToRefreshListView lvMsg;

    public int curActiveCatalog = ActiveList.CATALOG_LASTEST;


    public ListViewActiveAdapter lvActiveAdapter;
    public ListViewMessageAdapter lvMsgAdapter;


    @AfterViews
    protected void calledAfterViewInjection() {

        // 设置首选择项
        framebtn_Active_lastest.setEnabled(false);

        // 动态+留言
        framebtn_Active_lastest.setOnClickListener(frameActiveBtnClick(
                framebtn_Active_lastest, ActiveList.CATALOG_LASTEST));
        framebtn_Active_atme.setOnClickListener(frameActiveBtnClick(
                framebtn_Active_atme, ActiveList.CATALOG_ATME));
        framebtn_Active_comment.setOnClickListener(frameActiveBtnClick(
                framebtn_Active_comment, ActiveList.CATALOG_COMMENT));
        framebtn_Active_myself.setOnClickListener(frameActiveBtnClick(
                framebtn_Active_myself, ActiveList.CATALOG_MYSELF));
        framebtn_Active_message.setOnClickListener(frameActiveBtnClick(
                framebtn_Active_message, 0));


        this.initActiveListView();
    }

    private View.OnClickListener frameActiveBtnClick(final Button btn, final int catalog) {

        return new View.OnClickListener() {
            public void onClick(View v) {
                // 判断登录
                if (!appContext.isLogin()) {
                    if (lvActive.getVisibility() == View.VISIBLE
                            && lvActiveData.isEmpty()) {
                        lvActive_foot_more.setText(R.string.load_empty);
                        lvActive_foot_progress.setVisibility(View.GONE);
                    } else if (lvMsg.getVisibility() == View.VISIBLE
                            && lvMsgData.isEmpty()) {
                        lvMsg_foot_more.setText(R.string.load_empty);
                        lvMsg_foot_progress.setVisibility(View.GONE);
                    }
//                    UIHelper.showLoginDialog(Main.this);
                    return;
                }

//                frameActiveBtnOnClick(btn, catalog, UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
            }
        };
    }


    /**
     * 初始化动态列表
     */
    public void initActiveListView() {
        lvActiveAdapter = new ListViewActiveAdapter(getActivity().getApplicationContext(), lvActiveData, R.layout.active_listitem);
        lvActive_footer = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listview_footer, null);
        lvActive_foot_more = (TextView) lvActive_footer.findViewById(R.id.listview_foot_more);
        lvActive_foot_progress = (ProgressBar) lvActive_footer.findViewById(R.id.listview_foot_progress);
        lvActive.addFooterView(lvActive_footer);// 添加底部视图 必须在setAdapter前
        lvActive.setAdapter(lvActiveAdapter);
        lvActive.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // 点击头部、底部栏无效
                if (position == 0 || view == lvActive_footer)
                    return;

                Active active = null;
                // 判断是否是TextView
                if (view instanceof TextView) {
                    active = (Active) view.getTag();
                } else {
                    TextView tv = (TextView) view
                            .findViewById(R.id.active_listitem_username);
                    active = (Active) tv.getTag();
                }
                if (active == null)
                    return;

                // 跳转
                UIHelper.showActiveRedirect(view.getContext(), active);
            }
        });
        lvActive.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvActive.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvActiveData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvActive_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvActive.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvActive.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvActive_foot_more.setText(R.string.load_ing);
                    lvActive_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvActiveSumData / AppContext.PAGE_SIZE;
                    loadLvActiveData(curActiveCatalog, pageIndex,
                            UIHelper.LISTVIEW_ACTION_SCROLL);
                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvActive.onScroll(view, firstVisibleItem, visibleItemCount,
                        totalItemCount);
            }
        });
        lvActive.setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
            public void onRefresh() {
                // 处理通知信息
//                if (curActiveCatalog == ActiveList.CATALOG_ATME && bv_atme.isShown()) {
//                    isClearNotice = true;
//                    curClearNoticeType = Notice.TYPE_ATME;
//                } else if (curActiveCatalog == ActiveList.CATALOG_COMMENT && bv_review.isShown()) {
//                    isClearNotice = true;
//                    curClearNoticeType = Notice.TYPE_COMMENT;
//                }
                // 刷新数据
                loadLvActiveData(curActiveCatalog, 0,
                        UIHelper.LISTVIEW_ACTION_REFRESH);
            }
        });
    }


    public void loadLvActiveData(final int catalog, final int pageIndex, final int action) {
        boolean isRefresh = false;
        if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
            isRefresh = true;
        }

        String url = ApiClient.getActiveListUrl(AppContext.loginUid, catalog, pageIndex, ApiClient.PAGE_SIZE);

        new AQuery(getActivity().getApplicationContext()).ajax(url, InputStream.class, new AjaxCallback<InputStream>() {
            @Override
            public void callback(String url, InputStream inputStream, AjaxStatus status) {
                try {
                    ActiveList list = ActiveList.parse(inputStream);
                    int listCount = list.getPageSize();

                    if (listCount >= 0) {
                        Notice notice = getSucessNotice(listCount, list, UIHelper.LISTVIEW_DATATYPE_POST, action);
                        ListViewUtils.setSucessInfoForListView(listCount, ApiClient.PAGE_SIZE, lvActive, lvActiveAdapter, lvActive_foot_more, notice);
                    } else if (listCount == -1) {
                        ListViewUtils.setFailureInfoForListView(lvActive, lvActive_foot_more);
                    }

                    ListViewUtils.endRestEffect(lvActiveAdapter, lvActive, lvActive_foot_more, lvActive_foot_progress);
                    ListViewUtils.setListViewStatusByRest(getActivity().getApplicationContext(), action, lvActive);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Notice getSucessNotice(int what, ActiveList plist, int objtype, int actiontype) {
        Notice notice = null;
        switch (actiontype) {
            case UIHelper.LISTVIEW_DATATYPE_POST:
                notice = plist.getNotice();
                lvActiveSumData = what;
                if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                    if (lvActiveData.size() > 0) {
                        for (Active post1 : plist.getActivelist()) {
                            boolean b = false;
                            for (Active post2 : lvActiveData) {
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

        lvActiveData.clear();// 先清除原有数据
        lvActiveData.addAll(plist.getActivelist());

        return notice;
    }


}
