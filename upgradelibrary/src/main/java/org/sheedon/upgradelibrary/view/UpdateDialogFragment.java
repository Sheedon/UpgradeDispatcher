package org.sheedon.upgradelibrary.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.DialogFragment;

import org.sheedon.upgradelibrary.R;
import org.sheedon.upgradelibrary.UpgradeInstaller;
import org.sheedon.upgradelibrary.model.UpgradeTaskModel;


/**
 * Created by Vector
 * on 2017/7/19 0019.
 */

public class UpdateDialogFragment extends DialogFragment implements View.OnClickListener {
    public static boolean isShow = false;
    private TextView mContentTextView;
    private Button mUpdateOkButton;
    private UpgradeTaskModel mUpdateApp;
    private ImageView mIvClose;
    private TextView mTitleTextView;

    private LinearLayout mLlClose;


    public static UpdateDialogFragment newInstance(UpgradeTaskModel model) {
        UpdateDialogFragment fragment = new UpdateDialogFragment();
        if (model != null) {
            fragment.setUpdateApp(model);
        }
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isShow = true;
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.UpdateAppDialog);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onStart() {
        super.onStart();
        //点击window外的区域 是否消失
        getDialog().setCanceledOnTouchOutside(false);

        getDialog().setOnKeyListener((dialog, keyCode, event) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK) {
                //禁用
                if (mUpdateApp != null && mUpdateApp.isConstraint()) {
                    //返回桌面
                    startActivity(new Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_HOME));
                    return true;
                } else {
                    return false;
                }
            }
            return false;
        });

        Window dialogWindow = getDialog().getWindow();
        dialogWindow.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        lp.height = (int) (displayMetrics.heightPixels * 0.8f);
        dialogWindow.setAttributes(lp);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.update_app_dialog, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void setUpdateApp(UpgradeTaskModel mUpdateApp) {
        this.mUpdateApp = mUpdateApp;
    }

    private void initView(View view) {
        //提示内容
        mContentTextView = view.findViewById(R.id.tv_update_info);
        //标题
        mTitleTextView = view.findViewById(R.id.tv_title);
        //更新按钮
        mUpdateOkButton = view.findViewById(R.id.btn_ok);
        //关闭按钮
        mIvClose = view.findViewById(R.id.iv_close);
        //关闭按钮+线 的整个布局
        mLlClose = view.findViewById(R.id.ll_close);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {
        if (mUpdateApp != null) {
            //弹出对话框
            final String dialogTitle = mUpdateApp.getDialogTitle();
            final String newVersion = mUpdateApp.getVersionName();
            final String updateLog = mUpdateApp.getDescription();

            String msg = "";

            if (!TextUtils.isEmpty(updateLog)) {
                msg += updateLog;
            } else {
                msg += getString(R.string.several_feature_updates);
            }

            //更新内容
            mContentTextView.setText(msg);
            //标题
            mTitleTextView.setText(TextUtils.isEmpty(dialogTitle) ? String.format(getString(R.string.app_update_hint),
                    newVersion) : dialogTitle);
            //强制更新
            if (mUpdateApp.isConstraint()) {
                mLlClose.setVisibility(View.GONE);
            }

            initEvents();
        }
    }

    private void initEvents() {
        mUpdateOkButton.setOnClickListener(this);
        mIvClose.setOnClickListener(this);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.btn_ok) {

            //权限判断是否有访问外部存储空间权限
            int flag = ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
            if (flag != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    // 用户拒绝过这个权限了，应该提示用户，为什么需要这个权限。
                    Toast.makeText(getActivity(), getString(R.string.app_update_tips), Toast.LENGTH_LONG).show();
                } else {
                    // 申请授权。
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                }

            } else {
                dismiss();
                installApp();
            }

        } else if (i == R.id.iv_close) {
            dismiss();
        }
    }

    private void installApp() {
        UpgradeInstaller.upgradeApp(getContext(), String.valueOf(mUpdateApp.getVersionCode()),
                mUpdateApp.getApkPath());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //升级
                installApp();
            } else {
                //提示，并且关闭
                Toast.makeText(getActivity(), getString(R.string.app_update_tips), Toast.LENGTH_LONG).show();
                dismiss();
            }
        }

    }
}

