package flexjson;

public interface ClassLocator
{
	public Class locate(ObjectBinder context, Path currentPath) throws ClassNotFoundException;
}
