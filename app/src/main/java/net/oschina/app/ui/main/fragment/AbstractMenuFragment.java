package net.oschina.app.ui.main.fragment;

import android.support.v4.app.Fragment;
import android.widget.BaseAdapter;
import net.oschina.app.R;
import org.androidannotations.annotations.EFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

@EFragment
public abstract class AbstractMenuFragment extends Fragment implements ListViewFragment.OnListViewFragmentListener {

    /**
     * 子类提供的数据:
     * 1. adapter
     * 2. 服务器访问功能
     */

    // 1. adapter
    protected abstract BaseAdapter getListAdapter();


    // 1. 初始化listview数据（由服务器获得数据后生成adpater并显示）
    public abstract void initContentFragment(int pos);

    protected abstract void loadDataByRest(int pos);

    /**
     * 列表相关的数据
     */
    public int lvSumData;

    private HashMap<Integer, ListViewFragment_> listViewFragmentHashMap = new LinkedHashMap<Integer, ListViewFragment_>();

    public void setTabFragment(int pos) {
        // Fragments have access to their parent Activity's FragmentManager. You can
        // obtain the FragmentManager like this.
        FragmentManager fm = getFragmentManager();

        if (fm != null) {
            setContentFragment(pos, fm);
        }
    }

    private void setContentFragment(int catalog, FragmentManager fm) {
        // Perform the FragmentTransaction to load in the list tab content.
        // Using FragmentTransaction#replace will destroy any Fragments
        // currently inside R.id.fragment_content and add the new Fragment
        // in its place.
        FragmentTransaction ft = fm.beginTransaction();
        ListViewFragment_ fragment = getContentFragment(catalog);
        ft.replace(R.id.fragment_content, fragment);
        ft.commit();
    }

    protected ListViewFragment_ getContentFragment(int catalog) {
        ListViewFragment_ fragment = null;

        if (listViewFragmentHashMap.containsKey(catalog) == false) {
            fragment = new ListViewFragment_();
            fragment.setOnListViewFragmentListener(this);
            fragment.setCatalog(catalog);
            listViewFragmentHashMap.put(catalog, fragment);
        } else {
            fragment = listViewFragmentHashMap.get(catalog);
        }

        return fragment;
    }


    @Override
    public void initListView(ListViewFragment fragment) {
        fragment.initListView(getListAdapter());
        loadDataByRest(fragment.getCatalog());
    }


}
