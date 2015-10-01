package com.mvgv70.xposed_kgl_radio;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.Button;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;

public class Main implements IXposedHookLoadPackage, IXposedHookInitPackageResources
{

  private static Activity mtcRadio;
  private static OnClickListener mUi;
  private static int btn_search_id;
  private final static String TAG = "xposed-kgl-radio";
  
  @Override
  public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable 
  {
	// load resource
	XC_LayoutInflated loadPackage = new XC_LayoutInflated() {
		
	  @Override
	  public void handleLayoutInflated(LayoutInflatedParam liparam) throws Throwable 
	  {
	    Log.d(TAG,"handleLayoutInflated");
	    btn_search_id = liparam.res.getIdentifier("btn_search", "id", "com.microntek.radio");
	    Log.d(TAG,"btn_search="+btn_search_id);
      }
	};
	  
    if (!resparam.packageName.equals("com.microntek.radio")) return;
	resparam.res.hookLayout("com.microntek.radio", "layout", "radio", loadPackage);
    Log.d(TAG,"com.microntek.radio resource hook OK");
  }
  
  @Override
  public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable 
  {
    // RadioActivity.onCreate(Bindle)
    XC_MethodHook onCreate = new XC_MethodHook() {
	           
      @Override
      protected void afterHookedMethod(MethodHookParam param) throws Throwable {
    	Log.d(TAG,"onCreate");
      	mtcRadio = ((Activity)param.thisObject);
        // показать версию модуля
        try 
        {
     	  Context context = mtcRadio.createPackageContext(getClass().getPackage().getName(), Context.CONTEXT_IGNORE_SECURITY);
     	  String version = context.getString(R.string.app_version_name);
          Log.d(TAG,"version="+version);
     	} catch (NameNotFoundException e) {}
      	// кнопка поиска
      	Button btnSearch = (Button)mtcRadio.findViewById(btn_search_id);
      	if (btnSearch != null)
      	{
      	  // убираем обработчик нажатия
      	  btnSearch.setOnClickListener(null);
          btnSearch.setClickable(false);
          // устанавливаем обработчик длинного нажатия
      	  btnSearch.setLongClickable(true);
          btnSearch.setOnLongClickListener(searchLongClick);
      	  Log.d(TAG,"btnSearch remap OK");
      	}
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
