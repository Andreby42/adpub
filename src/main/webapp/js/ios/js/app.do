'use strict';

String.prototype.contains = String.prototype.contains || function(str) {
	return this.indexOf(str) >= 0;
};

String.prototype.startsWith = String.prototype.startsWith || function(prefix) {
	return this.indexOf(prefix) === 0;
};

String.prototype.endsWith = String.prototype.endsWith || function(suffix) {
	return this.indexOf(suffix, this.length - suffix.length) >= 0;
};

(function (global) {
    var moduleCaches = {};
    var systemRequire = global.require;
    global.require = function (filename, noCache, forceUpdate) {
        try {
            var name = filename;
            if (name.startsWith('./')) {
                name = name.slice(2);
            }
            if (!name.endsWith('.do')) {
                name += '.do';
            }

            var ret;
            if (noCache || forceUpdate) {
                ret = systemRequire.apply(this, arguments);
            } else {
                ret = moduleCaches[name];
                if (!(typeof ret === 'function')) {
                    ret = systemRequire.apply(this, arguments);
                    moduleCaches[name] = ret;
                }
            }

            return ret ? ret(global) : function () {};
        } catch (e) {
            console.log('e=' + e);
            delete moduleCaches[name];
            throw e;
        }
    };

    global.AddModule = function (name, code) {
        moduleCaches[name] = code;
    };


	!function(n){"use strict";function t(n,t){var r=(65535&n)+(65535&t);return(n>>16)+(t>>16)+(r>>16)<<16|65535&r}function r(n,t){return n<<t|n>>>32-t}function e(n,e,o,u,c,f){return t(r(t(t(e,n),t(u,f)),c),o)}function o(n,t,r,o,u,c,f){return e(t&r|~t&o,n,t,u,c,f)}function u(n,t,r,o,u,c,f){return e(t&o|r&~o,n,t,u,c,f)}function c(n,t,r,o,u,c,f){return e(t^r^o,n,t,u,c,f)}function f(n,t,r,o,u,c,f){return e(r^(t|~o),n,t,u,c,f)}function i(n,r){n[r>>5]|=128<<r%32,n[14+(r+64>>>9<<4)]=r;var e,i,a,d,h,l=1732584193,g=-271733879,v=-1732584194,m=271733878;for(e=0;e<n.length;e+=16)i=l,a=g,d=v,h=m,g=f(g=f(g=f(g=f(g=c(g=c(g=c(g=c(g=u(g=u(g=u(g=u(g=o(g=o(g=o(g=o(g,v=o(v,m=o(m,l=o(l,g,v,m,n[e],7,-680876936),g,v,n[e+1],12,-389564586),l,g,n[e+2],17,606105819),m,l,n[e+3],22,-1044525330),v=o(v,m=o(m,l=o(l,g,v,m,n[e+4],7,-176418897),g,v,n[e+5],12,1200080426),l,g,n[e+6],17,-1473231341),m,l,n[e+7],22,-45705983),v=o(v,m=o(m,l=o(l,g,v,m,n[e+8],7,1770035416),g,v,n[e+9],12,-1958414417),l,g,n[e+10],17,-42063),m,l,n[e+11],22,-1990404162),v=o(v,m=o(m,l=o(l,g,v,m,n[e+12],7,1804603682),g,v,n[e+13],12,-40341101),l,g,n[e+14],17,-1502002290),m,l,n[e+15],22,1236535329),v=u(v,m=u(m,l=u(l,g,v,m,n[e+1],5,-165796510),g,v,n[e+6],9,-1069501632),l,g,n[e+11],14,643717713),m,l,n[e],20,-373897302),v=u(v,m=u(m,l=u(l,g,v,m,n[e+5],5,-701558691),g,v,n[e+10],9,38016083),l,g,n[e+15],14,-660478335),m,l,n[e+4],20,-405537848),v=u(v,m=u(m,l=u(l,g,v,m,n[e+9],5,568446438),g,v,n[e+14],9,-1019803690),l,g,n[e+3],14,-187363961),m,l,n[e+8],20,1163531501),v=u(v,m=u(m,l=u(l,g,v,m,n[e+13],5,-1444681467),g,v,n[e+2],9,-51403784),l,g,n[e+7],14,1735328473),m,l,n[e+12],20,-1926607734),v=c(v,m=c(m,l=c(l,g,v,m,n[e+5],4,-378558),g,v,n[e+8],11,-2022574463),l,g,n[e+11],16,1839030562),m,l,n[e+14],23,-35309556),v=c(v,m=c(m,l=c(l,g,v,m,n[e+1],4,-1530992060),g,v,n[e+4],11,1272893353),l,g,n[e+7],16,-155497632),m,l,n[e+10],23,-1094730640),v=c(v,m=c(m,l=c(l,g,v,m,n[e+13],4,681279174),g,v,n[e],11,-358537222),l,g,n[e+3],16,-722521979),m,l,n[e+6],23,76029189),v=c(v,m=c(m,l=c(l,g,v,m,n[e+9],4,-640364487),g,v,n[e+12],11,-421815835),l,g,n[e+15],16,530742520),m,l,n[e+2],23,-995338651),v=f(v,m=f(m,l=f(l,g,v,m,n[e],6,-198630844),g,v,n[e+7],10,1126891415),l,g,n[e+14],15,-1416354905),m,l,n[e+5],21,-57434055),v=f(v,m=f(m,l=f(l,g,v,m,n[e+12],6,1700485571),g,v,n[e+3],10,-1894986606),l,g,n[e+10],15,-1051523),m,l,n[e+1],21,-2054922799),v=f(v,m=f(m,l=f(l,g,v,m,n[e+8],6,1873313359),g,v,n[e+15],10,-30611744),l,g,n[e+6],15,-1560198380),m,l,n[e+13],21,1309151649),v=f(v,m=f(m,l=f(l,g,v,m,n[e+4],6,-145523070),g,v,n[e+11],10,-1120210379),l,g,n[e+2],15,718787259),m,l,n[e+9],21,-343485551),l=t(l,i),g=t(g,a),v=t(v,d),m=t(m,h);return[l,g,v,m]}function a(n){var t,r="",e=32*n.length;for(t=0;t<e;t+=8)r+=String.fromCharCode(n[t>>5]>>>t%32&255);return r}function d(n){var t,r=[];for(r[(n.length>>2)-1]=void 0,t=0;t<r.length;t+=1)r[t]=0;var e=8*n.length;for(t=0;t<e;t+=8)r[t>>5]|=(255&n.charCodeAt(t/8))<<t%32;return r}function h(n){return a(i(d(n),8*n.length))}function l(n,t){var r,e,o=d(n),u=[],c=[];for(u[15]=c[15]=void 0,o.length>16&&(o=i(o,8*n.length)),r=0;r<16;r+=1)u[r]=909522486^o[r],c[r]=1549556828^o[r];return e=i(u.concat(d(t)),512+8*t.length),a(i(c.concat(e),640))}function g(n){var t,r,e="";for(r=0;r<n.length;r+=1)t=n.charCodeAt(r),e+="0123456789abcdef".charAt(t>>>4&15)+"0123456789abcdef".charAt(15&t);return e}function v(n){return unescape(encodeURIComponent(n))}function m(n){return h(v(n))}function p(n){return g(m(n))}function s(n,t){return l(v(n),v(t))}function C(n,t){return g(s(n,t))}function A(n,t,r){return t?r?s(t,n):C(t,n):r?m(n):p(n)}"function"==typeof define&&define.amd?define(function(){return A}):n.md5=A}(global);

	global.JsEncryptUtil = {};
	global.JsEncryptUtil.md5 = global.md5;

})(global);

global.AddModule('event.do', (function(global) {
   var module = {};
   global = global || this;
   exports = {};
   module.exports = exports;
   (function(moudle, exports, global) {
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

   })(module, exports, global);
   return module.exports;
})

);
global.AddModule('fetch.do', (function(global) {
   var module = {};
   global = global || this;
   exports = {};
   module.exports = exports;
   (function(moudle, exports, global) {
       "use strict";

var _typeof = typeof Symbol === "function" && typeof Symbol.iterator === "symbol" ? function (obj) { return typeof obj; } : function (obj) { return obj && typeof Symbol === "function" && obj.constructor === Symbol && obj !== Symbol.prototype ? "symbol" : typeof obj; };

function sdkfile(sdkname) {
    return "sdks/" + sdkname;
}

var mapSdks = {};

function getExistSdks(taskGroup) {
    var existSdks = [];
    taskGroup = taskGroup || [];
    taskGroup.forEach(function (task) {
        var sdk = getSdk(task.sdkname());
        if (sdk) existSdks.push({
            task: task,
            sdk: sdk
        });
    });
    return existSdks;
}

/**
 * @param {string!} sdkname
 * @brief 通过rule文件配置的名称，查找sdk接口
 */
function getSdk(sdkname) {

    sdkname = "sdk";
    var sdk = mapSdks[sdkname];
    if (!sdk) {
        try {
            sdk = require(sdkfile(sdkname));
        } catch (e) {
            console.log(e);
        }
        if (sdk) {
            mapSdks[sdkname] = sdk;
        }
    }
    return sdk;
}

// config.
var firstTime = 1000;
var secondTime = 2000;

function getAds(rule, userdata, callback) {

    if (!rule) return callback(null);

    if (Array.isArray(rule.timeouts)) {
        firstTime = rule.timeouts[0] || firstTime;
        secondTime = rule.timeouts[1] || secondTime;
    }

    var hookCallback = function hookCallback(data) {
        if (data && (typeof data === "undefined" ? "undefined" : _typeof(data)) == 'object' && data.sdk) {

            data.sdk.refreshTime = 15000;
            data.sdk.traceInfo = rule.traceInfo;
            data.sdk.mixRefreshAdInterval = 5000;
            data.sdk.maxSplashTimeout = 8000;
            data.sdk.warmSplashIntervalTime = 30 * 60 * 1000;
        }
        callback(data);
    };
    hookCallback.userdata = userdata;
    if (!rule.tasks || rule.tasks.length <= 0) {
        callback(null);
    } else {
        tryNthTaskGroup(rule, 0, hookCallback);
    }
}

/**
 * 尝试第n个taskGroup
 */
function tryNthTaskGroup(rule, nth, callback) {
    var taskGroups = rule.tasks;

    function wrappedFn(data) {
        if (data) {
            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.FetchedAd, { data: data, rule: rule, userdata: callback.userdata });
            return callback(data);
        }

        if (nth == taskGroups.length - 1) {
            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.NoDataLastGroup, { rule: rule, userdata: callback.userdata });
            return callback(null);
        }

        tryNthTaskGroup(rule, nth + 1, callback);
    }

    /**
     * 停止除noStopTaskNth之外的task
     * @param noStopTaskNth Int 继续执行的task，可以为null，停止全部的task
     */
    function stopCheckerAndTasks(noStopTaskNth) {
        if (checker) {
            clearInterval(checker);
        }
        // TODO
        sdkInfos.forEach(function (sdkInfo, idx) {
            if (idx === noStopTaskNth) return;
            if (sdkInfo.sdk.stop2) sdkInfo.sdk.stop2(sdkInfo.task);
        });
    }

    function checkResults() {

        var used = now() - stamp1;
        if (used > firstTime + secondTime) {

            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.AllAdTimeout, { used: used, rule: rule, userdata: callback.userdata });

            stopCheckerAndTasks();
            wrappedFn(null);
            return;
        }

        var finishCount = 0,
            succeedCount = 0;
        var sdkInfo, result;
        for (var i = 0; i < sdkInfos.length; i++) {
            sdkInfo = sdkInfos[i], result = sdkInfo._result;

            if (result == undefined) continue;

            finishCount++; // increase finish counter

            // skip if NO_RESULT
            if (!result[0]) continue;

            succeedCount++; // increase succeed counter

            // success
            if (used > firstTime || // for data on greedy mode.
            i == 0 // if it is the first slot
            ) {
                    stopCheckerAndTasks(i);
                    wrappedFn(result[0]);
                    return;
                }
        }

        if (finishCount >= sdkInfos.length) {
            if (succeedCount == 0) {
                console.log('All finish without any succeed.');
                stopCheckerAndTasks();
                wrappedFn(null);
            } else {
                stopCheckerAndTasks();
                if (minIndex > 0 && minIndex < sdkInfos.length - 1) {
                    sdkInfo = sdkInfos[minIndex];
                    result = sdkInfo._result;
                    wrappedFn(result[0]);
                }
            }
        }
    }
    checkResults._count = 0;

    var stamp1 = now(),
        interval = 50;
    var sdkInfos = getExistSdks(taskGroups[nth]);
    sdkInfos.forEach(function (sdkInfo) {
        var req = sdkInfo.task.adurl_ios();
        console.log('try sdk: ' + req.url);
        sdkInfo.sdk.load(sdkInfo.task, rule, callback.userdata, firstTime + secondTime, function (data) {
            console.log('uniReqId=' + callback.userdata.uniReqId + ' data comes ' + data);
            sdkInfo._result = [data];
        });
    });

    var checker = setInterval(checkResults, interval);
}

function now() {
    return new Date().getTime();
}

module.exports = getAds;

   })(module, exports, global);
   return module.exports;
})

);
global.AddModule('sdks/sdk.do', (function(global) {
   var module = {};
   global = global || this;
   exports = {};
   module.exports = exports;
   (function(moudle, exports, global) {
       "use strict";

var RENDER_MSG = 'render_ok';

function loadHttpPost(reqUrl, header, postData, fetchTimeout, callback) {
    Http.post(reqUrl, header, postData, fetchTimeout, function (string, response, error) {
        callback(string);
    });
}

function loadThirdPostApiIfNeed(requestInfo, fetchTimeout, data, callResult) {
    var hasCalled = false;
    if(data && data.adEntityArray && data.adEntityArray.length)
    {
        if(requestInfo.data && requestInfo.data.ad_data){
            if(requestInfo.data.ad_data == "AsyncPostData") {
                var postString = "";
                if(requestInfo.data.postData){
                    postString = JSON.stringify(requestInfo.data.postData);
                }
                hasCalled = true;
                data.adEntityArray[0].info = undefined;
                loadHttpPost(requestInfo.data.placementId, null, postString, fetchTimeout, function(string){
                    if(requestInfo.data.dataFormater && requestInfo.data.dataFormater.parse && string) {
                        var arr = requestInfo.data.dataFormater.parse(string);
                        if(arr && arr.length){
                            if(data.sdk){
                                data.sdk.finishedReqTime = +new Date;
                                data.sdk.didReqTime = +new Date;
                            }
                            data.adEntityArray[0].info = arr[0];
                        }
                    }
                    callResult();
                });
            }
        }
    }
    if(!hasCalled) {
        callResult();
    }
}

var api_shunfei = {

	sdkname: function() {
	      return "api_chelaile";
	},

	adurl_ios: function() {
	    //var config = JsFixedConfig.getJsFixedConfig();
        var configInfoString = GetDeviceInfo();
        var configKVArray = configInfoString.split('&');
        var deviceInfo = {};
        configKVArray.forEach(function(itemString) {
            var itemArr = itemString.split('=');
            deviceInfo[itemArr[0]] = decodeURIComponent(itemArr[1]);
        });

        console.log('deviceInfo=' + JSON.stringify(deviceInfo));

	    console.log("parseInt(deviceInfo.dct || '')=" + parseInt(deviceInfo.dct || ''));
	        var geolng = deviceInfo.geo_lng || '' ;
	        var geolat = deviceInfo.geo_lat || '';
	        var ts = +new Date;

            console.log("ts=" + ts);

	        var sv1 = deviceInfo.sv || '' + "";
			var sv = sv1.split(".");

	        var micro = 0;

	        if( sv.length == 3 ){
	        	micro = sv[2];
	        }

	        var net = parseInt(deviceInfo.dct || ''); // 有道用dct
	        if (net >= 11 && net <= 13) {
	          net = net - 9;
	        } else {
	          net = 1;
	        }

			var sign = JsEncryptUtil.md5('177'+'g@^6*1n@E7IX#)SuJ6SE$#BQ8rV*)O8y'+ts)+'';
            var dataFormater = this.dataFormater;
	        var ret = {
                type:"banner",
	            url: 'http://i-mbv.biddingx.com/api/v1/bid',
                data:{
                    ad_data:"AsyncPostData",
                    dataFormater:dataFormater,
    	            postData: {
    	            	 "ip": deviceInfo.ip || ''+'',
    	            	 "user_agent": deviceInfo.ua || ''+'',
    	            	 "detected_time": parseInt(ts),
    	            	 "time_zone": "+0800",
    					 "detected_language": "en_",

    					 "geo": {
    						"latitude":parseFloat(deviceInfo.geo_lat || ''+''),
    						"longitude":parseFloat(deviceInfo.geo_lng || ''+'')
    						},

    	            	 "mobile": {
    	            		 "device_id":deviceInfo.mac || '' + '',
    	            		 "device_type":1,
    	            		 "platform":1,
    	            		 "os_version": {
    	            			 "os_version_major": parseInt(sv[0]),
    	            			 "os_version_minor": parseInt(sv[1]),
                                 "os_version_micro": parseInt(micro)
    	            			 },

    						 "brand":deviceInfo.vendor || ''+'',
    						 "model":deviceInfo.deviceType || ''+'',

    	        	         "screen_width":parseInt(deviceInfo.screenWidth || ''+''),
    	        	         "screen_height": parseInt(deviceInfo.screenHeight || ''+''),
    	        	         "wireless_network_type":parseInt(net),
    	        	         "for_advertising_id":deviceInfo.idfa || ''+'',
    	        	         "mobile_app": {
    	        	        	 "app_id":1987,
    	        	        	 "sign":sign,
    	        	        	 "app_bundle_id":'com.ygkj.chelaile.standard',
    							 "first_launch":eval(deviceInfo.firstLaunch || ''+'')
    	        	         }
    	            	 },

    	            	"adslot":[
    	            			 {
    	            				 "ad_block_key":1985,
    	            				 "adslot_type":17,
    	            				 "width":179,
    	            			     "height":88
    	            			 }
    	            	],

    	            	 "api_version":"1.6",
    	            	 "is_test":false,

    	            }
                }
	        };

            return ret;
    },

    dataFormater : {
        parse:function(data) {
            if('AsyncPostData' == data) {
                return [{"AsyncPostData":data}];
            }
            else {
                console.log('****' + JSON.stringify(data))
                if (typeof data == 'string')
	            data = eval("a=" + data);

	        var rows = data.batch_ma;
	        if (!rows || rows.length === 0)
	            return null;

	        for (var i = 0; i < rows.length; i++) {
	            var row = rows[i];

	            var ad = {
	                provider_id: '12',
	                ad_order: i,
	                adType: row.adType,
	                downloadType: row.download_type,
	                packageName: row.package_name,
	                head: row.title,
	                subhead: row.sub_title,
	                pic: row.image,
	                brandIcon: row.icon,
	                link: row.landing_url,
	                deepLink: row.deep_link,
	                unfoldMonitorLink: row.impr_url.join(";"),
	                clickMonitorLink: row.click_url.join(";"),
					picsList: row.img_urls
	            }
	            return [ad];
	        }
	        return [{}];
            }
        }
    },

    filter_ios: function(list) {
        return list;
    },

    aid : function () {
        return 'api_shunfei_2';
    },

    adStyle : function() {
        return 2;
    }
}


function load(task, rule, userdata, fetchTimeout, callback) {

    task = api_shunfei;

    var sdknameMap = {
        "GDTSDK": "CLLGdtSdk",
        "BaiduSDK": "CLLBaiduSdk",
        "TOUTIAOSDK": "CLLTTSdk",
        "IFLYSDK": "CLLIflySdk",
        "InMobiSdk": "CLLInMobiSdk"
    };
    var requestInfo = task.adurl_ios();
    if (requestInfo.url && requestInfo.url.toLowerCase().indexOf("http") == 0) {
        requestInfo.data = requestInfo.data || {};
        requestInfo.data.placementId = requestInfo.url;
        requestInfo.url = "CLLAdApi";
    } else if (requestInfo.url && sdknameMap[requestInfo.url]) {
        requestInfo.url = sdknameMap[requestInfo.url];
    }

    var vendor = requestInfo.url;
    var sdkIns = newInstance(requestInfo.url);
    if (!sdkIns) {
        return callback(null);
    }

    requestInfo.type = requestInfo.type || requestInfo.pos;
    if (requestInfo.type == "splash") {

        TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadSplash, { userdata: userdata, rule: rule, task: task });

        sdkIns.loadSplash(requestInfo.data, userdata, fetchTimeout, function (data) {

            function callResult() {
                try {
                    if (task.aid && data.sdk) {
                        data.sdk.aid = task.aid();
                    }

                    var task_filter = task.filter_ios;
                    if (task_filter && data) {
                        data.adEntityArray = task_filter(data.adEntityArray);
                    }

                    if (userdata && userdata.startMode && data.adEntityArray && data.adEntityArray.length > 0) {
                        var info = data.adEntityArray[0].info;
                        info.startMode = userdata.startMode;
                        data.adEntityArray[0].info = info;
                    }
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadedSplash, { data: data, userdata: userdata, rule: rule, task: task });
                    callback(data);
                } catch (e) {
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedSplash, { error: "-91000", des: "" + e, requestInfo: requestInfo, userdata: userdata, rule: rule, task: task });
                    callback(null);
                }
            }
            loadThirdPostApiIfNeed(requestInfo, fetchTimeout, data, callResult);

        }, function (error) {
            error = error || "-90000";
            TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedSplash, { error: error, requestInfo: requestInfo, userdata: userdata, rule: rule, task: task });
            callback(null);
        });
    } else if (requestInfo.type == "banner") {

        TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadBanner, { userdata: userdata, rule: rule, task: task });

        if (task.adStyle) {
            var style = task.adStyle();
            var sizeObj = {
                "1": { showWidth: 180, showHeight: 88 },
                "2": { showWidth: 96, showHeight: 64 },
                "5": { showWidth: 96, showHeight: 64 }
            };
            var showSize = sizeObj[style + ""];
            if (showSize) {
                userdata.showWidth = showSize.showWidth;
                userdata.showHeight = showSize.showHeight;
            }
        }
        sdkIns.loadBanner(requestInfo.data, userdata, fetchTimeout, function (data) {
            function callResult() {
                try {
                    if (task.aid && data.sdk) {
                        data.sdk.aid = task.aid();
                    }

                    if (data && data.adEntityArray) {
                        var closePic = rule && rule.closeInfo && rule.closeInfo.closePic;

                        for (var i = 0; i < data.adEntityArray.length; i++) {
                            var adentity = data.adEntityArray[i];
                            var info = adentity.info;
                            if (info) {
                                if(closePic)
                                    info.closePic = closePic;
                                if (task.adStyle) {
                                    info.displayType = task.adStyle();
                                }
                                info.head = info.head || "";
                                info.subhead = info.subhead || "";
                                info.stats_act = userdata.stats_act;
                                if (info.head.length > info.subhead.length) {
                                    var tstr = info.subhead;
                                    info.subhead = info.head;
                                    info.head = tstr;
                                }
                                adentity.info = info;
                            }
                        }
                    }

                    var task_filter = task.filter_ios;
                    if (task_filter && data) {
                        data.adEntityArray = task_filter(data.adEntityArray);
                    }

                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadedBanner, { userdata: userdata, data: data, rule: rule, task: task });
                    callback(data);

                } catch (e) {
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedBanner, { error: "-91001", des: "" + e, userdata: userdata, data: data, rule: rule, task: task });
                    callback(null);
                }
            }
            loadThirdPostApiIfNeed(requestInfo, fetchTimeout, data, callResult);

        }, function (error, data, extension) {
            error = error || "-90001";
            TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedBanner, { error: error, requestInfo: requestInfo, userdata: userdata, data: data, rule: rule, task: task });
            callback(null);
        });
    }
}

function stop2(task) {
    var requestInfo = task.adurl_ios();
    var vendor = requestInfo.url;
}

module.exports = {
    load: load,
    stop2: stop2
};

   })(module, exports, global);
   return module.exports;
})

);
require('./event');
