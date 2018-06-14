const RENDER_MSG = 'render_ok';

function load(task, userdata, callback) {

    var requestInfo = task.adurl();
    var vendor = requestInfo.url;
    console.log('Load ' + vendor + " " + JSON.stringify(requestInfo));
    var sdkIns = task._sdkIns = newInstance(requestInfo.url);
    if (!sdkIns) {
        console.log(' vendor not found: ' + requestInfo.url);
        return callback({});
    }

    function wrappedFn(resp) {
        var logHead = vendor + " " + requestInfo.pos;
        console.log(logHead + " success resp=" + resp);

        if (!resp) resp = {};

        if (!resp.data) {
            console.log(logHead + ' no ad resp. return null.')
            return callback(resp);
        }

        try {
            // console.log(resp);
            var ad = task.filter(resp.data);

            if (ad == null) {
                console.log(logHead + ' ad is null ');
                return callback(resp);
            } else {
                console.log(logHead + ' get ad:' + ad);
                resp.ad = ad;
                if (requestInfo.pos == "splash") {
                    resp.isSkip = 0;
                    resp.isDisplay = 0;
                    resp.duration = 4;
                    resp.isFullShow = 0;
                }
                resp.entity = task.asEntity ? task.asEntity(ad) : ad;
                callback(resp);
            }
        } catch (e) {
            console.log(' ' + e);
            callback(resp);
        }
    }

    console.log("*********" + requestInfo.pos);
    if (requestInfo.pos == "splash") {
        sdkIns.loadSplash(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, wrappedFn);
    } else if (requestInfo.pos == "banner") {
        console.log(vendor + ' load banner')
        sdkIns.loadBanner(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, wrappedFn);
    }
}

function stop2(task) {
    var requestInfo = task.adurl();
    var vendor = requestInfo.url;
    console.log('Stop ' + vendor + " " + JSON.stringify(requestInfo));

    if (task._sdkIns && task._sdkIns.stopSplash) {
        if (requestInfo.pos == "splash") {
            task._sdkIns.stopSplash();
        }
    }
}

module.exports = {
    load: load,
    stop2: stop2
};

console.log("sdk.js loaded");
