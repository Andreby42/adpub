var version = 20;
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
    ['fetch2', 'sdks/api', 'sdks/sdk'].forEach(function(js) {
        require(js, false, true);
    });
    LocalStorage.set(tag_version, version);
}
