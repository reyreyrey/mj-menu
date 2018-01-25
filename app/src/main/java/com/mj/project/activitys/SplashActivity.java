package com.mj.project.activitys;

import android.content.Intent;

import com.android.library.ui.SplashBaseActivity;
import com.android.library.utils.ResourceUtil;
import com.mj.project.BuildConfig;
import com.mj.project.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wiki on 2018/1/24.
 */

public class SplashActivity extends SplashBaseActivity {
    @Override
    protected void toMain() {
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    protected String getAppID() {
        return "2";
    }

    @Override
    protected int getSplashImageRes() {
        return ResourceUtil.getDrawableId(this, BuildConfig.SPLASH_PIC);
    }

    @Override
    protected boolean isShowGuide() {
        return true;
    }

    @Override
    protected int[] guideRess() {
        return new int[]{
                R.drawable.guide_1, R.drawable.guide_4, R.drawable.guide_5};
    }

    @Override
    protected Date showCaipiaoSplashPicTime() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(BuildConfig.show_splash_time);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
}
