package net.oschina.app.ui.main.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.*;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.Post;
import net.oschina.app.bean.PostList;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.frame_question_menu)
public class QuestionMenuFragment extends AbstractMenuFragment {

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

            }
        };
    }


    @Override
    protected BaseAdapter getListAdapter() {
        return null;
    }

    @Override
    public void initContentFragment(int pos) {

    }

    @Override
    protected void loadDataByRest(int pos) {

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
        return this.lvQuestionData.isEmpty();
    }
}
