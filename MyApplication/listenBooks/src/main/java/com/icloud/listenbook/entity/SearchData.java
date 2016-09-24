package com.icloud.listenbook.entity;

import java.util.ArrayList;

import android.R.integer;

public class SearchData {
	public int result;
	public ArrayList<data> key;

	public class data {
		public ArrayList<TabData> children;
	}

	public class TabData {
		public int id;
		public String desc;
		public String remark;
		public int signValue;
		public String name;

		@Override
		public String toString() {
			return "TabData [id=" + id + ", desc=" + desc + ", remark="
					+ remark + ", signValue=" + signValue + ", name=" + name
					+ "]";
		}

	}

	@Override
	public String toString() {
		return "SearchData [result=" + result + ", key=" + key + "]";
	}

	/*
	 * 05-04 08:58:35.402: E/AdultUtils(1278): [ThreadID=0001]
	  得到{"result":0,"list"
	 :{"504":[{"id":"1","desc":"","remark":"","signValue":0
	 ,"name":"起心动念"},{"id"
	 :"2","desc":"","remark":"","signValue":0,"name":"言语态度"
	 },{"id":"3","desc":""
	 ,"remark":"","signValue":0,"name":"行为处事"},{"id":"4","desc"
	 :"","remark":"",
	 "signValue":0,"name":"待人接物"},{"id":"5","desc":"","remark":
	 "","signValue":0
	 ,"name":"工作事业"},{"id":"6","desc":"","remark":"","signValue"
	 :0,"name":"信仰修为"
	 },{"id":"7","desc":"","remark":"","signValue":0,"name":"其  它"}]}}
	 */
}
