/*jshint esversion: 6 */

var express = require('express');
var bodyParser = require('body-parser');	// https://github.com/expressjs/body-parser

var app = express();

// Данные в POST-запросе кодируются в формате json
var jsonParser = bodyParser.json()

// Данные для отправки, заполняются в POST-запросе (рекомендуется тестирования посредством Postman)
var dataToSend = {};

app.post('/send', jsonParser, function (req, res) {

	console.log('POST-Request is accepted...');
	console.log(req.body);

	dataToSend = {
		phone: req.body.phone,
		message: req.body.message
	};

	res.send('Send message "' + dataToSend.message + '" to phone no:' + dataToSend.phone)
});

app.get('/last', function (req, res) {

	console.dir(dataToSend);
	if( 'phone' in dataToSend) {

		console.log('The SMS message was transmitted to a mobile phone');
		res.json(dataToSend);

		// Если запрос на мобильный телефон отправлен, сбрасываем флаг наличия
		// SMS для отправки.
		// TODO: Данный подход не надёжен
		dataToSend = {};
	}
	else
	{
		console.log('Real data is not accepted yet');
		res.status(200).json({
			phone: "",
			message: ""
		});
	}
});

// Запускаем сервер, работающий по https. Сертификаты должны
// размещаться в корневом подкаталоге. Перед запуском сервера в режиме
// https, следует скопировать файлы "server_dev.key" и "server_dev.crt"
// из подкаталога "\MakeCertificates\" в текущий подкаталог
//
// Статьи:
//  https://medium.com/@noumaan/ssl-app-dev-a2923d5113c6
//  https://www.sitepoint.com/how-to-use-ssltls-with-node-js/
//  https://hackernoon.com/set-up-ssl-in-nodejs-and-express-using-openssl-f2529eab5bb

const https = require("https"),
fs = require("fs");

const options = {
  key: fs.readFileSync("server_dev.key"),
  cert: fs.readFileSync("server_dev.crt")
};


let server = https.createServer(options, app).listen(3000, function() {

  let host = server.address().address;
  let port = server.address().port;
  console.log('The server listening at ' + host + ':' + port);  
});
