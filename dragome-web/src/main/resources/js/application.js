/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 * 
 * This file is part of Dragome SDK.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v3.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

function WebSocketInitializer()
{
	if (getQuerystring("debug") == "true")
	{
		var socket = jQuery.atmosphere;
		var request = {
			timeout : 600000000,
			url : 'dragome-websocket',
			contentType : "application/json",
			transport : 'websocket',
			binaryType : 'arraybuffer'
		};

		window.onbeforeunload = function()
		{
			socket.unsubscribe();
		};

		request.onOpen = function(response)
		{
//			if (_ed.runApplication)
				_ed.runApplication();
		};

		request.onMessage = function(response)
		{
			var message = response.responseBody;

			// if (getQuerystring("zip") == "true")
			{
				// var inflator = new Inflator(new Base64Reader(message));
				// var textReader = new TextReader(new
				// Utf8Translator(inflator));
				// message = textReader.readToEnd();
			}

			if (window.onSocketMessage)
				window.onSocketMessage(message);
		};

		request.onClose = function(response)
		{
			// alert(response);
		};

		request.onError = function(response)
		{
			// alert(response);
		};

		window.subSocket = socket.subscribe(request);
	}
};

$(document).ready(function()
{
	WebSocketInitializer();
});
