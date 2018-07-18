var version = 'cbf5b8c7d822eb63f1ebc6e3ce4eba76;'
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
    require('./app', false, true);
}
else {
    require('./app');
}

module.exports = true;
