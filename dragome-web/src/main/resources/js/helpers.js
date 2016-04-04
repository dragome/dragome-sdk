
/*******************************************************************************
 * Copyright (c) 2011-2014 Fernando Petrola
 *
 * This file is part of Dragome SDK.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the GNU Public License v3.0 which accompanies
 * this distribution, and is available at http://www.gnu.org/licenses/gpl.html
 ******************************************************************************/

EventDispatcher = {};
_ed = EventDispatcher;
var __next_objid = 1;

function objectId(obj)
{
	if (obj == null)
		return null;
	if (obj.__obj_id == null)
		obj.__obj_id = __next_objid++;
	return obj.__obj_id;
}

function decode_base64(s)
{
	var b = l = 0, r = '', s = s.split(''), i, m = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'.split('');
	for (i in s)
	{
		b = (b << 6) + m.indexOf(s[i]);
		l += 6;
		while (l >= 8)
			r += String.fromCharCode((b >>> (l -= 8)) & 0xff);
	}
	return r;
}

function getOuterHTML(anElement)
{
	var parent = anElement.parentNode;
	var hasParent = parent != null;
	if (!hasParent)
	{
		parent = document.createElement("div");
		parent.appendChild(anElement);
	}

	var temp = document.createElement(anElement.tagName);
	parent.replaceChild(temp, anElement);

	var div = document.createElement('div');
	div.appendChild(anElement);
	result = div.innerHTML;

	parent.replaceChild(anElement, temp);

	if (!hasParent)
		parent.removeChild(anElement);

	return result;
}

function stopEvent(pE)
{
	if (!pE)
		if (window.event)
			pE = window.event;
		else
			return;
	if (pE.cancelBubble != null)
		pE.cancelBubble = true;
	if (pE.stopPropagation)
		pE.stopPropagation();
	if (pE.preventDefault)
		pE.preventDefault();
	if (window.event)
		pE.returnValue = false;
	if (pE.cancel != null)
		pE.cancel = true;
}

function rgb2html(red, green, blue)
{
	var decColor = red + 256 * green + 65536 * blue;
	return decColor.toString(16);
}

function getQuerystring(key, default_)
{
	if (default_ == null)
		default_ = "";
	key = key.replace(/[\[]/, "\\\[").replace(/[\]]/, "\\\]");
	var regex = new RegExp("[\\?&]" + key + "=([^&#]*)");
	var qs = regex.exec(window.location.href);
	if (qs == null)
		return unescape(default_);
	else
		return unescape(qs[1]);
};

function executeLocalstorageQuery(tx, aQuery, parameters, callback)
{
	// alert("executing: "+aQuery+" - "+ parameters);
	tx.executeSql(aQuery, parameters, callback);
}

window.performance = window.performance || {};
performance.now = (function()
{
	return performance.now || performance.mozNow || performance.msNow || performance.oNow || performance.webkitNow || function()
	{
		return new Date().getTime();
	};
})();

function jQueryHttpRequest(isAsync, url, parameters, asyncCallback, crossDomain, isGet)
{
	if (crossDomain != "true")
	{
		isAsync = isAsync && isAsync != "false";

		var ajaxParameters = {
			async : isAsync,
			contentType : 'application/x-www-form-urlencoded',
			dataType : 'text',
			success : null,
			error : null
		};

		if (isAsync)
		{
			ajaxParameters.success = function(data)
			{
				if (data != "null")
					asyncCallback.$onSuccess___java_lang_Object$void(data);
			};

			ajaxParameters.error = function()
			{
				asyncCallback.$onError$void();
			};
		}

		$.ajaxSetup(ajaxParameters);

		var result = isGet ? $.get(url, parameters) : $.post(url, parameters);
		return result.responseText;
	}
	else
	{
		fileParameter = parameters.file ? "file=" + parameters.file + "&" : "";
		var a = $.getJSON(url + '?' + fileParameter + 'callback=?', function(data)
		{
			var tranformedData = eval(parameters.transformer);
			asyncCallback.$onSuccess___java_lang_Object$void(tranformedData);
		});
	}
}

function httpRequest(isAsync, url, parameters, asyncCallback, crossDomain, isGet)
{
	var xhr=new XHRHandler(isAsync);
	if(crossDomain==='true')xhr.setCORSCall(false);
	if(isAsync) {
		if(isGet) {
			xhr.get(url)
			.success(function (data, xhr) {
			   if (data != "null")
				   asyncCallback.$onSuccess___java_lang_Object$void(data);
			})
			.error(function (data, xhr) {
				   asyncCallback.$onError$void();
			});
		} else {
			xhr.post(url,parameters)
			.success(function (data, xhr) {
			   if (data != "null")
				   asyncCallback.$onSuccess___java_lang_Object$void(data);
			})
			.error(function (data, xhr) {
				   asyncCallback.$onError$void();
			});
		}
	} else {
		 if(isGet) {
			return xhr.get(url);
		 } else {
			return xhr.post(url,parameters);
		 }
	}
}

function socketCreator(aUrl, successCallback, errorCallback)
{
	window.onSocketMessage = successCallback;
};

function getTemplatePart(content, id)
{
	var mainElement = document.createElement('div');
	mainElement.setAttribute("style", "display:none;");
	document.body.appendChild(mainElement);

	var uniqueId = new Date().valueOf();

	var data = content.replace('<body', '<body><div id="body' + uniqueId + '"').replace('</body>', '</div></body>');

	mainElement.innerHTML = data;

	var foundElement = getDocumentElementByAttributeValue(document, "data-template", id);
	if (!foundElement)
		foundElement = document.getElementById("body" + uniqueId);

	foundElement.removeAttribute("data-template");

	var subElement = document.createElement('div');
	subElement.appendChild(foundElement);
	mainElement.parentElement.removeChild(mainElement);
	return subElement.innerHTML;
}

function getURL(url)
{
    return $.ajax({
        type: "GET",
        url: url,
        cache: false,
        async: false
    }).responseText;
}

function getURLNew(url)
{
    return new XHRHandler(true).get(url);
}


function refreshPageSetup()
{
	lastCompilationTime = parseInt(getURL("compiler-service"));

	if (getQuerystring("refresh") == "true")
		setInterval(function()
		{
			var compilationTime = parseInt(getURL("compiler-service"));
			if (compilationTime > lastCompilationTime)
			{
				lastCompilationTime = compilationTime;
				window.location.reload();
			}
		}, 500);
}

if (getQuerystring("refresh") == "true")
	refreshPageSetup();

// if (getQuerystring("debug") != "true")
// document.getElementById("console").style.display="none";

function getElementByDebugId(attrib)
{
	return document.querySelectorAll('[data-debug-id="' + attrib + '"]')[0];
}

function getElementByAttributeValue(attribName, value)
{
	return getDocumentElementByAttributeValue(document, attribName, value);
}

function getDocumentElementByAttributeValue(aDocument, attribName, value)
{
	return aDocument.querySelectorAll('[' + attribName + '="' + value + '"]')[0];
}

function checkStyleSheet(url)
{
    var found = false;
	for ( var i = 0; i < document.styleSheets.length; i++)
	{
		if (document.styleSheets[i].href == url)
		{
			found = true;
			break;
		}
	}
	if (!found)
	{
		$('head').append($('<link rel="stylesheet" type="text/css" href="' + url + '" />'));
	}
}

//checkStyleSheet("dragome/dragome.css");

function setupCheckCast()
{
	if (getQuerystring("check-cast-disabled") == "true")
		dragomeJs.checkCast= function (obj){return obj};
}

// adapted from atomic.js library, https://github.com/toddmotto/atomic
function XHRHandler(syncCall) {
  'use strict';

  var contentType= 'application/x-www-form-urlencoded',
    cors= false,
    corscridentials= false,
    async=syncCall===undefined || syncCall===true;

  var sxhr = function (type, url, data) {
// var XHR = new XMLHttpRequest();
     request.open(type, url, false);
     request.setRequestHeader('Content-type', contentType);
     request.send(data);
     if (request.status >= 200 && request.status < 300) {
     	return request.responseText;
     } else return null;
  };

  var xhr = function (type, url, data) {
    if(async) {
		var methods = {
		  success: function () {},
		  error: function () {}
		};
		var request = new XMLHttpRequest();
		if(useCORS) {
			if ('withCredentials' in request) {
				request.withCredentials = corscridentials;
			} else {
				request = new XDomainRequest();
			}
		}

		request.open(type, url, true);
		if(request.overrideMimeType) request.overrideMimeType('text/html');
		request.setRequestHeader('Content-type', contentType);
		if(useCORS) {
			request.onload=function() {
				if (request.status >= 200 && request.status < 300) {
					methods.success.apply(methods, [request.responseText, request]);
				} else {
					methods.error.apply(methods, [null, request]);
				}
			};

			request.onerror=function() {
				methods.error.apply(methods, null);
			};
		} else {
		  request.onreadystatechange = function () {
			if (request.readyState === 4) {
				if (request.status >= 200 && request.status < 300) {
					methods.success.apply(methods, [request.responseText, request]);
				} else {
					methods.error.apply(methods, [null, request]);
				}
			}
		 };
		}
		request.send(data);

		var xhrMethods = {
		  success: function (callback) {
			methods.success = callback;
			return xhrMethods;
		  },
		  error: function (callback) {
			methods.error = callback;
			return xhrMethods;
		  }
		};

		return xhrMethods;
    } else {
    	return sxhr(type, url, data);
    }
  };

  this.get = function (src) {
    return xhr('GET', src);
  };

  this.put = function (url, data) {
    return xhr('PUT', url, data);
  };

  this.post= function (url, data) {
    return xhr('POST', url, data);
  };

  this.delete = function (url) {
    return xhr('DELETE', url);
  };

  this.setContentType = function(value) {
    contentType = value;
  };

  this.setCORSCall = function(useCridentials) {
  	  cors=true;
  	  corscridentials=useCridentials;
  };
};
