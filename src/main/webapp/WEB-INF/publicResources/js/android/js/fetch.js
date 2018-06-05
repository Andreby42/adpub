//==============================================================================================
//export
/**
 *
 * @param {string!} type 广告类型：如splash, banner, list等，主要是样式，不同样式，请求广告的方式会不一样。
 * @param {string?} pos  广告位置: 如type=banner时，位置可能是homepage, 也可能是 车站详情页
 * @param {function?} callback 回调:无论正确还是失败或者超时，必须保证回调
 * @brief 通过 type_pos 唯一确定 使用哪一个 rule 文件
 */
function requestAds(type, pos, userdata, callback) {

    console.log("requestAds:" + type + "," + pos + "," + userdata + "," + (callback));
    if (callback) {
        // var oldfn = callback;
        // callback = function() {
        //     console.log('callback =============:');
        //     oldfn(arguments);
        // }
        callback.userdata = userdata;
    }

    try {
        pos = pos || ""
        if (isString(type) && isString(pos)) {
            var uni_tag = type;
            if (pos) uni_tag += "_" + pos;
            var rule = getRule(uni_tag);
            if (rule) {
                fetchRule(rule(), callback);
                callback = null;
            }
        }
    } catch (e) {
        console.log(e);
    } finally {
        if (callback) callback(null);
    }
}

//==============================================================================================
var mapRules = {}
var mapSdks = {}

function rulefile(uni_tag) {
    return "rule/" + uni_tag;
}

function sdkfile(sdkname) {
    return "sdks/" + sdkname;
}

/**
 * @param {string!} uni_tag
 * @brief 通过 type+pos得到的唯一标识，获取规则描述。
 */
function getRule(uni_tag) {
    var rule = mapRules[uni_tag];
    if (!rule) {
        try {
            rule = require(rulefile(uni_tag));
        } catch (e) {
            console.log(e);
        }
        if (rule) {
            mapRules[uni_tag] = rule;
        }
    }
    return rule;
}

/**
 * @param {string!} sdkname
 * @brief 通过rule文件配置的名称，查找sdk接口
 */
function getSdk(sdkname) {
    if (sdkname && isString(sdkname)) {
        var sdk = mapSdks[sdkname];
        if (!sdk) {
            try {
                sdk = require(sdkfile(sdkname));
            } catch (e) {
                console.log(e);
            }
            if (sdk) {
                // console.log('load sdk load:' + sdk.load);
                mapSdks[sdkname] = sdk;
            }
        }
        return sdk;
    }
}

/**
 * @param {*} rule
 * @param {*} callback
 */
function fetchRule(rule, callback) {
    console.log('fetchRule', rule, callback);
    try {
        if (rule) {
            var firstTime = 200;
            var secondTime = 500;
            if (isArray(rule.timeouts)) {
                firstTime = rule.timeouts[0] || firstTime;
                secondTime = rule.timeouts[1] || secondTime;
            }

            var tasks = rule.tasks;
            var groupIter = TaskGroupIter(tasks);
            fetchTaskLine(firstTime, secondTime, groupIter, callback);
        } else {
            callback(null);
        }
    } catch (e) {
        console.log(e);
        callback(null);
    }
}

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
 * @param {*} firstTime 如果不是第一条数据的，等待时间
 * @param {*} secondTime 当前分组数据超时时间
 * @param {*} groupIter
 * @param {*} callback2
 */
function fetchTaskLine(firstTime, secondTime, groupIter, callback2) {

    try {
        console.log('fetchTaskLine start ' + (callback2))
        callNextGroup = fetchNextGroup(firstTime, secondTime, groupIter, callback2);
        //获取当前并发的task数组
        var concurren = groupIter.nextGroup();
        if (concurren && concurren.length > 0) {
            //task 存在，再检查是否有对应的sdk支持
            var existSdks = getExistSdks(concurren);
            //本地存在的sdk接口
            if (existSdks.length > 0) {
                console.log("existSdks.length2223333=" + existSdks.length);

                function mycallback(data, fetched, callback2) {
                    console.log('my callback' + data + " " + fetched);
                    if (callback2.called) {
                        //TODO:已经调用了callback，然后又回来了数据, 是否埋点
                        console.log("埋点? : " + data);
                    } else if (fetched) {
                        callback2.called = true;
                        console.log('do do do mycallback:')
                        // try {
                        //     var a = callback2;
                        console.log('xx  ' + (typeof callback2))

                        callback2(data);
                        // } catch (e) {
                        //     console.log('11111111');
                        // }
                        console.log('do do do mycallback:')
                        if (firstTimer)
                            clearTimeout(firstTimer);
                        if (secondTimer)
                            clearTimeout(secondTimer);
                    }
                }

                var resultData = new Array(existSdks.length);
                var callbackCount = 0;

                //firstTimer 超时回调，找到一条可用数据，直接回调
                function checkData() {
                    if (callback2.called)
                        return;
                    if (!resultData || !resultData.length)
                        return;

                    firstTimer = null;
                    console.log("checkData called");
                    for (var i = 0, len = resultData.length; i < len; i++) {
                        if (resultData[i]) {
                            // mycallback(resultData[i], fetched, callback2);
                            mycallback(resultData[i], true, callback2);
                            break;
                        }
                    }
                }

                //数据加载成功后,可以立即回调用的最小索引index。
                var nextFetchedIndex = 0;
                //并发调用sdk请求加载数据
                existSdks.forEach(function(sdkInfo, index) {
                    console.log("load " + sdkInfo + " index=" + index);
                    sdkInfo.sdk.load(sdkInfo.task, callback2.userdata, function(data) {
                        console.log("callbackCount = " + callbackCount + " data=" + data);
                        callbackCount++;

                        resultData[index] = data || null; //null 用来表示数据已回调，但是为空。与undefined区别

                        if (index == nextFetchedIndex && !data) {

                            //如果请求回调后，但是数据不可用，则可用最小索引后移
                            nextFetchedIndex++;
                            //检查后面是否有立即可用数据
                            while (nextFetchedIndex < resultData.length) {
                                console.log('11111----' + nextFetchedIndex);

                                var itemData = resultData[nextFetchedIndex];
                                if (itemData) {
                                    mycallback(resultData[nextFetchedIndex], true, callback2);
                                    break;
                                } else if (itemData == undefined) {
                                    //表示还没有进行回调，继续等吧
                                    break;
                                } else { //null 表示已回调，但数据为空，继续往后检查
                                    nextFetchedIndex++;
                                }
                            }
                        } else if (data) {
                            console.log('333333')
                            //获取到数据，并且是最小可用索引 或者 首次定时已超时
                            mycallback(data, index == nextFetchedIndex || !firstTimer, callback2);
                        } else {
                            console.log('44444444')
                            //TODO:请求数据失败,是否埋点
                        }

                        //请求已完全回调，但是仍然没有callback 开始下一组group
                        if (callbackCount == existSdks.length && !callback2.called) {
                            if (firstTimer)
                                clearTimeout(firstTimer);
                            if (secondTimer)
                                clearTimeout(secondTimer);
                            callNextGroup();
                        }
                    });
                });
                //设置两次超时
                //第一次超时后，顺序检查可用数据，进行回调
                var firstTimer = setTimeout(checkData, firstTime);

                //第二次超时后，开始下一组并发任务
                var secondTimer = setTimeout(function() {
                    console.log("secondTimer called");
                    callNextGroup();
                }, secondTime);
            } else {
                //没有可用sdk,进行下一组
                callNextGroup();
            }
        } else {
            //TODO:前面task已超时，并且也没有可用task，进行回调 是否埋点？
            if (!callback2.called) {
                callback2.called = true;
                callback2(null);
            }
        }
    } catch (e) {
        //TODO:出现未知异常 是否埋点？
        console.log("e=" + e);
        if (!callback2.called) {
            callback2.called = true;
            callback2(null);
        }
    }
}

function fetchNextGroup(firstTime, secondTime, groupIter, callback) {
    var hasCalledNextGroup = false;
    return function() {
        if (!hasCalledNextGroup) {
            hasCalledNextGroup = true;
            fetchTaskLine(firstTime, secondTime, groupIter, callback);
        }
    }
}

function TaskGroupIter(tasks) {
    var i = 0;
    return {
        nextGroup: function() {
            var task = tasks[i++];
            if (task && !isArray(task)) {
                task = [task];
            }
            return task;
        }
    }
}

function isString(str) {
    return typeof str == 'string';
}

function isArray(arr) {
    return Array.isArray(arr);
}

module.exports = requestAds;

console.log('fetch loaded');
