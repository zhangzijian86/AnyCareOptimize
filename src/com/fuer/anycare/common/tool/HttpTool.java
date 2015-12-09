package com.fuer.anycare.common.tool;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

/**
 * dis:
 * author:ma_yming
 * */
public class HttpTool {
	/**
	 * author:ma_yming dis:��ȡ����Ӧ���ݵĹ������߷���
	 * */
	public static DefaultHttpClient httpClient;
	private static String serversAddress = "http://www.any-care.cn";
	public static long totalLength;
	/**
	 * @param url ���������URL
	 * @param params �������
	 * @return ��������Ӧ�ַ���
	 * @throws Exception
	 */
	public static String postRequest(String data, Map<String, String> rawParams) throws Exception{
		// �������ӳ�ʱʱ������ݶ�ȡ��ʱʱ��   
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,20000);//�������ӳ�ʱʱ��
		HttpConnectionParams.setSoTimeout(httpParams,5000);//�������ݶ�ȡ��ʱʱ��
		httpClient = new DefaultHttpClient(httpParams);
		String result = "";
		try {
			String url = serversAddress + data;
			// ����HttpPost����
			HttpPost post = new HttpPost(url);
			// ������ݲ��������Ƚ϶�Ļ����ԶԴ��ݵĲ������з�װ
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : rawParams.entrySet()) {
				// ��װ�������
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// �����������(����post�ύ������ʵ��)
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// ����POST����
			HttpResponse httpResponse = httpClient.execute(post);
			// ����������ɹ��ط�����Ӧ
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// ��ȡ��������Ӧ�ַ���
				result = EntityUtils.toString(httpResponse.getEntity());
			}
		} catch (Exception e) {
			throw new Exception(e);
		} finally {
			httpClient.getConnectionManager().shutdown();
		}
		return result;
	}
}
