var express = require('express');
var router = express.Router();

/* GET home page. */
router.get('/', function(req, res, next) {
  res.render('index', { title: 'Express' });
});

router.get('/nn', function(req, res, next) {
  res.render('index_nn', { title: 'Express' });
});

router.get('/complex', function(req, res, next) {
  res.render('complex', { title: 'Express' });
});

router.get('/total', function(req, res, next) {
  res.render('total', { title: 'Express' });
});

router.get('/agency', function(req, res, next) {
  res.render('agency', { title: 'Express' });
});

module.exports = router;
