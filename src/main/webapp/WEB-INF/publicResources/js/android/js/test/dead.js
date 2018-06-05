
 jsfn1 = function(a, javafn) {
    console.log('jsfn1 called.');
    javafn()
}

 jsfn2 = function(a) {
    console.log('jsfn2 called.')
    try {
        console.log(a + ' + jsfn2');
    } catch (e) {
        console.log(e);
    }
}

// if (module)
    module.exports=jsfn1
