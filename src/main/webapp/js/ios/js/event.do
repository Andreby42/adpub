"use strict";

var exhibitTrackUrl = "http://atrace.chelaile.net.cn/exhibit";
var clickTrackUrl = "http://atrace.chelaile.net.cn/click";
var closeTrackUrl = "http://atrace.chelaile.net.cn/close";

function addParamsIfNotNull(params, key, value) {
    console.log("params.set(" + key + "," + value + ")");
    if (value) {
        params[key] = value;
    }
}

function trackBaseParams(sdk, ad) {
    console.log("sdk=" + sdk + " ad=" + ad);
    var params = {};
    var info = ad.info || {};
    var traceInfo = sdk.traceInfo || {};

    addParamsIfNotNull(params, "traceid", traceInfo.traceid);
    addParamsIfNotNull(params, "pid", traceInfo.pid);
    addParamsIfNotNull(params, "aid", sdk.aid);
    addParamsIfNotNull(params, "ad_order", info.ad_order);
    addParamsIfNotNull(params, "adid", info.adid);

    return params;
}

function trackExhibit(sdk, ad) {

    var params = trackBaseParams(sdk, ad);

    var info = ad.info || {};
    var traceInfo = sdk.traceInfo || {};

    addParamsIfNotNull(params, "show_status", info.show_status);
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
}

function sendTrackRequest(url, params) {

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

    Http.get(reqUrl, null, 10000, function (string) {});
}

function trackEvent(eventId /*String*/, eventType /*String*/, params /*object*/) {
    console.log("trackEvent eventId=" + eventId + " eventType=" + eventType + " params=" + JSON.stringify(params||{}));
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
        /*
            params = {
                task,
                userdata
            }
        */
        LoadSplash: "LoadSplash", //开始调用 sdk/api loadSplash方法
        LoadBanner: "LoadBanner", //开始调用 sdk/api loadBanner方法

        /*
            params = {
                data
            }
        */
        LoadedSplash: "LoadedSplash", //开屏加载完成
        LoadedBanner: "LoadedBanner", //Banner加载完成


        /*
            params = {
                error:"",
                ?des:"",
                ?requestInfo:requestInfo
            }
        */
        FailedSplash: "FailedSplash", //开屏加载失败
        FailedBanner: "FailedBanner", //Banner加载失败
        
        /*
            params = {
                used:1000
            }
        */
        AllAdTimeout: "AllAdTimeout", //循环检查js超时
        
        /*
            params = {
                data:data
            }
        */
        
        FetchedAd: "FetchedAd", //最终获取到的广告
        
        /* 
        */
        NoDataLastGroup: "NoDataLastGroup" //到达最后一组配置，仍没有获取到广告

    }
}
