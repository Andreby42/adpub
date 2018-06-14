function rulefile(uni_tag) {
    return "rule/" + uni_tag;
}

function sdkfile(sdkname) {
    return "sdks/" + sdkname;
}

var mapSdks = {};

/**
 * @param {string!} uni_tag
 * @brief 通过 type+pos得到的唯一标识，获取规则描述。
 */
function getRuleFn(uni_tag) {
    return require(rulefile(uni_tag), true);
}

function getSdkInfos(taskGroup) {
    var existSdks = [];
    console.log('taskGroup ' + taskGroup)
    taskGroup.forEach(function(task) {
        var sdk = getSdk(task.sdkname());
        // var sdk = getSdk('sdk'); // sdk实际上没起作用
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
    if (sdkname.indexOf('sdk_') == 0)
        sdkname = 'sdk';
    if (sdkname.indexOf('api_') == 0)
        sdkname = 'api';

    var sdk = mapSdks[sdkname];
    if (!sdk) {
        try {
            sdk = require(sdkfile(sdkname));
            console.log('load sdk:');
        } catch (e) {
            console.log(e);
        }
        if (sdk) {
            mapSdks[sdkname] = sdk;
        }
    }
    return sdk;

}

//
//
function ourUrls(traceInfo, entity, urls) {
    entity = entity || {};

    var ret = {};
    for (var k in urls)
        ret[k] = urls[k];

    ['adid', 'traceid', 'pid', 'ad_order', 'is_backup'].forEach(function(field) {
        var v = selectValue(field, traceInfo, entity);
        if (!nullOrUndefined(v)) {
            var added = '&' + field + '=' + v;
            for (var k in urls) {
                ret[k] += added;
            }
        }
    })

    console.log('exposeUrl: ' + ret.exposeUrl)
    return ret;
}


testRepeat = function(entity, pos, vendor) {
    const max = 10;

    var key = 'repeat-filter/' + (pos ? pos : 'pos-all') + '/' + (vendor ? vendor : 'vendor-all');
    var s = LocalStorage.get(key);
    // console.log('old data:' + s)
    var data = s ? eval('a=' + s) : [];

    var matched = false;
    var sum = sumEntity(entity);
    for (var i = 0; i < data.length; i++) {
        if (data[i] == sum) {
            matched = true;
            break;
        }
    }

    if (matched) {
        console.log('entity found before: ' + sum)
        return true;
    }

    console.log('add entity ' + sum + ' to list ' + key);
    data.splice(0, data.length > max ? data.length - max : 0, sum);
    // console.log('new data:' + (data));
    LocalStorage.set(key, JSON.stringify(data));

    return false;
}

function sumEntity(ad) {
    return ad.head + '#' + ad.subhead;
}

function getAds(type, pos, userdata, callback) {

    console.log("getAds:" + type + "," + pos + "," + userdata + "," + (callback));
    if (callback) {
        callback.userdata = userdata;
    }

    try {
        pos = pos || ""
        if (isString(type) && isString(pos)) {
            var uni_tag = type;
            if (pos) uni_tag += "_" + pos;
            var ruleFn = getRuleFn(uni_tag);
            if (ruleFn) {
                fetchRule(ruleFn(), callback);
                callback = null;
            } else {
                console.log('rule not found: ' + uni_tag)
            }
        }
    } catch (e) {
        console.log(e);
    } finally {
        if (callback)
            callback(null);
    }
}

// config.
var firstTime = 200;
var secondTime = 500;

function fetchRule(rule, callback) {
    console.log('fetchRule', rule, callback);

    if (!rule) return callback(null);

    if (isArray(rule.timeouts)) {
        console.log('adjust timeouts:' + rule.timeouts);
        firstTime = rule.timeouts[0] || firstTime;
        secondTime = rule.timeouts[1] || secondTime;
    }

    function feedData(data) {
        if (data) {
            console.log('get data ' + data);
            callback(data);
        } else {
            console.log('All fails');
            callback(null);
        }
    }

    tryNthTaskGroup(rule, 0, feedData);
}


/**
 * 尝试第n个taskGroup
 */
function tryNthTaskGroup(rule, nth, callback) {

    var taskGroups = rule.tasks;

    function wrappedFn(data) {
        if (data) {
            console.log('Get data, callback directly.');
            return callback(data);
        }

        if (nth >= taskGroups.length - 1) {
            console.log('Non data, and is the last group. Fail at last.');
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
        console.log('check results ' + ++checkResults._count);
        var used = now() - stamp1;
        if (used > firstTime + secondTime) {
            console.log('All timeout. fails');
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
            if (!result.ad) continue;

            succeedCount++; // increase succeed counter

            // success
            if (used > firstTime || // for data on greedy mode.
                i == 0 // if it is the first slot
            ) {
                console.log('Succeed Immediately.');
                stopCheckerAndTasks(i);
                wrappedFn(result);
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

    console.log('try taskGroup ' + nth);
    var stamp1 = now(),
        interval = 50;
    var sdkInfos = getSdkInfos(taskGroups[nth]);
    sdkInfos.forEach(function(sdkInfo) {
        var req = sdkInfo.task.adurl();
        console.log('try sdk: ' + req.url);

        var stamp1 = now();
        ['traceid', 'pid', 'adid'].forEach(function(field) {
            MdLogger.addPar(field, rule.traceInfo[field]);
        });

        sdkInfo.sdk.load(sdkInfo.task, {
            traceInfo: rule.traceInfo
        }, function(resp) {
            console.log('resp comes ' + resp);

            var used = now() - stamp1;
            MdLogger.addPar('req_time', used);
            MdLogger.addPar('code', resp.data ? 200 : 500);
            MdLogger.addPar('is_backup', nth == rule.tasks.length - 1 ? 1 : 0);
            MdLogger.addPar('aid', sdkInfo.task.aid());

            sdkInfo._result = resp;
            if (resp.ad) {
                var entity = sdkInfo.task.asEntity ? sdkInfo.task.asEntity(resp.ad) : resp.ad;
                entity.is_backup = nth == rule.tasks.length - 1 ? 1 : 0
                // ad_order != null
                if (nullOrUndefined(entity.ad_order)) entity.ad_order = 0;
                var urls = ourUrls(rule.traceInfo, entity, rule.urls);
                console.log('ourUrls: ' + JSON.stringify(urls));
                resp.urls = urls;
                console.log('**************** sdkInfo=' + sdkInfo.task.aid() + ',' + sdkInfo.task.sdkname())
                resp.aid = sdkInfo.task.aid();
                resp.refreshTime = 25000;
                resp.mixRefreshAdInterval = 5000;

                MdLogger.addPar('ad_order', entity.ad_order || 0);
            }
            MdLogger.sendThirdParty(resp.data);
        });
    });

    var checker = setInterval(checkResults, interval);
    console.log('start a new interval:' + checker)
}

var MdLogger = {
    pars: {},
    sendThirdParty: function(data) {
        var url = 'http://atrace.chelaile.net.cn/thirdPartyResponse?';
        for (var k in this.pars) {
            url += '&' + k + '=' + this.pars[k];
        }
        console.log('发送第三方埋点:' + url);
        console.log('data:' + data);
        Http.post(url, {}, typeof data == 'string' ? data : '', 5000, function() {
            console.log('成功发送第三方埋点:' + url);
        });
    },
    addPar: function(field, value) {
        this.pars[field] = nullOrUndefined(value) ? '' : value;
    }
}

function nullOrUndefined(a) {
    return typeof a == 'undefined' || a == null
}

function selectValue(field, m1, m2) {
    for (var i = 1; i < arguments.length; i++) {
        var value = arguments[field];
        if (!nullOrUndefined(value))
            return value;
    }
    return null;
}

function now() {
    return new Date().getTime();
}

function isString(str) {
    return typeof str == 'string';
}

function isArray(arr) {
    return Array.isArray(arr);
}


module.exports = getAds;
