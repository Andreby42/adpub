// // // we use this js for testing functions.
// //
function testTimer() {
    var num = setTimeout(function() {
        console.log('a timeout.' + num);
    }, 10);
    console.log('we get a timeout ' + num);

    var num2 = setInterval(function() {
        console.log('a interval' + num2);
    }, 10)
    console.log('we get an interval ' + num);
    setTimeout(function() {
        console.log('we stop an interval ' + num2)
        clearInterval(num2);

        console.log('we stop a timeout ' + num);
        clearTimeout(num);
    }, 30);
}

function testRequire() {
    var events = require('event');
    console.log(events);
}
//
//
// // testTimer();
// // testRequire();
