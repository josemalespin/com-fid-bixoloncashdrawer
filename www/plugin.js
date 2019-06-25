
var exec = require('cordova/exec');

var PLUGIN_NAME = 'FIDBixolon';

var FIDBixolon = {
  print: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'print', [phrase]);
  }
};

module.exports = FIDBixolon;
