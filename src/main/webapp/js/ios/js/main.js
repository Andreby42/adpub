'use strict';

String.prototype.contains = String.prototype.contains || function(str) {
	return this.indexOf(str) >= 0;
};

String.prototype.startsWith = String.prototype.startsWith || function(prefix) {
	return this.indexOf(prefix) === 0;
};

String.prototype.endsWith = String.prototype.endsWith || function(suffix) {
	return this.indexOf(suffix, this.length - suffix.length) >= 0;
};

(function (global) {
    var moduleCaches = {};
    var systemRequire = global.require;
    global.require = function (filename, noCache, forceUpdate) {
        try {
            var name = filename;
            if (name.startsWith('./')) {
                name = name.slice(2);
            }
            if (!name.endsWith('.do')) {
                name += '.do';
            }

            var ret;
            if (noCache || forceUpdate) {
                ret = systemRequire.apply(this, arguments);
            } else {
                ret = moduleCaches[name];
                if (!(typeof ret === 'function')) {
                    ret = systemRequire.apply(this, arguments);
                    moduleCaches[name] = ret;
                }
            }

            return ret ? ret(global) : function () {};
        } catch (e) {
            console.log('e=' + e);
            delete moduleCaches[name];
            throw e;
        }
    };

    global.AddModule = function (name, code) {
        moduleCaches[name] = code;
    };
})(global);
