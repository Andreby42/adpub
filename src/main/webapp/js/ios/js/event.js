"use strict";

var exhibitTrackUrl = "https://atrace.chelaile.net.cn/exhibit";
var clickTrackUrl = "https://atrace.chelaile.net.cn/click";
var closeTrackUrl = "https://atrace.chelaile.net.cn/close";

var thirdSimpleUrl = "https://atrace.chelaile.net.cn/thirdSimple";
var thirdPartyResponseUrl = "https://atrace.chelaile.net.cn/thirdPartyResponse";

var reportCloseAdUrl;
if(typeof GetConfig == 'function') {
    var Config = GetConfig();
    if('dev' == Config.server) {
        reportCloseAdUrl = "https://dev.chelaile.net.cn/adpub/adv!closeAd.action";
    } else if('stage' == Config.server) {
        reportCloseAdUrl = "https://stage.chelaile.net.cn/adpub/adv!closeAd.action";
    } else if('api' == Config.server) {
        reportCloseAdUrl = "https://api.chelaile.net.cn/adpub/adv!closeAd.action";
    }
}

//==================================//

function reportAdsClose(sdk, ad) {
    if(!reportCloseAdUrl) {
        return;
    }
    var params = trackBaseParams(sdk, ad);

    var info = ad.info || {};
    var traceInfo = sdk.traceInfo || {};
    var picsList = info.picsList;
    var adv_image = "";
    if(picsList && picsList.length) {
        adv_image = picsList.join(";")
    }
    addParamsIfNotNull(params, "adv_title", info.head);
    addParamsIfNotNull(params, "adv_image", adv_image);
    sendTrackRequest(reportCloseAdUrl +'?'+ GetDeviceInfo(), params);
}

//==================================//

function flatUrlParams(url, params) {

    var baseUrl = "";
    var indexUrl = "";
    var query = "";

    var hasIndex = url.indexOf("#") >= 0;
    if (hasIndex) {
        var urlArray = url.split("#");
        baseUrl = urlArray[0].indexOf("?") >= 0 ? urlArray[0] + "&" : urlArray[0] + "?";
        indexUrl = "#" + urlArray.slice(1).join("#");
    } else {
        baseUrl = url.indexOf("?") >= 0 ? url + "&" : url + "?";
    }

    for (var key in params) {
        query += "&" + key + "=" + encodeURIComponent(params[key]);
    }

    var reqUrl = baseUrl + query + indexUrl;
    return reqUrl;
}

function sendTrackRequest(url, params, body) {

    var reqUrl = flatUrlParams(url, params);
    if(body != null && body != undefined) {
        if(typeof body != 'string') {
            body = JSON.stringify(body);
        }
    }

    if(body) {
        Http.post(reqUrl, null, body, 5000, function (string, response, error) {
            console.log("sendTrackRequest ret="+string + " response.header="+JSON.stringify(OCValueForKey(response, "allHeaderFields"))+ " error="+error);
        });
    }
    else {
        Http.get(reqUrl, null, 5000, function (string, response, error) {
            console.log("sendTrackRequest ret="+string + " response.header="+JSON.stringify(OCValueForKey(response, "allHeaderFields"))+ " error="+error);
        });
    }
}

function addParamsIfNotNull(params, key, value) {
    if (value != undefined && value != null) {
        params[key] = value;
    }
}

function trackBaseParams(sdk, ad) {
    var params = {};
    var info = ad.info || {};
    var traceInfo = sdk.traceInfo || {};

    addParamsIfNotNull(params, "traceid", traceInfo.traceid);
    addParamsIfNotNull(params, "pid", traceInfo.pid);
    addParamsIfNotNull(params, "aid", sdk.aid);
    addParamsIfNotNull(params, "ad_order", info.ad_order);
    addParamsIfNotNull(params, "adid", info.adid);
    addParamsIfNotNull(params, "startMode", info.startMode);
    addParamsIfNotNull(params, "stats_act", info.stats_act);
    addParamsIfNotNull(params, "viewstatus", ad.viewstatus);

    return params;
}

function trackExhibit(sdk, ad) {

    var params = trackBaseParams(sdk, ad);

    var info = ad.info || {};
    var traceInfo = sdk.traceInfo || {};

    addParamsIfNotNull(params, "show_status", info.show_status || 0);
    addParamsIfNotNull(params, "cost_time", info.cost_time);
    addParamsIfNotNull(params, "is_backup", info.is_backup);
    addParamsIfNotNull(params, "adv_title", info.head);
    addParamsIfNotNull(params, "adv_desc", info.subhead);

    sendTrackRequest(exhibitTrackUrl, params);
}

function trackClick(sdk, ad) {
    var params = trackBaseParams(sdk, ad);

    var info = ad.info || {};
    var traceInfo = sdk.traceInfo || {};

    addParamsIfNotNull(params, "adv_title", info.head);
    addParamsIfNotNull(params, "adv_desc", info.subhead);

    sendTrackRequest(clickTrackUrl, params);
}

function trackClose(sdk, ad) {
    var params = trackBaseParams(sdk, ad);
    sendTrackRequest(closeTrackUrl, params);
    reportAdsClose(sdk, ad);
}

function trackThirdBaseParams(params) {

    var trackParams = {};
    params = params || {};
    var task = params.task || {};
    var rule = params.rule || {};
    var userdata = params.userdata || {}

    if(rule.traceInfo){
        addParamsIfNotNull(trackParams, "traceid", rule.traceInfo.traceid);
        addParamsIfNotNull(trackParams, "pid", rule.traceInfo.pid);
    }
    if(task.aid) {
        addParamsIfNotNull(trackParams, "aid", task.aid());
    }

    addParamsIfNotNull(trackParams, "req_timestamp", +new Date);
    addParamsIfNotNull(trackParams, "eventId", userdata.uniReqId);

    return trackParams;
}
//{userdata, rule, task}
function trackThirdSimple(params) {

    var trackParams = trackThirdBaseParams(params);
    sendTrackRequest(thirdSimpleUrl, trackParams);
}

function getFirstInfoByData(data) {
    if(data && data.adEntityArray && data.adEntityArray.length > 0) {
        return data.adEntityArray[0].info;
    }
}
//{data, userdata, rule, task}
function trackThirdPartyResponse(params) {

    var trackParams = trackThirdBaseParams(params);

    params = params || {};
    var task = params.task || {};
    var rule = params.rule || {};
    var data = params.data || {};
    var realInfo = getFirstInfoByData(data);
    var info = realInfo || {};

    addParamsIfNotNull(trackParams, "ad_order",  info.ad_order);
    addParamsIfNotNull(trackParams, "adid",      info.adid);
    addParamsIfNotNull(trackParams, "req_time",  (data.sdk && data.sdk.didReqTime||0) - (data.sdk && data.sdk.willReqTime||0));
    addParamsIfNotNull(trackParams, "ad_status", realInfo?1:0);
    addParamsIfNotNull(trackParams, "resp_size", data.contentLength);
    if(params.error){
        addParamsIfNotNull(trackParams, "code",  params.error);
    } else {
        addParamsIfNotNull(trackParams, "code",  OCValueForKey(data.extensionData, "statusCode"));
    }

    var body = {};

    addParamsIfNotNull(body, "adv_title",   info.head);
    addParamsIfNotNull(body, "adv_desc",    info.subhead);
    addParamsIfNotNull(body, "icon_image",  info.pic);
    addParamsIfNotNull(body, "main_image",  info.picsList);
    addParamsIfNotNull(body, "link",        info.link);
    addParamsIfNotNull(body, "url_type",    info.adType);

    sendTrackRequest(thirdPartyResponseUrl, trackParams, body);
}

function trackEvent(eventId /*String*/, eventType /*String*/, params /*object*/) {
    console.log("trackEvent eventId=" + eventId + " eventType=" + eventType + " params=" + JSON.stringify(params||{}));

    if(eventType == TrackClass.Type.LoadSplash ||
        eventType == TrackClass.Type.LoadBanner
    ) {
        //{userdata, rule, task}
        trackThirdSimple(params);
    }
    else if(eventType == TrackClass.Type.LoadedSplash ||
        eventType == TrackClass.Type.LoadedBanner
    ) {
        //{userdata, data, rule, task}
        trackThirdPartyResponse(params);
    }
    else if(eventType == TrackClass.Type.FailedSplash ||
        eventType == TrackClass.Type.FailedBanner
    ) {
        //params = {error:"code", des:"", requestInfo:requestInfo, userdata,rule}
        trackThirdPartyResponse(params);
    }
}

global.trackEvent = trackEvent;

module.exports = {
    trackExhibit: trackExhibit,
    trackClick: trackClick,
    trackClose: trackClose,
    trackEvent: trackEvent
}

global.TrackClass = {
    trackExhibit: trackExhibit,
    trackClick: trackClick,
    trackClose: trackClose,
    trackEvent: trackEvent,
    Type: {
        //params = {userdata, rule, task}
        LoadSplash: "LoadSplash", //开始调用 sdk/api loadSplash方法
        LoadBanner: "LoadBanner", //开始调用 sdk/api loadBanner方法

        //params = {userdata, data, rule, task}
        LoadedSplash: "LoadedSplash", //开屏加载完成
        LoadedBanner: "LoadedBanner", //Banner加载完成

        //params = {error:"code", des:"", requestInfo:requestInfo, userdata,rule}
        FailedSplash: "FailedSplash", //开屏加载失败
        FailedBanner: "FailedBanner", //Banner加载失败

        //params = {used,rule,userdata}
        AllAdTimeout: "AllAdTimeout", //循环检查js超时

        //params = {data,rule,userdata}
        FetchedAd: "FetchedAd", //最终获取到的广告

        //params = {rule,userdata}
        NoDataLastGroup: "NoDataLastGroup" //到达最后一组配置，仍没有获取到广告

    }
}
