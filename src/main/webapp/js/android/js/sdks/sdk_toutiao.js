function load(task, userdata, callback) {
    // bind_global('task', task);
    // bind_global('userdata', userdata);
    // bind_global('callback', callback);

    try {
        var requestInfo = task.adurl();
        console.log("sdk_toutiao_requestInfo:" + JSON.stringify(requestInfo));
        var sdkIns = newInstance(requestInfo.url);
        if (sdkIns) {
            console.log("*********" + requestInfo.pos);
            if (requestInfo.pos == "splash") {
                sdkIns.loadSplash(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, function(data) {
                    console.log("gdt loadSplash success data=" + data);
                    if (task.filter)
                        data = task.filter(data);
                    callback(data);
                });
            } else if (requestInfo.pos == "banner") {

                sdkIns.loadBanner(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, function(data) {
                    // try {
                    console.log("toutiao banner success222333 data=" + typeof task.filter);
                    if ((typeof task.filter) == 'function') {
                        console.info('has filter')
                        data = task.filter(data);
                    }
                    console.log('TouTiao: after filter:' + data);
                    callback(data);
                    // } catch (e) {
                    //     console.log(e);
                    // }
                });
            }
            //else if(feed ? banner? ){}
        } else {
            callback(nil);
        }
    } catch (e) {
        console.log("sdk_gdt load e=" + e);
        callback(null);
    }
}

exports.load = load;

console.log("sdk_gdt.js loaded");
