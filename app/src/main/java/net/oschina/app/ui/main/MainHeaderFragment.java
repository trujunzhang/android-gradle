package net.oschina.app.ui.main;

import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.oschina.app.R;
import net.oschina.app.common.UIHelper;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringArrayRes;

/**
 * Created by djzhang on 5/14/14.
 */

@EFragment(R.layout.main_header)
public class MainHeaderFragment extends Fragment {

    @StringArrayRes(R.array.head_titles)
    public String[] mHeadTitles;

    @ViewById(R.id.main_head_logo)
    ImageView mHeadLogo;
    @ViewById(R.id.main_head_title)
    TextView mHeadTitle;
    @ViewById(R.id.main_head_progress)
    ProgressBar mHeadProgress;
    @ViewById(R.id.main_head_search)
    ImageButton mHead_search;
    @ViewById(R.id.main_head_pub_post)
    ImageButton mHeadPub_post;
    @ViewById(R.id.main_head_pub_tweet)
    ImageButton mHeadPub_tweet;

    public void setHeaderTitle(int index) {
        mHeadTitle.setText(mHeadTitles[index]);
    }

    public void setCurPoint(int index) {
        this.setHeaderTitle(index);

        mHead_search.setVisibility(View.GONE);
        mHeadPub_post.setVisibility(View.GONE);
        mHeadPub_tweet.setVisibility(View.GONE);
        // 头部logo、发帖、发动弹按钮显示
        if (index == 0) {
            mHeadLogo.setImageResource(R.drawable.frame_logo_news);
            mHead_search.setVisibility(View.VISIBLE);
        } else if (index == 1) {
            mHeadLogo.setImageResource(R.drawable.frame_logo_post);
            mHeadPub_post.setVisibility(View.VISIBLE);
        } else if (index == 2) {
            mHeadLogo.setImageResource(R.drawable.frame_logo_tweet);
            mHeadPub_tweet.setVisibility(View.VISIBLE);
        } else if (index == 3) {
            mHeadLogo.setImageResource(R.drawable.frame_logo_active);
            mHeadPub_tweet.setVisibility(View.VISIBLE);
        }
    }

    @Click(R.id.main_head_search)
    void main_head_searchOnClick(View view) {
        UIHelper.showSearch(view.getContext());
    }

    @Click(R.id.main_head_pub_post)
    void main_head_pub_postOnClick(View view) {
        UIHelper.showQuestionPub(view.getContext());
    }

    @Click(R.id.main_head_pub_tweet)
    void main_head_pub_tweetOnClick(View view) {
        UIHelper.showTweetPub(getActivity());
    }

}
