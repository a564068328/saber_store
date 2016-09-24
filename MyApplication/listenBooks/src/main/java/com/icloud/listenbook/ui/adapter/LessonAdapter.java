package com.icloud.listenbook.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.view.lableview.LabelTextView;
import com.icloud.listenbook.entity.LessonItem;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.chipAct.ChantAct;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.listenbook.unit.UIUtils;
import com.icloud.wrzjh.base.utils.ThreadPoolUtils;
import com.listenBook.greendao.LessonInfo;
import com.yunva.live.sdk.interfaces.util.f;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

public class LessonAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
	ArrayList<LessonItem> datas;
	Activity act;
	private final String TAG = getClass().getName();
	private List<Integer> completePosition;
	LinearLayout ll_progressbar;

	public LessonAdapter(Activity act) {
		this.act = act;
		datas = new ArrayList<LessonItem>();
		ll_progressbar = (LinearLayout) act.findViewById(R.id.ll_progressbar);
		completePosition = new ArrayList<Integer>();
	}

	@Override
	public int getItemCount() {
		return datas.size();
	}

	@Override
	public int getItemViewType(int position) {
		return datas.get(position).type;
	}

	@Override
	public void onBindViewHolder(ViewHolder viewholder, int position) {
		if (viewholder instanceof ChioceHolder) {
			ChioceHolder holder = (ChioceHolder) viewholder;
			holder.setView(position);
		} else if (viewholder instanceof CompletionHolder) {
			CompletionHolder holder = (CompletionHolder) viewholder;
			holder.setView(position);
		} else if (viewholder instanceof RegoneHolder) {
			RegoneHolder holder = (RegoneHolder) viewholder;
			holder.setView(position);
		} else {
			SumbitnHolder holder = (SumbitnHolder) viewholder;
			holder.setView(position);
		}

	}

	public class ChioceHolder extends RecyclerView.ViewHolder {

		private TextView title;
		private TextView issue;
		private LinearLayout ll_group;
		private LessonItem item;
		private boolean iscomplete = false;
		private TextView tv_chioce_result;
		private TextView tv_part;
        private TextView tv_user_chioce_result;
		public ChioceHolder(View view) {
			super(view);
			title = (TextView) view.findViewById(R.id.title);
			issue = (TextView) view.findViewById(R.id.issue);
			ll_group = (LinearLayout) view.findViewById(R.id.ll_group);
			tv_chioce_result = (TextView) view
					.findViewById(R.id.tv_chioce_result);
			tv_part = (TextView) view.findViewById(R.id.tv_part);
			tv_user_chioce_result=(TextView) view.findViewById(R.id.tv_user_chioce_result);
			setIsRecyclable(false);
		}

		public void setView(int position) {
			item = datas.get(position);
			iscomplete = IoUtils.instance().getLessonIsComplete(item.date);
			if (position - completePosition.size() == 0)
				tv_part.setVisibility(View.VISIBLE);
			else
				tv_part.setVisibility(View.GONE);
			title.setText("第" + (position - completePosition.size()+ 1) + "题:" + (item.title));
			issue.setText(item.issue);
			for (int i = 0; i < item.option.length; i++) {
				CheckBox box = new CheckBox(act);
				LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
				box.setButtonDrawable(act.getResources().getDrawable(
						R.drawable.radiobutton_selection));
				box.setText(item.option[i]);
				box.setTextColor(act.getResources().getColor(R.color.black));
				box.setTextSize(14);
				box.setId(i);
				box.setTag(position);
				box.setChecked(false);
				if (datas.get(position).user_answer != null
						&& datas.get(position).user_answer[i] != null) {
					if (!datas.get(position).user_answer[i].equals(" ")) {
						box.setChecked(true);
					}
				}
				if (ll_group.getChildCount() < item.option.length) {

					ll_group.addView(box, params);
				}
				ll_group.setOrientation(LinearLayout.VERTICAL);
				if (!iscomplete) {
					box.setOnCheckedChangeListener(new OnCheckedChangeListener() {
						@Override
						public void onCheckedChanged(CompoundButton buttonView,
								boolean isChecked) {
							int id = buttonView.getId();
							int position = (Integer) buttonView.getTag();
//							LogUtil.e(TAG, "id" + id);
//							LogUtil.e(TAG, "position" + position);
							if (isChecked) {
								datas.get(position).user_answer[id] = ToolUtils
										.numToLetter(id);
							} else {
								datas.get(position).user_answer[id] = " ";
							}
							IoUtils.instance().saveLessonItem(
									datas.get(position), false);
						}
					});
				} else {
					box.setClickable(false);
				}
			}
			if (iscomplete) {
				tv_chioce_result.setVisibility(View.VISIBLE);
				tv_user_chioce_result.setVisibility(View.VISIBLE);
				StringBuilder builder = new StringBuilder();
				builder.append("正确答案为：");
				for (int i = 0; i < item.answer.length; i++) {
					if (item.answer[i].equals(" "))
						continue;
					builder.append(item.answer[i]).append(";");
				}
				tv_chioce_result.setText(builder.toString());
				builder= new StringBuilder();
				builder.append("您的答案为：");
				for (int i = 0; i < item.answer.length; i++) {
					if (item.user_answer[i].equals(" "))
						continue;
					builder.append(item.user_answer[i]).append(";");
				}
				tv_user_chioce_result.setText(builder.toString());
			} else {
				tv_chioce_result.setVisibility(View.GONE);
			}
		}
	}

	public class CompletionHolder extends RecyclerView.ViewHolder {
		private TextView title;
		private TextView issue;
		private LessonItem item;
		private TextView tv_compeletion_result;
		private LinearLayout ll_edittext;
		private TextView tv_part;
		private TextView tv_user_compeletion_result;
		boolean isExcu;
		private boolean iscomplete = false;

		public CompletionHolder(View view) {
			super(view);
			title = (TextView) view.findViewById(R.id.title);
			issue = (TextView) view.findViewById(R.id.issue);
			tv_compeletion_result = (TextView) view
					.findViewById(R.id.tv_compeletion_result);
			ll_edittext = (LinearLayout) view.findViewById(R.id.ll_edittext);
			tv_part = (TextView) view.findViewById(R.id.tv_part);
			tv_user_compeletion_result=(TextView) view.findViewById(R.id.tv_user_compeletion_result);
			setIsRecyclable(false);
		}

		public void setView(int position) {
			item = datas.get(position);
			iscomplete = IoUtils.instance().getLessonIsComplete(item.date);
			boolean isComplete = IoUtils.instance().getLessonIsComplete(
					item.date);
			if (!completePosition.contains(position))
				completePosition.add(position);
			if (position == 0)
				tv_part.setVisibility(View.VISIBLE);
			else
				tv_part.setVisibility(View.GONE);
			if (!TextUtils.isEmpty(item.title))
				title.setText("第" + (position  + 1)
						+ "题:" + (item.title));
			else
				title.setText("第" + (position  + 1)
						+ "题:");
			if (!TextUtils.isEmpty(item.issue))
				issue.setText(item.issue);
//			List<EditText> views = new ArrayList<EditText>();
			for (int i = 0; i < item.answer.length; i++) {
				if (ll_edittext.getChildCount() >= item.answer.length)
					break;
				final EditText et = new EditText(act);
				// views.add(et);
				LayoutParams params = new LinearLayout.LayoutParams(
						LayoutParams.FILL_PARENT, UIUtils.dip2px(act, 26));
				et.setBackgroundDrawable(act.getResources().getDrawable(
						R.drawable.bg_edittext));
				et.setPadding(UIUtils.dip2px(act, 2), UIUtils.dip2px(act, 2),
						UIUtils.dip2px(act, 2), UIUtils.dip2px(act, 2));
				params.setMargins(0, UIUtils.dip2px(act, 4), 0,
						UIUtils.dip2px(act, 4));
				et.setMaxLines(1);
				et.setTextSize(14);
				et.setTextColor(act.getResources().getColor(R.color.black));
				ll_edittext.addView(et, params);
				if (datas.get(position).user_answer != null
						&& datas.get(position).user_answer[i] != null
						&& !TextUtils
								.isEmpty(datas.get(position).user_answer[i]))
					et.setText(datas.get(position).user_answer[i]);
				else
					et.setHint("请输入第" + (i + 1) + "空的答案吧~");
				// 默认中文
				et.setInputType(EditorInfo.TYPE_CLASS_TEXT);
				et.setImeOptions(EditorInfo.IME_ACTION_DONE);
				et.setId(i);
				et.setTag(position);
				if (!isComplete) {
					et.addTextChangedListener(new TextWatcher() {
						@Override
						public void onTextChanged(CharSequence s, int start,
								int before, int count) {
							int position = (Integer) et.getTag();
							int i = (Integer) et.getId();
							// LogUtil.e(TAG, "CharSequence:" + s
							// +";position="+item.position+";i="+i);
							if (TextUtils.isEmpty(s.toString()))
								datas.get(position).user_answer[i] = " ";
							else
								datas.get(position).user_answer[i] = ToolUtils
										.replaceBlank(s.toString());
							IoUtils.instance().saveLessonItem(
									datas.get(position), true);
						}

						@Override
						public void beforeTextChanged(CharSequence s,
								int start, int count, int after) {
						}

						@Override
						public void afterTextChanged(Editable s) {
						}
					});

					et.setOnKeyListener(new OnKeyListener() {
						@Override
						public boolean onKey(View v, int keyCode, KeyEvent event) {
							if (keyCode == KeyEvent.KEYCODE_ENTER
									&& event.getAction() == KeyEvent.ACTION_UP) {
								InputMethodManager inputMethodManager = (InputMethodManager) act
										.getSystemService(Context.INPUT_METHOD_SERVICE);
								if (inputMethodManager.isActive()) {
									inputMethodManager.hideSoftInputFromWindow(
											v.getApplicationWindowToken(), 0);
								}
								return true;
							}
							return false;
						}
					});
				} else {
					if (TextUtils.isEmpty(datas.get(position).user_answer[i]))
						et.setHint("此项未填写答案～");
					et.setFocusable(false);
					et.setFocusableInTouchMode(false);
				}
			}

			if (iscomplete) {
				tv_user_compeletion_result.setVisibility(View.VISIBLE);
				tv_compeletion_result.setVisibility(View.VISIBLE);
				StringBuilder builder = new StringBuilder();
				builder.append("正确答案为：");
				for (int i = 0; i < item.answer.length; i++) {
					builder.append(item.answer[i]).append(";");
				}
				tv_compeletion_result.setText(builder.toString());
				builder = new StringBuilder();
				builder.append("您的答案为：");
				for (int i = 0; i < item.user_answer.length; i++) {
					if(TextUtils.isEmpty(item.user_answer[i]))
						continue;
					builder.append(item.user_answer[i]).append(";");
				}
				tv_user_compeletion_result.setText(builder.toString());
			} else {
				tv_compeletion_result.setVisibility(View.GONE);
				tv_user_compeletion_result.setVisibility(View.GONE);
			}
		}
	}

	public class RegoneHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		TextView tv_date;
		LabelTextView tv_result;
		TextView tv_gotit;

		public RegoneHolder(View view) {
			super(view);
			tv_date = (TextView) view.findViewById(R.id.tv_date);
			tv_result = (LabelTextView) view.findViewById(R.id.tv_result);
			tv_gotit = (TextView) view.findViewById(R.id.tv_gotit);
		}

		public void setView(int position) {
			LessonItem item = datas.get(position);
			boolean isComplete = item.iscomplete;
			StringBuilder builder = new StringBuilder(item.date.substring(4)
					.toString());
			builder.insert(2, "月").insert(5, "日");
			tv_date.setText(builder.toString());
			if (!isComplete) {
				tv_result.setText(new StringBuilder().append("该日功课")
						.append(item.describe).append("！请去完成吧～"));
				tv_gotit.setText("立即完成");
				tv_result.setLabelText("未完成");
			} else {
				tv_result.setText(item.describe);
				if (item.marks >= 90) {
					tv_result.setLabelText("优秀");
				} else if (item.marks >= 70 && item.marks < 90) {
					tv_result.setLabelText("良好");
				} else if (item.marks >= 50 && item.marks < 70) {
					tv_result.setLabelText("及格");
				} else if (item.marks >= 0 && item.marks < 50) {
					tv_result.setLabelText("不及格");
				}
				tv_result.setLabelTextColor(act.getResources().getColor(
						R.color.white));
				tv_result.setLabelTextSize(18);
				tv_gotit.setText("查看详情");
			}
			tv_gotit.setTag(item.date);
			tv_gotit.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.tv_gotit:
				String date = (String) v.getTag();
				ChantAct.handler.obtainMessage(ChantAct.REGONE_GOIT, date)
						.sendToTarget();
				break;

			default:
				break;
			}
		}

	}

	public class SumbitnHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		TextView tv_sumbit;

		public SumbitnHolder(View view) {
			super(view);
			tv_sumbit = (TextView) view.findViewById(R.id.tv_sumbit);
		}

		public void setView(int position) {
			tv_sumbit.setOnClickListener(this);
		}

		@Override
		public void onClick(View v) {
			int id = v.getId();
			switch (id) {
			case R.id.tv_sumbit:
				String date = datas.get(0).date;
				checkAnswer(date);
				break;

			default:
				break;
			}
		}

		private void checkAnswer(final String date) {
			ll_progressbar.setVisibility(View.VISIBLE);
			ThreadPoolUtils.execute(new Runnable() {
				@Override
				public void run() {
					String checkAnswer = ToolUtils.checkAnswer(date,completePosition.size());
					ChantAct.handler.obtainMessage(ChantAct.SHOW_MARKS,
							checkAnswer).sendToTarget();
					boolean lessonIsComplete = IoUtils.instance()
							.getLessonIsComplete(date);
					if (lessonIsComplete && !act.isFinishing()) {
						final ArrayList<LessonItem> lessonInfo = JsonUtils
								.toLessonItems(date);
						final ArrayList<LessonItem> marksItems = JsonUtils
								.toLessonMarksItems(30);
						act.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								up(marksItems);
								up(lessonInfo);
							}
						});
					}
				}
			});

		}

	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == LessonItem.CHOICE) {
			view = LayoutInflater.from(act).inflate(
					R.layout.lesson_chioce_item, parent, false);
			return new ChioceHolder(view);
		} else if (viewType == LessonItem.COMPLETION) {
			view = LayoutInflater.from(act).inflate(
					R.layout.lesson_compeletion_item, parent, false);
			return new CompletionHolder(view);
		} else if (viewType == LessonItem.SUMBIT) {
			view = LayoutInflater.from(act).inflate(
					R.layout.lesson_regone_item, parent, false);
			return new RegoneHolder(view);
		} else {
			view = LayoutInflater.from(act).inflate(
					R.layout.lesson_sumbit_item, parent, false);
			return new SumbitnHolder(view);
		}
	}

	public void up(ArrayList<LessonItem> datas) {
		if (this.datas != datas) {
			this.datas.clear();
			this.datas.addAll(datas);
			notifyDataSetChanged();
		}
	}

}
// RadioButton的动态添加
// RadioButton button = new RadioButton(act);
// LayoutParams params = new LinearLayout.LayoutParams(
// LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
// button.setButtonDrawable(act.getResources().getDrawable(
// R.drawable.radiobutton_selection));
// button.setText(item.option[i]);
// // 因为RadioButton默认字体颜色为透明，坑爹啊
// button.setTextColor(act.getResources().getColor(
// R.color.black));
// button.setId(i);
// group.addView(button, params);
// group.setOrientation(LinearLayout.VERTICAL);
// group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
//
// @Override
// public void onCheckedChanged(RadioGroup group,
// int checkedId) {
// group.getChildAt(checkedId).setSelected(true);
// }
// });