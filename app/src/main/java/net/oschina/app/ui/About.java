package net.oschina.app.ui;

import net.oschina.app.R;
import net.oschina.app.common.UpdateManager;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

/**
 * 关于我们
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@EActivity(R.layout.about)
public class About extends BaseActivity {


    @ViewById(R.id.about_version)
    TextView aboutVersion;
    @ViewById(R.id.about_update)
    Button aboutUpdate;

    @AfterViews
    protected void init() {
        //获取客户端版本信息
        try {
            aboutVersion.setText("版本：" + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
    }


    @Click(R.id.about_update)
    void about_updateOnClick() {
        UpdateManager.getUpdateManager().checkAppUpdate(About.this, true);
    }
}
