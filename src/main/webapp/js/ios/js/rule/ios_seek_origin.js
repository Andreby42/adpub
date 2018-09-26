


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
            url: 'https://api.chelaile.net.cn/adpub/adv!getSeekAds.action',
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


var api_shunfei = {

	sdkname: function() {
	        return "api_chelaile";
	},

	adurl_ios: function() {
	    //var config = JsFixedConfig.getJsFixedConfig();
        var configInfoString = GetDeviceInfo();
        var configKVArray = configInfoString.split('&');
        var deviceInfo = {};
        configKVArray.forEach(function(itemString) {
            var itemArr = itemString.split('=');
            deviceInfo[itemArr[0]] = decodeURIComponent(itemArr[1]);
        });

        console.log('deviceInfo=' + JSON.stringify(deviceInfo));

	    //console.log("parseInt(deviceInfo.dct || '')=" + parseInt(deviceInfo.dct || ''));
	        var geolng = deviceInfo.geo_lng || '' ;
	        var geolat = deviceInfo.geo_lat || '';
	        var ts = (+new Date) + '';
	    
		
			ts = String(ts).slice(0,-3);
			 
            console.log("ts=" + ts);
				
			
	        var sv1 = deviceInfo.sv || '' + "";
			var sv = sv1.split(".");
	        var micro = 0;
	        if( sv.length == 3 ){
	        	micro = sv[2];
	        }
            
	        var net = deviceInfo.nw || '0G'; // network
            if (net=='WIFI') {
                net = 1;
            } else {
                net = parseInt(net.substring(0,1))
            }
			
			var sign = JsEncryptUtil.md5('177'+'zDczEwi)+(e1)6^YB)(s*WdPZy*Y0H6w'+ts)+'';
	        var ret = {
                type:"banner",
	            url: 'http://i-mbv.biddingx.com/api/v1/bid',
	            data: {
                    ad_data:"AsyncPostData",
                    dataFormater:this.dataFormater,
	            	 postData: {
                     
                     "ip": '',
	            	 "user_agent": deviceInfo.userAgent || ''+'',
	            	 "detected_time": parseInt(ts),
	            	 "time_zone": "+0800",
					 "detected_language": "en_",
					 
					 "geo": {
						"latitude":parseFloat(deviceInfo.geo_lat || ''+''), 
						"longitude":parseFloat(deviceInfo.geo_lng || ''+'') 
						},
	            	 
	            	 "mobile": {
	            		 "device_id": '',
	            		 "device_type":1,
	            		 "platform":1,
	            		 "os_version": {
	            			 "os_version_major": parseInt(sv[0]),
	            			 "os_version_minor": parseInt(sv[1]),
                             "os_version_micro": parseInt(micro)	 
	            			 },
	            		 
						 "brand":deviceInfo.vendor || ''+'',
						 "model":deviceInfo.deviceType || ''+'',
						 
	        	         "screen_width":parseInt(deviceInfo.screenWidth || ''+''),
	        	         "screen_height": parseInt(deviceInfo.screenHeight || ''+''),
	        	         "wireless_network_type":parseInt(net),
	        	         "for_advertising_id":deviceInfo.idfa || ''+'',
	        	         "mobile_app": {
	        	        	 "app_id":970,
	        	        	 "sign":sign,
	        	        	 "app_bundle_id":'com.chelaile.lite',
							 "first_launch": false
	        	         }
                      },
	            		 
                        "adslot":[
	            			 {
	            				 "ad_block_key":1986,
	            				 "adslot_type":17,
	            				 "width":179,
	            			     "height":92
	            			 }
                        ],
	            	 
	            	 "api_version":"1.6",
	            	 "is_test":false,
	          
                    }
                }
	        };
			
            return ret;
    },

    dataFormater : {
        parse:function(data) {
            if('AsyncPostData' == data) {
                return [{"AsyncPostData":data}];
            }
            else {
                console.log('****' + JSON.stringify(data))
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
	            return [ad];
	        }
	        return null;
            }
        }
    },

    filter_ios: function(list) {
        return list;
    },

    aid : function () {
        return 'api_shunfei_2';
    },

    adStyle : function() {
        return "2";
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
          pid: '32',
          jsid: '${JSID}'
      },
      closeInfo: {
          closePic: '${closePic}',
          hostSpotSize: '${hostSpotSize}',
          fakeRate: '${fakeRate}'
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

