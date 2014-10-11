package com.sogistudio.online;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.sogistudio.online.R;
import com.sogistudio.online.dialog.HookUpProgressDialog;
import com.sogistudio.online.extendables.SideBarActivity;
import com.sogistudio.online.management.UsersManagement;
import com.sogistudio.online.utils.Const;
import com.sogistudio.online.utils.ConstServer;

public class InformationActivity extends SideBarActivity {

	private WebView mWebView;
	private HookUpProgressDialog mProgressDialog;
	private String mUrl=SpikaApp.getInstance().getBaseUrl()+Const.INFORMATION_FOLDER;
	
	@SuppressLint("SetJavaScriptEnabled")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);
		setSideBar(getString(R.string.INFORMATION));
		
		mWebView = (WebView) findViewById(R.id.web_view);
		mProgressDialog = new HookUpProgressDialog(this);
		mProgressDialog.show();
		

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.setWebChromeClient(new WebChromeClient());
		mWebView.setWebViewClient(new MyWebViewClient());
		
		mUrl=mUrl + UsersManagement.getLoginUser().getToken();
		
		mWebView.loadUrl(mUrl);
	}
	
	private class MyWebViewClient extends WebViewClient {
		
		@Override
		public void onPageFinished(WebView view, String url) {
			mProgressDialog.dismiss();
			super.onPageFinished(view, url);
	    }
		
		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			mProgressDialog.show();
			mWebView.loadUrl(url);
			return true;
//			return super.shouldOverrideUrlLoading(view, url);
		}
	}
	
	@Override
	public void onBackPressed() {
		if(mWebView.getUrl().equals(mUrl)){
			super.onBackPressed();
		}else{
			mProgressDialog.show();
			mWebView.loadUrl(mUrl);
		}
	}
}
