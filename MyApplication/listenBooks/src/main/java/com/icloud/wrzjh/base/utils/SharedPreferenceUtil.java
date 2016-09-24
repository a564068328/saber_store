package com.icloud.wrzjh.base.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.icloud.listenbook.base.GameApp;
import com.icloud.wrzjh.base.net.socket.packet.RC4Http;
import com.icloud.wrzjh.base.php.PhpEncrypt;
import com.icloud.wrzjh.base.utils.io.FileUtils;
import com.icloud.wrzjh.base.utils.io.IOUtils;

public class SharedPreferenceUtil {
	private static final String KEY_DEVICE_ID = "KEY_DEVICE_ID";

	private static final String HOME_VER_CODE = "HOME_VER_CODE";
	private static final String KEY_IMEI = "KEY_IMEI";
	private static final String KEY_MAC = "KEY_MAC";
	private static final String VER_CHECK_INTERVAL = "VER_CHECK_INTERVAL";
	private static final String IS_NO_WIFI_LOOK = "IS_NO_WIFI_LOOK";
	private static final String PAY_SUCC_TIME = "PAY_SUCC_TIME";
	private static final String NEW_USER_FEEDBACK_COUNT = "NEW_USER_TASK_COUNT";
	private static final String COMPLETE_ACC_INTERVAL = "COMPLETE_ACC_INTERVAL";
	private static final String LAST_PULL = "LAST_PULL";
	private static final String FRESH_PUSH_TIME = "FRESH_PUSH_TIME";
	private static final String HAVE_UNLINE_CHATMSG = "HAVE_UNLINE_CHATMSG";
	private static final String TEACH_NOTIFY="TEACH_NOTIFY";
	private static final String MP3_IS_EXISTS="MP3_IS_EXISTS";
	private static final String SET_PLAY_MP3="SET_PLAY_MP3";
	private static final String CHATMSG_NOTIFY_SWITH="CHATMSG_NOTIFY_SWITH";
	private static final String CHATMSG_NOTIFY_DETIAL="CHATMSG_NOTIFY_DETIAL";
	private static final String CHATMSG_NOTIFY_VOICE="CHATMSG_NOTIFY_VOICE";
	private static final String CHATMSG_NOTIFY_SHARK="CHATMSG_NOTIFY_SHARK";
	private static final String RECEIVER_GUIDE_NOTIFY="RECEIVER_GUIDE_NOTIFY";
	private static final String DELETE_CHATMSG_WARNNING="DELETE_CHATMSG_WARNNING";
	private static final String TEACH_NOTIFY_WARNNING="TEACH_NOTIFY_WARNNING";
	private static final String CHANT_VERSION="CHANT_VERSION";
	private static final String TTS_SPEED="TTS_SPEED";
	private static final String TTS_VOLUMN="TTS_VOLUMN";
	private static final String TTS_VOICE="TTS_VOICE";
	private static final String TTS_SAVE_SETTING="TTS_SAVE_SETTING";
	private static final String TTS_IS_NO_WIFI_LOOK = "IS_NO_WIFI_LOOK";
	public static void clear(){
//		getPreference().edit().clear().commit();
		getPreference().edit().remove(KEY_DEVICE_ID).commit();
		getPreference().edit().remove(HOME_VER_CODE).commit();
		getPreference().edit().remove(KEY_IMEI).commit();
		getPreference().edit().remove(KEY_MAC).commit();
		getPreference().edit().remove(VER_CHECK_INTERVAL).commit();
		getPreference().edit().remove(IS_NO_WIFI_LOOK).commit();
		getPreference().edit().remove(NEW_USER_FEEDBACK_COUNT).commit();
		getPreference().edit().remove(PAY_SUCC_TIME).commit();
		getPreference().edit().remove(LAST_PULL).commit();
		getPreference().edit().remove(COMPLETE_ACC_INTERVAL).commit();
		getPreference().edit().remove(FRESH_PUSH_TIME).commit();
		getPreference().edit().remove(TEACH_NOTIFY_WARNNING).commit();
	}
	public static void saveTtsNoWifiLook(boolean is) {
		getPreference().edit().putBoolean(TTS_IS_NO_WIFI_LOOK, is).commit();
	}

	public static boolean isTtsNoWifiLook() {
		return getPreference().getBoolean(TTS_IS_NO_WIFI_LOOK, false);
	}
	public static void saveTtsSetting(boolean isSave){
		getPreference().edit().putBoolean(TTS_SAVE_SETTING, isSave).commit();
	}
	public static boolean getTtsSetting(){
		return getPreference().getBoolean(TTS_SAVE_SETTING, false);
	}
	public static void saveTtsVoice(int position){
		getPreference().edit().putInt(TTS_VOICE, position).commit();
	}
	public static int getTtsVoice(){
		return getPreference().getInt(TTS_VOICE, 0);
	}
	public static void saveTtsVolumn(String speed){
		getPreference().edit().putString(TTS_VOLUMN, speed).commit();
	}
	public static String getTtsVolumn(){
		return getPreference().getString(TTS_VOLUMN, "50");
	}
	public static void saveTtsSpeed(String speed){
		getPreference().edit().putString(TTS_SPEED, speed).commit();
	}
	public static String getTtsSpeed(){
		return getPreference().getString(TTS_SPEED, "50");
	}
	public static void saveChantVresion(String version){
		getPreference().edit().putString(CHANT_VERSION, version).commit();
	}
	public static String getChantVersion(){
		return getPreference().getString(CHANT_VERSION, "0");
	}
	public static void saveTeachNotify(boolean value){
		getPreference().edit().putBoolean(TEACH_NOTIFY_WARNNING, value).commit();
	}
	public static boolean getTeachNotify() {
		return getPreference().getBoolean(TEACH_NOTIFY_WARNNING, false);
	}
	public static void saveDeleteChatmsgWarnning(boolean value) {
		getPreference().edit().putBoolean(DELETE_CHATMSG_WARNNING, value).commit();
	}
	public static boolean getDeleteChatmsgWarnning() {
		return getPreference().getBoolean(DELETE_CHATMSG_WARNNING, false);
	}
	public static void saveReiver_Guide_Notify(boolean value) {
		getPreference().edit().putBoolean(RECEIVER_GUIDE_NOTIFY, value).commit();
	}
	public static boolean getReiver_Guide_Notify() {
		return getPreference().getBoolean(RECEIVER_GUIDE_NOTIFY, false);
	}
	public static void saveChatmsgNotifySwith(boolean value) {
		getPreference().edit().putBoolean(CHATMSG_NOTIFY_SWITH, value).commit();
	}
	public static boolean getChatmsgNotifySwith() {
		return getPreference().getBoolean(CHATMSG_NOTIFY_SWITH, true);
	}
	public static void saveChatmsgNotifyDetial(boolean value) {
		getPreference().edit().putBoolean(CHATMSG_NOTIFY_DETIAL, value).commit();
	}
	public static boolean getChatmsgNotifyDetial() {
		return getPreference().getBoolean(CHATMSG_NOTIFY_DETIAL, true);
	}
	public static void saveChatmsgNotifyVoice(boolean value) {
		getPreference().edit().putBoolean(CHATMSG_NOTIFY_VOICE, value).commit();
	}
	public static boolean getChatmsgNotifyVoice() {
		return getPreference().getBoolean(CHATMSG_NOTIFY_VOICE, true);
	}
	public static void saveChatmsgNotifyShark(boolean value) {
		getPreference().edit().putBoolean(CHATMSG_NOTIFY_SHARK, value).commit();
	}
	public static boolean getChatmsgNotifyShark() {
		return getPreference().getBoolean(CHATMSG_NOTIFY_SHARK, true);
	}
	public static void savefreshPushTime(long value) {
		getPreference().edit().putLong(FRESH_PUSH_TIME, value).commit();
	}
	public static long getfreshPushTime() {
		return getPreference().getLong(FRESH_PUSH_TIME, 0);
	}
	public static boolean getSET_PLAY_MP3(){
		return getPreference().getBoolean(SET_PLAY_MP3, true);
	}
	public static void setSET_PLAY_MP3(boolean value){
		getPreference().edit().putBoolean(SET_PLAY_MP3, value).commit();
	}
	//MP3是否存在
	public static boolean getMP3IsExist(){
		return getPreference().getBoolean(MP3_IS_EXISTS, false);
	}
	public static void setMP3IsExist(boolean value){
		getPreference().edit().putBoolean(MP3_IS_EXISTS, value).commit();
	}
    //聊天室有无离线消息
	public static boolean getHaveUnlineChatmsg(){
		return getPreference().getBoolean(HAVE_UNLINE_CHATMSG, false);
	}
	
	public static void setHaveUnlineChatmsg(boolean value){
		getPreference().edit().putBoolean(HAVE_UNLINE_CHATMSG, value).commit();
	}
	//保存大讲堂通知
	public static String getTEACH_NOTIFY(){
		return getPreference().getString(TEACH_NOTIFY, null);
	}
	
	public static void setTEACH_NOTIFY(String value){
		getPreference().edit().putString(TEACH_NOTIFY, value).commit();
	}
	
	

	public static void saveFeedbackTipConut(String key, int value) {
		
		getPreference().edit().putInt(NEW_USER_FEEDBACK_COUNT + key, value)
				.commit();
	}

	public static int getFeedbackTipConut(String key) {
		return getPreference().getInt(NEW_USER_FEEDBACK_COUNT + key, 0);
	}

	public static void saveCompleteAccInterval(String key, long value) {
		getPreference().edit().putLong(COMPLETE_ACC_INTERVAL + key, value)
				.commit();
	}

	public static long getCompleteAccInterval(String key) {
		return getPreference().getLong(COMPLETE_ACC_INTERVAL + key, 0);
	}

	public static void savePaySuccTime(String key, long value) {
		getPreference().edit().putLong(PAY_SUCC_TIME + key, value).commit();
	}

	public static long getPaySuccTime(String key) {
		return getPreference().getLong(PAY_SUCC_TIME + key, 0);
	}

	public static void saveNoWifiLook(boolean is) {
		getPreference().edit().putBoolean(IS_NO_WIFI_LOOK, is).commit();
	}

	public static boolean isNoWifiLook() {
		return getPreference().getBoolean(IS_NO_WIFI_LOOK, false);
	}

	public static long getHomeverCode() {
		return getPreference().getLong(HOME_VER_CODE, 0);

	}

	public static void saveHomeverCode(long verCode) {
		getPreference().edit().putLong(HOME_VER_CODE, verCode).commit();

	}

	public static long geyVerCheckTime() {
		return getPreference().getLong(VER_CHECK_INTERVAL, 0);
	}

	public static void saveVerCheckTime(long verCode) {
		getPreference().edit().putLong(VER_CHECK_INTERVAL, verCode).commit();

	}

	public static SharedPreferences getPreference() {
		return PreferenceManager
				.getDefaultSharedPreferences(GameApp.instance());
	}

	/** device id 生成, 保存 start */
	private static int MINI_CID_LEN = 5;

	public static String getImei() {
		return getPreference().getString(KEY_IMEI, "");
	}

	public static String getmac() {
		return getPreference().getString(KEY_MAC, "");
	}

	public static void setImei(String imei) {
		getPreference().edit().putString(KEY_IMEI, imei).commit();
	}

	public static void setMac(String mac) {
		getPreference().edit().putString(KEY_MAC, mac).commit();
	}

	public static String getDeviceId() {
		String deviceIdPre = getPreference().getString(KEY_DEVICE_ID, "");
		String deviceIdSd = getSDDeviceId();

		LogUtil.d("getDeviceId prefernce ", deviceIdPre);
		LogUtil.d("getDeviceId SDCard ", deviceIdSd);

		if (deviceIdPre.length() > MINI_CID_LEN
				|| deviceIdSd.length() > MINI_CID_LEN) {
			if (!deviceIdPre.equals(deviceIdSd)) {
				if (deviceIdSd.length() > MINI_CID_LEN) {
					deviceIdPre = deviceIdSd;
				}
			}
		}

		if (deviceIdPre != null && (deviceIdPre.length() > MINI_CID_LEN)) {
			try {
				byte[] b = TypeConvert.hexStr2ByteArr(deviceIdPre);
				RC4Http.RC4Base(b, 0, b.length);
				String did = new String(b);
				LogUtil.d("getDeviceId did ", did);

				saveDeviceId(did, true);
				return did;
			} catch (Exception e) {
				e.printStackTrace();
				return retNoDeviceId();
			}
		} else {
			return retNoDeviceId();
		}
	}

	private static String retNoDeviceId() {
		if (FileUtils.isSDCardMounted()) {
			LogUtil.d("getDeviceId SDCard ", "isSDCardMounted");
			return "";
		} else {
			LogUtil.d("getDeviceId SDCard ", "no isSDCardMounted");
			return "";
		}
	}

	public static void saveDeviceId(JSONArray deviceArray) {
		try {
			String deviceId = "";
			if (deviceArray != null && deviceArray.length() > 0) {
				byte[] arr = new byte[deviceArray.length()];
				for (int i = 0; i < deviceArray.length(); i++) {
					arr[i] = (byte) deviceArray.getInt(i);
				}
				PhpEncrypt.CrevasseBuffer(arr, 0, arr.length);
				deviceId = new String(arr);
				SharedPreferenceUtil.saveDeviceId(deviceId, true);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public static void saveDeviceId(String deviceId, boolean saveSdCard) {
		try {

			byte[] bpsd = deviceId.getBytes();
			RC4Http.RC4Base(bpsd, 0, bpsd.length);
			String didStr = TypeConvert.toHexString(bpsd);

			getPreference().edit().putString(KEY_DEVICE_ID, didStr).commit();

			if (saveSdCard) {
				saveSDDeviceId(didStr);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static String DEVICEID_FILE = "/.android/.device_sys_sn/ts_LISTLINE";

	private static void saveSDDeviceId(String deviceId) {
		String root = FileUtils.getRoot();
		if (root != null) {
			File file = new File(root + DEVICEID_FILE);
			File dir = file.getParentFile();
			if (!dir.exists()) {
				dir.mkdirs();
			}
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(file.getAbsolutePath(), false);
				fos.write(deviceId.getBytes());
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				IOUtils.closeQuietly(fos);
			}
		}
	}

	public static String getSDDeviceId() {
		String root = FileUtils.getRoot();
		if (root != null) {
			File file = new File(root + DEVICEID_FILE);
			if (file.exists()) {
				FileInputStream fis = null;
				try {
					fis = new FileInputStream(file);
					String dStr = IOUtils.toString(fis);
					if (dStr != null) {
						LogUtil.d("getSDDeviceId", dStr);
						return dStr;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					IOUtils.closeQuietly(fis);
				}
			}
		}
		return "";
	}

	/** device id 生成, 保存 end */

	/** 网关信息 start **/
	private static String LAST_DNS_IP = "last_dns_ip";

	public static void setLastDNSIp(String ip) {
		getPreference().edit().putString(LAST_DNS_IP, ip).commit();
	}

	public static String getLastDNSIp() {
		return getPreference().getString(LAST_DNS_IP, "");
	}

	public static long getLastPull() {
		return getPreference().getLong(LAST_PULL, 0);
	}

	public static void saveLastPull(long value) {
		getPreference().edit().putLong(LAST_PULL, value).commit();
	}
	/** 网关信息 end **/

}