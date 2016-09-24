package com.icloud.wrzjh.base.utils;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.json.JSONObject;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.telephony.TelephonyManager;

import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.ProjectConfig;

public class ClientDeviceInfo {

	private static final String TAG = ClientDeviceInfo.class.getSimpleName();

	/** 网络类型 **/
	public byte netmode = 0;

	/** 平台列表 */
	public String pv = ProjectConfig.PLATFORM_ID + "";// 安卓平台
	public String pkg = "com.icloud.game.wrzjh";

	/** 客户端版本号 */
	public String version = "";

	public String mac = "";
	public String imei = "";
	public String imsi = "";

	/** 联盟号( 如果是联盟用户, 登录时带入联盟ID,默认为0) */
	public String unionid = "0";

	/** 系统版本 */
	public String system = "";

	/** 用户位置 */
	// public String province = "";

	/** 用户地区 */
	// public String area = "";

	public String nickname = "";

	public int verCode;
	public int verName;

	private static ClientDeviceInfo instance;

	public static ClientDeviceInfo getInstance() {
		if (instance == null) {
			instance = new ClientDeviceInfo();
		}
		return instance;
	}

	public ClientDeviceInfo() {
		Context ctx = GameApp.instance();
		unionid = getMetaDataValue(ctx, "MOBILE_CHANNEL", "yz");
		getVersion(ctx);
		getMachineId(ctx);
		getMacAddress(ctx);
		getDeviceModel(ctx);
		// getCityName(ctx);
		getNetmode(ctx);
		try {
			pkg = GameApp.instance().getPackageName();
		} catch (Exception e) {
		}
	}

	public void getNetmode(Context ctx) {
		netmode = APNUtil.getNetWorkType(ctx);
	}

	/** 扎金花客户端版本号 */
	private void getVersion(Context ctx) {
		try {
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			if (pi != null) {
				String versionName = pi.versionName == null ? "null"
						: pi.versionName;
				int versionCode = pi.versionCode;

				JSONObject ver = new JSONObject();

				// 1.0.0002
				if (versionName.length() == 8) {
					int name = Integer.parseInt(versionName.substring(0, 1))
							* 100000
							+ Integer.parseInt(versionName.substring(2, 3))
							* 10000
							+ Integer.parseInt(versionName.substring(4, 8));
					ver.put("name", name);
					verName = name;
				} else {
					ver.put("name", versionName);
				}

				ver.put("code", versionCode);
				verCode = versionCode;
				version = ver.toString();
			}
		} catch (NameNotFoundException e) {
			LogUtil.e("an error occured when collect package info", e);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/** 获取mac, imei, imsi */
	public void getMachineId(Context mContext) {
		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null) {
			imsi = telephonyManager.getSubscriberId();
			imei = telephonyManager.getDeviceId();
		}

		if (imei == null) {
			imei = SharedPreferenceUtil.getImei();
		} else {
			SharedPreferenceUtil.setImei(imei);
		}

		if (imsi == null) {
			imsi = "";
		}
	}

	/** 获取imsi */
	public static String getImsi(Context mContext) {
		String imsi = "";
		TelephonyManager telephonyManager = (TelephonyManager) mContext
				.getSystemService(Context.TELEPHONY_SERVICE);
		if (telephonyManager != null) {
			imsi = telephonyManager.getSubscriberId();
		}
		if (imsi == null) {
			imsi = "";
		}
		return imsi;
	}

	public void getMacAddress(Context mContext) {
		WifiManager mgr = (WifiManager) mContext
				.getSystemService(Context.WIFI_SERVICE);
		if (mgr != null) {
			WifiInfo wifiinfo = mgr.getConnectionInfo();
			if (wifiinfo != null) {
				mac = wifiinfo.getMacAddress();
			}
		}
		if (mac == null) {
			mac = SharedPreferenceUtil.getmac();
		} else {
			SharedPreferenceUtil.setMac(mac);
		}
	}

	public void getDeviceModel(Context mContext) {
		system = android.os.Build.VERSION.SDK_INT + "";
		nickname = Build.MODEL;
	}

	public void getCityName(Context context) {
		LocationManager locationManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);

		if (locationManager == null) {
			return;
		}

		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(false);
		criteria.setPowerRequirement(Criteria.POWER_LOW);

		// String provider = locationManager.getBestProvider(criteria, true);
		// if (provider == null) {
		// System.out.println("2");
		// return;
		// }

		// 得到坐标相关的信息
		// Location location =
		// locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		Location location = locationManager
				.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		if (location == null) {
			return;
		} else {
			double latitude = location.getLatitude();
			double longitude = location.getLongitude();
			LogUtil.i(TAG, "latitude" + latitude + "longitude" + longitude);
			// 更具地理环境来确定编码
			Geocoder gc = new Geocoder(context, Locale.getDefault());
			try {
				// 取得地址相关的一些信息\经度、纬度
				List<Address> addresses = gc.getFromLocation(latitude,
						longitude, 1);
				StringBuilder sb = new StringBuilder();
				if (addresses.size() > 0) {
					Address address = addresses.get(0);
					sb.append(address.getLocality()).append("\n");
					// province = sb.toString();
				}
			} catch (IOException e) {
				LogUtil.w(TAG, e);
			}
		}
	}

	private String getMetaDataValue(Context ctx, String name, String def) {
		String value = getMetaDataValue(ctx, name);
		return (value == null) ? def : value;
	}

	private String getMetaDataValue(Context ctx, String name) {
		Object value = null;
		PackageManager packageManager = ctx.getPackageManager();
		ApplicationInfo applicationInfo;
		try {
			applicationInfo = packageManager.getApplicationInfo(
					ctx.getPackageName(), PackageManager.GET_META_DATA);
			if (applicationInfo != null && applicationInfo.metaData != null) {
				value = applicationInfo.metaData.get(name);
			}
		} catch (NameNotFoundException e) {
		}
		if (value == null) {
			value = "yunzhong";
		}
		return value.toString();
	}

	private static String getSign(Context context) {
		PackageManager pm = context.getPackageManager();
		PackageInfo pi;
		try {
			pi = pm.getPackageInfo(context.getPackageName(),
					PackageManager.GET_ACTIVITIES);
			String signature = new String(pi.signatures[0].toByteArray());
			return signature;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return "";
	}

	public String toString() {
		return "ClientDeviceInfo [brandid=" + ", netmode=" + netmode + ", pv="
				+ pv + ", version=" + version + ", mac=" + mac + ", imei="
				+ imei + ", imsi=" + imsi + ", unionid=" + unionid
				+ ", system=" + system + ", province=" + "" + ", area=" + ""
				+ ", nickname=" + nickname + "]";
	}

}
