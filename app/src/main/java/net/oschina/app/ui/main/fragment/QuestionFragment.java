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
import net.oschina.app.adapter.ListViewQuestionAdapter;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.Notice;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostList;
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

@EFragment(R.layout.frame_question_menu)
public class QuestionFragment extends Fragment {

    @App
    public AppContext appContext;


    public int lvQuestionSumData;

    public List<Post> lvQuestionData = new ArrayList<Post>();

    public View lvQuestion_footer;


    public TextView lvQuestion_foot_more;

    public ProgressBar lvQuestion_foot_progress;


    @ViewById(R.id.frame_btn_question_ask)
    public Button framebtn_Question_ask;
    @ViewById(R.id.frame_btn_question_share)
    public Button framebtn_Question_share;
    @ViewById(R.id.frame_btn_question_other)
    public Button framebtn_Question_other;
    @ViewById(R.id.frame_btn_question_job)
    public Button framebtn_Question_job;
    @ViewById(R.id.frame_btn_question_site)
    public Button framebtn_Question_site;


    //    @ViewById(R.id.frame_listview_question)
    public PullToRefreshListView lvQuestion;

    public ListViewQuestionAdapter lvQuestionAdapter;

    public int curQuestionCatalog = PostList.CATALOG_ASK;


    @AfterViews
    protected void calledAfterViewInjection() {
        // 设置首选择项
        framebtn_Question_ask.setEnabled(false);

        // 问答
        framebtn_Question_ask.setOnClickListener(frameQuestionBtnClick(
                framebtn_Question_ask, PostList.CATALOG_ASK));
        framebtn_Question_share.setOnClickListener(frameQuestionBtnClick(
                framebtn_Question_share, PostList.CATALOG_SHARE));
        framebtn_Question_other.setOnClickListener(frameQuestionBtnClick(
                framebtn_Question_other, PostList.CATALOG_OTHER));
        framebtn_Question_job.setOnClickListener(frameQuestionBtnClick(
                framebtn_Question_job, PostList.CATALOG_JOB));
        framebtn_Question_site.setOnClickListener(frameQuestionBtnClick(
                framebtn_Question_site, PostList.CATALOG_SITE));

        this.initQuestionListView();
    }


    private View.OnClickListener frameQuestionBtnClick(final Button btn,
                                                       final int catalog) {
        return new View.OnClickListener() {
            public void onClick(View v) {
                if (btn == framebtn_Question_ask)
                    framebtn_Question_ask.setEnabled(false);
                else
                    framebtn_Question_ask.setEnabled(true);
                if (btn == framebtn_Question_share)
                    framebtn_Question_share.setEnabled(false);
                else
                    framebtn_Question_share.setEnabled(true);
                if (btn == framebtn_Question_other)
                    framebtn_Question_other.setEnabled(false);
                else
                    framebtn_Question_other.setEnabled(true);
                if (btn == framebtn_Question_job)
                    framebtn_Question_job.setEnabled(false);
                else
                    framebtn_Question_job.setEnabled(true);
                if (btn == framebtn_Question_site)
                    framebtn_Question_site.setEnabled(false);
                else
                    framebtn_Question_site.setEnabled(true);

                curQuestionCatalog = catalog;
                loadLvQuestionData(curQuestionCatalog, 0,
                        UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG);
            }
        };
    }

    public void loadLvQuestionData(final int catalog, final int pageIndex, final int action) {
        boolean isRefresh = false;
        if (action == UIHelper.LISTVIEW_ACTION_REFRESH
                || action == UIHelper.LISTVIEW_ACTION_SCROLL) {
            isRefresh = true;
        }

        String url = ApiClient.getPostListUrl(catalog, pageIndex, ApiClient.PAGE_SIZE);

        new AQuery(getActivity().getApplicationContext()).ajax(url, InputStream.class, new AjaxCallback<InputStream>() {
            @Override
            public void callback(String url, InputStream inputStream, AjaxStatus status) {
                try {
                    PostList list = PostList.parse(inputStream);
                    int listCount = list.getPageSize();

                    if (listCount >= 0) {
                        Notice notice = getSucessNotice(listCount, list, UIHelper.LISTVIEW_DATATYPE_POST, action);
                        ListViewUtils.setSucessInfoForListView(listCount, ApiClient.PAGE_SIZE, lvQuestion, lvQuestionAdapter, lvQuestion_foot_more, notice);
                    } else if (listCount == -1) {
                        ListViewUtils.setFailureInfoForListView(lvQuestion, lvQuestion_foot_more);
                    }

                    ListViewUtils.endRestEffect(lvQuestionAdapter, lvQuestion, lvQuestion_foot_more, lvQuestion_foot_progress);
                    ListViewUtils.setListViewStatusByRest(getActivity().getApplicationContext(), action, lvQuestion);

                } catch (IOException e) {
                    e.printStackTrace();
                } catch (AppException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private Notice getSucessNotice(int what, PostList plist, int objtype, int actiontype) {
        Notice notice = null;
        switch (actiontype) {
            case UIHelper.LISTVIEW_DATATYPE_POST:
                notice = plist.getNotice();
                lvQuestionSumData = what;
                if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
                    if (lvQuestionData.size() > 0) {
                        for (Post post1 : plist.getPostlist()) {
                            boolean b = false;
                            for (Post post2 : lvQuestionData) {
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

        lvQuestionData.clear();// 先清除原有数据
        lvQuestionData.addAll(plist.getPostlist());

        return notice;
    }


    /**
     * 初始化帖子列表
     */
    public void initQuestionListView() {
        lvQuestionAdapter = new ListViewQuestionAdapter(getActivity().getApplicationContext(), lvQuestionData, R.layout.question_listitem);
        lvQuestion_footer = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.listview_footer, null);
        lvQuestion_foot_more = (TextView) lvQuestion_footer.findViewById(R.id.listview_foot_more);
        lvQuestion_foot_progress = (ProgressBar) lvQuestion_footer.findViewById(R.id.listview_foot_progress);
        lvQuestion.addFooterView(lvQuestion_footer);// 添加底部视图 必须在setAdapter前
        lvQuestion.setAdapter(lvQuestionAdapter);
        lvQuestion
                .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        // 点击头部、底部栏无效
                        if (position == 0 || view == lvQuestion_footer)
                            return;

                        Post post = null;
                        // 判断是否是TextView
                        if (view instanceof TextView) {
                            post = (Post) view.getTag();
                        } else {
                            TextView tv = (TextView) view.findViewById(R.id.question_listitem_title);
                            post = (Post) tv.getTag();
                        }
                        if (post == null)
                            return;

                        // 跳转到问答详情
                        UIHelper.showQuestionDetail(view.getContext(),
                                post.getId());
                    }
                });
        lvQuestion.setOnScrollListener(new AbsListView.OnScrollListener() {
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                lvQuestion.onScrollStateChanged(view, scrollState);

                // 数据为空--不用继续下面代码了
                if (lvQuestionData.isEmpty())
                    return;

                // 判断是否滚动到底部
                boolean scrollEnd = false;
                try {
                    if (view.getPositionForView(lvQuestion_footer) == view
                            .getLastVisiblePosition())
                        scrollEnd = true;
                } catch (Exception e) {
                    scrollEnd = false;
                }

                int lvDataState = StringUtils.toInt(lvQuestion.getTag());
                if (scrollEnd && lvDataState == UIHelper.LISTVIEW_DATA_MORE) {
                    lvQuestion.setTag(UIHelper.LISTVIEW_DATA_LOADING);
                    lvQuestion_foot_more.setText(R.string.load_ing);
                    lvQuestion_foot_progress.setVisibility(View.VISIBLE);
                    // 当前pageIndex
                    int pageIndex = lvQuestionSumData / AppContext.PAGE_SIZE;
                    loadLvQuestionData(curQuestionCatalog, pageIndex, UIHelper.LISTVIEW_ACTION_SCROLL);

                }
            }

            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                lvQuestion.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount);

            }
        });
        lvQuestion
                .setOnRefreshListener(new PullToRefreshListView.OnRefreshListener() {
                    public void onRefresh() {
                        loadLvQuestionData(curQuestionCatalog, 0, UIHelper.LISTVIEW_ACTION_REFRESH);

                    }
                });
    }

}
