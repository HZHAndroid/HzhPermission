# HzhPermission
【Android】一个权限申请工具类，直接复制到项目中即可使用。
# 使用方式
  提示 ： 
    
    1. 本人项目中使用的SDK版本   
    compileSdkVersion 25
    buildToolsVersion "26.0.0"
    
    2. (本类不使用单例，预防Activity嵌套Fragment，同时Activity和Fragment都有授权的适合，弹出框会弹出两次)
    
一、 在需要申请权限的地方如案例那样调用。（其中Manifest.permission.ACCESS_COARSE_LOCATION,
Manifest.permission.ACCESS_FINE_LOCATION是需要申请的权限）

     HzhPermission  mHzhPermission = new HzhPermission();
     
     mHzhPermission.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
     
                    Manifest.permission.ACCESS_FINE_LOCATION}, 2, new HzhPermission.OnHzhPermissionListener() {
                    
                @Override
                public void onHzhPermissionGrantedListener(int requestCode, String[] permissions) {
                    //所有权限授权成功的操作
                    Double _lat = TextUtils.isEmpty(mLat) ? null : Double.parseDouble(mLat);
                    Double _lng = TextUtils.isEmpty(mLng) ? null : Double.parseDouble(mLng);
                    new DisplayPositionUtil().open(OrgDetailActivity.this,
                            _lat, _lng, mAddress);
                }

                @Override
                public void onHzhPermissionDenitedListener(int requestCode, String[] permissions) {
                  //未授权或者取消授权的操作
                }
            });
二、 将onRequestPermissionsResult在Activity或者Fragment的onRequestPermissionsResult方法中调用(必须)

三、 将onActivityResult在Activity或者Fragment中的onActivityResult方法调用(可选,如果有调用，
在设置页面全部请求的权限都被授予后会调用onHzhPermissionGrantedListener)

# 提示
  对于方法或者其他具体的操作，可以参考本类中的注释。
  
