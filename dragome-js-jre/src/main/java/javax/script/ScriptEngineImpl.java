package javax.script;

class ScriptEngineImpl implements ScriptEngine
{
	public native Object eval(String script);
	public native void put(String key, Object value);
}
