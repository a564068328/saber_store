package com.icloud.listenbook.dialog;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.ActivityUtils;
import com.icloud.listenbook.entity.UserInfo;
import com.icloud.listenbook.http.DownloadBean;
import com.icloud.listenbook.ui.chipAct.FeedbackAct;
import com.icloud.listenbook.ui.chipAct.UpUserInfoAct;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.io.FileUtils;

public class DialogManage {
	public static ProgressDialog ProgressDialog(Context context, String title,
			String msg) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(title);
		progressDialog.setMessage(msg);
		progressDialog.setCancelable(false);
		return progressDialog;
	}

	public static void showAlertDialog(Context ctx, String msg, int pBtn,
			android.content.DialogInterface.OnClickListener plist, int Nbrt,
			android.content.DialogInterface.OnClickListener nlist) {
		new AlertDialog.Builder(ctx).setMessage(msg)
				.setPositiveButton(pBtn, plist).setNegativeButton(Nbrt, nlist)
				.show();

	}

	public static void showAlertDialog(Context ctx, String title, int msg,
			int pBtn, android.content.DialogInterface.OnClickListener plist,
			int Nbrt, android.content.DialogInterface.OnClickListener nlist,
			boolean isCancelable) {
		new AlertDialog.Builder(ctx).setTitle(title).setMessage(msg)
				.setPositiveButton(pBtn, plist).setCancelable(isCancelable)
				.setNegativeButton(Nbrt, nlist).show();

	}

	public static void showAlertDialog(Context ctx, int title, int msg,
			int pBtn, int Nbrt,
			android.content.DialogInterface.OnClickListener list) {
		new AlertDialog.Builder(ctx).setTitle(title).setMessage(msg)
				.setPositiveButton(pBtn, null).setNegativeButton(Nbrt, list)
				.show();

	}

	public static void showAlertDialog(Context ctx, String title, int msg,
			int pBtn, int Nbrt,
			android.content.DialogInterface.OnClickListener list) {
		new AlertDialog.Builder(ctx).setTitle(title).setMessage(msg)
				.setPositiveButton(pBtn, null).setNegativeButton(Nbrt, list)
				.show();

	}

	public static WarningDlg showWarningDlg(Context ctx, int title, int msg,
			int checkStr, int NBtn, int Pbrt,
			android.content.DialogInterface.OnClickListener list) {
		WarningDlg dlg = WarningDlg.Builder(ctx)
				.setTitle(ctx.getString(R.string.warning)).setMsg(msg)
				.setPositiveButton(Pbrt, list).setNegativeButton(NBtn, null)
				.setCheckBox(checkStr);
		dlg.show();
		dlg.setCancelable(true);
		return dlg;

	}
	public static WarningDlg showWarningDlg(Context ctx, int title, int msg,
			int checkStr, int NBtn, int Pbrt,
			android.content.DialogInterface.OnClickListener list,boolean isCancelable){
		WarningDlg dlg = WarningDlg.Builder(ctx)
				.setTitle(ctx.getString(R.string.warning)).setMsg(msg)
				.setPositiveButton(Pbrt, list).setNegativeButton(NBtn, null)
				.setCheckBox(checkStr);
		dlg.show();
		dlg.setCancelable(isCancelable);
		return dlg;
	}
	public static AlertDlg showAlertDlg(Context ctx, String title, String msg,
			int pBtn, android.content.DialogInterface.OnClickListener list,
			int Nbrt, boolean isCancelable) {
		AlertDlg dlg = AlertDlg.Builder(ctx).setTitle(title).setMsg(msg)
				.setPositiveButton(pBtn, list).setNegativeButton(Nbrt, null);
		dlg.show();
		dlg.setCancelable(isCancelable);
		return dlg;
	}

	public static AlertDlg showAlertDlg(Context ctx, String title, String msg,
			int pBtn, android.content.DialogInterface.OnClickListener list,
			int Nbrt) {
		return showAlertDlg(ctx, title, msg, pBtn, list, Nbrt, false);
	}

	public static AlertDlg showAlertDlg(Context ctx, int title, String msg,
			int pBtn, android.content.DialogInterface.OnClickListener list,
			int Nbrt, boolean isCancelable) {
		return showAlertDlg(ctx, ctx.getString(title), msg, pBtn, list, Nbrt,
				isCancelable);
	}

	public static AlertDlg showAlertDlg(Context ctx, int title, String msg,
			int pBtn, android.content.DialogInterface.OnClickListener list,
			int Nbrt) {
		return showAlertDlg(ctx, ctx.getString(title), msg, pBtn, list, Nbrt,
				false);
	}

	public static AlertDlg showAlertDlg(Context ctx, String title, int msg,
			int pBtn, android.content.DialogInterface.OnClickListener list,
			int Nbrt, boolean isCancelable) {
		return showAlertDlg(ctx, title, ctx.getString(msg), pBtn, list, Nbrt,
				isCancelable);
	}

	public static AlertDlg showAlertDlg(Context ctx, String title, int msg,
			int pBtn, android.content.DialogInterface.OnClickListener list,
			int Nbrt) {
		return showAlertDlg(ctx, title, ctx.getString(msg), pBtn, list, Nbrt,
				false);
	}

	public static InputDlg showInput(Context ctx, int title, int pBtn,
			android.content.DialogInterface.OnClickListener list, int Nbrt) {
		return showInput(ctx, ctx.getString(title), pBtn, list, Nbrt, true);
	}

	public static InputDlg showInput(Context ctx, String title, int pBtn,
			android.content.DialogInterface.OnClickListener list, int Nbrt,
			boolean isCancelable) {
		InputDlg dlg = InputDlg.Builder(ctx).setTitle(title)
				.setPositiveButton(pBtn, list).setNegativeButton(Nbrt, null);
		dlg.show();
		dlg.setCancelable(isCancelable);
		return dlg;
	}

	/** 更新提示 */
	public static void showUpVersion(String url, String msg, String version,
			final boolean isCancel) {
		final Activity act = ActivityUtils.instance().getCurrAct();
		final DownloadBean db = new DownloadBean();
		db.setUrl(url);
		db.setUpdateTxt(msg);
		db.setSaveDir(FileUtils.getUpdateDown() + version + "listenBook.apk");
		if (act != null && !act.isFinishing() && db.getUrl() != null
				&& db.getUrl().startsWith("http")) {
			act.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					new VerCheckDlg(act, db, isCancel).show();
				}
			});

		}

	}

	public static void showCompleteDig(final Activity act, String title) {
		showAlertDialog(act, title, R.string.complete,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (UserInfo.instance().isLogin()) {
							LoadingTool
									.launchActivity(act, UpUserInfoAct.class);
							return;
						}
					}
				}, R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						act.finish();
					}

				});
	}

	public static void showFeedbackDig(final Activity act, String title) {
		showAlertDialog(act, title, R.string.feedback,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						if (UserInfo.instance().isLogin()) {
							LoadingTool.launchActivity(act, FeedbackAct.class);
							return;
						}
					}
				}, R.string.cancel,
				new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						act.finish();
					}

				});
	}

	//pBtn为确定按钮的内容
	public static EditDlg showEditInput(Context ctx, int title, int pBtn,
			android.content.DialogInterface.OnClickListener list, int Nbrt) {
		return showEditInput(ctx, ctx.getString(title), pBtn, list, Nbrt, true);
	}

	public static EditDlg showEditInput(Context ctx, String title, int pBtn,
			android.content.DialogInterface.OnClickListener list, int Nbrt,
			boolean isCancelable) {
		EditDlg dlg = EditDlg.Builder(ctx).setTitle(title)
				.setPositiveButton(pBtn, list).setNegativeButton(Nbrt, null);
		dlg.show();
		dlg.setCancelable(isCancelable);
		return dlg;
	}
	
	public static ShowTextDlg showTextDlg(Context ctx, String title, int pBtn,
			android.content.DialogInterface.OnClickListener list) {
		ShowTextDlg dlg = ShowTextDlg.Builder(ctx).setTitle(title)
				.setPositiveButton(pBtn, list);
		dlg.show();
		dlg.setCancelable(true);
		return dlg;
	}
	
}
