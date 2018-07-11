var status = 0;

function load(task, userdata, callback) {

    function wrappedFn(data) {
        console.log("api data=" + data);
        var ret = {
            data: data
        };
        try {
            console.log('filter with data');
            var ad = task.filter(data);
            console.log('after filter ' + ad)
        } catch (e) {
            console.log(e);
        }

        if (!ad) {
            return callback(ret);
        }

        if (task.sdkname() == 'api_chelaile') {
            var ret = {
                isSkip: ad.isSkip,
                isDisplay: ad.isDisplay,
                duration: ad.duration,
                isFullShow: ad.isFullShow
            }
        } else {
            var ret = {
                isSkip: 0,
                isDisplay: 0,
                duration: 4,
                isFullShow: 0
            }
        }
        ret.ad = ad;
        ret.data = data;
        callback(ret);
    }

    if (task.ad_data) {
        console.log('A direct AD comes.');
        wrappedFn(task.ad_data());
        return;
    } else { 
        var requestInfo = task.adurl();
        console.log('API for ' + requestInfo.url);
        if (requestInfo.data) {
            Http.post(requestInfo.url, {
                "Accept-Encoding": "gzip"
            }, requestInfo.data, 10000, wrappedFn);
        }
        else {
            Http.get(requestInfo.url, {
                "Accept-Encoding": "gzip"
            }, 10000, wrappedFn)
        }
    }
}

function stop2() {
    console.log('API voicead stop');

}

exports.load = load;
exports.stop2 = stop2;

console.log("api.js loaded");
