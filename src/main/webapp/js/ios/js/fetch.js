
function sdkfile(sdkname) {
    return "sdks/" + sdkname;
}

var mapSdks = {};

function getExistSdks(taskGroup) {
    var existSdks = [];
    taskGroup = taskGroup || []
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
                        data.sdk.warmSplashIntervalTime = 2*60*1000;

                    }
                    console.log("before hookCallback = "+data);
                    callback(data);
                    console.log("after hookCallback = "+data);
                }
    hookCallback.userdata = userdata;
    console.log("rule.tasks="+rule.tasks);
    tryNthTaskGroup(rule, 0, hookCallback);
}


/**
 * 尝试第n个taskGroup
 */
function tryNthTaskGroup(rule, nth, callback) {
    var taskGroups = rule.tasks;

    console.log("tryNthTaskGroup nth="+nth);
    function wrappedFn(data) {
        if (data) {
            console.log('Get data, callback directly.');
            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.FetchedAd, {data, rule, userdata:callback.userdata});
            return callback(data);
        }

        if (nth == taskGroups.length - 1) {
            console.log('Non data, and is the last group. Fail at last.');
            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.NoDataLastGroup, {rule, userdata:callback.userdata});
            return callback(null);
        }

        console.log('try next group.')
        tryNthTaskGroup(rule, nth + 1, callback);
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

            TrackClass.trackEvent(callback.userdata.uniReqId, TrackClass.Type.AllAdTimeout, {used, rule, userdata:callback.userdata});

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
                     sdkInfo.sdk.load(sdkInfo.task, rule, callback.userdata, (firstTime + secondTime), function(data) {
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
