const RENDER_MSG = 'render_ok';

function load(task, userdata, callback) {

    var requestInfo = task.adurl();
    var vendor = requestInfo.url;
    console.log('Load ' + vendor + " " + JSON.stringify(requestInfo));
	var sdkIns = task._sdkIns = newInstance(requestInfo.url);
 

    if (!sdkIns) {
        console.log(' vendor not found: ' + requestInfo.url);
        return callback(null);
    }

    console.log("*********" + requestInfo.pos);
    if (requestInfo.pos == "splash") {
        sdkIns.loadSplash(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, function(data) {
            console.log(vendor + " loadSplash success data=" + data);

            if (RENDER_MSG != data) { //
                if (task.filter)
                    data = task.filter(data);
            }
            callback(data);
        });
    } else if (requestInfo.pos == "banner") {
        console.log(vendor + ' load banner')

        sdkIns.loadBanner(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, function(data) {
            // native array,
            console.log('loadBanner callback ' + data.length);

            if (!data.length) {
                console.log(vendor + ' Mobile load no data.')
                return callback(null);
            }

            var ad = null;
            try {
                console.log(vendor + ' before filter ');
                ad = task.filter(data);
                console.log(vendor + ' after filter:' + ad);
            } catch (e) {
                console.log('' + e);
            } finally {
                callback(ad);
            }
        });
    }
}


function stop2(task) {
    var requestInfo = task.adurl();
    var vendor = requestInfo.url;
    console.log('Stop ' + vendor + " " + JSON.stringify(requestInfo));
	if (task._sdkIns && task._sdkIns.stopSplash){
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
