package com.example.nfcapp4;

import java.nio.charset.Charset;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareUltralight;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

public class MifareultralightMainActivity extends Activity{
	
	/*
	 * 声明变量
	 */
	private CheckBox mWriteData;
	private NfcAdapter mNfcAdapter;
	private PendingIntent mPendingIntent;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mifareultralight);
		
		mWriteData = (CheckBox) findViewById(R.id.checkbox_write);
		
		mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
		mPendingIntent = PendingIntent.getActivity(this , 0 , new Intent(this , getClass()) , 0);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		
		if (mNfcAdapter != null) {
			mNfcAdapter.enableForegroundDispatch(this , mPendingIntent , null , null);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		
		if (mNfcAdapter != null) {
			mNfcAdapter.disableForegroundDispatch(this);
		}
	}
	
	
	/*
	 * 在这个窗口中进行NFC调用
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		//先取得NFC的Tag对象
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		//取得当前的这个NFC标签所支持的数据格式，这一个个格式以字符串的形式展现，所以存到一个字符串数组中
		String[] techList = tag.getTechList();
		
		//由于MifareUltralight这种格式并不是所有的NFC标签都会支持的，所以要先进行一个检测
		boolean haveMifareUltralight = false;
		for(String t : techList){
			if(t.indexOf("MifareUltralight") >= 0){ //如果有这种格式
				haveMifareUltralight = true;
				break; //直接跳出for循环，做下一步操作
			}
		}
		
		if( haveMifareUltralight == false ){
			Toast.makeText(this , "该NFC标签不支持MifareUltralight格式" , Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mWriteData.isChecked()){ //如果该复选框被勾选，就往NFC里面写数据
			writeTag(tag);
		}
		else{ //否则就读数据
			String data = readTag(tag);
			if(data != null){
				Toast.makeText(this , data , Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	
	
	/*
	 * 往NFC标签中写数据
	 */
	public void writeTag(Tag tag){
		MifareUltralight ultralight = MifareUltralight.get(tag);
		try {
			ultralight.connect();
			//因为MifareUltralight格式中，总共是16位，但是第0~3位是用来存标签系统信息的，因此提供给
			//开发者的可用位数是4~15.下面这个方法第一个参数需要传进去起始的页数，显然是从4开始
			ultralight.writePage( 4 , "中国".getBytes(Charset.forName("GB2312")) );
			ultralight.writePage( 5 , "传媒".getBytes(Charset.forName("GB2312")) );
			ultralight.writePage( 6 , "大学".getBytes(Charset.forName("GB2312")) );
			ultralight.writePage( 7 , "玉凯".getBytes(Charset.forName("GB2312")) );
			
			Toast.makeText(this , "写入数据完毕" , Toast.LENGTH_SHORT).show();
			
		} 
		catch (Exception e) {
			Toast.makeText(this , "操作失败" , Toast.LENGTH_SHORT).show();
		}
		finally{
			try {
				ultralight.close();
			} 
			catch (Exception e) {
				Toast.makeText(this , "操作出现异常" , Toast.LENGTH_SHORT).show();
			}
		}
	}//writeTag()方法结束
	
	
	
	/*
	 * 从NFC标签中读数据
	 */
	public String readTag(Tag tag){
		MifareUltralight ultralight = MifareUltralight.get(tag);
		try {
			ultralight.connect();
			//readPages()方法会连续读4页。形参表示从哪开始读取4页
			byte[] data = ultralight.readPages(4);
			return ( new String(data , Charset.forName("GB2312")) );
		} 
		catch (Exception e) {
			Toast.makeText(this , "读取失败" , Toast.LENGTH_SHORT).show();
		}
		finally{
			try {
				ultralight.close();
			} 
			catch (Exception e) {
				Toast.makeText(this , "操作出现异常" , Toast.LENGTH_SHORT).show();
			}
		}

		return null;
	}
	
	

}
