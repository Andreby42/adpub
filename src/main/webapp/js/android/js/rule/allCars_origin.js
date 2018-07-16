// bottom ad js

env = {
    wifi: true
}


var api_chelaile = {
    sdkname: function() {
        return 'api_chelaile'
    },

    adurl: function() {
        return {
            url: 'https://api.chelaile.net.cn/adpub/adv!getAllCar.action?${QUERY_STRING}'
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
            provider_id: '1',
			id: row.id,
			adid: row.id,
			head: row.head,
			subhead: row.subhead,
			imgsType: row.imgsType,
			targetType: row.targetType,
            link: row.link,
            unfoldMonitorLink: row.unfoldMonitorLink,
            clickMonitorLink: row.clickMonitorLink,
			monitorType: row.monitorType,
            openType: row.openType,
            ad_order: 0,
			action: row.action,
            pic: row.pic,
			picsList: row.picsList,
			adStyle: row.displayType
        }

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
          "udid" : config.get('udid').toUpperCase(),
		  // "udid":'BA8C0E13-F99A-4294-BABA-1489C33E9B6D',
          "ll" : config.get('geo_lng') + ',' + config.get('geo_lat'),
          "lla" : config.get('geo_lac'),
          "llt" : config.get('llt'),
          "llp" : config.get('llp'),
          "wifi" : config.get('wifi'),
          "sc_a" : config.get('screenDensity'), // optional
          // 不同位置需要更改
          "id" : "0d581bbc39eb35abdbcd5e2c1db1b2bf", // 广告位，不知是什么
          "ran" : "1", // optional 一次请求的广告数量，默认值为 1
        }
        var str = 'http://gorgon.youdao.com/gorgon/request.s?';
        for (var p in params) {
          str = str + p + '=' + params[p] + "&";
        }
        str = str.substring(0, str.length - 1);
        return {
            // url:
			// 'http://gorgon.youdao.com/gorgon/request.s?id=e3f49841bbd3ceb0c6a531ca32f4a754&udid=BA8C0E13-F99A-4294-BABA-1489C33E9B6D&imei=BA8C0E13-F99A-4294-BABA-1489C33E9B6D&lla=73.0&llp=p&wifi=&rip=10.168.0.10&imeimd5=305612168A059FC9CCDAC8D95D99E485&ct=2&dct=0&ll=116.403538,39.994026&auidmd5=305612168A059FC9CCDAC8D95D99E485&av=5.50.0&llt=1'
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
        return 'api_yd_${api_yd_aid}';
    },
	
	adStyle : function() {
      return ${api_yd_displayType};
    }

}



var api_shunfei = {

	    sdkname: function() {
	        return "api_shunfei";
	    },

	    adurl: function() {
	        var config = JsFixedConfig.getJsFixedConfig();

	        var net = parseInt(config.get('dct')); // 有道用dct
	        if (net >= 11 && net <= 13) {
	          net = net - 7;
	        } else {
	          net = config.get('ct');
	        }
	        var geolng = config.get('geo_lng') ;
	        var geolat = config.get('geo_lat');
	        var ts = config.get('ts');
	        ts = ts / 1000;
	        var sv = config.get('sv').split('.');
	        var micro = 0;
	        
	        if( sv.length == 3 ){
	        	micro = sv[2];
	        }

	        var net = parseInt(config.get('dct')); // 有道用dct
	        if (net >= 11 && net <= 13) {
	          net = net - 7;
	        } else {
	          net = config.get('ct');
	        }
	        
	        
	        return {
	            url: 'http://hostname:port/api/v1/bid',
	            data: {
	            	 "ip": config.get('ip'),
	            	 "user_agent": config.get('ua'),
	            	 "detected_time": ts,
	            	 "time_zone": "+0800",
	            	 "geo": {
	            		 "latitude": geolng, 
	            		 "longitude": geolat  
	            		 },
	            	 
	            	 "mobile": {
	            		 "device_id":config.get('mac'),
	            		 "device_type":1,
	            		 "platform":2,
	            		 "os_version": {
	            			 "os_version_major": sv[0],
	            			 "os_version_minor": sv[1],
                             "os_version_micro": micro	 
	            			 },
	            		 "brand": config.get('vendor'),
	        	         "model": config.get('model'),	
	        	         "screen_width":config.get('screenWidth'),
	        	         "screen_height": config.get('screenHeight'),
	        	         "wireless_network_type":net,
	        	         "for_advertising_id":config.get('imei'),
	        	         "android_id":config.get('AndroidID'),
	        	         "mobile_app": {
	        	         }
	            	 },
	            		 
	            	"adslot":[
	            			 {
	            				 "width":179,
	            			     "height":88
	            			 }
	            	],
	            	 
	            	 "api_version":"1.6.1",
	            	 "is_test":true,
	          
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
	                clickMonitorLink: row.click_url.join(";"),
					picsList: row.img_urls
	            }
	            return ad;
	        }
	        return null;
	    },

	  aid : function () {
	        return 'api_shunfei_${api_shunfei_displayType}';
	    },
		
		adStyle : function() {
	      return ${api_shunfei_aid};
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
              // "geo": config.get('geo_lng') + ',' + config.get('geo_lat'),
				// // optional，用了还报错
                "batch_cnt": "1", // 广告数量，只支持1
                "appid": "5add7ce1",
                "appname": "车来了",
                "appver": config.get('v').split('_')[0],
                "pkgname": "com.ygkj.chelaile.standard",
                "debug": { // optional
                    /*
					 * 用于指定下发广告的交互类型，取值范围：0，不限制；1，跳转类；
					 * 2，下载类；3，特殊下载类。默认0。当前下载类广告暂不支持 deep link，为2 时下个值不能为1
					 */
                    "action_type": "0",
                    /*
					 * 用于指定下发广告的落地页类型，取值范围：0，不限制；1，包含 landing_url 和 deep_link；
					 * 2，仅包含 landing_url。不指定 的话，按值为 0 处理。
					 */
                    "landing_type": "0"
                },
                // 不同位置需要更改
                 "adunitid": "${api_voicead_placementId}", // 广告位
                 "adw": config.get('screenWidth'), // 广告图宽，看后台申请广告位的尺寸
                "adh": "92",
                "isboot": "0", // 1表示开屏；0表示非开屏
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
                clickMonitorLink: row.click_url.join(";"),
				picsList: row.img_urls
            }
            return ad;
        }
        return null;
    },

  aid : function () {
        return 'api_voicead_${api_voicead_displayType}';
    },
	
	adStyle : function() {
      return ${api_voicead_aid};
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
	            url: 'http://123.56.176.83:10091/durer/zmtmobads/v4/getAd.do',
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
			
			if( typeof data != 'Object'  ){
				console.log("object=111");	
			}
			
	        if (typeof data == 'string')
	            data = eval("a=" + data);
           
	        var rows = data.ads;
			
	        if (!rows || rows.length === 0)
	            return null;

	        for (var i = 0; i < rows.length; i++) {
	            var row = rows[i];
				
				
				
				var creativeType = row.materialMetas[0].creativeType;
				// 只要图文广告,右上角和站点有区别
				if( creativeType != 3   ){
				//	continue;
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
				
				var title = row.materialMetas[0].title;
				
				console.log("title1=" + title);
				
				if( row.materialMetas[0].title === '' ){
					title = row.materialMetas[0].desc;
				}
				
				console.log("title2=" + title);
				
				if( title === '' ){
					continue;
				}
				
				
				var desc = row.materialMetas[0].desc;
				
				if( desc == title ){
				
					//desc = '';
				}
					

	            var ad = {
	                provider_id: '14',
	                ad_order: index,
	                adType: interactionType,				//这两个不知道是否有问题	
	                packageName: row.materialMetas[0].packageName,
	                head: title,
	                subhead: desc,
	                pic: row.materialMetas[0].imageSrcs[0],
	                brandIcon: row.materialMetas[0].iconSrcs[0],
	                link: row.materialMetas[0].landingUrl,
	                deepLink: row.materialMetas[0].dpUrl,
	                unfoldMonitorLink: unfoldMonitorLink,
	                clickMonitorLink: clickMonitorLink
	            }
				
			
				
	            return ad;
	        }
			
			var url = 'http://atrace.chelaile.net.cn/thirdNodata?aid=api_zm&pid=26';
            
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
               placementId: "${sdk_gdt_placementId}"
            }
        }
    },

    sdkname: function() {
        return "sdk_gdt";
    },

    filter: function(list) {
        var ad = list[0];
        if (this.adCheck(ad)) {
          return ad;
        } else {
          return null;
        }
    },
    adCheck : function(ad) {
      if (this.adStyle() == 3){
        console.log("ad img list: " + ad.getImgList());
        return (ad.getImgList() != null && ad.getImgList().size() == 3);
      } else {
        console.log("ad img: " + ad.getImgUrl());
        return true;
      }
    },

    asEntity: function(ad) {
        return !ad ? null : {
            head: ad.getTitle(),
            subhead: ad.getDesc(),
            pic: ad.getImgUrl()
        }
    },

    aid : function () {
        return 'sdk_gdt_${sdk_gdt_aid}';
    },
	
	adStyle : function() {
     return ${sdk_gdt_displayType};
    }
}


var sdk_baidu = {

    adurl: function() {
        return {
            url: "BaiduSDK",
            pos: "banner",
            data: {
                appId: "",
               placementId: "${sdk_baidu_placementId}"
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
            // if (!testRepeat(entity, 'home-ad', 'baidu'))
            if (this.adCheck(ad)) {
              return ad;
            } else {
              return null;
            }
        }
        return null;
    },
	
    adCheck : function(ad) {
      if (this.adStyle() == 3){
        console.log("ad img list: " + ad.getMultiPicUrls());
        return (ad.getMultiPicUrls() != null && ad.getMultiPicUrls().size() == 3);
      } else {
        console.log("ad img: " + ad.getImageUrl());
        return true;
      }
    },

    aid : function () {
        return 'sdk_baidu_${sdk_baidu_aid}';
    },
	
	adStyle : function() {
     return ${sdk_baidu_displayType};
    }
}

var sdk_toutiao = {

    adurl: function() {
        return {
            url: "TOUTIAOSDK",
            pos: "banner",
            data: {
                appId: "",
                placementId: "${sdk_toutiao_placementId}"
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
        var ad = list && list[0];
        if (!ad) return null;
        
        if (this.adCheck(ad)) {
          return ad;
        } else {
          return null;
        }
    },
    adCheck : function(ad) {
      if (this.adStyle() == 3){
        console.log("ad img list: " + ad.getImageList());
        return (ad.getImageList() != null && ad.getImageList().size() == 3);
      } else {
        return true;
      }
    },

    aid : function () {
        return 'sdk_toutiao_${sdk_toutiao_aid}';
    },
	
	adStyle : function() {
     return ${sdk_toutiao_displayType};
    }
}



var sdk_ifly = {

    adurl : function() {
        return {
            url:"IFLYSDK",
            pos:"banner",
            data:{
                appId:"1106616441",
                 placementId:"${sdk_voicead_placementId}"
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
	
	asEntity: function(ad) {
        if (ad == null) return null;

        var ret = {};
        ret.head = ad.getTitle();
        ret.subhead = ad.getSubTitle();
        return ret;
    },

    exurls : function(row) {
        return {}
    },
   
    filter: function(list) {
		var ad = list && list[0];
        if (!ad) return null;
        
        if (this.adCheck(ad)) {
          return ad;
        } else {
          return null;
        }
    },
    adCheck : function(ad) {
      if (this.adStyle() == 3){
        console.log("ad img list: " + ad.getImgUrls());
        return (ad.getImgUrls() != null && ad.getImgUrls().size() == 3);
      } else {
        return true;
      }
    },

    aid : function () {
        return 'sdk_ifly_${sdk_ifly_aid}';
    },
	
	adStyle : function() {
     return ${sdk_voicead_displayType};
    }
}



function ads() {
    return {
      traceInfo : {
		  traceid: '${TRACEID}',
		  pid: '26'
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


