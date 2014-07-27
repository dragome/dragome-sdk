package javax.script;

public class ScriptEngineManager
{

	public ScriptEngine getEngineByName(String engineName)
	{
		if (!"JavaScript".equals(engineName))
		{
			throw new IllegalArgumentException("Script of type " + engineName + " not supported");
		}
		return new ScriptEngineImpl();
	}
}
