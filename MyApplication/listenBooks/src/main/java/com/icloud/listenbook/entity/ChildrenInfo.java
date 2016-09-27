package com.icloud.listenbook.entity;

public class ChildrenInfo {

	private static ChildrenInfo instance;

	public static ChildrenInfo instance() {
		if (instance == null) {
			instance = new ChildrenInfo();
		}
		return instance;
	}

	protected Long uid;

	protected Long Date;

	protected int schedules;

	protected boolean schedulesGong;

	protected boolean schedulesGuo;

	protected String schedulesDEs;

	protected String schedulesExc;

	protected int attitude;

	protected boolean attitudeGong;

	protected boolean attitudeGuo;

	protected String attitudeDEs;

	protected String attitudeExc;

	protected int study;
	
	protected boolean studyGong;

	protected boolean studyGuo;

	protected String studyDEs;

	protected String studyExc;

	protected int love;
	
	protected boolean loveGong;

	protected boolean loveGuo;

	protected String loveDEs;

	protected String loveExc;

	protected int respect;
	
	protected boolean respectGong;

	protected boolean respectGuo;

	protected String respectDEs;

	protected String respectExc;

	protected int action;
	
	protected boolean actionGong;

	protected boolean actionGuo;

	public void clear(){
		schedulesGong=false;
		schedulesGuo=false;
		attitudeGuo=false;
		attitudeGong=false;
		studyGuo=false;
		studyGong=false;
		loveGuo=false;
		loveGong=false;
		respectGuo=false;
		respectGong=false;
		actionGuo=false;
		actionGong=false;
		otherGong=false;
		otherGuo=false;
	}
	public int getSchedules() {
		return schedules;
	}

	public void setSchedules(int schedules) {
		this.schedules = schedules;
	}

	public boolean isSchedulesGong() {
		return schedulesGong;
	}

	public void setSchedulesGong(boolean schedulesGong) {
		this.schedulesGong = schedulesGong;
	}

	public boolean isSchedulesGuo() {
		return schedulesGuo;
	}

	public void setSchedulesGuo(boolean schedulesGuo) {
		this.schedulesGuo = schedulesGuo;
	}

	public int getAttitude() {
		return attitude;
	}

	public void setAttitude(int attitude) {
		this.attitude = attitude;
	}

	public boolean isAttitudeGong() {
		return attitudeGong;
	}

	public void setAttitudeGong(boolean attitudeGong) {
		this.attitudeGong = attitudeGong;
	}

	public boolean isAttitudeGuo() {
		return attitudeGuo;
	}

	public void setAttitudeGuo(boolean attitudeGuo) {
		this.attitudeGuo = attitudeGuo;
	}

	public int getStudy() {
		return study;
	}

	public void setStudy(int study) {
		this.study = study;
	}

	public boolean isStudyGong() {
		return studyGong;
	}

	public void setStudyGong(boolean studyGong) {
		this.studyGong = studyGong;
	}

	public boolean isStudyGuo() {
		return studyGuo;
	}

	public void setStudyGuo(boolean studyGuo) {
		this.studyGuo = studyGuo;
	}

	public int getLove() {
		return love;
	}

	public void setLove(int love) {
		this.love = love;
	}

	public boolean isLoveGong() {
		return loveGong;
	}

	public void setLoveGong(boolean loveGong) {
		this.loveGong = loveGong;
	}

	public boolean isLoveGuo() {
		return loveGuo;
	}

	public void setLoveGuo(boolean loveGuo) {
		this.loveGuo = loveGuo;
	}

	public int getRespect() {
		return respect;
	}

	public void setRespect(int respect) {
		this.respect = respect;
	}

	public boolean isRespectGong() {
		return respectGong;
	}

	public void setRespectGong(boolean respectGong) {
		this.respectGong = respectGong;
	}

	public boolean isRespectGuo() {
		return respectGuo;
	}

	public void setRespectGuo(boolean respectGuo) {
		this.respectGuo = respectGuo;
	}

	public int getAction() {
		return action;
	}

	public void setAction(int action) {
		this.action = action;
	}

	public boolean isActionGong() {
		return actionGong;
	}

	public void setActionGong(boolean actionGong) {
		this.actionGong = actionGong;
	}

	public boolean isActionGuo() {
		return actionGuo;
	}

	public void setActionGuo(boolean actionGuo) {
		this.actionGuo = actionGuo;
	}

	public int getOther() {
		return other;
	}

	public void setOther(int other) {
		this.other = other;
	}

	public boolean isOtherGong() {
		return otherGong;
	}

	public void setOtherGong(boolean otherGong) {
		this.otherGong = otherGong;
	}

	public boolean isOtherGuo() {
		return otherGuo;
	}

	public void setOtherGuo(boolean otherGuo) {
		this.otherGuo = otherGuo;
	}

	protected String actionDEs;

	protected String actionExc;

	protected int other;
	
	protected boolean otherGong;

	protected boolean otherGuo;

	protected String otherDEs;

	protected String otherExc;

	protected int total;

	private int Credit;

	private int fault;

	public int getCredit() {
		return Credit;
	}

	public void setCredit(int credit) {
		Credit = credit;
	}

	public int getFault() {
		return fault;
	}

	public void setFault(int fault) {
		this.fault = fault;
	}

	public Long getUid() {
		return uid;
	}

	public void setUid(Long uid) {
		this.uid = uid;
	}

	public Long getDate() {
		return Date;
	}

	public void setDate(Long date) {
		Date = date;
	}


	public String getSchedulesDEs() {
		return schedulesDEs;
	}

	public void setSchedulesDEs(String schedulesDEs) {
		this.schedulesDEs = schedulesDEs;
	}

	public String getSchedulesExc() {
		return schedulesExc;
	}

	public void setSchedulesExc(String schedulesExc) {
		this.schedulesExc = schedulesExc;
	}


	public String getAttitudeDEs() {
		return attitudeDEs;
	}

	public void setAttitudeDEs(String attitudeDEs) {
		this.attitudeDEs = attitudeDEs;
	}

	public String getAttitudeExc() {
		return attitudeExc;
	}

	public void setAttitudeExc(String attitudeExc) {
		this.attitudeExc = attitudeExc;
	}


	public String getStudyDEs() {
		return studyDEs;
	}

	public void setStudyDEs(String studyDEs) {
		this.studyDEs = studyDEs;
	}

	public String getStudyExc() {
		return studyExc;
	}

	public void setStudyExc(String studyExc) {
		this.studyExc = studyExc;
	}


	public String getLoveDEs() {
		return loveDEs;
	}

	public void setLoveDEs(String loveDEs) {
		this.loveDEs = loveDEs;
	}

	public String getLoveExc() {
		return loveExc;
	}

	public void setLoveExc(String loveExc) {
		this.loveExc = loveExc;
	}


	public String getRespectDEs() {
		return respectDEs;
	}

	public void setRespectDEs(String respectDEs) {
		this.respectDEs = respectDEs;
	}

	public String getRespectExc() {
		return respectExc;
	}

	public void setRespectExc(String respectExc) {
		this.respectExc = respectExc;
	}

	

	public String getActionDEs() {
		return actionDEs;
	}

	public void setActionDEs(String actionDEs) {
		this.actionDEs = actionDEs;
	}

	public String getActionExc() {
		return actionExc;
	}

	public void setActionExc(String actionExc) {
		this.actionExc = actionExc;
	}

	

	public String getOtherDEs() {
		return otherDEs;
	}

	public void setOtherDEs(String otherDEs) {
		this.otherDEs = otherDEs;
	}

	public String getOtherExc() {
		return otherExc;
	}

	public void setOtherExc(String otherExc) {
		this.otherExc = otherExc;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}
}
