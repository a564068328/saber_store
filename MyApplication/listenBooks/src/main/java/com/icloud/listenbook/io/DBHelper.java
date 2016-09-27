package com.icloud.listenbook.io;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.R.integer;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.icloud.listenbook.entity.DeductCurrencyInfo;
import com.icloud.listenbook.unit.ChatMsgManage;
import com.icloud.listenbook.unit.ToolUtils;
import com.icloud.wrzjh.base.utils.LogUtil;
import com.listenBook.greendao.Ads;
import com.listenBook.greendao.AdsDao;
import com.listenBook.greendao.Article;
import com.listenBook.greendao.ArticleChapterInfo;
import com.listenBook.greendao.ArticleChapterInfoDao;
import com.listenBook.greendao.ArticleChapterRecode;
import com.listenBook.greendao.ArticleChapterRecodeDao;
import com.listenBook.greendao.ArticleDao;
import com.listenBook.greendao.ArticleFeedback;
import com.listenBook.greendao.ArticleFeedbackDao;
import com.listenBook.greendao.Baseuser;
import com.listenBook.greendao.BaseuserDao;
import com.listenBook.greendao.Category;
import com.listenBook.greendao.CategoryDao;
import com.listenBook.greendao.CategoryDao.Properties;
import com.listenBook.greendao.ChatMsg;
import com.listenBook.greendao.ChatMsgDao;
import com.listenBook.greendao.ChatPeopleInfo;
import com.listenBook.greendao.ChatPeopleInfoDao;
import com.listenBook.greendao.ChipFeedback;
import com.listenBook.greendao.ChipFeedbackDao;
import com.listenBook.greendao.Collect;
import com.listenBook.greendao.CollectDao;
import com.listenBook.greendao.DaoSession;
import com.listenBook.greendao.Down;
import com.listenBook.greendao.DownDao;
import com.listenBook.greendao.Feedback;
import com.listenBook.greendao.FeedbackDao;
import com.listenBook.greendao.FreshPush;
import com.listenBook.greendao.FreshPushDao;
import com.listenBook.greendao.LessonInfo;
import com.listenBook.greendao.LessonInfoDao;
import com.listenBook.greendao.LessonMarksInfo;
import com.listenBook.greendao.LessonMarksInfoDao;
import com.listenBook.greendao.Media;
import com.listenBook.greendao.MediaDao;
import com.listenBook.greendao.MeritTableAdult;
import com.listenBook.greendao.MeritTableAdultDao;
import com.listenBook.greendao.MeritTableChildren;
import com.listenBook.greendao.MeritTableChildrenDao;
import com.listenBook.greendao.Rank;
import com.listenBook.greendao.RankDao;
import com.listenBook.greendao.ReadingTrack;
import com.listenBook.greendao.ReadingTrackDao;
import com.listenBook.greendao.Recommend;
import com.listenBook.greendao.RecommendDao;

import de.greenrobot.dao.query.QueryBuilder;

public class DBHelper {
	private static final String TAG = DBHelper.class.getSimpleName();
	private static DBHelper instance;
	private static Context appContext;
	private DaoSession daoSession;
	private BaseuserDao baseuserDao;
	private ArticleDao articleDao;
	private CategoryDao categoryDao;
	private MediaDao mediaDao;
	private ArticleFeedbackDao articleFeedbackDao;
	private ArticleChapterInfoDao articleChapterInfoDao;
	private RecommendDao recommendDao;
	private RankDao rankDao;
	private AdsDao adsDao;
	private DownDao downDao;
	private CollectDao collectDao;
	private ReadingTrackDao readingTrackDao;
	private FeedbackDao feedbackDao;
	private ChipFeedbackDao chipFeedbackDao;
	private ChatMsgDao chatMsgDao;
	private FreshPushDao freshPushDao;
	private MeritTableAdultDao meritTableAdultDao;
	private MeritTableChildrenDao meritTableChildrenDao;
	private ChatPeopleInfoDao chatPeopleInfoDao;
	private LessonInfoDao lessonInfoDao;
	private LessonMarksInfoDao lessonMarksInfoDao;
	private ArticleChapterRecodeDao articleChapterRecodeDao;

	public static DBHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DBHelper();
			if (appContext == null) {
				appContext = context.getApplicationContext();
			}
			instance.daoSession = DaoManage.instance().getDaoSession(context);
			// 添加一个新的note到数据库中
			instance.baseuserDao = instance.daoSession.getBaseuserDao();
			instance.categoryDao = instance.daoSession.getCategoryDao();
			instance.mediaDao = instance.daoSession.getMediaDao();
			instance.articleDao = instance.daoSession.getArticleDao();
			instance.articleChapterInfoDao = instance.daoSession
					.getArticleChapterInfoDao();
			instance.articleFeedbackDao = instance.daoSession
					.getArticleFeedbackDao();
			instance.recommendDao = instance.daoSession.getRecommendDao();
			instance.rankDao = instance.daoSession.getRankDao();
			instance.adsDao = instance.daoSession.getAdsDao();
			instance.downDao = instance.daoSession.getDownDao();
			instance.collectDao = instance.daoSession.getCollectDao();
			instance.readingTrackDao = instance.daoSession.getReadingTrackDao();
			instance.feedbackDao = instance.daoSession.getFeedbackDao();
			instance.chipFeedbackDao = instance.daoSession.getChipFeedbackDao();
			instance.chatMsgDao = instance.daoSession.getChatMsgDao();
			instance.freshPushDao = instance.daoSession.getFreshPushDao();
			instance.meritTableAdultDao = instance.daoSession
					.getMeritTableAdultDao();
			instance.meritTableChildrenDao = instance.daoSession
					.getMeritTableChildrenDao();
			instance.chatPeopleInfoDao = instance.daoSession
					.getChatPeopleInfoDao();
			instance.lessonInfoDao = instance.daoSession.getLessonInfoDao();
			instance.lessonMarksInfoDao = instance.daoSession
					.getLessonMarksInfoDao();
			instance.articleChapterRecodeDao = instance.daoSession
					.getArticleChapterRecodeDao();
		}
		return instance;
	}

	// 获得所有的Session倒序排存到List列表里面
	public List<Media> getAllMedia() {
		return mediaDao.loadAll();
	}

	public List<Media> getExtendMedias(long id) {
		QueryBuilder<Media> mqBuilder = mediaDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.MediaDao.Properties.Id.eq(id));
		return mqBuilder.list();
	}

	public List<Category> getAllCategory() {
		QueryBuilder<Category> mqBuilder = categoryDao.queryBuilder();
		mqBuilder.orderAsc(Properties.CSort);
		return mqBuilder.list();
	}

	public List<Category> getCategorys(long MediaId) {
		QueryBuilder<Category> mqBuilder = categoryDao.queryBuilder();
		mqBuilder.where(Properties.Media.eq(MediaId))
				.orderAsc(Properties.CSort);
		return mqBuilder.list();
	}

	public List<Category> getCategorys(long MediaId, String name1,
			String name2, String name3) {
		QueryBuilder<Category> mqBuilder = categoryDao.queryBuilder();
		mqBuilder.where(Properties.Media.eq(MediaId),
				Properties.CName.notEq(name1), Properties.CName.notEq(name2),
				Properties.CName.notEq(name3)).orderAsc(Properties.CSort);
		return mqBuilder.list();
	}

	public List<Category> getCategorys(long MediaId, String name) {
		QueryBuilder<Category> mqBuilder = categoryDao.queryBuilder();
		mqBuilder
				.where(Properties.Media.eq(MediaId), Properties.CName.eq(name))
				.limit(1);
		return mqBuilder.list();
	}

	public String getCategoryName(long cid) {
		QueryBuilder<Category> mqBuilder = categoryDao.queryBuilder();
		mqBuilder.where(Properties.CId.eq(cid));
		return mqBuilder.list().get(0).getCName();
	}

	public long getMediaFromCid(long cid) {
		QueryBuilder<Category> mqBuilder = categoryDao.queryBuilder();
		mqBuilder.where(Properties.CId.eq(cid)).limit(1);
		return mqBuilder.list().get(0).getMedia();
	}

	public boolean upMedia(Media _Media) {
		mediaDao.insertOrReplace(_Media);
		return true;
	}

	public boolean deleteAllCategory() {
		categoryDao.deleteAll();
		return true;
	}

	public boolean addALLCategory(List<Category> Categorys) {
		categoryDao.insertOrReplaceInTx(Categorys);
		return true;
	}

	public Baseuser getLastUser(String type) {
		QueryBuilder<Baseuser> mqBuilder = baseuserDao.queryBuilder();
		mqBuilder
				.where(com.listenBook.greendao.BaseuserDao.Properties.Type
						.eq(type))
				.orderDesc(com.listenBook.greendao.BaseuserDao.Properties.Date)
				.limit(1);

		return mqBuilder.unique();
	}

	public Baseuser getLastUser() {

		QueryBuilder<Baseuser> mqBuilder = baseuserDao.queryBuilder();
		mqBuilder
				.orderDesc(com.listenBook.greendao.BaseuserDao.Properties.Date)
				.limit(1);

		return mqBuilder.unique();
	}

	public boolean delUserInType(String type) {
		QueryBuilder<Baseuser> mqBuilder = baseuserDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.BaseuserDao.Properties.Type
				.eq(type));
		List<Baseuser> chatEntityList = mqBuilder.build().list();
		baseuserDao.deleteInTx(chatEntityList);
		return true;
	}

	public boolean delUser(String account) {
		QueryBuilder<Baseuser> mqBuilder = baseuserDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.BaseuserDao.Properties.Account
				.eq(account));
		List<Baseuser> chatEntityList = mqBuilder.build().list();
		baseuserDao.deleteInTx(chatEntityList);
		return true;
	}

	public Baseuser getUser(String account) {
		QueryBuilder<Baseuser> mqBuilder = baseuserDao.queryBuilder();
		mqBuilder.where(
				com.listenBook.greendao.BaseuserDao.Properties.Account
						.eq(account)).limit(1);
		return mqBuilder.unique();
	}

	public boolean saveUser(Baseuser baseuser) {
		baseuserDao.insertOrReplace(baseuser);

		return true;
	}

	public boolean saveArticle(Article Article) {
		articleDao.insertOrReplace(Article);
		return true;
	}

	public boolean saveArticle(List<Article> Article) {
		articleDao.insertOrReplaceInTx(Article);
		return true;
	}

	public boolean removeArticle(long cId) {
		QueryBuilder<Article> mqBuilder = articleDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.ArticleDao.Properties.CId
				.eq(cId));
		articleDao.deleteInTx(mqBuilder.list());
		return true;
	}

	public List<Article> getArticlesIncId(long cId) {
		QueryBuilder<Article> mqBuilder = articleDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.ArticleDao.Properties.CId
				.eq(cId));
		return mqBuilder.list();
	}

	public Article getArticlesInaId(long aid) {
		QueryBuilder<Article> mqBuilder = articleDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.ArticleDao.Properties.AId
				.eq(aid));
		mqBuilder.limit(1);
		return mqBuilder.unique();
	}

	public long getCidFromAid(long aid) {
		LogUtil.e(TAG, "aid" + aid);
		QueryBuilder<Article> mqBuilder = articleDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.ArticleDao.Properties.AId
				.eq(aid));
		return mqBuilder.list().get(0).getCId();
	}

	public long fromNameGetCid(String cname) {
		QueryBuilder<Category> qb = categoryDao.queryBuilder();
		qb.where(com.listenBook.greendao.CategoryDao.Properties.CName.eq(cname));
		Category category = qb.limit(1).unique();
		if (category == null)
			return 0;
		else
			return category.getCId();
	}

	public Article getArticlesInaId(long aid, long media) {
		QueryBuilder<Article> mqBuilder = articleDao.queryBuilder();
		mqBuilder.where(
				com.listenBook.greendao.ArticleDao.Properties.AId.eq(aid),
				com.listenBook.greendao.ArticleDao.Properties.Media.eq(media));
		mqBuilder.limit(1);
		return mqBuilder.unique();
	}

	public List<ArticleFeedback> getArticleFeedback(long aid) {
		QueryBuilder<ArticleFeedback> mqBuilder = articleFeedbackDao
				.queryBuilder();
		mqBuilder
				.where(com.listenBook.greendao.ArticleFeedbackDao.Properties.Aid
						.eq(aid));
		if (mqBuilder.list() == null)
			return new ArrayList<ArticleFeedback>();
		return mqBuilder.list();
	}

	public boolean saveArticleFeedback(ArticleFeedback articleFeedback) {
		articleFeedbackDao.insertOrReplace(articleFeedback);
		return true;
	}

	public boolean saveArticleFeedback(List<ArticleFeedback> articleFeedback) {
		articleFeedbackDao.insertOrReplaceInTx(articleFeedback);
		return true;
	}

	public boolean saveArticleChapterInfo(ArticleChapterInfo articleChapterInfo) {
		articleChapterInfoDao.insertOrReplace(articleChapterInfo);
		return true;
	}

	public boolean saveArticleChapterInfo(
			List<ArticleChapterInfo> articleChapterInfo) {
		articleChapterInfoDao.insertOrReplaceInTx(articleChapterInfo);
		return true;
	}

	public boolean delArticleChapterInfo(long aid) {
		QueryBuilder<ArticleChapterInfo> mqBuilder = articleChapterInfoDao
				.queryBuilder();
		mqBuilder
				.where(com.listenBook.greendao.ArticleChapterInfoDao.Properties.Aid
						.eq(aid));
		articleChapterInfoDao.deleteInTx(mqBuilder.list());
		return true;
	}

	public List<ArticleChapterInfo> getArticleChapterInfos(long aid) {
		QueryBuilder<ArticleChapterInfo> mqBuilder = articleChapterInfoDao
				.queryBuilder();
		mqBuilder
				.where(com.listenBook.greendao.ArticleChapterInfoDao.Properties.Aid
						.eq(aid));
		return mqBuilder.list();
	}

	public Article getArticleInfo(long aid) {
		QueryBuilder<Article> mqBuilder = articleDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.ArticleDao.Properties.AId
				.eq(aid));
		return mqBuilder.unique();
	}

	public ArticleChapterInfo getArticleChapterInfosInCpId(long cpid) {
		QueryBuilder<ArticleChapterInfo> mqBuilder = articleChapterInfoDao
				.queryBuilder();
		mqBuilder
				.where(com.listenBook.greendao.ArticleChapterInfoDao.Properties.CpId
						.eq(cpid));
		return mqBuilder.unique();
	}

	public boolean saveRecommend(Recommend recommend) {
		recommendDao.insertOrReplace(recommend);
		return true;
	}

	public List<Recommend> getRecommends(long Media, int limit) {
		QueryBuilder<Recommend> mqBuilder = recommendDao.queryBuilder();
		mqBuilder
				.where(com.listenBook.greendao.RecommendDao.Properties.Media
						.eq(Media))
				.orderAsc(com.listenBook.greendao.RecommendDao.Properties.AId)
				.limit(limit);
		return mqBuilder.list();
	}

	public List<Recommend> getRecommends(long Media) {
		QueryBuilder<Recommend> mqBuilder = recommendDao.queryBuilder();
		mqBuilder
				.where(com.listenBook.greendao.RecommendDao.Properties.Media
						.eq(Media)).orderAsc(
						com.listenBook.greendao.RecommendDao.Properties.AId);

		return mqBuilder.list();
	}

	public boolean saveRank(Rank rank) {
		rankDao.insertOrReplace(rank);
		return false;
	}

	public boolean saveRank(List<Rank> rank) {
		rankDao.insertOrReplaceInTx(rank);
		return false;
	}

	public boolean clearRank(int rankid) {
		QueryBuilder<Rank> mqBuilder = rankDao.queryBuilder();
		mqBuilder.where(
				com.listenBook.greendao.RankDao.Properties.Rankid.eq(rankid))
				.orderAsc(com.listenBook.greendao.RankDao.Properties.AId);
		rankDao.deleteInTx(mqBuilder.list());
		return true;
	}

	public List<Rank> getRanks(int rankid) {
		QueryBuilder<Rank> mqBuilder = rankDao.queryBuilder();
		mqBuilder.where(
				com.listenBook.greendao.RankDao.Properties.Rankid.eq(rankid))
				.orderAsc(com.listenBook.greendao.RankDao.Properties.AId);

		return mqBuilder.list();
	}

	public boolean clearAds() {
		adsDao.deleteAll();
		return true;
	}

	public boolean saveAds(List<Ads> Adss) {
		adsDao.insertOrReplaceInTx(Adss);
		return true;
	}

	public List<Ads> getAds(int type) {
		QueryBuilder<Ads> mqBuilder = adsDao.queryBuilder();
		mqBuilder
				.where(com.listenBook.greendao.AdsDao.Properties.Type.eq(type))
				.orderAsc(com.listenBook.greendao.AdsDao.Properties.Aid);
		return mqBuilder.list();
	}

	public void saveFreshPush(List<FreshPush> freshPush) {
		freshPushDao.insertOrReplaceInTx(freshPush);
	}

	public int getFreshPushCount() {
		return (int) freshPushDao.count();
	}

	public void saveRecommend(List<Recommend> recommends) {
		recommendDao.insertOrReplaceInTx(recommends);
	}

	public void clearRecommendNotId(long media) {
		List<Long> keys = new ArrayList<Long>();
		List<Media> allMedia = getAllMedia();
		for (Media medialist : allMedia) {
			if (medialist.getId() != media)
				keys.add(medialist.getId());
		}
		recommendDao.deleteByKeyInTx(keys);
	}

	public void clearRecommend() {
		recommendDao.deleteAll();
	}

	public void clearFreshPush() {
		freshPushDao.deleteAll();
	}

	public List<FreshPush> getFreshPush() {
		return freshPushDao.loadAll();
	}

	public void clearRecommend(long media) {
		QueryBuilder<Recommend> mqBuilder = recommendDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.RecommendDao.Properties.Media
				.eq(media));
		recommendDao.deleteInTx(mqBuilder.list());
	}

	public Down getDown(long cpId) {
		QueryBuilder<Down> mqBuilder = downDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.DownDao.Properties.CpId
				.eq(cpId));
		return mqBuilder.unique();
	}

	public List<Down> getDownInAid(long aid) {
		QueryBuilder<Down> mqBuilder = downDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.DownDao.Properties.AId.eq(aid));
		return mqBuilder.list();
	}

	public List<Down> getDownAll() {
		QueryBuilder<Down> mqBuilder = downDao.queryBuilder();
		return mqBuilder.list();
	}

	public void saveDown(Down down) {
		downDao.insertOrReplace(down);
	}

	public List<Collect> getCollects() {
		QueryBuilder<Collect> mqBuilder = collectDao.queryBuilder();
		mqBuilder.orderAsc(com.listenBook.greendao.CollectDao.Properties.Cid);
		return mqBuilder.list();
	}

	public int getCollectSize(long uid) {
		QueryBuilder<Collect> mqBuilder = collectDao.queryBuilder();
		mqBuilder.where(
				com.listenBook.greendao.CollectDao.Properties.Uid.eq(uid))
				.orderAsc(com.listenBook.greendao.CollectDao.Properties.Cid);
		return mqBuilder.list().size();
	}

	public List<Collect> getCollects(long uid) {
		QueryBuilder<Collect> mqBuilder = collectDao.queryBuilder();
		mqBuilder.where(
				com.listenBook.greendao.CollectDao.Properties.Uid.eq(uid))
				.orderAsc(com.listenBook.greendao.CollectDao.Properties.Cid);
		return mqBuilder.list();
	}

	public Collect getCollect(long aid, long uid) {
		QueryBuilder<Collect> mqBuilder = collectDao.queryBuilder();
		mqBuilder.where(
				com.listenBook.greendao.CollectDao.Properties.Aid.eq(aid),
				com.listenBook.greendao.CollectDao.Properties.Uid.eq(uid));
		mqBuilder.limit(1);
		return mqBuilder.unique();
	}

	public boolean saveCollect(Collect collect) {
		collectDao.insertOrReplaceInTx(collect);
		return true;
	}

	public boolean saveCollect(ArrayList<Collect> collect) {
		collectDao.insertOrReplaceInTx(collect);
		return true;
	}

	public void delCollect(Collect collect) {
		collectDao.delete(collect);

	}

	public void saveReadingTrack(ReadingTrack track) {
		readingTrackDao.insertOrReplaceInTx(track);
	}

	public int getReadingTrack(long aid) {
		QueryBuilder<ReadingTrack> mqBuilder = readingTrackDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.ReadingTrackDao.Properties.Rid
				.eq(aid));
		ReadingTrack track = mqBuilder.unique();
		if (track == null)
			return 0;
		// LogUtil.e(TAG, "track.getPos()"+track.getPos());
		return track.getPos();
	}

	public void delReadingTrack(long aid) {
		QueryBuilder<ReadingTrack> mqBuilder = readingTrackDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.ReadingTrackDao.Properties.Rid
				.eq(aid));
		List<ReadingTrack> chatEntityList = mqBuilder.build().list();
		readingTrackDao.deleteInTx(chatEntityList);
	}

	public void delReadingTrack() {
		readingTrackDao.deleteAll();
	}

	public List<ReadingTrack> getReadingTrack() {
		QueryBuilder<ReadingTrack> mqBuilder = readingTrackDao.queryBuilder();
		mqBuilder
				.orderDesc(com.listenBook.greendao.ReadingTrackDao.Properties.Time);
		return mqBuilder.list();
	}

	public void delDown(long cpId) {
		QueryBuilder<Down> mqBuilder = downDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.DownDao.Properties.CpId
				.eq(cpId));
		List<Down> chatEntityList = mqBuilder.build().list();
		downDao.deleteInTx(chatEntityList);

	}

	public void delDownInAid(long aid) {
		QueryBuilder<Down> mqBuilder = downDao.queryBuilder();
		mqBuilder.where(com.listenBook.greendao.DownDao.Properties.AId.eq(aid));
		List<Down> chatEntityList = mqBuilder.build().list();
		downDao.deleteInTx(chatEntityList);
	}

	public void saveFeedback(Feedback feedback) {
		feedbackDao.insertOrReplace(feedback);
	}

	public void saveChipFeedback(ChipFeedback chipFeedback) {
		chipFeedbackDao.insertOrReplace(chipFeedback);
	}

	public void saveFeedback(ArrayList<Feedback> feedbacks) {
		feedbackDao.insertOrReplaceInTx(feedbacks);

	}

	public void saveChipFeedback(ArrayList<ChipFeedback> chipFeedbacks) {
		chipFeedbackDao.insertOrReplaceInTx(chipFeedbacks);

	}

	public boolean saveChatMsg(ChatMsg msg) {
		chatMsgDao.insertOrReplace(msg);
		return true;
	}

	public boolean removeChatMsg(ChatMsg msg) {
		chatMsgDao.delete(msg);
		return true;
	}

	public boolean clearChatMsg() {
		chatMsgDao.deleteAll();
		return true;
	}

	public void clearChatMsgToRuid(long ruid) {
		QueryBuilder<ChatMsg> qb = chatMsgDao.queryBuilder();
		qb.where(com.listenBook.greendao.ChatMsgDao.Properties.Ruid.eq(ruid));
		chatMsgDao.deleteInTx(qb.list());
	}

	public List<ChatMsg> getChatMsgToRuid(long ruid, int limit) {
		// LogUtil.e(TAG, "chatMsgDao.count()"+chatMsgDao.count());
		if (chatMsgDao.count() < limit)
			limit = (int) chatMsgDao.count();
		if (limit == 0)
			return new ArrayList<ChatMsg>();
		QueryBuilder<ChatMsg> qb = chatMsgDao.queryBuilder();
		qb.where(com.listenBook.greendao.ChatMsgDao.Properties.Ruid.eq(ruid))
				.orderDesc(com.listenBook.greendao.ChatMsgDao.Properties.Id)
				.limit(limit);
		Long id = qb.list().get(limit - 1).getId();
		qb.list().clear();
		qb = chatMsgDao.queryBuilder();
		qb.where(com.listenBook.greendao.ChatMsgDao.Properties.Ruid.eq(ruid),
				com.listenBook.greendao.ChatMsgDao.Properties.Id.gt(id)).limit(
				limit);
		return qb.list();
	}

	public List<ChatMsg> getChatMsg(long uid, long rid) {
		QueryBuilder<ChatMsg> qb = chatMsgDao.queryBuilder();
		qb.where(qb.or(qb.and(
				com.listenBook.greendao.ChatMsgDao.Properties.Suid.eq(uid),
				com.listenBook.greendao.ChatMsgDao.Properties.Ruid.eq(rid)),
				qb.and(com.listenBook.greendao.ChatMsgDao.Properties.Suid
						.eq(rid),
						com.listenBook.greendao.ChatMsgDao.Properties.Ruid
								.eq(uid))));
		return qb.list();
	}

	public List<ChatMsg> getUnlineandelayOnlineMsg(long date) {
		QueryBuilder<ChatMsg> qb = chatMsgDao.queryBuilder();
		qb.where(com.listenBook.greendao.ChatMsgDao.Properties.Ruid
				.eq(ChatMsgManage.CHAR_TAG),
				com.listenBook.greendao.ChatMsgDao.Properties.Dateline.gt(date));
		return qb.list();
	}

	public long getChatMsgCount() {
		// LogUtil.e("数据库大小", "" + chatMsgDao.count());
		// com.listenBook.greendao.ChatMsgDao.Properties.
		QueryBuilder<ChatMsg> qb = chatMsgDao.queryBuilder();
		qb.where(com.listenBook.greendao.ChatMsgDao.Properties.Ruid
				.eq(ChatMsgManage.CHAR_TAG));
		long size = qb.list().size();
		qb.list().clear();
		return size;
	}

	public boolean clearnUnlineandelayOnlineMsg(long date) {
		QueryBuilder<ChatMsg> qb = chatMsgDao.queryBuilder();
		qb.where(com.listenBook.greendao.ChatMsgDao.Properties.Ruid
				.eq(ChatMsgManage.CHAR_TAG),
				com.listenBook.greendao.ChatMsgDao.Properties.Dateline.gt(date));
		chatMsgDao.deleteInTx(qb.list());
		return true;
	}

	public long getMsgId(ChatMsg msg) {
		QueryBuilder<ChatMsg> qb = chatMsgDao.queryBuilder();
		qb.where(com.listenBook.greendao.ChatMsgDao.Properties.Id.eq(msg
				.getId()),
				com.listenBook.greendao.ChatMsgDao.Properties.Dateline.eq(msg
						.getDateline()));
		for (ChatMsg c : qb.list()) {
			return c.getId();
		}
		return 0;
	}

	public List<ChatMsg> getMsgLimit(long id, int limitCount) {
		QueryBuilder<ChatMsg> qb = chatMsgDao.queryBuilder();
		qb.where(
				com.listenBook.greendao.ChatMsgDao.Properties.Ruid
						.eq(ChatMsgManage.CHAR_TAG),
				com.listenBook.greendao.ChatMsgDao.Properties.Id.gt(id)).limit(
				limitCount);
		return qb.list();
	}

	/*
	 * 功过表成人版
	 */
	public List<MeritTableAdult> getMeritTableAdultToUid(long uid) {
		QueryBuilder<MeritTableAdult> qb = meritTableAdultDao.queryBuilder();
		qb.where(com.listenBook.greendao.MeritTableAdultDao.Properties.Uid
				.eq(uid));

		return qb.list();
	}

	public boolean saveMeritTableAdult(MeritTableAdult msg) {
		meritTableAdultDao.insertOrReplace(msg);
		return true;
	}

	public void clearMeritTableAdultToUid(long uid) {
		QueryBuilder<MeritTableAdult> qb = meritTableAdultDao.queryBuilder();
		qb.where(com.listenBook.greendao.MeritTableAdultDao.Properties.Uid
				.eq(uid));
		meritTableAdultDao.deleteInTx(qb.list());
	}

	/*
	 * 功过表孩子版
	 */
	public List<MeritTableChildren> getMeritTableChildrenToUid(long uid) {
		QueryBuilder<MeritTableChildren> qb = meritTableChildrenDao
				.queryBuilder();
		qb.where(com.listenBook.greendao.MeritTableChildrenDao.Properties.Uid
				.eq(uid));

		return qb.list();
	}

	public boolean saveMeritTableChildren(MeritTableChildren msg) {
		meritTableChildrenDao.insertOrReplace(msg);
		return true;
	}

	public void clearMeritTableChildrenToUid(long uid) {
		QueryBuilder<MeritTableChildren> qb = meritTableChildrenDao
				.queryBuilder();
		qb.where(com.listenBook.greendao.MeritTableChildrenDao.Properties.Uid
				.eq(uid));
		meritTableChildrenDao.deleteInTx(qb.list());
	}

	public void saveChatPeopleInfoList(List<ChatPeopleInfo> list) {
		chatPeopleInfoDao.insertInTx(list);
	}

	public void clearChatPeopleInfo() {
		chatPeopleInfoDao.deleteAll();
	}

	public boolean chatPeopleInfoIsExist(ChatPeopleInfo chatPeopleInfo) {
		List<ChatPeopleInfo> list = chatPeopleInfoDao.loadAll();
		for (ChatPeopleInfo info : list) {
			if (info.getUid() == chatPeopleInfo.getUid()) {
				return true;
			}
		}
		list.clear();
		return false;
	}

	public void saveChatPeopleInfo(ChatPeopleInfo chatPeopleInfo) {
		chatPeopleInfoDao.insertOrReplace(chatPeopleInfo);
		// LogUtil.e(TAG, "saveChatPeopleInfo"+chatPeopleInfo.toString());
	}

	public List<Long> getAllChatPeopleInfoKeys() {
		List<ChatPeopleInfo> list = chatPeopleInfoDao.loadAll();
		List<Long> keylist = new ArrayList<Long>();
		for (ChatPeopleInfo info : list) {
			keylist.add(info.getUid());
		}
		list.clear();
		return keylist;
	}

	public void deleteUnchatPeopleInfo(long key) {
		chatPeopleInfoDao.deleteByKey(key);
	}

	public List<ChatPeopleInfo> getAllChatPeopleInfo() {
		return chatPeopleInfoDao.loadAll();
	}

	public ChatPeopleInfo getChatPeopleInfo(long uid) {
		QueryBuilder<ChatPeopleInfo> qb = chatPeopleInfoDao.queryBuilder();
		qb.where(com.listenBook.greendao.ChatPeopleInfoDao.Properties.Uid
				.eq(uid));
		return qb.unique();
	}

	public List<LessonMarksInfo> getLessonMarksInfos(int max) {
		QueryBuilder<LessonMarksInfo> qb = lessonMarksInfoDao.queryBuilder();
		List<LessonMarksInfo> list = qb.orderDesc().limit(max).list();
		if (list == null)
			return new ArrayList<LessonMarksInfo>();
		return qb.orderDesc().limit(max).list();
	}

	public String getLessonInfoLastDate() {
		QueryBuilder<LessonMarksInfo> qb = lessonMarksInfoDao.queryBuilder();
		if (qb.orderDesc().list().get(0) == null)
			return null;
		return qb.orderDesc().list().get(0).getDate();
	}

	public List<LessonInfo> getLessonInfoLast() {
		String date = getLessonInfoLastDate();
		if (date == null)
			return new ArrayList<LessonInfo>();
		QueryBuilder<LessonInfo> qb = lessonInfoDao.queryBuilder();
		return qb.where(
				com.listenBook.greendao.LessonInfoDao.Properties.Date.eq(date))
				.list();
	}

	public List<LessonInfo> getLessonInfo(String date) {
		if (date == null)
			return new ArrayList<LessonInfo>();
		QueryBuilder<LessonInfo> qb = lessonInfoDao.queryBuilder();
		return qb.where(
				com.listenBook.greendao.LessonInfoDao.Properties.Date.eq(date))
				.list();
	}

	public LessonMarksInfo getLessonMarksInfo(String date) {
		QueryBuilder<LessonMarksInfo> qb = lessonMarksInfoDao.queryBuilder();
		return qb.where(
				com.listenBook.greendao.LessonMarksInfoDao.Properties.Date
						.eq(date)).unique();
	}

	public void saveLessonMarksInfo(LessonMarksInfo lessonMarksInfo) {
		lessonMarksInfoDao.insertOrReplace(lessonMarksInfo);
	}

	public void saveLessonInfos(List<LessonInfo> lessonInfos) {
		lessonInfoDao.insertInTx(lessonInfos);
	}

	public boolean getLessonIsComplete(String date) {
		if (TextUtils.isEmpty(date)) {
			date = getLessonInfoLastDate();
		}
		QueryBuilder<LessonMarksInfo> qb = lessonMarksInfoDao.queryBuilder();
		return qb
				.where(com.listenBook.greendao.LessonMarksInfoDao.Properties.Date
						.eq(date)).unique().getIscomplete();
	}

	public void saveCompleteItem(LessonInfo info) {
		lessonInfoDao.insertOrReplace(info);
	}

	public ArticleChapterRecode getArticleChapterTtack(long cpId) {
		QueryBuilder<ArticleChapterRecode> qb = articleChapterRecodeDao
				.queryBuilder();
		return qb.where(
				com.listenBook.greendao.ArticleChapterRecodeDao.Properties.Cpid
						.eq(cpId)).unique();
	}

	public void saveArticleChapterTtack(ArticleChapterRecode recode) {
		articleChapterRecodeDao.insertOrReplace(recode);
	}


}
