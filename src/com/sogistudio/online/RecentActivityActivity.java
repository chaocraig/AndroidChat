/*
 * The MIT License (MIT)
 * 
 * Copyright � 2013 Clover Studio Ltd. All rights reserved.
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sogistudio.online;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sogistudio.online.R;
import com.google.android.gcm.GCMRegistrar;
import com.sogistudio.online.adapters.RecentActivityAdapter;
import com.sogistudio.online.couchdb.model.ActivitySummary;
import com.sogistudio.online.dialog.HookUpDialog;
import com.sogistudio.online.extendables.SideBarActivity;
import com.sogistudio.online.management.UsersManagement;
import com.sogistudio.online.utils.Const;

/**
 * RecentActivityActivity
 * 
 * Shows unread notifications for login user.
 */

public class RecentActivityActivity extends SideBarActivity {

	private HookUpDialog mExitAppDialog;
	private LinearLayout mLlRecentActivity;
	private RecentActivityAdapter mRecentActivityAdapter;
	private static RecentActivityActivity sInstance;
	private View hoverView;
	private boolean pushHandledOnNewIntent = false;
    private TextView mNoActivitiesView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		sInstance = this;
		setContentView(R.layout.activity_recent_activity);
		setSideBar(getString(R.string.RECENT_ACTIVITY));
		initialization();
		
		//showTutorial(getString(R.string.tutorial_recent));

		//String savedPushToken = SpikaApp.getPreferences().getUserPushToken();
        //if (savedPushToken.equals(null) || savedPushToken.equals("")) {
			registerOnGCM();
        //}
		
		//Cray: jump to GroupCategories
		//Intent intent = new Intent(RecentActivityActivity.this, GroupsActivity.class);
		//startActivity(intent);

	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		pushHandledOnNewIntent = false;
		if (intent.getBooleanExtra(Const.PUSH_INTENT, false) == true) {
			pushHandledOnNewIntent = true;
			Intent pushIntent = intent;
			pushIntent.setClass(this, WallActivity.class);
			startActivity(pushIntent);
		}
		if (intent.getBooleanExtra(Const.USER_URI_INTENT, false) == true) {
			pushHandledOnNewIntent = true;
			Intent pushIntent = intent;
			pushIntent.setClass(this, UserProfileActivity.class);
			startActivity(pushIntent);
		}
		if (intent.getBooleanExtra(Const.GROUP_URI_INTENT, false) == true) {
			pushHandledOnNewIntent = true;
			Intent pushIntent = intent;
			pushIntent.setClass(this, GroupProfileActivity.class);
			startActivity(pushIntent);
		}
		super.onNewIntent(intent);
	}

	
	@Override
	protected void onResume() {

        getActivitySummary();

		if (!pushHandledOnNewIntent) {
			if (getIntent().getBooleanExtra(Const.PUSH_INTENT, false)) {
				pushHandledOnNewIntent = true;
				Intent pushIntent = getIntent();
				pushIntent.setClass(this, WallActivity.class);
				startActivity(pushIntent);
			}
			if (getIntent().getBooleanExtra(Const.USER_URI_INTENT, false)) {
				pushHandledOnNewIntent = true;
				Intent pushIntent = getIntent();
				pushIntent.setClass(this, UserProfileActivity.class);
				startActivity(pushIntent);
			}
			if (getIntent().getBooleanExtra(Const.GROUP_URI_INTENT, false)) {
				pushHandledOnNewIntent = true;
				Intent pushIntent = getIntent();
				pushIntent.setClass(this, GroupProfileActivity.class);
				startActivity(pushIntent);
			}

		}
		super.onResume();
	}

	public static RecentActivityActivity getInstance() {
		return sInstance;
	}

	@Override
	protected void refreshActivitySummaryViews() {
		super.refreshActivitySummaryViews();

		ActivitySummary summary = UsersManagement.getLoginUser().getActivitySummary();

		if (summary != null) {

            if(summary.getRecentActivityList().size() > 0){
                mNoActivitiesView.setVisibility(View.GONE);
            }else{
                mNoActivitiesView.setVisibility(View.VISIBLE);
                
                //Cray: jump to Groups
        		Intent intent = new Intent(RecentActivityActivity.this, GroupsActivity.class);
        		startActivity(intent);
                
            }

			if (mRecentActivityAdapter == null) {
				mRecentActivityAdapter = new RecentActivityAdapter(
						RecentActivityActivity.this, mLlRecentActivity,
						summary.getRecentActivityList());
			} else {
				mRecentActivityAdapter
						.setItems(summary.getRecentActivityList());
			}

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		GCMRegistrar.onDestroy(getApplicationContext());
	}

	@Override
	protected void setObjectsNull() {
		mExitAppDialog = null;
		mLlRecentActivity.removeAllViews();
		mLlRecentActivity = null;
		sInstance = null;
		super.setObjectsNull();
	}

	private void initialization() {
		
		hoverView = (View) findViewById(R.id.hoverView);
		hoverView.setVisibility(View.GONE);
		mExitAppDialog = new HookUpDialog(this);
		mExitAppDialog.setMessage(getString(R.string.exit_app_message));
		mExitAppDialog.setOnButtonClickListener(HookUpDialog.BUTTON_OK,
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						mExitAppDialog.dismiss();
						
						if (WallActivity.getInstance() != null) {
							WallActivity.getInstance().finish();
						}
						sInstance.finish();

					}
				});
		mExitAppDialog.setOnButtonClickListener(
				HookUpDialog.BUTTON_CANCEL, new OnClickListener() {

					@Override
					public void onClick(View v) {
						mExitAppDialog.dismiss();

					}
				});

		mLlRecentActivity = (LinearLayout) findViewById(R.id.llRecentActivity);
        mNoActivitiesView = (TextView) findViewById(R.id.tvNoRecentActivities);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (mSideBarOpened) {
				closeSideBar();
				return true;
			} else {
				mExitAppDialog.show();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}


	@Override
	protected void enableViews() {
		super.enableViews();
		hoverView.setVisibility(View.GONE);
	}

	@Override
	protected void disableViews() {
		super.disableViews();
		hoverView.setVisibility(View.VISIBLE);
	}

	private String registerOnGCM() {

		if (Const.PUSH_SENDER_ID == null || Const.PUSH_SENDER_ID.length() == 0) {
			Toast.makeText(this.getApplicationContext(),
					"Please set your GCM Sender ID", Toast.LENGTH_LONG).show();
			return null;
		}

		GCMRegistrar.checkDevice(getApplicationContext());
		GCMRegistrar.checkManifest(getApplicationContext());
		
		String registrationId = GCMRegistrar.getRegistrationId(getApplicationContext());

        GCMRegistrar.register(getApplicationContext(), Const.PUSH_SENDER_ID);

		if (registrationId.equals("") || registrationId.equals(null)) {


		} else {
			// // Device is already registered on GCM
			if (GCMRegistrar.isRegisteredOnServer(getApplicationContext())) {

			} else {

			}
		}
		return GCMRegistrar.getRegistrationId(getApplicationContext());
	}
	

}
