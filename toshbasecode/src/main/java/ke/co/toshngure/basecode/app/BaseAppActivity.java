/*
 * Copyright (c) 2017.
 *
 * Anthony Ngure
 *
 * Email : anthonyngure25@gmail.com
 */

package ke.co.toshngure.basecode.app;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.jaeger.library.StatusBarUtil;

import ke.co.toshngure.basecode.R;
import ke.co.toshngure.basecode.log.BeeLog;
import ke.co.toshngure.basecode.utils.BaseUtils;

/**
 * Created by Anthony Ngure on 18/09/2017.
 * Email : anthonyngure25@gmail.com.
 */

public class BaseAppActivity extends AppCompatActivity {


    protected static int sessionDepth = 0;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    protected BaseProgressDialog mProgressDialog;
    private Toolbar toolbar;


    public BaseAppActivity() {
    }

    @Override
    protected void onStart() {
        super.onStart();
        sessionDepth++;
    }


    @Override
    protected void onPause() {
        try {
            if ((mProgressDialog != null) && mProgressDialog.isShowing()) {
                hideProgressDialog();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (sessionDepth > 0) {
            sessionDepth--;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        overridePendingTransition(0, 0);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setupToolbar();
        setUpStatusBarColor();
    }

    protected void setUpStatusBarColor() {
        StatusBarUtil.setColor(this, BaseUtils.getColorAttr(this, R.attr.colorPrimaryDark), 1);
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(0, 0);
    }

    public AppCompatActivity getThis() {
        return this;
    }

    protected void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitleTextColor(Color.WHITE);
            setSupportActionBar(toolbar);
            if (getSupportActionBar() != null) {
                getSupportActionBar().setElevation(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_base, menu);
        BaseUtils.tintMenu(this, menu, Color.WHITE);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            this.finish();
            overridePendingTransition(R.anim.activity_in, R.anim.activity_out);
            return true;
        }
        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void hideKeyboardFrom(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public Toolbar getToolbar() {
        return toolbar;
    }

    public void showProgressDialog() {
        showProgressDialog(R.string.message_waiting);
    }

    protected void showProgressDialog(@StringRes int message) {
        showProgressDialog(getString(message));
    }

    protected void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new BaseProgressDialog(this)
                    .setMessage(message)
                    .setCancelable(false);
        }
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if ((mProgressDialog != null) && (mProgressDialog.isShowing())) {
            mProgressDialog.dismiss();
            mProgressDialog = null;
        }
    }

    public void toast(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void toastDebug(String msg) {
        if (BeeLog.DEBUG) {
            toast(msg);
        }
    }

    public void toast(@StringRes int string) {
        toast(getString(string));
    }
}
