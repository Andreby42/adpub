
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
        
        if(!filename.endsWith(".do")){
            filename += ".do";
        }
    
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

function writeFile(writeJSString, md5Str) {
    let outputFileName = config.outputDir+'/'+config.outputName;
    console.log(outputFileName)
    fs.writeFileSync(outputFileName, writeJSString, 'utf8');
}

let writeJSString = joinJSFiles()

const md5Str = md5String(writeJSString);

function setLocalVersion(version) {
    writeJSString += `LocalStorage.set('js_engine_version', '${version}');`;
}
//setLocalVersion(md5Str);

function replaceRequireInRule(md5Str, dir) {

    const filelist = fs.readdirSync(dir);

    filelist.forEach( filename => {
        const filedir = path.join(dir,filename);
        const stats = fs.statSync(filedir);
        const isFile = stats.isFile();
        if(isFile) {
            let content = fs.readFileSync(filedir, 'utf8');
            content = content.replace(/app\.([0-9a-zA-Z]{32})/g,'app.'+md5Str);
            fs.writeFileSync(filedir, content, 'utf8');
        }
    })
}

function removeOldOutput(dir) {

    const filelist = fs.readdirSync(dir);

    filelist.forEach( filename => {
        const filedir = path.join(dir,filename);
        const stats = fs.statSync(filedir);
        const isFile = stats.isFile();
        if(isFile) {
            let matchs = filedir.match(/app\.[0-9a-zA-Z]{32}\.do/)
            if(matchs && matchs.length) {
                fs.unlinkSync(filedir);
            }
        }
    });
}


// if(process.argv.includes('clean')) {
//     removeOldOutput(config.outputDir);
// }

function compress(writeJSString) {
    const babelCode = babel.transform(writeJSString, {"presets": ["es2015"]}).code;
    const result = UglifyJS.minify(babelCode);
    writeJSString = result.code;
    return writeJSString;
}

if(process.argv.includes('compress')) {
    writeJSString = compress(writeJSString);
}

function writeVersionFile(md5Str) {
    let content = fs.readFileSync(config.rootDir + "/" + "update.do.tpl", 'utf8');
    content = content.replace(/var version = __CURENTVERSION__;/g,`var version = '${md5Str};'`);
    fs.writeFileSync(config.rootDir + "/" + "update.do", content, 'utf8');
     
}

writeFile(writeJSString, md5Str);
//writeVersionFile(md5Str);

// if(process.argv.includes('autorule')) {
//     replaceRequireInRule(md5Str, config.rootDir + '/rule');
// }



