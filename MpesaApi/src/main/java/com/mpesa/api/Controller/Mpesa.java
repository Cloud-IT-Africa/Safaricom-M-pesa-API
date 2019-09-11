package com.mpesa.api.Controller;

import okhttp3.*;
import org.json.*;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.security.PublicKey;
import java.security.cert.*;

import javax.crypto.Cipher;
import javax.crypto.*;

import java.math.BigInteger;

public class Mpesa {
	private String appKey;
	private String appSecret;

	public Mpesa(String app_key, String app_secret) {
		appKey = app_key;
		appSecret = app_secret;
	}

	public String getAppKey() {
		return appKey;
	}

	public void setAppKey(String appKey) {
		this.appKey = appKey;
	}

	public String getAppSecret() {
		return appSecret;
	}

	public void setAppSecret(String appSecret) {
		this.appSecret = appSecret;
	}

	public String authenticate() throws IOException {

		String appKeySecret = appKey + ":" + appSecret;
		byte[] bytes = appKeySecret.getBytes("ISO-8859-1");
		String encoded = Base64.getEncoder().encodeToString(bytes);

		OkHttpClient client = new OkHttpClient();

		Request request = new Request.Builder()
				.url("https://sandbox.safaricom.co.ke/oauth/v1/generate?grant_type=client_credentials").get()
				.addHeader("authorization", "Basic " + encoded).addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		JSONObject jsonObject = new JSONObject(response.body().string());
//		System.out.println(jsonObject.getString("access_token"));

		return jsonObject.getString("access_token");
	}

	public String C2BSimulation(String shortCode, String commandID, String amount, String MSISDN, String billRefNumber)
			throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ShortCode", shortCode);
		jsonObject.put("CommandID", commandID);
		jsonObject.put("Amount", amount);
		jsonObject.put("Msisdn", MSISDN);
		jsonObject.put("BillRefNumber", billRefNumber);

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");
		System.out.println(requestJson);
		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url("https://sandbox.safaricom.co.ke/safaricom/c2b/v1/simulate").post(body).addHeader("content-type", "application/json").addHeader("authorization", "Bearer " + authenticate()).addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		System.out.println("RESPONSE BODY "+response.body().string());
		return response.body().toString();
	}

	public String B2CRequest(String initiatorName, String securityCredential, String commandID, String amount,
			String partyA, String partyB, String remarks, String queueTimeOutURL, String resultURL, String occassion)
			throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("InitiatorName", initiatorName);
		jsonObject.put("SecurityCredential", securityCredential);
		jsonObject.put("CommandID", commandID);
		jsonObject.put("Amount", amount);
		jsonObject.put("PartyA", partyA);
		jsonObject.put("PartyB", partyB);
		jsonObject.put("Remarks", remarks);
		jsonObject.put("QueueTimeOutURL", queueTimeOutURL);
		jsonObject.put("ResultURL", resultURL);
		jsonObject.put("Occassion", occassion);

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");

		OkHttpClient client = new OkHttpClient();
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url("https://sandbox.safaricom.co.ke/mpesa/b2c/v1/paymentrequest")
				.post(body).addHeader("content-type", "application/json")
				.addHeader("authorization", "Bearer " + authenticate()).addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
		return response.body().toString();
	}

	public String B2BRequest(String initiatorName, String accountReference, String securityCredential, String commandID,
			String senderIdentifierType, String receiverIdentifierType, float amount, String partyA, String partyB,
			String remarks, String queueTimeOutURL, String resultURL, String occassion) throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Initiator", initiatorName);
		jsonObject.put("SecurityCredential", securityCredential);
		jsonObject.put("CommandID", commandID);
		jsonObject.put("SenderIdentifierType", senderIdentifierType);
		jsonObject.put("RecieverIdentifierType", receiverIdentifierType);
		jsonObject.put("Amount", amount);
		jsonObject.put("PartyA", partyA);
		jsonObject.put("PartyB", partyB);
		jsonObject.put("Remarks", remarks);
		jsonObject.put("AccountReference", accountReference);
		jsonObject.put("QueueTimeOutURL", queueTimeOutURL);
		jsonObject.put("ResultURL", resultURL);

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");
		System.out.println(requestJson);

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url("https://sandbox.safaricom.co.ke/safaricom/b2b/v1/paymentrequest")
				.post(body).addHeader("content-type", "application/json")
				.addHeader("authorization", "Bearer " + authenticate()).addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		return response.body().string();

	}

	public String STKPushSimulation(String businessShortCode, String password, String timestamp, String transactionType,
			String amount, String phoneNumber, String partyA, String partyB, String callBackURL, String queueTimeOutURL,
			String accountReference, String transactionDesc) throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("BusinessShortCode", businessShortCode);
		jsonObject.put("Password", password);
		jsonObject.put("Timestamp", timestamp);
		jsonObject.put("TransactionType", transactionType);
		jsonObject.put("Amount", amount);
		jsonObject.put("PhoneNumber", phoneNumber);
		jsonObject.put("PartyA", partyA);
		jsonObject.put("PartyB", partyB);
		jsonObject.put("CallBackURL", callBackURL);
		jsonObject.put("AccountReference", accountReference);
		jsonObject.put("QueueTimeOutURL", queueTimeOutURL);
		jsonObject.put("TransactionDesc", transactionDesc);

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");

		OkHttpClient client = new OkHttpClient();
		String url = "https://sandbox.safaricom.co.ke/mpesa/stkpush/v1/processrequest";
		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url(url).post(body).addHeader("content-type", "application/json")
				.addHeader("authorization", "Bearer " + authenticate()).addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
		return response.body().toString();
	}

	public String STKPushTransactionStatus(String businessShortCode, String password, String timestamp,
			String checkoutRequestID) throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("BusinessShortCode", businessShortCode);
		jsonObject.put("Password", password);
		jsonObject.put("Timestamp", timestamp);
		jsonObject.put("CheckoutRequestID", checkoutRequestID);

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url("https://sandbox.safaricom.co.ke/mpesa/stkpushquery/v1/query")
				.post(body).addHeader("authorization", "Bearer " + authenticate())
				.addHeader("content-type", "application/json").build();

		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
		return response.body().toString();

	}

	public String reversal(String initiator, String securityCredential, String commandID, String transactionID,
			String amount, String receiverParty, String recieverIdentifierType, String resultURL,
			String queueTimeOutURL, String remarks, String ocassion) throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Initiator", initiator);
		jsonObject.put("SecurityCredential", securityCredential);
		jsonObject.put("CommandID", commandID);
		jsonObject.put("TransactionID", transactionID);
		jsonObject.put("Amount", amount);
		jsonObject.put("ReceiverParty", receiverParty);
		jsonObject.put("RecieverIdentifierType", recieverIdentifierType);
		jsonObject.put("QueueTimeOutURL", queueTimeOutURL);
		jsonObject.put("ResultURL", resultURL);
		jsonObject.put("Remarks", remarks);
		jsonObject.put("Occasion", ocassion);

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");
		System.out.println(requestJson);

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url("https://sandbox.safaricom.co.ke/safaricom/reversal/v1/request")
				.post(body).addHeader("content-type", "application/json")
				.addHeader("authorization", "Bearer xNA3e9KhKQ8qkdTxJJo7IDGkpFNV")
				.addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
		return response.body().string();
	}

	public String balanceInquiry(String initiator, String commandID, String securityCredential, String partyA,
			String identifierType, String remarks, String queueTimeOutURL, String resultURL) throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("Initiator", initiator);
		jsonObject.put("SecurityCredential", securityCredential);
		jsonObject.put("CommandID", commandID);
		jsonObject.put("PartyA", partyA);
		jsonObject.put("IdentifierType", identifierType);
		jsonObject.put("Remarks", remarks);
		jsonObject.put("QueueTimeOutURL", queueTimeOutURL);
		jsonObject.put("ResultURL", resultURL);

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");
		System.out.println(requestJson);

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url("https://sandbox.safaricom.co.ke/safaricom/accountbalance/v1/query")
				.post(body).addHeader("content-type", "application/json")
				.addHeader("authorization", "Bearer fwu89P2Jf6MB1A2VJoouPg0BFHFM")
				.addHeader("cache-control", "no-cache")
				.addHeader("postman-token", "2aa448be-7d56-a796-065f-b378ede8b136").build();

		Response response = client.newCall(request).execute();
		return response.body().string();
	}

	public String registerURL(String shortCode, String responseType, String confirmationURL, String validationURL)
			throws IOException {
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("ShortCode", shortCode);
		jsonObject.put("ResponseType", responseType);
		jsonObject.put("ConfirmationURL", confirmationURL);
		jsonObject.put("ValidationURL", validationURL);

		jsonArray.put(jsonObject);

		String requestJson = jsonArray.toString().replaceAll("[\\[\\]]", "");
		System.out.println(requestJson);

		OkHttpClient client = new OkHttpClient();

		MediaType mediaType = MediaType.parse("application/json");
		RequestBody body = RequestBody.create(mediaType, requestJson);
		Request request = new Request.Builder().url("https://sandbox.safaricom.co.ke/mpesa/c2b/v1/registerurl")
				.post(body).addHeader("content-type", "application/json")
				.addHeader("authorization", "Bearer " + authenticate()).addHeader("cache-control", "no-cache").build();

		Response response = client.newCall(request).execute();
		System.out.println(response.body().string());
		return response.body().string();
	}

	public static String encryptInitiatorPassword(String securityCertificate, String password) {
		String encryptedPassword = "1234";
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			byte[] input = password.getBytes();

			Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding", "BC");
			FileInputStream fin = new FileInputStream(new File(securityCertificate));
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			X509Certificate certificate = (X509Certificate) cf.generateCertificate(fin);
			
			PublicKey pk = certificate.getPublicKey();
			cipher.init(Cipher.ENCRYPT_MODE, pk);

			byte[] cipherText = cipher.doFinal(input);

			// Convert the resulting encrypted byte array into a string using base64
			// encoding

			encryptedPassword = Base64.getEncoder().encodeToString(cipherText);

		} catch (NoSuchAlgorithmException ex) {
			ex.printStackTrace();
		} catch (NoSuchProviderException ex) {
			ex.printStackTrace();
		} catch (NoSuchPaddingException ex) {
			ex.printStackTrace();
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (CertificateException ex) {
			ex.printStackTrace();
		} catch (InvalidKeyException ex) {
			ex.printStackTrace();
		} catch (IllegalBlockSizeException ex) {
			ex.printStackTrace();
		} catch (BadPaddingException ex) {
			ex.printStackTrace();
		}

		return encryptedPassword;
	}

}
