package com.mvgv70.xposed_kgl_radio;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.app.Activity;

public class Main implements IXposedHookLoadPackage 
{
	
  private static Activity mtcRadio;
  private static OnClickListener mUi;
  private final static String TAG = "xposed-kgl-radio";
  
  @Override
  public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable 
  {
    // MTCRadio.onCreate(Bindle)
    XC_MethodHook onCreate = new XC_MethodHook() {
	           
      @Override
      protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    	Log.d(TAG,"onCreate");
      	mtcRadio = ((Activity)param.thisObject);
      	// кнопка поиска
      	Button btnSearch = (Button)mtcRadio.findViewById(0x7f05001e);
      	mUi = (OnClickListener)XposedHelpers.getObjectField(param.thisObject, "mUi");
      	// убираем обработчик нажатия
      	btnSearch.setOnClickListener(null);
      	btnSearch.setClickable(false);
      	// устанавливаем обработчик длинного нажатия
      	btnSearch.setLongClickable(true);
      	btnSearch.setOnLongClickListener(searchLongClick);
      	Log.d(TAG,"btnSearch remap OK");
      }
    };
    
	// start hooks  
    if (!lpparam.packageName.equals("com.microntek.radio")) return;
    Log.d(TAG,"package com.microntek.radio");
    XposedHelpers.findAndHookMethod("com.microntek.radio.RadioActivity", lpparam.classLoader, "onCreate", Bundle.class, onCreate);
    Log.d(TAG,"com.microntek.radio hook OK");
  }

  // обработчик длинного нажатия на кнопку поиска
  private OnLongClickListener searchLongClick = new OnLongClickListener()
  {
    public boolean onLongClick(View v)
    {
      mUi.onClick(v);
	  return true;
    }
  };
  
};
