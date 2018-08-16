// Interstitial ad js

env = {
    wifi: true
}


var api_chelaile = {
    sdkname: function() {
        return 'api_chelaile'
    },

    adurl: function() {
        return {
            type: "banner",
            url: 'https://api.chelaile.net.cn/adpub/adv!getInterstitialMineAds.action',
            data: {
                ad_data : this.ad_data(),
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

    filter_ios: function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[list.length - 1].info;
            info.adid = info.id;
            list[list.length - 1].info = info;
            return [list[list.length - 1]];
        }
        return [];
    },
    
    aid : function () {
        return 'api_chelaile';
    },

    ad_data: function() {
        return '${API_CHELAILE_DATA}'
    }
}


// sdk taks ===================
// 手机调用sdk

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
                "placementId":"${sdk_gdt_placementId}"
            }
        }
    },

    filter_ios : function(list) {
        return list;
    },

    aid : function () {
        return 'sdk_gdt_${sdk_gdt_aid}';
    },
    
    adStyle : function() {
      return "${sdk_gdt_displayType}";
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
                "placementId":"${sdk_ifly_placementId}"
            }
        }
    },

    filter_ios : function(list) {
        return list;
    },

    aid : function () {
        return 'sdk_ifly_${sdk_ifly_aid}';
    },
    
    adStyle : function() {
      return "${sdk_ifly_displayType}";
    }
}

var sdk_adview = {

    sdkname: function() {
        return "sdk_adview";
    },

    adurl_ios: function() {
        return {
            url: "AdViewSDK",
            pos: "banner",
            data: {
                "appId":"SDK20181709050815opfx8spc79j5ria",
                "placementId":"${sdk_adview_placementId}"
            }
        }
    },

    filter_ios : function(list) {
        return list;
    },

    aid : function () {
        return 'sdk_adview_${sdk_adview_aid}';
    },

    adStyle : function() {
      return '${sdk_adview_displayType}';
    }
}


function ads() {
    return {
        traceInfo: {
            traceid: '${TRACEID}',
            pid: '31'
        },
        closeInfo: {
            closePic: '${closePic}'
        },
        timeouts: ${TIMEOUTS},
        tasks: ${TASKS}
    }
}

var getAds = require('./fetch');
function loadAds(userdata, callback) {
    if(getAds) {
        getAds(ads(), userdata, callback);
    }
}
module.exports = loadAds;