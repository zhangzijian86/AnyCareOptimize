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
	 * author:ma_yming dis:获取到相应数据的公共工具方法
	 * */
	public static DefaultHttpClient httpClient;
	private static String serversAddress = "http://www.any-care.cn";
	public static long totalLength;
	/**
	 * @param url 发送请求的URL
	 * @param params 请求参数
	 * @return 服务器响应字符串
	 * @throws Exception
	 */
	public static String postRequest(String data, Map<String, String> rawParams) throws Exception{
		// 设置连接超时时间和数据读取超时时间   
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams,20000);//设置连接超时时间
		HttpConnectionParams.setSoTimeout(httpParams,5000);//设置数据读取超时时间
		httpClient = new DefaultHttpClient(httpParams);
		String result = "";
		try {
			String url = serversAddress + data;
			// 创建HttpPost对象。
			HttpPost post = new HttpPost(url);
			// 如果传递参数个数比较多的话可以对传递的参数进行封装
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			for (Map.Entry<String, String> entry : rawParams.entrySet()) {
				// 封装请求参数
				params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
			}
			// 设置请求参数(设置post提交的数据实体)
			post.setEntity(new UrlEncodedFormEntity(params, HTTP.UTF_8));
			// 发送POST请求
			HttpResponse httpResponse = httpClient.execute(post);
			// 如果服务器成功地返回响应
			if (httpResponse.getStatusLine().getStatusCode() == 200) {
				// 获取服务器响应字符串
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
