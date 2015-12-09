package com.fuer.anycare.common.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

/**
 * Http����Ĺ�����
 * 
 * @author mym
 * 
 */
public class HttpUtils {

	private static final int TIMEOUT_IN_MILLIONS = 15000;
	private static final String HOST ="http://222.73.227.8:8081/AnyCareServer";

	public interface CallBack {
		void onRequestComplete(String result);
	}

	/**
	 * �첽��Get����
	 * 
	 * @param urlStr
	 * @param callBack
	 */
	public static void doGetAsyn(final String urlStr, final CallBack callBack) {
		new Thread() {
			public void run() {
				try {
					String result = doGet(urlStr);
					if (callBack != null) {
						callBack.onRequestComplete(result);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

			};
		}.start();
	}

	/**
	 * �첽��Post����
	 * 
	 * @param urlStr
	 * @param params
	 * @param callBack
	 * @throws Exception
	 */
	 public static void doPostAsyn(final String urlStr, final Map<String, String> rawParams,final CallBack callBack) throws Exception{
		 new Thread(){
			 public void run(){
			 try{
				 String result = doPost(urlStr, rawParams);
				 if (callBack != null){
					 callBack.onRequestComplete(result);
				 }
			 } catch (Exception e){
				 e.printStackTrace();
			 	}
			 };
		 }.start();
	 }
	 
	 public static void doPostAsyn(final String urlStr, final String json,final CallBack callBack) throws Exception{
		 new Thread(){
			 public void run(){
			 try{
				 String result = doPost(urlStr, json);
				 if (callBack != null){
					 callBack.onRequestComplete(result);
				 }
			 } catch (Exception e){
				 e.printStackTrace();
			 	}
			 };
		 }.start();
	 }

	/**
	 * Get���󣬻�÷�������
	 * 
	 * @param urlStr
	 * @return
	 * @throws Exception
	 */
	public static String doGet(String urlStr) {
		URL url = null;
		HttpURLConnection conn = null;
		InputStream is = null;
		ByteArrayOutputStream baos = null;
		try {
			url = new URL(urlStr);
			conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			conn.setRequestMethod("GET");
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			if (conn.getResponseCode() == 200) {
				is = conn.getInputStream();
				baos = new ByteArrayOutputStream();
				int len = -1;
				byte[] buf = new byte[128];

				while ((len = is.read(buf)) != -1) {
					baos.write(buf, 0, len);
				}
				baos.flush();
				return baos.toString();
			} else {
				throw new RuntimeException(" responseCode is not 200 ... ");
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (is != null)
					is.close();
			} catch (IOException e) {
			}
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
			}
			conn.disconnect();
		}

		return null;

	}

	/**
	 * ��ָ�� URL ����POST����������
	 * 
	 * @param url���������
	 *            URL
	 * @param param�������
	 *            ���������Ӧ���� name1=value1&name2=value2 ����ʽ��
	 * @return ������Զ����Դ����Ӧ���
	 * @throws Exception
	 */
	public static String doPost(String url, Map<String, String> rawParams) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			StringBuffer paramStr = new StringBuffer();
			// ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
			for (Map.Entry<String, String> entry : rawParams.entrySet()) {
				// ��װ�������
				paramStr.append(entry.getKey() + "=" + entry.getValue() + "&");
			}

			URL realUrl = new URL(HOST+url);
			// �򿪺�URL֮�������
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			String param = "";
			if (paramStr.length() > 0) {
				param = paramStr.deleteCharAt(paramStr.length() - 1).toString();
			}
			if (param != null && !param.trim().equals("")) {
				// ��ȡURLConnection�����Ӧ�������
				out = new PrintWriter(conn.getOutputStream());
				// �����������
				out.print(param);
				// flush������Ļ���
				out.flush();
			}
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		// ʹ��finally�����ر��������������	
		}finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
	
	public static String doPost(String url, String json) {
		PrintWriter out = null;
		BufferedReader in = null;
		String result = "";
		try {
			// ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
			URL realUrl = new URL(url);
			// �򿪺�URL֮�������
			HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
			// ����ͨ�õ���������
			conn.setRequestProperty("accept", "*/*");
			conn.setRequestProperty("connection", "Keep-Alive");
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
			conn.setRequestProperty("charset", "utf-8");
			conn.setUseCaches(false);
			// ����POST�������������������
			conn.setDoOutput(true);
			conn.setDoInput(true);
			conn.setReadTimeout(TIMEOUT_IN_MILLIONS);
			conn.setConnectTimeout(TIMEOUT_IN_MILLIONS);
			if (json != null && !json.trim().equals("")) {
				// ��ȡURLConnection�����Ӧ�������
				out = new PrintWriter(conn.getOutputStream());
				// �����������
				out.print(json);
				// flush������Ļ���
				out.flush();
			}
			// ����BufferedReader����������ȡURL����Ӧ
			in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				result += line;
			}
		} catch (Exception e) {
			e.printStackTrace();
		// ʹ��finally�����ر��������������	
		}finally {
			try {
				if (out != null) {
					out.close();
				}
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
		return result;
	}
}
