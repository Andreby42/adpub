// home ad js

// home ad js

env = {
    wifi: true
}


var api_chelaile = {
    sdkname: function() {
        return 'api_chelaile'
    },

    adurl: function() {
        return {
            url: 'https://api.chelaile.net.cn/adpub/adv!geColumntAds.action'
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
            link: row.link,
            unfoldMonitorLink: row.unfoldMonitorLink,
            clickMonitorLink: row.clickMonitorLink,
			monitorType: row.monitorType,
            openType: row.openType,
            ad_order: 0,
            pic: row.buttonIcon,
			barColor : row.barColor,
			buttonColor: row.buttonColor,
			brandIcon : row.brandIcon,
			brandName : row.brandName,
			buttonIcon :row.buttonIcon,
			buttonType : row.buttonType,
			head : row.head,
			subhead : row.subhead,
			targetType : row.targetType,
			type : row.targetType,
			sindex :row.sindex,
			picsList: row.picsList,
            wxMiniProId: row.wxMiniProId,
            wxMiniProPath: row.wxMiniProPath,
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
        var config = JsFixedConfig.getJsFixedConfig();
        var params = {
          "av" : config.get('v').split('_')[0],
          "ct" : config.get('ct'),
          "dct" : config.get('dct'),
          "udid" : config.get('udid').toUpperCase(),
		  //"udid":'BA8C0E13-F99A-4294-BABA-1489C33E9B6D',
          "ll" : config.get('geo_lng') + ',' + config.get('geo_lat'),
          "lla" : config.get('geo_lac'),
          "llt" : config.get('llt'),
          "llp" : config.get('llp'),
          "wifi" : config.get('wifi'),
          "sc_a" : config.get('screenDensity'), // optional
          "id" : "7a7b059ca39624f1c1ec24fa2ad375f6", 
          "ran" : "1", 
        }
        var str = 'http://gorgon.youdao.com/gorgon/request.s?';
        for (var p in params) {
          str = str + p + '=' + params[p] + "&";
        }
        str = str.substring(0, str.length - 1);
        return {
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

var api_voicead = {

    sdkname: function() {
        return "api_voicead";
    },

    adurl: function() {
        var config = JsFixedConfig.getJsFixedConfig();

        var net = parseInt(config.get('dct'));
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
                "is_support_deeplink": "1", 
                "secure": "3",
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
                "orientation": "0", 
                "vendor": config.get('vendor'),
                "model": config.get('model'),
                "lan": config.get('lan'),
                "batch_cnt": "1",
                "appid": "5add7ce1",
                "appname": "车来了",
                "appver": config.get('v').split('_')[0],
                "pkgname": "com.ygkj.chelaile.standard",
                "debug": {
                    "action_type": "0",
                    "landing_type": "0"
                },
                "adunitid": "${api_voicead_placementId}", 
                "adw": "359",
                "adh": "92",
                "isboot": "0",
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






var api_shunfei = {

	    sdkname: function() {
	        return "api_shunfei";
	    },

	    adurl: function() {
	        var config = JsFixedConfig.getJsFixedConfig();

			
			console.log("parseInt(config.get('dct'))=" + parseInt(config.get('dct')));
	        var geolng = config.get('geo_lng') ;
	        var geolat = config.get('geo_lat');
	        var ts = config.get('ts');
	    
		
			ts = String(ts).slice(0,-3);
			 
			 console.log("ts=" + ts);
				
			
	        var sv1 = config.get('sv') + "";
			var sv = sv1.split(".");
		
			
	        var micro = 0;
	        
	        if( sv.length == 3 ){
	        	micro = sv[2];
	        }
			

	        var net = parseInt(config.get('dct'));
	        if (net >= 11 && net <= 13) {
	          net = net - 9;
	        } else {
	          net = 1;
	        }
			
			
	        
			
			var sign = JsEncryptUtil.md5('177'+'g@^6*1n@E7IX#)SuJ6SE$#BQ8rV*)O8y'+ts)+'';
	        
	        var ret = {
	            url: 'http://i-mbv.biddingx.com/api/v1/bid',
	            data: {
	            	 "ip": config.get('ip')+'',
	            	 "user_agent": config.get('ua')+'',
	            	 "detected_time": parseInt(ts),
	            	 "time_zone": "+0800",
					 "detected_language": "en_",
					 
					 "geo": {
						"latitude":parseFloat(config.get('geo_lat')+''), 
						"longitude":parseFloat(config.get('geo_lng')+'') 
						},
	            	 
	            	 "mobile": {
	            		 "device_id":config.get('mac')+'',
	            		 "device_type":1,
	            		 "platform":2,
	            		 "os_version": {
	            			 "os_version_major": parseInt(sv[0]),
	            			 "os_version_minor": parseInt(sv[1]),
                             "os_version_micro": parseInt(micro)	 
	            			 },
	            		 
						 "brand":config.get('vendor')+'',
						 "model":config.get('deviceType')+'',
						 
	        	         "screen_width":parseInt(config.get('screenWidth')+''),
	        	         "screen_height": parseInt(config.get('screenHeight')+''),
	        	         "wireless_network_type":parseInt(net),
	        	         "for_advertising_id":config.get('imei')+'',
	        	         "android_id":config.get('AndroidID')+'',
	        	         "mobile_app": {
	        	        	 "app_id":969,
	        	        	 "sign":sign,
	        	        	 "app_bundle_id":'com.ygkj.chelaile.standard',
							 "first_launch":eval(config.get('firstLaunch')+'')
	        	         }
	            	 },
	            		 
	            	"adslot":[
	            			 {
	            				 "ad_block_key":1984,
	            				 "adslot_type":17,
	            				 "width":179,
	            			     "height":92
	            			 }
	            	],
	            	 
	            	 "api_version":"1.6",
	            	 "is_test":false,
	          
	            }
	        };
			
			var s = JSON.stringify(ret);
            var j = JSON.parse(s);
            //console.log("******** str " + s)
            //console.log("******** json " + j)
            return j;
			
	    },
	    filter: function(data) {
	        if (typeof data == 'string')
	            data = eval("a=" + data);

	        var rows = data.ads;
	        if (!rows || rows.length === 0)
	            return null;

	        for (var i = 0; i < rows.length; i++) {
	            var row = rows[i];

				var click_type = parseInt(row.click_type);
				 
				if( click_type == 2 ){
					click_type = 0;	
				}else{
					click_type = 1;	
				}
				
	            var ad = {
	                provider_id: '13',
	                ad_order: i,
	                adType: click_type,
	                head: row.title,
	                subhead: row.desc,
	                pic: row.imgs[0],
	                brandIcon: row.logo_url,
	                link: row.click_url.join(";"),
	                deepLink: row.deep_url,
	                unfoldMonitorLink: row.exposure.join(";"),
					actionMonitorLink: row.action_url.join(";"),
	                clickMonitorLink: row.click.join(";"),
					picsList: row.imgs
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




var api_zm = {

	    sdkname: function() {
	        return "api_zm";
	    },

	    adurl: function() {
	        var config = JsFixedConfig.getJsFixedConfig();

	        var net = parseInt(config.get('dct'));
	        if (net >= 11 && net <= 13) {
	          net = net - 9;
	        } else {
	          net = 1;
	        }
	        
	        return {
	            url: 'http://adalliance.zmeng123.com/zmtmobads/v4/getAd.do',
	            data: {
					"reqInfo": {
						"adSlotId": "ZM_AD_4_6231",
						"accessToken": "YWxnOkFFUyB0eXBlOmFjY2Vzc190b2tlbiA=.YXBwX3BhY2thZ2U6Y29tLnlna2ouY2hlbGFpbGUuc3RhbmRhcmQgaXNfd2ViOiBhcHBfaWQ6em1fYXBwXzYyMzAg.LbRwsYKihx-oESlgpPF2jPXMQ0YwNW5AGbEcMzELZ1snmrzVu5NcKF7p7O6Z4jSAeybVU5jhqT3WARdgRFYz6x6XNtlv_p7J0t4hm3-hAvVOobPqla_8mgY3vd0KqIkw976jFwy_9MmOZSWSnya6QAXybxfaRBN0AZR1o9Uryg4"
					},
					"adSlotInfo": {
						"mimes": "jpg,gif,icon,png,",
						"slotWidth": 359,
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
			
			console.log("home=" + data);	
			
			if( typeof data != 'Object'  ){
				console.log("object=111");	
			//	return null;
			}
			
			
			
			
	        if (typeof data == 'string')
	            data = eval("a=" + data);
           
	        var rows = data.ads;
			
	        if (!rows || rows.length === 0)
	            return null;

	        for (var i = 0; i < rows.length; i++) {
	            var row = rows[i];
				
				console.log("creativeType=" + row.materialMetas[0].creativeType);
				
				console.log("interactionType=" + row.materialMetas[0].interactionType);
				
				var creativeType = row.materialMetas[0].creativeType;
				if( creativeType != 3 ){
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
				
				if( row.materialMetas[0].title == '' ){
					title = row.materialMetas[0].desc;
				}
				
				if( title == '' ){
					continue;
				}
				
				var desc = row.materialMetas[0].desc;
				
				if( desc == title ){
				
					//desc = '';
				}

	            var ad = {
	                provider_id: '14',
	                ad_order: index,
	                adType: interactionType,
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
			
			var url = 'http://atrace.chelaile.net.cn/thirdNodata?aid=api_zm&pid=00';
            
			 Http.get(url, {}, 5000, function() {
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


var sdk_gdt = {

    adurl: function() {
        return {
            url: "GDTSDK",
            pos: "banner",
            data: {
                appId: "1104972974",
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
            //head: ad.getTitle(),
            //subhead: ad.getDesc(),
            //pic: ad.getImgUrl()
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
                appId: "ae469914",
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
            //if (!testRepeat(entity, 'home-ad', 'baidu'))
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
                appId: "5000673",
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
                placementId:"${sdk_ifly_placementId}"
                // placementId:"9040714184494018"
            }
        }
    },

    sdkname : function() {
        return "sdk_ifly";
    },

    hide : function(row) {
        return false;
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
	
	asEntity: function(ad) {
        if (ad == null) return null;

        var ret = {};
        ret.head = ad.getTitle();
        ret.subhead = ad.getSubTitle();
        return ret;
    },

    aid : function () {
        return 'sdk_ifly_${sdk_ifly_aid}';
    },
	
	adStyle : function() {
      return ${sdk_ifly_displayType};
    }
}

var sdk_adview = {

    adurl : function() {
        return {
            url:"ADVIEWSDK",
            pos:"banner",
            data:{
                appId:"SDK201817090508490a6u8s7z5m4aoqf",
                placementId:"${sdk_adview_placementId}"
                // placementId:"9040714184494018"
            }
        }
    },

    sdkname : function() {
        return "sdk_adview";
    },

    hide : function(row) {
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
      return true;
    },

    aid : function () {
        return 'sdk_adview_${sdk_adview_aid}';
    },
	
	adStyle : function() {
     return ${sdk_adview_displayType};
    }
}

function ads() {
    return {
      traceInfo : {
		  traceid: '${TRACEID}',
		  pid: '00',
          s: 'android'
      },
      closeInfo: {
          closePic: '${closePic}',
            hostSpotSize: '${hostSpotSize}',
            fakeRate: '${fakeRate}'
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


