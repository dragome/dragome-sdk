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


EventTarget.prototype.addEventListenerBase = EventTarget.prototype.addEventListener;
EventTarget.prototype.addEventListener = function(type, listener)
{
	if (!this._listeners)
		this._listeners= [];
		
    this._listeners.push({target: this, type: type, listener: listener});
    this.addEventListenerBase(type, listener);
};

EventTarget.prototype.removeEventListeners = function(targetType)
{
	if (this._listeners)
	    for(var index = 0; index != this._listeners.length; index++)
	    {
	        var item = this._listeners[index];
	
	        var target = item.target;
	        var type = item.type;
	        var listener = item.listener;
	
	        if(target == this && type == targetType)
	        {
	            this.removeEventListener(type, listener);
	        }
	    }
}

function EvalFunction() {
    this.execute = function(o1, p1) {
        var properties = Object.getOwnPropertyNames(o1);

        var fieldToLocal = "";
        var localToField = "";
        for (key in properties)
            if (!properties[key].toString().startsWith("function(")) {
                fieldToLocal += "var " + properties[key] + "= o1." + properties[key] + ";";
                localToField += "o1." + properties[key] + "= " + properties[key] + ";";
            }
        eval(fieldToLocal);
        var result = eval(p1);
        eval(localToField);
        return result;
    }
};

contextEvaluator= new EvalFunction();

function byXpath(parentId, xpathExpression) {
    var parent = com_dragome_helpers_DragomeEntityManager.$get___java_lang_String$java_lang_Object(parentId)

    return document.evaluate(xpathExpression, parent.node, null, XPathResult.FIRST_ORDERED_NODE_TYPE, null).singleNodeValue
}

function objectId(obj) {
    if (obj == null)
        return null;
    else if (typeof obj === 'string')
    	return obj.split('').reduce((prevHash, currVal) => (((prevHash << 5) - prevHash) + currVal.charCodeAt(0))|0, 0);
    else if (obj.__obj_id == null)
        obj.__obj_id = __next_objid++;

    return obj.__obj_id;
}

function decode_base64(s) {
    var b = l = 0,
        r = '',
        s = s.split(''),
        i, m = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/'.split('');
    for (i in s) {
        b = (b << 6) + m.indexOf(s[i]);
        l += 6;
        while (l >= 8)
            r += String.fromCharCode((b >>> (l -= 8)) & 0xff);
    }
    return r;
}

function getOuterHTML(anElement) {
    var parent = anElement.parentNode;
    var hasParent = parent != null;
    if (!hasParent) {
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

function stopEvent(pE) {
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

function rgb2html(red, green, blue) {
    var decColor = red + 256 * green + 65536 * blue;
    return decColor.toString(16);
}

function getQuerystring(key, default_) {
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

function executeLocalstorageQuery(tx, aQuery, parameters, callback) {
    tx.executeSql(aQuery, parameters, callback);
}

window.performance = window.performance || {};
performance.now = (function() {
    return performance.now || performance.mozNow || performance.msNow || performance.oNow || performance.webkitNow || function() {
        return new Date().getTime();
    };
})();

function socketCreator(aUrl, successCallback, errorCallback) {
    window.onSocketMessage = successCallback;
}

function getTemplatePart(content, id) {
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

function consoleMessage(message) {
    if (window['console'] != undefined && console['log'] != undefined) {
        console.log(message);
    }
}

function refreshPageSetup() {
    lastCompilationTime = parseInt(getURL("compiler-service"));

    if (getQuerystring("refresh") == "true")
        setInterval(function() {
            var compilationTime = parseInt(getURL("compiler-service"));
            if (compilationTime > lastCompilationTime) {
                lastCompilationTime = compilationTime;
                window.location.reload();
            }
        }, 500);
}

if (getQuerystring("refresh") == "true")
    refreshPageSetup();

function getElementByDebugId(attrib) {
    return document.querySelectorAll('[data-debug-id="' + attrib + '"]')[0];
}

function getElementByAttributeValue(attribName, value) {
    return getDocumentElementByAttributeValue(document, attribName, value);
}

function getDocumentElementByAttributeValue(aDocument, attribName, value) {
    return aDocument.querySelectorAll('[' + attribName + '="' + value + '"]')[0];
}

function checkStyleSheet(url) {
    var found = false;
    for (var i = 0; i < document.styleSheets.length; i++) {
        if (document.styleSheets[i].href == url) {
            found = true;
            break;
        }
    }
    if (!found) {
        var fileref = document.createElement("link");
        fileref.setAttribute("rel", "stylesheet");
        fileref.setAttribute("type", "text/css");
        fileref.setAttribute("href", url);
        document.getElementsByTagName("head")[0].appendChild(fileref);
    }
}

function setupCheckCast() {
    if (getQuerystring("check-cast-disabled") == "true")
        dragomeJs.checkCast = function(obj) {
            return obj;
        };
}

function onReady(callback) {
    if (document.readyState == "complete") {
        return setTimeout(callback, 1);
    }

    window.addEventListener("load", function(event) {
        callback();
    });
}

function jsonToQueryString(params) {
    var query = "";
    for (key in params)
        query += encodeURIComponent(key) + "=" + encodeURIComponent(params[key]) + "&";

    return query;
}