package net.oschina.app.ui.main.fragment;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.*;
import net.oschina.app.AppContext;
import net.oschina.app.R;
import net.oschina.app.bean.Tweet;
import net.oschina.app.bean.TweetList;
import org.androidannotations.annotations.App;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

@EFragment(R.layout.frame_tweet_menu)
public class TweetMenuFragment extends AbstractMenuFragment {

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
        return this.lvTweetData.isEmpty();
    }
}
