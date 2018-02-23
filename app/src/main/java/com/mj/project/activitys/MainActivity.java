package com.mj.project.activitys;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarDrawerToggle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.android.library.base.UIActivity;
import com.android.library.manager.UIManager;
import com.android.library.manager.UserManager;
import com.android.library.models.BaseModel;
import com.android.library.models.UserModel;
import com.android.library.ui.AddPostActivity;
import com.android.library.utils.FileCacheUtils;
import com.android.library.utils.ToastUtils;
import com.android.library.utils.activity_manager.ActivityManager;
import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.mj.project.R;
import com.mj.project.databinding.ActivityMainBinding;
import com.mj.project.databinding.NavHeaderDrawerLayoutBinding;
import com.mj.project.utils.GlideImageLoaderBanner;
import com.mj.project.utils.Urls;
import com.youth.banner.listener.OnBannerListener;

import java.util.ArrayList;
import java.util.List;

import ticketsystem.ui.LibraryMainActivity;

import static com.mj.project.utils.Tools.changeDrawableToDefaultTheme;
import static com.youth.banner.BannerConfig.NUM_INDICATOR_TITLE;

public class MainActivity extends UIActivity<ActivityMainBinding> implements NavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    //drawable header view
    private NavHeaderDrawerLayoutBinding headerDrawerLayoutBinding;
    private UserModel userModel;
    private boolean isLogin = false;
    private TextView unReadSysMessageTextview;
    private String unReadSysMessageCount;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected void init() {
        tvTitle.setText("首页");
        initDrawlayout();
        databinding.contentInclude.actionButton.setOnClickListener(this);
        initBanner();
    }

    private void initBanner() {
        List<Integer> images = new ArrayList<>();
        images.add(R.drawable.baaner1);
        images.add(R.drawable.baaner2);
        images.add(R.drawable.baaner3);
        images.add(R.drawable.baaner4);
        List<String> titles = new ArrayList<>();
        titles.add("笑一笑十年少");
        titles.add("夜半惊魂，胆小勿入");
        titles.add("丰富多彩的论坛，让你一吐为快");
        titles.add("看累了，来一些养眼图片休息一会");
        databinding.contentInclude.banner.setImageLoader(new GlideImageLoaderBanner());
        databinding.contentInclude.banner.setBannerStyle(NUM_INDICATOR_TITLE);
        databinding.contentInclude.banner.setDelayTime(3000);
        databinding.contentInclude.banner.setImages(images);
        databinding.contentInclude.banner.setBannerTitles(titles);
        databinding.contentInclude.banner.setOnBannerListener(new OnBannerListener() {
            @Override
            public void OnBannerClick(int position) {
                switch (position) {
                    case 0:
                        UIManager.xiaohua(context);
                        break;
                    case 1:
                        UIManager.guigushi(context);
                        break;
                    case 2:
                        UIManager.postList(context);
                        break;
                    case 3:
                        UIManager.fuli(context);
                        break;
                }
            }
        });
        databinding.contentInclude.banner.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshHeadView();
        initIcons();
    }

    private void initDrawlayout() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, databinding.drawerLayout, toolbar, R.string.drawlayout_open, R.string.drawlayout_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                requestUnreadMessage();
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        databinding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        tvBack.setVisibility(View.GONE);
        databinding.navView.setNavigationItemSelectedListener(this);
        headerDrawerLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.nav_header_drawer_layout, null, false);
        databinding.navView.addHeaderView(headerDrawerLayoutBinding.getRoot());
    }

    private void requestUnreadMessage() {
        if (!isLogin) return;
        OkGo.<String>get(Urls.UNREAD_MESSAGE_COUNT_URL + userModel.getUserid())
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String result = response.body();
                        BaseModel<String> baseModel = new Gson().fromJson(result, BaseModel.class);
                        if (baseModel.getSuccess() == 1) {
                            unReadSysMessageCount = baseModel.getData();
                            if (unReadSysMessageTextview != null) {
                                unReadSysMessageTextview.setText(unReadSysMessageCount);
                            }
                        }
                    }
                });
    }

    private void refreshHeadView() {
        userModel = UserManager.getCurrentUser();
        isLogin = userModel != null;
        headerDrawerLayoutBinding.tvNoLogin.setOnClickListener(this);
        headerDrawerLayoutBinding.imgHeader.setOnClickListener(this);
        if (!isLogin) {
            headerDrawerLayoutBinding.layoutLogin.setVisibility(View.GONE);
            headerDrawerLayoutBinding.tvNoLogin.setVisibility(View.VISIBLE);
        } else {
            headerDrawerLayoutBinding.layoutLogin.setVisibility(View.VISIBLE);
            headerDrawerLayoutBinding.tvNoLogin.setVisibility(View.GONE);
            String photo = userModel.getPhoto();
            if (!TextUtils.isEmpty(photo)) {
                Glide.with(context).load(photo).into(headerDrawerLayoutBinding.imgHeader);
            }
            headerDrawerLayoutBinding.tvUsername.setText(userModel.getUsername());
            headerDrawerLayoutBinding.tvAttFan.setText("关注：" + (TextUtils.isEmpty(userModel.getAttCount()) ? "0" : userModel.getAttCount()) + " | 粉丝：" + (TextUtils.isEmpty(userModel.getFanCount()) ? "0" : userModel.getFanCount()));
            headerDrawerLayoutBinding.tvUsersign.setText(userModel.getSign());
        }
        refreshMenu();
    }

    private void initIcons() {
        databinding.contentInclude.tvOne.setOnClickListener(this);
        databinding.contentInclude.tvTwo.setOnClickListener(this);
        databinding.contentInclude.tvThree.setOnClickListener(this);
        databinding.contentInclude.tvFour.setOnClickListener(this);
        databinding.contentInclude.tvFive.setOnClickListener(this);
        databinding.contentInclude.tvSix.setOnClickListener(this);
        databinding.contentInclude.tvSeven.setOnClickListener(this);
        databinding.contentInclude.tvEight.setOnClickListener(this);
        databinding.contentInclude.tvNine.setOnClickListener(this);
        databinding.contentInclude.tvTen.setOnClickListener(this);
        //图标颜色变成设置的主题色\
        //开奖
        databinding.contentInclude.tvOne.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_home_kaijiang)), null, null);
        //艺术欣赏
        databinding.contentInclude.tvTwo.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_home_art)), null, null);
        //微信鸡汤
        databinding.contentInclude.tvThree.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_home_weixinmeiwen)), null, null);
        //每日一笑
        databinding.contentInclude.tvFour.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_h_xiaohua)), null, null);
        //走势图
        databinding.contentInclude.tvFive.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_home_zoushi)), null, null);
        //测试运气
        databinding.contentInclude.tvSix.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_shouqi)), null, null);
        //漫画
        databinding.contentInclude.tvSeven.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_home_manhua)), null, null);
        //鬼故事
        databinding.contentInclude.tvEight.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_home_guigushi)), null, null);
        //福利
        databinding.contentInclude.tvNine.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_home_fuli)), null, null);
        //论坛
        databinding.contentInclude.tvTen.setCompoundDrawablesWithIntrinsicBounds(null, changeDrawableToDefaultTheme(getResources().getDrawable(R.drawable.ic_home_faxian)), null, null);

    }

    private void refreshMenu() {
        unReadSysMessageTextview = (TextView) MenuItemCompat.getActionView(databinding.navView.getMenu().
                findItem(R.id.sys_msg));
        unReadSysMessageTextview.setGravity(Gravity.CENTER_VERTICAL);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_mine:
                if (!isLogin) {
                    UIManager.login(context);
                    ToastUtils.toastSuccess(context, "请先登录");
                    return true;
                }
                UIManager.mine(context);
                break;
            case R.id.sys_msg:
                UIManager.systemNotify(context);
                break;
            case R.id.friend_msg:
                if (!isLogin) {
                    UIManager.login(context);
                    ToastUtils.toastSuccess(context, "请先登录");
                    return true;
                }
                UIManager.chatList(context);
                break;
            case R.id.clear_cache:
                FileCacheUtils.cleanInternalCache(context);
                ToastUtils.toastSuccess(context, "缓存已清理");
                break;
            case R.id.check_update:
                UIManager.checkUpdate(context);
                break;
            case R.id.setting:
                UIManager.setting(context);
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_no_login:
            case R.id.img_header:
                if (isLogin) {
                    UIManager.mine(context);
                } else {
                    UIManager.login(context);
                }
                break;
            case R.id.tv_one:
//                UIManager.caipiaoHistory(context);
                startActivity(new Intent(context, LibraryMainActivity.class));
                break;
            case R.id.tv_two:
                UIManager.art(context);
                break;
            case R.id.tv_three:
                UIManager.wx(context);
                break;
            case R.id.tv_four:
                UIManager.xiaohua(context);
                break;
            case R.id.tv_five:
                UIManager.lotteryChar(context);
                break;
            case R.id.tv_six:
                UIManager.luck(context);
                break;
            case R.id.tv_seven:
                UIManager.manhua(context);
                break;
            case R.id.tv_eight:
                UIManager.guigushi(context);
                break;
            case R.id.tv_nine:
                UIManager.fuli(context);
                break;
            case R.id.tv_ten:
                UIManager.postList(context);
                break;
            case R.id.action_button:
                startActivity(new Intent(context, AddPostActivity.class));
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.search) {
            UIManager.searchUser(context);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private long backTime;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (databinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
                databinding.drawerLayout.closeDrawer(GravityCompat.START);
                return true;
            }
            if (backTime == 0) {
                backTime = System.currentTimeMillis();
                ToastUtils.toastWarn(this, getString(R.string.hybrid_exit_app));
                return true;
            }
            if ((System.currentTimeMillis() - backTime) >= 2000) {
                backTime = System.currentTimeMillis();
                ToastUtils.toastWarn(this, getString(R.string.hybrid_exit_app));
                return true;
            }
            ActivityManager.exitApp();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }


}
