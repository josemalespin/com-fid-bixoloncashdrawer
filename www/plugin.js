
var exec = require('cordova/exec');

var PLUGIN_NAME = 'FIDBixolon';

var FIDBixolon = {
  print: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'print', [phrase]);
  },
  openDrawer: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'openDrawer', [phrase]);
  }
};

module.exports = FIDBixolon;
