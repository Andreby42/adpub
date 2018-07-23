var RENDER_MSG = 'render_ok';

function load(task, rule, userdata, fetchTimeout, callback) {

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

        TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadSplash, {userdata, rule, task});

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

                    if(userdata && userdata.startMode
                        && data.adEntityArray && data.adEntityArray.length > 0) {

                        var info = data.adEntityArray[0].info;
                        info.startMode = userdata.startMode;
                        data.adEntityArray[0].info = info;
                    }
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadedSplash, {data, userdata, rule, task});

                } catch(e) {
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedSplash, {error:"jsexception", des:""+e, requestInfo:requestInfo, userdata, rule, task});
                } finally {
                    callback(data);
                }
            },
            function(error) {
                error = error || "unkown";
                TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedSplash, {error:error, requestInfo:requestInfo, userdata, rule, task});
                callback(null);
            }
        );
    } else if (requestInfo.type == "banner") {

        TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadBanner, {userdata, rule, task});

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
                                info.head = info.head || "";
                                info.subhead = info.subhead || "";
                                info.stats_act = userdata.stats_act;
                                if(info.head.length > info.subhead.length) {
                                    var tstr = info.subhead;
                                    info.subhead = info.head;
                                    info.head = tstr;
                                }
                                adentity.info = info;
                            }
                        }
                    }

                    var task_filter = task.filter_ios;
                    if(task_filter && data){
                          data.adEntityArray = task_filter(data.adEntityArray);
                    }

                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.LoadedBanner, {userdata, data, rule, task});
                } catch(e) {
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedBanner, {error:"jsexception", des:""+e, userdata, rule, task});
                } finally {
                    callback(data);
                }
            },
            function(error) {
                error = error || "unkown";
                TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedBanner, {error:error, requestInfo:requestInfo, userdata, rule, task});
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
