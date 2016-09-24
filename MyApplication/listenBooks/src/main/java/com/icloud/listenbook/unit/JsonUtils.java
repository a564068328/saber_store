package com.icloud.listenbook.unit;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.R.integer;
import android.text.TextUtils;

import com.android.volley.Response.Listener;
import com.google.gson.Gson;
import com.icloud.listenbook.entity.DeductCurrencyInfo;
import com.icloud.listenbook.entity.LessonItem;
import com.icloud.listenbook.http.HttpUtils;
import com.icloud.listenbook.io.IoUtils;
import com.icloud.listenbook.ui.adapter.entity.ArticleChapterItem;
import com.icloud.listenbook.ui.adapter.entity.FriendItem;
import com.icloud.listenbook.ui.adapter.entity.LectureInfo;
import com.icloud.listenbook.ui.adapter.entity.PPtItem;
import com.icloud.listenbook.ui.adapter.entity.PinterestLikeItem;
import com.icloud.listenbook.ui.adapter.entity.Type;
import com.icloud.wrzjh.base.utils.DateKit;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.icloud.wrzjh.base.utils.SharedPreferenceUtil;
import com.listenBook.greendao.Ads;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.ArticleChapterRecode;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.Category;
import com.listenBook.greendao.ChipFeedback;
import com.listenBook.greendao.Feedback;
import com.listenBook.greendao.FreshPush;
import com.listenBook.greendao.LessonInfo;
import com.listenBook.greendao.LessonMarksInfo;
import com.listenBook.greendao.Media;
import com.listenBook.greendao.OtherUser;
import com.listenBook.greendao.Rank;
import com.listenBook.greendao.Recommend;

public class JsonUtils {
	static Gson gson = new Gson();
	static String TAG = "com.icloud.listenbook.unit.JsonUtils";

	public static String toJson(Object jsonElement) {
		return gson.toJson(jsonElement);
	}

	public static <T> T fromJson(String json, Class<T> classOfT) {
		return gson.fromJson(json, classOfT);
	}

	public static ArrayList<Recommend> toRecommendArray(JSONObject json, String verCode) {
		/** 清空 保存 **/
		ArrayList<Recommend> saveitems = new ArrayList<Recommend>();
		/** 读取分类 */
		List<Media> medias = IoUtils.instance().getAllMedia();
		/** 获取首页Recommend的文章信息 **/
		List<Integer> ids = new ArrayList<Integer>();
		Recommend item;
		Media mediaItem;
		JSONArray jsonArray;
		JSONObject jsonTmp;
		LogUtil.e(TAG, "medias.size()" + medias.size());
		for (int i = 0; i < medias.size(); i++) {
			mediaItem = medias.get(i);
			jsonArray = json.optJSONArray(String.valueOf(mediaItem.getId()));
			if (jsonArray != null && jsonArray.length() > 0) {
				for (int j = 0; j < jsonArray.length(); j++) {
					jsonTmp = jsonArray.optJSONObject(j);
					item = new Recommend();
					item.setMedia(mediaItem.getId());
					JsonUtils
							.toRecommend(item, jsonTmp, mediaItem.getId(), ids);
					if (!saveitems.contains(item))
						saveitems.add(item);
				}
			}
		}
		MethodUtils utils = new MethodUtils();
		int lastId= ids.get(ids.size()-1);
		/* 获取新增内容的文章信息 */
		for (Integer id : ids) {
			utils.getArticleInfo(id,lastId,verCode);
			
		}
		return saveitems;
	}

	public static Category toCategory(long mid, JSONObject item) {
		Category category = new Category();
		category = new Category();
		category.setMedia(mid);
		category.setCId(item.optInt("cId"));
		// LogUtil.e("cId", "id"+item.optInt("cId"));
		category.setCName(item.optString("cName"));
		category.setCIcon(item.optString("cIcon"));
		category.setCDesc(item.optString("cDesc"));
		category.setCSort(item.optInt("cSort"));
		return category;
	}

	public static ArticleChapterItem toArticle(long aid, JSONObject item) {
		ArticleChapterItem infoItem = new ArticleChapterItem();
		infoItem.setCpId(item.optInt("cpId"));
		infoItem.setCpName(item.optString("cpName"));
		infoItem.setCpIcon(item.optString("cpIcon"));
		infoItem.setCpUrl(item.optString("cpUrl"));
		infoItem.setCpDesc(item.optString("cpDesc"));
		infoItem.setCpSize(item.optInt("cpSize"));
		infoItem.setMCurrency((float) item.optDouble("currency"));
		infoItem.setDateline(item.optString("dateline"));
		infoItem.setAid(aid);
		return infoItem;
	}

	public static ArticleFeedback toArticleFeedback(long aid, JSONObject item) {
		ArticleFeedback articleFeedback = new ArticleFeedback();
		articleFeedback.setId(item.optInt("id"));
		articleFeedback.setAid(aid);
		articleFeedback.setUserId(item.optInt("uid"));
		articleFeedback.setIcon(item.optString("icon"));
		articleFeedback.setNick(item.optString("nick"));
		articleFeedback.setAccount(item.optString("account"));
		articleFeedback.setMsg(item.optString("msg"));
		articleFeedback.setDateline(item.optString("dateline"));
		articleFeedback.setStars(item.optInt("stars"));
		return articleFeedback;
	}

	public static Article toArticle(Article Item, JSONObject response) {
		return toArticle(Item, response, Configuration.TYPE_VOICE);
	}

	public static Article toArticle(Article Item, JSONObject response, int media) {
		Item.setAId(response.optLong("aId", Item.getAId()));
		Item.setCId(response.optLong("cId", Item.getCId()));
		Item.setAIcon(response.optString("aIcon"));
		Item.setAAbstract(response.optString("aAbstract"));
		Item.setAAuthor(response.optString("aAuthor"));
		Item.setChapterNum(response.optInt("chapterNum", 0));
		Item.setClickConut(response.optInt("clickNum", 0));
		Item.setStatus(response.optInt("status"));
		Item.setSubCaterory(response.optString("subCaterory"));
		Item.setBroadAuthor(response.optString("broadAuthor"));
		Item.setDateline(response.optString("dateline"));
		Item.setAName(response.optString("aName"));
		Item.setADesc(response.optString("aDesc"));
		Item.setPrice((float) response.optDouble("price", 0));
		Item.setV1Price((float) response.optDouble("v1Price", 0));
		Item.setMedia(response.optInt("media", media));

		return Item;
	}

	public static Recommend toRecommend(Recommend item, JSONObject response,
			long media) {
		if (item == null)
			item = new Recommend();
		item.setAId(response.optInt("aId"));
		item.setAIcon(response.optString("aIcon"));
		item.setAName(response.optString("aName"));
		item.setAAbstract(response.optString("aAbstract"));
		item.setAAuthor(response.optString("aAuthor"));
		item.setChapterNum(response.optInt("chapterNum"));
		item.setMedia(media);
		return item;
	}

	public static Recommend toRecommend(Recommend item, JSONObject response,
			long media, List<Integer> ids) {
		if (item == null)
			item = new Recommend();
		item.setAId(response.optInt("aId"));
		item.setAIcon(response.optString("aIcon"));
		item.setAName(response.optString("aName"));
		item.setAAbstract(response.optString("aAbstract"));
		item.setAAuthor(response.optString("aAuthor"));
		item.setChapterNum(response.optInt("chapterNum"));
		item.setMedia(media);
		if (media == Configuration.TYPE_WORD
				|| media == Configuration.TYPE_WHATTHEFUCK
				|| media == Configuration.TYPE_MASTERWROK) {
			ids.add(response.optInt("aId"));
		}
		return item;
	}

	public static FreshPush toFreshPush(FreshPush item, JSONObject response,
			long media) {
		if (item == null)
			item = new FreshPush();
		item.setAId(response.optInt("aId"));
		item.setAIcon(response.optString("aIcon"));
		item.setAName(response.optString("aName"));
		item.setAAbstract(response.optString("aAbstract"));
		item.setAAuthor(response.optString("aAuthor"));
		item.setChapterNum(response.optInt("chapterNum"));
		item.setMedia(media);
		return item;
	}

	public static Ads toAds(int type, JSONObject response) {
		Ads item = new Ads();
		item.setType(type);
		item.setAid(response.optInt("aid"));
		item.setIcon(response.optString("icon"));
		item.setUrl(response.optString("url"));
		item.setMedia(response.optLong("media"));
		item.setCategory(response.optInt("category"));
		item.setArticle(response.optInt("article"));
		return item;
	}

	public static Rank toRank(String type, int rankid, JSONObject response) {
		Rank ranksItem = new Rank();
		ranksItem.setAId(response.optInt("aId"));
		ranksItem.setAIcon(response.optString("aIcon"));
		ranksItem.setAName(response.optString("aName"));
		ranksItem.setAAbstract(response.optString("aAbstract"));
		ranksItem.setAAuthor(response.optString("aAuthor"));
		ranksItem.setNum(response.optInt("num"));
		ranksItem.setMedia(response.optInt("media"));
		ranksItem.setType(type);
		ranksItem.setRankid(rankid);
		return ranksItem;
	}

	public static Feedback toFeedback(JSONObject response) {
		Feedback item = new Feedback();
		item.setDateline(response.optString("dateline"));
		item.setMsg(response.optString("msg"));
		item.setFid(response.optLong("fid"));
		return item;
	}

	public static ChipFeedback toChipFeedback(long fid, JSONObject response) {
		ChipFeedback item = new ChipFeedback();
		item.setDateline(response.optString("dateline"));
		item.setMsg(response.optString("msg"));
		item.setUid(response.optLong("uid"));
		item.setFid(fid);
		return item;
	}

	public static FriendItem toFriendItem(JSONObject response) {
		FriendItem user = new FriendItem();
		user.setUid(response.optLong("uid"));
		user.setIcon(response.optString("icon"));
		user.setNick(response.optString("nick"));
		user.setArea(response.optString("area"));
		user.setSignature(response.optString("signature"));
		user.setIsfriend(response.optInt("isfriend") == 1);
		user.isSendChatMsg = false;
		return user;
	}

	public static OtherUser toOtherUser(JSONObject response) {
		OtherUser user = new OtherUser();
		user.setUid(response.optLong("uid"));
		user.setNick(response.optString("nick"));
		user.setIcon(response.optString("icon"));
		user.setArea(response.optString("area"));
		user.setSignature(response.optString("signature"));
		user.setIsfriend(response.optInt("isfriend") == 1);
		user.setFriendsCount(response.optInt("friendsCount"));
		user.setCollectCount(response.optInt("collectCount"));
		return user;
	}

	public static PinterestLikeItem toPinterestLikeItem(JSONObject response) {
		PinterestLikeItem item = new PinterestLikeItem();
		item.id = response.optInt("id");
		item.title = (response.optString("title"));
		item.time = (response.optString("createtime"));
		item.icon = (response.optString("icon"));
		item.url = (response.optString("url"));
		return item;
	}

	public static LectureInfo toLectureInfo(JSONObject response) {
		LectureInfo item = new LectureInfo();
		item.id = response.optInt("id");
		item.title = (response.optString("title"));
		item.time = (response.optString("createtime"));
		item.icon = (response.optString("icon"));
		item.isstart = (response.optInt("isstart", 0) == 1);
		item.topic = (response.optString("topic"));
		item.intro = (response.optString("intro"));
		item.pptid = response.optInt("pptid", 0);
		item.roomid = response.optInt("roomid", 0);
		item.uri = (response.optString("uri"));
		return item;
	}

	public static ArrayList<PPtItem> toPPtItems(JSONArray json) {
		ArrayList<PPtItem> array = new ArrayList<PPtItem>();
		JSONObject jsonObject;
		PPtItem _PPtItem;
		if (json != null)
			for (int i = 0; i < json.length(); i++) {
				jsonObject = json.optJSONObject(i);
				_PPtItem = new PPtItem();
				_PPtItem.name = jsonObject.optString("name", "");
				_PPtItem.url = jsonObject.optString("url", "");
				_PPtItem.icon = jsonObject.optString("icon", "");
				_PPtItem.id = jsonObject.optInt("id");
				array.add(_PPtItem);
			}
		return array;
	}

	public static ArrayList<LessonItem> toChantItems(int max) {
		List<LessonMarksInfo> infos = IoUtils.instance().getLessonMarksInfos(
				max);
		ArrayList<LessonItem> lessonItems = new ArrayList<LessonItem>();
		for (LessonMarksInfo info : infos) {
			LessonItem item = new LessonItem();
			item.date = info.getDate();
			item.chant = info.getChant();
			lessonItems.add(item);
			// LogUtil.e(TAG,"toChantItems"+item.toString());
		}
		infos.clear();
		return lessonItems;
	}

	public static ArrayList<LessonItem> toLessonItems(String date) {
		List<LessonInfo> infos;
		boolean iscomplete;
		if (TextUtils.isEmpty(date)) {
			infos = IoUtils.instance().getLessonInfoLast();
			date = infos.get(0).getDate();
		} else {
			infos = IoUtils.instance().getLessonInfo(date);
		}
		iscomplete = IoUtils.instance().getLessonIsComplete(date);
		ArrayList<LessonItem> lessonItems = new ArrayList<LessonItem>();
		int position = 0;
		for (LessonInfo info : infos) {
			LessonItem item = new LessonItem();
			item.id = info.getId();
			item.date = info.getDate();
			item.position = position;
			item.title = info.getTitle();
			item.issue = info.getIssue();
			item.type = info.getType();
			try {
				item.marks = info.getMarks();
				// LogUtil.e(TAG,"marks="+item.marks );
			} catch (Exception e) {
				e.printStackTrace();
				item.marks = 10;
			}
			if (info.getType() == LessonItem.CHOICE) {
				item.option = info.getOption().split("#");
			} else {
				item.option = new String[0];
			}
			item.answer = info.getAnswer().split("#");
			if (TextUtils.isEmpty(info.getUser_answer())) {
				if (info.getType() == LessonItem.CHOICE)
					item.user_answer = new String[item.option.length];
				else {
					item.user_answer = new String[item.answer.length];
				}
			} else {
				item.user_answer = info.getUser_answer().split("#");
			}
			// else if (!iscomplete && !TextUtils.isEmpty(info.getAnswer())) {
			// item.user_answer=info.getUser_answer().;
			// }
			position++;
			// LogUtil.e(TAG, "toLessonItems" + item.toString());
			lessonItems.add(item);
		}
		if (!iscomplete) {
			LessonItem item = new LessonItem();
			item.type = 0;
			lessonItems.add(item);
			infos.clear();
		}
		return lessonItems;
	}

	public static ArrayList<LessonItem> toLessonMarksItems(int max) {
		List<LessonMarksInfo> infos = IoUtils.instance().getLessonMarksInfos(
				max);
		ArrayList<LessonItem> lessonItems = new ArrayList<LessonItem>();
		for (LessonMarksInfo info : infos) {
			LessonItem item = new LessonItem();
			item.date = info.getDate();
			item.marks = info.getMarks();
			item.iscomplete = info.getIscomplete();
			item.describe = info.getDescribe();
			item.right_count = info.getRight_count();
			item.type = LessonItem.SUMBIT;
			// LogUtil.e(TAG,"toLessonMarksItems"+item.toString());
			lessonItems.add(item);
		}
		infos.clear();
		return lessonItems;
	}

	public static void refreshChant(JSONObject response) {
		try {
			int res = response.getInt("result");
			if (res == 0) {
				String version = response.optString("version");
				JSONArray list = response.optJSONArray("list");
				if (list.length() == 0)
					return;
				if (version.equals(SharedPreferenceUtil.getChantVersion()))
					return;
				SharedPreferenceUtil.saveChantVresion(version);
				String chant = response.optString("chant");
				list = response.optJSONArray("list");
				LessonMarksInfo lessonMarksInfo = new LessonMarksInfo();
				lessonMarksInfo.setChant(chant);
				lessonMarksInfo.setDate(version);
				lessonMarksInfo.setDescribe("未完成");
				lessonMarksInfo.setIscomplete(false);
				lessonMarksInfo.setRight_count(0);
				lessonMarksInfo.setMarks(0);
				IoUtils.instance().saveLessonMarksInfo(lessonMarksInfo);
				JSONObject object;
				List<LessonInfo> lessonInfos = new ArrayList<LessonInfo>();
				LessonInfo lessonInfo;
				for (int i = 0; i < list.length(); i++) {
					object = list.optJSONObject(i);
					lessonInfo = new LessonInfo();
					int type = object.optInt("type");
					String title = object.optString("title");
					String issue = object.optString("issue");
					String option = object.optString("option");
					String answer = object.optString("answer");
					int marks = object.optInt("marks");
					LogUtil.e(TAG, "marks" + marks);
					lessonInfo.setAnswer(answer);
					lessonInfo.setDate(version);
					lessonInfo.setIssue(issue);
					lessonInfo.setOption(option);
					lessonInfo.setTitle(title);
					lessonInfo.setType(type);
					lessonInfo.setMarks(marks);
					lessonInfo.setUser_answer("");
					lessonInfos.add(lessonInfo);
				}
				IoUtils.instance().saveLessonInfos(lessonInfos);
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}

	public static DeductCurrencyInfo toDeductCurrencyInfo(ArticleChapterRecode recode, int type) {
		DeductCurrencyInfo info=new DeductCurrencyInfo();
		info.setCpid(recode.getCpid());
		info.setCid(recode.getCid());
		info.setAid(recode.getAid());
		info.setPrice(recode.getPrice());
		info.setStatus(recode.getStatus());
		info.setCpname(recode.getCpname());
		info.setDateline(recode.getDateline());
		info.setType(type);
		info.setPos(IoUtils.instance().getReadingTrack(info.getCpid()));
		info.setDateline(recode.getDateline());
		
		return info;
	}
}
