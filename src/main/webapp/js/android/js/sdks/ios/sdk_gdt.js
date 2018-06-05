
function load(task, userdata, callback) {

    try {
        var requestInfo = task.adurl();
        console.log("sdk_gdt_requestInfo:"+JSON.stringify(requestInfo));
        var sdkIns = newInstance(requestInfo.url);
        if(sdkIns) {
            console.log("*********" + requestInfo.pos);
            if(requestInfo.pos == "splash") {
                sdkIns.loadSplash(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, function(data){
                    console.log("gdt loadSplash success data="+data);
                    if(task.filter)
                        data = task.filter(data);
                    callback(data);
                }, function(data){
                    console.log("gdt loadSplash failed data="+data);
                    callback(data);
                });
            } else if (requestInfo.pos == "banner") {
                sdkIns.loadBanner(requestInfo.data.appId, requestInfo.data.placementId, userdata, 3000, function(data){
                    console.log("gdt banner success data="+data);
                    if(task.filter)
                        data = task.filter(data);
                    callback(data);
                }, function(data){
                    console.log("gdt banner failed data="+data);
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
        console.log("sdk_gdt load e="+e);
        callback(null);
    }
}

exports.load = load;

console.log("sdk_gdt.js loaded");
