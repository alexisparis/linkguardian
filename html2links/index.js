const BMParser = require('bookmark-parser');
const cheerio = require('cheerio')
const fs = require("fs");
var Busboy = require('busboy');
var Readable = require('stream').Readable;
var jsdom = require("jsdom");


var replaceAll = function(str, find, replace) {
  return str.replace(new RegExp(find, 'g'), replace);
};

var parsehtml = function(html, callback) {
    return jsdom.env({
         html: html,
         scripts: ["./node_modules/jquery/dist/jquery.min.js"],
         done: function(errors, window) {
             var $, anchors, itemdoubleclick, results;
             $ = window.$;
             itemdoubleclick = "";
             results = new Array();
             anchors = $("dl").find("a");
             anchors.each(function(i, e) {
                 var add_date, name, result, tags, url;
                 url = $(e).attr("href");
                 name = $(e).text();
                 add_date = $(e).attr("add_date");
                 tags = new Array();
                 $(e).parents("dl").each(function(ii, ee) {
                     var folder, tag;
                     folder = $(ee).prev();
                     tag = folder.text();
                     return tags.push(replaceAll(tag, ",", " "));
                     // return tags.push(tag);
                 });
                 result = {
                     url: url,
                     name: name,
                     add_date: add_date,
                     tags: tags
                 };
                 return results.push(result);
             });
             if (typeof callback === "function") {
                 return callback(results);
             } else {
                 return console.warn("Callback isn't a function.");
             }
         }
     });
};

// parse recursively the output of the parsing of a bookmark file
// root in the current node
// results is the list of bookmarks
// currentPath is the path of the folders that contains the current node root
var processBmParserResult = function(root, results, currentPath) {

    if ( root ) {

        var newPath = currentPath;

        if (root.type == "folder") {
            if (newPath == undefined) {
                newPath = new Array();
            } else {
                newPath = currentPath.slice();
            }
            newPath.push(root.name);

        } else if (root.type == "bookmark") {
            if (root.url && root.url.length > 0) {
                results.push({
                    title: root.name,
                    url: root.url,
                    path: currentPath
                });
            }
        }

        if (root.children && root.children.length && root.children.length > 0) {
            for(var i = 0; i < root.children.length; i++) {
                var current = root.children[i];

                processBmParserResult(current, results, newPath);
            }
        }
    }
};

var parseBookmarkFile = function(data, result) {

    const items = new Array();

    // check if file contains FOLDED without value
    // if so, replace by FOLDED=""
    // var replaceAll = function(str, find, replace) {
    //   return str.replace(new RegExp(find, 'g'), replace);
    // };
    // data = replaceAll(data, 'FOLDED>', 'FOLDED="">');
    // data = replaceAll(data, 'folded>', 'folded="">');

    // // convert data to stream
    // var stream = new Readable();
    // stream.push(data);
    // stream.push(null);

    // BMParser.readFromHTMLFile(stream)
    //     .then(function (res) {

    //         conxole.log("### res ", res.Bookmarks);
    //         processBmParserResult(res.Bookmarks, items);
    //         result.send(JSON.stringify(items, null, 4));
    //     }, function(error) {
    //         console.log("error reading file", error);
    //     })
    //     .catch(function(error) {
    //         console.log("error reading file", error);
    //     });

    parsehtml(data, function(res) {
        if (res && res.length && res.length > 0) {

            for(var i = 0; i < res.length; i++) {
                var current = res[i];
                var bookmark = {
                    title: current.name,
                    url: current.url,
                    path: current.tags
                };
                if (bookmark.url && bookmark.url.length > 0) {
                    items.push(bookmark);
                }
            }
            result.send(JSON.stringify(items, null, 4));
        }
    });
};

const parse = function(data, result) {

    var isBookmarkFile = false;
    if (data) {
        const extracted = data.substring(0, 100).toLowerCase();

        isBookmarkFile = extracted.includes(" netscape-bookmark-file-1");
    }

    if (isBookmarkFile == true) {
        console.log("parse netscape bookmarks file...");
        result.setHeader('Content-Type', 'application/json');

        parseBookmarkFile(data, result);

    } else {
        console.log("parse standard html file...");
        result.setHeader('Content-Type', 'application/json');

        const items = new Array();
        const $ = cheerio.load(data);
        $('a').each(function (index, element) {
            var dThis = $(this);
            var bookmark = {
                title: dThis.text(),
                url: dThis.attr('href')
            };
            if (bookmark.url && bookmark.url.length > 0) {
                items.push(bookmark);
            }
        });
        result.send(JSON.stringify(items, null, 4));
    }
};

exports.html2links = function helloWorld (req, res) {

    if (req.method === 'POST') {
        var busboy = new Busboy({ headers: req.headers });

        var input = [];
        busboy.on('file', function(fieldname, val, fieldnameTruncated, valTruncated, encoding, mimetype) {
            var buffer = '';
            console.log('File [' + fieldname + ']: filename: ', val);
            val.on('data', function(data) {
                console.log('File [' + fieldname + '] got ' + data.length + ' bytes');
                buffer += data;
            });
            val.on('end', function() {
                console.log('File [' + fieldname + '] Finished');
                input.push(buffer);
            });
        });

        busboy.on('finish', function() {
            parse(input[0], res);
        });

        // The raw bytes of the upload will be in req.rawBody.
        busboy.end(req.rawBody);
    } else {
        // Only support POST.
        res.status(405).end();
    }
};

if (false) {
    var file = 'files/SafariBookmarks.html';
    file = 'files/SafariBookmarks_orig.html';
    // file = 'files/google.html';
    // file = 'files/bookmarks_small.html';

    var contents = fs.readFileSync(file, 'utf8');

    parse(contents, {
        send: function(data) {
            console.log("data", data);
        },
        setHeader: function(arg) {
            console.log("calling set header with " + arg);
        }
    });
}
