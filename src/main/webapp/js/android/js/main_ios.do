global.PLATFORM = "ios";

var sys_require = global.require;
global.require = function(file) {
    if(file) {
        var retValue = null;
        try {
            retValue = sys_require(file);
        } catch(e) {
            console.log(e);
        }

        try {
            if(!retValue) {
                var pathComs = file.split('/');
                pathComs.splice(pathComs.length-1,0,PLATFORM)
                file = pathComs.join("/");
                retValue = sys_require(file);
            }
        } catch(e) {
            console.log(e);
        }
        return retValue;
    }
}
