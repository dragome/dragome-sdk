package javax.script;

public interface ScriptEngine
{
	public Object eval(String script);
	public void put(String key, Object value);
}
