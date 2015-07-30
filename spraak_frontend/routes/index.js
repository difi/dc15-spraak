var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});
router.get('/_nn', function(req, res, next) {
  res.render('index_nn', { title: 'Express' });
});
router.get('/complex', function(req, res, next) {
  res.render('complex', { title: 'Express' });
});
router.get('/total', function (req, res, next) {
  res.render('total', {title: 'Express'});
});
router.get('/complex_nn', function(req, res, next) {
  res.render('complex_nn', { title: 'Express' });
});
router.get('/total_nn', function(req, res, next) {
  res.render('total_nn', { title: 'Express' });
});
router.get('/nynorsk_o_meter', function(req, res, next) {
  res.render('nynorsk_o_meter', { title: 'Express' });
});
router.get('/nynorsk_o_meter_nn', function(req, res, next) {
  res.render('nynorsk_o_meter_nn', { title: 'Express' });
});
router.get('/ordbruk', function(req, res, next) {
  res.render('ordbruk', { title: 'Express' });
});
router.get('/ordbruk_nn', function(req, res, next) {
  res.render('ordbruk_nn', { title: 'Express' });
});
module.exports = router;
