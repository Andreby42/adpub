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
        console.log('name = '+name + noCache + forceUpdate);
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
        console.log('999999');
        return ret ? ret(global) : function(){};
    } catch(e) {
        console.log('e='+e);
        delete moduleCaches[name];
        throw e;
    }
}

global.AddModule = function(name, code) {
    console.log('AddModule '+name + ' code='+code);
    moduleCaches[name] = code;
}

console.log('run main.do');
})(this);
