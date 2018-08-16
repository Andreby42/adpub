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
            url: 'https://api.chelaile.net.cn/adpub/adv!getInterstitialHomeAds.action'}
    },


    filter: function(data) {
        var array = data.split("YGKJ");
        if (array.length < 2) {
            return null;}
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
            isSkip : row.isSkip,
            isDisplay : row.isDisplay,
            duration : row.duration,
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
            wxMiniProId: row.wxMiniProId,
            wxMiniProPath: row.wxMiniProPath,
            adStyle: row.displayType
        }

        return ad;
    },

    aid: function() {
        return 'api_chelaile';
    },

    ad_data: function() {
        return '${API_CHELAILE_DATA}'
    }
}


// sdk taks ===================
// 手机调用sdk

var sdk_gdt = {

    adurl: function() {
        return {
            url: "GDTSDK",
            pos: "interstitial",
            data: {
                appId: "1106616441",
                placementId: "${sdk_gdt_placementId}"
            }}
    },

    sdkname: function() {
        return "sdk_gdt";
    },

    filter: function(list) {
        var ad = list[0];
        if (this.adCheck(ad)) {
            return ad;} else {
            return null;}
    },
    adCheck: function(ad) {
        if (this.adStyle() == 3) {
            console.log("ad img list: " + ad.getImgList());
            return (ad.getImgList() != null && ad.getImgList().size() == 3);} else {
            console.log("ad img: " + ad.getImgUrl());
            return true;}
    },

    asEntity: function(ad) {
        return !ad ? null : {
            head: ad.getTitle(),
            subhead: ad.getDesc(),
            pic: ad.getImgUrl()}
    },

    aid: function() {
        return 'sdk_gdt';
    },

    adStyle: function() {
        return 2;
    }
}



var sdk_ifly = {

    adurl: function() {
        return {
            url: "IFLYSDK",
            pos: "interstitial",
            data: {
                appId: "1106616441",
                placementId: "${sdk_ifly_placementId}"
                // placementId:"9040714184494018"
            }}
    },

    sdkname: function() {
        return "sdk_ifly";
    },

    hide: function(row) {
        if (row.title.indexOf('抖音') > -1) {
            return true;}
        if (row.title.indexOf('西瓜') > -1) {
            return true;}
        return false;
    },

    asEntity: function(ad) {
        if (ad == null) return null;

        var ret = {};
        ret.head = ad.getTitle();
        ret.subhead = ad.getSubTitle();
        return ret;
    },

    exurls: function(row) {
        return {}
    },

    filter: function(list) {
        var ad = list && list[0];
        if (!ad) return null;

        if (this.adCheck(ad)) {
            return ad;} else {
            return null;}
    },
    adCheck: function(ad) {
        if (this.adStyle() == 3) {
            console.log("ad img list: " + ad.getImgUrls());
            return (ad.getImgUrls() != null && ad.getImgUrls().size() == 3);} else {
            return true;}
    },

    aid: function() {
        return 'sdk_ifly';
    },

    adStyle: function() {
        return 2;
    }
}

var sdk_adview = {

    adurl: function() {
        return {
            url: "ADVIEWSDK",
            pos: "interstitial",
            data: {
                appId: "SDK201817090508490a6u8s7z5m4aoqf",
                placementId: "${sdk_adview_placementId}"
                // placementId:"9040714184494018"
            }}
    },

    sdkname: function() {
        return "sdk_adview";
    },

    hide: function(row) {
        return false;
    },

    asEntity: function(ad) {
        if (ad == null) return null;

        var ret = {};
        ret.head = ad.getTitle();
        ret.subhead = ad.getSubTitle();
        return ret;
    },

    exurls: function(row) {
        return {}
    },

    filter: function(list) {
        var ad = list && list[0];
        if (!ad) return null;

        if (this.adCheck(ad)) {
            return ad;} else {
            return null;}
    },
    adCheck: function(ad) {
        return true;
    },

    aid: function() {
        return 'sdk_adview';
    },

    adStyle: function() {
        return 2;
    }
}


function ads() {
    return {
        traceInfo: {
            traceid: '${TRACEID}',
            pid: '28'
            },
        closeInfo: {
            closePic: '${closePic}'
            },
        urls: {
            exposeUrl: 'http://atrace.chelaile.net.cn/exhibit?',
            clickUrl: 'http://atrace.chelaile.net.cn/click?',
            closeUrl: 'http://atrace.chelaile.net.cn/close?'},
        timeouts: ${TIMEOUTS},
        tasks: ${TASKS}
    }
}

module.exports = ads;

console.log('splash loaded');