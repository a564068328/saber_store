package com.icloud.listenbook.dialog;

import android.app.Activity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.GameApp;
import com.icloud.listenbook.base.HandlerUtils;
import com.icloud.listenbook.http.DownloadBean;
import com.icloud.listenbook.http.DownloaderTask;
import com.icloud.wrzjh.base.utils.ThreadPoolFactory;

public class VerCheckDlg extends BaseDialog implements OnClickListener {

	private TextView app_version, update_txt;
	private ProgressBar progress_now;
	private Button cancel, down;
	private DownloadBean db;
	DownloaderTask downTask;
	boolean isCancel;

	public VerCheckDlg(Activity ctx, DownloadBean db, boolean isCancel) {
		super(ctx);
		this.db = db;
		this.isCancel = isCancel;
	}

	@Override
	public int getLayout() {
		return R.layout.dlg_update_version;
	}

	public void dismiss() {
		super.dismiss();
		downTask.cancel();

	}

	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.cancel:
			dismiss();
			break;
		case R.id.submit:
			ThreadPoolFactory.getInstance().execute(downTask);
			progress_now.setVisibility(View.VISIBLE);
			update_txt.setVisibility(View.INVISIBLE);
			HandlerUtils.instance().post(progressTask);
			this.down.setVisibility(View.GONE);
			break;
		}
	}

	Runnable progressTask = new Runnable() {
		public void run() {
			progress_now.setProgress(db.progress);
			HandlerUtils.instance().postDelayed(progressTask, 500);
		}
	};

	@Override
	protected void init() {

	}

	@Override
	protected void setListener() {
		cancel.setOnClickListener(this);
		down.setOnClickListener(this);
		cancel.setOnTouchListener(GameApp.instance().onTouchListener);
		down.setOnTouchListener(GameApp.instance().onTouchListener);

	}

	@Override
	protected void findView() {
		app_version = (TextView) this.findViewById(R.id.title);
		update_txt = (TextView) this.findViewById(R.id.msg);
		progress_now = (ProgressBar) this.findViewById(R.id.progress_now);
		cancel = (Button) this.findViewById(R.id.cancel);
		down = (Button) this.findViewById(R.id.submit);

	}

	@Override
	public void show() {
		super.show();
		this.setCancelable(isCancel);
		cancel.setVisibility(isCancel ? View.VISIBLE : View.GONE);
		app_version.setText(R.string.version_tip);
		update_txt.setText(db.getUpdateTxt());
		update_txt.setMovementMethod(new ScrollingMovementMethod());
		downTask = new DownloaderTask(this.context, db);
	}

	@Override
	protected void setData() {

	}

}