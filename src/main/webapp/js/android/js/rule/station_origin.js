// station ad js

env = {
    wifi: true
}


var api_chelaile = {
    sdkname: function() {
        return 'api_chelaile'
    },

    adurl: function() {
        return {
            url: 'https://api.chelaile.net.cn/adpub/adv!getStationAds.action'
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
		if(! row.bannerInfo.tag) {
			row.bannerInfo.tag = {};
		}
		if(! row.adCard) {
			row.adCard = {};
		}
        var ad = {
            provider_id: '1',
            id: row.id,
            adid: row.id,
            targetType: row.targetType,
            link: row.link,
            h5Url: row.h5Url,
            unfoldMonitorLink: row.unfoldMonitorLink,
            clickMonitorLink: row.clickMonitorLink,
            monitorType: row.monitorType,
            openType: row.openType,
            ad_order: 0,
            action: row.action,
            wxMiniProId: row.wxMiniProId,
            wxMiniProPath: row.wxMiniProPath,
            bannerInfo: {
                bannerType:row.bannerInfo.bannerType,
                color: row.bannerInfo.color,
                name: row.bannerInfo.name,
                slogan: row.bannerInfo.slogan,
                sloganColor: row.bannerInfo.sloganColor,
                button:{
                    buttonBG: row.bannerInfo.button.buttonBG,
                    buttonColor: row.bannerInfo.button.buttonColor,
                    buttonPic: row.bannerInfo.button.buttonPic,
                    buttonRim: row.bannerInfo.button.buttonRim,
                    buttonText: row.bannerInfo.button.buttonText
                },
                tag:{
                    tagBG: row.bannerInfo.tag.tagBG,
                    tagPic: row.bannerInfo.tag.tagPic,
                    tagText: row.bannerInfo.tag.tagText
                }
            },
            adCard: {
                    address:row.adCard.address,
                    cardType: row.adCard.cardType,
                    gpsType: row.adCard.gpsType,
                    lat: row.adCard.lat,
                    lng: row.adCard.lng,
                    logo: row.adCard.logo,
                    name: row.adCard.name,
                    phoneNum: row.adCard.phoneNum,
                    tagPic: row.adCard.tagPic,
                    topPic: row.adCard.topPic
            },
            pic: row.pic
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
		  //"udid":'BA8C0E13-F99A-4294-BABA-1489C33E9B6D',
          "ll" : config.get('geo_lng') + ',' + config.get('geo_lat'),
          "lla" : config.get('geo_lac'),
          "llt" : config.get('llt'),
          "llp" : config.get('llp'),
          "wifi" : config.get('wifi'),
          "sc_a" : config.get('screenDensity'), // optional
          // 不同位置需要更改
          "id" : "59856e17db1d73fb3dbcb5af6c1ab10f", // 广告位，不知是什么
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

        //var rows = data.mainimage ? [data] : data;
		var rows = data.clk ? [data] : data;

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
                "adunitid": "2CF5CD0015BC4E926778633B8AD0D9AE", // 广告位
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
                clickMonitorLink: row.click_url.join(";"),
				
				bannerInfo: {
				bannerType:4,
				color: "",
				name: "",
				slogan: row.sub_title,
				sloganColor: "",
				button:{
					buttonBG: "",
					buttonColor: "",
					buttonPic: "https://image3.chelaile.net.cn//3c8d25de6d6b481989e596bad3d42cff#150,88",
					buttonRim: "",
					buttonText: ""
				},
				tag:{
					tagBG: "",
					tagPic: "",
					tagText: ""
				}
			}
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
						"adSlotId": "ZM_AD_4_6232",
						"accessToken": "YWxnOkFFUyB0eXBlOmFjY2Vzc190b2tlbiA=.YXBwX3BhY2thZ2U6Y29tLnlna2ouY2hlbGFpbGUuc3RhbmRhcmQgaXNfd2ViOiBhcHBfaWQ6em1fYXBwXzYyMzAg.LbRwsYKihx-oESlgpPF2jPXMQ0YwNW5AGbEcMzELZ1snmrzVu5NcKF7p7O6Z4jSAeybVU5jhqT3WARdgRFYz6x6XNtlv_p7J0t4hm3-hAvVOobPqla_8mgY3vd0KqIkw976jFwy_9MmOZSWSnya6QAXybxfaRBN0AZR1o9Uryg4"
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
			
				console.log("station=" + data);
			
			if( typeof data != 'Object'  ){
			console.log("object=222");	
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
				
				console.log("title1station=" + title);
				
				if( row.materialMetas[0].title === '' ){
					title = row.materialMetas[0].desc;
				}
				
				console.log("title2station=" + title);
				
				title = '';
				
				
				console.log("titlestation=" + title);
				
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
	               // pic: row.materialMetas[0].imageSrcs[0],
	                brandIcon: row.materialMetas[0].iconSrcs[0],
	                link: row.materialMetas[0].landingUrl,
	                deepLink: row.materialMetas[0].dpUrl,
	                unfoldMonitorLink: unfoldMonitorLink,
	                clickMonitorLink: clickMonitorLink,
					
				bannerInfo: {
					bannerType:4,
					color: "",
					name: "",
					slogan: title,
					sloganColor: "",
					button:{
						buttonBG: "",
						buttonColor: "",
						buttonPic: "https://image3.chelaile.net.cn//3c8d25de6d6b481989e596bad3d42cff#150,88",
						buttonRim: "",
						buttonText: ""
						},
					tag:{
						tagBG: "",
						tagPic: "",
						tagText: ""
						}
					}
					
	            }
				
			
				
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
                placementId: "6000631364333392"
            }
        }
    },

    sdkname: function() {
        return "sdk_gdt";
    },

    filter: function(list) {
        return list[0];
    },
	
		        banner : function () {
				return  {
                                bannerType: 4,
                                color: "",
                                name: "",
                                slogan: "",
                                sloganColor: "",
                                button:{
                                        buttonBG: "",
                                        buttonColor: "",
                                        buttonPic: "https://image3.chelaile.net.cn//3c8d25de6d6b481989e596bad3d42cff#150,88",
                                        buttonRim: "",
                                        buttonText: ""
                                },
                                tag:{
                                        tagBG: "",
                                        tagPic: "",
                                        tagText: ""
                                }
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
                placementId: "5826175"
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
            // (!testRepeat(entity, 'home-ad', 'baidu'))
                return ad;
        }

        return null;
    },
	
	        banner : function () {
				return  {
                                bannerType: 4,
                                color: "",
                                name: "",
                                slogan: "",
                                sloganColor: "",
                                button:{
                                        buttonBG: "",
                                        buttonColor: "",
                                        buttonPic: "https://image3.chelaile.net.cn//3c8d25de6d6b481989e596bad3d42cff#150,88",
                                        buttonRim: "",
                                        buttonText: ""
                                },
                                tag:{
                                        tagBG: "",
                                        tagPic: "",
                                        tagText: ""
                                }
                        }
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
                placementId: "900673616"
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
	
		        banner : function () {
				return  {
                                bannerType: 4,
                                color: "",
                                name: "",
                                slogan: "",
                                sloganColor: "",
                                button:{
                                        buttonBG: "",
                                        buttonColor: "",
                                        buttonPic: "https://image3.chelaile.net.cn//3c8d25de6d6b481989e596bad3d42cff#150,88",
                                        buttonRim: "",
                                        buttonText: ""
                                },
                                tag:{
                                        tagBG: "",
                                        tagPic: "",
                                        tagText: ""
                                }
                        }
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
                placementId:"5CBF4E804C06EBF6EEAF93DC5EA6BBCF"
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
	
	
		        banner : function () {
				return  {
                                bannerType: 4,
                                color: "",
                                name: "",
                                slogan: "",
                                sloganColor: "",
                                button:{
                                        buttonBG: "",
                                        buttonColor: "",
                                        buttonPic: "https://image3.chelaile.net.cn//3c8d25de6d6b481989e596bad3d42cff#150,88",
                                        buttonRim: "",
                                        buttonText: ""
                                },
                                tag:{
                                        tagBG: "",
                                        tagPic: "",
                                        tagText: ""
                                }
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
        return 'sdk_ifly';
    }
}


function ads() {
    return {
      traceInfo : {
		  traceid: '${TRACEID}',
		  pid: '15'
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


