package net.oschina.app.ui;

import net.oschina.app.AppContext;
import net.oschina.app.AppException;
import net.oschina.app.R;
import net.oschina.app.api.ApiClient;
import net.oschina.app.bean.Result;
import net.oschina.app.bean.User;
import net.oschina.app.common.StringUtils;
import net.oschina.app.common.UIHelper;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ViewSwitcher;
import net.oschina.app.ui.main.Main;
import org.androidannotations.annotations.*;

/**
 * 用户登录对话框
 *
 * @author liux (http://my.oschina.net/liux)
 * @version 1.0
 * @created 2012-3-21
 */
@EActivity(R.layout.login_dialog)
public class LoginDialog extends BaseActivity {

    @App
    public AppContext appContext;

    @Extra("LOGINTYPE")
    int curLoginType;

    @ViewById(R.id.login_account)
    AutoCompleteTextView loginAccount;
    @ViewById(R.id.login_uly)
    android.widget.LinearLayout loginUly;
    @ViewById(R.id.login_password)
    EditText loginPassword;
    @ViewById(R.id.login_user_table)
    android.widget.TableLayout loginUserTable;
    @ViewById(R.id.login_checkbox_rememberMe)
    CheckBox loginCheckboxRememberMe;
    @ViewById(R.id.login_btn_login)
    Button loginBtnLogin;

    @ViewById(R.id.login_loading)
    View loginLoading;
    @ViewById(R.id.logindialog_view_switcher)
    ViewSwitcher logindialogViewSwitcher;
    @ViewById(R.id.login_scrollview)
    android.widget.ScrollView loginScrollview;
    @ViewById(R.id.login_close_button)
    ImageButton loginCloseButton;
    @ViewById(R.id.logindialog_space)
    android.widget.RelativeLayout logindialogSpace;

    private AnimationDrawable loadingAnimation;
    private InputMethodManager imm;

    public final static int LOGIN_OTHER = 0x00;
    public final static int LOGIN_MAIN = 0x01;
    public final static int LOGIN_SETTING = 0x02;

    @AfterViews
    protected void init() {

        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);

        loginCloseButton.setOnClickListener(UIHelper.finish(this));

        //是否显示登录信息
        User user = appContext.getLoginInfo();
        if (user == null || !user.isRememberMe()) return;
        if (!StringUtils.isEmpty(user.getAccount())) {
            loginAccount.setText(user.getAccount());
            loginAccount.selectAll();
            loginCheckboxRememberMe.setChecked(user.isRememberMe());
        }
        if (!StringUtils.isEmpty(user.getPwd())) {
            loginPassword.setText(user.getPwd());
        }
    }

    @Click(R.id.login_btn_login)
    void login_btn_loginOnClick(View v) {
        //隐藏软键盘
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

        String account = loginAccount.getText().toString();
        String pwd = loginPassword.getText().toString();
        boolean isRememberMe = loginCheckboxRememberMe.isChecked();
        //判断输入
        if (StringUtils.isEmpty(account)) {
            UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_email_null));
            return;
        }
        if (StringUtils.isEmpty(pwd)) {
            UIHelper.ToastMessage(v.getContext(), getString(R.string.msg_login_pwd_null));
            return;
        }

        loginCloseButton.setVisibility(View.GONE);
        loadingAnimation = (AnimationDrawable) loginLoading.getBackground();
        loadingAnimation.start();
        logindialogViewSwitcher.showNext();

        login(account, pwd, isRememberMe);
    }


    //登录验证
    private void login(final String account, final String pwd, final boolean isRememberMe) {
        final Handler handler = new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 1) {
                    User user = (User) msg.obj;
                    if (user != null) {
                        //清空原先cookie
                        ApiClient.cleanCookie();
                        //发送通知广播
                        UIHelper.sendBroadCast(LoginDialog.this, user.getNotice());
                        //提示登陆成功
                        UIHelper.ToastMessage(LoginDialog.this, R.string.msg_login_success);
                        if (curLoginType == LOGIN_MAIN) {
                            //跳转--加载用户动态
                            Intent intent = new Intent(LoginDialog.this, Main.class);
                            intent.putExtra("LOGIN", true);
                            startActivity(intent);
                        } else if (curLoginType == LOGIN_SETTING) {
                            //跳转--用户设置页面
                            Intent intent = new Intent(LoginDialog.this, Setting.class);
                            intent.putExtra("LOGIN", true);
                            startActivity(intent);
                        }
                        finish();
                    }
                } else if (msg.what == 0) {
                    logindialogViewSwitcher.showPrevious();
                    loginCloseButton.setVisibility(View.VISIBLE);
                    UIHelper.ToastMessage(LoginDialog.this, getString(R.string.msg_login_fail) + msg.obj);
                } else if (msg.what == -1) {
                    logindialogViewSwitcher.showPrevious();
                    loginCloseButton.setVisibility(View.VISIBLE);
                    ((AppException) msg.obj).makeToast(LoginDialog.this);
                }
            }
        };
        new Thread() {
            public void run() {
                Message msg = new Message();
                try {
                    AppContext ac = (AppContext) getApplication();
                    User user = ac.loginVerify(account, pwd);
                    user.setAccount(account);
                    user.setPwd(pwd);
                    user.setRememberMe(isRememberMe);
                    Result res = user.getValidate();
                    if (res.OK()) {
                        ac.saveLoginInfo(user);//保存登录信息
                        msg.what = 1;//成功
                        msg.obj = user;
                    } else {
                        ac.cleanLoginInfo();//清除登录信息
                        msg.what = 0;//失败
                        msg.obj = res.getErrorMessage();
                    }
                } catch (AppException e) {
                    e.printStackTrace();
                    msg.what = -1;
                    msg.obj = e;
                }
                handler.sendMessage(msg);
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            this.onDestroy();
        }
        return super.onKeyDown(keyCode, event);
    }
}
