//yaml = require('js-yaml');
fs = require('fs');
propertiesReader = require('properties-reader');

var rootFolderWeb = "src/main/webapp/i18n/";
var rootFolderBack = "src/main/resources/i18n/";
var backPropertiesPrefix = "messages_";
var referenceLang = "en";
console.log("reference language is " + referenceLang + ".");

// get all the available languages
var langs = new Array();
fs.readdirSync(rootFolderWeb).forEach(file => {
    const stat = fs.statSync(rootFolderWeb + file);
    if (stat.isDirectory()) {
        langs.push(file);
    }
});

console.log("language to check? " + langs);
var stdin = process.openStdin();

function valueChanger(val) {
    return "XXAP " + val;
};

function merge(fileReference, fileTarget) {
    console.log("  merge " + fileReference + " and " + fileTarget);
    //var doc = yaml.safeLoad(fs.readFileSync('/src/main/webapp/de/activate.json', 'utf8'));
    var content = JSON.parse(fs.readFileSync(fileReference, 'utf8'));

    if (fs.existsSync(fileTarget)) {
        var target = JSON.parse(fs.readFileSync(fileTarget, 'utf8'));

        if (mergeObjects(content, target)) {
            fs.writeFileSync(fileTarget, JSON.stringify(target, null, 4));
            console.log("     " + fileTarget + " saved");
        }
    } else {
        console.log("  " + fileTarget + " does not exist => create it based on reference file");

        changeProperty(content, valueChanger);

        fs.writeFileSync(fileTarget, JSON.stringify(content, null, 4));
        console.log("     " + fileTarget + " saved");
    }
}

function changeProperty(obj, valueModifierFunction) {
    var modified = false;
    if (obj) {
        for(var e in obj) {
            var current = obj[e];

            if (typeof current == "object") {
                var subModified = changeProperty(current, valueModifierFunction);
                modified = modified || subModified;
            } else if (typeof current == "string") {
                obj[e] = valueModifierFunction(obj[e]);
                modified = true;
            }
        }
    }
    return modified
}

function mergeObjects(reference, target) {
    var modified = false;
    if (reference) {
        Object.keys(reference).forEach(function(key) {
            var referencePart = reference[key];

            if(target.hasOwnProperty(key)){
                if (typeof referencePart === 'object') {
                    var subModified = mergeObjects(referencePart, target[key]);
                    modified = modified || subModified;
                }
            } else {
                if (typeof referencePart == 'object') {
                    var subModified = changeProperty(referencePart, valueChanger);
                    modified = modified || subModified;
                    target[key] = referencePart;
                } else if (typeof referencePart == 'string') {
                    target[key] = valueChanger(referencePart);
                    modified = true;
                }
            }
        });
    }
    return modified;
}

stdin.addListener("data", function(d) {
    // note:  d is an object, and when converted to a string it will
    // end with a linefeed.  so we (rather crudely) account for that
    // with toString() and then trim()
    var targetLang = d.toString().trim();
    console.log("you choose: [" + targetLang + "]");

    if (langs.indexOf(targetLang) < 0) {
        console.log("ERROR : target lang should be one of " + langs);
        process.exit(1);
    }

    try {
        fs.readdirSync(rootFolderWeb + referenceLang).forEach(file => {
            merge(rootFolder + referenceLang + "/" + file, rootFolderWeb + targetLang + "/" + file);
        });
    } catch (e) {
        console.log(e);
    }

    var referenceProperties = propertiesReader(rootFolderBack + backPropertiesPrefix + referenceLang.replace("-", "_") + ".properties");
    var targetPropertiesPath = rootFolderBack + backPropertiesPrefix + targetLang.replace("-", "_") + ".properties";
    var targetProperties = propertiesReader(targetPropertiesPath);

    var propertiesModified = false;
    for(var key in referenceProperties.getAllProperties()) {
        console.log("  a " + key);

        if (!targetProperties.getAllProperties().hasOwnProperty(key)){
            targetProperties.set(key, valueChanger(referenceProperties.get(key)));

            fs.appendFileSync(targetPropertiesPath, key + "=" + valueChanger(referenceProperties.get(key)) + "\r\n");

            propertiesModified = true;
        }
    }

    process.exit();
});

