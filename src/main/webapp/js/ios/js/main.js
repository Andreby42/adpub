(function(global){
var moduleCaches = {};
var systemRequire = global.require;
global.require = function(filename, noCache, forceUpdate) {
    try {
        var name = filename;
        if(name.startsWith('./')) {
            name = name.slice(2);
        }
        if(!name.endsWith('.do')){
            name += '.do';
        }

        var ret;
        if(noCache || forceUpdate) {
            ret = systemRequire.apply(this, arguments);
        }
        else {
            ret = moduleCaches[name];
            if(!(typeof ret === 'function')) {
                ret = systemRequire.apply(this, arguments);
                moduleCaches[name] = ret;
            }
        }

        return ret ? ret(global) : function(){};
    } catch(e) {
        console.log('e='+e);
        delete moduleCaches[name];
        throw e;
    }
}

global.AddModule = function(name, code) {
    moduleCaches[name] = code;
}

})(this);
