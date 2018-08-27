


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
            url: 'https://api.chelaile.net.cn/adpub/adv!getAllCar.action',
            data:{
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
            info.is_backup = 0;
            if(list.length > 1) {
                info.is_backup = 1;
            }
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



var api_voicead = {

    sdkname: function() {
        return "api_voicead";
    },

    adurl_ios: function() {
        var configInfoString = GetDeviceInfo();
        var configKVArray = configInfoString.split('&');
        var deviceInfo = {};
        configKVArray.forEach(function(itemString) {
            var itemArr = itemString.split('=');
            deviceInfo[itemArr[0]] = decodeURIComponent(itemArr[1]);
        });

        console.log(JSON.stringify(deviceInfo));

        return {
            type:"banner",
            url: 'http://cs.voiceads.cn/ad/request',
            data: {
                ad_data : "AsyncPostData",
                dataFormater : this.dataFormater,
                postData:{
                    "tramaterialtype": "json",
                    "api_ver": "1.3.8",
                    "is_support_deeplink": "1", // optional 0不支持(默认值)，1直接触发 2 进入落地页再触发，不能用
                    "secure": "3", // 1 只支持http 2 只支持https 3 都支持
                    "devicetype": deviceInfo.deviceType||"",//iPhone10,1
                    "os": "IOS",
                    "osv": deviceInfo.sv||"",//'11.4.1'
                    "adid": "adid",//?
                    "imei": "",
                    "mac": "",
                    "density": "",
                    "operator": "",//config.get('operator'),
                    "net": "",//deviceInfo.nw //"WIFI"
                    "ip": "",//config.get('ip'),
                    "ua": deviceInfo.userAgent||"",
                    "ts": "",//config.get('ts'),
                    "dvw": deviceInfo.screenWidth||"",
                    "dvh": deviceInfo.screenHeight||"",
                    "orientation": "0", // 屏幕方向，强制竖屏
                    "vendor": "apple",
                    "model": "",//config.get('model'),
                    "lan": "",// deviceInfo.language "1"
                  //  "geo": config.get('geo_lng') + ',' + config.get('geo_lat'), // optional，用了还报错
                    "batch_cnt": "1", // 广告数量，只支持1
                    "appid": "5add7ce1",
                    "appname": "车来了",
                    "appver": (deviceInfo.v||"0").split('_')[0],
                    "pkgname": "com.chelaile.lite",
                    "debug": { // optional
                        /* 用于指定下发广告的交互类型，取值范围：0，不限制；1，跳转类； 2，下载类；3，特殊下载类。默认0。当前下载类广告暂不支持 deep link，为2 时下个值不能为1*/
                        "action_type": "0"
                        }
                    }
                }
            }
        },

    dataFormater : {
        parse:function(data) {
            if('AsyncPostData' == data) {
                return [{"AsyncPostData":data}];
            }
            else {
                var rows = data.batch_ma;
                if (!rows || rows.length === 0)
                return null;

                for (var i = 0; i < rows.length; i++) {
                    var row = rows[i];

                    var ad = {
                        provider_id: '12',
                        ad_order: i,
                        adType: row.adType,
                        downloadType: row.download_type,
                        packageName: row.package_name,
                        head: row.title,
                        subhead: row.sub_title,
                        pic: row.image,
                        brandIcon: row.icon,
                        link: row.landing_url,
                        deepLink: row.deep_link,
                        unfoldMonitorLink: row.impr_url.join(";"),
                        clickMonitorLink: row.click_url.join(";"),
                        picsList: row.img_urls
                    }
                    return [ad];
                }
                return [{}];
            }
        }
    },

    filter_ios : function(list) {
        return list;
    },

    aid : function () {
        return 'api_voicead_${api_voicead_displayType}';
    },
	
	adStyle : function() {
      return ${api_voicead_aid};
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

var sdk_baidu = {

    sdkname: function() {
        return "sdk_baidu";
    },
    
    adurl_ios: function() {
        return {
            url: "BaiduSDK",
            pos: "banner",
            data: {
                "appId":"d654f7e6",
                "placementId":"${sdk_baidu_placementId}"
            }
        }
    },
    
    filter_ios : function(list) {
        return list;
    },

    aid : function () {
        return 'sdk_baidu_${sdk_baidu_aid}';
    },
	
	adStyle : function() {
      return "${sdk_baidu_displayType}";
    }
}

var sdk_toutiao = {

    adurl_ios: function() {
        return {
            url: "TOUTIAOSDK",
            pos: "banner",
            data: {
                "appId":"5001451",
                "placementId":"${sdk_toutiao_placementId}"
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
        return 'sdk_toutiao_${sdk_toutiao_aid}';
    },
	
	adStyle : function() {
      return "${sdk_toutiao_displayType}";
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
                "placementId":"${sdk_inmobi_placementId}"
            }
        }
    },
    
    aid : function () {
        return 'sdk_inmobi_${sdk_inmobi_aid}';
    },
	
	adStyle : function() {
      return "${sdk_inmobi_displayType}";
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

//var ads = [api_chelaile, sdk_inmobi, sdk_toutiao, sdk_gdt, sdk_voicead, sdk_baidu];
    return {
      traceInfo : {
          traceid: '${TRACEID}',
          pid: '26'
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

