package com.icloud.listenbook.unit;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.text.TextUtils;
import android.view.View.OnClickListener;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.ui.chipAct.HomeMediaAct;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ThreadPoolUtils;
import com.icloud.wrzjh.base.utils.down.DownloadMp3;
import com.listenBook.greendao.Article;

public class MethodUtils {
	public static final Long[] media = new Long[] {
			(long) Configuration.TYPE_WORD,
			(long) Configuration.TYPE_WHATTHEFUCK,
			(long) Configuration.TYPE_MASTERWROK };
	public static final String[] AbstractlimitString = { "视频", "音频", "文字" };
	private static int page;
	private static long Cid;
	private static int position;
	private static int totalposition;
	List<Long> allCid;

//	public static List<Long> getAllCid(long cid,int media) {
//		ArrayList<Long> CidAll = new ArrayList<Long>();
//		ArrayList<Long> Cid = new ArrayList<Long>();
//		if(cid == 0) {
//			Cid.clear();
//			Cid.addAll((ArrayList<Long>) IoUtils.instance().getCid(media));
//			if (Cid != null || Cid.size() > 0) {
//				CidAll.addAll(Cid);
//			}
//		}else{
//			CidAll.add(cid);
//		}
//		return CidAll;
//	}

	public MethodUtils() {

	}

	public MethodUtils(List<Long> allCid) {
		this.allCid = allCid;
	}

//	public void getAllNewArticle() {
//		Cid = allCid.get(0);
//		page = 0;
//		position = 0;
//		totalposition = allCid.size();
//		HttpUtils.readNewArticle(Cid, page, new getAllNewArticleResponse(),
//				null);
//	}
//
//	public class getAllNewArticleResponse implements Listener<JSONObject> {
//		@Override
//		public void onResponse(final JSONObject response) {
//			final int res = response.optInt("result", -1);
//			// LogUtil.e("getAllNewArticleResponse", response.toString());
//			ThreadPoolUtils.execute(new Runnable() {
//				@Override
//				public void run() {
//					// LogUtil.e("TreadId:",""+Thread.currentThread().getId());
//					if (res == 0) {
//						JSONArray jsonArray = response.optJSONArray("list");
//						JSONObject item;
//						ArticleItem articleItem;
//						if (jsonArray.length() > 0) {
//							for (int i = 0; i < jsonArray.length(); i++) {
//								articleItem = new ArticleItem();
//								item = jsonArray.optJSONObject(i);
//								JsonUtils.toArticle(articleItem, item,
//										Configuration.TYPE_WORD);
//								// 更新文章
//								IoUtils.instance().saveArticle(articleItem);
//							}
//							page++;
//						} else {
//							page = 0;
//							position++;
//							if (position >= totalposition) {
//								DataUpManage.saveVerCode("readArticle",
//										response.optString("verCode", "0"));
//								return;
//							}
//							Cid = allCid.get(position);
//						}
//						if (position < totalposition) {
//							HttpUtils.readNewArticle(Cid, page,
//									getAllNewArticleResponse.this, null);
//						}
//					}
//				}
//			});
//
//		}
//	}

	public static String delHead(String Abstract) {
		if (!TextUtils.isEmpty(Abstract)) {
			if (Abstract.startsWith(AbstractlimitString[0])) {
				Abstract = (Abstract.substring(2));
			} else if (Abstract.startsWith(AbstractlimitString[1])) {
				Abstract = (Abstract.substring(2));
			} else if (Abstract.startsWith(AbstractlimitString[2])) {
				Abstract = (Abstract.substring(2));
			}
			if (Abstract.startsWith(HomeMediaAct.limitString[0])) {
				Abstract = (Abstract.substring(2));
			} else if (Abstract.startsWith(HomeMediaAct.limitString[1])) {
				Abstract = (Abstract.substring(2));
			} else if (Abstract.startsWith(HomeMediaAct.limitString[2])) {
				Abstract = (Abstract.substring(2));
			}
		}
		return Abstract;
	}

	public void getArticleInfo(final int id, final int lastId, final String verCode) {
		HttpUtils.getArticleInfo(id, new Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				Article article = new Article();
				try {
					LogUtil.e("Tag", "getArticleInfo" + response.toString());
					int res = response.optInt("result", -1);
					if (res == 0) {
						article.setAId(id);
						article = JsonUtils.toArticle(article, response,
								Configuration.TYPE_BOOK);
						IoUtils.instance().saveArticle(article);
						if (id == lastId) {
							JSONObject jb = new JSONObject();
							jb.put("media", 0);
							jb.put("more", 0);
							DataUpManage.saveVerCode(
									"getRecommend" + jb.toString(), verCode);
						}
					}
				} catch (Exception e) {

				}

			}
		}, new ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
			}
		});
	}
}
