package com.itheima52.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;


import com.itheima52.mobilesafe.R;

public class ContactActivity extends Activity {

	private ListView lvList;
	private ArrayList<HashMap<String, String>> readContact;

	@Override
	protected void onCreate(Bundle savedInstanceState) {


		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact);


		lvList = (ListView) findViewById(R.id.lv_list);

		 readContact = readContact();
		// for (HashMap<String, String> map : readContact) {
		// System.out.println(map.get("name") + map.get("phone"));
		// }

		// lvList.setAdapter(new SimpleAdapter(this, readContact,
		// R.layout.contact_list_item, new String[] { "name", "phone" },
		// new int[] { R.id.tv_name, R.id.tv_phone }));

		lvList.setAdapter(new myAdapter());

		lvList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				// 返回电话号码
				String phone = readContact.get(position).get("phone")
						.toString();
				Intent intent = new Intent();
				intent.putExtra("phone", phone);
				setResult(Activity.RESULT_OK, intent);
				finish();
			}
		});
	}

	private ArrayList<HashMap<String, String>> readContact() {
		// 首先,从raw_contacts中读取联系人的id("contact_id")
		// 其次, 根据contact_id从data表中查询出相应的电话号码和联系人名称
		// 然后,根据mimetype来区分哪个是联系人,哪个是电话号码
		Uri rawContactsUri = Uri
				.parse("content://com.android.contacts/raw_contacts");
		Uri dataUri = Uri.parse("content://com.android.contacts/data");

		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();

		// 从raw_contacts中读取联系人的id("contact_id")
		Cursor rawContactsCursor = getContentResolver().query(rawContactsUri,
				new String[] { "contact_id" }, null, null, null);
		if (rawContactsCursor != null) {
			while (rawContactsCursor.moveToNext()) {
				String contactId = rawContactsCursor.getString(0);
				if (contactId != null) {

					// System.out.println(contactId);

					// 根据contact_id从data表中查询出相应的电话号码和联系人名称, 实际上查询的是视图view_data
					Cursor dataCursor = getContentResolver().query(dataUri,
							new String[] { "data1", "mimetype" },
							"contact_id=?", new String[] { contactId },
							null);
					String checkphone = null;
					if (dataCursor != null) {
						HashMap<String, String> map = new HashMap<String, String>();
						while (dataCursor.moveToNext()) {
							checkphone = null;
							String data1 = dataCursor.getString(dataCursor
									.getColumnIndex("data1"));
							String mimetype = dataCursor.getString(dataCursor
									.getColumnIndex("mimetype"));
							if ("vnd.android.cursor.item/phone_v2"
									.equals(mimetype)) {
								map.put("phone", data1);
								checkphone = data1;
							} else if ("vnd.android.cursor.item/name"
									.equals(mimetype)) {
								map.put("name", data1);
							}

						}

						if (TextUtils.isEmpty(checkphone)) {//判断号码是否为空
							dataCursor.close();
						} else {
							list.add(map);
							dataCursor.close();
						}
					}
				}
				//rawContactsCursor.close();错在这，还没while完，怎么能就把资源关了？
			}
		}
		rawContactsCursor.close();
		return list;
	}

	class myAdapter extends BaseAdapter {


		public int getCount() {
			// TODO Auto-generated method stub
			return readContact.size();
		}


		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return readContact.get(position);
		}


		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}


		public View getView(int position, View convertView, ViewGroup parent) {
			View v = null;
			HashMap<String, String> infoMap = readContact.get(position);
			if (convertView != null) {
				v = convertView;
			} else {
				v = View.inflate(ContactActivity.this,
						R.layout.contact_list_item, null);
			}
			TextView tv_name = (TextView) v.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) v.findViewById(R.id.tv_phone);
			tv_phone.setText(infoMap.get("phone"));
			tv_name.setText(infoMap.get("name"));
			return v;
		}

	}
}
