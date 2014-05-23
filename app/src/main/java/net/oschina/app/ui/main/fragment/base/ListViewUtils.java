package net.oschina.app.ui.main.fragment.base;


import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.oschina.app.R;
import net.oschina.app.bean.Notice;
import net.oschina.app.common.UIHelper;
import net.oschina.app.widget.PullToRefreshListView;

import java.util.Date;

public class ListViewUtils {


    public static void setSucessInfoForListView(int listCount, int pageSize, PullToRefreshListView listView, BaseAdapter adapter, TextView more, Notice notice) {
        // listview数据处理

        if (listCount < pageSize) {
            listView.setTag(UIHelper.LISTVIEW_DATA_FULL);
            adapter.notifyDataSetChanged();
            more.setText(R.string.load_full);
        } else if (listCount == pageSize) {
            listView.setTag(UIHelper.LISTVIEW_DATA_MORE);
            adapter.notifyDataSetChanged();
            more.setText(R.string.load_more);

            // 特殊处理-热门动弹不能翻页
//            if (listView == lvTweet) {
//                TweetList tlist = (TweetList) msg.obj;
//                if (lvTweetData.size() == tlist.getTweetCount()) {
//                    listView.setTag(UIHelper.LISTVIEW_DATA_FULL);
//                    more.setText(R.string.load_full);
//                }
//            }
        }
        // 发送通知广播
        if (notice != null) {
            UIHelper.sendBroadCast(listView.getContext(), notice);
        }
        // 是否清除通知信息
//        if (isClearNotice) {
//            ClearNotice(curClearNoticeType);
//            isClearNotice = false;// 重置
//            curClearNoticeType = 0;
//        }
    }

    public static void setFailureInfoForListView(PullToRefreshListView listView, TextView more) {
        // 有异常--显示加载出错 & 弹出错误消息
        listView.setTag(UIHelper.LISTVIEW_DATA_MORE);
        more.setText(R.string.load_error);
//                    ((AppException) msg.obj).makeToast(Main.this);
    }

    public static void setListViewStatusByRest(Context context, int listType, PullToRefreshListView listView) {
        if (listType == UIHelper.LISTVIEW_ACTION_REFRESH) {
            listView.onRefreshComplete(context.getString(R.string.pull_to_refresh_update) + new Date().toLocaleString());
            listView.setSelection(0);
        } else if (listType == UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG) {
            listView.onRefreshComplete();
            listView.setSelection(0);
        }
    }

    public static void endRestEffect(BaseAdapter adapter, PullToRefreshListView listView, TextView more, ProgressBar progress) {
        if (adapter.getCount() == 0) {
            listView.setTag(UIHelper.LISTVIEW_DATA_EMPTY);
            more.setText(R.string.load_empty);
        }
        progress.setVisibility(ProgressBar.GONE);
//                mHeadProgress.setVisibility(ProgressBar.GONE);
    }

}
