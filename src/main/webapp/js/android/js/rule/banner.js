// 手机处理任务
// api task ===============
// 根据taskname找到js fun，获取download url


LTAG = 'chelaile';

env = {
    wifi: true
}

function api_exurls(standardRow) {
    return {
        click: 'http://163.com/click',
        exhibit: 'exhibit',
        response: '',
        close: 'close'
    };
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
        var ad = eval('a=' + data);
        return ad;
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
        console.log('api_voicead get data:' + data)

        if (typeof data == 'string')
            data = eval("a=" + data);

        var rows = data.batch_ma;
        if (!rows || rows.length == 0)
            return null;

        for (var i = 0; i < rows.length; i++) {
            var row = rows[i];
            if (this.hide(row))
                continue;

            var ad = {
                title: row.title,
                sub_title: row.sub_title,
                image: row.image,
                loading_url: row.loading_url,
                click_url: row.click_url,
                impr_url: row.impr_url,
                inst_installsucc_url: row.inst_installsucc_url
            }
            return ad;
        }
        return null;
    },

    hide: function(row, vendor) {
        if (row.adtype == 'download' && !env.wifi) {
            return true;
        }
        return false;
    }
}


// sdk taks ===================
// 手机调用sdk
// 广点通
var sdk_toutiao = {

    adurl: function() {
        return {
            url: "TOUTIAOSDK",
            pos: "banner",
            data: {
                appId: "1106616441",
                placementId: "8010016467754860777"
                // placementId:"9040714184494018"
            }
        }
    },

    sdkname: function() {
        return "sdk_toutiao";
    },

    hide: function(row) {
        if (row.title.indexOf('抖音') > -1) {
            return true;
        }
        if (row.title.indexOf('西瓜') > -1) {
            return true;
        }
        return false;
    },

    exurls: function(row) {
        return {}
    },

    filter: function(list) {
        return list[0];
    }
}


// sdk taks ===================
// 手机调用sdk
// 广点通
var sdk_gdt = {

    adurl: function() {
        return {
            url: "GDTSDK",
            pos: "banner",
            data: {
                appId: "1106616441",
                placementId: "8010016467754860777"
                // placementId:"9040714184494018"
            }
        }
    },

    sdkname: function() {
        return "sdk_gdt";
    },

    hide: function(row) {
        if (row.title.indexOf('抖音') > -1) {
            return true;
        }
        if (row.title.indexOf('西瓜') > -1) {
            return true;
        }
        return false;
    },

    exurls: function(row) {
        return {}
    },

    filter: function(list) {
        return list;
    }
}

var sdk_baidu = {

    adurl: function() {
        return {
            url: "BaiduSDK",
            pos: "banner",
            data: {
                appId: "1106616441",
                placementId: "8010016467754860777"
                // placementId:"9040714184494018"
            }
        }
    },

    sdkname: function() {
        return "sdk_baidu";
    },

    hide: function(row) {
        if (row.title.indexOf('抖音') > -1) {
            return true;
        }
        if (row.title.indexOf('西瓜') > -1) {
            return true;
        }
        return false;
    },

    exurls: function(row) {
        return {}
    },

    filter: function(list) {
        return list[0];
    }
}

// 手机sdk inmobi
var sdk_inmobi = {

    adurl: function() {
        return {
            url: "InMobiSDK",
            pos: "banner",
            data: {
                appId: "f83af5e921de42cf813dc475c362aaf0",
                placementId: "1522609003688"
            }
        }
    },

    sdkname: function() {
        return "sdk_inmobi";
    },

    hide: function(row) {
        if (row.title.indexOf('抖音') > -1) {
            return true;
        }
        if (row.title.indexOf('西瓜') > -1) {
            return true;
        }
        return false;
    },

    exurls: function(row) {
        return {}
    },

    filter: function(list) {
        return list[0];
    }
}

// ================================
var api_wrong = {

    sdkname: function() {
        return "api_wrong";
    },

    adurl: function() {
        return {
            url: 'http://google.com',
            data: {}
        }
    },

    hide: function(row) {
        return true;
    },

    filter: function(list) {
        return null;
    }
}

function ads() {
    return {
        timeouts: [1500, 1500],
        tasks: [
            [api_yd]
        ]
    }
}

module.exports = ads;

console.log('banner loaded');
