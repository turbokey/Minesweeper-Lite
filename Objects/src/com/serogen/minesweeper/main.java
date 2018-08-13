package com.serogen.minesweeper;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = true;
	public static final boolean includeTitle = false;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.serogen.minesweeper", "com.serogen.minesweeper.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.serogen.minesweeper", "com.serogen.minesweeper.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.serogen.minesweeper.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public com.appodeal.basic4android.AppodealB4A _appodeal = null;
public static String _appkey = "";
public dominex.example.pinchzoomandmove[] _pz = null;
public static int _numview = 0;
public static boolean _game = false;
public anywheresoftware.b4a.objects.Timer _doubleclick = null;
public adr.stringfunctions.stringfunctions _sf = null;
public anywheresoftware.b4a.objects.MediaPlayerWrapper _mp = null;
public static int _concellheight = 0;
public static int _concellwidth = 0;
public static int _conzifrawidth = 0;
public static int _conzifraheight = 0;
public static int _confaceheight = 0;
public static int _confacewidth = 0;
public static int[][] _doska = null;
public static int[][] _doska1 = null;
public static int _kolmines = 0;
public static int _sizex = 0;
public static int _sizey = 0;
public anywheresoftware.b4a.objects.Timer _timer1 = null;
public anywheresoftware.b4a.objects.Timer _flagt = null;
public anywheresoftware.b4a.objects.Timer _voprt = null;
public static int _closecell = 0;
public static boolean _gameover = false;
public com.serogen.minesweeper.main._tindikator[] _ind = null;
public com.serogen.minesweeper.main._tface _face = null;
public com.serogen.minesweeper.main._tdoska _dsk = null;
public static int _oldi = 0;
public static int _oldj = 0;
public static long _oldx = 0L;
public static long _oldy = 0L;
public static int _oldface = 0;
public static float _xlong = 0f;
public static float _ylong = 0f;
public static int _mmm = 0;
public static int _nnn = 0;
public static long _time0 = 0L;
public static long _longclicktime = 0L;
public static float _x0 = 0f;
public static float _y0 = 0f;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _srcrect = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper _bmp = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _cnvs = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _cnvs2 = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _cnvs3 = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper _cnvs4 = null;
public anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper _rectfon = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _flag = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _vopr = null;
public static boolean _longc = false;
public anywheresoftware.b4a.objects.ButtonWrapper _new = null;
public anywheresoftware.b4a.objects.ButtonWrapper _beginner = null;
public anywheresoftware.b4a.objects.ButtonWrapper _medium = null;
public anywheresoftware.b4a.objects.ButtonWrapper _expert = null;
public anywheresoftware.b4a.objects.ButtonWrapper _sound = null;
public anywheresoftware.b4a.objects.ButtonWrapper _leaderboard = null;
public anywheresoftware.b4a.objects.Timer _menuanim = null;
public static boolean _hide = false;
public anywheresoftware.b4a.objects.ImageViewWrapper _imv = null;
public anywheresoftware.b4a.phone.Phone.PhoneVibrate _pv = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _galka = null;
public static int _level = 0;
public anywheresoftware.b4a.objects.ImageViewWrapper _galka2 = null;
public static boolean _soundon = false;
public anywheresoftware.b4a.phone.Phone _p = null;
public static boolean _winn = false;
public static boolean _lose = false;
public anywheresoftware.b4a.objects.PanelWrapper _mine2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _custom = null;
public anywheresoftware.b4a.objects.PanelWrapper _custompanel = null;
public anywheresoftware.b4a.objects.ButtonWrapper _ok = null;
public anywheresoftware.b4a.objects.ButtonWrapper _cancel = null;
public anywheresoftware.b4a.objects.EditTextWrapper _h = null;
public anywheresoftware.b4a.objects.EditTextWrapper _w = null;
public anywheresoftware.b4a.objects.EditTextWrapper _mine = null;
public static boolean _winopen = false;
public anywheresoftware.b4a.objects.Timer _facetimer = null;
public com.serogen.minesweeper.main._b _beg = null;
public com.serogen.minesweeper.main._b _emm = null;
public com.serogen.minesweeper.main._b _exp = null;
public anywheresoftware.b4a.objects.PanelWrapper _dlgrec = null;
public anywheresoftware.b4a.objects.LabelWrapper _bn = null;
public anywheresoftware.b4a.objects.LabelWrapper _in = null;
public anywheresoftware.b4a.objects.LabelWrapper _en = null;
public anywheresoftware.b4a.objects.LabelWrapper _bs = null;
public anywheresoftware.b4a.objects.LabelWrapper _iss = null;
public anywheresoftware.b4a.objects.LabelWrapper _es = null;
public anywheresoftware.b4a.objects.ButtonWrapper _ok2 = null;
public anywheresoftware.b4a.objects.ButtonWrapper _reset = null;
public anywheresoftware.b4a.objects.ButtonWrapper _rules = null;
public anywheresoftware.b4a.objects.ButtonWrapper _about = null;
public anywheresoftware.b4a.objects.EditTextWrapper _edittext1 = null;
public anywheresoftware.b4a.objects.PanelWrapper _bestpanel = null;
public anywheresoftware.b4a.objects.ButtonWrapper _ok3 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lbltext = null;
public static boolean _first = false;
public com.serogen.minesweeper.starter _starter = null;
public static class _tindikator{
public boolean IsInitialized;
public long x;
public long y;
public int value;
public void Initialize() {
IsInitialized = true;
x = 0L;
y = 0L;
value = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tface{
public boolean IsInitialized;
public long x;
public long y;
public int Face;
public void Initialize() {
IsInitialized = true;
x = 0L;
y = 0L;
Face = 0;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _tdoska{
public boolean IsInitialized;
public long x;
public long y;
public long width;
public long height;
public void Initialize() {
IsInitialized = true;
x = 0L;
y = 0L;
width = 0L;
height = 0L;
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}
public static class _b{
public boolean IsInitialized;
public int score;
public String name;
public void Initialize() {
IsInitialized = true;
score = 0;
name = "";
}
@Override
		public String toString() {
			return BA.TypeToString(this, false);
		}}

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _about_click() throws Exception{
anywheresoftware.b4a.phone.PackageManagerWrapper _pm = null;
 //BA.debugLineNum = 1497;BA.debugLine="Sub About_Click";
 //BA.debugLineNum = 1498;BA.debugLine="Dim pm As PackageManager";
_pm = new anywheresoftware.b4a.phone.PackageManagerWrapper();
 //BA.debugLineNum = 1499;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1500;BA.debugLine="Rules.Visible=False";
mostCurrent._rules.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1501;BA.debugLine="About.Visible=False";
mostCurrent._about.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1502;BA.debugLine="Msgbox2(\"'\" & pm.GetApplicationLabel(\"com.serogen";
anywheresoftware.b4a.keywords.Common.Msgbox2("'"+_pm.GetApplicationLabel("com.serogen.minesweeper")+"' "+_pm.GetVersionName("com.serogen.minesweeper")+anywheresoftware.b4a.keywords.Common.CRLF+"by Sergey Nesterov","About","OK","","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA);
 //BA.debugLineNum = 1503;BA.debugLine="End Sub";
return "";
}
public static String  _activity_create(boolean _firsttime) throws Exception{
float _vwidth = 0f;
float _vheight = 0f;
int _c = 0;
 //BA.debugLineNum = 144;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 146;BA.debugLine="Dim vWidth,vHeight As Float";
_vwidth = 0f;
_vheight = 0f;
 //BA.debugLineNum = 147;BA.debugLine="Dim c As Int";
_c = 0;
 //BA.debugLineNum = 148;BA.debugLine="For c = 0 To PZ.Length-1";
{
final int step3 = 1;
final int limit3 = (int) (mostCurrent._pz.length-1);
for (_c = (int) (0) ; (step3 > 0 && _c <= limit3) || (step3 < 0 && _c >= limit3); _c = ((int)(0 + _c + step3)) ) {
 //BA.debugLineNum = 149;BA.debugLine="vWidth = Activity.Width";
_vwidth = (float) (mostCurrent._activity.getWidth());
 //BA.debugLineNum = 150;BA.debugLine="vHeight = Activity.Height";
_vheight = (float) (mostCurrent._activity.getHeight());
 //BA.debugLineNum = 151;BA.debugLine="imv.Initialize(\"\")";
mostCurrent._imv.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 152;BA.debugLine="imv.Gravity = Gravity.FILL";
mostCurrent._imv.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 153;BA.debugLine="Activity.AddView(imv,0,0,vWidth,vHeight)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._imv.getObject()),(int) (0),(int) (0),(int) (_vwidth),(int) (_vheight));
 //BA.debugLineNum = 154;BA.debugLine="imv.Left = 0";
mostCurrent._imv.setLeft((int) (0));
 //BA.debugLineNum = 155;BA.debugLine="imv.top = 0";
mostCurrent._imv.setTop((int) (0));
 //BA.debugLineNum = 157;BA.debugLine="PZ(c).Initialize(imv,\"PZ\",c,1,3,3,False,Me)";
mostCurrent._pz[_c]._initialize(mostCurrent.activityBA,(anywheresoftware.b4a.objects.ConcreteViewWrapper) anywheresoftware.b4a.AbsObjectWrapper.ConvertToWrapper(new anywheresoftware.b4a.objects.ConcreteViewWrapper(), (android.view.View)(mostCurrent._imv.getObject())),"PZ",(Object)(_c),(float) (1),(float) (3),(float) (3),anywheresoftware.b4a.keywords.Common.False,main.getObject());
 //BA.debugLineNum = 158;BA.debugLine="PZ(c).LimitArea=True";
mostCurrent._pz[_c]._setlimitarea(anywheresoftware.b4a.keywords.Common.True);
 }
};
 //BA.debugLineNum = 160;BA.debugLine="Activity.LoadLayout(\"Mine\")";
mostCurrent._activity.LoadLayout("Mine",mostCurrent.activityBA);
 //BA.debugLineNum = 162;BA.debugLine="Appodeal.initialize(appKey, Bit.Or(Appodeal.BANNE";
mostCurrent._appodeal.initialize(mostCurrent.activityBA,mostCurrent._appkey,anywheresoftware.b4a.keywords.Common.Bit.Or(mostCurrent._appodeal.BANNER_BOTTOM,mostCurrent._appodeal.INTERSTITIAL));
 //BA.debugLineNum = 163;BA.debugLine="Appodeal.show(Appodeal.BANNER_BOTTOM)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.BANNER_BOTTOM);
 //BA.debugLineNum = 164;BA.debugLine="cnvs.Initialize(imv)";
mostCurrent._cnvs.Initialize((android.view.View)(mostCurrent._imv.getObject()));
 //BA.debugLineNum = 165;BA.debugLine="If FirstTime Then";
if (_firsttime) { 
 //BA.debugLineNum = 166;BA.debugLine="If File.Exists(File.DirInternalCache, \"Pref.txt\")";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"Pref.txt")==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 167;BA.debugLine="File.Copy(File.DirAssets, \"Pref.txt\", File.DirI";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Pref.txt",anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"Pref.txt");
 };
 };
 //BA.debugLineNum = 170;BA.debugLine="LoadFile";
_loadfile();
 //BA.debugLineNum = 171;BA.debugLine="vistavlenie";
_vistavlenie();
 //BA.debugLineNum = 172;BA.debugLine="cnvs2.Initialize(Mine2)";
mostCurrent._cnvs2.Initialize((android.view.View)(mostCurrent._mine2.getObject()));
 //BA.debugLineNum = 173;BA.debugLine="bmp.Initialize(File.DirAssets, \"Mine2.bmp\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Mine2.bmp");
 //BA.debugLineNum = 174;BA.debugLine="RectFon.Initialize(0,0,Mine2.Width,Mine2.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._mine2.getWidth(),mostCurrent._mine2.getHeight());
 //BA.debugLineNum = 175;BA.debugLine="cnvs2.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs2.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 176;BA.debugLine="Mine2.Invalidate";
mostCurrent._mine2.Invalidate();
 //BA.debugLineNum = 177;BA.debugLine="cnvs3.Initialize(CustomPanel)";
mostCurrent._cnvs3.Initialize((android.view.View)(mostCurrent._custompanel.getObject()));
 //BA.debugLineNum = 178;BA.debugLine="bmp.Initialize(File.DirAssets, \"Tint.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Tint.png");
 //BA.debugLineNum = 179;BA.debugLine="RectFon.Initialize(0,0,CustomPanel.Width,CustomPan";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._custompanel.getWidth(),mostCurrent._custompanel.getHeight());
 //BA.debugLineNum = 180;BA.debugLine="cnvs3.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs3.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 181;BA.debugLine="CustomPanel.Invalidate";
mostCurrent._custompanel.Invalidate();
 //BA.debugLineNum = 182;BA.debugLine="cnvs3.Initialize(CustomPanel)";
mostCurrent._cnvs3.Initialize((android.view.View)(mostCurrent._custompanel.getObject()));
 //BA.debugLineNum = 183;BA.debugLine="bmp.Initialize(File.DirAssets, \"CustomPanel.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"CustomPanel.png");
 //BA.debugLineNum = 184;BA.debugLine="RectFon.Initialize(0,20%x,CustomPanel.Width,120%x)";
mostCurrent._rectfon.Initialize((int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),mostCurrent._custompanel.getWidth(),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (120),mostCurrent.activityBA));
 //BA.debugLineNum = 185;BA.debugLine="cnvs3.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs3.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 186;BA.debugLine="CustomPanel.Invalidate";
mostCurrent._custompanel.Invalidate();
 //BA.debugLineNum = 188;BA.debugLine="cnvs3.Initialize(dlgrec)";
mostCurrent._cnvs3.Initialize((android.view.View)(mostCurrent._dlgrec.getObject()));
 //BA.debugLineNum = 189;BA.debugLine="bmp.Initialize(File.DirAssets, \"Tint.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Tint.png");
 //BA.debugLineNum = 190;BA.debugLine="RectFon.Initialize(0,0,dlgrec.Width,dlgrec.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._dlgrec.getWidth(),mostCurrent._dlgrec.getHeight());
 //BA.debugLineNum = 191;BA.debugLine="cnvs3.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs3.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 192;BA.debugLine="dlgrec.Invalidate";
mostCurrent._dlgrec.Invalidate();
 //BA.debugLineNum = 193;BA.debugLine="cnvs3.Initialize(dlgrec)";
mostCurrent._cnvs3.Initialize((android.view.View)(mostCurrent._dlgrec.getObject()));
 //BA.debugLineNum = 194;BA.debugLine="bmp.Initialize(File.DirAssets, \"Fastest.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Fastest.png");
 //BA.debugLineNum = 195;BA.debugLine="RectFon.Initialize(0,20%x,dlgrec.Width,120%x)";
mostCurrent._rectfon.Initialize((int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),mostCurrent._dlgrec.getWidth(),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (120),mostCurrent.activityBA));
 //BA.debugLineNum = 196;BA.debugLine="cnvs3.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs3.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 197;BA.debugLine="dlgrec.Invalidate";
mostCurrent._dlgrec.Invalidate();
 //BA.debugLineNum = 198;BA.debugLine="cnvs3.Initialize(BestPanel)";
mostCurrent._cnvs3.Initialize((android.view.View)(mostCurrent._bestpanel.getObject()));
 //BA.debugLineNum = 199;BA.debugLine="bmp.Initialize(File.DirAssets, \"Tint.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Tint.png");
 //BA.debugLineNum = 200;BA.debugLine="RectFon.Initialize(0,0,BestPanel.Width,BestPanel.H";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._bestpanel.getWidth(),mostCurrent._bestpanel.getHeight());
 //BA.debugLineNum = 201;BA.debugLine="cnvs3.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs3.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 202;BA.debugLine="BestPanel.Invalidate";
mostCurrent._bestpanel.Invalidate();
 //BA.debugLineNum = 203;BA.debugLine="InitGame";
_initgame();
 //BA.debugLineNum = 204;BA.debugLine="End Sub";
return "";
}
public static boolean  _activity_keypress(int _keycode) throws Exception{
 //BA.debugLineNum = 1376;BA.debugLine="Sub Activity_KeyPress (KeyCode As Int) As Boolean";
 //BA.debugLineNum = 1377;BA.debugLine="If KeyCode = KeyCodes.KEYCODE_BACK Then";
if (_keycode==anywheresoftware.b4a.keywords.Common.KeyCodes.KEYCODE_BACK) { 
 //BA.debugLineNum = 1378;BA.debugLine="Select Msgbox2(\"Are you sure you want to exit the";
switch (BA.switchObjectToInt(anywheresoftware.b4a.keywords.Common.Msgbox2("Are you sure you want to exit the application?","","Yes","Cancel","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE,anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL,anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE)) {
case 0: {
 //BA.debugLineNum = 1380;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 break; }
case 1: {
 //BA.debugLineNum = 1382;BA.debugLine="Return True";
if (true) return anywheresoftware.b4a.keywords.Common.True;
 break; }
case 2: {
 //BA.debugLineNum = 1384;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1385;BA.debugLine="ExitApplication";
anywheresoftware.b4a.keywords.Common.ExitApplication();
 break; }
}
;
 };
 //BA.debugLineNum = 1388;BA.debugLine="End Sub";
return false;
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 625;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 626;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 627;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 622;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 623;BA.debugLine="End Sub";
return "";
}
public static String  _beginner_click() throws Exception{
 //BA.debugLineNum = 1170;BA.debugLine="Sub Beginner_Click";
 //BA.debugLineNum = 1171;BA.debugLine="SizeX=9";
_sizex = (int) (9);
 //BA.debugLineNum = 1172;BA.debugLine="SizeY=9";
_sizey = (int) (9);
 //BA.debugLineNum = 1173;BA.debugLine="KolMines=10";
_kolmines = (int) (10);
 //BA.debugLineNum = 1174;BA.debugLine="Level=0";
_level = (int) (0);
 //BA.debugLineNum = 1175;BA.debugLine="InitGame";
_initgame();
 //BA.debugLineNum = 1176;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1177;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1178;BA.debugLine="If Appodeal.isLoaded(Appodeal.INTERSTITIAL) Then";
if (mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL)) { 
 //BA.debugLineNum = 1179;BA.debugLine="Appodeal.show(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 //BA.debugLineNum = 1180;BA.debugLine="Appodeal.cache(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.cache(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 };
 //BA.debugLineNum = 1182;BA.debugLine="End Sub";
return "";
}
public static String  _cancel_click() throws Exception{
anywheresoftware.b4a.objects.AnimationWrapper _a1 = null;
 //BA.debugLineNum = 1263;BA.debugLine="Sub Cancel_Click";
 //BA.debugLineNum = 1264;BA.debugLine="Dim a1 As Animation";
_a1 = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 1265;BA.debugLine="a1.InitializeAlpha(\"\", 1, 0)";
_a1.InitializeAlpha(mostCurrent.activityBA,"",(float) (1),(float) (0));
 //BA.debugLineNum = 1266;BA.debugLine="a1.Duration = 800";
_a1.setDuration((long) (800));
 //BA.debugLineNum = 1267;BA.debugLine="a1.Start(CustomPanel)";
_a1.Start((android.view.View)(mostCurrent._custompanel.getObject()));
 //BA.debugLineNum = 1268;BA.debugLine="CustomPanel.Visible=False";
mostCurrent._custompanel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1269;BA.debugLine="WinOpen=False";
_winopen = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1270;BA.debugLine="End Sub";
return "";
}
public static String  _clear_sosedi() throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 1470;BA.debugLine="Sub Clear_sosedi";
 //BA.debugLineNum = 1471;BA.debugLine="Dim i As Int, j As Int";
_i = 0;
_j = 0;
 //BA.debugLineNum = 1472;BA.debugLine="For i = 0 To SizeY + 1";
{
final int step2 = 1;
final int limit2 = (int) (_sizey+1);
for (_i = (int) (0) ; (step2 > 0 && _i <= limit2) || (step2 < 0 && _i >= limit2); _i = ((int)(0 + _i + step2)) ) {
 //BA.debugLineNum = 1473;BA.debugLine="For j = 0 To SizeX";
{
final int step3 = 1;
final int limit3 = _sizex;
for (_j = (int) (0) ; (step3 > 0 && _j <= limit3) || (step3 < 0 && _j >= limit3); _j = ((int)(0 + _j + step3)) ) {
 //BA.debugLineNum = 1474;BA.debugLine="If Doska(i, j) <> -1 Then";
if (_doska[_i][_j]!=-1) { 
 //BA.debugLineNum = 1475;BA.debugLine="Doska(i, j) = 0";
_doska[_i][_j] = (int) (0);
 };
 }
};
 }
};
 //BA.debugLineNum = 1479;BA.debugLine="End Sub";
return "";
}
public static String  _count_sosedi() throws Exception{
int _i = 0;
int _j = 0;
int _i1 = 0;
int _j1 = 0;
 //BA.debugLineNum = 1480;BA.debugLine="Sub Count_sosedi";
 //BA.debugLineNum = 1481;BA.debugLine="Dim i As Int, j As Int, i1 As Int, j1 As Int";
_i = 0;
_j = 0;
_i1 = 0;
_j1 = 0;
 //BA.debugLineNum = 1482;BA.debugLine="For i = 1 To SizeY";
{
final int step2 = 1;
final int limit2 = _sizey;
for (_i = (int) (1) ; (step2 > 0 && _i <= limit2) || (step2 < 0 && _i >= limit2); _i = ((int)(0 + _i + step2)) ) {
 //BA.debugLineNum = 1483;BA.debugLine="For j = 1 To SizeX";
{
final int step3 = 1;
final int limit3 = _sizex;
for (_j = (int) (1) ; (step3 > 0 && _j <= limit3) || (step3 < 0 && _j >= limit3); _j = ((int)(0 + _j + step3)) ) {
 //BA.debugLineNum = 1484;BA.debugLine="If Doska(i, j) = -1 Then";
if (_doska[_i][_j]==-1) { 
 //BA.debugLineNum = 1485;BA.debugLine="For i1 = i - 1 To i + 1";
{
final int step5 = 1;
final int limit5 = (int) (_i+1);
for (_i1 = (int) (_i-1) ; (step5 > 0 && _i1 <= limit5) || (step5 < 0 && _i1 >= limit5); _i1 = ((int)(0 + _i1 + step5)) ) {
 //BA.debugLineNum = 1486;BA.debugLine="For j1 = j - 1 To j + 1";
{
final int step6 = 1;
final int limit6 = (int) (_j+1);
for (_j1 = (int) (_j-1) ; (step6 > 0 && _j1 <= limit6) || (step6 < 0 && _j1 >= limit6); _j1 = ((int)(0 + _j1 + step6)) ) {
 //BA.debugLineNum = 1487;BA.debugLine="If Doska(i1, j1) >= 0 Then";
if (_doska[_i1][_j1]>=0) { 
 //BA.debugLineNum = 1488;BA.debugLine="Doska(i1, j1) = Doska(i1, j1)";
_doska[_i1][_j1] = (int) (_doska[_i1][_j1]+1);
 };
 }
};
 }
};
 };
 }
};
 }
};
 //BA.debugLineNum = 1496;BA.debugLine="End Sub";
return "";
}
public static String  _custom_click() throws Exception{
anywheresoftware.b4a.objects.AnimationWrapper _a1 = null;
 //BA.debugLineNum = 1220;BA.debugLine="Sub Custom_Click";
 //BA.debugLineNum = 1221;BA.debugLine="Dim a1 As Animation";
_a1 = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 1222;BA.debugLine="a1.InitializeAlpha(\"\", 0, 1)";
_a1.InitializeAlpha(mostCurrent.activityBA,"",(float) (0),(float) (1));
 //BA.debugLineNum = 1223;BA.debugLine="a1.Duration = 800";
_a1.setDuration((long) (800));
 //BA.debugLineNum = 1224;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1225;BA.debugLine="CustomPanel.Visible=True";
mostCurrent._custompanel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1226;BA.debugLine="a1.Start(CustomPanel)";
_a1.Start((android.view.View)(mostCurrent._custompanel.getObject()));
 //BA.debugLineNum = 1227;BA.debugLine="Mine.Enabled=True";
mostCurrent._mine.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1228;BA.debugLine="SetEditTextSize(H,SizeY,100,1)";
_setedittextsize(mostCurrent._h,BA.NumberToString(_sizey),(float) (100),(float) (1));
 //BA.debugLineNum = 1229;BA.debugLine="SetEditTextSize(W,SizeX,100,1)";
_setedittextsize(mostCurrent._w,BA.NumberToString(_sizex),(float) (100),(float) (1));
 //BA.debugLineNum = 1230;BA.debugLine="SetEditTextSize(Mine,KolMines,100,1)";
_setedittextsize(mostCurrent._mine,BA.NumberToString(_kolmines),(float) (100),(float) (1));
 //BA.debugLineNum = 1231;BA.debugLine="H.Text=SizeY";
mostCurrent._h.setText((Object)(_sizey));
 //BA.debugLineNum = 1232;BA.debugLine="W.Text=SizeX";
mostCurrent._w.setText((Object)(_sizex));
 //BA.debugLineNum = 1233;BA.debugLine="Mine.Text=KolMines";
mostCurrent._mine.setText((Object)(_kolmines));
 //BA.debugLineNum = 1234;BA.debugLine="WinOpen=True";
_winopen = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1235;BA.debugLine="End Sub";
return "";
}
public static String  _doskacell(int _i,int _j,int _cell) throws Exception{
 //BA.debugLineNum = 275;BA.debugLine="Sub DoskaCell(i As Int, j As Int, Cell As Int)";
 //BA.debugLineNum = 276;BA.debugLine="bmp.Initialize(File.DirAssets, \"bmp101XP.bmp\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bmp101XP.bmp");
 //BA.debugLineNum = 277;BA.debugLine="SrcRect.Initialize(0, Cell * bmp.Height/16, bmp.W";
mostCurrent._srcrect.Initialize((int) (0),(int) (_cell*mostCurrent._bmp.getHeight()/(double)16),mostCurrent._bmp.getWidth(),(int) (_cell*mostCurrent._bmp.getHeight()/(double)16+mostCurrent._bmp.getHeight()/(double)16));
 //BA.debugLineNum = 278;BA.debugLine="RectFon.Initialize(Dsk.x + (j - 1) * conCellWidth";
mostCurrent._rectfon.Initialize((int) (mostCurrent._dsk.x+(_j-1)*_concellwidth),(int) (mostCurrent._dsk.y+(_i-1)*_concellheight),(int) (mostCurrent._dsk.x+(_j-1)*_concellwidth+_concellwidth),(int) (mostCurrent._dsk.y+(_i-1)*_concellheight+_concellheight));
 //BA.debugLineNum = 279;BA.debugLine="cnvs.DrawBitmap(bmp, SrcRect, RectFon) 'draws hal";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(mostCurrent._srcrect.getObject()),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 280;BA.debugLine="End Sub";
return "";
}
public static boolean  _doskacoord(float _x,float _y) throws Exception{
 //BA.debugLineNum = 822;BA.debugLine="Sub DoskaCoord(x As Float, y As Float) As Boolean";
 //BA.debugLineNum = 823;BA.debugLine="If (x >= Dsk.x ) And (x <= Dsk.x + Dsk.width ) A";
if ((_x>=mostCurrent._dsk.x) && (_x<=mostCurrent._dsk.x+mostCurrent._dsk.width) && (_y>=mostCurrent._dsk.y) && (_y<=mostCurrent._dsk.y+mostCurrent._dsk.height)) { 
if (true) return anywheresoftware.b4a.keywords.Common.True;}
else {
if (true) return anywheresoftware.b4a.keywords.Common.False;};
 //BA.debugLineNum = 824;BA.debugLine="End Sub";
return false;
}
public static String  _doubleclick_tick() throws Exception{
 //BA.debugLineNum = 943;BA.debugLine="Sub DoubleClick_Tick";
 //BA.debugLineNum = 944;BA.debugLine="If Not(lose) And Not(Winn) Then FaceBtn(4)";
if (anywheresoftware.b4a.keywords.Common.Not(_lose) && anywheresoftware.b4a.keywords.Common.Not(_winn)) { 
_facebtn((int) (4));};
 //BA.debugLineNum = 945;BA.debugLine="If imv.top/(PZ(NumView).Zoom/100)<-28%x Then Min";
if (mostCurrent._imv.getTop()/(double)(mostCurrent._pz[_numview]._getzoom()/(double)100)<-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA)) { 
mostCurrent._mine2.setVisible(anywheresoftware.b4a.keywords.Common.True);}
else {
mostCurrent._mine2.setVisible(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 947;BA.debugLine="If WinOpen=True Then";
if (_winopen==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 948;BA.debugLine="cnvs4.Initialize(Reset)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._reset.getObject()));
 //BA.debugLineNum = 949;BA.debugLine="bmp.Initialize(File.DirAssets, \"Reset.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Reset.png");
 //BA.debugLineNum = 950;BA.debugLine="RectFon.Initialize(0,0,Reset.Width,Reset.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._reset.getWidth(),mostCurrent._reset.getHeight());
 //BA.debugLineNum = 951;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 952;BA.debugLine="Reset.Invalidate";
mostCurrent._reset.Invalidate();
 };
 //BA.debugLineNum = 954;BA.debugLine="End Sub";
return "";
}
public static String  _expert_click() throws Exception{
 //BA.debugLineNum = 1201;BA.debugLine="Sub Expert_Click";
 //BA.debugLineNum = 1202;BA.debugLine="SizeX=30";
_sizex = (int) (30);
 //BA.debugLineNum = 1203;BA.debugLine="SizeY=16";
_sizey = (int) (16);
 //BA.debugLineNum = 1204;BA.debugLine="KolMines=99";
_kolmines = (int) (99);
 //BA.debugLineNum = 1205;BA.debugLine="Level=2";
_level = (int) (2);
 //BA.debugLineNum = 1206;BA.debugLine="InitGame";
_initgame();
 //BA.debugLineNum = 1207;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1208;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1209;BA.debugLine="If Appodeal.isLoaded(Appodeal.INTERSTITIAL) Then";
if (mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL)) { 
 //BA.debugLineNum = 1210;BA.debugLine="Appodeal.show(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 //BA.debugLineNum = 1211;BA.debugLine="Appodeal.cache(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.cache(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 };
 //BA.debugLineNum = 1213;BA.debugLine="End Sub";
return "";
}
public static String  _facebtn(int _new_face) throws Exception{
 //BA.debugLineNum = 311;BA.debugLine="Sub FaceBtn(New_Face As Int)";
 //BA.debugLineNum = 312;BA.debugLine="Face.Face = New_Face";
mostCurrent._face.Face = _new_face;
 //BA.debugLineNum = 314;BA.debugLine="bmp.Initialize(File.DirAssets, \"bmp103.bmp\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bmp103.bmp");
 //BA.debugLineNum = 315;BA.debugLine="SrcRect.Initialize(0, Face.Face * bmp.Height/5, b";
mostCurrent._srcrect.Initialize((int) (0),(int) (mostCurrent._face.Face*mostCurrent._bmp.getHeight()/(double)5),mostCurrent._bmp.getWidth(),(int) (mostCurrent._face.Face*mostCurrent._bmp.getHeight()/(double)5+mostCurrent._bmp.getHeight()/(double)5));
 //BA.debugLineNum = 316;BA.debugLine="RectFon.Initialize(Face.x,Face.y ,Face.x +16%x ,F";
mostCurrent._rectfon.Initialize((int) (mostCurrent._face.x),(int) (mostCurrent._face.y),(int) (mostCurrent._face.x+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (16),mostCurrent.activityBA)),(int) (mostCurrent._face.y+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (16),mostCurrent.activityBA)));
 //BA.debugLineNum = 317;BA.debugLine="cnvs.DrawBitmap(bmp, SrcRect, RectFon) 'draws hal";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(mostCurrent._srcrect.getObject()),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 318;BA.debugLine="imv.Invalidate";
mostCurrent._imv.Invalidate();
 //BA.debugLineNum = 319;BA.debugLine="bmp.Initialize(File.DirAssets, \"bmp103.bmp\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bmp103.bmp");
 //BA.debugLineNum = 320;BA.debugLine="SrcRect.Initialize(0, Face.Face * bmp.Height/5, b";
mostCurrent._srcrect.Initialize((int) (0),(int) (mostCurrent._face.Face*mostCurrent._bmp.getHeight()/(double)5),mostCurrent._bmp.getWidth(),(int) (mostCurrent._face.Face*mostCurrent._bmp.getHeight()/(double)5+mostCurrent._bmp.getHeight()/(double)5));
 //BA.debugLineNum = 321;BA.debugLine="RectFon.Initialize(Face.x,Face.y-27%x ,Face.x +16";
mostCurrent._rectfon.Initialize((int) (mostCurrent._face.x),(int) (mostCurrent._face.y-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA)),(int) (mostCurrent._face.x+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (16),mostCurrent.activityBA)),(int) (mostCurrent._face.y-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA)+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (16),mostCurrent.activityBA)));
 //BA.debugLineNum = 322;BA.debugLine="cnvs2.DrawBitmap(bmp, SrcRect, RectFon) 'draws ha";
mostCurrent._cnvs2.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(mostCurrent._srcrect.getObject()),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 323;BA.debugLine="Mine2.Invalidate";
mostCurrent._mine2.Invalidate();
 //BA.debugLineNum = 325;BA.debugLine="End Sub";
return "";
}
public static boolean  _facecoord(float _x,float _y) throws Exception{
 //BA.debugLineNum = 826;BA.debugLine="Sub FaceCoord(x As Float, y As Float) As Boolean";
 //BA.debugLineNum = 827;BA.debugLine="If ((x >= Face.x) And (x<= Face.x + conFaceWidth";
if (((_x>=mostCurrent._face.x) && (_x<=mostCurrent._face.x+_confacewidth) && (_y>=mostCurrent._face.y) && (_y<=mostCurrent._face.y+_confacewidth))) { 
if (true) return anywheresoftware.b4a.keywords.Common.True;}
else {
if (true) return anywheresoftware.b4a.keywords.Common.False;};
 //BA.debugLineNum = 828;BA.debugLine="End Sub";
return false;
}
public static String  _facetimer_tick() throws Exception{
 //BA.debugLineNum = 938;BA.debugLine="Sub FaceTimer_Tick";
 //BA.debugLineNum = 939;BA.debugLine="If Not(lose) And Not(Winn) Then FaceBtn(OldFace)";
if (anywheresoftware.b4a.keywords.Common.Not(_lose) && anywheresoftware.b4a.keywords.Common.Not(_winn)) { 
_facebtn(_oldface);};
 //BA.debugLineNum = 940;BA.debugLine="FaceTimer.Enabled=False";
mostCurrent._facetimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 941;BA.debugLine="End Sub";
return "";
}
public static String  _find_clean_place() throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 1459;BA.debugLine="Sub Find_clean_place";
 //BA.debugLineNum = 1460;BA.debugLine="Dim i As Int, j As Int";
_i = 0;
_j = 0;
 //BA.debugLineNum = 1461;BA.debugLine="For i = 1 To SizeY";
{
final int step2 = 1;
final int limit2 = _sizey;
for (_i = (int) (1) ; (step2 > 0 && _i <= limit2) || (step2 < 0 && _i >= limit2); _i = ((int)(0 + _i + step2)) ) {
 //BA.debugLineNum = 1462;BA.debugLine="For j = 1 To SizeX";
{
final int step3 = 1;
final int limit3 = _sizex;
for (_j = (int) (1) ; (step3 > 0 && _j <= limit3) || (step3 < 0 && _j >= limit3); _j = ((int)(0 + _j + step3)) ) {
 //BA.debugLineNum = 1463;BA.debugLine="If Doska(i, j) <> -1 Then";
if (_doska[_i][_j]!=-1) { 
 //BA.debugLineNum = 1464;BA.debugLine="Doska(i, j) = -1";
_doska[_i][_j] = (int) (-1);
 //BA.debugLineNum = 1465;BA.debugLine="Return";
if (true) return "";
 };
 }
};
 }
};
 //BA.debugLineNum = 1469;BA.debugLine="End Sub";
return "";
}
public static String  _flagt_tick() throws Exception{
 //BA.debugLineNum = 955;BA.debugLine="Sub FlagT_Tick";
 //BA.debugLineNum = 956;BA.debugLine="Flag.Visible=True";
mostCurrent._flag.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 957;BA.debugLine="Flag.Width=Flag.Width-1%x";
mostCurrent._flag.setWidth((int) (mostCurrent._flag.getWidth()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (1),mostCurrent.activityBA)));
 //BA.debugLineNum = 958;BA.debugLine="Flag.Height=Flag.Height-1%x";
mostCurrent._flag.setHeight((int) (mostCurrent._flag.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (1),mostCurrent.activityBA)));
 //BA.debugLineNum = 959;BA.debugLine="Flag.Top=ylong-Flag.Height/2";
mostCurrent._flag.setTop((int) (_ylong-mostCurrent._flag.getHeight()/(double)2));
 //BA.debugLineNum = 960;BA.debugLine="Flag.left=xlong-Flag.Width/2";
mostCurrent._flag.setLeft((int) (_xlong-mostCurrent._flag.getWidth()/(double)2));
 //BA.debugLineNum = 961;BA.debugLine="If Flag.Width<=0 Then";
if (mostCurrent._flag.getWidth()<=0) { 
 //BA.debugLineNum = 962;BA.debugLine="FlagT.Enabled=False";
mostCurrent._flagt.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 963;BA.debugLine="Flag.Visible=False";
mostCurrent._flag.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 964;BA.debugLine="Flag.Width=30%x";
mostCurrent._flag.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 965;BA.debugLine="Flag.Height=30%x";
mostCurrent._flag.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 };
 //BA.debugLineNum = 967;BA.debugLine="End Sub";
return "";
}
public static String  _gameb() throws Exception{
 //BA.debugLineNum = 982;BA.debugLine="Sub Gameb";
 //BA.debugLineNum = 983;BA.debugLine="If game=False And Hide=True Then game=False Else g";
if (_game==anywheresoftware.b4a.keywords.Common.False && _hide==anywheresoftware.b4a.keywords.Common.True) { 
_game = anywheresoftware.b4a.keywords.Common.False;}
else {
_game = anywheresoftware.b4a.keywords.Common.True;};
 //BA.debugLineNum = 984;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 985;BA.debugLine="bmp.Initialize(File.DirAssets, \"gamebp.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gamebp.png");
 //BA.debugLineNum = 986;BA.debugLine="RectFon.Initialize(5%x,18%x,23%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (23),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 987;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 988;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 989;BA.debugLine="End Sub";
return "";
}
public static String  _generatedoska() throws Exception{
int _i = 0;
int _j = 0;
int _k = 0;
int _m = 0;
int _ubd = 0;
int[] _availx = null;
int[] _availy = null;
 //BA.debugLineNum = 326;BA.debugLine="Sub GenerateDoska()";
 //BA.debugLineNum = 327;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 328;BA.debugLine="Dim j As Int";
_j = 0;
 //BA.debugLineNum = 329;BA.debugLine="Dim k As Int";
_k = 0;
 //BA.debugLineNum = 330;BA.debugLine="Dim M As Int";
_m = 0;
 //BA.debugLineNum = 331;BA.debugLine="Dim ubd As Int";
_ubd = 0;
 //BA.debugLineNum = 332;BA.debugLine="Dim AvailX() As Int";
_availx = new int[(int) (0)];
;
 //BA.debugLineNum = 333;BA.debugLine="Dim AvailY() As Int";
_availy = new int[(int) (0)];
;
 //BA.debugLineNum = 335;BA.debugLine="Dim AvailX(SizeX * SizeY+2), AvailY(SizeX * Size";
_availx = new int[(int) (_sizex*_sizey+2)];
;
_availy = new int[(int) (_sizex*_sizey+2)];
;
 //BA.debugLineNum = 336;BA.debugLine="For i = 1 To SizeY";
{
final int step9 = 1;
final int limit9 = _sizey;
for (_i = (int) (1) ; (step9 > 0 && _i <= limit9) || (step9 < 0 && _i >= limit9); _i = ((int)(0 + _i + step9)) ) {
 //BA.debugLineNum = 337;BA.debugLine="For j = 1 To SizeX";
{
final int step10 = 1;
final int limit10 = _sizex;
for (_j = (int) (1) ; (step10 > 0 && _j <= limit10) || (step10 < 0 && _j >= limit10); _j = ((int)(0 + _j + step10)) ) {
 //BA.debugLineNum = 338;BA.debugLine="AvailX((i - 1) * SizeX + j) = j";
_availx[(int) ((_i-1)*_sizex+_j)] = _j;
 //BA.debugLineNum = 339;BA.debugLine="AvailY((i - 1) * SizeX + j) = i";
_availy[(int) ((_i-1)*_sizex+_j)] = _i;
 }
};
 }
};
 //BA.debugLineNum = 343;BA.debugLine="ubd = AvailX.Length-2";
_ubd = (int) (_availx.length-2);
 //BA.debugLineNum = 344;BA.debugLine="For M = 1 To KolMines";
{
final int step16 = 1;
final int limit16 = _kolmines;
for (_m = (int) (1) ; (step16 > 0 && _m <= limit16) || (step16 < 0 && _m >= limit16); _m = ((int)(0 + _m + step16)) ) {
 //BA.debugLineNum = 345;BA.debugLine="k = Rnd(1,ubd)";
_k = anywheresoftware.b4a.keywords.Common.Rnd((int) (1),_ubd);
 //BA.debugLineNum = 346;BA.debugLine="Doska(AvailY(k), AvailX(k)) = -1";
_doska[_availy[_k]][_availx[_k]] = (int) (-1);
 //BA.debugLineNum = 347;BA.debugLine="For i = AvailY(k) - 1 To AvailY(k) + 1";
{
final int step19 = 1;
final int limit19 = (int) (_availy[_k]+1);
for (_i = (int) (_availy[_k]-1) ; (step19 > 0 && _i <= limit19) || (step19 < 0 && _i >= limit19); _i = ((int)(0 + _i + step19)) ) {
 //BA.debugLineNum = 348;BA.debugLine="For j = AvailX(k) - 1 To AvailX(k) + 1";
{
final int step20 = 1;
final int limit20 = (int) (_availx[_k]+1);
for (_j = (int) (_availx[_k]-1) ; (step20 > 0 && _j <= limit20) || (step20 < 0 && _j >= limit20); _j = ((int)(0 + _j + step20)) ) {
 //BA.debugLineNum = 349;BA.debugLine="If (i <> AvailY(k) Or j <> AvailX(k)) Then";
if ((_i!=_availy[_k] || _j!=_availx[_k])) { 
 //BA.debugLineNum = 350;BA.debugLine="If Doska(i, j) >= 0 Then Doska(i, j) = D";
if (_doska[_i][_j]>=0) { 
_doska[_i][_j] = (int) (_doska[_i][_j]+1);};
 };
 }
};
 }
};
 //BA.debugLineNum = 354;BA.debugLine="AvailX(k) = AvailX(ubd)";
_availx[_k] = _availx[_ubd];
 //BA.debugLineNum = 355;BA.debugLine="AvailY(k) = AvailY(ubd)";
_availy[_k] = _availy[_ubd];
 //BA.debugLineNum = 356;BA.debugLine="ubd = ubd - 1";
_ubd = (int) (_ubd-1);
 }
};
 //BA.debugLineNum = 359;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 46;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 47;BA.debugLine="Dim Appodeal As AppodealB4A";
mostCurrent._appodeal = new com.appodeal.basic4android.AppodealB4A();
 //BA.debugLineNum = 48;BA.debugLine="Dim appKey As String = \"ade116f04817d6d7cac20cdbee";
mostCurrent._appkey = "ade116f04817d6d7cac20cdbeea6cf3e61ff609da37b4094";
 //BA.debugLineNum = 49;BA.debugLine="Dim PZ(1) As PinchZoomAndMove";
mostCurrent._pz = new dominex.example.pinchzoomandmove[(int) (1)];
{
int d0 = mostCurrent._pz.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._pz[i0] = new dominex.example.pinchzoomandmove();
}
}
;
 //BA.debugLineNum = 50;BA.debugLine="Dim NumView As Int";
_numview = 0;
 //BA.debugLineNum = 51;BA.debugLine="Dim game As Boolean";
_game = false;
 //BA.debugLineNum = 52;BA.debugLine="Dim DoubleClick As Timer";
mostCurrent._doubleclick = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 53;BA.debugLine="Dim sf As StringFunctions";
mostCurrent._sf = new adr.stringfunctions.stringfunctions();
 //BA.debugLineNum = 54;BA.debugLine="Dim MP As MediaPlayer";
mostCurrent._mp = new anywheresoftware.b4a.objects.MediaPlayerWrapper();
 //BA.debugLineNum = 55;BA.debugLine="sf.Initialize";
mostCurrent._sf._initialize(processBA);
 //BA.debugLineNum = 56;BA.debugLine="Dim conCellHeight As Int";
_concellheight = 0;
 //BA.debugLineNum = 57;BA.debugLine="Dim conCellWidth As Int";
_concellwidth = 0;
 //BA.debugLineNum = 58;BA.debugLine="Dim conZifraWidth As Int= 9%x";
_conzifrawidth = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (9),mostCurrent.activityBA);
 //BA.debugLineNum = 59;BA.debugLine="Dim conZifraHeight As Int= 15%x";
_conzifraheight = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA);
 //BA.debugLineNum = 60;BA.debugLine="Dim conFaceHeight As Int= 16%x";
_confaceheight = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (16),mostCurrent.activityBA);
 //BA.debugLineNum = 61;BA.debugLine="Dim conFaceWidth As Int= 16%x";
_confacewidth = anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (16),mostCurrent.activityBA);
 //BA.debugLineNum = 62;BA.debugLine="Dim Doska(,) As Int";
_doska = new int[(int) (0)][];
{
int d0 = _doska.length;
int d1 = (int) (0);
for (int i0 = 0;i0 < d0;i0++) {
_doska[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 63;BA.debugLine="Dim Doska1(,) As Int";
_doska1 = new int[(int) (0)][];
{
int d0 = _doska1.length;
int d1 = (int) (0);
for (int i0 = 0;i0 < d0;i0++) {
_doska1[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 64;BA.debugLine="Dim KolMines As Int=10";
_kolmines = (int) (10);
 //BA.debugLineNum = 65;BA.debugLine="Dim SizeX As Int=9";
_sizex = (int) (9);
 //BA.debugLineNum = 66;BA.debugLine="Dim SizeY As Int=9";
_sizey = (int) (9);
 //BA.debugLineNum = 67;BA.debugLine="Dim Timer1 As Timer";
mostCurrent._timer1 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 68;BA.debugLine="Dim FlagT As Timer";
mostCurrent._flagt = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 69;BA.debugLine="Dim VoprT As Timer";
mostCurrent._voprt = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 70;BA.debugLine="Dim CloseCell As Int";
_closecell = 0;
 //BA.debugLineNum = 71;BA.debugLine="Dim GameOver As Boolean";
_gameover = false;
 //BA.debugLineNum = 72;BA.debugLine="Type TIndikator(x As Long, y As Long, value As Int";
;
 //BA.debugLineNum = 73;BA.debugLine="Type TFace(x As Long, y As Long, Face As Int)";
;
 //BA.debugLineNum = 74;BA.debugLine="Type TDoska(x As Long, y As Long, width As Long, h";
;
 //BA.debugLineNum = 75;BA.debugLine="Dim ind(2) As TIndikator";
mostCurrent._ind = new com.serogen.minesweeper.main._tindikator[(int) (2)];
{
int d0 = mostCurrent._ind.length;
for (int i0 = 0;i0 < d0;i0++) {
mostCurrent._ind[i0] = new com.serogen.minesweeper.main._tindikator();
}
}
;
 //BA.debugLineNum = 76;BA.debugLine="Dim Face As TFace";
mostCurrent._face = new com.serogen.minesweeper.main._tface();
 //BA.debugLineNum = 77;BA.debugLine="Dim Dsk As TDoska";
mostCurrent._dsk = new com.serogen.minesweeper.main._tdoska();
 //BA.debugLineNum = 78;BA.debugLine="Dim OldI As Int, OldJ As Int, OldX As Long, OldY A";
_oldi = 0;
_oldj = 0;
_oldx = 0L;
_oldy = 0L;
_oldface = 0;
 //BA.debugLineNum = 79;BA.debugLine="Dim xlong As Float,ylong As Float";
_xlong = 0f;
_ylong = 0f;
 //BA.debugLineNum = 80;BA.debugLine="Dim mmm As Int=0";
_mmm = (int) (0);
 //BA.debugLineNum = 81;BA.debugLine="Dim nnn As Int=0";
_nnn = (int) (0);
 //BA.debugLineNum = 82;BA.debugLine="Dim Time0 As Long";
_time0 = 0L;
 //BA.debugLineNum = 83;BA.debugLine="Dim LongClickTime =500 As Long";
_longclicktime = (long) (500);
 //BA.debugLineNum = 84;BA.debugLine="Dim x0,y0 As Float";
_x0 = 0f;
_y0 = 0f;
 //BA.debugLineNum = 85;BA.debugLine="Dim SrcRect As Rect";
mostCurrent._srcrect = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 86;BA.debugLine="Dim bmp As Bitmap";
mostCurrent._bmp = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.BitmapWrapper();
 //BA.debugLineNum = 87;BA.debugLine="Dim cnvs As Canvas";
mostCurrent._cnvs = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 88;BA.debugLine="Dim cnvs2 As Canvas";
mostCurrent._cnvs2 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 89;BA.debugLine="Dim cnvs3 As Canvas";
mostCurrent._cnvs3 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 90;BA.debugLine="Dim cnvs4 As Canvas";
mostCurrent._cnvs4 = new anywheresoftware.b4a.objects.drawable.CanvasWrapper();
 //BA.debugLineNum = 91;BA.debugLine="Dim RectFon As Rect";
mostCurrent._rectfon = new anywheresoftware.b4a.objects.drawable.CanvasWrapper.RectWrapper();
 //BA.debugLineNum = 92;BA.debugLine="Dim Flag As ImageView";
mostCurrent._flag = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 93;BA.debugLine="Dim Vopr As ImageView";
mostCurrent._vopr = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 94;BA.debugLine="Dim LongC As Boolean=False";
_longc = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 95;BA.debugLine="Private New As Button";
mostCurrent._new = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 96;BA.debugLine="Private Beginner As Button";
mostCurrent._beginner = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 97;BA.debugLine="Private Medium As Button";
mostCurrent._medium = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 98;BA.debugLine="Private Expert As Button";
mostCurrent._expert = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 99;BA.debugLine="Private Sound As Button";
mostCurrent._sound = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 100;BA.debugLine="Private Leaderboard As Button";
mostCurrent._leaderboard = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 101;BA.debugLine="Dim MenuAnim As Timer";
mostCurrent._menuanim = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 102;BA.debugLine="Dim Hide As Boolean=False";
_hide = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 103;BA.debugLine="Dim imv As ImageView";
mostCurrent._imv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 104;BA.debugLine="Dim pv As PhoneVibrate";
mostCurrent._pv = new anywheresoftware.b4a.phone.Phone.PhoneVibrate();
 //BA.debugLineNum = 105;BA.debugLine="Private Galka As ImageView";
mostCurrent._galka = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 106;BA.debugLine="Dim Level As Int";
_level = 0;
 //BA.debugLineNum = 107;BA.debugLine="Private Galka2 As ImageView";
mostCurrent._galka2 = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 108;BA.debugLine="Dim SoundOn As Boolean=True";
_soundon = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 109;BA.debugLine="Dim p As Phone";
mostCurrent._p = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 110;BA.debugLine="Dim Winn As Boolean";
_winn = false;
 //BA.debugLineNum = 111;BA.debugLine="Dim lose As Boolean";
_lose = false;
 //BA.debugLineNum = 112;BA.debugLine="Private Mine2 As Panel";
mostCurrent._mine2 = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 113;BA.debugLine="Private Custom As Button";
mostCurrent._custom = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 114;BA.debugLine="Private CustomPanel As Panel";
mostCurrent._custompanel = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 115;BA.debugLine="Private OK As Button";
mostCurrent._ok = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 116;BA.debugLine="Private Cancel As Button";
mostCurrent._cancel = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 117;BA.debugLine="Private H As EditText";
mostCurrent._h = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 118;BA.debugLine="Private W As EditText";
mostCurrent._w = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 119;BA.debugLine="Private Mine As EditText";
mostCurrent._mine = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 120;BA.debugLine="Public WinOpen As Boolean";
_winopen = false;
 //BA.debugLineNum = 121;BA.debugLine="Dim FaceTimer As Timer";
mostCurrent._facetimer = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 122;BA.debugLine="Type B(score As Int,name As String)";
;
 //BA.debugLineNum = 123;BA.debugLine="Dim beg As B";
mostCurrent._beg = new com.serogen.minesweeper.main._b();
 //BA.debugLineNum = 124;BA.debugLine="Dim emm As B";
mostCurrent._emm = new com.serogen.minesweeper.main._b();
 //BA.debugLineNum = 125;BA.debugLine="Dim exp As B";
mostCurrent._exp = new com.serogen.minesweeper.main._b();
 //BA.debugLineNum = 126;BA.debugLine="Private dlgrec As Panel";
mostCurrent._dlgrec = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 127;BA.debugLine="Private bn As Label";
mostCurrent._bn = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 128;BA.debugLine="Private In As Label";
mostCurrent._in = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 129;BA.debugLine="Private en As Label";
mostCurrent._en = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 130;BA.debugLine="Private bs As Label";
mostCurrent._bs = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 131;BA.debugLine="Private iss As Label";
mostCurrent._iss = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 132;BA.debugLine="Private es As Label";
mostCurrent._es = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 133;BA.debugLine="Private OK2 As Button";
mostCurrent._ok2 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 134;BA.debugLine="Private Reset As Button";
mostCurrent._reset = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 135;BA.debugLine="Private Rules As Button";
mostCurrent._rules = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 136;BA.debugLine="Private About As Button";
mostCurrent._about = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 137;BA.debugLine="Private EditText1 As EditText";
mostCurrent._edittext1 = new anywheresoftware.b4a.objects.EditTextWrapper();
 //BA.debugLineNum = 138;BA.debugLine="Private BestPanel As Panel";
mostCurrent._bestpanel = new anywheresoftware.b4a.objects.PanelWrapper();
 //BA.debugLineNum = 139;BA.debugLine="Private OK3 As Button";
mostCurrent._ok3 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 140;BA.debugLine="Dim lblText As Label";
mostCurrent._lbltext = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 141;BA.debugLine="Dim first As Boolean";
_first = false;
 //BA.debugLineNum = 142;BA.debugLine="End Sub";
return "";
}
public static String  _helpb() throws Exception{
 //BA.debugLineNum = 1367;BA.debugLine="Sub Helpb";
 //BA.debugLineNum = 1368;BA.debugLine="If game=True And Hide=True Then game=True Else gam";
if (_game==anywheresoftware.b4a.keywords.Common.True && _hide==anywheresoftware.b4a.keywords.Common.True) { 
_game = anywheresoftware.b4a.keywords.Common.True;}
else {
_game = anywheresoftware.b4a.keywords.Common.False;};
 //BA.debugLineNum = 1369;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1370;BA.debugLine="bmp.Initialize(File.DirAssets, \"helpbp.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"helpbp.png");
 //BA.debugLineNum = 1371;BA.debugLine="RectFon.Initialize(25%x,18%x,43%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (43),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1372;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1373;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1374;BA.debugLine="End Sub";
return "";
}
public static String  _indikator(int _id,int _new_value) throws Exception{
String _str_value = "";
int _i = 0;
 //BA.debugLineNum = 282;BA.debugLine="Sub Indikator(id As Int, new_value As Int)";
 //BA.debugLineNum = 283;BA.debugLine="Dim str_value As String";
_str_value = "";
 //BA.debugLineNum = 284;BA.debugLine="Dim i As Int";
_i = 0;
 //BA.debugLineNum = 285;BA.debugLine="If new_value < 1000 Then";
if (_new_value<1000) { 
 //BA.debugLineNum = 286;BA.debugLine="ind(id).value = new_value";
mostCurrent._ind[_id].value = _new_value;
 //BA.debugLineNum = 287;BA.debugLine="str_value = ind(id).value";
_str_value = BA.NumberToString(mostCurrent._ind[_id].value);
 //BA.debugLineNum = 288;BA.debugLine="str_value.Trim";
_str_value.trim();
 //BA.debugLineNum = 289;BA.debugLine="For i=0 To (3 - str_value.Length)-1";
{
final int step7 = 1;
final int limit7 = (int) ((3-_str_value.length())-1);
for (_i = (int) (0) ; (step7 > 0 && _i <= limit7) || (step7 < 0 && _i >= limit7); _i = ((int)(0 + _i + step7)) ) {
 //BA.debugLineNum = 290;BA.debugLine="str_value = \"0\" &str_value";
_str_value = "0"+_str_value;
 }
};
 //BA.debugLineNum = 292;BA.debugLine="For i = 1 To 3";
{
final int step10 = 1;
final int limit10 = (int) (3);
for (_i = (int) (1) ; (step10 > 0 && _i <= limit10) || (step10 < 0 && _i >= limit10); _i = ((int)(0 + _i + step10)) ) {
 //BA.debugLineNum = 295;BA.debugLine="bmp.Initialize(File.DirAssets, \"bmp102.bmp\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bmp102.bmp");
 //BA.debugLineNum = 296;BA.debugLine="SrcRect.Initialize(0,  (11 - sf.Mid(str_value,i,1";
mostCurrent._srcrect.Initialize((int) (0),(int) ((11-(double)(Double.parseDouble(mostCurrent._sf._vvvv5(_str_value,_i,(int) (1)))))*mostCurrent._bmp.getHeight()/(double)12),mostCurrent._bmp.getWidth(),(int) ((11-(double)(Double.parseDouble(mostCurrent._sf._vvvv5(_str_value,_i,(int) (1)))))*mostCurrent._bmp.getHeight()/(double)12+mostCurrent._bmp.getHeight()/(double)12));
 //BA.debugLineNum = 297;BA.debugLine="RectFon.Initialize(ind(id).x + conZifraWidth * (i";
mostCurrent._rectfon.Initialize((int) (mostCurrent._ind[_id].x+_conzifrawidth*(_i-1)),(int) (mostCurrent._ind[_id].y),(int) (mostCurrent._ind[_id].x+_conzifrawidth*(_i-1)+_conzifrawidth),(int) (mostCurrent._ind[_id].y+_conzifraheight));
 //BA.debugLineNum = 298;BA.debugLine="cnvs.DrawBitmap(bmp, SrcRect, RectFon) 'draws hal";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(mostCurrent._srcrect.getObject()),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 299;BA.debugLine="imv.Invalidate";
mostCurrent._imv.Invalidate();
 //BA.debugLineNum = 302;BA.debugLine="bmp.Initialize(File.DirAssets, \"bmp102.bmp\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"bmp102.bmp");
 //BA.debugLineNum = 303;BA.debugLine="SrcRect.Initialize(0,  (11 - sf.Mid(str_value,i,1";
mostCurrent._srcrect.Initialize((int) (0),(int) ((11-(double)(Double.parseDouble(mostCurrent._sf._vvvv5(_str_value,_i,(int) (1)))))*mostCurrent._bmp.getHeight()/(double)12),mostCurrent._bmp.getWidth(),(int) ((11-(double)(Double.parseDouble(mostCurrent._sf._vvvv5(_str_value,_i,(int) (1)))))*mostCurrent._bmp.getHeight()/(double)12+mostCurrent._bmp.getHeight()/(double)12));
 //BA.debugLineNum = 304;BA.debugLine="RectFon.Initialize(ind(id).x + conZifraWidth * (i";
mostCurrent._rectfon.Initialize((int) (mostCurrent._ind[_id].x+_conzifrawidth*(_i-1)),(int) (mostCurrent._ind[_id].y-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA)),(int) (mostCurrent._ind[_id].x+_conzifrawidth*(_i-1)+_conzifrawidth),(int) (mostCurrent._ind[_id].y-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA)+_conzifraheight));
 //BA.debugLineNum = 305;BA.debugLine="cnvs2.DrawBitmap(bmp, SrcRect, RectFon) 'draws ha";
mostCurrent._cnvs2.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(mostCurrent._srcrect.getObject()),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 306;BA.debugLine="Mine2.Invalidate";
mostCurrent._mine2.Invalidate();
 }
};
 };
 //BA.debugLineNum = 309;BA.debugLine="End Sub";
return "";
}
public static String  _initgame() throws Exception{
int _i = 0;
int _j = 0;
String _s = "";
 //BA.debugLineNum = 205;BA.debugLine="Sub InitGame";
 //BA.debugLineNum = 206;BA.debugLine="Dim i As Int, j As Int";
_i = 0;
_j = 0;
 //BA.debugLineNum = 207;BA.debugLine="Dim s As String";
_s = "";
 //BA.debugLineNum = 208;BA.debugLine="bmp.Initialize(File.DirAssets, \"Mine.bmp\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Mine.bmp");
 //BA.debugLineNum = 209;BA.debugLine="RectFon.Initialize(0,0,Activity.Width, 1.5*Activit";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._activity.getWidth(),(int) (1.5*mostCurrent._activity.getWidth()));
 //BA.debugLineNum = 210;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 211;BA.debugLine="bmp.Initialize(File.DirAssets, \"gameb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gameb.png");
 //BA.debugLineNum = 212;BA.debugLine="RectFon.Initialize(5%x,18%x,23%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (23),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 213;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 214;BA.debugLine="bmp.Initialize(File.DirAssets, \"helpb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"helpb.png");
 //BA.debugLineNum = 215;BA.debugLine="RectFon.Initialize(25%x,18%x,43%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (43),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 216;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 217;BA.debugLine="bmp.Initialize(File.DirAssets, \"exitbutton.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"exitbutton.png");
 //BA.debugLineNum = 218;BA.debugLine="RectFon.Initialize(87%x,4%x,97%x, 14%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (87),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (97),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 219;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 220;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 221;BA.debugLine="If Timer1.Enabled Then Timer1.Enabled = False";
if (mostCurrent._timer1.getEnabled()) { 
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 222;BA.debugLine="Winn=False";
_winn = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 223;BA.debugLine="lose=False";
_lose = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 224;BA.debugLine="ind(1).value = 1";
mostCurrent._ind[(int) (1)].value = (int) (1);
 //BA.debugLineNum = 225;BA.debugLine="CloseCell = SizeX * SizeY";
_closecell = (int) (_sizex*_sizey);
 //BA.debugLineNum = 226;BA.debugLine="GameOver = False";
_gameover = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 227;BA.debugLine="ind(0).x = 12%x";
mostCurrent._ind[(int) (0)].x = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (12),mostCurrent.activityBA));
 //BA.debugLineNum = 228;BA.debugLine="ind(0).y = 37%x";
mostCurrent._ind[(int) (0)].y = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (37),mostCurrent.activityBA));
 //BA.debugLineNum = 229;BA.debugLine="ind(0).value = 0";
mostCurrent._ind[(int) (0)].value = (int) (0);
 //BA.debugLineNum = 230;BA.debugLine="ind(1).x = 62%x";
mostCurrent._ind[(int) (1)].x = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (62),mostCurrent.activityBA));
 //BA.debugLineNum = 231;BA.debugLine="ind(1).y = 37%x";
mostCurrent._ind[(int) (1)].y = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (37),mostCurrent.activityBA));
 //BA.debugLineNum = 232;BA.debugLine="ind(1).value = 0";
mostCurrent._ind[(int) (1)].value = (int) (0);
 //BA.debugLineNum = 233;BA.debugLine="Face.x = 42%x";
mostCurrent._face.x = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (42),mostCurrent.activityBA));
 //BA.debugLineNum = 234;BA.debugLine="Face.y = 36%x";
mostCurrent._face.y = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (36),mostCurrent.activityBA));
 //BA.debugLineNum = 235;BA.debugLine="Face.Face = 4";
mostCurrent._face.Face = (int) (4);
 //BA.debugLineNum = 236;BA.debugLine="If SizeX=SizeY Then";
if (_sizex==_sizey) { 
 //BA.debugLineNum = 237;BA.debugLine="conCellHeight=(86%x/SizeY)";
_concellheight = (int) ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (86),mostCurrent.activityBA)/(double)_sizey));
 //BA.debugLineNum = 238;BA.debugLine="conCellWidth=(86%x/SizeX)";
_concellwidth = (int) ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (86),mostCurrent.activityBA)/(double)_sizex));
 //BA.debugLineNum = 239;BA.debugLine="Dsk.x = 9%x";
mostCurrent._dsk.x = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (9),mostCurrent.activityBA));
 //BA.debugLineNum = 240;BA.debugLine="Dsk.y = 60%x";
mostCurrent._dsk.y = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA));
 };
 //BA.debugLineNum = 242;BA.debugLine="If SizeX>SizeY Then";
if (_sizex>_sizey) { 
 //BA.debugLineNum = 243;BA.debugLine="conCellHeight=(86%x/SizeX)";
_concellheight = (int) ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (86),mostCurrent.activityBA)/(double)_sizex));
 //BA.debugLineNum = 244;BA.debugLine="conCellWidth=(86%x/SizeX)";
_concellwidth = (int) ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (86),mostCurrent.activityBA)/(double)_sizex));
 //BA.debugLineNum = 245;BA.debugLine="Dsk.x = 9%x";
mostCurrent._dsk.x = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (9),mostCurrent.activityBA));
 //BA.debugLineNum = 246;BA.debugLine="Dsk.y = 60%x+(SizeX-SizeY)/2*conCellHeight";
mostCurrent._dsk.y = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA)+(_sizex-_sizey)/(double)2*_concellheight);
 };
 //BA.debugLineNum = 248;BA.debugLine="If SizeX<SizeY Then";
if (_sizex<_sizey) { 
 //BA.debugLineNum = 249;BA.debugLine="conCellHeight=(86%x/SizeY)";
_concellheight = (int) ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (86),mostCurrent.activityBA)/(double)_sizey));
 //BA.debugLineNum = 250;BA.debugLine="conCellWidth=(86%x/SizeY)";
_concellwidth = (int) ((anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (86),mostCurrent.activityBA)/(double)_sizey));
 //BA.debugLineNum = 251;BA.debugLine="Dsk.x = 9%x+(SizeY-SizeX)/2*conCellWidth";
mostCurrent._dsk.x = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (9),mostCurrent.activityBA)+(_sizey-_sizex)/(double)2*_concellwidth);
 //BA.debugLineNum = 252;BA.debugLine="Dsk.y = 60%x";
mostCurrent._dsk.y = (long) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA));
 };
 //BA.debugLineNum = 254;BA.debugLine="Dsk.width = SizeX * conCellWidth";
mostCurrent._dsk.width = (long) (_sizex*_concellwidth);
 //BA.debugLineNum = 255;BA.debugLine="Dsk.height = SizeY * conCellHeight";
mostCurrent._dsk.height = (long) (_sizey*_concellheight);
 //BA.debugLineNum = 256;BA.debugLine="Dim Doska(SizeY + 2, SizeX + 2) As Int";
_doska = new int[(int) (_sizey+2)][];
{
int d0 = _doska.length;
int d1 = (int) (_sizex+2);
for (int i0 = 0;i0 < d0;i0++) {
_doska[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 257;BA.debugLine="Dim Doska1(SizeY + 2, SizeX + 2)As Int";
_doska1 = new int[(int) (_sizey+2)][];
{
int d0 = _doska1.length;
int d1 = (int) (_sizex+2);
for (int i0 = 0;i0 < d0;i0++) {
_doska1[i0] = new int[d1];
}
}
;
 //BA.debugLineNum = 259;BA.debugLine="GenerateDoska";
_generatedoska();
 //BA.debugLineNum = 261;BA.debugLine="FaceBtn(0)";
_facebtn((int) (0));
 //BA.debugLineNum = 263;BA.debugLine="Indikator(0, KolMines)";
_indikator((int) (0),_kolmines);
 //BA.debugLineNum = 264;BA.debugLine="Indikator (1, 0)";
_indikator((int) (1),(int) (0));
 //BA.debugLineNum = 265;BA.debugLine="For i = 1 To SizeY";
{
final int step57 = 1;
final int limit57 = _sizey;
for (_i = (int) (1) ; (step57 > 0 && _i <= limit57) || (step57 < 0 && _i >= limit57); _i = ((int)(0 + _i + step57)) ) {
 //BA.debugLineNum = 266;BA.debugLine="For j = 1 To SizeX";
{
final int step58 = 1;
final int limit58 = _sizex;
for (_j = (int) (1) ; (step58 > 0 && _j <= limit58) || (step58 < 0 && _j >= limit58); _j = ((int)(0 + _j + step58)) ) {
 //BA.debugLineNum = 267;BA.debugLine="DoskaCell (i, j, 0)";
_doskacell(_i,_j,(int) (0));
 }
};
 }
};
 //BA.debugLineNum = 271;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 272;BA.debugLine="first = True";
_first = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 273;BA.debugLine="End Sub";
return "";
}
public static String  _lbltext_click() throws Exception{
 //BA.debugLineNum = 1454;BA.debugLine="Sub lblText_Click";
 //BA.debugLineNum = 1455;BA.debugLine="lblText.Visible=False";
mostCurrent._lbltext.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1456;BA.debugLine="WinOpen=False";
_winopen = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1457;BA.debugLine="End Sub";
return "";
}
public static String  _leaderboard_click() throws Exception{
anywheresoftware.b4a.objects.AnimationWrapper _a1 = null;
 //BA.debugLineNum = 1305;BA.debugLine="Sub Leaderboard_Click";
 //BA.debugLineNum = 1306;BA.debugLine="Dim a1 As Animation";
_a1 = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 1307;BA.debugLine="a1.InitializeAlpha(\"\", 0, 1)";
_a1.InitializeAlpha(mostCurrent.activityBA,"",(float) (0),(float) (1));
 //BA.debugLineNum = 1308;BA.debugLine="a1.Duration = 800";
_a1.setDuration((long) (800));
 //BA.debugLineNum = 1309;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1310;BA.debugLine="dlgrec.Visible=True";
mostCurrent._dlgrec.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1311;BA.debugLine="a1.Start(dlgrec)";
_a1.Start((android.view.View)(mostCurrent._dlgrec.getObject()));
 //BA.debugLineNum = 1312;BA.debugLine="SetLabelTextSize(bs,beg.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._bs,BA.NumberToString(mostCurrent._beg.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1313;BA.debugLine="SetLabelTextSize(bn,beg.name,100,1)";
_setlabeltextsize(mostCurrent._bn,mostCurrent._beg.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1314;BA.debugLine="SetLabelTextSize(iss,emm.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._iss,BA.NumberToString(mostCurrent._emm.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1315;BA.debugLine="SetLabelTextSize(In,emm.name,100,1)";
_setlabeltextsize(mostCurrent._in,mostCurrent._emm.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1316;BA.debugLine="SetLabelTextSize(es,exp.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._es,BA.NumberToString(mostCurrent._exp.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1317;BA.debugLine="SetLabelTextSize(en,exp.name,100,1)";
_setlabeltextsize(mostCurrent._en,mostCurrent._exp.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1318;BA.debugLine="es.Text=exp.score&\" seconds\"";
mostCurrent._es.setText((Object)(BA.NumberToString(mostCurrent._exp.score)+" seconds"));
 //BA.debugLineNum = 1319;BA.debugLine="en.Text=exp.name";
mostCurrent._en.setText((Object)(mostCurrent._exp.name));
 //BA.debugLineNum = 1320;BA.debugLine="iss.Text=emm.score&\" seconds\"";
mostCurrent._iss.setText((Object)(BA.NumberToString(mostCurrent._emm.score)+" seconds"));
 //BA.debugLineNum = 1321;BA.debugLine="In.Text=emm.name";
mostCurrent._in.setText((Object)(mostCurrent._emm.name));
 //BA.debugLineNum = 1322;BA.debugLine="bs.Text=beg.score&\" seconds\"";
mostCurrent._bs.setText((Object)(BA.NumberToString(mostCurrent._beg.score)+" seconds"));
 //BA.debugLineNum = 1323;BA.debugLine="bn.Text=beg.name";
mostCurrent._bn.setText((Object)(mostCurrent._beg.name));
 //BA.debugLineNum = 1324;BA.debugLine="WinOpen=True";
_winopen = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1325;BA.debugLine="End Sub";
return "";
}
public static String  _loadfile() throws Exception{
anywheresoftware.b4a.objects.streams.File.TextReaderWrapper _textreader1 = null;
 //BA.debugLineNum = 1272;BA.debugLine="Sub LoadFile";
 //BA.debugLineNum = 1273;BA.debugLine="Dim TextReader1 As TextReader";
_textreader1 = new anywheresoftware.b4a.objects.streams.File.TextReaderWrapper();
 //BA.debugLineNum = 1274;BA.debugLine="TextReader1.Initialize(File.OpenInput(File.Dir";
_textreader1.Initialize((java.io.InputStream)(anywheresoftware.b4a.keywords.Common.File.OpenInput(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"Pref.txt").getObject()));
 //BA.debugLineNum = 1275;BA.debugLine="Level=TextReader1.ReadLine";
_level = (int)(Double.parseDouble(_textreader1.ReadLine()));
 //BA.debugLineNum = 1276;BA.debugLine="beg.name=TextReader1.ReadLine";
mostCurrent._beg.name = _textreader1.ReadLine();
 //BA.debugLineNum = 1277;BA.debugLine="beg.score=TextReader1.ReadLine";
mostCurrent._beg.score = (int)(Double.parseDouble(_textreader1.ReadLine()));
 //BA.debugLineNum = 1278;BA.debugLine="emm.name=TextReader1.ReadLine";
mostCurrent._emm.name = _textreader1.ReadLine();
 //BA.debugLineNum = 1279;BA.debugLine="emm.score=TextReader1.ReadLine";
mostCurrent._emm.score = (int)(Double.parseDouble(_textreader1.ReadLine()));
 //BA.debugLineNum = 1280;BA.debugLine="exp.name=TextReader1.ReadLine";
mostCurrent._exp.name = _textreader1.ReadLine();
 //BA.debugLineNum = 1281;BA.debugLine="exp.score=TextReader1.ReadLine";
mostCurrent._exp.score = (int)(Double.parseDouble(_textreader1.ReadLine()));
 //BA.debugLineNum = 1282;BA.debugLine="SizeX=TextReader1.ReadLine";
_sizex = (int)(Double.parseDouble(_textreader1.ReadLine()));
 //BA.debugLineNum = 1283;BA.debugLine="SizeY=TextReader1.ReadLine";
_sizey = (int)(Double.parseDouble(_textreader1.ReadLine()));
 //BA.debugLineNum = 1284;BA.debugLine="KolMines=TextReader1.ReadLine";
_kolmines = (int)(Double.parseDouble(_textreader1.ReadLine()));
 //BA.debugLineNum = 1285;BA.debugLine="If TextReader1.ReadLine=\"1\" Then SoundOn=True Els";
if ((_textreader1.ReadLine()).equals("1")) { 
_soundon = anywheresoftware.b4a.keywords.Common.True;}
else {
_soundon = anywheresoftware.b4a.keywords.Common.False;};
 //BA.debugLineNum = 1286;BA.debugLine="TextReader1.Close";
_textreader1.Close();
 //BA.debugLineNum = 1287;BA.debugLine="End Sub";
return "";
}
public static String  _loss(int _i,int _j) throws Exception{
int _i1 = 0;
int _j1 = 0;
 //BA.debugLineNum = 902;BA.debugLine="Sub Loss(i As Int, j As Int)";
 //BA.debugLineNum = 903;BA.debugLine="FaceBtn(2)";
_facebtn((int) (2));
 //BA.debugLineNum = 904;BA.debugLine="Dim i1 As Int, j1 As Int";
_i1 = 0;
_j1 = 0;
 //BA.debugLineNum = 905;BA.debugLine="lose=True";
_lose = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 906;BA.debugLine="If SoundOn Then";
if (_soundon) { 
 //BA.debugLineNum = 907;BA.debugLine="pv.Vibrate(100)";
mostCurrent._pv.Vibrate(processBA,(long) (100));
 //BA.debugLineNum = 908;BA.debugLine="MP.Load(File.DirAssets,\"lose.wav\")";
mostCurrent._mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"lose.wav");
 //BA.debugLineNum = 909;BA.debugLine="MP.Play";
mostCurrent._mp.Play();
 };
 //BA.debugLineNum = 911;BA.debugLine="DoskaCell (i, j, 3)";
_doskacell(_i,_j,(int) (3));
 //BA.debugLineNum = 912;BA.debugLine="For i1 = 1 To SizeY";
{
final int step10 = 1;
final int limit10 = _sizey;
for (_i1 = (int) (1) ; (step10 > 0 && _i1 <= limit10) || (step10 < 0 && _i1 >= limit10); _i1 = ((int)(0 + _i1 + step10)) ) {
 //BA.debugLineNum = 913;BA.debugLine="For j1 = 1 To SizeX";
{
final int step11 = 1;
final int limit11 = _sizex;
for (_j1 = (int) (1) ; (step11 > 0 && _j1 <= limit11) || (step11 < 0 && _j1 >= limit11); _j1 = ((int)(0 + _j1 + step11)) ) {
 //BA.debugLineNum = 914;BA.debugLine="If i <> i1 Or j <> j1 Then";
if (_i!=_i1 || _j!=_j1) { 
 //BA.debugLineNum = 915;BA.debugLine="If Doska(i1, j1) = -1 Then";
if (_doska[_i1][_j1]==-1) { 
 //BA.debugLineNum = 916;BA.debugLine="If Doska1(i1, j1) <> 1 Then DoskaCell (i";
if (_doska1[_i1][_j1]!=1) { 
_doskacell(_i1,_j1,(int) (5));};
 }else {
 //BA.debugLineNum = 918;BA.debugLine="If Doska1(i1, j1) = 1 Then DoskaCell (i1";
if (_doska1[_i1][_j1]==1) { 
_doskacell(_i1,_j1,(int) (4));};
 };
 };
 //BA.debugLineNum = 921;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 }
};
 }
};
 //BA.debugLineNum = 924;BA.debugLine="FaceBtn (2)";
_facebtn((int) (2));
 //BA.debugLineNum = 925;BA.debugLine="GameOver = True";
_gameover = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 926;BA.debugLine="Timer1.Enabled = False";
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 927;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 928;BA.debugLine="End Sub";
return "";
}
public static String  _medium_click() throws Exception{
 //BA.debugLineNum = 1188;BA.debugLine="Sub Medium_Click";
 //BA.debugLineNum = 1189;BA.debugLine="SizeX=16";
_sizex = (int) (16);
 //BA.debugLineNum = 1190;BA.debugLine="SizeY=16";
_sizey = (int) (16);
 //BA.debugLineNum = 1191;BA.debugLine="KolMines=40";
_kolmines = (int) (40);
 //BA.debugLineNum = 1192;BA.debugLine="Level=1";
_level = (int) (1);
 //BA.debugLineNum = 1193;BA.debugLine="InitGame";
_initgame();
 //BA.debugLineNum = 1194;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1195;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1196;BA.debugLine="If Appodeal.isLoaded(Appodeal.INTERSTITIAL) Then";
if (mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL)) { 
 //BA.debugLineNum = 1197;BA.debugLine="Appodeal.show(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 //BA.debugLineNum = 1198;BA.debugLine="Appodeal.cache(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.cache(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 };
 //BA.debugLineNum = 1200;BA.debugLine="End Sub";
return "";
}
public static String  _menuanim_tick() throws Exception{
 //BA.debugLineNum = 991;BA.debugLine="Sub MenuAnim_Tick";
 //BA.debugLineNum = 992;BA.debugLine="If game Then";
if (_game) { 
 //BA.debugLineNum = 993;BA.debugLine="If Hide=False Then";
if (_hide==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 994;BA.debugLine="If Beginner.Top<New.Top+New.Height-2.5%x Then";
if (mostCurrent._beginner.getTop()<mostCurrent._new.getTop()+mostCurrent._new.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2.5),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 995;BA.debugLine="Beginner.Top=Beginner.Top+4%x";
mostCurrent._beginner.setTop((int) (mostCurrent._beginner.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 996;BA.debugLine="Medium.Top=Medium.Top+4%x";
mostCurrent._medium.setTop((int) (mostCurrent._medium.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 997;BA.debugLine="Expert.Top=Expert.Top+4%x";
mostCurrent._expert.setTop((int) (mostCurrent._expert.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 998;BA.debugLine="Custom.Top=Custom.Top+4%x";
mostCurrent._custom.setTop((int) (mostCurrent._custom.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 999;BA.debugLine="Sound.Top=Sound.Top+4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1000;BA.debugLine="Leaderboard.Top=Leaderboard.Top+4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1002;BA.debugLine="New.Visible=True";
mostCurrent._new.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1003;BA.debugLine="Beginner.Visible=True";
mostCurrent._beginner.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1004;BA.debugLine="Medium.Visible=True";
mostCurrent._medium.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1005;BA.debugLine="Expert.Visible=True";
mostCurrent._expert.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1006;BA.debugLine="Custom.Visible=True";
mostCurrent._custom.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1007;BA.debugLine="Sound.Visible=True";
mostCurrent._sound.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1008;BA.debugLine="Leaderboard.Visible=True";
mostCurrent._leaderboard.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1009;BA.debugLine="If Medium.Top<Beginner.Top+Beginner.Height-2.5%x T";
if (mostCurrent._medium.getTop()<mostCurrent._beginner.getTop()+mostCurrent._beginner.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2.5),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1010;BA.debugLine="Medium.Top=Medium.Top+4%x";
mostCurrent._medium.setTop((int) (mostCurrent._medium.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1011;BA.debugLine="Expert.Top=Expert.Top+4%x";
mostCurrent._expert.setTop((int) (mostCurrent._expert.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1012;BA.debugLine="Custom.Top=Custom.Top+4%x";
mostCurrent._custom.setTop((int) (mostCurrent._custom.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1013;BA.debugLine="Sound.Top=Sound.Top+4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1014;BA.debugLine="Leaderboard.Top=Leaderboard.Top+4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1016;BA.debugLine="If Expert.Top<Medium.Top+Medium.Height-2.5%x Then";
if (mostCurrent._expert.getTop()<mostCurrent._medium.getTop()+mostCurrent._medium.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2.5),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1017;BA.debugLine="Expert.Top=Expert.Top+4%x";
mostCurrent._expert.setTop((int) (mostCurrent._expert.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1018;BA.debugLine="Custom.Top=Custom.Top+4%x";
mostCurrent._custom.setTop((int) (mostCurrent._custom.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1019;BA.debugLine="Sound.Top=Sound.Top+4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1020;BA.debugLine="Leaderboard.Top=Leaderboard.Top+4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1022;BA.debugLine="If Custom.Top<Expert.Top+Expert.Height-2.5%x Then";
if (mostCurrent._custom.getTop()<mostCurrent._expert.getTop()+mostCurrent._expert.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2.5),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1023;BA.debugLine="Custom.Top=Custom.Top+4%x";
mostCurrent._custom.setTop((int) (mostCurrent._custom.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1024;BA.debugLine="Sound.Top=Sound.Top+4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1025;BA.debugLine="Leaderboard.Top=Leaderboard.Top+4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1027;BA.debugLine="If Sound.Top<Custom.Top+Custom.Height-2.5%x Then";
if (mostCurrent._sound.getTop()<mostCurrent._custom.getTop()+mostCurrent._custom.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2.5),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1028;BA.debugLine="Sound.Top=Sound.Top+4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1029;BA.debugLine="Leaderboard.Top=Leaderboard.Top+4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1031;BA.debugLine="If Leaderboard.Top<Sound.Top+Sound.Height-2.5%x Th";
if (mostCurrent._leaderboard.getTop()<mostCurrent._sound.getTop()+mostCurrent._sound.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2.5),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1032;BA.debugLine="Leaderboard.Top=Leaderboard.Top+4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1034;BA.debugLine="MenuAnim.Enabled=False";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1035;BA.debugLine="Hide=True";
_hide = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1036;BA.debugLine="Select Level";
switch (_level) {
case 0: {
 //BA.debugLineNum = 1038;BA.debugLine="Galka.Top=Beginner.top";
mostCurrent._galka.setTop(mostCurrent._beginner.getTop());
 break; }
case 1: {
 //BA.debugLineNum = 1040;BA.debugLine="Galka.Top=Medium.top";
mostCurrent._galka.setTop(mostCurrent._medium.getTop());
 break; }
case 2: {
 //BA.debugLineNum = 1042;BA.debugLine="Galka.Top=Expert.top";
mostCurrent._galka.setTop(mostCurrent._expert.getTop());
 break; }
case 3: {
 //BA.debugLineNum = 1044;BA.debugLine="Galka.Top=Custom.top";
mostCurrent._galka.setTop(mostCurrent._custom.getTop());
 break; }
}
;
 //BA.debugLineNum = 1046;BA.debugLine="Galka.Visible=True";
mostCurrent._galka.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1047;BA.debugLine="bmp.Initialize(File.DirAssets, \"gameb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gameb.png");
 //BA.debugLineNum = 1048;BA.debugLine="RectFon.Initialize(5%x,18%x,23%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (23),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1049;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1050;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1051;BA.debugLine="bmp.Initialize(File.DirAssets, \"helpb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"helpb.png");
 //BA.debugLineNum = 1052;BA.debugLine="RectFon.Initialize(25%x,18%x,43%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (43),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1053;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1054;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1055;BA.debugLine="Galka2.Top=Sound.top";
mostCurrent._galka2.setTop(mostCurrent._sound.getTop());
 //BA.debugLineNum = 1056;BA.debugLine="If SoundOn Then Galka2.Visible=True";
if (_soundon) { 
mostCurrent._galka2.setVisible(anywheresoftware.b4a.keywords.Common.True);};
 };
 };
 };
 };
 };
 };
 }else {
 //BA.debugLineNum = 1064;BA.debugLine="If Leaderboard.Top+Leaderboard.Height>=Sound.Top+S";
if (mostCurrent._leaderboard.getTop()+mostCurrent._leaderboard.getHeight()>=mostCurrent._sound.getTop()+mostCurrent._sound.getHeight()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1065;BA.debugLine="Leaderboard.Top=Leaderboard.Top-4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1066;BA.debugLine="Galka.Visible=False";
mostCurrent._galka.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1067;BA.debugLine="Galka2.Visible=False";
mostCurrent._galka2.setVisible(anywheresoftware.b4a.keywords.Common.False);
 }else {
 //BA.debugLineNum = 1069;BA.debugLine="If Sound.Top+Sound.Height>=Custom.Top+Custom.Heigh";
if (mostCurrent._sound.getTop()+mostCurrent._sound.getHeight()>=mostCurrent._custom.getTop()+mostCurrent._custom.getHeight()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1070;BA.debugLine="Sound.Top=Sound.Top-4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1071;BA.debugLine="Leaderboard.Top=Leaderboard.Top-4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1073;BA.debugLine="If Custom.Top+Custom.Height>=Expert.Top+Expert.Hei";
if (mostCurrent._custom.getTop()+mostCurrent._custom.getHeight()>=mostCurrent._expert.getTop()+mostCurrent._expert.getHeight()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1074;BA.debugLine="Custom.Top=Custom.Top-4%x";
mostCurrent._custom.setTop((int) (mostCurrent._custom.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1075;BA.debugLine="Sound.Top=Sound.Top-4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1076;BA.debugLine="Leaderboard.Top=Leaderboard.Top-4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1078;BA.debugLine="If Expert.Top+Expert.Height>=Medium.Top+Medium.Hei";
if (mostCurrent._expert.getTop()+mostCurrent._expert.getHeight()>=mostCurrent._medium.getTop()+mostCurrent._medium.getHeight()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1079;BA.debugLine="Expert.Top=Expert.Top-4%x";
mostCurrent._expert.setTop((int) (mostCurrent._expert.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1080;BA.debugLine="Custom.Top=Custom.Top-4%x";
mostCurrent._custom.setTop((int) (mostCurrent._custom.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1081;BA.debugLine="Sound.Top=Sound.Top-4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1082;BA.debugLine="Leaderboard.Top=Leaderboard.Top-4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1084;BA.debugLine="If Medium.Top+Medium.Height>=Beginner.Top+Beginner";
if (mostCurrent._medium.getTop()+mostCurrent._medium.getHeight()>=mostCurrent._beginner.getTop()+mostCurrent._beginner.getHeight()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1085;BA.debugLine="Medium.Top=Medium.Top-4%x";
mostCurrent._medium.setTop((int) (mostCurrent._medium.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1086;BA.debugLine="Expert.Top=Expert.Top-4%x";
mostCurrent._expert.setTop((int) (mostCurrent._expert.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1087;BA.debugLine="Custom.Top=Custom.Top-4%x";
mostCurrent._custom.setTop((int) (mostCurrent._custom.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1088;BA.debugLine="Sound.Top=Sound.Top-4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1089;BA.debugLine="Leaderboard.Top=Leaderboard.Top-4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1091;BA.debugLine="New.Visible=False";
mostCurrent._new.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1092;BA.debugLine="Beginner.Visible=False";
mostCurrent._beginner.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1093;BA.debugLine="Medium.Visible=False";
mostCurrent._medium.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1094;BA.debugLine="Expert.Visible=False";
mostCurrent._expert.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1095;BA.debugLine="Sound.Visible=False";
mostCurrent._sound.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1096;BA.debugLine="Leaderboard.Visible=False";
mostCurrent._leaderboard.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1097;BA.debugLine="Custom.Visible=False";
mostCurrent._custom.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1098;BA.debugLine="If Beginner.Top+Beginner.Height>=New.Top+New.Heigh";
if (mostCurrent._beginner.getTop()+mostCurrent._beginner.getHeight()>=mostCurrent._new.getTop()+mostCurrent._new.getHeight()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1099;BA.debugLine="Beginner.Top=Beginner.Top-4%x";
mostCurrent._beginner.setTop((int) (mostCurrent._beginner.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1100;BA.debugLine="Medium.Top=Medium.Top-4%x";
mostCurrent._medium.setTop((int) (mostCurrent._medium.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1101;BA.debugLine="Expert.Top=Expert.Top-4%x";
mostCurrent._expert.setTop((int) (mostCurrent._expert.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1102;BA.debugLine="Custom.Top=Custom.Top-4%x";
mostCurrent._custom.setTop((int) (mostCurrent._custom.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1103;BA.debugLine="Sound.Top=Sound.Top-4%x";
mostCurrent._sound.setTop((int) (mostCurrent._sound.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 //BA.debugLineNum = 1104;BA.debugLine="Leaderboard.Top=Leaderboard.Top-4%x";
mostCurrent._leaderboard.setTop((int) (mostCurrent._leaderboard.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1106;BA.debugLine="bmp.Initialize(File.DirAssets, \"gameb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gameb.png");
 //BA.debugLineNum = 1107;BA.debugLine="RectFon.Initialize(5%x,18%x,23%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (23),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1108;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1109;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1110;BA.debugLine="bmp.Initialize(File.DirAssets, \"helpb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"helpb.png");
 //BA.debugLineNum = 1111;BA.debugLine="RectFon.Initialize(25%x,18%x,43%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (43),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1112;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1113;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1114;BA.debugLine="MenuAnim.Enabled=False";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1115;BA.debugLine="Hide=False";
_hide = anywheresoftware.b4a.keywords.Common.False;
 };
 };
 };
 };
 };
 };
 };
 }else {
 //BA.debugLineNum = 1124;BA.debugLine="If Hide=False Then";
if (_hide==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1125;BA.debugLine="About.Visible=True";
mostCurrent._about.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1126;BA.debugLine="Rules.Visible=True";
mostCurrent._rules.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1127;BA.debugLine="If About.Top<Rules.Top+Rules.Height-2.5%x Then";
if (mostCurrent._about.getTop()<mostCurrent._rules.getTop()+mostCurrent._rules.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2.5),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1128;BA.debugLine="About.Top=About.Top+4%x";
mostCurrent._about.setTop((int) (mostCurrent._about.getTop()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1130;BA.debugLine="bmp.Initialize(File.DirAssets, \"helpb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"helpb.png");
 //BA.debugLineNum = 1131;BA.debugLine="RectFon.Initialize(25%x,18%x,43%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (43),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1132;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1133;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1134;BA.debugLine="bmp.Initialize(File.DirAssets, \"gameb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gameb.png");
 //BA.debugLineNum = 1135;BA.debugLine="RectFon.Initialize(5%x,18%x,23%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (23),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1136;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1137;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1138;BA.debugLine="MenuAnim.Enabled=False";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1139;BA.debugLine="Hide=True";
_hide = anywheresoftware.b4a.keywords.Common.True;
 };
 }else {
 //BA.debugLineNum = 1142;BA.debugLine="If About.Top+About.Height>=Rules.Top+Rules.Height+";
if (mostCurrent._about.getTop()+mostCurrent._about.getHeight()>=mostCurrent._rules.getTop()+mostCurrent._rules.getHeight()+anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (2),mostCurrent.activityBA)) { 
 //BA.debugLineNum = 1143;BA.debugLine="About.Top=About.Top-4%x";
mostCurrent._about.setTop((int) (mostCurrent._about.getTop()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA)));
 }else {
 //BA.debugLineNum = 1145;BA.debugLine="About.Visible=False";
mostCurrent._about.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1146;BA.debugLine="Rules.Visible=False";
mostCurrent._rules.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1147;BA.debugLine="bmp.Initialize(File.DirAssets, \"helpb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"helpb.png");
 //BA.debugLineNum = 1148;BA.debugLine="RectFon.Initialize(25%x,18%x,43%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (43),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1149;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1150;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1151;BA.debugLine="bmp.Initialize(File.DirAssets, \"gameb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"gameb.png");
 //BA.debugLineNum = 1152;BA.debugLine="RectFon.Initialize(5%x,18%x,23%x, 27%x)";
mostCurrent._rectfon.Initialize(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (23),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 1153;BA.debugLine="cnvs.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1154;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 1155;BA.debugLine="MenuAnim.Enabled=False";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1156;BA.debugLine="Hide=False";
_hide = anywheresoftware.b4a.keywords.Common.False;
 };
 };
 };
 //BA.debugLineNum = 1160;BA.debugLine="End Sub";
return "";
}
public static String  _mine2_touch(int _action,float _x,float _y) throws Exception{
 //BA.debugLineNum = 1215;BA.debugLine="Sub Mine2_Touch (Action As Int, X As Float, Y As F";
 //BA.debugLineNum = 1216;BA.debugLine="If WinOpen=False Then";
if (_winopen==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 1217;BA.debugLine="If X>Face.X And X<Face.X+conFaceWidth And Y>Face.Y";
if (_x>mostCurrent._face.x && _x<mostCurrent._face.x+_confacewidth && _y>mostCurrent._face.y-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA) && _y<mostCurrent._face.y-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA)+_confaceheight) { 
_initgame();}
else {
_facebtn(_oldface);};
 };
 //BA.debugLineNum = 1219;BA.debugLine="End Sub";
return "";
}
public static String  _new_click() throws Exception{
 //BA.debugLineNum = 1162;BA.debugLine="Sub New_Click";
 //BA.debugLineNum = 1163;BA.debugLine="InitGame";
_initgame();
 //BA.debugLineNum = 1164;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1165;BA.debugLine="If Appodeal.isLoaded(Appodeal.INTERSTITIAL) Then";
if (mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL)) { 
 //BA.debugLineNum = 1166;BA.debugLine="Appodeal.show(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 //BA.debugLineNum = 1167;BA.debugLine="Appodeal.cache(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.cache(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 };
 //BA.debugLineNum = 1169;BA.debugLine="End Sub";
return "";
}
public static String  _ok_click() throws Exception{
anywheresoftware.b4a.objects.AnimationWrapper _a1 = null;
anywheresoftware.b4a.phone.Phone _phone = null;
 //BA.debugLineNum = 1236;BA.debugLine="Sub OK_Click";
 //BA.debugLineNum = 1237;BA.debugLine="Dim a1 As Animation";
_a1 = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 1238;BA.debugLine="a1.InitializeAlpha(\"\", 1, 0)";
_a1.InitializeAlpha(mostCurrent.activityBA,"",(float) (1),(float) (0));
 //BA.debugLineNum = 1239;BA.debugLine="a1.Duration = 800";
_a1.setDuration((long) (800));
 //BA.debugLineNum = 1240;BA.debugLine="If H.Text=\"\" Then H.Text=\"0\"";
if ((mostCurrent._h.getText()).equals("")) { 
mostCurrent._h.setText((Object)("0"));};
 //BA.debugLineNum = 1241;BA.debugLine="If W.Text=\"\" Then W.Text=\"0\"";
if ((mostCurrent._w.getText()).equals("")) { 
mostCurrent._w.setText((Object)("0"));};
 //BA.debugLineNum = 1242;BA.debugLine="If Mine.Text=\"\" Then Mine.Text=\"0\"";
if ((mostCurrent._mine.getText()).equals("")) { 
mostCurrent._mine.setText((Object)("0"));};
 //BA.debugLineNum = 1243;BA.debugLine="SizeY=H.Text";
_sizey = (int)(Double.parseDouble(mostCurrent._h.getText()));
 //BA.debugLineNum = 1244;BA.debugLine="SizeX=W.Text";
_sizex = (int)(Double.parseDouble(mostCurrent._w.getText()));
 //BA.debugLineNum = 1245;BA.debugLine="KolMines=Mine.Text";
_kolmines = (int)(Double.parseDouble(mostCurrent._mine.getText()));
 //BA.debugLineNum = 1246;BA.debugLine="a1.Start(CustomPanel)";
_a1.Start((android.view.View)(mostCurrent._custompanel.getObject()));
 //BA.debugLineNum = 1247;BA.debugLine="CustomPanel.Visible=False";
mostCurrent._custompanel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1248;BA.debugLine="Level=3";
_level = (int) (3);
 //BA.debugLineNum = 1249;BA.debugLine="If SizeX>30 Then SizeX=30";
if (_sizex>30) { 
_sizex = (int) (30);};
 //BA.debugLineNum = 1250;BA.debugLine="If SizeY>24 Then SizeY=24";
if (_sizey>24) { 
_sizey = (int) (24);};
 //BA.debugLineNum = 1251;BA.debugLine="If KolMines>667 Then KolMines=667";
if (_kolmines>667) { 
_kolmines = (int) (667);};
 //BA.debugLineNum = 1252;BA.debugLine="If SizeX<9 Then SizeX=9";
if (_sizex<9) { 
_sizex = (int) (9);};
 //BA.debugLineNum = 1253;BA.debugLine="If SizeY<9 Then SizeY=9";
if (_sizey<9) { 
_sizey = (int) (9);};
 //BA.debugLineNum = 1254;BA.debugLine="If KolMines<10 Then KolMines=10";
if (_kolmines<10) { 
_kolmines = (int) (10);};
 //BA.debugLineNum = 1255;BA.debugLine="If KolMines>=(SizeX-1)*(SizeY-1) Then KolMines=(S";
if (_kolmines>=(_sizex-1)*(_sizey-1)) { 
_kolmines = (int) ((_sizex-1)*(_sizey-1));};
 //BA.debugLineNum = 1256;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 1257;BA.debugLine="Phone.HideKeyboard(Activity)";
_phone.HideKeyboard(mostCurrent._activity);
 //BA.debugLineNum = 1258;BA.debugLine="Mine.Enabled=False";
mostCurrent._mine.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1259;BA.debugLine="WinOpen=False";
_winopen = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1260;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1261;BA.debugLine="InitGame";
_initgame();
 //BA.debugLineNum = 1262;BA.debugLine="End Sub";
return "";
}
public static String  _ok2_click() throws Exception{
anywheresoftware.b4a.objects.AnimationWrapper _a1 = null;
 //BA.debugLineNum = 1326;BA.debugLine="Sub OK2_Click";
 //BA.debugLineNum = 1327;BA.debugLine="Dim a1 As Animation";
_a1 = new anywheresoftware.b4a.objects.AnimationWrapper();
 //BA.debugLineNum = 1328;BA.debugLine="a1.InitializeAlpha(\"\", 1, 0)";
_a1.InitializeAlpha(mostCurrent.activityBA,"",(float) (1),(float) (0));
 //BA.debugLineNum = 1329;BA.debugLine="a1.Duration = 800";
_a1.setDuration((long) (800));
 //BA.debugLineNum = 1330;BA.debugLine="WinOpen=False";
_winopen = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 1331;BA.debugLine="a1.Start(dlgrec)";
_a1.Start((android.view.View)(mostCurrent._dlgrec.getObject()));
 //BA.debugLineNum = 1332;BA.debugLine="dlgrec.Visible=False";
mostCurrent._dlgrec.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1333;BA.debugLine="End Sub";
return "";
}
public static String  _ok3_click() throws Exception{
anywheresoftware.b4a.phone.Phone _phone = null;
 //BA.debugLineNum = 1420;BA.debugLine="Sub OK3_Click";
 //BA.debugLineNum = 1421;BA.debugLine="Select Level";
switch (_level) {
case 0: {
 //BA.debugLineNum = 1423;BA.debugLine="beg.name=EditText1.Text";
mostCurrent._beg.name = mostCurrent._edittext1.getText();
 //BA.debugLineNum = 1424;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1425;BA.debugLine="LoadFile";
_loadfile();
 //BA.debugLineNum = 1426;BA.debugLine="SetLabelTextSize(bs,beg.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._bs,BA.NumberToString(mostCurrent._beg.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1427;BA.debugLine="SetLabelTextSize(bn,beg.name,100,1)";
_setlabeltextsize(mostCurrent._bn,mostCurrent._beg.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1428;BA.debugLine="bs.Text=beg.score&\" seconds\"";
mostCurrent._bs.setText((Object)(BA.NumberToString(mostCurrent._beg.score)+" seconds"));
 //BA.debugLineNum = 1429;BA.debugLine="bn.Text=beg.name";
mostCurrent._bn.setText((Object)(mostCurrent._beg.name));
 //BA.debugLineNum = 1430;BA.debugLine="dlgrec.Visible=True";
mostCurrent._dlgrec.setVisible(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 1: {
 //BA.debugLineNum = 1432;BA.debugLine="emm.name=EditText1.Text";
mostCurrent._emm.name = mostCurrent._edittext1.getText();
 //BA.debugLineNum = 1433;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1434;BA.debugLine="LoadFile";
_loadfile();
 //BA.debugLineNum = 1435;BA.debugLine="SetLabelTextSize(iss,emm.score&\" seconds\",100,1";
_setlabeltextsize(mostCurrent._iss,BA.NumberToString(mostCurrent._emm.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1436;BA.debugLine="SetLabelTextSize(In,emm.name,100,1)";
_setlabeltextsize(mostCurrent._in,mostCurrent._emm.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1437;BA.debugLine="iss.Text=emm.score&\" seconds\"";
mostCurrent._iss.setText((Object)(BA.NumberToString(mostCurrent._emm.score)+" seconds"));
 //BA.debugLineNum = 1438;BA.debugLine="In.Text=emm.name";
mostCurrent._in.setText((Object)(mostCurrent._emm.name));
 //BA.debugLineNum = 1439;BA.debugLine="dlgrec.Visible=True";
mostCurrent._dlgrec.setVisible(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 2: {
 //BA.debugLineNum = 1441;BA.debugLine="exp.name=EditText1.Text";
mostCurrent._exp.name = mostCurrent._edittext1.getText();
 //BA.debugLineNum = 1442;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1443;BA.debugLine="LoadFile";
_loadfile();
 //BA.debugLineNum = 1444;BA.debugLine="SetLabelTextSize(es,exp.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._es,BA.NumberToString(mostCurrent._exp.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1445;BA.debugLine="SetLabelTextSize(en,exp.name,100,1)";
_setlabeltextsize(mostCurrent._en,mostCurrent._exp.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1446;BA.debugLine="es.Text=exp.score&\" seconds\"";
mostCurrent._es.setText((Object)(BA.NumberToString(mostCurrent._exp.score)+" seconds"));
 //BA.debugLineNum = 1447;BA.debugLine="en.Text=exp.name";
mostCurrent._en.setText((Object)(mostCurrent._exp.name));
 //BA.debugLineNum = 1448;BA.debugLine="dlgrec.Visible=True";
mostCurrent._dlgrec.setVisible(anywheresoftware.b4a.keywords.Common.True);
 break; }
}
;
 //BA.debugLineNum = 1450;BA.debugLine="BestPanel.Visible=False";
mostCurrent._bestpanel.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 1451;BA.debugLine="Dim Phone As Phone";
_phone = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 1452;BA.debugLine="Phone.HideKeyboard(Activity)";
_phone.HideKeyboard(mostCurrent._activity);
 //BA.debugLineNum = 1453;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 43;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 44;BA.debugLine="End Sub";
return "";
}
public static String  _pz_click(dominex.example.pinchzoomandmove._touchdata _touchdata) throws Exception{
int _i = 0;
int _j = 0;
int _i1 = 0;
int _j1 = 0;
int _empt = 0;
float _x = 0f;
float _y = 0f;
 //BA.debugLineNum = 628;BA.debugLine="Sub PZ_Click (TouchData As TouchData)";
 //BA.debugLineNum = 629;BA.debugLine="Try";
try { //BA.debugLineNum = 630;BA.debugLine="If WinOpen=False Then";
if (_winopen==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 631;BA.debugLine="Dim i As Int=0, j As Int=0, i1 As Int=0, j1 As In";
_i = (int) (0);
_j = (int) (0);
_i1 = (int) (0);
_j1 = (int) (0);
_empt = 0;
_x = 0f;
_y = 0f;
 //BA.debugLineNum = 632;BA.debugLine="x=TouchData.x/(PZ(NumView).Zoom/100)";
_x = (float) (_touchdata.X/(double)(mostCurrent._pz[_numview]._getzoom()/(double)100));
 //BA.debugLineNum = 633;BA.debugLine="y=TouchData.y/(PZ(NumView).Zoom/100)";
_y = (float) (_touchdata.Y/(double)(mostCurrent._pz[_numview]._getzoom()/(double)100));
 //BA.debugLineNum = 634;BA.debugLine="If x>=5%x And x<=23%x And y>=18%x And y<=27%x The";
if (_x>=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA) && _x<=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (23),mostCurrent.activityBA) && _y>=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA) && _y<=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA)) { 
_gameb();};
 //BA.debugLineNum = 635;BA.debugLine="If x>=25%x And x<=43%x And y>=18%x And y<=27%x Th";
if (_x>=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA) && _x<=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (43),mostCurrent.activityBA) && _y>=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (18),mostCurrent.activityBA) && _y<=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA)) { 
_helpb();};
 //BA.debugLineNum = 636;BA.debugLine="If x>=87%x And x<=97%x And y>=4%x And y<=14%x The";
if (_x>=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (87),mostCurrent.activityBA) && _x<=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (97),mostCurrent.activityBA) && _y>=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (4),mostCurrent.activityBA) && _y<=anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA)) { 
anywheresoftware.b4a.keywords.Common.ExitApplication();};
 //BA.debugLineNum = 637;BA.debugLine="If Hide=False Then";
if (_hide==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 638;BA.debugLine="LongC=False";
_longc = anywheresoftware.b4a.keywords.Common.False;
 //BA.debugLineNum = 639;BA.debugLine="DoubleClick.Enabled=True";
mostCurrent._doubleclick.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 640;BA.debugLine="Time0=DateTime.Now";
_time0 = anywheresoftware.b4a.keywords.Common.DateTime.getNow();
 //BA.debugLineNum = 641;BA.debugLine="x0=x";
_x0 = _x;
 //BA.debugLineNum = 642;BA.debugLine="y0=y";
_y0 = _y;
 //BA.debugLineNum = 643;BA.debugLine="OldX = x";
_oldx = (long) (_x);
 //BA.debugLineNum = 644;BA.debugLine="OldY = y";
_oldy = (long) (_y);
 //BA.debugLineNum = 645;BA.debugLine="OldFace = Face.Face";
_oldface = mostCurrent._face.Face;
 //BA.debugLineNum = 646;BA.debugLine="If FaceCoord(x,y) Then";
if (_facecoord(_x,_y)) { 
 //BA.debugLineNum = 647;BA.debugLine="FaceBtn(0)";
_facebtn((int) (0));
 }else {
 //BA.debugLineNum = 649;BA.debugLine="If Not(GameOver) Then FaceBtn(3)";
if (anywheresoftware.b4a.keywords.Common.Not(_gameover)) { 
_facebtn((int) (3));};
 };
 //BA.debugLineNum = 651;BA.debugLine="If Not(GameOver) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_gameover)) { 
 //BA.debugLineNum = 652;BA.debugLine="If DoskaCoord(x, y) Then";
if (_doskacoord(_x,_y)) { 
 //BA.debugLineNum = 653;BA.debugLine="XY2IJ (x, y, i, j)";
_xy2ij(_x,_y,_i,_j);
 //BA.debugLineNum = 654;BA.debugLine="i=mmm";
_i = _mmm;
 //BA.debugLineNum = 655;BA.debugLine="j=nnn";
_j = _nnn;
 //BA.debugLineNum = 656;BA.debugLine="If Doska1(i, j) = 0 Then";
if (_doska1[_i][_j]==0) { 
 //BA.debugLineNum = 657;BA.debugLine="DoskaCell (i, j, 15)";
_doskacell(_i,_j,(int) (15));
 //BA.debugLineNum = 658;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 659;BA.debugLine="OldI = i: OldJ = j";
_oldi = _i;
 //BA.debugLineNum = 659;BA.debugLine="OldI = i: OldJ = j";
_oldj = _j;
 }else {
 //BA.debugLineNum = 661;BA.debugLine="OldI = 0: OldJ = 0";
_oldi = (int) (0);
 //BA.debugLineNum = 661;BA.debugLine="OldI = 0: OldJ = 0";
_oldj = (int) (0);
 };
 };
 };
 //BA.debugLineNum = 665;BA.debugLine="OldI = 0: OldJ = 0";
_oldi = (int) (0);
 //BA.debugLineNum = 665;BA.debugLine="OldI = 0: OldJ = 0";
_oldj = (int) (0);
 //BA.debugLineNum = 666;BA.debugLine="If FaceCoord(OldX, OldY) And FaceCoord(x, y) T";
if (_facecoord((float) (_oldx),(float) (_oldy)) && _facecoord(_x,_y)) { 
 //BA.debugLineNum = 667;BA.debugLine="FaceBtn(0)";
_facebtn((int) (0));
 //BA.debugLineNum = 668;BA.debugLine="InitGame";
_initgame();
 }else {
 //BA.debugLineNum = 670;BA.debugLine="If FaceTimer.Enabled=False Then FaceTimer.Enabled";
if (mostCurrent._facetimer.getEnabled()==anywheresoftware.b4a.keywords.Common.False) { 
mostCurrent._facetimer.setEnabled(anywheresoftware.b4a.keywords.Common.True);}
else {
_facetimer_tick();};
 };
 //BA.debugLineNum = 672;BA.debugLine="If Not(GameOver) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_gameover)) { 
 //BA.debugLineNum = 673;BA.debugLine="If DoskaCoord(x, y) Then";
if (_doskacoord(_x,_y)) { 
 //BA.debugLineNum = 674;BA.debugLine="If Not(Timer1.Enabled) Then";
if (anywheresoftware.b4a.keywords.Common.Not(mostCurrent._timer1.getEnabled())) { 
 //BA.debugLineNum = 675;BA.debugLine="Timer1.Enabled = True";
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 676;BA.debugLine="ind(1).value = 1";
mostCurrent._ind[(int) (1)].value = (int) (1);
 //BA.debugLineNum = 677;BA.debugLine="Indikator(1,1)";
_indikator((int) (1),(int) (1));
 };
 //BA.debugLineNum = 679;BA.debugLine="XY2IJ (x, y, i, j)";
_xy2ij(_x,_y,_i,_j);
 //BA.debugLineNum = 680;BA.debugLine="i=mmm";
_i = _mmm;
 //BA.debugLineNum = 681;BA.debugLine="j=nnn";
_j = _nnn;
 //BA.debugLineNum = 682;BA.debugLine="If Doska1(i, j) = 0 Then";
if (_doska1[_i][_j]==0) { 
 //BA.debugLineNum = 683;BA.debugLine="Select Doska(i, j)";
switch (BA.switchObjectToInt(_doska[_i][_j],(int) (-1),(int) (0),(int) (1),(int) (2),(int) (3),(int) (4),(int) (5),(int) (6),(int) (7),(int) (8))) {
case 0: {
 //BA.debugLineNum = 685;BA.debugLine="If first Then";
if (_first) { 
 //BA.debugLineNum = 686;BA.debugLine="Find_clean_place";
_find_clean_place();
 //BA.debugLineNum = 687;BA.debugLine="Doska(i, j) = 0";
_doska[_i][_j] = (int) (0);
 //BA.debugLineNum = 688;BA.debugLine="Clear_sosedi";
_clear_sosedi();
 //BA.debugLineNum = 689;BA.debugLine="Count_sosedi";
_count_sosedi();
 //BA.debugLineNum = 691;BA.debugLine="Doska1(i, j) = 3";
_doska1[_i][_j] = (int) (3);
 //BA.debugLineNum = 692;BA.debugLine="DoskaCell (i, j, (8 - Doska(i, j)) +";
_doskacell(_i,_j,(int) ((8-_doska[_i][_j])+7));
 //BA.debugLineNum = 693;BA.debugLine="CloseCell = CloseCell - 1";
_closecell = (int) (_closecell-1);
 //BA.debugLineNum = 694;BA.debugLine="If Doska(i, j) = 0 Then";
if (_doska[_i][_j]==0) { 
 //BA.debugLineNum = 695;BA.debugLine="empt = 1";
_empt = (int) (1);
 //BA.debugLineNum = 696;BA.debugLine="Do While empt > 0";
while (_empt>0) {
 //BA.debugLineNum = 697;BA.debugLine="empt = 0";
_empt = (int) (0);
 //BA.debugLineNum = 698;BA.debugLine="For i = 1 To SizeY";
{
final int step72 = 1;
final int limit72 = _sizey;
for (_i = (int) (1) ; (step72 > 0 && _i <= limit72) || (step72 < 0 && _i >= limit72); _i = ((int)(0 + _i + step72)) ) {
 //BA.debugLineNum = 699;BA.debugLine="For j = 1 To SizeX";
{
final int step73 = 1;
final int limit73 = _sizex;
for (_j = (int) (1) ; (step73 > 0 && _j <= limit73) || (step73 < 0 && _j >= limit73); _j = ((int)(0 + _j + step73)) ) {
 //BA.debugLineNum = 700;BA.debugLine="If Doska(i, j) = 0 And Doska";
if (_doska[_i][_j]==0 && _doska1[_i][_j]==3) { 
 //BA.debugLineNum = 701;BA.debugLine="For i1 = i - 1 To i + 1";
{
final int step75 = 1;
final int limit75 = (int) (_i+1);
for (_i1 = (int) (_i-1) ; (step75 > 0 && _i1 <= limit75) || (step75 < 0 && _i1 >= limit75); _i1 = ((int)(0 + _i1 + step75)) ) {
 //BA.debugLineNum = 702;BA.debugLine="For j1 = j - 1 To j + 1";
{
final int step76 = 1;
final int limit76 = (int) (_j+1);
for (_j1 = (int) (_j-1) ; (step76 > 0 && _j1 <= limit76) || (step76 < 0 && _j1 >= limit76); _j1 = ((int)(0 + _j1 + step76)) ) {
 //BA.debugLineNum = 703;BA.debugLine="If i1 >= 1 And i1 <= S";
if (_i1>=1 && _i1<=_sizey && _j1>=1 && _j1<=_sizex) { 
 //BA.debugLineNum = 704;BA.debugLine="If Doska1(i1, j1) =";
if (_doska1[_i1][_j1]==0) { 
 //BA.debugLineNum = 705;BA.debugLine="empt = empt + 1";
_empt = (int) (_empt+1);
 //BA.debugLineNum = 706;BA.debugLine="Doska1(i1, j1) = 3";
_doska1[_i1][_j1] = (int) (3);
 //BA.debugLineNum = 707;BA.debugLine="DoskaCell (i1, j1,";
_doskacell(_i1,_j1,(int) ((8-_doska[_i1][_j1])+7));
 //BA.debugLineNum = 708;BA.debugLine="CloseCell = CloseC";
_closecell = (int) (_closecell-1);
 //BA.debugLineNum = 709;BA.debugLine="If CloseCell = Kol";
if (_closecell==_kolmines) { 
_win();};
 };
 };
 }
};
 }
};
 };
 }
};
 }
};
 }
;
 };
 }else {
 //BA.debugLineNum = 720;BA.debugLine="Loss (i, j)";
_loss(_i,_j);
 };
 break; }
case 1: 
case 2: 
case 3: 
case 4: 
case 5: 
case 6: 
case 7: 
case 8: 
case 9: {
 //BA.debugLineNum = 723;BA.debugLine="Doska1(i, j) = 3";
_doska1[_i][_j] = (int) (3);
 //BA.debugLineNum = 724;BA.debugLine="DoskaCell (i, j, (8 - Doska(i, j)) +";
_doskacell(_i,_j,(int) ((8-_doska[_i][_j])+7));
 //BA.debugLineNum = 725;BA.debugLine="imv.Invalidate";
mostCurrent._imv.Invalidate();
 //BA.debugLineNum = 726;BA.debugLine="CloseCell = CloseCell - 1";
_closecell = (int) (_closecell-1);
 //BA.debugLineNum = 727;BA.debugLine="If CloseCell = KolMines Then Win";
if (_closecell==_kolmines) { 
_win();};
 //BA.debugLineNum = 728;BA.debugLine="If Doska(i, j) = 0 Then";
if (_doska[_i][_j]==0) { 
 //BA.debugLineNum = 729;BA.debugLine="empt = 1";
_empt = (int) (1);
 //BA.debugLineNum = 730;BA.debugLine="Do While empt > 0";
while (_empt>0) {
 //BA.debugLineNum = 731;BA.debugLine="empt = 0";
_empt = (int) (0);
 //BA.debugLineNum = 732;BA.debugLine="For i = 1 To SizeY";
{
final int step106 = 1;
final int limit106 = _sizey;
for (_i = (int) (1) ; (step106 > 0 && _i <= limit106) || (step106 < 0 && _i >= limit106); _i = ((int)(0 + _i + step106)) ) {
 //BA.debugLineNum = 733;BA.debugLine="For j = 1 To SizeX";
{
final int step107 = 1;
final int limit107 = _sizex;
for (_j = (int) (1) ; (step107 > 0 && _j <= limit107) || (step107 < 0 && _j >= limit107); _j = ((int)(0 + _j + step107)) ) {
 //BA.debugLineNum = 734;BA.debugLine="If Doska(i, j) = 0 And Doska";
if (_doska[_i][_j]==0 && _doska1[_i][_j]==3) { 
 //BA.debugLineNum = 735;BA.debugLine="For i1 = i - 1 To i + 1";
{
final int step109 = 1;
final int limit109 = (int) (_i+1);
for (_i1 = (int) (_i-1) ; (step109 > 0 && _i1 <= limit109) || (step109 < 0 && _i1 >= limit109); _i1 = ((int)(0 + _i1 + step109)) ) {
 //BA.debugLineNum = 736;BA.debugLine="For j1 = j - 1 To j + 1";
{
final int step110 = 1;
final int limit110 = (int) (_j+1);
for (_j1 = (int) (_j-1) ; (step110 > 0 && _j1 <= limit110) || (step110 < 0 && _j1 >= limit110); _j1 = ((int)(0 + _j1 + step110)) ) {
 //BA.debugLineNum = 737;BA.debugLine="If i1 >= 1 And i1 <= S";
if (_i1>=1 && _i1<=_sizey && _j1>=1 && _j1<=_sizex) { 
 //BA.debugLineNum = 738;BA.debugLine="If Doska1(i1, j1) =";
if (_doska1[_i1][_j1]==0) { 
 //BA.debugLineNum = 739;BA.debugLine="empt = empt + 1";
_empt = (int) (_empt+1);
 //BA.debugLineNum = 740;BA.debugLine="Doska1(i1, j1) = 3";
_doska1[_i1][_j1] = (int) (3);
 //BA.debugLineNum = 741;BA.debugLine="DoskaCell (i1, j1,";
_doskacell(_i1,_j1,(int) ((8-_doska[_i1][_j1])+7));
 //BA.debugLineNum = 742;BA.debugLine="imv.Invalidate";
mostCurrent._imv.Invalidate();
 //BA.debugLineNum = 743;BA.debugLine="CloseCell = CloseC";
_closecell = (int) (_closecell-1);
 //BA.debugLineNum = 744;BA.debugLine="If CloseCell = Kol";
if (_closecell==_kolmines) { 
_win();};
 };
 };
 }
};
 }
};
 };
 }
};
 }
};
 }
;
 };
 break; }
}
;
 };
 };
 };
 }else {
 //BA.debugLineNum = 760;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 };
 //BA.debugLineNum = 763;BA.debugLine="If CloseCell < SizeX * SizeY Then first = False";
if (_closecell<_sizex*_sizey) { 
_first = anywheresoftware.b4a.keywords.Common.False;};
 } 
       catch (Exception e138) {
			processBA.setLastException(e138); //BA.debugLineNum = 765;BA.debugLine="Log(LastException.Message)";
anywheresoftware.b4a.keywords.Common.Log(anywheresoftware.b4a.keywords.Common.LastException(mostCurrent.activityBA).getMessage());
 };
 //BA.debugLineNum = 767;BA.debugLine="End Sub";
return "";
}
public static String  _pz_longclick(dominex.example.pinchzoomandmove._touchdata _touchdata) throws Exception{
int _i = 0;
int _j = 0;
float _x = 0f;
float _y = 0f;
 //BA.debugLineNum = 769;BA.debugLine="Sub PZ_LongClick (TouchData As TouchData)";
 //BA.debugLineNum = 770;BA.debugLine="If WinOpen=False Then";
if (_winopen==anywheresoftware.b4a.keywords.Common.False) { 
 //BA.debugLineNum = 771;BA.debugLine="Dim i As Int, j As Int, x As Float,y As Float";
_i = 0;
_j = 0;
_x = 0f;
_y = 0f;
 //BA.debugLineNum = 772;BA.debugLine="xlong=TouchData.x+imv.Left";
_xlong = (float) (_touchdata.X+mostCurrent._imv.getLeft());
 //BA.debugLineNum = 773;BA.debugLine="ylong=TouchData.y+imv.top";
_ylong = (float) (_touchdata.Y+mostCurrent._imv.getTop());
 //BA.debugLineNum = 774;BA.debugLine="x=TouchData.x/(PZ(NumView).Zoom/100)";
_x = (float) (_touchdata.X/(double)(mostCurrent._pz[_numview]._getzoom()/(double)100));
 //BA.debugLineNum = 775;BA.debugLine="y=TouchData.y/(PZ(NumView).Zoom/100)";
_y = (float) (_touchdata.Y/(double)(mostCurrent._pz[_numview]._getzoom()/(double)100));
 //BA.debugLineNum = 776;BA.debugLine="If Not(GameOver) Then";
if (anywheresoftware.b4a.keywords.Common.Not(_gameover)) { 
 //BA.debugLineNum = 777;BA.debugLine="If DoskaCoord(x, y) Then";
if (_doskacoord(_x,_y)) { 
 //BA.debugLineNum = 778;BA.debugLine="XY2IJ (x, y, i, j)";
_xy2ij(_x,_y,_i,_j);
 //BA.debugLineNum = 779;BA.debugLine="i=mmm";
_i = _mmm;
 //BA.debugLineNum = 780;BA.debugLine="j=nnn";
_j = _nnn;
 //BA.debugLineNum = 781;BA.debugLine="Select Doska1(i, j)";
switch (BA.switchObjectToInt(_doska1[_i][_j],(int) (0),(int) (1),(int) (2))) {
case 0: {
 //BA.debugLineNum = 783;BA.debugLine="If ind(0).value >= 1 Then";
if (mostCurrent._ind[(int) (0)].value>=1) { 
 //BA.debugLineNum = 784;BA.debugLine="FlagT.Enabled=True";
mostCurrent._flagt.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 785;BA.debugLine="Doska1(i, j) = 1";
_doska1[_i][_j] = (int) (1);
 //BA.debugLineNum = 786;BA.debugLine="DoskaCell (i, j, 1)";
_doskacell(_i,_j,(int) (1));
 //BA.debugLineNum = 787;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 788;BA.debugLine="Indikator (0, ind(0).value - 1)";
_indikator((int) (0),(int) (mostCurrent._ind[(int) (0)].value-1));
 //BA.debugLineNum = 789;BA.debugLine="If SoundOn Then pv.Vibrate(100)";
if (_soundon) { 
mostCurrent._pv.Vibrate(processBA,(long) (100));};
 };
 break; }
case 1: {
 //BA.debugLineNum = 792;BA.debugLine="VoprT.Enabled=True";
mostCurrent._voprt.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 793;BA.debugLine="Doska1(i, j) = 2";
_doska1[_i][_j] = (int) (2);
 //BA.debugLineNum = 794;BA.debugLine="DoskaCell (i, j, 2)";
_doskacell(_i,_j,(int) (2));
 //BA.debugLineNum = 795;BA.debugLine="Indikator(0, ind(0).value + 1)";
_indikator((int) (0),(int) (mostCurrent._ind[(int) (0)].value+1));
 //BA.debugLineNum = 796;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 797;BA.debugLine="If SoundOn Then pv.Vibrate(100)";
if (_soundon) { 
mostCurrent._pv.Vibrate(processBA,(long) (100));};
 break; }
case 2: {
 //BA.debugLineNum = 799;BA.debugLine="Doska1(i, j) = 0";
_doska1[_i][_j] = (int) (0);
 //BA.debugLineNum = 800;BA.debugLine="DoskaCell (i, j, 0)";
_doskacell(_i,_j,(int) (0));
 //BA.debugLineNum = 801;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 802;BA.debugLine="If SoundOn Then pv.Vibrate(100)";
if (_soundon) { 
mostCurrent._pv.Vibrate(processBA,(long) (100));};
 break; }
}
;
 };
 };
 //BA.debugLineNum = 806;BA.debugLine="LongC=True";
_longc = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 807;BA.debugLine="If Not(Winn) And Not(lose) Then FaceBtn(4)";
if (anywheresoftware.b4a.keywords.Common.Not(_winn) && anywheresoftware.b4a.keywords.Common.Not(_lose)) { 
_facebtn((int) (4));};
 };
 //BA.debugLineNum = 809;BA.debugLine="End Sub";
return "";
}
public static String  _reset_click() throws Exception{
 //BA.debugLineNum = 1334;BA.debugLine="Sub Reset_Click";
 //BA.debugLineNum = 1335;BA.debugLine="Select Msgbox2(\"Are you sure?\", \"\", \"Yes\", \"Cance";
switch (BA.switchObjectToInt(anywheresoftware.b4a.keywords.Common.Msgbox2("Are you sure?","","Yes","Cancel","",(android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.Null),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DialogResponse.NEGATIVE,anywheresoftware.b4a.keywords.Common.DialogResponse.CANCEL,anywheresoftware.b4a.keywords.Common.DialogResponse.POSITIVE)) {
case 0: {
 //BA.debugLineNum = 1337;BA.debugLine="Return True";
if (true) return BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 1: {
 //BA.debugLineNum = 1339;BA.debugLine="Return True";
if (true) return BA.ObjectToString(anywheresoftware.b4a.keywords.Common.True);
 break; }
case 2: {
 //BA.debugLineNum = 1341;BA.debugLine="cnvs4.Initialize(Reset)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._reset.getObject()));
 //BA.debugLineNum = 1342;BA.debugLine="bmp.Initialize(File.DirAssets, \"ResetP.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"ResetP.png");
 //BA.debugLineNum = 1343;BA.debugLine="RectFon.Initialize(0,0,Reset.Width,Reset.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._reset.getWidth(),mostCurrent._reset.getHeight());
 //BA.debugLineNum = 1344;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 1345;BA.debugLine="Reset.Invalidate";
mostCurrent._reset.Invalidate();
 //BA.debugLineNum = 1346;BA.debugLine="beg.name=\"Anonymous\"";
mostCurrent._beg.name = "Anonymous";
 //BA.debugLineNum = 1347;BA.debugLine="beg.score=999";
mostCurrent._beg.score = (int) (999);
 //BA.debugLineNum = 1348;BA.debugLine="emm.name=\"Anonymous\"";
mostCurrent._emm.name = "Anonymous";
 //BA.debugLineNum = 1349;BA.debugLine="emm.score=999";
mostCurrent._emm.score = (int) (999);
 //BA.debugLineNum = 1350;BA.debugLine="exp.name=\"Anonymous\"";
mostCurrent._exp.name = "Anonymous";
 //BA.debugLineNum = 1351;BA.debugLine="exp.score=999";
mostCurrent._exp.score = (int) (999);
 //BA.debugLineNum = 1352;BA.debugLine="SetLabelTextSize(bs,beg.score&\" seconds\",100,1";
_setlabeltextsize(mostCurrent._bs,BA.NumberToString(mostCurrent._beg.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1353;BA.debugLine="SetLabelTextSize(bn,beg.name,100,1)";
_setlabeltextsize(mostCurrent._bn,mostCurrent._beg.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1354;BA.debugLine="SetLabelTextSize(iss,emm.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._iss,BA.NumberToString(mostCurrent._emm.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1355;BA.debugLine="SetLabelTextSize(In,emm.name,100,1)";
_setlabeltextsize(mostCurrent._in,mostCurrent._emm.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1356;BA.debugLine="SetLabelTextSize(es,exp.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._es,BA.NumberToString(mostCurrent._exp.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 1357;BA.debugLine="SetLabelTextSize(en,exp.name,100,1)";
_setlabeltextsize(mostCurrent._en,mostCurrent._exp.name,(float) (100),(float) (1));
 //BA.debugLineNum = 1358;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1359;BA.debugLine="es.Text=exp.score&\" seconds\"";
mostCurrent._es.setText((Object)(BA.NumberToString(mostCurrent._exp.score)+" seconds"));
 //BA.debugLineNum = 1360;BA.debugLine="en.Text=exp.name";
mostCurrent._en.setText((Object)(mostCurrent._exp.name));
 //BA.debugLineNum = 1361;BA.debugLine="iss.Text=emm.score&\" seconds\"";
mostCurrent._iss.setText((Object)(BA.NumberToString(mostCurrent._emm.score)+" seconds"));
 //BA.debugLineNum = 1362;BA.debugLine="In.Text=emm.name";
mostCurrent._in.setText((Object)(mostCurrent._emm.name));
 //BA.debugLineNum = 1363;BA.debugLine="bs.Text=beg.score&\" seconds\"";
mostCurrent._bs.setText((Object)(BA.NumberToString(mostCurrent._beg.score)+" seconds"));
 //BA.debugLineNum = 1364;BA.debugLine="bn.Text=beg.name";
mostCurrent._bn.setText((Object)(mostCurrent._beg.name));
 break; }
}
;
 //BA.debugLineNum = 1366;BA.debugLine="End Sub";
return "";
}
public static String  _rules_click() throws Exception{
 //BA.debugLineNum = 1389;BA.debugLine="Sub Rules_Click";
 //BA.debugLineNum = 1390;BA.debugLine="WinOpen=True";
_winopen = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 1391;BA.debugLine="lblText.Visible=True";
mostCurrent._lbltext.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1392;BA.debugLine="MenuAnim.Enabled=True";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 1393;BA.debugLine="End Sub";
return "";
}
public static String  _save() throws Exception{
anywheresoftware.b4a.objects.streams.File.TextWriterWrapper _textwriter1 = null;
 //BA.debugLineNum = 1289;BA.debugLine="Sub Save";
 //BA.debugLineNum = 1290;BA.debugLine="Dim TextWriter1 As TextWriter";
_textwriter1 = new anywheresoftware.b4a.objects.streams.File.TextWriterWrapper();
 //BA.debugLineNum = 1291;BA.debugLine="TextWriter1.Initialize(File.OpenOutput(File.Di";
_textwriter1.Initialize((java.io.OutputStream)(anywheresoftware.b4a.keywords.Common.File.OpenOutput(anywheresoftware.b4a.keywords.Common.File.getDirInternalCache(),"Pref.txt",anywheresoftware.b4a.keywords.Common.False).getObject()));
 //BA.debugLineNum = 1292;BA.debugLine="TextWriter1.WriteLine(Level)";
_textwriter1.WriteLine(BA.NumberToString(_level));
 //BA.debugLineNum = 1293;BA.debugLine="TextWriter1.WriteLine(beg.name)";
_textwriter1.WriteLine(mostCurrent._beg.name);
 //BA.debugLineNum = 1294;BA.debugLine="TextWriter1.WriteLine(beg.score)";
_textwriter1.WriteLine(BA.NumberToString(mostCurrent._beg.score));
 //BA.debugLineNum = 1295;BA.debugLine="TextWriter1.WriteLine(emm.name)";
_textwriter1.WriteLine(mostCurrent._emm.name);
 //BA.debugLineNum = 1296;BA.debugLine="TextWriter1.WriteLine(emm.score)";
_textwriter1.WriteLine(BA.NumberToString(mostCurrent._emm.score));
 //BA.debugLineNum = 1297;BA.debugLine="TextWriter1.WriteLine(exp.name)";
_textwriter1.WriteLine(mostCurrent._exp.name);
 //BA.debugLineNum = 1298;BA.debugLine="TextWriter1.WriteLine(exp.score)";
_textwriter1.WriteLine(BA.NumberToString(mostCurrent._exp.score));
 //BA.debugLineNum = 1299;BA.debugLine="TextWriter1.WriteLine(SizeX)";
_textwriter1.WriteLine(BA.NumberToString(_sizex));
 //BA.debugLineNum = 1300;BA.debugLine="TextWriter1.WriteLine(SizeY)";
_textwriter1.WriteLine(BA.NumberToString(_sizey));
 //BA.debugLineNum = 1301;BA.debugLine="TextWriter1.WriteLine(KolMines)";
_textwriter1.WriteLine(BA.NumberToString(_kolmines));
 //BA.debugLineNum = 1302;BA.debugLine="If SoundOn=True Then TextWriter1.WriteLine(\"1\") E";
if (_soundon==anywheresoftware.b4a.keywords.Common.True) { 
_textwriter1.WriteLine("1");}
else {
_textwriter1.WriteLine("0");};
 //BA.debugLineNum = 1303;BA.debugLine="TextWriter1.Close";
_textwriter1.Close();
 //BA.debugLineNum = 1304;BA.debugLine="End Sub";
return "";
}
public static String  _setedittextsize(anywheresoftware.b4a.objects.EditTextWrapper _lbl2,String _txt2,float _maxfontsize,float _minfontsize) throws Exception{
float _fontsize = 0f;
int _height = 0;
anywheresoftware.b4a.objects.StringUtils _stu = null;
 //BA.debugLineNum = 1407;BA.debugLine="Sub SetEditTextSize(lbl2 As EditText, txt2 As Stri";
 //BA.debugLineNum = 1408;BA.debugLine="Dim FontSize = MaxFontSize As Float";
_fontsize = _maxfontsize;
 //BA.debugLineNum = 1409;BA.debugLine="Dim Height As Int";
_height = 0;
 //BA.debugLineNum = 1410;BA.debugLine="Dim stu As StringUtils";
_stu = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 1412;BA.debugLine="lbl2.TextSize = FontSize";
_lbl2.setTextSize(_fontsize);
 //BA.debugLineNum = 1413;BA.debugLine="Height = stu.MeasureMultilineTextHeight(lbl2,";
_height = _stu.MeasureMultilineTextHeight((android.widget.TextView)(_lbl2.getObject()),_txt2);
 //BA.debugLineNum = 1414;BA.debugLine="Do While Height > lbl2.Height-3%x And FontSize";
while (_height>_lbl2.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (3),mostCurrent.activityBA) && _fontsize>_minfontsize) {
 //BA.debugLineNum = 1415;BA.debugLine="FontSize = FontSize - 1";
_fontsize = (float) (_fontsize-1);
 //BA.debugLineNum = 1416;BA.debugLine="lbl2.TextSize = FontSize";
_lbl2.setTextSize(_fontsize);
 //BA.debugLineNum = 1417;BA.debugLine="Height = stu.MeasureMultilineTextHeight(lb";
_height = _stu.MeasureMultilineTextHeight((android.widget.TextView)(_lbl2.getObject()),_txt2);
 }
;
 //BA.debugLineNum = 1419;BA.debugLine="End Sub";
return "";
}
public static String  _setlabeltextsize(anywheresoftware.b4a.objects.LabelWrapper _lbl2,String _txt2,float _maxfontsize,float _minfontsize) throws Exception{
float _fontsize = 0f;
int _height = 0;
anywheresoftware.b4a.objects.StringUtils _stu = null;
 //BA.debugLineNum = 1394;BA.debugLine="Sub SetLabelTextSize(lbl2 As Label, txt2 As String";
 //BA.debugLineNum = 1395;BA.debugLine="Dim FontSize = MaxFontSize As Float";
_fontsize = _maxfontsize;
 //BA.debugLineNum = 1396;BA.debugLine="Dim Height As Int";
_height = 0;
 //BA.debugLineNum = 1397;BA.debugLine="Dim stu As StringUtils";
_stu = new anywheresoftware.b4a.objects.StringUtils();
 //BA.debugLineNum = 1399;BA.debugLine="lbl2.TextSize = FontSize";
_lbl2.setTextSize(_fontsize);
 //BA.debugLineNum = 1400;BA.debugLine="Height = stu.MeasureMultilineTextHeight(lbl2,";
_height = _stu.MeasureMultilineTextHeight((android.widget.TextView)(_lbl2.getObject()),_txt2);
 //BA.debugLineNum = 1401;BA.debugLine="Do While Height > lbl2.Height And FontSize > M";
while (_height>_lbl2.getHeight() && _fontsize>_minfontsize) {
 //BA.debugLineNum = 1402;BA.debugLine="FontSize = FontSize - 1";
_fontsize = (float) (_fontsize-1);
 //BA.debugLineNum = 1403;BA.debugLine="lbl2.TextSize = FontSize";
_lbl2.setTextSize(_fontsize);
 //BA.debugLineNum = 1404;BA.debugLine="Height = stu.MeasureMultilineTextHeight(lb";
_height = _stu.MeasureMultilineTextHeight((android.widget.TextView)(_lbl2.getObject()),_txt2);
 }
;
 //BA.debugLineNum = 1406;BA.debugLine="End Sub";
return "";
}
public static String  _sound_click() throws Exception{
 //BA.debugLineNum = 1183;BA.debugLine="Sub Sound_Click";
 //BA.debugLineNum = 1184;BA.debugLine="SoundOn=Not(SoundOn)";
_soundon = anywheresoftware.b4a.keywords.Common.Not(_soundon);
 //BA.debugLineNum = 1185;BA.debugLine="If SoundOn Then Galka2.Visible=True Else Galka2.V";
if (_soundon) { 
mostCurrent._galka2.setVisible(anywheresoftware.b4a.keywords.Common.True);}
else {
mostCurrent._galka2.setVisible(anywheresoftware.b4a.keywords.Common.False);};
 //BA.debugLineNum = 1186;BA.debugLine="Save";
_save();
 //BA.debugLineNum = 1187;BA.debugLine="End Sub";
return "";
}
public static String  _timer1_tick() throws Exception{
 //BA.debugLineNum = 930;BA.debugLine="Sub Timer1_Tick";
 //BA.debugLineNum = 931;BA.debugLine="Indikator (1, ind(1).value + 1)";
_indikator((int) (1),(int) (mostCurrent._ind[(int) (1)].value+1));
 //BA.debugLineNum = 932;BA.debugLine="If SoundOn Then";
if (_soundon) { 
 //BA.debugLineNum = 933;BA.debugLine="MP.Load(File.DirAssets,\"time.wav\")";
mostCurrent._mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"time.wav");
 //BA.debugLineNum = 934;BA.debugLine="MP.Play";
mostCurrent._mp.Play();
 };
 //BA.debugLineNum = 936;BA.debugLine="End Sub";
return "";
}
public static String  _vistavlenie() throws Exception{
 //BA.debugLineNum = 362;BA.debugLine="Sub vistavlenie";
 //BA.debugLineNum = 363;BA.debugLine="Timer1.Initialize(\"Timer1\",1000)";
mostCurrent._timer1.Initialize(processBA,"Timer1",(long) (1000));
 //BA.debugLineNum = 364;BA.debugLine="Timer1.Enabled=False";
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 365;BA.debugLine="MenuAnim.Initialize(\"MenuAnim\",1)";
mostCurrent._menuanim.Initialize(processBA,"MenuAnim",(long) (1));
 //BA.debugLineNum = 366;BA.debugLine="MenuAnim.Enabled=False";
mostCurrent._menuanim.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 367;BA.debugLine="DoubleClick.Initialize(\"DoubleClick\",100)";
mostCurrent._doubleclick.Initialize(processBA,"DoubleClick",(long) (100));
 //BA.debugLineNum = 368;BA.debugLine="DoubleClick.Enabled=True";
mostCurrent._doubleclick.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 369;BA.debugLine="FaceTimer.Initialize(\"FaceTimer\",200)";
mostCurrent._facetimer.Initialize(processBA,"FaceTimer",(long) (200));
 //BA.debugLineNum = 370;BA.debugLine="FaceTimer.Enabled=False";
mostCurrent._facetimer.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 371;BA.debugLine="FlagT.Initialize(\"FlagT\",1)";
mostCurrent._flagt.Initialize(processBA,"FlagT",(long) (1));
 //BA.debugLineNum = 372;BA.debugLine="FlagT.Enabled=False";
mostCurrent._flagt.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 373;BA.debugLine="VoprT.Initialize(\"VoprT\",1)";
mostCurrent._voprt.Initialize(processBA,"VoprT",(long) (1));
 //BA.debugLineNum = 374;BA.debugLine="VoprT.Enabled=False";
mostCurrent._voprt.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 375;BA.debugLine="Flag.Width=30%x";
mostCurrent._flag.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 376;BA.debugLine="Flag.Height=30%x";
mostCurrent._flag.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 377;BA.debugLine="Vopr.Width=40%x";
mostCurrent._vopr.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (40),mostCurrent.activityBA));
 //BA.debugLineNum = 378;BA.debugLine="Vopr.Height=40%x";
mostCurrent._vopr.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (40),mostCurrent.activityBA));
 //BA.debugLineNum = 379;BA.debugLine="MP.Initialize2(\"MP\")";
mostCurrent._mp.Initialize2(processBA,"MP");
 //BA.debugLineNum = 381;BA.debugLine="New.Left=5%x";
mostCurrent._new.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 382;BA.debugLine="New.Top=28%x";
mostCurrent._new.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 383;BA.debugLine="New.Width=50%x";
mostCurrent._new.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 384;BA.debugLine="New.Height=14%x";
mostCurrent._new.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 385;BA.debugLine="cnvs4.Initialize(New)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._new.getObject()));
 //BA.debugLineNum = 386;BA.debugLine="bmp.Initialize(File.DirAssets, \"new.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"new.png");
 //BA.debugLineNum = 387;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 388;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 389;BA.debugLine="New.Invalidate";
mostCurrent._new.Invalidate();
 //BA.debugLineNum = 391;BA.debugLine="Beginner.Left=5%x";
mostCurrent._beginner.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 392;BA.debugLine="Beginner.Top=28%x";
mostCurrent._beginner.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 393;BA.debugLine="Beginner.Width=50%x";
mostCurrent._beginner.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 394;BA.debugLine="Beginner.Height=14%x";
mostCurrent._beginner.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 395;BA.debugLine="cnvs4.Initialize(Beginner)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._beginner.getObject()));
 //BA.debugLineNum = 396;BA.debugLine="bmp.Initialize(File.DirAssets, \"beginner.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"beginner.png");
 //BA.debugLineNum = 397;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 398;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 399;BA.debugLine="Beginner.Invalidate";
mostCurrent._beginner.Invalidate();
 //BA.debugLineNum = 401;BA.debugLine="Medium.Left=5%x";
mostCurrent._medium.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 402;BA.debugLine="Medium.Top=28%x";
mostCurrent._medium.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 403;BA.debugLine="Medium.Width=50%x";
mostCurrent._medium.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 404;BA.debugLine="Medium.Height=14%x";
mostCurrent._medium.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 405;BA.debugLine="cnvs4.Initialize(Medium)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._medium.getObject()));
 //BA.debugLineNum = 406;BA.debugLine="bmp.Initialize(File.DirAssets, \"medium.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"medium.png");
 //BA.debugLineNum = 407;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 408;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 409;BA.debugLine="Medium.Invalidate";
mostCurrent._medium.Invalidate();
 //BA.debugLineNum = 411;BA.debugLine="Expert.Left=5%x";
mostCurrent._expert.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 412;BA.debugLine="Expert.Top=28%x";
mostCurrent._expert.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 413;BA.debugLine="Expert.Width=50%x";
mostCurrent._expert.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 414;BA.debugLine="Expert.Height=14%x";
mostCurrent._expert.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 415;BA.debugLine="cnvs4.Initialize(Expert)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._expert.getObject()));
 //BA.debugLineNum = 416;BA.debugLine="bmp.Initialize(File.DirAssets, \"expert.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"expert.png");
 //BA.debugLineNum = 417;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 418;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 419;BA.debugLine="Expert.Invalidate";
mostCurrent._expert.Invalidate();
 //BA.debugLineNum = 421;BA.debugLine="Custom.Left=5%x";
mostCurrent._custom.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 422;BA.debugLine="Custom.Top=28%x";
mostCurrent._custom.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 423;BA.debugLine="Custom.Width=50%x";
mostCurrent._custom.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 424;BA.debugLine="Custom.Height=14%x";
mostCurrent._custom.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 425;BA.debugLine="cnvs4.Initialize(Custom)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._custom.getObject()));
 //BA.debugLineNum = 426;BA.debugLine="bmp.Initialize(File.DirAssets, \"custom.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"custom.png");
 //BA.debugLineNum = 427;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 428;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 429;BA.debugLine="Custom.Invalidate";
mostCurrent._custom.Invalidate();
 //BA.debugLineNum = 431;BA.debugLine="Sound.Left=5%x";
mostCurrent._sound.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 432;BA.debugLine="Sound.Top=28%x";
mostCurrent._sound.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 433;BA.debugLine="Sound.Width=50%x";
mostCurrent._sound.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 434;BA.debugLine="Sound.Height=14%x";
mostCurrent._sound.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 435;BA.debugLine="cnvs4.Initialize(Sound)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._sound.getObject()));
 //BA.debugLineNum = 436;BA.debugLine="bmp.Initialize(File.DirAssets, \"sound.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"sound.png");
 //BA.debugLineNum = 437;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 438;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 439;BA.debugLine="Sound.Invalidate";
mostCurrent._sound.Invalidate();
 //BA.debugLineNum = 441;BA.debugLine="Leaderboard.Left=5%x";
mostCurrent._leaderboard.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 442;BA.debugLine="Leaderboard.Top=28%x";
mostCurrent._leaderboard.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 443;BA.debugLine="Leaderboard.Width=50%x";
mostCurrent._leaderboard.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 444;BA.debugLine="Leaderboard.Height=14%x";
mostCurrent._leaderboard.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 445;BA.debugLine="cnvs4.Initialize(Leaderboard)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._leaderboard.getObject()));
 //BA.debugLineNum = 446;BA.debugLine="bmp.Initialize(File.DirAssets, \"leaderboard.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"leaderboard.png");
 //BA.debugLineNum = 447;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 448;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 449;BA.debugLine="Leaderboard.Invalidate";
mostCurrent._leaderboard.Invalidate();
 //BA.debugLineNum = 451;BA.debugLine="Vopr.Bitmap=LoadBitmap(File.DirAssets,\"VoprXP.png\"";
mostCurrent._vopr.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"VoprXP.png").getObject()));
 //BA.debugLineNum = 452;BA.debugLine="Flag.Bitmap=LoadBitmap(File.DirAssets,\"FlagXP.png\"";
mostCurrent._flag.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"FlagXP.png").getObject()));
 //BA.debugLineNum = 454;BA.debugLine="Rules.Left=25%x";
mostCurrent._rules.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA));
 //BA.debugLineNum = 455;BA.debugLine="Rules.Top=28%x";
mostCurrent._rules.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 456;BA.debugLine="Rules.Width=50%x";
mostCurrent._rules.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 457;BA.debugLine="Rules.Height=14%x";
mostCurrent._rules.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 458;BA.debugLine="cnvs4.Initialize(Rules)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._rules.getObject()));
 //BA.debugLineNum = 459;BA.debugLine="bmp.Initialize(File.DirAssets, \"howtoplay.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"howtoplay.png");
 //BA.debugLineNum = 460;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 461;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 462;BA.debugLine="Rules.Invalidate";
mostCurrent._rules.Invalidate();
 //BA.debugLineNum = 464;BA.debugLine="About.Left=25%x";
mostCurrent._about.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (25),mostCurrent.activityBA));
 //BA.debugLineNum = 465;BA.debugLine="About.Top=28%x";
mostCurrent._about.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (28),mostCurrent.activityBA));
 //BA.debugLineNum = 466;BA.debugLine="About.Width=50%x";
mostCurrent._about.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA));
 //BA.debugLineNum = 467;BA.debugLine="About.Height=14%x";
mostCurrent._about.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (14),mostCurrent.activityBA));
 //BA.debugLineNum = 468;BA.debugLine="cnvs4.Initialize(About)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._about.getObject()));
 //BA.debugLineNum = 469;BA.debugLine="bmp.Initialize(File.DirAssets, \"about.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"about.png");
 //BA.debugLineNum = 470;BA.debugLine="RectFon.Initialize(0,0,New.Width,New.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._new.getWidth(),mostCurrent._new.getHeight());
 //BA.debugLineNum = 471;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 472;BA.debugLine="About.Invalidate";
mostCurrent._about.Invalidate();
 //BA.debugLineNum = 475;BA.debugLine="Galka.Width=3%x";
mostCurrent._galka.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (3),mostCurrent.activityBA));
 //BA.debugLineNum = 476;BA.debugLine="Galka.Height=12%x";
mostCurrent._galka.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (12),mostCurrent.activityBA));
 //BA.debugLineNum = 477;BA.debugLine="Galka.Left=7%x";
mostCurrent._galka.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (7),mostCurrent.activityBA));
 //BA.debugLineNum = 478;BA.debugLine="cnvs4.Initialize(Galka)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._galka.getObject()));
 //BA.debugLineNum = 479;BA.debugLine="bmp.Initialize(File.DirAssets, \"galka.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"galka.png");
 //BA.debugLineNum = 480;BA.debugLine="RectFon.Initialize(0,0,Galka.Width,Galka.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._galka.getWidth(),mostCurrent._galka.getHeight());
 //BA.debugLineNum = 481;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 482;BA.debugLine="Galka.Invalidate";
mostCurrent._galka.Invalidate();
 //BA.debugLineNum = 483;BA.debugLine="Galka2.Width=3%x";
mostCurrent._galka2.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (3),mostCurrent.activityBA));
 //BA.debugLineNum = 484;BA.debugLine="Galka2.Height=12%x";
mostCurrent._galka2.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (12),mostCurrent.activityBA));
 //BA.debugLineNum = 485;BA.debugLine="Galka2.Left=7%x";
mostCurrent._galka2.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (7),mostCurrent.activityBA));
 //BA.debugLineNum = 486;BA.debugLine="cnvs4.Initialize(Galka2)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._galka2.getObject()));
 //BA.debugLineNum = 487;BA.debugLine="bmp.Initialize(File.DirAssets, \"galka.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"galka.png");
 //BA.debugLineNum = 488;BA.debugLine="RectFon.Initialize(0,0,Galka2.Width,Galka2.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._galka2.getWidth(),mostCurrent._galka2.getHeight());
 //BA.debugLineNum = 489;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 490;BA.debugLine="Galka2.Invalidate";
mostCurrent._galka2.Invalidate();
 //BA.debugLineNum = 491;BA.debugLine="Mine2.Left=0";
mostCurrent._mine2.setLeft((int) (0));
 //BA.debugLineNum = 492;BA.debugLine="Mine2.Top=0";
mostCurrent._mine2.setTop((int) (0));
 //BA.debugLineNum = 493;BA.debugLine="Mine2.Height=33%x";
mostCurrent._mine2.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (33),mostCurrent.activityBA));
 //BA.debugLineNum = 494;BA.debugLine="Mine2.Width=100%x";
mostCurrent._mine2.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 496;BA.debugLine="CustomPanel.Width=100%x";
mostCurrent._custompanel.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 497;BA.debugLine="CustomPanel.Height=100%y";
mostCurrent._custompanel.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 498;BA.debugLine="CustomPanel.Left=0";
mostCurrent._custompanel.setLeft((int) (0));
 //BA.debugLineNum = 499;BA.debugLine="CustomPanel.Top=50%y-CustomPanel.Height/2";
mostCurrent._custompanel.setTop((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA)-mostCurrent._custompanel.getHeight()/(double)2));
 //BA.debugLineNum = 500;BA.debugLine="BestPanel.Width=100%x";
mostCurrent._bestpanel.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 501;BA.debugLine="BestPanel.Height=100%y";
mostCurrent._bestpanel.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 502;BA.debugLine="BestPanel.Left=0";
mostCurrent._bestpanel.setLeft((int) (0));
 //BA.debugLineNum = 503;BA.debugLine="BestPanel.Top=50%y-BestPanel.Height/2";
mostCurrent._bestpanel.setTop((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA)-mostCurrent._bestpanel.getHeight()/(double)2));
 //BA.debugLineNum = 504;BA.debugLine="dlgrec.Width=100%x";
mostCurrent._dlgrec.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 505;BA.debugLine="dlgrec.Height=100%y";
mostCurrent._dlgrec.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 506;BA.debugLine="dlgrec.Left=0";
mostCurrent._dlgrec.setLeft((int) (0));
 //BA.debugLineNum = 507;BA.debugLine="dlgrec.Top=50%y-dlgrec.Height/2";
mostCurrent._dlgrec.setTop((int) (anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (50),mostCurrent.activityBA)-mostCurrent._dlgrec.getHeight()/(double)2));
 //BA.debugLineNum = 509;BA.debugLine="bs.Width=27%x";
mostCurrent._bs.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 510;BA.debugLine="bs.Height=11%x";
mostCurrent._bs.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (11),mostCurrent.activityBA));
 //BA.debugLineNum = 511;BA.debugLine="bs.Left=38%x";
mostCurrent._bs.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (38),mostCurrent.activityBA));
 //BA.debugLineNum = 512;BA.debugLine="bs.top=52%x";
mostCurrent._bs.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (52),mostCurrent.activityBA));
 //BA.debugLineNum = 513;BA.debugLine="bn.Width=30%x";
mostCurrent._bn.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 514;BA.debugLine="bn.Height=10%x";
mostCurrent._bn.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 515;BA.debugLine="bn.Left=65%x";
mostCurrent._bn.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (65),mostCurrent.activityBA));
 //BA.debugLineNum = 516;BA.debugLine="bn.top=52%x";
mostCurrent._bn.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (52),mostCurrent.activityBA));
 //BA.debugLineNum = 517;BA.debugLine="SetLabelTextSize(bs,beg.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._bs,BA.NumberToString(mostCurrent._beg.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 518;BA.debugLine="SetLabelTextSize(bn,beg.name,100,1)";
_setlabeltextsize(mostCurrent._bn,mostCurrent._beg.name,(float) (100),(float) (1));
 //BA.debugLineNum = 519;BA.debugLine="bs.Text=beg.score&\" seconds\"";
mostCurrent._bs.setText((Object)(BA.NumberToString(mostCurrent._beg.score)+" seconds"));
 //BA.debugLineNum = 520;BA.debugLine="bn.Text=beg.name";
mostCurrent._bn.setText((Object)(mostCurrent._beg.name));
 //BA.debugLineNum = 522;BA.debugLine="iss.Width=27%x";
mostCurrent._iss.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 523;BA.debugLine="iss.Height=11%x";
mostCurrent._iss.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (11),mostCurrent.activityBA));
 //BA.debugLineNum = 524;BA.debugLine="iss.Left=38%x";
mostCurrent._iss.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (38),mostCurrent.activityBA));
 //BA.debugLineNum = 525;BA.debugLine="iss.top=71%x";
mostCurrent._iss.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (71),mostCurrent.activityBA));
 //BA.debugLineNum = 526;BA.debugLine="In.Width=30%x";
mostCurrent._in.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 527;BA.debugLine="In.Height=10%x";
mostCurrent._in.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 528;BA.debugLine="In.Left=65%x";
mostCurrent._in.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (65),mostCurrent.activityBA));
 //BA.debugLineNum = 529;BA.debugLine="In.top=71%x";
mostCurrent._in.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (71),mostCurrent.activityBA));
 //BA.debugLineNum = 530;BA.debugLine="SetLabelTextSize(iss,emm.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._iss,BA.NumberToString(mostCurrent._emm.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 531;BA.debugLine="SetLabelTextSize(In,emm.name,100,1)";
_setlabeltextsize(mostCurrent._in,mostCurrent._emm.name,(float) (100),(float) (1));
 //BA.debugLineNum = 532;BA.debugLine="iss.Text=emm.score&\" seconds\"";
mostCurrent._iss.setText((Object)(BA.NumberToString(mostCurrent._emm.score)+" seconds"));
 //BA.debugLineNum = 533;BA.debugLine="In.Text=emm.name";
mostCurrent._in.setText((Object)(mostCurrent._emm.name));
 //BA.debugLineNum = 535;BA.debugLine="es.Width=27%x";
mostCurrent._es.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (27),mostCurrent.activityBA));
 //BA.debugLineNum = 536;BA.debugLine="es.Height=11%x";
mostCurrent._es.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (11),mostCurrent.activityBA));
 //BA.debugLineNum = 537;BA.debugLine="es.Left=38%x";
mostCurrent._es.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (38),mostCurrent.activityBA));
 //BA.debugLineNum = 538;BA.debugLine="es.top=90%x";
mostCurrent._es.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA));
 //BA.debugLineNum = 539;BA.debugLine="en.Width=30%x";
mostCurrent._en.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 540;BA.debugLine="en.Height=10%x";
mostCurrent._en.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 541;BA.debugLine="en.Left=65%x";
mostCurrent._en.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (65),mostCurrent.activityBA));
 //BA.debugLineNum = 542;BA.debugLine="en.top=90%x";
mostCurrent._en.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA));
 //BA.debugLineNum = 543;BA.debugLine="SetLabelTextSize(es,exp.score&\" seconds\",100,1)";
_setlabeltextsize(mostCurrent._es,BA.NumberToString(mostCurrent._exp.score)+" seconds",(float) (100),(float) (1));
 //BA.debugLineNum = 544;BA.debugLine="SetLabelTextSize(en,exp.name,100,1)";
_setlabeltextsize(mostCurrent._en,mostCurrent._exp.name,(float) (100),(float) (1));
 //BA.debugLineNum = 545;BA.debugLine="es.Text=exp.score&\" seconds\"";
mostCurrent._es.setText((Object)(BA.NumberToString(mostCurrent._exp.score)+" seconds"));
 //BA.debugLineNum = 546;BA.debugLine="en.Text=exp.name";
mostCurrent._en.setText((Object)(mostCurrent._exp.name));
 //BA.debugLineNum = 548;BA.debugLine="OK.Width=30%x";
mostCurrent._ok.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 549;BA.debugLine="OK.Height=13%x";
mostCurrent._ok.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (13),mostCurrent.activityBA));
 //BA.debugLineNum = 550;BA.debugLine="OK.Left=60%x";
mostCurrent._ok.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA));
 //BA.debugLineNum = 551;BA.debugLine="OK.Top=62%x";
mostCurrent._ok.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (62),mostCurrent.activityBA));
 //BA.debugLineNum = 552;BA.debugLine="cnvs4.Initialize(OK)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._ok.getObject()));
 //BA.debugLineNum = 553;BA.debugLine="bmp.Initialize(File.DirAssets, \"OK.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"OK.png");
 //BA.debugLineNum = 554;BA.debugLine="RectFon.Initialize(0,0,OK.Width,OK.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._ok.getWidth(),mostCurrent._ok.getHeight());
 //BA.debugLineNum = 555;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 556;BA.debugLine="OK.Invalidate";
mostCurrent._ok.Invalidate();
 //BA.debugLineNum = 557;BA.debugLine="OK3.Width=30%x";
mostCurrent._ok3.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 558;BA.debugLine="OK3.Height=13%x";
mostCurrent._ok3.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (13),mostCurrent.activityBA));
 //BA.debugLineNum = 559;BA.debugLine="OK3.Left=35%x";
mostCurrent._ok3.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (35),mostCurrent.activityBA));
 //BA.debugLineNum = 560;BA.debugLine="OK3.Top=90%x";
mostCurrent._ok3.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA));
 //BA.debugLineNum = 561;BA.debugLine="cnvs4.Initialize(OK3)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._ok3.getObject()));
 //BA.debugLineNum = 562;BA.debugLine="bmp.Initialize(File.DirAssets, \"OK.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"OK.png");
 //BA.debugLineNum = 563;BA.debugLine="RectFon.Initialize(0,0,OK3.Width,OK3.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._ok3.getWidth(),mostCurrent._ok3.getHeight());
 //BA.debugLineNum = 564;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 565;BA.debugLine="OK3.Invalidate";
mostCurrent._ok3.Invalidate();
 //BA.debugLineNum = 566;BA.debugLine="OK2.Width=30%x";
mostCurrent._ok2.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 567;BA.debugLine="OK2.Height=13%x";
mostCurrent._ok2.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (13),mostCurrent.activityBA));
 //BA.debugLineNum = 568;BA.debugLine="OK2.Left=60%x";
mostCurrent._ok2.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA));
 //BA.debugLineNum = 569;BA.debugLine="OK2.Top=102%x";
mostCurrent._ok2.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (102),mostCurrent.activityBA));
 //BA.debugLineNum = 570;BA.debugLine="Reset.Width=40%x";
mostCurrent._reset.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (40),mostCurrent.activityBA));
 //BA.debugLineNum = 571;BA.debugLine="Reset.Height=13%x";
mostCurrent._reset.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (13),mostCurrent.activityBA));
 //BA.debugLineNum = 572;BA.debugLine="Reset.Left=15%x";
mostCurrent._reset.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA));
 //BA.debugLineNum = 573;BA.debugLine="Reset.Top=102%x";
mostCurrent._reset.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (102),mostCurrent.activityBA));
 //BA.debugLineNum = 574;BA.debugLine="cnvs4.Initialize(OK2)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._ok2.getObject()));
 //BA.debugLineNum = 575;BA.debugLine="bmp.Initialize(File.DirAssets, \"OK.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"OK.png");
 //BA.debugLineNum = 576;BA.debugLine="RectFon.Initialize(0,0,OK2.Width,OK2.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._ok2.getWidth(),mostCurrent._ok2.getHeight());
 //BA.debugLineNum = 577;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 578;BA.debugLine="OK2.Invalidate";
mostCurrent._ok2.Invalidate();
 //BA.debugLineNum = 579;BA.debugLine="cnvs4.Initialize(Reset)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._reset.getObject()));
 //BA.debugLineNum = 580;BA.debugLine="bmp.Initialize(File.DirAssets, \"Reset.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Reset.png");
 //BA.debugLineNum = 581;BA.debugLine="RectFon.Initialize(0,0,Reset.Width,Reset.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._reset.getWidth(),mostCurrent._reset.getHeight());
 //BA.debugLineNum = 582;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 583;BA.debugLine="Reset.Invalidate";
mostCurrent._reset.Invalidate();
 //BA.debugLineNum = 584;BA.debugLine="Cancel.Width=30%x";
mostCurrent._cancel.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 585;BA.debugLine="Cancel.Height=13%x";
mostCurrent._cancel.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (13),mostCurrent.activityBA));
 //BA.debugLineNum = 586;BA.debugLine="Cancel.Left=60%x";
mostCurrent._cancel.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA));
 //BA.debugLineNum = 587;BA.debugLine="Cancel.Top=82%x";
mostCurrent._cancel.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (82),mostCurrent.activityBA));
 //BA.debugLineNum = 588;BA.debugLine="cnvs4.Initialize(Cancel)";
mostCurrent._cnvs4.Initialize((android.view.View)(mostCurrent._cancel.getObject()));
 //BA.debugLineNum = 589;BA.debugLine="bmp.Initialize(File.DirAssets, \"Cancel.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Cancel.png");
 //BA.debugLineNum = 590;BA.debugLine="RectFon.Initialize(0,0,OK.Width,OK.Height)";
mostCurrent._rectfon.Initialize((int) (0),(int) (0),mostCurrent._ok.getWidth(),mostCurrent._ok.getHeight());
 //BA.debugLineNum = 591;BA.debugLine="cnvs4.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs4.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 592;BA.debugLine="Cancel.Invalidate";
mostCurrent._cancel.Invalidate();
 //BA.debugLineNum = 594;BA.debugLine="H.Width=20%x";
mostCurrent._h.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA));
 //BA.debugLineNum = 595;BA.debugLine="H.Height=10%x";
mostCurrent._h.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 596;BA.debugLine="H.Top=60%x";
mostCurrent._h.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA));
 //BA.debugLineNum = 597;BA.debugLine="H.Left=35%x";
mostCurrent._h.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (35),mostCurrent.activityBA));
 //BA.debugLineNum = 598;BA.debugLine="H.Color=Colors.White";
mostCurrent._h.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 599;BA.debugLine="W.Width=20%x";
mostCurrent._w.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA));
 //BA.debugLineNum = 600;BA.debugLine="W.Height=10%x";
mostCurrent._w.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 601;BA.debugLine="W.Top=73%x";
mostCurrent._w.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (73),mostCurrent.activityBA));
 //BA.debugLineNum = 602;BA.debugLine="W.Left=35%x";
mostCurrent._w.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (35),mostCurrent.activityBA));
 //BA.debugLineNum = 603;BA.debugLine="W.Color=Colors.White";
mostCurrent._w.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 604;BA.debugLine="Mine.Width=20%x";
mostCurrent._mine.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA));
 //BA.debugLineNum = 605;BA.debugLine="Mine.Height=10%x";
mostCurrent._mine.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (10),mostCurrent.activityBA));
 //BA.debugLineNum = 606;BA.debugLine="Mine.Top=86%x";
mostCurrent._mine.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (86),mostCurrent.activityBA));
 //BA.debugLineNum = 607;BA.debugLine="Mine.Left=35%x";
mostCurrent._mine.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (35),mostCurrent.activityBA));
 //BA.debugLineNum = 608;BA.debugLine="Mine.Color=Colors.White";
mostCurrent._mine.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 609;BA.debugLine="EditText1.Width=90%x";
mostCurrent._edittext1.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (90),mostCurrent.activityBA));
 //BA.debugLineNum = 610;BA.debugLine="EditText1.Height=15%x";
mostCurrent._edittext1.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (15),mostCurrent.activityBA));
 //BA.debugLineNum = 611;BA.debugLine="EditText1.Left=5%x";
mostCurrent._edittext1.setLeft(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 612;BA.debugLine="EditText1.Top=65%x";
mostCurrent._edittext1.setTop(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (65),mostCurrent.activityBA));
 //BA.debugLineNum = 613;BA.debugLine="EditText1.Color=Colors.White";
mostCurrent._edittext1.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 614;BA.debugLine="lblText.Height=100%y";
mostCurrent._lbltext.setHeight(anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 615;BA.debugLine="lblText.Width=100%x";
mostCurrent._lbltext.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA));
 //BA.debugLineNum = 616;BA.debugLine="lblText.Left=0";
mostCurrent._lbltext.setLeft((int) (0));
 //BA.debugLineNum = 617;BA.debugLine="lblText.Top=0";
mostCurrent._lbltext.setTop((int) (0));
 //BA.debugLineNum = 618;BA.debugLine="SetLabelTextSize(lblText,File.GetText(File.DirAsse";
_setlabeltextsize(mostCurrent._lbltext,anywheresoftware.b4a.keywords.Common.File.GetText(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Minesweeper instruction.txt"),(float) (100),(float) (1));
 //BA.debugLineNum = 619;BA.debugLine="lblText.Text=File.GetText(File.DirAssets,\"Mineswee";
mostCurrent._lbltext.setText((Object)(anywheresoftware.b4a.keywords.Common.File.GetText(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Minesweeper instruction.txt")));
 //BA.debugLineNum = 620;BA.debugLine="End Sub";
return "";
}
public static String  _voprt_tick() throws Exception{
 //BA.debugLineNum = 968;BA.debugLine="Sub VoprT_Tick";
 //BA.debugLineNum = 969;BA.debugLine="Vopr.Visible=True";
mostCurrent._vopr.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 970;BA.debugLine="Vopr.Width=Vopr.Width-1%x";
mostCurrent._vopr.setWidth((int) (mostCurrent._vopr.getWidth()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (1),mostCurrent.activityBA)));
 //BA.debugLineNum = 971;BA.debugLine="Vopr.Height=Vopr.Height-1%x";
mostCurrent._vopr.setHeight((int) (mostCurrent._vopr.getHeight()-anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (1),mostCurrent.activityBA)));
 //BA.debugLineNum = 972;BA.debugLine="Vopr.Top=ylong-Vopr.Height/2";
mostCurrent._vopr.setTop((int) (_ylong-mostCurrent._vopr.getHeight()/(double)2));
 //BA.debugLineNum = 973;BA.debugLine="Vopr.left=xlong-Vopr.Width/2";
mostCurrent._vopr.setLeft((int) (_xlong-mostCurrent._vopr.getWidth()/(double)2));
 //BA.debugLineNum = 974;BA.debugLine="If Vopr.Width<=0 Then";
if (mostCurrent._vopr.getWidth()<=0) { 
 //BA.debugLineNum = 975;BA.debugLine="VoprT.Enabled=False";
mostCurrent._voprt.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 976;BA.debugLine="Vopr.Visible=False";
mostCurrent._vopr.setVisible(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 977;BA.debugLine="Vopr.Width=40%x";
mostCurrent._vopr.setWidth(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (40),mostCurrent.activityBA));
 //BA.debugLineNum = 978;BA.debugLine="Vopr.Height=40%x";
mostCurrent._vopr.setHeight(anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (40),mostCurrent.activityBA));
 };
 //BA.debugLineNum = 980;BA.debugLine="End Sub";
return "";
}
public static String  _win() throws Exception{
int _i = 0;
int _j = 0;
 //BA.debugLineNum = 831;BA.debugLine="Sub Win";
 //BA.debugLineNum = 832;BA.debugLine="Dim i As Int, j As Int";
_i = 0;
_j = 0;
 //BA.debugLineNum = 833;BA.debugLine="FaceBtn(1)";
_facebtn((int) (1));
 //BA.debugLineNum = 834;BA.debugLine="If SoundOn Then";
if (_soundon) { 
 //BA.debugLineNum = 835;BA.debugLine="MP.Load(File.DirAssets,\"win.wav\")";
mostCurrent._mp.Load(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"win.wav");
 //BA.debugLineNum = 836;BA.debugLine="MP.Play";
mostCurrent._mp.Play();
 };
 //BA.debugLineNum = 838;BA.debugLine="Winn=True";
_winn = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 840;BA.debugLine="For i = 1 To SizeY";
{
final int step8 = 1;
final int limit8 = _sizey;
for (_i = (int) (1) ; (step8 > 0 && _i <= limit8) || (step8 < 0 && _i >= limit8); _i = ((int)(0 + _i + step8)) ) {
 //BA.debugLineNum = 841;BA.debugLine="For j = 1 To SizeX";
{
final int step9 = 1;
final int limit9 = _sizex;
for (_j = (int) (1) ; (step9 > 0 && _j <= limit9) || (step9 < 0 && _j >= limit9); _j = ((int)(0 + _j + step9)) ) {
 //BA.debugLineNum = 842;BA.debugLine="If Doska1(i, j) < 3 Then";
if (_doska1[_i][_j]<3) { 
 //BA.debugLineNum = 843;BA.debugLine="Doska1(i, j) = 1";
_doska1[_i][_j] = (int) (1);
 //BA.debugLineNum = 844;BA.debugLine="DoskaCell (i, j, 1)";
_doskacell(_i,_j,(int) (1));
 //BA.debugLineNum = 845;BA.debugLine="Activity.Invalidate";
mostCurrent._activity.Invalidate();
 //BA.debugLineNum = 846;BA.debugLine="Indikator (0, 0)";
_indikator((int) (0),(int) (0));
 };
 }
};
 }
};
 //BA.debugLineNum = 850;BA.debugLine="FaceBtn (1)";
_facebtn((int) (1));
 //BA.debugLineNum = 851;BA.debugLine="GameOver = True";
_gameover = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 852;BA.debugLine="Timer1.Enabled = False";
mostCurrent._timer1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 853;BA.debugLine="If (Level<3) Then";
if ((_level<3)) { 
 //BA.debugLineNum = 854;BA.debugLine="Select Level";
switch (_level) {
case 0: {
 //BA.debugLineNum = 856;BA.debugLine="If ind(1).value < beg.score Then";
if (mostCurrent._ind[(int) (1)].value<mostCurrent._beg.score) { 
 //BA.debugLineNum = 857;BA.debugLine="beg.score=ind(1).value";
mostCurrent._beg.score = mostCurrent._ind[(int) (1)].value;
 //BA.debugLineNum = 858;BA.debugLine="cnvs3.Initialize(BestPanel)";
mostCurrent._cnvs3.Initialize((android.view.View)(mostCurrent._bestpanel.getObject()));
 //BA.debugLineNum = 859;BA.debugLine="bmp.Initialize(File.DirAssets, \"Bestb.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Bestb.png");
 //BA.debugLineNum = 860;BA.debugLine="RectFon.Initialize(0,20%x,CustomPanel.Width,120%x)";
mostCurrent._rectfon.Initialize((int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),mostCurrent._custompanel.getWidth(),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (120),mostCurrent.activityBA));
 //BA.debugLineNum = 861;BA.debugLine="cnvs3.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs3.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 862;BA.debugLine="BestPanel.Invalidate";
mostCurrent._bestpanel.Invalidate();
 //BA.debugLineNum = 863;BA.debugLine="BestPanel.Visible=True";
mostCurrent._bestpanel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 864;BA.debugLine="WinOpen=True";
_winopen = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 865;BA.debugLine="SetEditTextSize(EditText1,\"Anonymous\",100,1)";
_setedittextsize(mostCurrent._edittext1,"Anonymous",(float) (100),(float) (1));
 //BA.debugLineNum = 866;BA.debugLine="EditText1.Text=\"Anonymous\"";
mostCurrent._edittext1.setText((Object)("Anonymous"));
 };
 break; }
case 1: {
 //BA.debugLineNum = 869;BA.debugLine="If ind(1).value <emm.score Then";
if (mostCurrent._ind[(int) (1)].value<mostCurrent._emm.score) { 
 //BA.debugLineNum = 870;BA.debugLine="emm.score=ind(1).value";
mostCurrent._emm.score = mostCurrent._ind[(int) (1)].value;
 //BA.debugLineNum = 871;BA.debugLine="cnvs3.Initialize(BestPanel)";
mostCurrent._cnvs3.Initialize((android.view.View)(mostCurrent._bestpanel.getObject()));
 //BA.debugLineNum = 872;BA.debugLine="bmp.Initialize(File.DirAssets, \"Besti.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Besti.png");
 //BA.debugLineNum = 873;BA.debugLine="RectFon.Initialize(0,20%x,CustomPanel.Width,120%x)";
mostCurrent._rectfon.Initialize((int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),mostCurrent._custompanel.getWidth(),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (120),mostCurrent.activityBA));
 //BA.debugLineNum = 874;BA.debugLine="cnvs3.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs3.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 875;BA.debugLine="BestPanel.Invalidate";
mostCurrent._bestpanel.Invalidate();
 //BA.debugLineNum = 876;BA.debugLine="BestPanel.Visible=True";
mostCurrent._bestpanel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 877;BA.debugLine="WinOpen=True";
_winopen = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 878;BA.debugLine="SetEditTextSize(EditText1,\"Anonymous\",100,1)";
_setedittextsize(mostCurrent._edittext1,"Anonymous",(float) (100),(float) (1));
 //BA.debugLineNum = 879;BA.debugLine="EditText1.Text=\"Anonymous\"";
mostCurrent._edittext1.setText((Object)("Anonymous"));
 };
 break; }
case 2: {
 //BA.debugLineNum = 882;BA.debugLine="If ind(1).value <exp.score Then";
if (mostCurrent._ind[(int) (1)].value<mostCurrent._exp.score) { 
 //BA.debugLineNum = 883;BA.debugLine="exp.score=ind(1).value";
mostCurrent._exp.score = mostCurrent._ind[(int) (1)].value;
 //BA.debugLineNum = 884;BA.debugLine="cnvs3.Initialize(BestPanel)";
mostCurrent._cnvs3.Initialize((android.view.View)(mostCurrent._bestpanel.getObject()));
 //BA.debugLineNum = 885;BA.debugLine="bmp.Initialize(File.DirAssets, \"Beste.png\")";
mostCurrent._bmp.Initialize(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"Beste.png");
 //BA.debugLineNum = 886;BA.debugLine="RectFon.Initialize(0,20%x,CustomPanel.Width,120%x)";
mostCurrent._rectfon.Initialize((int) (0),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),mostCurrent._custompanel.getWidth(),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (120),mostCurrent.activityBA));
 //BA.debugLineNum = 887;BA.debugLine="cnvs3.DrawBitmap(bmp,Null,RectFon)";
mostCurrent._cnvs3.DrawBitmap((android.graphics.Bitmap)(mostCurrent._bmp.getObject()),(android.graphics.Rect)(anywheresoftware.b4a.keywords.Common.Null),(android.graphics.Rect)(mostCurrent._rectfon.getObject()));
 //BA.debugLineNum = 888;BA.debugLine="BestPanel.Invalidate";
mostCurrent._bestpanel.Invalidate();
 //BA.debugLineNum = 889;BA.debugLine="BestPanel.Visible=True";
mostCurrent._bestpanel.setVisible(anywheresoftware.b4a.keywords.Common.True);
 //BA.debugLineNum = 890;BA.debugLine="WinOpen=True";
_winopen = anywheresoftware.b4a.keywords.Common.True;
 //BA.debugLineNum = 891;BA.debugLine="SetEditTextSize(EditText1,\"Anonymous\",100,1)";
_setedittextsize(mostCurrent._edittext1,"Anonymous",(float) (100),(float) (1));
 //BA.debugLineNum = 892;BA.debugLine="EditText1.Text=\"Anonymous\"";
mostCurrent._edittext1.setText((Object)("Anonymous"));
 };
 break; }
}
;
 };
 //BA.debugLineNum = 896;BA.debugLine="If Appodeal.isLoaded(Appodeal.INTERSTITIAL) Then";
if (mostCurrent._appodeal.isLoaded(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL)) { 
 //BA.debugLineNum = 897;BA.debugLine="Appodeal.show(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.show(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 //BA.debugLineNum = 898;BA.debugLine="Appodeal.cache(Appodeal.INTERSTITIAL)";
mostCurrent._appodeal.cache(mostCurrent.activityBA,mostCurrent._appodeal.INTERSTITIAL);
 };
 //BA.debugLineNum = 900;BA.debugLine="End Sub";
return "";
}
public static String  _xy2ij(float _x,float _y,int _i,int _j) throws Exception{
 //BA.debugLineNum = 813;BA.debugLine="Sub XY2IJ( x As Float, y As Float, i As Int, j As";
 //BA.debugLineNum = 814;BA.debugLine="x = x - Dsk.x";
_x = (float) (_x-mostCurrent._dsk.x);
 //BA.debugLineNum = 815;BA.debugLine="y = y - Dsk.y";
_y = (float) (_y-mostCurrent._dsk.y);
 //BA.debugLineNum = 816;BA.debugLine="mmm = y / conCellHeight";
_mmm = (int) (_y/(double)_concellheight);
 //BA.debugLineNum = 817;BA.debugLine="nnn = x / conCellWidth";
_nnn = (int) (_x/(double)_concellwidth);
 //BA.debugLineNum = 818;BA.debugLine="If y Mod conCellHeight > 0 Or mmm=0 Then mmm = m";
if (_y%_concellheight>0 || _mmm==0) { 
_mmm = (int) (_mmm+1);};
 //BA.debugLineNum = 819;BA.debugLine="If x Mod conCellWidth > 0 Or nnn=0 Then nnn = nn";
if (_x%_concellwidth>0 || _nnn==0) { 
_nnn = (int) (_nnn+1);};
 //BA.debugLineNum = 820;BA.debugLine="End Sub";
return "";
}
}
