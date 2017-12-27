package cn.yfwl.dygy.util.hzhpermission;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Administrator on 2017/7/5.
 * 创建人 ： Administrator
 * 功能描述 ：Hzh权限工具类
 * 创建时间 ：2017/7/5
 */

public class HzhPermission {
////    使用说明(本类不使用单例，预防Activity嵌套Fragment，同时Activity和Fragment都有授权的适合，弹出框会弹出两次)
//1.使用该对象，调用requestPermissions请求权限的方法
//2.将onRequestPermissionsResult在Activity或者Fragment的onRequestPermissionsResult方法中调用(必须)
//3.将onActivityResult在Activity或者Fragment中的onActivityResult方法调用(可选,如果有调用，在设置页面全部请求的权限都被授予后会调用onHzhPermissionGrantedListener)

    private final String TAG = "HzhPermission";

//    public static HzhPermission mHzhPermission;//HzhPermission对象

    private boolean mIsGoSettingScreen = false;//是否去了权限设置页面,该标志可以预防其他页面返回调用onActivityResult时候，防止本类的onActivityResult方法被调用
    private final String GOPERMISSIONSETTINGDIALOG_MSG = "当前应用缺少必要权限。\n请点击\"设置\"-\"权限\"-打开所需权限。";//去权限设置页面弹出框的消息
    private final String AGAINREQUESTPERMISSIONDIALOG_MSG = "请授予当前应用程序权限，如果没有足够权限，程序可能无法正常运行喔!";//重新申请授权弹出框的消息

    private final int SETTINGS_REQ_CODE = 1001;//跳转到权限设置请求码
    private String mPackageName = "";//应用程序的包名
    private Object mContextObj;//上下文对象
    private OnHzhPermissionListener mOnHzhPermissionListener;//权限授权监听对象

    private int mPermissionRequestCode = 0;//用于缓存当前权限请求码
    private String[] mPermissionArr = null;//用于缓存当前的权限数组


//    public static HzhPermission getInstance() {
////        if (mHzhPermission == null) {
//            mHzhPermission = new HzhPermission();
////        }
//        return mHzhPermission;
//    }

    /**
     * 权限授权监听对象
     */
    public interface OnHzhPermissionListener {
        /**
         * 所有权限被授予的回调
         *
         * @param requestCode 权限请求码
         * @param permissions 请求的权限数组
         */
        public void onHzhPermissionGrantedListener(int requestCode, String[] permissions);

        /**
         * 只有部分权限或者没有权限被授予的回调
         *
         * @param requestCode 权限请求码
         * @param permissions 请求的权限数组
         */
        public void onHzhPermissionDenitedListener(int requestCode, String[] permissions);
    }

    /**
     * 设置参数
     *
     * @param contextObj              上线文对象
     * @param permissions             权限数组
     * @param requestCode             权限请求码
     * @param onHzhPermissionListener 权限授权监听对象
     */
    private void setParamenters(Object contextObj, String[] permissions, int requestCode, OnHzhPermissionListener onHzhPermissionListener) {
        this.mContextObj = contextObj;
        this.mOnHzhPermissionListener = onHzhPermissionListener;
        this.mPermissionArr = permissions;
        this.mPermissionRequestCode = requestCode;
        this.mIsGoSettingScreen = false;
    }

    //-----------------对外调用部分start------------------

    /**
     * 请求权限
     *
     * @param activity                Activity
     * @param perms                   权限数组
     * @param requestCode             权限请求码
     * @param onHzhPermissionListener 权限授权监听对象
     */
    public void requestPermissions(Activity activity, String[] perms, int requestCode, OnHzhPermissionListener onHzhPermissionListener) {
//        if (checkPermissionsBase(activity, perms)) {
//            Toast.makeText(activity, "全部权限都有了", Toast.LENGTH_LONG).show();
//        } else {
//            requestPermissionsBase(activity, perms, requestCode);
//        }
        if (onHzhPermissionListener != null) {
            if (activity == null || perms == null || perms.length <= 0) {
                return;
            }
            setParamenters(activity, perms, requestCode, onHzhPermissionListener);
            if (checkSelfPermissionsBase(activity, perms)) {
                onHzhPermissionListener.onHzhPermissionGrantedListener(requestCode, perms);
            } else {
                if (shouldShowRequestPermissionRationalesBase(activity, perms)) {
                    showAgainRequestPermissionDialog(activity, perms, requestCode);
                } else {
                    requestPermissionsBase(activity, perms, requestCode);
                }
            }
        }
    }

    /**
     * 请求权限
     *
     * @param fragment                Fragment
     * @param perms                   权限数组
     * @param requestCode             权限请求码
     * @param onHzhPermissionListener 权限授权监听对象
     */
    public void requestPermissions(Fragment fragment, String[] perms, int requestCode, OnHzhPermissionListener onHzhPermissionListener) {
//        if (checkPermissionsBase(activity, perms)) {
//            Toast.makeText(activity, "全部权限都有了", Toast.LENGTH_LONG).show();
//        } else {
//            requestPermissionsBase(activity, perms, requestCode);
//        }
        if (onHzhPermissionListener != null) {
            if (fragment == null || perms == null || perms.length <= 0) {
                return;
            }
            setParamenters(fragment, perms, requestCode, onHzhPermissionListener);
            if (checkSelfPermissionsBase(fragment, perms)) {
                onHzhPermissionListener.onHzhPermissionGrantedListener(requestCode, perms);
            } else {
                if (shouldShowRequestPermissionRationalesBase(fragment, perms)) {
                    showAgainRequestPermissionDialog(fragment, perms, requestCode);
                } else {
                    requestPermissionsBase(fragment, perms, requestCode);
                }
            }
        }
    }

    /**
     * 请求权限
     *
     * @param fragment                android.app.Fragment
     * @param perms                   权限数组
     * @param requestCode             权限请求码
     * @param onHzhPermissionListener 权限授权监听对象
     */
    public void requestPermissions(android.app.Fragment fragment, String[] perms, int requestCode, OnHzhPermissionListener onHzhPermissionListener) {
//        if (checkPermissionsBase(activity, perms)) {
//            Toast.makeText(activity, "全部权限都有了", Toast.LENGTH_LONG).show();
//        } else {
//            requestPermissionsBase(activity, perms, requestCode);
//        }
        if (onHzhPermissionListener != null) {
            if (fragment == null || perms == null || perms.length <= 0) {
                return;
            }
            setParamenters(fragment, perms, requestCode, onHzhPermissionListener);
            if (checkSelfPermissionsBase(fragment, perms)) {
                onHzhPermissionListener.onHzhPermissionGrantedListener(requestCode, perms);
            } else {
                if (shouldShowRequestPermissionRationalesBase(fragment, perms)) {
                    showAgainRequestPermissionDialog(fragment, perms, requestCode);
                } else {
                    requestPermissionsBase(fragment, perms, requestCode);
                }
            }
        }
    }

    /**
     * 权限授予过程中的回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (mOnHzhPermissionListener != null && mContextObj != null) {
            if (checkSelfPermissionsBase(mContextObj, permissions)) {
                //全部权限被授予
                mOnHzhPermissionListener.onHzhPermissionGrantedListener(requestCode, permissions);
            } else {
                //只有部分权限或者没有权限被授予
                mOnHzhPermissionListener.onHzhPermissionDenitedListener(requestCode, permissions);
                if (shouldShowRequestPermissionRationalesBase(mContextObj, permissions)) {
//                    System.out.println("请弹出提醒框");
                } else {
                    showGoPermissioinSettingDialog(mContextObj);
                }
            }
        }
    }

    /**
     * 从设置页面回来的检测
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (mIsGoSettingScreen && requestCode == SETTINGS_REQ_CODE) {
            mIsGoSettingScreen = false;
            if (mOnHzhPermissionListener != null && mContextObj != null) {
                if (checkSelfPermissionsBase(mContextObj, mPermissionArr)) {
                    //全部权限被授予
                    mOnHzhPermissionListener.onHzhPermissionGrantedListener(mPermissionRequestCode, mPermissionArr);
                }
//                else {
//                    //只有部分权限或者没有权限被授予
//                    mOnHzhPermissionListener.onHzhPermissionDenitedListener(mPermissionRequestCode, mPermissionArr);
//                    if (shouldShowRequestPermissionRationalesBase(mContextObj, mPermissionArr)) {
////                    System.out.println("请弹出提醒框");
//                    } else {
//                        showGoPermissioinSettingDialog(mContextObj);
//                    }
//                }
            }
        }
    }

    //-----------------对外调用部分end------------------

    //---------------权限校验核心start-------------------

    /**
     * 校验权限
     *
     * @param object 可以是Activity/Fragment/android.app.Fragment
     * @param perms  权限数组
     * @return ture : 拥有全部权限 false : 没有或者拥有部分权限
     */
    private boolean checkPermissionsBase(Object object, String[] perms) {
        if (perms == null || perms.length <= 0) {
            showELog("getContext 方法的object为null");
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //6.0之后需要权限申请
            boolean _hasAllPermission = true;
            PackageManager _packageManager = getPackageManager(object);
            if (TextUtils.isEmpty(mPackageName)) {
                showELog("checkPermissionsBase 方法中的包名为空!");
                return false;
            }
            for (String permission : perms) {
                if (TextUtils.isEmpty(permission)) {
                    continue;
                }
                boolean _hasPermission = (PackageManager.PERMISSION_GRANTED ==
                        _packageManager.checkPermission(permission, mPackageName));
                if (!_hasPermission) {
                    _hasAllPermission = false;
                    break;
                }
            }
            return _hasAllPermission;
        } else {
            return true;
        }
    }

    /**
     * 校验是否拥有所有权限
     *
     * @param object 可以是Activity/Fragment/android.app.Fragment
     * @param perms  权限数组
     * @return ture : 拥有全部权限 false : 没有或者拥有部分权限
     */
    private boolean checkSelfPermissionsBase(Object object, String[] perms) {
        if (object == null || perms == null || perms.length <= 0) {
            showELog("checkSelfPermissionsBase 的方法的object为null,或者perms为空!");
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Context _context = getContext(object);
            if (_context == null) {
                showELog("checkSelfPermissionsBase 的方法的_context为null!");
                return false;
            }
            //6.0之后需要权限申请
            boolean _hasAllPermission = true;
            for (String permission : perms) {
                if (TextUtils.isEmpty(permission)) {
                    continue;
                }
                boolean _hasPermission = PackageManager.PERMISSION_GRANTED ==
                        ActivityCompat.checkSelfPermission(_context, permission);
                if (!_hasPermission) {
                    _hasAllPermission = false;
                    break;
                }
            }
            return _hasAllPermission;
        } else {
            return true;
        }
    }


    /**
     * 用户是否拒绝且未禁止弹出框,是否需要弹出窗进行解释权限
     *
     * @param object 可以是Activity/Fragment/android.app.Fragment
     * @param perms  权限数组
     * @return
     */
    private boolean shouldShowRequestPermissionRationalesBase(Object object, String[] perms) {
        if (object == null || perms == null || perms.length <= 0) {
            showELog("shouldShowRequestPermissionRationalesBase 的方法的object为null,或者perms为空!");
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            Activity _context = getContext(object);
//            if (_context == null) {
//                showELog("shouldShowRequestPermissionRationalesBase 的方法的_context为null!");
//                return false;
//            }
            //6.0之后需要权限申请
            boolean _hasAllPermission = false;

            for (String permission : perms) {
                if (TextUtils.isEmpty(permission)) {
                    continue;
                }
//                boolean _hasPermission = ActivityCompat.shouldShowRequestPermissionRationale(_context, permission);
                boolean _hasPermission = shouldShowRequestPermissionRationale(object, permission);
                if (_hasPermission) {
                    _hasAllPermission = _hasPermission;
                    break;
                }
            }
            return _hasAllPermission;
        } else {
            return false;
        }
    }

    /**
     * 是否被用户拒绝，但是没有被禁止再弹窗
     *
     * @param object     上下文对象
     * @param permission 权限数组
     * @return
     */
    private boolean shouldShowRequestPermissionRationale(Object object, String permission) {
        if (object == null) {
            showELog("shouldShowRequestPermissionRationale 方法object为null!");
            return false;
        }
        if (object instanceof Activity) {
            Activity _activity = (Activity) object;
            return ActivityCompat.shouldShowRequestPermissionRationale(_activity, permission);
        } else if (object instanceof Fragment) {
            Fragment _fragment = (Fragment) object;
            return _fragment.shouldShowRequestPermissionRationale(permission);
        } else if (object instanceof android.app.Fragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                android.app.Fragment _fragment = (android.app.Fragment) object;
                return _fragment.shouldShowRequestPermissionRationale(permission);
            } else {
                showELog("shouldShowRequestPermissionRationale 方法的 ((android.app.Fragment) object).shouldShowRequestPermissionRationale要求最小SDK是23");
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * 请求权限
     *
     * @param object      可以是Activity/Fragment/android.app.Fragment
     * @param perms       权限数组
     * @param requestCode 权限请求代码
     */
    private void requestPermissionsBase(Object object, String[] perms, int requestCode) {
        if (object == null) {
            showELog("requestPermissionsBase 的方法的object为null!");
            return;
        }
        if (object instanceof Activity) {
            ActivityCompat.requestPermissions((Activity) object, perms, requestCode);
        } else if (object instanceof Fragment) {
            ((Fragment) object).requestPermissions(perms, requestCode);
        } else if (object instanceof android.app.Fragment) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ((android.app.Fragment) object).requestPermissions(perms, requestCode);
            } else {
                showELog("requestPermissionsBase 方法的 ((android.app.Fragment) object).requestPermissions要求最小SDK是23");
            }
        }
    }

    //---------------权限校验核心end-------------------

    //---------------弹出框相关start-------------------

    /**
     * 弹出再次申请权限解释弹出框
     *
     * @param contextObj  上下文对象
     * @param permissions 权限数组
     * @param requestCode 权限请求码
     */
    private void showAgainRequestPermissionDialog(Object contextObj, String[] permissions, int requestCode) {
        showAgainRequestPermissionDialog(contextObj, AGAINREQUESTPERMISSIONDIALOG_MSG, permissions, requestCode, "授权", "取消");
    }

    /**
     * 弹出再次申请权限解释弹出框
     *
     * @param contextObj     上下文对象
     * @param explain        权限的说明或解释
     * @param permissions    权限数组
     * @param requestCode    权限请求码
     * @param positiveButton 弹出框右边按钮文字(积极的)
     * @param negativeButton 弹出框左边按钮文字(消极的)
     */
    private void showAgainRequestPermissionDialog(Object contextObj, String explain, String[] permissions, int requestCode, String positiveButton, String negativeButton) {
        if (contextObj == null) {
            showELog("showAgainRequestPermissionDialog 的方法的contextObj为null!");
            return;
        }
        final Context _context = getContext(contextObj);
        if (_context == null) {
            showELog("showAgainRequestPermissionDialog 的方法的_context为null!");
            return;
        }
        final String[] _permissions = permissions;
        final int _requestCode = requestCode;
        AlertDialog dialog = new AlertDialog.Builder(_context)
                .setMessage(TextUtils.isEmpty(explain) ? "" : explain)
                .setPositiveButton(TextUtils.isEmpty(positiveButton) ? "授权" : positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermissionsBase(_context, _permissions, _requestCode);
                    }
                })
                .setNegativeButton(TextUtils.isEmpty(negativeButton) ? "取消" : negativeButton, null)
                .create();
        dialog.show();
    }


    /**
     * 弹出跳转到权限设置页面提示弹出框
     *
     * @param contextObj 上下文对象
     */
    private void showGoPermissioinSettingDialog(Object contextObj) {
        showGoPermissioinSettingDialog(contextObj, GOPERMISSIONSETTINGDIALOG_MSG, "设置", "取消");
    }

    /**
     * 弹出跳转到权限设置页面提示弹出框
     *
     * @param contextObj     上下文对象
     * @param explain        权限的说明或解释
     * @param positiveButton 弹出框右边按钮文字(积极的)
     * @param negativeButton 弹出框左边按钮文字(消极的)
     */
    private void showGoPermissioinSettingDialog(Object contextObj, String explain, String positiveButton, String negativeButton) {
        if (contextObj == null) {
            showELog("showGoPermissioinSettingDialog 方法的contextObj为null!");
            return;
        }
        final Context _context = getContext(contextObj);
        if (_context == null) {
            showELog("showGoPermissioinSettingDialog 方法的_context为null!");
            return;
        }
        AlertDialog dialog = new AlertDialog.Builder(_context)
                .setMessage(TextUtils.isEmpty(explain) ? "" : explain)
                .setPositiveButton(TextUtils.isEmpty(positiveButton) ? "设置" : positiveButton, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", _context.getPackageName(), null);
                        intent.setData(uri);
                        startAppSettingsScreen(_context, intent);
                    }
                })
                .setNegativeButton(TextUtils.isEmpty(negativeButton) ? "取消" : negativeButton, null)
                .create();
        dialog.show();
    }

    /**
     * 跳转到手机权限设置页面
     *
     * @param object
     * @param intent
     */
    private void startAppSettingsScreen(Object object, Intent intent) {
        if (object == null || intent == null) {
            showELog("startAppSettingsScreen 的object或者intent为null!");
            return;
        }
        if (object instanceof Activity) {
            mIsGoSettingScreen = true;
            ((Activity) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof Fragment) {
            mIsGoSettingScreen = true;
            ((Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof android.app.Fragment) {
            mIsGoSettingScreen = true;
            ((android.app.Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        }

    }

    //---------------弹出框相关end-------------------

    //---------------其他start-------------------

    /**
     * 通过Activity设置包名
     *
     * @param activity Activity
     */

    private void setPackageName(Activity activity) {
        if (TextUtils.isEmpty(mPackageName)) {
            if (activity != null) {
                mPackageName = activity.getPackageName();
            }
        }
    }

    /**
     * 获取PackageManager
     *
     * @param object 可以是Activity/Fragment/android.app.Fragment
     * @return
     */
    private PackageManager getPackageManager(Object object) {
        if (object == null) {
            showELog("getPackageManager 方法的object为null");
            return null;
        }
        if (object instanceof Activity) {
            Activity _activity = (Activity) object;
            setPackageName(_activity);
            return _activity.getPackageManager();
        } else if (object instanceof Fragment) {
            Fragment _fragment = (Fragment) object;
            Activity _activity = _fragment.getActivity();
            setPackageName(_activity);
            return _activity.getPackageManager();
        } else if (object instanceof android.app.Fragment) {
            android.app.Fragment _fragment = (android.app.Fragment) object;
            Activity _activity = _fragment.getActivity();
            setPackageName(_activity);
            return _activity.getPackageManager();
        } else {
            showELog("getPackageManager 方法的object不属于Activity/Fragment/android.app.Fragment");
            return null;
        }
    }

    /**
     * 获取上下文
     *
     * @param object 可以是Activity/Fragment/android.app.Fragment
     * @return
     */
    private Activity getContext(Object object) {
        if (object == null) {
            showELog("getContext 方法的object为null");
            return null;
        }
        if (object instanceof Activity) {
            Activity _activity = (Activity) object;
            return _activity;
        } else if (object instanceof Fragment) {
            Fragment _fragment = (Fragment) object;
            Activity _activity = _fragment.getActivity();
            return _activity;
        } else if (object instanceof android.app.Fragment) {
            android.app.Fragment _fragment = (android.app.Fragment) object;
            Activity _activity = _fragment.getActivity();
            return _activity;
        } else {
            showELog("getContext 方法的object不属于Activity/Fragment/android.app.Fragment");
            return null;
        }
    }

    /**
     * 显示日志
     *
     * @param msg 需要显示的信息
     */
    private void showELog(String msg) {
        if (TextUtils.isEmpty(msg)) {
            Log.e(TAG, msg);
        }
    }

    //---------------其他end-------------------
}
