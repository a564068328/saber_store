package com.icloud.listenbook.entity;

import com.android.volley.toolbox.ClearCacheRequest;

import android.R.integer;

public class AdultInfo {

	private static AdultInfo instance;

	public static AdultInfo instance() {
		if (instance == null) {
			instance = new AdultInfo();
		}
		return instance;
	}

	protected Long uid;

	protected Long Date;

	protected boolean ideaGong;
	
	protected boolean ideaGuo;

	protected String ideaDEs;

	protected String ideaExc;

	protected boolean attitudeGong;
	
	protected boolean attitudeGuo;

	protected String attitudeDEs;

	protected String attitudeExc;
	
	protected boolean actionGong;

	protected boolean actionGuo;
	
	protected String actionDEs;

	protected String actionExc;

	protected boolean treatGong;
	
	protected boolean treatGuo;

	protected String treatDEs;

	protected String treatExc;
	
	protected boolean workGong;
	
	protected boolean workGuo;
	
	protected String workDEs;

	protected String workExc;
	
	protected boolean beliefGong;
	
	protected boolean beliefGuo;

	protected String beliefDEs;

	protected String beliefExc;
	
	protected int  total;
	
	protected boolean otherGong;
	
	protected boolean otherGuo;

	protected String otherDEs;

	protected String otherExc;
	
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


	public String getIdeaDEs() {
		return ideaDEs;
	}

	public void setIdeaDEs(String ideaDEs) {
		this.ideaDEs = ideaDEs;
	}

	public String getIdeaExc() {
		return ideaExc;
	}

	public void setIdeaExc(String ideaExc) {
		this.ideaExc = ideaExc;
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


	public String getTreatDEs() {
		return treatDEs;
	}

	public void setTreatDEs(String treatDEs) {
		this.treatDEs = treatDEs;
	}

	public String getTreatExc() {
		return treatExc;
	}

	public void setTreatExc(String treatExc) {
		this.treatExc = treatExc;
	}


	public String getWorkDEs() {
		return workDEs;
	}

	public void setWorkDEs(String workDEs) {
		this.workDEs = workDEs;
	}

	public String getWorkExc() {
		return workExc;
	}

	public void setWorkExc(String workExc) {
		this.workExc = workExc;
	}


	public String getBeliefDEs() {
		return beliefDEs;
	}

	public void setBeliefDEs(String beliefDEs) {
		this.beliefDEs = beliefDEs;
	}

	public String getBeliefExc() {
		return beliefExc;
	}

	public void setBeliefExc(String beliefExc) {
		this.beliefExc = beliefExc;
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


	public boolean isIdeaGong() {
		return ideaGong;
	}

	public void setIdeaGong(boolean ideaGong) {
		this.ideaGong = ideaGong;
	}

	public boolean isIdeaGuo() {
		return ideaGuo;
	}

	public void setIdeaGuo(boolean ideaGuo) {
		this.ideaGuo = ideaGuo;
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

	public boolean isTreatGong() {
		return treatGong;
	}

	public void setTreatGong(boolean treatGong) {
		this.treatGong = treatGong;
	}

	public boolean isTreatGuo() {
		return treatGuo;
	}

	public void setTreatGuo(boolean treatGuo) {
		this.treatGuo = treatGuo;
	}

	public boolean isWorkGong() {
		return workGong;
	}

	public void setWorkGong(boolean workGong) {
		this.workGong = workGong;
	}

	public boolean isWorkGuo() {
		return workGuo;
	}

	public void setWorkGuo(boolean workGuo) {
		this.workGuo = workGuo;
	}

	public boolean isBeliefGong() {
		return beliefGong;
	}

	public void setBeliefGong(boolean beliefGong) {
		this.beliefGong = beliefGong;
	}

	public boolean isBeliefGuo() {
		return beliefGuo;
	}

	public void setBeliefGuo(boolean beliefGuo) {
		this.beliefGuo = beliefGuo;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
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
    public void Clear(){
    	ideaGong=false;
    	ideaGuo=false;
    	attitudeGong=false;
    	attitudeGuo=false;
    	treatGong=false;
    	treatGuo=false;
    	workGong=false;
    	workGuo=false;
    	beliefGong=false;
    	beliefGuo=false;
    	actionGong=false;
    	actionGuo=false;
    	otherGong=false;
    	otherGuo=false;
    }
	
}
