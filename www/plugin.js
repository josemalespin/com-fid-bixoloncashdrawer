
var exec = require('cordova/exec');

var PLUGIN_NAME = 'FIDBixolon';

var FIDBixolon = {
  print: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'print', [phrase]);
  },
  openDrawer: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'openDrawer', [phrase]);
  },
  cashCount: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'cashCount', [phrase]);
  },
  printSmax: function(phrase, cb) {
    exec(cb, null, PLUGIN_NAME, 'printSmax', [phrase]);
  },
};

module.exports = FIDBixolon;
