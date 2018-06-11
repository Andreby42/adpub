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

    function wrappedFn(data) {
      var logHead = vendor + " " + requestInfo.pos;
      console.log( logHead + " success data=" +  data);

      if (!data || !data.data){
        console.log(logHead + ' no ad data. return null.')
        return callback(null);
      }

      try {
        // console.log(data);
        var ad = task.filter(data.data);

        if (ad == null ) {
          console.log(logHead + ' ad is null ');
          return callback(null);
        } else {
          console.log(logHead + ' get ad:' + ad);
		  if (requestInfo.pos == "splash") {
		      ad.isSkip = 0;
			  ad.isDisplay = 0;
			  ad.duration = 4;
			  ad.isFullShow = 0;
		  }
          data.ad = ad;
          data.entity = task.asEntity ? task.asEntity(ad) : ad;
          callback(data);
        }
      } catch (e) {
        console.log(' ' + e);
        callback(null);
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
