// right ad js

env = {
    wifi: true
}


var api_chelaile = {
    sdkname: function() {
        return 'api_chelaile'
    },

    adurl: function() {
        return {
            url: 'https://api.chelaile.net.cn/adpub/adv!getRightTopAds.action'
        }
    },

    filter: function(data) {
        var array = data.split("YGKJ");
        if (array.length < 2) {
            return null;
        }
        data = array[1];
        if (typeof data == 'string')
            data = eval("a=" + data);

        var rows = data.jsonr.data.ads;

        if (!rows || rows.length == 0)
            return null;
        var row = rows[rows.length - 1];
        var ad = {
			id: row.id,
			adid: row.id,
            provider_id: '1',
			type: 1,
			combpic: row.pic,
			adMode: row.adMode,
            link: row.link,
            unfoldMonitorLink: row.unfoldMonitorLink,
            clickMonitorLink: row.clickMonitorLink,
			monitorType: row.monitorType,
            openType: row.openType,
            ad_order: 0,
            pic: row.pic,
            picsList: [row.pic]
        };

        return ad;
    },

    aid : function () {
        return 'api_chelaile';
    },

    ad_data : function () {
        return '${API_CHELAILE_DATA}'
    }
}

var api_yd = {
    sdkname: function() {
        return 'api_yd'
    },

    adurl: function() {
        // 有道post方式要单独设置一个请求头，不支持json格式，所以还是拼 get 链接
        var config = JsFixedConfig.getJsFixedConfig();
        var params = {
          "av" : config.get('v').split('_')[0],
          "ct" : config.get('ct'),
          "dct" : config.get('dct'),
          //"udid" : config.get('udid').toUpperCase(),
		  "udid":'BA8C0E13-F99A-4294-BABA-1489C33E9B6D',
          "ll" : config.get('geo_lng') + ',' + config.get('geo_lat'),
          "lla" : config.get('geo_lac'),
          "llt" : config.get('llt'),
          "llp" : config.get('llp'),
          "wifi" : config.get('wifi'),
          "sc_a" : config.get('screenDensity'), // optional
          // 不同位置需要更改
          "id" : "7a7b059ca39624f1c1ec24fa2ad375f6", // 广告位，不知是什么
          "ran" : "1", // optional 一次请求的广告数量，默认值为 1
        }
        var str = 'http://gorgon.youdao.com/gorgon/request.s?';
        for (var p in params) {
          str = str + p + '=' + params[p] + "&";
        }
        str = str.substring(0, str.length - 1);
        return {
            // url: 'http://gorgon.youdao.com/gorgon/request.s?id=e3f49841bbd3ceb0c6a531ca32f4a754&udid=BA8C0E13-F99A-4294-BABA-1489C33E9B6D&imei=BA8C0E13-F99A-4294-BABA-1489C33E9B6D&lla=73.0&llp=p&wifi=&rip=10.168.0.10&imeimd5=305612168A059FC9CCDAC8D95D99E485&ct=2&dct=0&ll=116.403538,39.994026&auidmd5=305612168A059FC9CCDAC8D95D99E485&av=5.50.0&llt=1'
            url: str
        }
    },

    filter: function(data) {
        if (typeof data == 'string')
            data = eval("a=" + data);

        var rows = data.mainimage ? [data] : data;

        if (!rows || rows.length == 0)
            return null;

        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];

            var ad = {
                provider_id: "11",
                id: row.creativeid,
                link: row.clk,
                unfoldMonitorLink: row.imptracker.join(";"),
                clickMonitorLink: row.clktrackers.join(";"),
                deepLink: row.deeplink,
                dptrackers: row.dptrackers,
                adType: row.ydAdType,
                brandIcon: row.iconimage,
                pic: row.mainimage,
                head: row.title,
                ad_order: i,
                subhead: row.text,
                packageName: row.packageName
            }
            console.log("ad = " + ad.link + "  ad  pic ==  " + ad.pic);
            return ad;
        }
        return null;
    },

    aid : function () {
        return 'api_yd';
    }

}

var api_voicead = {

    sdkname: function() {
        return "api_voicead";
    },

    adurl: function() {
        var config = JsFixedConfig.getJsFixedConfig();

        var net = parseInt(config.get('dct')); // 有道用dct
        if (net >= 11 && net <= 13) {
          net = net - 7;
        } else {
          net = config.get('ct');
        }
        var geo = config.get('geo_lng') + ',' + config.get('geo_lat');

        return {
            url: 'http://cs.voiceads.cn/ad/request',
            data: {
                "tramaterialtype": "json",
                "api_ver": "1.3.8",
                "is_support_deeplink": "1", // optional 0不支持(默认值)，1直接触发 2 进入落地页再触发，不能用
                "secure": "3", // 1 只支持http 2 只支持https 3 都支持
                "devicetype": "0",
                "os": "Android",
                "osv": config.get('sv'),
                "adid": config.get('AndroidID'),
                "imei": config.get('imei'),
                "mac": config.get('mac'),
                "density": config.get('screenDensity'),
                "operator": config.get('operator'),
                "net": net,
                "ip": config.get('ip'),
                "ua": config.get('ua'),
                "ts": config.get('ts'),
                "dvw": config.get('screenWidth'),
                "dvh": config.get('screenHeight'),
                "orientation": "0", // 屏幕方向，强制竖屏
                "vendor": config.get('vendor'),
                "model": config.get('model'),
                "lan": config.get('lan'),
              //  "geo": config.get('geo_lng') + ',' + config.get('geo_lat'), // optional，用了还报错
                "batch_cnt": "1", // 广告数量，只支持1
                "appid": "5add7ce1",
                "appname": "车来了",
                "appver": config.get('v').split('_')[0],
                "pkgname": "com.ygkj.chelaile.standard",
                "debug": { // optional
                    /* 用于指定下发广告的交互类型，取值范围：0，不限制；1，跳转类； 2，下载类；3，特殊下载类。默认0。当前下载类广告暂不支持 deep link，为2 时下个值不能为1*/
                    "action_type": "0",
                    /* 用于指定下发广告的落地页类型，取值范围：0，不限制；1，包含 landing_url 和 deep_link； 2，仅包含 landing_url。不指定 的话，按值为 0 处理。*/
                    "landing_type": "0"
                },
                // 不同位置需要更改
                "adunitid": "10BE44800806378732740DB5B955800C", // 广告位
                "adw": config.get('screenWidth'), // 广告图宽，看后台申请广告位的尺寸
                "adh": "92",
                "isboot": "0", //1表示开屏；0表示非开屏
            }
        };
    },

    filter: function(data) {
        if (typeof data == 'string')
            data = eval("a=" + data);

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
                clickMonitorLink: row.click_url.join(";")
            }
            return ad;
        }
        return null;
    },

    aid : function () {
        return 'api_voicead';
    }
}


var api_zm = {

	    sdkname: function() {
	        return "api_zm";
	    },

	    adurl: function() {
	        var config = JsFixedConfig.getJsFixedConfig();

	        var net = parseInt(config.get('dct')); // 有道用dct
	        if (net >= 11 && net <= 13) {
	          net = net - 9;
	        } else {
	          net = 1;
	        }
	        
	        return {
	            url: 'http://adalliance.zmeng123.com/zmtmobads/v4/getAd.do',
	            data: {
					"reqInfo": {
						"adSlotId": "multi_05",
						"accessToken": "dHlwZTphY2Nlc3NfdG9rZW4gYWxnOkFFUyA=.YXBwX2lkOlJlemFyMDAwMDIg.3dj1iAlb0nnCmxIv3Opj41etWfzSY2Bnd4ICsBCgt6HG2UTmnRhnOxEvpxe73wfBqK8nUO6xuHHazmuft204fg"
					},
					"adSlotInfo": {
						"mimes": "jpg,gif,icon,png,",
						"slotWidth": config.get('screenWidth'),
						"slotHeight": '92'
					},
					"mobileInfo": {
						"osVersion": config.get('sv'),
						"appVersion": config.get('v'),
						"mobileModel": config.get('deviceType'),
						"vendor": config.get('vendor'),
						"connectionType": net,
						"operatorType": '0',
						"imei": config.get('imei'),
						"imsi": "",
						"androidId": config.get('AndroidID'),
						"mac": config.get('mac'),
						"deviceType": '1',
						"osType": '0'
					},
					"networkInfo": {
						"ua": config.get('ua'),
						"ip": config.get('ip'),
						"ipType": '0',
						"httpType": '0'
					},
					"coordinateInfo": {
						"coordinateType": '3',
						"lng": config.get('geo_lng'),
						"lat": config.get('geo_lat'),
						"timestamp": config.get('ts')
					}
				}
	        };
	    },
	    filter: function(data) {
	        if (typeof data == 'string')
	            data = eval("a=" + data);
           
	        var rows = data.ads;
			
	        if (!rows || rows.length === 0)
	            return null;

	        for (var i = 0; i < rows.length; i++) {
	            var row = rows[i];
				
				
				var creativeType = row.materialMetas[0].creativeType;
				// 只要图文广告,右上角和站点有区别
				if( creativeType != 3 && creativeType != 2  ){
					continue;
				}
				
				var interactionType = row.materialMetas[0].interactionType;
				
				if( interactionType == 3 || interactionType == 4 || interactionType == 5 || interactionType == 100  ){
					continue;
				}
				
				var index = row.materialMetas[0].index;
				
				var traceArgs = row.adTracking;
				
				var unfoldMonitorLink = '';
				var clickMonitorLink = '';
				
				for(  var j = 0; j < traceArgs.length;j++ ){
					var tarceInfo = traceArgs[j];
					if( tarceInfo.materialMetaIndex == index ){
						if( tarceInfo.trackingEventType == 1 ){
							 unfoldMonitorLink = tarceInfo.trackingUrls.join(";");
						}else if( tarceInfo.trackingEventType == 0 ){
							 clickMonitorLink = tarceInfo.trackingUrls.join(";");
						}else if( tarceInfo.trackingEventType == 10000 ){
							 dptrackers = tarceInfo.trackingUrls.join(";");
						}
						
					}
				}
				
				console.log("clickMonitorLink=" + clickMonitorLink);

	            var ad = {
	                provider_id: '14',
	                ad_order: index,
	                adType: interactionType,				//这两个不知道是否有问题	
	                packageName: row.materialMetas[0].packageName,
	                head: row.materialMetas[0].title,
	                subhead: row.materialMetas[0].desc,
	                pic: row.materialMetas[0].imageSrcs[0],
	                brandIcon: row.materialMetas[0].iconSrcs[0],
	                link: row.materialMetas[0].landingUrl,
	                deepLink: row.materialMetas[0].dpUrl,
	                unfoldMonitorLink: unfoldMonitorLink,
	                clickMonitorLink: clickMonitorLink
	            }
				
				console.log("ad=" + ad);
				
	            return ad;
	        }
			
			var url = 'http://atrace.chelaile.net.cn/thirdNodata?aid=api_zm&pid=15';
            
			 Http.get(url, {}, 5000, function() {
                console.log('成功发送过滤掉数据上报埋点:' + url);
             });
			
	        return null;
	    },

	  aid : function () {
	        return 'api_zm_${api_zm_displayType}';
	    },
		
		adStyle : function() {
	      return ${api_zm_aid};
	    }
	}

// sdk taks ===================
// 手机调用sdk

var sdk_gdt = {

    adurl: function() {
        return {
            url: "GDTSDK",
            pos: "banner",
            data: {
                appId: "1106616441",
                placementId: "4060239431859044"
            }
        }
    },

    sdkname: function() {
        return "sdk_gdt";
    },

    filter: function(list) {
        return list[0];
    },

    asEntity: function(ad) {
        return !ad ? null : {
            head: ad.getTitle(),
            subhead: ad.getDesc(),
            pic: ad.getImgUrl()
        }
    },

    aid : function () {
        return 'sdk_gdt';
    }
}


var sdk_baidu = {

    adurl: function() {
        return {
            url: "BaiduSDK",
            pos: "banner",
            data: {
                appId: "",
                placementId: "5826196"
            }
        }
    },

    sdkname: function() {
        return "sdk_baidu";
    },

    asEntity: function(ad) {
        return !ad ? null : {
            head: ad.getTitle(),
            subhead: ad.getDesc(),
            pic: ad.getImageUrl()
        }
    },

    filter: function(list) {
        if (!list || !list[0])
            return null;

        for (var i = 0; i < list.length; i++) {
            var ad = list[i];
            var entity = this.asEntity(ad);
            //if (!testRepeat(entity, 'home-ad', 'baidu'))
                return ad;
        }

        return null;
    },

    aid : function () {
        return 'sdk_baidu';
    }
}

var sdk_toutiao = {

    adurl: function() {
        return {
            url: "TOUTIAOSDK",
            pos: "banner",
            data: {
                appId: "",
                placementId: "900673291"
            }
        }
    },

    sdkname: function() {
        return "sdk_toutiao";
    },

    asEntity: function(ad) {
        return !ad ? null : {
            head: ad.getTitle(),
            subhead: ad.getDescription(),
            pic: ad.getIcon().getImageUrl()
        };
    },

    filter: function(list) {
        return list && list[0];
    },

    aid : function () {
        return 'sdk_toutiao';
    }
}



var sdk_ifly = {

    adurl : function() {
        return {
            url:"IFLYSDK",
            pos:"banner",
            data:{
                appId:"1106616441",
                placementId:"10BE44800806378732740DB5B955800C"
                // placementId:"9040714184494018"
            }
        }
    },

    sdkname : function() {
        return "sdk_ifly";
    },

    hide : function(row) {
        if (row.title.indexOf('抖音') > -1) {
            return true;
        }
        if (row.title.indexOf('西瓜') > -1) {
            return true;
        }
        return false;
    },

    exurls : function(row) {
        return {}
    },

    filter : function(list) {
        return list[0];
    },
	
	asEntity: function(ad) {
        if (ad == null) return null;

        var ret = {};
        ret.head = ad.getTitle();
        ret.subhead = ad.getSubTitle();
        return ret;
    },

    aid : function () {
        return 'sdk_ifly';
    }
}


function ads() {
    return {
      traceInfo : {
		  traceid: '${TRACEID}',
		  pid: '23'
      },
      closeInfo: {
          closePic: '${closePic}'
      },
      urls : {
        exposeUrl:'http://atrace.chelaile.net.cn/exhibit?',
        clickUrl:'http://atrace.chelaile.net.cn/click?',
        closeUrl:'http://atrace.chelaile.net.cn/close?'
      },
        timeouts: ${TIMEOUTS},
        tasks: ${TASKS}
    }
}

module.exports = ads;

console.log('splash loaded');


