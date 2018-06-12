var status = 0;

function mypostOk(){
    console.log('自我埋点完成')
}

function load(task, userdata, callback) {
    var requestInfo = task.adurl();
    console.log('API for ' + requestInfo.url);

    var myurl = "http://atrace.chelaile.net.cn/thirdPartyResponse?";
    function mypar(field, value) {
        myurl += '&' + field + '=' + value;
    }

    ['traceid', 'pid', 'adid'].forEach(function(field) {
        mypar(field, userdata.traceInfo[field]);
    });
    mypar('aid', task.aid());

    function wrappedFn(data) {
        var used = new Date().getTime() - stamp1;
        mypar('req_time', used);

        console.log("api data=" + data);
        try {
            console.log('filter with data');
            var ad = task.filter(data);
            console.log('after filter ' + ad)
        } catch (e) {
            console.log(e);

            mypar('code', 500);
            Http.post(myurl, {}, {}, 1000, mypostOk);

            return callback(null);
        }

        if (!ad) {
            Http.post(myurl, {}, data, 1000, mypostOk);
            return callback(null);
        }

        mypar('ad_order', ad.ad_order);
        Http.post(myurl, {}, data, 1000, mypostOk);

        var ret = {
            isSkip: 0,
            isDisplay: 0,
            duration: 4,
            isFullShow: 0,
            ad: ad
        }
        callback(ret);
    }

    var stamp1 = new Date().getTime();

    if (requestInfo.data)
        Http.post(requestInfo.url, {
            "Accept-Encoding": "gzip"
        }, requestInfo.data, 10000, wrappedFn);
    else
        Http.get(requestInfo.url, {
            "Accept-Encoding": "gzip"
        }, 10000, wrappedFn)
}

function stop2() {
    console.log('API voicead stop');

}

exports.load = load;
exports.stop2 = stop2;

console.log("api.js loaded");
