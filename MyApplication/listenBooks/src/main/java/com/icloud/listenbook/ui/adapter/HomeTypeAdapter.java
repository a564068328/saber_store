package com.icloud.listenbook.ui.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.icloud.listenbook.R;
import com.icloud.listenbook.http.ServerIps;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.entity.RecommendItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.BookInfoAct;
import com.icloud.listenbook.ui.chipAct.BuyCourseAct;
import com.icloud.listenbook.ui.chipAct.GreatMaster;
import com.icloud.listenbook.ui.chipAct.HomeMediaAct;
import com.icloud.listenbook.ui.chipAct.PhotoViewAct;
import com.icloud.listenbook.ui.chipAct.RecommendTypeAct;
import com.icloud.listenbook.ui.chipAct.VedioInfoAct;
import com.icloud.listenbook.ui.chipAct.VoiceInfoAct;
import com.icloud.listenbook.ui.chipAct.WebAct;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.LruImageCache;
import com.icloud.wrzjh.base.utils.LoadingTool;
import com.icloud.wrzjh.base.utils.ToastUtil;
import com.icloud.wrzjh.base.utils.ViewUtils;
import com.listenBook.greendao.Article;

import java.util.ArrayList;

/*
 * 视频推荐，听书推荐，文章推荐的RecyclerView.Adapter
 */
public class HomeTypeAdapter extends
		RecyclerView.Adapter<RecyclerView.ViewHolder> {
    String TAG=getClass().getName();
	Activity act;
	private ArrayList<RecommendItem> datasetSizes;
	String urlHead;
	ImageLoader imageLoader;
	public static final String[] Abstract = { "视频", "音频", "文字" };
    
	public HomeTypeAdapter(Activity act, ArrayList<RecommendItem> sizes) {
		this.datasetSizes = sizes;
		this.act = act;
		RequestQueue mQueue = Volley.newRequestQueue(act);
		LruImageCache lruImageCache = LruImageCache.instance(act
				.getApplicationContext());
		// 获取登录地址
		urlHead = ServerIps.getLoginAddr();
		imageLoader = new ImageLoader(mQueue, lruImageCache);

	}

	public void upData(ArrayList<RecommendItem> sizes) {
		if (this.datasetSizes != sizes) {
			datasetSizes.clear();
			datasetSizes.addAll(sizes);
		}
		this.notifyDataSetChanged();
	}

	public void setDatasetSizes(ArrayList<RecommendItem> datasetSizes) {
		this.datasetSizes = datasetSizes;
	}

	@Override
	public int getItemViewType(int pos) {
		return datasetSizes.get(pos).viewType;
	}

	/*
	 * 内容的Holder
	 */
	public class GridViewHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		NetworkImageView icon;
		TextView name;
		TextView aAuthor;
        LinearLayout ll_core;
		public GridViewHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			aAuthor = (TextView) view.findViewById(R.id.aAuthor);
			icon = (NetworkImageView) view.findViewById(R.id.icon);
			ll_core= (LinearLayout) view.findViewById(R.id.ll_core);
			icon.setOnClickListener(this);
			icon.setOnTouchListener(ViewUtils.instance().onTouchListener);
		}

		public void setView(RecommendItem item) {
			if (item.getAName() != null) {
				name.setVisibility(View.VISIBLE);
				aAuthor.setVisibility(View.VISIBLE);
				icon.setVisibility(View.VISIBLE);
				name.setText(item.getAName());
				aAuthor.setText(item.getAAuthor());
				icon.setTag(item);
				icon.setImageUrl(urlHead + item.getAIcon(), imageLoader);
				icon.setDefaultImageResId(R.drawable.icon_default_img);
				icon.setErrorImageResId(R.drawable.icon_default_img);
			} else {
				name.setVisibility(View.INVISIBLE);
				aAuthor.setVisibility(View.INVISIBLE);
				icon.setVisibility(View.INVISIBLE);
			}
			if(item.viewType==Type.TABLE_TITLE_CORE){
				LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 0, 15);
				ll_core.setLayoutParams(params);
			}else{
				LayoutParams params=new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
				params.setMargins(0, 0, 0, 0);
				ll_core.setLayoutParams(params);
			}
		}

		@Override
		public void onClick(View v) {
			if (v.getTag() != null) {
				RecommendItem item = (RecommendItem) v.getTag();
				// if (item.viewType == Type.TABLE_TITLE_CHILDREN) {
				// return;
				// }
				Article article = new Article();
				article.setAAbstract(item.getAAbstract());
				article.setAAuthor(item.getAAuthor());
				article.setAIcon(item.getAIcon());
				article.setAId(item.getAId());
				article.setAName(item.getAName());
				article.setClickConut(0);
				article.setChapterNum(item.getChapterNum());
				Intent Intent = new Intent();
				Intent.putExtra("data", JsonUtils.toJson(article));
				/* 1表示视频，2音频 3文字 默认为 0 表示 推荐首页* */
				Class<? extends Activity> activity = VedioInfoAct.class;
				if (item.getMedia() == Configuration.TYPE_VEDIO) {
					activity = VedioInfoAct.class;
				} else if (item.getMedia() == Configuration.TYPE_VOICE) {
					activity = VoiceInfoAct.class;
				} else if (item.getMedia() == Configuration.TYPE_BOOK) {
					activity = BookInfoAct.class;
				}else if(item.getMedia() == Configuration.TYPE_CATEGORY){
					Intent = new Intent();
					Intent.putExtra("cid", item.getAId());
					Intent.putExtra("title", item.getAName());
					Intent.putExtra("icon", item.getAIcon());
					Intent.putExtra("intro", item.getAAbstract());
					activity=BuyCourseAct.class;
				}

				if (item.getMedia() == Configuration.TYPE_GREAT) {
					if (item != null) {
						if (!TextUtils.isEmpty(item.getAAbstract())) {
							if (item.getAAbstract().startsWith("http://")
									|| item.getAAbstract().startsWith("www.")
									|| item.getAAbstract().startsWith(
											"https://")) {
								Intent intent = new Intent();
								intent.putExtra("url", item.getAAbstract());
								LoadingTool.launchActivity(act, WebAct.class,
										intent);
							}
						} else {
							Intent intent = new Intent();
							intent.putExtra("url", urlHead + item.getAIcon());
							LoadingTool.launchActivity(act, PhotoViewAct.class,
									intent);
						}
					}
					// activity = VedioInfoAct.class;
					return;
				}
				LoadingTool.launchActivity(act, activity, Intent);
			}

		}
	}

	public class ItemHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		NetworkImageView icon;
		TextView title;

		public ItemHolder(View view) {
			super(view);
			title = (TextView) view.findViewById(R.id.title);
			title.setOnClickListener(this);
			title.setOnTouchListener(ViewUtils.instance().onTouchListener);
		}

		public void setView(RecommendItem item) {
			if (item.viewType == Type.TABLE_TITLE_TIME
					|| item.viewType == Type.TABLE_TITLE_WORD
					|| item.viewType == Type.TABLE_TITLE_WORK) {
				title.setTextColor(act.getResources().getColor(
						R.color.litter_orange));
			}
			title.setText(item.getAName());
			title.setTag(item);
		}

		@Override
		public void onClick(View v) {
			RecommendItem item = (RecommendItem) v.getTag();
			if (item.viewType == Type.TABLE_TITLE_TIME
					|| item.viewType == Type.TABLE_TITLE_WORD
					|| item.viewType == Type.TABLE_TITLE_WORK) {
				if (item.getAId() == 0)
					return;
				else {
					Intent Intent = new Intent();
					Intent.putExtra("cId", item.getAId());
					Intent.putExtra("name", item.getAAbstract());
					Intent.putExtra("type", item.getMedia());
					LoadingTool.launchActivity(act, HomeMediaAct.class, Intent);
					return;
				}
			}
//			Article article = new Article();
			Article article = IoUtils.instance().getArticleInfo(item.getAId());
//			article.setAAbstract(item.getAAbstract());
//			article.setAAuthor(item.getAAuthor());
//			article.setAIcon(item.getAIcon());
//			article.setAId(item.getAId());
//			article.setAName(item.getAName());
//			article.setClickConut(0);
//			article.setChapterNum(item.getChapterNum());
//			article.setMedia((int)item.getMedia());
			Intent Intent = new Intent();
			Intent.putExtra("data", JsonUtils.toJson(article));
//			LogUtil.e(TAG, "item.getAAbstract()"+item.toString());
			Class<? extends Activity> activity = VedioInfoAct.class;
            if(article==null){
            	ToastUtil.showMessage("请稍等，数据可能还在加载中~");
            	return;
            }
			if (article.getAAbstract().startsWith(Abstract[0])) {
				activity = VedioInfoAct.class;
			} else if (article.getAAbstract().startsWith(Abstract[1])) {
				activity = VoiceInfoAct.class;
			} else if (article.getAAbstract().startsWith(Abstract[2])) {
				activity = BookInfoAct.class;
			}
			if (article.getMedia() == Configuration.TYPE_GREAT) {
				if (article != null) {
					if (!TextUtils.isEmpty(article.getAAbstract())) {
						if (article.getAAbstract().startsWith("http://")
								|| article.getAAbstract().startsWith("www.")
								|| article.getAAbstract().startsWith("https://")) {
							Intent intent = new Intent();
							intent.putExtra("url", article.getAAbstract());
							LoadingTool.launchActivity(act, WebAct.class,
									intent);
						}
					} else {
						Intent intent = new Intent();
						intent.putExtra("url", urlHead + article.getAIcon());
						LoadingTool.launchActivity(act, PhotoViewAct.class,
								intent);
					}
				}
				// activity = VedioInfoAct.class;
				return;
			}
			LoadingTool.launchActivity(act, activity, Intent);

		}
	}

	/*
	 * 标题的Holder
	 */
	public class TitleHolder extends RecyclerView.ViewHolder {
		TextView name;

		public TitleHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);

		}

		public void setView(RecommendItem item) {
			name.setText(item.getAName());

		}

	}

	/*
	 * 底部的Holder
	 */
	public class BottomHolder extends RecyclerView.ViewHolder implements
			OnClickListener {
		TextView name;

		public BottomHolder(View view) {
			super(view);
			name = (TextView) view.findViewById(R.id.name);
			name.setOnClickListener(this);
			name.setOnTouchListener(ViewUtils.instance().onTouchListener);
		}

		public void setView(RecommendItem item) {
			name.setText(item.getAName());
			name.setTag(item);
		}

		@Override
		public void onClick(View v) {
			if (v.getTag() != null) {
				RecommendItem item = (RecommendItem) v.getTag();
				Intent Intent = new Intent();
				Intent.putExtra("media", item.getMedia());
				Intent.putExtra("type", item.getMedia());
				if (item.getMedia() == Configuration.TYPE_VEDIO
						|| item.getMedia() == Configuration.TYPE_VOICE
						|| item.getMedia() == Configuration.TYPE_BOOK)
					LoadingTool.launchActivity(act, RecommendTypeAct.class,
							Intent);
				else if (item.getMedia() == Configuration.TYPE_GREAT) {
					LoadingTool.launchActivity(act, GreatMaster.class, Intent);
				} else
					LoadingTool.launchActivity(act, HomeMediaAct.class, Intent);
			}
		}
	}

	@Override
	public void onBindViewHolder(ViewHolder viewHolder, int pos) {
		RecommendItem item = datasetSizes.get(pos);
		if (viewHolder instanceof TitleHolder) {
			TitleHolder headerHolder = (TitleHolder) viewHolder;
			headerHolder.setView(item);
		} else if (viewHolder instanceof GridViewHolder) {
			GridViewHolder holder = (GridViewHolder) viewHolder;
			holder.setView(item);
		} else if (viewHolder instanceof BottomHolder) {
			BottomHolder holder = (BottomHolder) viewHolder;
			holder.setView(item);
		} else if (viewHolder instanceof ItemHolder) {
			ItemHolder holder = (ItemHolder) viewHolder;
			holder.setView(item);
		}
	}

	@Override
	public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		View view;
		if (viewType == Type.TABLE_TITLE) {
			view = LayoutInflater.from(act).inflate(
					R.layout.recommen_title_item, parent, false);
			return new TitleHolder(view);
		} else if (viewType == Type.TABLE_FOOTER) {
			view = LayoutInflater.from(act).inflate(
					R.layout.recommen_bottom_item, parent, false);
			return new BottomHolder(view);
		} else if (viewType == Type.TABLE_WORD || viewType == Type.TABLE_WORK
				|| viewType == Type.TABLE_TIME
				|| viewType == Type.TABLE_TITLE_TIME
				|| viewType == Type.TABLE_TITLE_WORD
				|| viewType == Type.TABLE_TITLE_WORK) {
			view = LayoutInflater.from(act).inflate(
					R.layout.homemedia_grid_item, parent, false);
			return new ItemHolder(view);
		} else {
			view = LayoutInflater.from(act).inflate(
					R.layout.recommen_grid_item, parent, false);
			return new GridViewHolder(view);
		}
	}

	@Override
	public int getItemCount() {
		return datasetSizes.size();
	}

}
