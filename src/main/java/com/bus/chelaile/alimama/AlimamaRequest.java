package com.bus.chelaile.alimama;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/9/27.
 */
public class AlimamaRequest {
	private String[] necessaryFields = { "aid", // advertisement placement id
			"net", // wifi / cell / unknown / offline
			"netp", // network protocol, e.g. netp=LTE
			"apvc", // app version (vc)
			"apvn", // app package name
			"ip", "ver", "mn", // device type, e.g. Nexus%20S
			"os", // Android or iOS
			"osv" // e.g. 4.2.2
	};
	private String[] optionalFields = { "mnc", // 00: ChinaMobile, 01:
												// ChinaUnicom, 03:
												// ChinaTelecomm
			"adnam", // app name
			"lct", // longitude,latitude, e.g. ict=120.1,30.123
			"lt", "ct", "extdata", "bn", // device brand. If the os is iOS, set
											// this field to "Apple". Else get
											// by android.os.Build.BRAND
			"mcc", // mobile country code
			"sz", // container size, e.g. 320x50
			"rs", // device resolution
			"mac", "idfa", // for iOS only
			"imei", // for android only
			"dpr" // dp ratio
	};
	private Map<String, String> necessaryFieldsValues = new HashMap<>();
	private Map<String, String> optionalFieldsValues = new HashMap<>();

	public AlimamaRequest(String aid, String net, String netp, String apvc,
			String apvn, String ip, String ver, String mn, String os,
			String osv, String mnc, String adnam, String lct, Integer lt,
			String ct, String extdata, String bn, Integer mcc, String sz,
			String rs, String mac, String idfa, String imei, Double dpr) {
		necessaryFieldsValues.put("aid", aid);
		necessaryFieldsValues.put("net", net);
		necessaryFieldsValues.put("netp", netp);
		necessaryFieldsValues.put("apvc", apvc);
		necessaryFieldsValues.put("apvn", apvn);
		necessaryFieldsValues.put("ip", ip);
		necessaryFieldsValues.put("ver", ver);
		necessaryFieldsValues.put("mn", mn);
		necessaryFieldsValues.put("os", os);
		necessaryFieldsValues.put("osv", osv);

		// optionalFieldsValues.put("mnc", mnc);
		// optionalFieldsValues.put("adnam", adnam);
		// optionalFieldsValues.put("lct", lct);
		// optionalFieldsValues.put("lt", lt.toString());
		// optionalFieldsValues.put("ct", ct);
		// optionalFieldsValues.put("extdata", extdata);
		// optionalFieldsValues.put("bn", bn);
		// optionalFieldsValues.put("mcc", mcc.toString());
		optionalFieldsValues.put("sz", sz);
		optionalFieldsValues.put("rs", rs);
		// optionalFieldsValues.put("mac", mac);
		optionalFieldsValues.put("idfa", idfa);
		optionalFieldsValues.put("imei", imei);
		optionalFieldsValues.put("dpr", dpr == null ? null : dpr.toString());
	}

	public AlimamaRequest(String aid, String net, String netp, String apvc,
			String apvn, String ip, String ver, String mn, String os,
			String osv,String sz,String rs, String idfa, String imei, Double dpr) {
		necessaryFieldsValues.put("aid", aid);
		necessaryFieldsValues.put("net", net);
		necessaryFieldsValues.put("netp", netp);
		necessaryFieldsValues.put("apvc", apvc);
		necessaryFieldsValues.put("apvn", apvn);
		necessaryFieldsValues.put("ip", ip);
		necessaryFieldsValues.put("ver", ver);
		necessaryFieldsValues.put("mn", mn);
		necessaryFieldsValues.put("os", os);
		necessaryFieldsValues.put("osv", osv);

		// optionalFieldsValues.put("mnc", mnc);
		// optionalFieldsValues.put("adnam", adnam);
		// optionalFieldsValues.put("lct", lct);
		// optionalFieldsValues.put("lt", lt.toString());
		// optionalFieldsValues.put("ct", ct);
		// optionalFieldsValues.put("extdata", extdata);
		// optionalFieldsValues.put("bn", bn);
		// optionalFieldsValues.put("mcc", mcc.toString());
		optionalFieldsValues.put("sz", sz);
		optionalFieldsValues.put("rs", rs);
		// optionalFieldsValues.put("mac", mac);
		if( idfa != null ){
			optionalFieldsValues.put("idfa", idfa);
		}
		if( imei != null ){
			optionalFieldsValues.put("imei", imei);
		}
		if( dpr != null ){
			optionalFieldsValues.put("dpr", dpr == null ? null : dpr.toString());
		}
		
	}

	public String[] getNecessaryFields() {
		return necessaryFields;
	}

	public String[] getOptionalFields() {
		return optionalFields;
	}

	public Map<String, String> getNecessaryFieldsValues() {
		return necessaryFieldsValues;
	}

	public Map<String, String> getOptionalFieldsValues() {
		return optionalFieldsValues;
	}
}
