global.AddModule('event.do', (function(global) {
   var module = {};
   global = global || this;
   exports = {};
   module.exports = exports;
   (function(moudle, exports, global) {
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
       
function sdkfile(sdkname) {
    return "sdks/" + sdkname;
}

var mapSdks = {};
 
function getExistSdks(taskGroup) {
    var existSdks = [];
    taskGroup.forEach(function(task) {
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
            console.log('load sdk:' + sdk);
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
    console.log('2fetchRule', rule, userdata, callback);
    
    if (!rule) return callback(null);
    
    if (Array.isArray(rule.timeouts)) {
        console.log('adjust timeouts:' + rule.timeouts);
        firstTime = rule.timeouts[0] || firstTime;
        secondTime = rule.timeouts[1] || secondTime;
    }
    
    var hookCallback = function(data){
                    console.log("typeof data == 'object' = "+ (typeof data));
                    if(data && typeof data == 'object' && data.sdk){
                        console.log(" tryNthTaskGroup data:")
                        console.log(data)
                        
                        data.sdk.refreshTime = 25000;
                        data.sdk.traceInfo = rule.traceInfo;
                        data.sdk.mixRefreshAdInterval = 15000;
                        data.sdk.maxSplashTimeout = 8000;
                        data.sdk.warmSplashIntervalTime = 30*60*1000;
                        
                    }
                    console.log("before hookCallback = "+data);
                    callback(data);
                    console.log("after hookCallback = "+data);
                }
    hookCallback.userdata = userdata;
    console.log("rule.tasks="+rule.tasks);
    tryNthTaskGroup(rule.tasks, 0, hookCallback);
}


/**
 * 尝试第n个taskGroup
 */
function tryNthTaskGroup(taskGroups, nth, callback) {
    console.log("tryNthTaskGroup nth="+nth);
    function wrappedFn(data) {
        if (data) {
            console.log('Get data, callback directly.');
            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.FetchedAd, {data});
            return callback(data);
        }
        
        if (nth == taskGroups.length - 1) {
            console.log('Non data, and is the last group. Fail at last.');
            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.NoDataLastGroup, {});
            return callback(null);
        }
        
        console.log('try next group.')
        tryNthTaskGroup(taskGroups, nth + 1, callback);
    }
    
    /**
     * 停止除noStopTaskNth之外的task
     * @param noStopTaskNth Int 继续执行的task，可以为null，停止全部的task
     */
    function stopCheckerAndTasks(noStopTaskNth) {
        console.log('stopCheckerAndTasks');
        if (checker) {
            console.log('stop interval ' + checker);
            clearInterval(checker);
        }
        
        console.log('stop all tasks ' + (typeof noStopTaskNth == 'number' ? ('except for ' + noStopTaskNth) : ''));
        // TODO
        sdkInfos.forEach(function(sdkInfo, idx) {
                         if (idx === noStopTaskNth) return;
                         console.log('Will stop task ' + idx);
                         if (sdkInfo.sdk.stop2)
                            sdkInfo.sdk.stop2(sdkInfo.task);
                         });
    }
    
    function checkResults() {

        var used = now() - stamp1;
        if (used > firstTime + secondTime) {
            console.log('All timeout. fails');
            
            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.AllAdTimeout, {used});
            
            stopCheckerAndTasks();
            wrappedFn(null);
            return;
        }
        
        var finishCount = 0,
        succeedCount = 0;
        var sdkInfo, result;
        for (var i = 0; i < sdkInfos.length; i++) {
            sdkInfo = sdkInfos[i],
            result = sdkInfo._result;
            
            if (result == undefined) continue;
            
            finishCount++; // increase finish counter
            
            // skip if NO_RESULT
            if (!result[0]) continue;
            
            succeedCount++; // increase succeed counter
            
            // success
            if (used > firstTime || // for data on greedy mode.
                i == 0 // if it is the first slot
                ) {
                console.log('Succeed Immediately.');
                stopCheckerAndTasks(i);
                wrappedFn(result[0]);
                return;
            }
        }
        
        console.log('after checking loop')
        if (finishCount >= sdkInfos.length) {
            if (succeedCount == 0) {
                console.log('All finish without any succeed.')
                stopCheckerAndTasks();
                wrappedFn(null);
            } else {
                console.log('??? How can it be???');
            }
        }
    }
    checkResults._count = 0;
    
    console.log('try taskGroup  ' + nth);
    var stamp1 = now(),
    interval = 50;
    var sdkInfos = getExistSdks(taskGroups[nth]);
    sdkInfos.forEach(function(sdkInfo) {
                     var req = sdkInfo.task.adurl_ios();
                     console.log('try sdk: ' + req.url);
                     sdkInfo.sdk.load(sdkInfo.task, callback.userdata, (firstTime + secondTime), function(data) {
                                      console.log('uniReqId='+callback.userdata.uniReqId + ' data comes ' + data);
                                      sdkInfo._result = [data];
                                      });
                     });
    
    var checker = setInterval(checkResults, interval);
}

function now() {
    return new Date().getTime();
}

module.exports = getAds;

console.log('fetch.js loaded');

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
       var RENDER_MSG = 'render_ok';

function load(task, userdata, fetchTimeout, callback) {

    var sdknameMap = {
        "GDTSDK":"CLLGdtSdk",
        "BaiduSDK":"CLLBaiduSdk",
        "TOUTIAOSDK":"CLLTTSdk",
        "IFLYSDK":"CLLIflySdk",
        "InMobiSdk":"CLLInMobiSdk"
    }
    
    var requestInfo = task.adurl_ios();
    if(requestInfo.url && requestInfo.url.toLowerCase().indexOf("http")==0) {
        requestInfo.data = requestInfo.data || {}
        requestInfo.data.placementId = requestInfo.url;
        requestInfo.url = "CLLAdApi";
    }
    else if(requestInfo.url && sdknameMap[requestInfo.url]) {
        requestInfo.url = sdknameMap[requestInfo.url];
    }
    
    var vendor = requestInfo.url;
    var sdkIns = newInstance(requestInfo.url);
    if (!sdkIns) {
        return callback(null);
    }

    requestInfo.type = requestInfo.type || requestInfo.pos;
    if (requestInfo.type == "splash") {
    
        TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadSplash, {task,userdata});
        
        sdkIns.loadSplash(requestInfo.data, userdata, fetchTimeout,
            function(data) {
            
                try {
                    if(task.aid && data.sdk) {
                        data.sdk.aid = task.aid();
                    }
                        
                    var task_filter = task.filter_ios;
                    if (task_filter && data) {
                        data.adEntityArray = task_filter(data.adEntityArray);
                    }
                    
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadedSplash, {data});
                    
                } catch(e) {
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedSplash, {error:"jsexception", des:""+e, requestInfo:requestInfo});
                } finally {
                    callback(data);
                }
            },
            function(error) {
                error = error || "unkown";
                TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedSplash, {error:error, requestInfo:requestInfo});
                callback(null);
            }
        );
    } else if (requestInfo.type == "banner") {
        
        TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadBanner, {task,userdata});
        
        sdkIns.loadBanner(requestInfo.data, userdata, fetchTimeout,
            function(data) {
            
                try {
                    if(task.aid && data.sdk) {
                        data.sdk.aid = task.aid();
                    }
                    
                    if(task.adStyle && data.adEntityArray) {
                        for(var i=0; i<data.adEntityArray.length; i++){
                            var adentity = data.adEntityArray[i];
                            var info = adentity.info;
                            if(info) {
                                info.displayType = task.adStyle();
                                adentity.info = info;
                            }
                        }
                    }
                    
                    var task_filter = task.filter_ios;
                    if(task_filter && data){
                          data.adEntityArray = task_filter(data.adEntityArray);
                    }
                    
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadedBanner, {data});
                } catch(e) {
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedBanner, {error:"jsexception", des:""+e});
                } finally {
                    callback(data);
                }
            },
            function(error) {
                error = error || "unkown";
                TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedBanner, {error:error, requestInfo:requestInfo});
                callback(null);
            }
        );
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
