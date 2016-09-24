package com.icloud.listenbook.ui.chipAct;

import java.util.ArrayList;
import java.util.List;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.View.OnClickListener;

import com.icloud.listenbook.R;
import com.icloud.listenbook.base.BaseActivity;
import com.icloud.listenbook.entity.StandardInfo;
import com.icloud.listenbook.recyclerplus.DividerItemDecoration;
import com.icloud.listenbook.ui.adapter.StandardAdapter;
import com.icloud.listenbook.unit.Configuration;
import com.icloud.wrzjh.base.utils.ViewUtils;

public class StandardAct extends BaseActivity implements OnClickListener {

	View back;
	RecyclerView list;
	boolean isAdult;
	List<StandardInfo> array;
	LinearLayoutManager manager;
	StandardAdapter adapter;
	static final String[] adultTitle = new String[] { "起心动念", "言语态度", "行为处事",
			"待人接物", "工作事业", "信仰修为" };
	static final String[] adultBody = new String[] {
			"反观当日主要事务中的起心动念，有损人、害他、一味为我之念均是过，反之，善心利他、将心比心、恻隐之心、真心等则为功；",
			"反观当日主要事务中，言语轻佻、撒谎不实、不和气、不友善、恶声恶气、阴阳怪气、谩骂、嗔怨、迁怒人、抱怨等均是过，反之，和气、诚实、友善、积极等则为功；",
			"反观当日主要事务中，损人利己、好占便宜、玩心机、不诚实、不守信、不踏实、不稳重、马虎、好高骛远等均是过，反之，真心、真实、让利、诚信、宽恕等则为功；",
			"反观当日主要事务中，不谦虚、不真实、斤斤计较、盛气凌人、趾高气扬、张扬炫富、颐指气使、自我自负等均是过，反之，谦让、真实、心平气和等则为功；",
			"反观当日主要事务中，无责任心、无目标、不认真、不用心、不努力、不上进、不容人、舍不得时间、舍不得精力、吃不得亏、听不得不同意见等均是过，反之，则为功；",
			"反观当日主要事务中，好概念、好定义、好名声、好声光色电、迷信外在、脱离生活、好清静、不用心于过程、迷执于结果、不善疑、不善思等均是过，反之，不迷信、善起疑、直接参悟、视一切过程为觉悟机缘，则为功。" };
	static final String[] childrenTitle = new String[] { "作息规律", "对人态度",
			"学习专注", "爱心善意", "尊师重教", "思考行动" };
	static final String[] childrenBody = new String[] {
			"每日反省自己作息是否形成规律，是否按时起床、刷牙、洗脸、吃饭、上学，是否按时自动自主做作业，是否能及时完成作业上床睡觉，因外在事件轻易改变自己作息规律，没有主动性，做作业拖拉等均是过，有恒心能坚持作息规律是功；",
			"对同学、家人或他人态度不友善、好生气、怨恨他人、迁怒他人、攻击他人、不理解他人、诋毁他人、待人三心二意、不诚心、不诚信等均是过，反之，善心待人、和气待人、理解人、谦虚谨慎则是功；",
			"读书不认真、听讲跑神、做作业沉不下心、上课交头接耳、多动难静、学习没有恒心均是过，反之，学习认真、听讲用心、做作业专心、能持之以恒等是功；",
			"反省当日自己经历事务起心动念，凡是损人利己、为了满足自己、自以为是、自负、自大、骄傲自满、好表现自己、哗众取宠等均是过，反之，善心、真心、爱心等是功。",
			"不尊敬长辈、不听从老师安排、对长辈没礼貌、阳奉阴违、当面一套背后一套、不重视学习、没有明确学习目标、不能主动自发地学习、缺乏学习积极性等是过，反之是功。",
			"反省当日行为，行为好动多动、没有三思后行、没想清楚就行动、马虎、不用心、不专心、举止轻浮、做事不稳重、做事不周全等是过，反之，善质疑、喜思考、好追根究底、行事稳重等是功。" };

	@Override
	public void init() {
		isAdult = getIntent().getBooleanExtra("isadult", true);
		array = new ArrayList<StandardInfo>();
		manager = new LinearLayoutManager(this);
		manager.setOrientation(LinearLayoutManager.VERTICAL);
	}

	@Override
	public int getLayout() {

		return R.layout.act_standard;
	}

	@Override
	public void findViews() {
		back = findViewById(R.id.back);
		list = (RecyclerView) findViewById(R.id.list);
	}

	@Override
	public void setListeners() {
		back.setOnClickListener(this);
		back.setOnTouchListener(ViewUtils.instance().onTouchListener);

	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		switch (id) {
		case R.id.back:
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void setDatas() {
		super.setDatas();
		StandardInfo info = null;
		if (isAdult) {
			for (int i = 0; i < adultTitle.length; i++) {
				info = new StandardInfo(adultTitle[i],
						Configuration.STANDARD_TITLE);
				array.add(info);
				info = new StandardInfo(adultBody[i],
						Configuration.STANDARD_BODY);
				array.add(info);
			}
		} else {
			for (int i = 0; i < childrenTitle.length; i++) {
				info = new StandardInfo(childrenTitle[i],
						Configuration.STANDARD_TITLE);
				array.add(info);
				info = new StandardInfo(childrenBody[i],
						Configuration.STANDARD_BODY);
				array.add(info);
			}
		}
		info = new StandardInfo("确定",
				Configuration.STANDARD_BOTTOM);
		array.add(info);
		list.setLayoutManager(manager);
		adapter=new StandardAdapter(array,this);
		list.setLayoutManager(manager);
		list.setAdapter(adapter);
		//list.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
	}
}
