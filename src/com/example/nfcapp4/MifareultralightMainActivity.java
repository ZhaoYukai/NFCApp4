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
	 * ��������
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
	 * ����������н���NFC����
	 */
	@Override
	protected void onNewIntent(Intent intent) {
		//��ȡ��NFC��Tag����
		Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
		
		//ȡ�õ�ǰ�����NFC��ǩ��֧�ֵ����ݸ�ʽ����һ������ʽ���ַ�������ʽչ�֣����Դ浽һ���ַ���������
		String[] techList = tag.getTechList();
		
		//����MifareUltralight���ָ�ʽ���������е�NFC��ǩ����֧�ֵģ�����Ҫ�Ƚ���һ�����
		boolean haveMifareUltralight = false;
		for(String t : techList){
			if(t.indexOf("MifareUltralight") >= 0){ //��������ָ�ʽ
				haveMifareUltralight = true;
				break; //ֱ������forѭ��������һ������
			}
		}
		
		if( haveMifareUltralight == false ){
			Toast.makeText(this , "��NFC��ǩ��֧��MifareUltralight��ʽ" , Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(mWriteData.isChecked()){ //����ø�ѡ�򱻹�ѡ������NFC����д����
			writeTag(tag);
		}
		else{ //����Ͷ�����
			String data = readTag(tag);
			if(data != null){
				Toast.makeText(this , data , Toast.LENGTH_SHORT).show();
			}
		}
		
	}
	
	
	
	/*
	 * ��NFC��ǩ��д����
	 */
	public void writeTag(Tag tag){
		MifareUltralight ultralight = MifareUltralight.get(tag);
		try {
			ultralight.connect();
			//��ΪMifareUltralight��ʽ�У��ܹ���16λ�����ǵ�0~3λ���������ǩϵͳ��Ϣ�ģ�����ṩ��
			//�����ߵĿ���λ����4~15.�������������һ��������Ҫ����ȥ��ʼ��ҳ������Ȼ�Ǵ�4��ʼ
			ultralight.writePage( 4 , "�й�".getBytes(Charset.forName("GB2312")) );
			ultralight.writePage( 5 , "��ý".getBytes(Charset.forName("GB2312")) );
			ultralight.writePage( 6 , "��ѧ".getBytes(Charset.forName("GB2312")) );
			ultralight.writePage( 7 , "��".getBytes(Charset.forName("GB2312")) );
			
			Toast.makeText(this , "д���������" , Toast.LENGTH_SHORT).show();
			
		} 
		catch (Exception e) {
			Toast.makeText(this , "����ʧ��" , Toast.LENGTH_SHORT).show();
		}
		finally{
			try {
				ultralight.close();
			} 
			catch (Exception e) {
				Toast.makeText(this , "���������쳣" , Toast.LENGTH_SHORT).show();
			}
		}
	}//writeTag()��������
	
	
	
	/*
	 * ��NFC��ǩ�ж�����
	 */
	public String readTag(Tag tag){
		MifareUltralight ultralight = MifareUltralight.get(tag);
		try {
			ultralight.connect();
			//readPages()������������4ҳ���βα�ʾ���Ŀ�ʼ��ȡ4ҳ
			byte[] data = ultralight.readPages(4);
			return ( new String(data , Charset.forName("GB2312")) );
		} 
		catch (Exception e) {
			Toast.makeText(this , "��ȡʧ��" , Toast.LENGTH_SHORT).show();
		}
		finally{
			try {
				ultralight.close();
			} 
			catch (Exception e) {
				Toast.makeText(this , "���������쳣" , Toast.LENGTH_SHORT).show();
			}
		}

		return null;
	}
	
	

}
