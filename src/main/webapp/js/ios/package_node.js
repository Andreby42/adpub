
const config = require('./config.json');
const fs = require('fs');
const path = require('path');
const crypto = require('crypto');
const md5 = crypto.createHash("md5");

const babel = require("babel-core");
const UglifyJS = require("uglify-js");

config.rootDir = config.rootDir || './';
config.outputDir = config.outputDir || config.rootDir;
config.outputName = config.outputName || "app.do";

function joinJSFiles() {

    let contentList = [];
    config.filelist.map( filename => {

        const string = fs.readFileSync( config.rootDir+"/"+filename,'utf8');
        if(!string) {
            console.error("["+filename +"] is error file");
        }
        else {
            let packer = [
                "(function(global) {",
                "   var module = {};",
                "   global = global || this;",
                "   exports = {};",
                "   module.exports = exports;",
                "   (function(moudle, exports, global) {",
                "       "+string,
                "   })(module, exports, global);",
                "   return module.exports;",
                "})",
                "",
                ""
            ].join('\n');

            let name = filename;
            if(name.startsWith("./")) {
                name = name.slice(2);
            }
            name = name.replace(/\.js$/, ".do");
            contentList.push({name, packer});
        }
    });

    let writeJSString = "";

    contentList.forEach( item => {
        writeJSString += `global.AddModule('${item.name}', ${item.packer});\n`;;
    });

    writeJSString += "require('./event');\n"

    return writeJSString;
}

function md5String(needMd5) {
    md5.update(writeJSString);
    const str = md5.digest('hex');
    const md5Str = str.toLowerCase();
    return md5Str;
}

function writeFile(writeJSString) {
    let outputFileName = config.outputDir+'/'+config.outputName;
    console.log(outputFileName)
    fs.writeFileSync(outputFileName, writeJSString, 'utf8');
}

let writeJSString = joinJSFiles();

function compress(writeJSString) {
    const babelCode = babel.transform(writeJSString, {"presets": ["es2015"]}).code;
    const result = UglifyJS.minify(babelCode);
    writeJSString = result.code;
    return writeJSString;
}

function appendMain(writeJSString) {
    const string = fs.readFileSync( config.rootDir+"/main.js",'utf8');
    writeJSString = string + "\n" + writeJSString;
    return writeJSString;
}

writeJSString = appendMain(writeJSString);

if(process.argv.includes('compress')) {
    writeJSString = compress(writeJSString);
}

writeFile(writeJSString);
