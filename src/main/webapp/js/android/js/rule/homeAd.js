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
            url: 'http://dev.chelaile.net.cn/adpub/adv!getCoopenAds.action?last_src=app_360_sj&s=ios&push_open=1&userId=unknown&geo_lt=6&idfa=&geo_lat=22.8994&ol=e63166503869e58e344bb28edc630e35318118cc&vc=105&sv=7.1.1&v=5.50.1&startMode=0&imei=867977033452765&udid=0a47fad2-59c9-48ea-a01f-0e952e36a117111&type=0&cityId=027&sign=wM%2FOYSfqDhH4rk62aieVLg%3D%3D&mac=02%3A00%3A00%3A00%3A00%3A00&deviceType=12+MAX+2&wifi_open=0&geo_type=gcj&lchsrc=icon&nw=MOBILE_LTE&AndroidID=4deac64641b12eb6&geo_lac=550.0&language=1&first_src=app_baidu_as&userAgent=Mozilla%2F5.0+%28Linux%3B+Android+7.1.1%3B+MI+MAX+2+Build%2FNMF26F%3B+wv%29+AppleWebKit%2F537.36+%28KHTML%2C+like+Gecko%29+Version%2F4.0+Chrome%2F64.0.3282.137+Mobile+Safari%2F537.36&geo_lng=112.886266&remote_addr=223.104.63.159&x_forwarded_for=&requestid=1527574692647619ad176ee25caebf8a'
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
        var row = rows[0];
        var ad = {
            provider_id: '1',
            link: row.link,
            unfoldMonitorLink: row.unfoldMonitorLink,
            clickMonitorLink: row.clickMonitorLink,
            openType: row.openType,
            ad_order: 0,
            pic: row.pic
        }
        return ad;
    },

    aid : function () {
        return 'api_chelaile';
    }
}

var api_yd = {
    sdkname: function() {
        return 'api_yd'
    },

    adurl: function() {
        return {
            url: 'http://gorgon.youdao.com/gorgon/request.s?id=e3f49841bbd3ceb0c6a531ca32f4a754&udid=BA8C0E13-F99A-4294-BABA-1489C33E9B6D&imei=BA8C0E13-F99A-4294-BABA-1489C33E9B6D&lla=73.0&llp=p&wifi=&rip=10.168.0.10&imeimd5=305612168A059FC9CCDAC8D95D99E485&ct=2&dct=0&ll=116.403538,39.994026&auidmd5=305612168A059FC9CCDAC8D95D99E485&av=5.50.0&llt=1'

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
                provider_id: '11',
                link: row.clk,
                unfoldMonitorLink: row.imptracker.join(";"),
                clickMonitorLink: row.clktrackers.join(";"),
                deeplink: row.deeplink,
                dptrackers: row.dptrackers,
                adType: row.ydAdType,
                // styleName: row.styleName,
                brandIcon: row.iconimage,
                pic: row.mainimage,
                head: row.title,
                ad_order: i,
                subhead: row.text,
                packageName: row.packageName
            }
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
        return {
            url: 'http://ws.voiceads.cn/ad/request',
            data: {
                "debug": {
                    "action_type": 2,
                    "landing_ty": 0
                },
                "idfa": "BA8C0E13-F99A-4294-BABA-1489C33E9B6D",
                "idfv": "92669482-B539-4E4C-BCE3-92829225F5BB",
                "ua": "Mozilla/5.0%20(iPhone;%20CPU%20iPhone%20OS%2010_3_3%20like%20Mac%20OS%20X)%20AppleWebKit/603.3.3%20(KHTML,%20like%20Gecko)%20Mobile/14G5037b",
                "net": "0",
                "ip": "10.168.0.10",
                "tramaterialtype": "json",
                "isboot": "1",
                "batch_cnt": "1",
                "density": "2.000000",
                "operator": "46000",
                "lan": "zh-CN",
                "dvh": "480",
                "geo": "116.403538,39.994026",
                "pkgname": "com.chelaile.lite",
                "adunitid": "2D8857EE0D286E80203F7334F8356B1C",
                "adw": "640",
                "orientation": "0",
                "dvw": "320.000000",
                "osv": "10.3.3",
                "ts": "1527056043",
                "appid": "5acf1d60",
                "appname": "车来了",
                "os": "iOS",
                "openudid": "d41d8cd98f00b204e9800998ecf8427e089ec208",
                "devicetype": "1",
                "vendor": "apple",
                "appver": "5.50.0",
                "api_ver": "1.3.8",
                "adh": "960",
                "secure": 0,
                "model": "iPhone5c"
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
                adType: row.adType,
                downloadType: row.download_type,
                packageName: row.package_name,
                head: row.title,
                subhead: row.sub_title,
                pic: row.image,
                ad_order: i,
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

// sdk taks ===================
// 手机调用sdk

var sdk_gdt = {

    adurl: function() {
        return {
            url: "GDTSDK",
            pos: "banner",
            data: {
                appId: "1106616441",
                placementId: "9040714184494018"
            }
        }
    },

    sdkname: function() {
        return "sdk_gdt";
    },

    filter: function(list) {
        return list;
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
                placementId: "5826174"
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
            if (!testRepeat(entity, 'home-ad', 'baidu'))
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
                placementId: "900673326"
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
            pic: ad.getImageUrl()
        };
    },

    filter: function(list) {
        return list && list[0];
    },

    aid : function () {
        return 'sdk_toutiao';
    }
}

var sdk_voicead = {

    adurl: function() {
        return {
            url: "IFLYSDK",
            pos: "splash",
            data: {
                appId: "",
                placementId: "D028C0ADDDBC38952DA01241B4939E64"
            }
        }
    },

    sdkname: function() {
        return "sdk_voicead";
    },

    asEntity: function(ad) {
        return !ad ? null : {
            head: ad.getTitle(),
            subhead: ad.getSubTitle(),
            pic: ad.getImage()
        }
    },

    filter: function(ad) {
        return ad;
    },

    aid : function () {
        return 'sdk_voicead';
    }
}

function ads() {
    return {
        traceInfo: {
            ip: '192.168.100.100'
        },
        urls: {
            exposeUrl: 'http://atrace.chelaile.net.cn/exhibit',
            clickUrl: 'http://atrace.chelaile.net.cn/click',
            closeUrl: 'http://atrace.chelaile.net.cn/close'
        },
        timeouts: [1000, 2000],
        tasks: [
            [sdk_gdt], [api_yd]
        ]
    }
}

module.exports = ads;

console.log('splash loaded');
