
var version = 78;

var tag_version = 'js_engine_version';

var localVersion = (function() {
    try {
        return parseInt(LocalStorage.get(tag_version)) || 0;
    } catch (e) {
        return 0;
    }
})();

console.log(localVersion + ' => ' + version);
if (localVersion != version) {
    console.log('Update jsengine from ' + localVersion + ' to ' + version);
    var success = true;
    ['fetch2', 'sdks/api', 'sdks/sdk'].forEach(function(js) {
        success = success && (!!require(js, false, true));
    });
    if (success) {
        console.log('Js Updated.');
        LocalStorage.set(tag_version, version);
    }
}
