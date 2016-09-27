package com.icloud.listenbook.io;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;

import com.icloud.listenbook.connector.ImageManage;
import com.icloud.listenbook.entity.DeductCurrencyInfo;
import com.icloud.listenbook.entity.LessonItem;
import com.icloud.listenbook.entity.ReadingTrackItem;
import com.icloud.listenbook.entity.TabItemInfo;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.ArticleItem;
import com.icloud.listenbook.ui.adapter.entity.RecommendItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.listenbook.unit.JsonUtils;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.ThreadPoolUtils;
import com.icloud.wrzjh.base.utils.down.DownloadManager;
import com.listenBook.greendao.Ads;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.ArticleChapterInfo;
import com.listenBook.greendao.ArticleChapterRecode;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.Baseuser;
import com.listenBook.greendao.Category;
import com.listenBook.greendao.ChatMsg;
import com.listenBook.greendao.ChatPeopleInfo;
import com.listenBook.greendao.ChipFeedback;
import com.listenBook.greendao.Collect;
import com.listenBook.greendao.Down;
import com.listenBook.greendao.Feedback;
import com.listenBook.greendao.FreshPush;
import com.listenBook.greendao.LessonInfo;
import com.listenBook.greendao.LessonMarksInfo;
import com.listenBook.greendao.Media;
import com.listenBook.greendao.Rank;
import com.listenBook.greendao.ReadingTrack;
import com.listenBook.greendao.Recommend;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class IoUtils {
	public static final int BOUTIQUE = 0;
	public static final int DOWN = BOUTIQUE + 1;
	public static final int WORD = 3;
	public static final int TIME = WORD + 1;
	public static final int WORK = TIME + 1;
	public static final int GREAT = WORK + 1;
	public static final String[] limitString = { "小学", "初中", "高中" };
	public static final String[] timeString = { "资源类", "科技类", "商业类", "企管类",
			"社会类", "学问类" };
	public static final String[] workString = { "必研大道经典", "巨著汇编", "政经名著",
			"史学名著", "文学名著", "艺术名著" };
	public static final long[] timeId = { 42, 44, 43, 45, 47, 48 };
	public static final long[] workId = { 49, 50, 51, 52, 61, 62 };
	String TAG = this.getClass().getName();
	protected static IoUtils instance;
	DBHelper dbHelper;

	public static IoUtils instance() {
		if (instance == null)
			instance = new IoUtils();
		return instance;
	}

	/**
	 * 预先读取IO数据，得到各种dao对象
	 * */
	public void init(Context context) {
		dbHelper = DBHelper.getInstance(context);
	}

	public Baseuser getUser(String acc) {
		try {
			return dbHelper.getUser(acc);
		} catch (Exception e) {
		}
		return null;
	}

	public boolean saveUser(Baseuser baseuser) {
		try {
			dbHelper.saveUser(baseuser);
		} catch (Exception e) {
		}
		return true;
	}

	public boolean delUserInType(String type) {
		try {
			dbHelper.delUserInType(type);
		} catch (Exception e) {
		}
		return true;
	}

	public boolean delUser(String account) {
		try {
			dbHelper.delUser(account);
		} catch (Exception e) {
		}
		return true;
	}

	/** 获取收藏列表 **/
	public ArrayList<Article> getCollects(long uid) {
		ArrayList<Article> datas = new ArrayList<Article>();
		/** 获取收藏列表 **/
		List<Collect> collects = dbHelper.getCollects(uid);
		Collect cItem;
		Article article;
		for (int i = 0; i < collects.size(); i++) {
			cItem = collects.get(i);
			/** 获取详细信息 **/
			article = dbHelper.getArticlesInaId(cItem.getAid());
			if (article != null && !datas.contains(article))
				datas.add(article);
		}
		return datas;
	}

	public int getCollectSize(long uid) {
		try {
			return dbHelper.getCollectSize(uid);
		} catch (Exception e) {
		}
		return 0;
	}

	public ArrayList<ReadingTrackItem> getReadingTrack() {
		ArrayList<ReadingTrackItem> datas = new ArrayList<ReadingTrackItem>();
		/** 获取历史列表 **/
		List<ReadingTrack> readingTracks = dbHelper.getReadingTrack();
		ReadingTrack item;
		ReadingTrackItem value;
		Article article;
		for (int i = 0; i < readingTracks.size(); i++) {
			item = readingTracks.get(i);
			article = dbHelper.getArticlesInaId(item.getRid());
			if (article != null) {
				value = new ReadingTrackItem(article, item);
				if (!datas.contains(value)) {
					datas.add(value);
				}
			}
		}
		return datas;
	}

	public ArrayList<TabItemInfo> readHomeDatas() {
		ArrayList<TabItemInfo> datas = new ArrayList<TabItemInfo>();
		/** 读取媒体分类 **/
		List<Media> Medias = dbHelper.getAllMedia();
		List<Category> Categorys = new ArrayList<Category>();
		Category _Category;
		int categorysSize;
		for (Media _Media : Medias) {
			long id = _Media.getId();
			// LogUtil.e(getClass().getSimpleName(), "_Media"+_Media.getName());
			int _j = (int) (id - 1);
			//
			if (_j >= 0 && _j <= 3) {
				_j += 3;
			} else if (_j > 3 && _j < 7) {
				_j -= 4;
			}
			//
			// LogUtil.e(getClass().getSimpleName(), "_j"+_j);
			_Media = Medias.get(_j);
			datas.add(new TabItemInfo(_Media.getId(), ToolUtils
					.replaceBlank(_Media.getName()), _Media.getIcon(), _Media
					.getId(), Type.TABLE_TITLE));

			// Categorys = dbHelper.getCategorys(_Media.getId());
			// 去除了小学，初中，高中类
			Categorys = dbHelper.getCategorys(_Media.getId(), limitString[0],
					limitString[1], limitString[2]);
			categorysSize = Categorys.size();
			if (_Media.getId() == Configuration.TYPE_VEDIO
					|| _Media.getId() == Configuration.TYPE_VOICE
					|| _Media.getId() == Configuration.TYPE_BOOK
					|| _Media.getId() == Configuration.TYPE_WORD) {
				if (categorysSize % 2 != 0)
					categorysSize += 1;
				for (int i = 0; i < categorysSize; i++) {
					if (i < Categorys.size()) {
						_Category = Categorys.get(i);
					} else {
						_Category = new Category(0, "", "", "", _Media.getId(),
								-1);
					}
					// LogUtil.e(getClass().getSimpleName(),
					// "type"+_Category.getMedia());
					datas.add(new TabItemInfo(_Category.getCId(), _Category
							.getCName(), _Category.getCIcon(), _Category
							.getMedia(), i % 2 == 0 ? Type.TABLE_GRID_LEFT
							: Type.TABLE_GRID_RIGHT));
				}
			} else {
				if (categorysSize % 3 != 0)
					categorysSize += 1;
				for (int i = 0; i < categorysSize; i++) {
					int type;
					if (i < Categorys.size()) {
						_Category = Categorys.get(i);
					} else {

						_Category = new Category(0, "", "", "", _Media.getId(),
								-1);
						datas.add(new TabItemInfo(_Category.getCId(), _Category
								.getCName(), _Category.getCIcon(), _Category
								.getMedia(), -1));
						if (i % 3 == 1) {
							_Category = new Category(0, "", "", "",
									_Media.getId(), -1);
							datas.add(new TabItemInfo(_Category.getCId(),
									_Category.getCName(), _Category.getCIcon(),
									_Category.getMedia(), -1));
						}
						continue;
					}
					if (i % 3 == 0) {
						type = Type.TABLE_GRID_L;
					} else if (i % 3 == 1) {
						type = Type.TABLE_GRID_M;
					} else {
						type = Type.TABLE_GRID_R;
					}
					datas.add(new TabItemInfo(_Category.getCId(), _Category
							.getCName(), _Category.getCIcon(), _Category
							.getMedia(), type));
				}
			}
		}
		datas.add(new TabItemInfo(-1, "", "", 0, Type.TABLE_FOOTER));
		return datas;
	}

	public List<Long> getALLCid(long Media) {
		List<Category> categorys = dbHelper.getCategorys(Media);
		if (categorys.size() > 0) {
			List<Long> cidList = new ArrayList<Long>();
			for (Category list : categorys) {
				cidList.add(list.getCId());
			}
			categorys.clear();
			return cidList;
		}
		return null;
	}

	public List<Long> getCid(long Media) {
		List<Category> categorys = dbHelper.getCategorys(Media, limitString[0],
				limitString[1], limitString[2]);
		if (categorys.size() > 0) {
			List<Long> cidList = new ArrayList<Long>();
			for (Category list : categorys) {
				cidList.add(list.getCId());
			}
			categorys.clear();
			return cidList;
		}
		return null;
	}

	public List<Long> getCid(long Media, String name) {
		List<Category> categorys = dbHelper.getCategorys(Media, name);
		if (categorys.size() > 0) {
			List<Long> cidList = new ArrayList<Long>();
			for (Category list : categorys) {
				cidList.add(list.getCId());
			}
			categorys.clear();
			return cidList;
		}
		return null;
	}

	public long getCidFromAid(long Aid) {
		return dbHelper.getCidFromAid(Aid);
	}

	public String getCidName(long cid) {
		return dbHelper.getCategoryName(cid);
	}

	public long getMediaFromCid(long cid) {
		return dbHelper.getMediaFromCid(cid);
	}

	public List<Media> getAllMedia() {
		return dbHelper.getAllMedia();
	}

	public List<Media> getExtendMedia(long id) {
		return dbHelper.getExtendMedias(id);
	}

	public void saveMedia(JSONObject json) {
		/** 读取媒体分类 **/
		List<Media> Medias = dbHelper.getAllMedia();
		dbHelper.deleteAllCategory();
		ArrayList<String> downIcons = new ArrayList<String>();
		try {
			JSONObject tmpJson;
			JSONArray jsonArray;
			ArrayList<Category> Categorys = new ArrayList<Category>();
			Category _Category;
			JSONObject _CategoryJson;
			String id;
			String Icon;
			for (Media _Media : Medias) {
				id = String.format("0%d", _Media.getId());
				tmpJson = json.optJSONObject(id);
				if (tmpJson != null) {
					_Media.setName(tmpJson.optString("mediaName",
							_Media.getName()));
					Icon = tmpJson.optString("mediaIcon", _Media.getIcon());
					if (!TextUtils.isEmpty(Icon)
							&& !Icon.equals(_Media.getIcon())) {
						_Media.setIcon(Icon);
						/** 下载更新图片 **/
						if (!downIcons.contains(Icon))
							downIcons.add(Icon);
					}
					dbHelper.upMedia(_Media);
					jsonArray = tmpJson.optJSONArray("category");
					Categorys.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						_CategoryJson = jsonArray.getJSONObject(i);
						_Category = JsonUtils.toCategory(_Media.getId(),
								_CategoryJson);
						Icon = _Category.getCIcon();
						/** 下载更新图片 **/
						if (!TextUtils.isEmpty(Icon)
								&& !downIcons.contains(Icon))
							downIcons.add(Icon);
						Categorys.add(_Category);
					}
					dbHelper.addALLCategory(Categorys);
				}
			}
		} catch (JSONException e) {
			LogUtil.e("saveMedia", e.toString());
		}
		/** 下载图片 */
		ImageManage.instance().downImages(downIcons);
	}

	public void saveArticle(Article article) {
		try {
			dbHelper.saveArticle(article);
		} catch (Exception e) {
		}
	}

	public void removeArticle(long cId) {
		try {
			dbHelper.removeArticle(cId);
		} catch (Exception e) {
		}
	}

	public void saveArticle(ArrayList<Article> articles) {
		try {
			dbHelper.saveArticle(articles);
		} catch (Exception e) {
		}
	}

	public void saveArticleFeedback(ArticleFeedback articleFeedback) {
		try {
			dbHelper.saveArticleFeedback(articleFeedback);
		} catch (Exception e) {
		}
	}

	public void saveArticleFeedback(List<ArticleFeedback> articleFeedback) {
		try {
			dbHelper.saveArticleFeedback(articleFeedback);
		} catch (Exception e) {
		}
	}

	public void saveArticleChapterInfo(ArticleChapterInfo articleChapterInfo) {
		try {
			dbHelper.saveArticleChapterInfo(articleChapterInfo);
		} catch (Exception e) {
		}
	}

	public void delArticleChapterInfo(long aid) {
		try {
			dbHelper.delArticleChapterInfo(aid);
		} catch (Exception e) {
		}
	}

	public void saveArticleChapterInfo(
			ArrayList<ArticleChapterInfo> articleChapterInfo) {
		try {
			dbHelper.saveArticleChapterInfo(articleChapterInfo);
		} catch (Exception e) {
		}
	}

	public List<ArticleItem> getVedioArticle(long cId, String tip) {
		List<Article> articles = dbHelper.getArticlesIncId(cId);
		ArticleItem articleItem;
		Article article;
		ArrayList<ArticleItem> datas = new ArrayList<ArticleItem>();
		if (articles.size() > 0) {
			articleItem = new ArticleItem();
			articleItem.setViewType(Type.TABLE_TITLE);
			articleItem.setAName(tip);
			datas.add(articleItem);
		}
		for (int i = 0; i < articles.size(); i++) {
			article = articles.get(i);
			articleItem = new ArticleItem(article);

			articleItem.setViewType(Type.TABLE_GRID);

			if (!datas.contains(articleItem)) {
				datas.add(articleItem);
			}

		}

		return datas;

	}

	public List<ArticleFeedback> getArticleFeedback(long aid) {
		return dbHelper.getArticleFeedback(aid);
	}

	public List<ArticleChapterItem> getArticleChapterInfo(Activity act, long aid) {
		DownloadManager dlManager = new DownloadManager(
				act.getContentResolver(), act.getPackageName());
		List<ArticleChapterItem> datas = new ArrayList<ArticleChapterItem>();
		List<ArticleChapterInfo> articles = dbHelper
				.getArticleChapterInfos(aid);
		ArticleChapterItem item;
		Down downItem;
		for (ArticleChapterInfo article : articles) {
			item = new ArticleChapterItem(article);
			downItem = dbHelper.getDown(item.getCpId());
			if (downItem != null) {
				/** 获取下载信息 */
				int[] status = dlManager.getBytesAndStatus(downItem.getDId());
				item.upDown(downItem, status);
			}
			datas.add(item);
		}
		/** 需要读取下载数据库 获取下载进度一类数据 **/
		return datas;
	}

	public Article getArticleInfo(long aid) {
		return dbHelper.getArticleInfo(aid);
	}

	public void upArticleChapterInfo(Activity act,
			ArrayList<ArticleChapterItem> items) {
		DownloadManager dlManager = new DownloadManager(
				act.getContentResolver(), act.getPackageName());
		ArticleChapterItem item;
		Down downItem;
		for (int i = 0; i < items.size(); i++) {
			item = items.get(i);
			downItem = dbHelper.getDown(item.getCpId());
			if (downItem != null) {
				/** 获取下载信息 */
				int[] status = dlManager.getBytesAndStatus(downItem.getDId());
				item.upDown(downItem, status);
			}
		}
	}

	public List<ArticleItem> getVoiceArticle(long cId, String tip) {
		List<Article> articles = dbHelper.getArticlesIncId(cId);
		ArticleItem articleItem;
		Article article;
		ArrayList<ArticleItem> datas = new ArrayList<ArticleItem>();
		for (int i = 0; i < articles.size(); i++) {
			article = articles.get(i);
			articleItem = new ArticleItem(article);
			if (datas.size() <= 6) {
				articleItem.setViewType(Type.TABLE_GRID);
			} else {
				articleItem.setViewType(Type.TABLE_ITEM);
			}
			if (!datas.contains(articleItem)) {
				datas.add(articleItem);
			}
			if (datas.size() == 6) {
				articleItem = new ArticleItem();
				articleItem.setViewType(Type.TABLE_TITLE);
				articleItem.setAName(tip);
				datas.add(articleItem);
			}
		}

		return datas;
	}

	public void saveRecommend(Recommend recommend) {
		try {
			dbHelper.saveRecommend(recommend);
		} catch (Exception e) {
		}
	}

	public void saveFreshPush(List<FreshPush> freshPush) {
		try {
			dbHelper.saveFreshPush(freshPush);
		} catch (Exception e) {
		}
	}

	public int getFreshPushCount() {
		return dbHelper.getFreshPushCount();
	}

	public void clearFreshPush() {
		try {
			dbHelper.clearFreshPush();
		} catch (Exception e) {
		}
	}

	public List<FreshPush> getFreshPush() {
		return dbHelper.getFreshPush();
	}

	public void saveRecommend(final List<Recommend> recommends) {
		try {
			ThreadPoolUtils.execute(new Runnable() {

				@Override
				public void run() {
					dbHelper.saveRecommend(recommends);
				}
			});
		} catch (Exception e) {
		}
	}

	// public void replaceRecommend()
	public void clearRecommend(long media) {
		try {
			dbHelper.clearRecommend(media);
		} catch (Exception e) {
		}
	}

	public void clearRecommend() {
		try {
			dbHelper.clearRecommend();
		} catch (Exception e) {
		}
	}

	public void clearRecommendNotId(Long media) {
		try {
			dbHelper.clearRecommendNotId(media);
		} catch (Exception e) {
		}
	}

	public List<Recommend> getRecommend(long media, int size) {
		return dbHelper.getRecommends(media, size);
	}

	public List<Recommend> getRecommend(long media) {
		return dbHelper.getRecommends(media);
	}

	public ArrayList<RecommendItem> getRecommendItems() {
		ArrayList<RecommendItem> array = new ArrayList<RecommendItem>();
		/** 读取分类 */
		List<Media> medias = IoUtils.instance().getAllMedia();
		RecommendItem item;
		Media mediaItem;
		List<Recommend> recommends;

		// 我只能表示，这里怎么乱也是没办法
		item = new RecommendItem();
		item.setAName("核心内容推荐");
		item.setMedia(Configuration.TYPE_BOOK);
		item.viewType = Type.TABLE_TITLE;
		array.add(item);
		item = new RecommendItem();
		item.setAName("《顿悟》三字经");
		item.setAIcon("/images/article/14733188999712.png");
		item.setAId(934);
		item.setAAbstract("《顿悟》三字经");
		item.setAAuthor("刘硕斌老师");
		item.setMedia(Configuration.TYPE_BOOK);
		item.setChapterNum(1);
		item.viewType = Type.TABLE_TITLE_CORE;
		array.add(item);
		item = new RecommendItem();
		item.setAName("生命宣言");
		item.setAIcon("/images/article/14734768049583.png");
		item.setAId(935);
		item.setAAbstract("生命宣言");
		item.setAAuthor("刘硕斌老师");
		item.setMedia(Configuration.TYPE_BOOK);
		item.setChapterNum(1);
		item.viewType = Type.TABLE_TITLE_CORE;
		array.add(item);
		item = new RecommendItem();
		item.setAName("无极禅功");
		item.setAIcon("/images/article/14734770618664.png");
		item.setAId(936);
		item.setAAbstract("无极禅功");
		item.setAAuthor("刘硕斌老师");
		item.setMedia(Configuration.TYPE_BOOK);
		item.setChapterNum(1);
		item.viewType = Type.TABLE_TITLE_CORE;
		array.add(item);
		item = new RecommendItem();
		item.setAName("迷人看名人");
		item.setAIcon("/images/article/14738245068391.jpg");
		item.setAId(87);
		item.setAAbstract("打开心智，顿悟智慧！——《迷人看名人》频道是刘硕斌（法喜）老师以深厚的国学功底，从当前名人的相格、心智及其对应的成功、富贵，进行综合点评的一个前沿级智慧解析频道。\n如何智慧识人？\n什么才是人生成功的真相？\n如何知命？\n如何转变先天命数？\n如何掌控后天运数？\n随着频道的不断推出，在各位大佬级的印证和解析中很容易看清真相，敬请关注！");
		item.setAAuthor("刘硕斌老师");
		item.setMedia(Configuration.TYPE_CATEGORY);
		item.setChapterNum(1);
		item.viewType = Type.TABLE_TITLE_CORE;
		array.add(item);
		item = new RecommendItem();
		item.setAName("未来语文");
		item.setAIcon("/images/article/14738362791030.png");
		item.setAId(86);
		item.setAAbstract("《未来语文》是刘硕斌(法喜)老师针对当前应试教育困境，应变未来学习趋势、未来教改趋势、国学智慧趋势、科技文明趋势、在线教育趋势而揭示出的，集合人类最核心经典学问、最精髓思想、最应变时代概念、最具大道智慧、最精英人物，以顿悟教学法为核心的大学问系列课程。");
		item.setAAuthor("刘硕斌老师");
		item.setMedia(Configuration.TYPE_CATEGORY);
		item.setChapterNum(1);
		item.viewType = Type.TABLE_TITLE_CORE;
		array.add(item);
		for (int i = 0; i < medias.size(); i++) {
			int _j = (int) (i);
			if (_j >= 0 && _j <= 3) {
				_j += 3;
			} else if (_j > 3 && _j < 7) {
				_j -= 4;
			}
			mediaItem = medias.get(_j);
			item = new RecommendItem();
			item.setAName(ToolUtils.replaceBlank(mediaItem.getName()) + "推荐");
			item.setMedia(mediaItem.getId());
			item.viewType = Type.TABLE_TITLE;
			long position = mediaItem.getId();
			if (_j == WORD) {
				recommends = getRecommend(position, 32);
			} else if (_j == TIME) {
				recommends = getRecommend(position, 18);
			} else if (_j == WORK) {
				recommends = getRecommend(position, 12);
			} else if (_j == GREAT) {
				recommends = getRecommend(position, 8);
			} else
				recommends = getRecommend(position, 8);
			if (recommends != null && recommends.size() > 0) {
				array.add(item);
				/** 8个为一组 为了样子 */

				for (int j = 0; j < recommends.size(); j++) {
					if (j < recommends.size()) {
						item = new RecommendItem(recommends.get(j));
						if (_j == WORD) {
							if (j == 0) {
								item.setAName("字类");
								item.viewType = Type.TABLE_TITLE_WORD;
								item.setAId(0);
								array.add(item);
								item = new RecommendItem(recommends.get(j));
							} else if (j == recommends.size() / 2) {
								item.setAName("更多");
								item.viewType = Type.TABLE_TITLE_WORD;
								item.setAId(41);
								item.setAAbstract("字类");
								item.setMedia(Configuration.TYPE_WORD);
								array.add(item);
								item = new RecommendItem(recommends.get(j));
								item.setAName("词类");
								item.viewType = Type.TABLE_TITLE_WORD;
								item.setAId(0);
								array.add(item);
								item = new RecommendItem(recommends.get(j));
							}
							item.viewType = Type.TABLE_WORD;
						} else if (_j == TIME) {
							if (j % 3 == 0) {
								if (j != 0) {
									item.setAName("更多");
									item.viewType = Type.TABLE_TITLE_TIME;
									item.setAId(timeId[j / 3 - 1]);
									item.setAAbstract(timeString[j / 3 - 1]);
									item.setMedia(Configuration.TYPE_WHATTHEFUCK);
									array.add(item);
									item = new RecommendItem(recommends.get(j));
								}
								item.setAName(timeString[j / 3]);
								item.setAId(0);
								item.viewType = Type.TABLE_TITLE_TIME;
								array.add(item);
								item = new RecommendItem(recommends.get(j));
							}
							item.viewType = Type.TABLE_TIME;
						} else if (_j == WORK) {
							if (j % 2 == 0) {
								if (j != 0) {
									item.setAName("更多");
									item.viewType = Type.TABLE_TITLE_WORK;
									item.setAId(workId[j / 2 - 1]);
									item.setAAbstract(workString[j / 2 - 1]);
									item.setMedia(Configuration.TYPE_MASTERWROK);
									array.add(item);
									item = new RecommendItem(recommends.get(j));
								}
								item.setAName(workString[j / 2]);
								item.setAId(0);
								item.viewType = Type.TABLE_TITLE_WORK;
								array.add(item);
								item = new RecommendItem(recommends.get(j));
							}
							item.viewType = Type.TABLE_WORK;
						} else if (_j == GREAT) {
							item.viewType = Type.TABLE_GREAT;
						} else
							item.viewType = Type.TABLE_GRID;
						array.add(item);
					} else {
						item = new RecommendItem();
						if (_j == 3) {
							item.viewType = Type.TABLE_GREAT;
						} else
							item.viewType = Type.TABLE_GRID;
						array.add(item);
					}
				}

				if (_j == WORD) {
					item = new RecommendItem();
					item.setAName("更多");
					item.setAAbstract("词类");
					item.setMedia(Configuration.TYPE_WORD);
					item.viewType = Type.TABLE_TITLE_WORD;
					item.setAId(46);
					array.add(item);
				} else if (_j == TIME) {
					item = new RecommendItem();
					item.setAName("更多");
					item.setAAbstract(timeString[5]);
					item.setMedia(Configuration.TYPE_WORD);
					item.viewType = Type.TABLE_TITLE_TIME;
					item.setAId(timeId[5]);
					array.add(item);
				} else if (_j == WORK) {
					item = new RecommendItem();
					item.setAName("更多");
					item.setAAbstract(workString[5]);
					item.setMedia(Configuration.TYPE_MASTERWROK);
					item.viewType = Type.TABLE_TITLE_WORK;
					item.setAId(workId[5]);
					array.add(item);
				}

				item = new RecommendItem();
				item.setAName("查看更多"
						+ ToolUtils.replaceBlank(mediaItem.getName()) + "推荐");
				item.setMedia(mediaItem.getId());
				item.viewType = Type.TABLE_FOOTER;
				array.add(item);
			}
		}
		return array;
	}

	public void saveRank(Rank rank) {
		try {
			dbHelper.saveRank(rank);
		} catch (Exception e) {
		}
	}

	public void saveRank(List<Rank> rank) {
		try {
			dbHelper.saveRank(rank);
		} catch (Exception e) {
		}
	}

	public void clearRank(int randId) {
		try {
			dbHelper.clearRank(randId);
		} catch (Exception e) {
		}
	}

	public List<Rank> getRanks(int randId) {
		return dbHelper.getRanks(randId);
	}

	public void clearAds() {
		try {
			dbHelper.clearAds();
		} catch (Exception e) {
		}

	}

	public void saveAds(List<Ads> Adss) {
		try {
			dbHelper.saveAds(Adss);
		} catch (Exception e) {
		}

	}

	public List<Ads> getAds(int type) {
		return dbHelper.getAds(type);
	}

	public Collect getCollect(long aid, long uid) {
		return dbHelper.getCollect(aid, uid);
	}

	public void saveCollect(ArrayList<Collect> Collects) {
		dbHelper.saveCollect(Collects);
	}

	public void saveCollect(long aid, long uid) {
		Collect collect = new Collect();
		collect.setCid(aid + ":" + uid);
		collect.setAid(aid);
		collect.setUid(uid);
		dbHelper.saveCollect(collect);
	}

	public void delCollect(Collect collect) {
		dbHelper.delCollect(collect);
	}

	/** 阅读记录 */
	public void saveReadingTrack(long aid, int pos) {
		ReadingTrack track = new ReadingTrack();
		track.setRid(aid);
		track.setPos(pos);
		track.setTime(System.currentTimeMillis());
		dbHelper.saveReadingTrack(track);
	}

	public int getReadingTrack(long aid) {
		int readingTrack = dbHelper.getReadingTrack(aid);
		if (readingTrack < 0)
			return 0;
		return readingTrack;
	}

	public void delReadingTrack(long aid) {
		dbHelper.delReadingTrack(aid);
	}

	public void delReadingTrack() {
		dbHelper.delReadingTrack();
	}

	public static ArrayList<FreshPush> toFreshPushInfo(JSONArray jsonArray) {
		FreshPush item;
		JSONObject jsonTmp;
		ArrayList<FreshPush> saveitems = new ArrayList<FreshPush>();
		if (jsonArray != null && jsonArray.length() > 0) {
			for (int j = 0; j < jsonArray.length(); j++) {
				jsonTmp = jsonArray.optJSONObject(j);
				item = new FreshPush();
				JsonUtils.toFreshPush(item, jsonTmp, Configuration.TYPE_VEDIO);
				saveitems.add(item);
			}
		}
		return saveitems;
	}

	public void saveDown(long aId, long dId, long cpId) {
		Down down = new Down();
		down.setCpId(cpId);
		down.setDId(dId);
		down.setAId(aId);
		dbHelper.saveDown(down);
	}

	public void delDown(long cpId) {
		dbHelper.delDown(cpId);
	}

	public void delDownInAid(long aid) {
		dbHelper.delDownInAid(aid);
	}

	public ArrayList<Article> getDownLoadAll(long media) {
		/*** 获取下载表 */
		List<Down> downList = dbHelper.getDownAll();
		ArrayList<Article> articleList = new ArrayList<Article>();
		Article article;
		for (Down down : downList) {
			article = dbHelper.getArticlesInaId(down.getAId(), media);
			if (article != null && !articleList.contains(article)) {
				articleList.add(article);
			}
		}
		return articleList;
	}

	public List<Down> getDownLoadInAid(long aid) {
		/*** 获取下载表 */
		return dbHelper.getDownInAid(aid);
	}

	public void saveFeedback(ArrayList<Feedback> Feedbacks) {
		dbHelper.saveFeedback(Feedbacks);
	}

	public void saveChipFeedback(ArrayList<ChipFeedback> ChipFeedbacks) {
		dbHelper.saveChipFeedback(ChipFeedbacks);
	}

	public boolean clearChatMsgToRid(long Rid) {
		dbHelper.clearChatMsgToRuid(Rid);
		return true;
	}

	public boolean clearChatMsg() {
		dbHelper.clearChatMsg();
		return true;
	}

	/** 保存 消息 */
	public boolean saveChatMsg(ChatMsg msg) {
		try {
			dbHelper.saveChatMsg(msg);
		} catch (Exception e) {
		}
		return true;
	}

	public boolean removeCharMsg(ChatMsg chatMsg) {
		try {
			dbHelper.removeChatMsg(chatMsg);
		} catch (Exception e) {
		}
		return true;
	}

	public List<ChatMsg> getChatMsgToRuid(long rid, int limit) {
		return dbHelper.getChatMsgToRuid(rid, limit);
	}

	/** 获取消息记录 */
	public List<ChatMsg> getChatMsg(long uid, long rid) {
		return dbHelper.getChatMsg(uid, rid);
	}

	public List<ChatMsg> getUnlineandelayOnlineMsg(long date) {
		// TODO Auto-generated method stub
		return dbHelper.getUnlineandelayOnlineMsg(date);
	}

	public boolean clearnUnlineandelayOnlineMsg(long date) {
		return dbHelper.clearnUnlineandelayOnlineMsg(date);
	}

	public long getChatMsgCount() {
		return dbHelper.getChatMsgCount();
	}

	public long getMsgId(ChatMsg msg) {
		return dbHelper.getMsgId(msg);
	}

	public synchronized List<ChatMsg> getMsgLimit(long id, int limitCount) {
		return dbHelper.getMsgLimit(id, limitCount);
	}

	public void saveChatPeopleInfoList(List<ChatPeopleInfo> list) {
		List<Long> newkeys = new ArrayList<Long>();
		for (ChatPeopleInfo info : list) {
			newkeys.add(info.getUid());
			if (dbHelper.chatPeopleInfoIsExist(info)) {
				// Log.e(TAG, "ChatPeopleInfo" + info.toString());
				continue;
			}
			saveChatPeopleInfo(info);
		}
		// 去掉已经离线的
		list.clear();
		List<Long> oldkeys = getAllChatPeopleInfoKeys();
		for (Long key : oldkeys) {
			if (!newkeys.contains(key)) {
				dbHelper.deleteUnchatPeopleInfo(key);
			}
		}
		oldkeys.clear();
		newkeys.clear();
	}

	public void saveChatPeopleInfo(ChatPeopleInfo chatPeopleInfo) {
		dbHelper.saveChatPeopleInfo(chatPeopleInfo);
	}

	public void clearChatPeopleInfo() {
		dbHelper.clearChatPeopleInfo();
	}

	public List<Long> getAllChatPeopleInfoKeys() {
		return dbHelper.getAllChatPeopleInfoKeys();
	}

	public List<ChatPeopleInfo> getAllChatPeopleInfo() {
		return dbHelper.getAllChatPeopleInfo();
	}

	public ChatPeopleInfo getChatPeopleInfo(long uid) {
		return dbHelper.getChatPeopleInfo(uid);
	}

	public void delChatPeopleInfo(long uid) {
		dbHelper.deleteUnchatPeopleInfo(uid);
	}

	/*
	 * 操作功过表成人版
	 */
	// @SuppressLint("SimpleDateFormat")
	// public boolean saveMeritTableAdult(MeritTableAdult msg){
	// long date =msg.getDate();
	// SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
	// Date curDate=new Date(date);
	// String str=formatter.format(curDate);
	// msg.setDate(Long.parseLong(str));
	// dbHelper.saveMeritTableAdult(msg);
	// return true;
	// }
	//
	// public List<MeritTableAdult> getMeritTableAdultToUid(long uid) {
	// return dbHelper.getMeritTableAdultToUid(uid);
	// }
	// //判断是否保存过 MeritTableAdult的时间应为long
	// public boolean isSaveAdult(MeritTableAdult msg){
	// long date =msg.getDate();
	// SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
	// Date curDate=new Date(date);
	// String str=formatter.format(curDate);
	// long newdate=Long.valueOf(str);
	// ArrayList<MeritTableAdult> lists=(ArrayList<MeritTableAdult>)
	// getMeritTableAdultToUid(msg.getUid());
	// for(MeritTableAdult list:lists){
	// if(list.getDate()>=newdate){
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// public List<MeritTableAdult> getMeritTableAdultToDate(long uid,String
	// minTime,String maxTime){
	// List<MeritTableAdult>
	// lists=getMeritTableAdultToUid(UserInfo.instance().getUid());
	// List<MeritTableAdult> filterlist=new ArrayList<MeritTableAdult>();
	// long minDate=Long.valueOf(minTime);
	// long maxDate=Long.valueOf(maxTime);
	// for(MeritTableAdult list:lists){
	// // long date =list.getDate();
	// // SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
	// // Date curDate=new Date(date);
	// // String str=formatter.format(curDate);
	// long listDate=list.getDate();
	// LogUtil.e(TAG, "minDate"+minDate+"maxDate"+maxDate+"listDate"+listDate);
	// if((listDate>=minDate)&&(listDate<=maxDate)){
	// filterlist.add(list);
	// }
	// }
	// lists.clear();
	// return filterlist;
	// }
	//
	// public boolean claerMeritTableAdultToUid(long uid){
	// dbHelper.clearMeritTableAdultToUid(uid);
	// return true;
	// }
	//
	// /*
	// * 操作功过表孩子版
	// */
	// @SuppressLint("SimpleDateFormat")
	// public boolean saveMeritTableChildren(MeritTableChildren msg){
	// long date =msg.getDate();
	// SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
	// Date curDate=new Date(date);
	// String str=formatter.format(curDate);
	// msg.setDate(Long.parseLong(str));
	// dbHelper.saveMeritTableChildren(msg);
	// return true;
	// }
	//
	// public List<MeritTableChildren> getMeritTableChildrenToUid(long uid) {
	// return dbHelper.getMeritTableChildrenToUid(uid);
	// }
	// //判断是否保存过 MeritTableAdult的时间应为long
	// public boolean isSaveChildren(MeritTableChildren msg){
	// ArrayList<MeritTableChildren> lists=(ArrayList<MeritTableChildren>)
	// getMeritTableChildrenToUid(msg.getUid());
	// for(MeritTableChildren list:lists){
	// if(list.getDate()>=msg.getDate()){
	// return true;
	// }
	// }
	// return false;
	// }
	//
	// public List<MeritTableChildren> getMeritTableChildrenToDate(long
	// uid,String minTime,String maxTime){
	// List<MeritTableChildren>
	// lists=getMeritTableChildrenToUid(UserInfo.instance().getUid());
	// List<MeritTableChildren> filterlist=new ArrayList<MeritTableChildren>();
	// long minDate=Long.valueOf(minTime);
	// long maxDate=Long.valueOf(maxTime);
	// for(MeritTableChildren list:lists){
	// // long date =list.getDate();
	// // SimpleDateFormat formatter=new SimpleDateFormat("yyyyMMdd");
	// // Date curDate=new Date(date);
	// // String str=formatter.format(curDate);
	// long listDate=Long.valueOf(list.getDate());
	// LogUtil.e(TAG,
	// "Children:minDate"+minDate+"maxDate"+maxDate+"listDate"+listDate);
	// if((listDate>=minDate)&&(listDate<=maxDate)){
	// filterlist.add(list);
	// }
	// }
	// lists.clear();
	// return filterlist;
	// }
	//
	// public boolean claerMeritTableChildrenToUid(long uid){
	// dbHelper.clearMeritTableChildrenToUid(uid);
	// return true;
	// }
	public List<LessonInfo> getLessonInfoLast() {
		return dbHelper.getLessonInfoLast();
	}

	public List<LessonInfo> getLessonInfo(String date) {
		return dbHelper.getLessonInfo(date);
	}

	public List<LessonMarksInfo> getLessonMarksInfos(int max) {
		return dbHelper.getLessonMarksInfos(max);
	}

	public LessonMarksInfo getLessonMarksInfo(String date) {
		return dbHelper.getLessonMarksInfo(date);
	}

	public void saveLessonMarksInfo(LessonMarksInfo lessonMarksInfo) {
		dbHelper.saveLessonMarksInfo(lessonMarksInfo);
	}

	public void saveLessonInfos(List<LessonInfo> lessonInfos) {
		dbHelper.saveLessonInfos(lessonInfos);
	}

	public boolean getLessonIsComplete(String date) {
		return dbHelper.getLessonIsComplete(date);
	}

	public void saveLessonItem(LessonItem lessonItem, boolean isComplete) {
		LessonInfo info = new LessonInfo();
		info.setId(lessonItem.id);
		info.setDate(lessonItem.date);
		info.setIssue(lessonItem.issue);
		info.setTitle(lessonItem.title);
		info.setType(lessonItem.type);
		info.setMarks(lessonItem.marks);
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < lessonItem.user_answer.length; i++) {
			if (TextUtils.isEmpty(lessonItem.user_answer[i]))
				lessonItem.user_answer[i] = " ";
			builder.append(lessonItem.user_answer[i]);
			if (i != lessonItem.user_answer.length - 1)
				builder.append("#");
		}
		info.setUser_answer(builder.toString());
		builder = new StringBuilder();
		for (int i = 0; i < lessonItem.answer.length; i++) {
			builder.append(lessonItem.answer[i]);
			if (i != lessonItem.answer.length - 1)
				builder.append("#");
		}
		info.setAnswer(builder.toString());
		if (isComplete)
			info.setOption("");
		else {
			builder = new StringBuilder();
			for (int i = 0; i < lessonItem.option.length; i++) {
				builder.append(lessonItem.option[i]);
				if (i != lessonItem.option.length - 1)
					builder.append("#");
			}
			info.setOption(builder.toString());
		}
		dbHelper.saveCompleteItem(info);
	}

	public ArrayList<DeductCurrencyInfo> getArticleChapterTtack(
			ArrayList<ArticleChapterItem> chapterInfos, long cid, int typeItem) {
		ArrayList<DeductCurrencyInfo> infos = new ArrayList<DeductCurrencyInfo>();
		if (chapterInfos.size() < 1)
			return infos;
		for (ArticleChapterItem item : chapterInfos) {
			ArticleChapterRecode recode = dbHelper.getArticleChapterTtack(item
					.getCpId());
			if (recode == null) {
				recode = new ArticleChapterRecode(item.getCpId());
				recode.setAid(item.getAid());
				recode.setCid(cid);
				recode.setCpname(item.getCpName());
				recode.setPrice((int) item.getMCurrency());
				recode.setStatus(DeductCurrencyInfo.STATUS_NO_BUY);
				recode.setDateline(new Date());
				dbHelper.saveArticleChapterTtack(recode);
			}
			DeductCurrencyInfo deductCurrencyInfo = JsonUtils
					.toDeductCurrencyInfo(recode, typeItem);
			// LogUtil.e(TAG,
			// "IO-"+DateKit.friendlyFormat(deductCurrencyInfo.getDateline()));
			infos.add(deductCurrencyInfo);
		}

		return infos;
	}

	public ArticleChapterRecode getArticleChapterTtackItem(long cpid) {
		return dbHelper.getArticleChapterTtack(cpid);
	}

	public void saveArticleChapterTtack(DeductCurrencyInfo item) {
		ArticleChapterRecode recode = IoUtils.instance()
				.getArticleChapterTtackItem(item.getCpid());
		recode.setAid(item.getAid());
		recode.setCid(item.getCid());
		recode.setCpname(item.getCpname());
		recode.setPrice((int) item.getPrice());
		recode.setStatus(item.getStatus());
		if (recode.getDateline() == null) {
			recode.setDateline(new Date());
		}
		dbHelper.saveArticleChapterTtack(recode);
	}

	public long fromNameGetCid(String trim) {
		return dbHelper.fromNameGetCid(trim);
	}
}
