
function load(task, userdata, callback) {    
    var requestInfo = task.adurl();
    Http.post(requestInfo.url, {"Accept-Encoding":"gzip"}, requestInfo.data, 10000, function(data){
        console.log("*********" + data);
        // console.log("api homepage data="+data);
        if(task.filter)
            data = task.filter(data);
        callback(data);
    });
}

exports.load = load;

console.log("api_voicead.js loaded");
