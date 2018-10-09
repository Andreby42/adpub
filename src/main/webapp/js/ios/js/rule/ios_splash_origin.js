
console.log('run js file splashAd.do');

var api_chelaile = {
    sdkname: function() {
        return 'api_chelaile'
    },

    adurl_ios: function() {
        return {
            type:"splash",
            url: 'https://api.chelaile.net.cn/adpub/adv!getCoopenAds.action',
            data:{
                ad_data : this.ad_data(),
                dataFormater:this.dataFormater
            }
        }
    },

    dataFormater : {
        parse:function(data) {
            
            try{
                var array = data.split("YGKJ");
                if (array.length < 2) {
                    return null;
                }
                data = array[1];
                data = JSON.parse(data);
                if(data && data.jsonr && data.jsonr.data) {
                    var rows = data.jsonr.data.ads;
                    return rows;
                }
            } catch(e) {

            }
            return [];
        }
    },
    
    filter_ios: function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[list.length - 1].info;
            info.adid = info.id;
            info.is_backup = 0;
            if(list.length > 1) {
                info.is_backup = 1;
            }
            info.picsList = [info.pic];
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

// sdk
var sdk_gdt = {

    adurl_ios: function() {
        return {
            url: "GDTSDK",
            pos: "splash",
            data: {
                appId: "1105595946",
                placementId: "1040935476065022"
            }
        }
    },

    sdkname: function() {
        return "sdk_gdt";
    },

    filter_ios: function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[0].info;
            info.isDisplay = 0;
            info.isSkip = 0;
            info.duration = 4;
            list[0].info = info;
            return [list[0]];
        }
        return [];
    },

    aid : function () {
        return 'sdk_gdt';
    }
}


var sdk_baidu = {

    adurl_ios: function() {
        return {
            url: "BaiduSDK",
            pos: "splash",
            data: {
                appId: "d654f7e6",
                placementId: "5826162"
            }
        }
    },

    sdkname: function() {
        return "sdk_baidu";
    },

    filter_ios: function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[0].info;
            info.isDisplay = 0;
            info.isSkip = 0;
            info.duration = 4;
            list[0].info = info;
            return [list[0]];
        }
        return [];
    },

    aid : function () {
        return 'sdk_baidu';
    }
}

var sdk_toutiao = {

    adurl_ios: function() {
        return {
            url: "TOUTIAOSDK",
            pos: "splash",
            data: {
                "appId":"5001451",
                "placementId":"801451568"
            }
        }
    },

    sdkname: function() {
        return "sdk_toutiao";
    },

    filter_ios: function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[0].info;
            info.isDisplay = 0;
            info.isSkip = 0;
            info.duration = 4;
            list[0].info = info;
            return [list[0]];
        }
        return [];
    },

    aid : function () {
        return 'sdk_toutiao';
    }
}

var sdk_inmobi = {

    sdkname : function() {
        return "sdk_inmobi";
    },

    adurl_ios : function() {
        return {
            url:"InMobiSdk",
            type:"splash",
            data:{
                "appId":"f83af5e921de42cf813dc475c362aaf0",
                "placementId":"1522609003688"
            }
        }
    },
    
    filter_ios: function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[0].info;
            info.isDisplay = 0;
            info.isSkip = 0;
            info.duration = 4;
            list[0].info = info;
            return [list[0]];
        }
        return [];
    },
    
    aid : function () {
        return 'sdk_inmobi';
    },
    
    adStyle : function() {
        return 2;
    }
}

var sdk_voicead = {

    sdkname : function() {
        return "sdk_voicead";
    },
    
    adurl_ios : function() {
        return {
            url:"IFLYSDK",
            pos:"banner",
            data:{
                "appId":"5acf1d60",
                "placementId":"46232AB17BDA70BED71794AD4915D12A"
            }
        }
    },

    filter_ios: function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[0].info;
            info.isDisplay = 0;
            info.isSkip = 0;
            info.duration = 4;
            list[0].info = info;
            return [list[0]];
        }
        return [];
    },
    
    aid : function () {
        return 'sdk_voicead';
    }
}

// sdk_voiced for some cities
var sdk_voicead_no2 = {

    sdkname : function() {
        return "sdk_voicead";
    },
    
    adurl_ios : function() {
        return {
            url:"IFLYSDK",
            pos:"banner",
            data:{
                "appId":"5acf1d60",
                "placementId":"B42751F67AAFC10F8CF1C090FD834962"
            }
        }
    },

    filter_ios: function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[0].info;
            info.isDisplay = 0;
            info.isSkip = 0;
            info.duration = 4;
            list[0].info = info;
            return [list[0]];
        }
        return [];
    },
    
    aid : function () {
        return 'sdk_voicead_no2';
    }
}

var sdk_adview = {

    sdkname: function() {
        return "sdk_adview";
    },

    adurl_ios: function() {
        return {
            url: "AdViewSDK",
            pos: "splash",
            data: {
                "appId":"SDK20181709050815opfx8spc79j5ria",
                "placementId":"POSIDbcr1f417qu6v"
            }
        }
    },

    filter_ios : function(list) {
        if(Array.isArray(list) && list.length > 0) {
            var info = list[0].info;
            info.isDisplay = 0;
            info.isSkip = 0;
            info.duration = 4;
            list[0].info = info;
            return [list[0]];
        }
        return [];
    },

    aid : function () {
        return 'sdk_adview';
    },

	adStyle : function() {
      return "2";
    }
}

var sdk_admobile = {

    sdkname: function() {
        return "sdk_admobile";
    },
    
    adurl_ios: function() {
        return {
            url: "ADMobSDK",
            pos: "splash",//banner
            data: {
                "appId":"2252620",
                "placementId":"0"
            }
        }
    },

    filter_ios : function(list) {
        return list;
    },

    aid : function () {
        return 'sdk_admobile';
    },

	adStyle : function() {
      return "8";
    }
}


function ads() {

//var ads = [api_chelaile, sdk_inmobi, sdk_toutiao, sdk_gdt, sdk_voicead, sdk_baidu];
    return {
      traceInfo : {
          traceid: '${TRACEID}',
          pid: '04',
          jsid: '${JSID}'
      },
      closeInfo: {
          closePic: '${closePic}'
      },
        timeouts: ${TIMEOUTS},
        tasks: ${TASKS}
    }
}
console.log('splash loaded');

var getAds = require('./fetch');
function loadAds(userdata, callback) {
    if(getAds) {
        getAds(ads(), userdata, callback);
    }
}
module.exports = loadAds;

 
