"use strict";

var RENDER_MSG = 'render_ok';
function getCloseLayout(){
  return  {
"class": "RelativeLayout",
"layout_width": "match_parent",
"layout_height": "match_parent",
"background": "@color/clear",
"children": [
  {
    "class": "RelativeLayout",
    "layout_width": "wrap_content",
    "layout_height": "wrap_content",
    "children": [
      {
        "class": "ImageView",
        "id": "bannericon",
        "layout_width": "24dp",
        "layout_height": "24dp",
        "action":"@selector/clickAdvertise:event:",
        "clickable": "true",
      },
      {
        "class": "RelativeLayout",
        "layout_width": "24dp",
        "layout_height": "24dp",
        "layout_centerInParent": "true",
        "action":"@selector/closeAdvertise:",
        "clickable": "true",
      }
    ]
  }
]
}
}
function loadHttpPost(reqUrl, header, postData, fetchTimeout, callback) {
    Http.post(reqUrl, header, postData, fetchTimeout, function (string, response, error) {
        callback(string, response, error);
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
                loadHttpPost(requestInfo.data.placementId, null, postString, fetchTimeout, function(string, response, error){
                    if(requestInfo.data.dataFormater && requestInfo.data.dataFormater.parse && string) {
                        var arr = requestInfo.data.dataFormater.parse(string);
                        if(arr && arr.length){
                            if(data.sdk){
                                data.sdk.finishedReqTime = +new Date;
                                data.sdk.didReqTime = +new Date;
                            }
                            data.adEntityArray[0].info = arr[0];
                            callResult();
                        } else {
                            callResult("-90003");
                        }
                    } else {
                        callResult(OCValueForKey(error, "code"));
                    }
                });
            }
        }
    }
    if(!hasCalled) {
        callResult();
    }
}

function load(task, rule, userdata, fetchTimeout, callback) {

    var sdknameMap = {
        "GDTSDK": "CLLGdtSdk",
        "BaiduSDK": "CLLBaiduSdk",
        "TOUTIAOSDK": "CLLTTSdk",
        "IFLYSDK": "CLLIflySdk",
        "InMobiSdk": "CLLInMobiSdk",
        "AdViewSDK": "CLLAdViewSdk",
        "ADMobSDK":"CLLAdMobileSdk"
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

            function callResult(errorCode) {
                if(errorCode) {
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedSplash, { error: errorCode, des: "" + errorCode, userdata: userdata, data: data, rule: rule, task: task });
                    callback(null);
                    return;
                }
                try {
                    if (task.aid && data.sdk) {
                        data.sdk.aid = task.aid();
                    }

                    var task_filter = task.filter_ios;
                    if (task_filter && data) {
                        data.adEntityArray = task_filter(data.adEntityArray);
                    }

                    if (userdata &&  data.adEntityArray && data.adEntityArray.length > 0) {
                        var info = data.adEntityArray[0].info;
                        if(userdata.startMode){
                            info.startMode = userdata.startMode;
                        }

                        info.isSplash = true;
                        if(info.provider_id != 1 && !info.link) {
                            info.targetType = 1;
                        }
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

            if(requestInfo.url == 'CLLAdMobileSdk')
            {
                var deviceObject = GetDeviceInfoObject() || {};
                var screenWidth = ((deviceObject.screenWidth || 750) / (deviceObject.dpi || 2));
                var rule_pid = rule.traceInfo.pid;
                if(rule_pid != '25' && rule_pid != '26' && rule_pid != '32')
                {
                    screenWidth -= 16;
                }
                var screenHeight;
                if(rule_pid == '15')
                {
                    screenHeight = 48;
                    screenWidth = 48 * 6.4;
                    userdata["bannerSize"] = 0;
                }
                else
                {
                    screenHeight = screenWidth / 4;
                    userdata["bannerSize"] = 1;
                }
            }

            var style = task.adStyle();
            var sizeObj = {
                "1": { showWidth: 180, showHeight: 88 },
                "2": { showWidth: 96, showHeight: 64 },
                "5": { showWidth: 96, showHeight: 64 },
                "8": { showWidth: screenWidth, showHeight: screenHeight }
            };

            var showSize = sizeObj[style + ""];
            if (showSize) {
                userdata.showWidth = showSize.showWidth;
                userdata.showHeight = showSize.showHeight;
            }
        }
        sdkIns.loadBanner(requestInfo.data, userdata, fetchTimeout, function (data) {
            function callResult(errorCode) {
                if(errorCode) {
                    TrackClass.trackEvent(userdata.uniReqId, TrackClass.Type.FailedBanner, { error: errorCode, des: "" + errorCode, userdata: userdata, data: data, rule: rule, task: task });
                    callback(null);
                    return;
                }
                try {
                    if (task.aid && data.sdk) {
                        data.sdk.aid = task.aid();
                    }

                    var task_filter = task.filter_ios;
                    if (task_filter && data) {
                        data.adEntityArray = task_filter(data.adEntityArray);
                    }

                    if (data && data.adEntityArray) {
                        var closePic = rule && rule.closeInfo && rule.closeInfo.closePic;
                        var hostSpotSize = rule && rule.closeInfo && rule.closeInfo.hostSpotSize;
                        var fakeRate = rule && rule.closeInfo && rule.closeInfo.fakeRate;

                        for (var i = 0; i < data.adEntityArray.length; i++) {
                            var adentity = data.adEntityArray[i];
                            var info = adentity.info;
                            if (info) {
                                if(closePic) {
                                    info.closePic = closePic;
                                    info.isDisplay = 1;
                                    info.isSkip = 1;
                                    info.hostSpotSize = hostSpotSize;
                                    info.fakeRate = eval(fakeRate);
                                }
                                if (task.adStyle) {
                                    info.displayType = task.adStyle() == '' ? 2 : parseInt(task.adStyle());
                                }
                                if(info.displayType) {
                                    info.displayType = parseInt(info.displayType);
                                }

                                info.head = info.head || "";
                                info.subhead = info.subhead || "";
                                info.stats_act = userdata.stats_act;
                                if (info.head.length > info.subhead.length) {
                                    var tstr = info.subhead;
                                    info.subhead = info.head;
                                    info.head = tstr;
                                }

                                if(userdata.startMode){
                                    info.startMode = userdata.startMode;
                                    info.isSplash = true;
                                    if(info.provider_id != 1 && !info.link) {
                                        info.targetType = 1;
                                    }
                                }

                                // add brandPic
                                /*
                                if(task.sdkname() == 'sdk_gdt') {

                                    var traceid = rule && rule.traceInfo && rule.traceInfo.traceid;
                                    if (
                                        /^e/.test(traceid) &&
                                        (   (info.head && info.head.contains("京东")) || (info.subhead && info.subhead.contains("京东")) || (info.head && info.head.contains("杜蕾斯")) || (info.subhead && info.subhead.contains("杜蕾斯")) )
                                     )
                                    {
                                        info.brandPic = "https://image3.chelaile.net.cn/47db7ec76e75407cb57a890cead60c85";
                                    }
                                }
                                */
                                if (info.closePic) {
                                  info.layout = getCloseLayout();
                                  var imageView = info.layout.children[0].children[0];
                                  imageView.url = info.closePic;

                                  var imageViewParant = info.layout.children[0];

                                  /*
                                  "layout_marginBottom":"8dp",
                                  "layout_alignParentRight": "true",
                                  "layout_alignParentBottom": "true",
                                  "layout_marginRight": "120dp",

                                  "diplayType==1，
                                  */
                                  switch (info.displayType) {
                                    case 1://"diplayType==1",
                                      imageViewParant.layout_alignParentRight = 'true';
                                      imageViewParant.layout_alignParentBottom = 'true';
                                      imageViewParant.layout_marginBottom = '8dp';
                                    var object = GetDeviceInfoObject();
                                    if (object.screenWidth > 640) {
                                      imageViewParant.layout_marginRight = '192dp';
                                    }else {
                                      imageViewParant.layout_marginRight = '165dp';
                                    }
                                    break;
                                      case 2:
                                      case 5:
                                      imageViewParant.layout_alignParentRight = 'true';
                                      imageViewParant.layout_alignParentBottom = 'true';
                                      imageViewParant.layout_marginBottom = '8dp';
                                      imageViewParant.layout_marginRight = '120dp';
                                        break;
                                        case 3:
                                        case 4:
                                        imageViewParant.layout_alignParentRight = 'true';
                                        imageViewParant.layout_marginTop = '8dp';
                                        imageViewParant.layout_marginRight = '8dp';
                                          break;
                                    default:

                                  }

                                  var btn = info.layout.children[0].children[1];
                                  if (eval(info.hostSpotSize) >= 0 && eval(info.hostSpotSize) <= 24) {
                                    btn.layout_width = info.hostSpotSize + 'dp';
                                    btn.layout_height = info.hostSpotSize + 'dp';
                                  }
                                }
                                adentity.info = info;
                            }
                        }
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
