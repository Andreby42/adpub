// 函数参数可以自由协商
function loaded(type, pos, extInfo) {

}

function didShow(type, pos, extInfo) {
    console.log("event_didShow type="+type+" pos="+pos+" extInfo="+extinfo);
}

function didClick(type, pos, extInfo) {
    console.log("event_didClick type="+type+" pos="+pos+" extInfo="+extinfo);
}

function didClose(type, pos, extInfo) {
    //xxx
}


module.exports = [
    loaded,
    didShow,
    didClick,
    didClose
]

console.log('events loaded');
