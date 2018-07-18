


env = {
    wifi: true
}

var api_chelaile = {
    sdkname: function() {
        return 'api_chelaile'
    },

    adurl_ios: function() {
        return {
            type:"banner",
            url: 'https://api.chelaile.net.cn/adpub/adv!getLineFeedAds.action?idfa=DD4764E4-D576-46C3-A28C-372056744B5E&geo_type=wgs&lineId=010-301-0&language=1&stnName=%E7%82%8E%E9%BB%84%E8%89%BA%E6%9C%AF%E9%A6%86&geo_lat=39.994435&geo_lng=116.403321&dpi=3&sv=11.4&deviceType=iPhone10,2&s=IOS&lchsrc=icon&vendor=apple&screenHeight=2208&v=5.51.0&udid=a6e419a2be097641a8279a3ceee17cdce3a9cc86&gpsAccuracy=65.000000&sign=eYq0+Bu9QOCDIOXR2E7Emg==&nw=WiFi&mac=&wifi_open=1&geo_lac=65.000000&cityId=027&lineNo=301&idfv=9C166F09-7308-43D9-8EED-6D6A6F118C3A&userAgent=Mozilla/5.0%20(iPhone;%20CPU%20iPhone%20OS%2011_4%20like%20Mac%20OS%20X)%20AppleWebKit/605.1.15%20(KHTML,%20like%20Gecko)%20Mobile/15F79&screenWidth=1242&push_open=1&direction=0&vc=10560&userId=',
            data:{
                dataFormater:this.dataFormater
            }
        }
    },
    
    dataFormater : {
        parse:function(data) {
            var array = data.split("YGKJ");
            if (array.length < 2) {
                return null;
            }
            data = array[1];
            if (typeof data == 'string')
                data = eval("a=" + data);
            var rows = data.jsonr.data.ads;
            console.log("rows="+rows);
            return rows;
        }
    },
    
    adStyle : function() {
        return 2;
    },
    
    filter_ios : function(list) {
        return list;
    },
    
    aid : function () {
        return 'api_chelaile';
    }
}

// sdk taks ===================
// ææºè°ç¨sdk

var sdk_gdt = {

    sdkname: function() {
        return "sdk_gdt";
    },
    
    adurl_ios: function() {
        return {
            url: "GDTSDK",
            pos: "banner",
            data: {
                "appId":"1105595946",
                "placementId":"3080736383701333"
            }
        }
    },

    filter_ios : function(list) {
        return list;
    },

    aid : function () {
        return 'sdk_gdt';
    },
    
    adStyle : function() {
        return 2;
    }
}

var sdk_baidu = {

    sdkname: function() {
        return "sdk_baidu";
    },
    
    adurl_ios: function() {
        return {
            url: "BaiduSDK",
            pos: "banner",
            data: {
                "appId":"bbf9b3b5",
                "placementId":"5827199"
            }
        }
    },
    
    filter_ios : function(list) {
        return list;
    },

    aid : function () {
        return 'sdk_baidu';
    },
    
    adStyle : function() {
        return 2;
    }
}

var sdk_toutiao = {

    adurl_ios: function() {
        return {
            url: "TOUTIAOSDK",
            pos: "banner",
            data: {
                "appId":"5001451",
                "placementId":"901451521"
            }
        }
    },

    sdkname: function() {
        return "sdk_toutiao";
    },

    filter_ios : function(list) {
        return list;
    },
    
    aid : function () {
        return 'sdk_toutiao';
    },
    
    adStyle : function() {
        return 2;
    }
}

var sdk_ifly = {

    sdkname : function() {
        return "sdk_ifly";
    },
    
    adurl_ios : function() {
        return {
            url:"IFLYSDK",
            pos:"banner",
            data:{
                "appId":"5acf1d60",
                "placementId":"5F7EDBCCC6C116C07DBB40EB9A937F4E"
            }
        }
    },

    filter_ios : function(list) {
        return list;
    },
    
    aid : function () {
        return 'sdk_ifly';
    },
    
    adStyle : function() {
        return 2;
    }
}

// 手机sdk inmobi
var sdk_inmobi = {

    sdkname : function() {
        return "sdk_inmobi";
    },

    adurl_ios : function() {
        return {
            url:"InMobiSdk",
            type:"banner",
            pos:"homecell",
            data:{
                "appId":"f83af5e921de42cf813dc475c362aaf0",
                "placementId":"1526909972727"
            }
        }
    },
    
    adStyle : function() {
        return 2;
    }
}

function ads() {

//var ads = [api_chelaile, sdk_inmobi, sdk_toutiao, sdk_gdt, sdk_voicead, sdk_baidu];
    return {
      traceInfo : {
          traceid: '9684e917-418e-41ea-a91e-632a4c41fff8_1529920073.829',
          pid: '00'
      },
        timeouts: [1000, 2000],
        tasks: [[sdk_toutiao]]
    }
}



var getAds = require('./fetch');
function loadAds(userdata, callback) {
    if(getAds) {
        getAds(ads(), userdata, callback);
    }
}
module.exports = loadAds;

