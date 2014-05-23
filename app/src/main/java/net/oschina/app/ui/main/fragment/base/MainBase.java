package net.oschina.app.ui.main.fragment.base;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import net.oschina.app.AppContext;
import net.oschina.app.bean.Notice;
import net.oschina.app.ui.BaseActivity;
import net.oschina.app.widget.PullToRefreshListView;

public class MainBase extends BaseActivity {

    public AppContext appContext;// 全局Context

    public Context mContext;

    /**
     * 获取listview的初始化Handler
     *
     * @param listView
     * @param adapter
     * @return
     */
    public Handler getLvHandler(final PullToRefreshListView listView,
                                final BaseAdapter adapter, final TextView more,
                                final ProgressBar progress, final int pageSize) {
        return new Handler() {
            public void handleMessage(Message msg) {
                int listCount = msg.what;
                int listType = msg.arg1;
//
//                if (listCount >= 0) {
////                    setSucessInfoForListView(msg, listCount, pageSize, listView, adapter, more);
//                } else if (listCount == -1) {
//                    ListViewUtils.setFailureInfoForListView(listView, more);
//                }
//
//                ListViewUtils.endRestEffect(adapter, listView, more, progress);
//                ListViewUtils.setListViewStatusByRest(this, listType, listView);
            }
        };
    }


    /**
     * listview数据处理
     *
     * @param what       数量
     * @param obj        数据
     * @param objtype    数据类型
     * @param actiontype 操作类型
     * @return notice 通知信息
     */
    private static Notice handleLvData(int what, Object obj, int objtype, int actiontype) {
        Notice notice = null;
        switch (actiontype) {
//            case UIHelper.LISTVIEW_ACTION_INIT:
//            case UIHelper.LISTVIEW_ACTION_REFRESH:
//            case UIHelper.LISTVIEW_ACTION_CHANGE_CATALOG:
//                int newdata = 0;// 新加载数据-只有刷新动作才会使用到
//                switch (objtype) {
//                    case UIHelper.LISTVIEW_DATATYPE_NEWS:
//                        NewsList nlist = (NewsList) obj;
//                        notice = nlist.getNotice();
//                        lvSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvData.size() > 0) {
//                                for (News news1 : nlist.getNewslist()) {
//                                    boolean b = false;
//                                    for (News news2 : lvData) {
//                                        if (news1.getId() == news2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvData.clear();// 先清除原有数据
//                        lvData.addAll(nlist.getNewslist());
//                        break;
// ####################################################################################
// ####################################################################################

//                    case UIHelper.LISTVIEW_DATATYPE_BLOG:
//                        BlogList blist = (BlogList) obj;
//                        notice = blist.getNotice();
//                        lvBlogSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvBlogData.size() > 0) {
//                                for (Blog blog1 : blist.getBloglist()) {
//                                    boolean b = false;
//                                    for (Blog blog2 : lvBlogData) {
//                                        if (blog1.getId() == blog2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvBlogData.clear();// 先清除原有数据
//                        lvBlogData.addAll(blist.getBloglist());
//                        break;
// ####################################################################################
// ####################################################################################

//            case UIHelper.LISTVIEW_DATATYPE_POST:
//                PostList plist = (PostList) obj;
//                notice = plist.getNotice();
//                lvQuestionSumData = what;
//                if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                    if (lvQuestionData.size() > 0) {
//                        for (Post post1 : plist.getPostlist()) {
//                            boolean b = false;
//                            for (Post post2 : lvQuestionData) {
//                                if (post1.getId() == post2.getId()) {
//                                    b = true;
//                                    break;
//                                }
//                            }
//                            if (!b)
//                                newdata++;
//                        }
//                    } else {
//                        newdata = what;
//                    }
//                }
//                lvQuestionData.clear();// 先清除原有数据
//                lvQuestionData.addAll(plist.getPostlist());
//                break;
// ####################################################################################
// ####################################################################################

//                    case UIHelper.LISTVIEW_DATATYPE_TWEET:
//                        TweetList tlist = (TweetList) obj;
//                        notice = tlist.getNotice();
//                        lvTweetSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvTweetData.size() > 0) {
//                                for (Tweet tweet1 : tlist.getTweetlist()) {
//                                    boolean b = false;
//                                    for (Tweet tweet2 : lvTweetData) {
//                                        if (tweet1.getId() == tweet2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvTweetData.clear();// 先清除原有数据
//                        lvTweetData.addAll(tlist.getTweetlist());
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
//                        ActiveList alist = (ActiveList) obj;
//                        notice = alist.getNotice();
//                        lvActiveSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvActiveData.size() > 0) {
//                                for (Active active1 : alist.getActivelist()) {
//                                    boolean b = false;
//                                    for (Active active2 : lvActiveData) {
//                                        if (active1.getId() == active2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvActiveData.clear();// 先清除原有数据
//                        lvActiveData.addAll(alist.getActivelist());
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
//                        MessageList mlist = (MessageList) obj;
//                        notice = mlist.getNotice();
//                        lvMsgSumData = what;
//                        if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                            if (lvMsgData.size() > 0) {
//                                for (Messages msg1 : mlist.getMessagelist()) {
//                                    boolean b = false;
//                                    for (Messages msg2 : lvMsgData) {
//                                        if (msg1.getId() == msg2.getId()) {
//                                            b = true;
//                                            break;
//                                        }
//                                    }
//                                    if (!b)
//                                        newdata++;
//                                }
//                            } else {
//                                newdata = what;
//                            }
//                        }
//                        lvMsgData.clear();// 先清除原有数据
//                        lvMsgData.addAll(mlist.getMessagelist());
//                        break;
//                }
//                if (actiontype == UIHelper.LISTVIEW_ACTION_REFRESH) {
//                    // 提示新加载数据
//                    if (newdata > 0) {
//                        NewDataToast
//                                .makeText(
//                                        this,
//                                        getString(R.string.new_data_toast_message,
//                                                newdata), appContext.isAppSound()
//                                )
//                                .show();
//                    } else {
//                        NewDataToast.makeText(this,
//                                getString(R.string.new_data_toast_none), false)
//                                .show();
//                    }
//                }
//                break;
//            case UIHelper.LISTVIEW_ACTION_SCROLL:
//                switch (objtype) {
//                    case UIHelper.LISTVIEW_DATATYPE_NEWS:
//                        NewsList list = (NewsList) obj;
//                        notice = list.getNotice();
//                        lvSumData += what;
//                        if (lvData.size() > 0) {
//                            for (News news1 : list.getNewslist()) {
//                                boolean b = false;
//                                for (News news2 : lvData) {
//                                    if (news1.getId() == news2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvData.add(news1);
//                            }
//                        } else {
//                            lvData.addAll(list.getNewslist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_BLOG:
//                        BlogList blist = (BlogList) obj;
//                        notice = blist.getNotice();
//                        lvBlogSumData += what;
//                        if (lvBlogData.size() > 0) {
//                            for (Blog blog1 : blist.getBloglist()) {
//                                boolean b = false;
//                                for (Blog blog2 : lvBlogData) {
//                                    if (blog1.getId() == blog2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvBlogData.add(blog1);
//                            }
//                        } else {
//                            lvBlogData.addAll(blist.getBloglist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_POST:
//                        PostList plist = (PostList) obj;
//                        notice = plist.getNotice();
//                        lvQuestionSumData += what;
//                        if (lvQuestionData.size() > 0) {
//                            for (Post post1 : plist.getPostlist()) {
//                                boolean b = false;
//                                for (Post post2 : lvQuestionData) {
//                                    if (post1.getId() == post2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvQuestionData.add(post1);
//                            }
//                        } else {
//                            lvQuestionData.addAll(plist.getPostlist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_TWEET:
//                        TweetList tlist = (TweetList) obj;
//                        notice = tlist.getNotice();
//                        lvTweetSumData += what;
//                        if (lvTweetData.size() > 0) {
//                            for (Tweet tweet1 : tlist.getTweetlist()) {
//                                boolean b = false;
//                                for (Tweet tweet2 : lvTweetData) {
//                                    if (tweet1.getId() == tweet2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvTweetData.add(tweet1);
//                            }
//                        } else {
//                            lvTweetData.addAll(tlist.getTweetlist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_ACTIVE:
//                        ActiveList alist = (ActiveList) obj;
//                        notice = alist.getNotice();
//                        lvActiveSumData += what;
//                        if (lvActiveData.size() > 0) {
//                            for (Active active1 : alist.getActivelist()) {
//                                boolean b = false;
//                                for (Active active2 : lvActiveData) {
//                                    if (active1.getId() == active2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvActiveData.add(active1);
//                            }
//                        } else {
//                            lvActiveData.addAll(alist.getActivelist());
//                        }
//                        break;
//                    case UIHelper.LISTVIEW_DATATYPE_MESSAGE:
//                        MessageList mlist = (MessageList) obj;
//                        notice = mlist.getNotice();
//                        lvMsgSumData += what;
//                        if (lvMsgData.size() > 0) {
//                            for (Messages msg1 : mlist.getMessagelist()) {
//                                boolean b = false;
//                                for (Messages msg2 : lvMsgData) {
//                                    if (msg1.getId() == msg2.getId()) {
//                                        b = true;
//                                        break;
//                                    }
//                                }
//                                if (!b)
//                                    lvMsgData.add(msg1);
//                            }
//                        } else {
//                            lvMsgData.addAll(mlist.getMessagelist());
//                        }
//                        break;
//                }
//                break;
        }
        return notice;
    }

}
