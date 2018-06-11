var status = 0;

function load(task, userdata, callback) {
    var requestInfo = task.adurl();
    console.log('API for ' + requestInfo.url)

    function wrappedFn(data) {
        console.log("api data=" + data);
        try {
            console.log('filter with data');
            var ad = task.filter(data);
            console.log('after filter ' + ad)
        } catch (e) {
            console.log(e);
            return callback(null);
        }

        callback(ad ? {ad: ad} : null);
    }

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
