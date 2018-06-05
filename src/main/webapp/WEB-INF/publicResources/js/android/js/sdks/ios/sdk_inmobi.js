
function load(task, userdata, callback) {

    try {
        var requestInfo = task.adurl();
        console.log("sdk_inmobi_requestInfo:"+JSON.stringify(requestInfo));
        var sdkIns = newInstance(requestInfo.url);
        if(sdkIns) {
            console.log("*********" + requestInfo.pos);
            if(requestInfo.pos == "splash") {
                sdkIns.loadSplash(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, function(data){
                    console.log("inmobi loadSplash success data="+data);
                    if(task.filter)
                        data = task.filter(data);
                    callback(data);
                }, function(data){
                    console.log("inmobi loadSplash failed data="+data);
                    callback(data);
                });
            } else if (requestInfo.pos == "banner") {
                console.log("inmobi sdkIns ="+sdkIns);
                sdkIns.loadBanner(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, function(data){
                    console.log("inmobi banner success data="+data);
                    if(task.filter)
                        data = task.filter(data);
                    callback(data);
                }, function(data){
                    console.log("inmobi banner failed data="+data);
                    callback(data);
                });
            }
            //else if(feed ? banner? ){}
        }
        else {
            callback(nil);
        }
    }
    catch(e) {
        console.log("sdk_inmobi load e="+e);
        callback(null);
    }
}

exports.load = load;

console.log("sdk_inmobi.js loaded");
