dragomeJs = {};

function _isNull(aObject) {
	if (typeof aObject == 'boolean')
		return aObject == false;
	else if (typeof aObject == 'number')
		return aObject == 0;
	else
		return aObject == null;
}

function addSignatureTo(aClass, aMethodSignature, aGenericSignature) {
	if (!aClass.$$$$signatures)
		aClass.$$$$signatures = [];

	aClass.$$$$signatures[aMethodSignature] = aGenericSignature;
}

java_lang_null = function() {
};

var now = (function() {
	var performance = window.performance || {};

	performance.now = (function() {
		return performance.now || performance.webkitNow || performance.msNow
				|| performance.oNow || performance.mozNow || function() {
					return new Date().getTime();
				};
	})();

	return performance.now();
});

if (typeof String.prototype.startsWith != 'function') {
	String.prototype.startsWith = function(str) {
		return this.slice(0, str.length) == str;
	};
}

if (typeof String.prototype.endsWith != 'function') {
	String.prototype.endsWith = function(str) {
		return this.slice(-str.length) == str;
	};
}

dragomeJs.createException = function(className, message, original) {
	if (dragomeJs.signalState == 1)
		throw "Recursive exception creation";

	dragomeJs.signalState = 1;

	var stack;

	try {
		undefined.b = 1;
	} catch (e) {
		stack = e.stack;
		stack = stack.replace(
				"TypeError: Cannot set property 'b' of undefined", message);
	}

	var exception;
	try {
		var clazz = java_lang_Class
				.$forName___java_lang_String$java_lang_Class(className);
		exception = eval("new clazz.$$$nativeClass___java_lang_Object");
		exception.$$init____java_lang_String$void(message);
		exception.original = original;
		exception.stack = stack;
	} catch (e) {
		throw "Could not create exception for " + message;
	} finally {
		dragomeJs.signalState = 0;
	}
	return exception;
};

/**
 * Returns the specified exception. According VM Spec's athrow instruction, if
 * objectref is null, a NullPointerException is returned instead of objectref.
 */
dragomeJs.nullSaveException = function(objectref) {
	if (objectref instanceof Error && getQuerystring("debug") == "true")
		objectref = dragomeJs.createException("java.lang.NullPointerException",
				objectref.message, objectref);
	else if (objectref == null)
		objectref = dragomeJs.createException("java.lang.NullPointerException",
				null);
	if (!objectref.message) {
		var message = objectref.$$$message___java_lang_String;
		objectref.message = message;
		var cleanStack = "";
		try {
			undefined.b = 1;
		} catch (e) {
			var stackSplit = e.stack.split("\n");
			for(i = 2; i < stackSplit.length;i++)
				cleanStack = cleanStack+stackSplit[i]+"\n";
		}
		objectref.$$$stackTrace___java_lang_String = cleanStack;
	}
	return objectref;
};

dragomeJs.console_write = function(message) {
	consoleMessage(message);
};

dragomeJs.console_clear = function() {
	if (window['console'] != undefined && console['clear'] != undefined) {
		console.clear();
	}
};

dragomeJs.println = function(message) {
	consoleMessage(message);
};

dragomeJs.print = function(message) {
	consoleMessage(message);
};

dragomeJs.isInstanceof = function(obj, type) {
	if (obj == undefined)
		return false;

	if ((typeof obj == "string" || obj instanceof String)
			&& type == java_lang_String)
		return true;

	var clazz = !obj.$$type ? obj.constructor : obj;

	if (clazz == type)
		return true;

	if (type.$$type == "Interface") {
		if (obj.$$type == "Interface") {
			return checkInterfaceExtendsOther(obj, type);
		} else
			return qx.Class.hasInterface(clazz, type);
	} else
		return qx.Class.isSubClassOf(clazz, type);
}

function checkInterfaceExtendsOther(obj, type) {
	var interfacesList = obj.$$extends;

	var result = false;

	if (obj.name == type.name)
		return true;

	for (i in interfacesList)
		result |= checkInterfaceExtendsOther(interfacesList[i], type);

	return result;
};

dragomeJs.isInstanceofArray = function(obj, arrayType) {
	return obj == undefined || obj == null ? false : obj.classSignature.replace(/\[/g, '_').replace(/;/g, '$').replace(/\./g, '_') == arrayType;
}

dragomeJs.cmp = function(value1, value2) {
	if (value1 == value2)
		return 0;
	else if (value1 > value2)
		return 1;
	else
		return -1;
};

// Truncate a number. Needed for integral types in casting and division.
dragomeJs.trunc = function(f) {
	if (f < 0)
		return Math.ceil(f);
	return Math.floor(f);
};

/**
 * Narrows the number n to the specified type. The type must be 0xff (byte) or
 * 0xffff (short). See 5.1.3 "Narrowing Primitive Conversions" of the Java
 * Language Specification.
 */
dragomeJs.narrow = function(n, bits) {
	n = dragomeJs.trunc(n);
	n = n & bits;
	if (n > (bits >>> 1))
		n -= (bits + 1);
	return n;
};

/**
 * Returns a new multidimensional array of the specified array type [...[T and
 * the desired dimensions. For example, if there are three dimensions, then the
 * returned array is new [[[T[dim[0]][dim[1]][dim[2]] If T is the boolean type,
 * then the elements are initialized to false. Otherwise, if T is not a class,
 * then the elements are initialize to numeric 0.
 * 
 * If index > 0, then the first index dimensions are ignored. For example, index =
 * 1 returns new [[T[dim[1]][dim[2]] This way, we can employ the method
 * recursively.
 */
dragomeJs.newArray = function(classSignature, dim, index) {
	if (index == null)
		index = 0;
	var subSignature = classSignature.substr(index);
	var dimensionAtIndex = dim[index];

	var array = new Array(dimensionAtIndex);
	array.$clone$java_lang_Object = java_lang_Object.prototype.$clone$java_lang_Object;
	array.classSignature = classSignature;
	array.__proto__.$getClass$java_lang_Class = function() {
		var clazz = java_lang_Class
				.$forName___java_lang_String$java_lang_Class(this.classSignature);
		return clazz;
	};

	if (subSignature == "Z") {
		for (var i = 0; i < dimensionAtIndex; i++) {
			array[i] = false;
		}
	} else if (subSignature.charAt(1) != "[" && subSignature.charAt(1) != "L") {
		for (var i = 0; i < dimensionAtIndex; i++) {
			array[i] = 0;
		}
	} else {
		// Component type is a reference (array or object type).
		if (index + 1 < dim.length) {
			for (var i = 0; i < dimensionAtIndex; i++) {
				array[i] = dragomeJs.newArray(classSignature, dim, index + 1);
			}
		}
	}

	return array;
};

/**
 * Returns a shallow clone of the specified array. This method is used in
 * java.lang.Object#clone()java.lang.Object
 */
dragomeJs.cloneArray = function(other) {
	var dim = other.length;
	var array = new Array(dim);
	array.clazz = other.clazz;
	for (var i = 0; i < dim; i++) {
		array[i] = other[i];
	}
	return array;
};

// Returns all attributes of the specified object as a string.
dragomeJs.inspect = function(object) {
	var s = "Value " + String(object);

	if (object == null || object == undefined)
		return "null";

	if (typeof (object) == "string")
		return object;

	var attributes = new Array();
	for ( var e in object) {
		attributes[attributes.length] = e;
	}

	if (attributes.length > 0) {
		attributes.sort();
		s += "\n\tAttributes:\n";
		for ( var e in attributes) {
			var attribute = attributes[e];
			var value = "";
			try {
				value = object[attribute];
			} catch (e) {
				value += "While fetching attribute: " + e.message;
			}
			var type = typeof (value);
			if (type == "function") {
				s += "\t" + attribute + "[" + type + "]\n";
			} else {
				s += "\t" + attribute + "[" + type + "]: " + value + "\n";
			}
		}
	}

	return s;
};

dragomeJs.unquote = function(s) {
	if (s == null || s.length < 2) {
		return s;
	}

	var r = "";
	for (var i = 0; i < s.length; i++) {
		var c = s.charAt(i);
		if (c == '\\' && i < s.length - 1) {
			c = s.charAt(++i);
		}
		r += c;
	}
	return r;
};

dragomeJs.checkCast = function(obj, className) {
	if (!className.basename || className == java_lang_Object || obj == null)
		return obj;

	if (typeof obj == "string")
		if (className == java_lang_CharSequence
				|| className == java_lang_Comparable)
			return obj;

	// if (className == java_lang_Boolean && (obj == 1 || obj == 0))
	// return obj;

	if (className == java_lang_Class && obj.$$type == "Class")
		return obj;

	if (obj.classname && obj.classname.startsWith("ProxyOf_"))
		return obj;

	if (!dragomeJs.isInstanceof(obj, className)) {
		var cn = obj.classname;
		if (typeof obj == "string")
			cn = "java_lang_String";

		throw dragomeJs.createException("java.lang.RuntimeException",
				"Cannot cast " + cn + " to " + className.basename);
	}

	return obj;
};

function createProxyOf(types, methods, handler1, handler) {
	var membersMap = {};
	membersMap.$$$handler___java_lang_reflect_InvocationHandler = handler1;

	var createInvoker = function(method) {
		return function() {
			return handler
					.$invoke___java_lang_Object__java_lang_reflect_Method__java_lang_Object_ARRAYTYPE$java_lang_Object(
							this, method, arguments);
		};
	}

	for ( var i in methods.$$$array___java_lang_Object_ARRAYTYPE) {
		var methodName = methods.$$$array___java_lang_Object_ARRAYTYPE[i].$$$signature___java_lang_String;
		membersMap[methodName] = createInvoker(methods.$$$array___java_lang_Object_ARRAYTYPE[i]);
	}

	var nativeTypes = new Array(types.length);
	for ( var i in types)
		nativeTypes[i] = types[i].$$$nativeClass___java_lang_Object;

	var nextNumber = objectId({});

	qx.Class.define("ProxyOf_" + nextNumber, {
		extend : java_lang_Object,
		implement : nativeTypes,
		construct : function() {
		},
		members : membersMap
	});

	return eval("new ProxyOf_" + nextNumber + "()");
};

dragomeJs.addNativeMethod = function(signature, method) {
	dragomeJs.nativeMethods[signature] = method;
};

dragomeJs.resolveNativeMethod = function(owner, signature) {
	var instance = java_lang_Class.$forName___java_lang_String$java_lang_Class(
			(owner.classname.substring(0, owner.classname.lastIndexOf("_") + 1)
					+ "Delegate" + owner.classname.substring(owner.classname
					.lastIndexOf("_") + 1)).replaceAll("_", "."))
			.$newInstance$java_lang_Object()[signature];
	return instance;
	// return dragomeJs.nativeMethods[signature];
};

dragomeJs.resolveMethod = function(owner, signature) {
	var clazz = java_lang_Class
			.$forName___java_lang_String$java_lang_Class(owner);
	var method = clazz.getMethodBySignature(signature);
	return method;
};

dragomeJs.castTo = function(instance, className) {
	var clazz = java_lang_Class
			.$forName___java_lang_String$java_lang_Class(className);
	return dragomeJs.castTo2(instance, clazz);
};

dragomeJs.castTo2 = function(instance, clazz) {
	return com_dragome_web_enhancers_jsdelegate_JsCast
			.$castTo___java_lang_Object__java_lang_Class$java_lang_Object(
					instance, clazz);
};
