# Dragome status: current version 0.95.1-beta1

 1. JRE js implementation is supporting the entire framework features but it's not 100% implemented yet. For example running JRuby, Jython or Scala applications is not allowed yet but it could be achieved in a near future.
 2. Reflection API can be used for method invocations, constructing instances, calling dynamic Proxies, but also needs to be improved by js JRE implementation. There are several features that cannot be executed in js by now, for example: field access, introspection, serialization, complex annotations handling, non default constructors invocations..
 3. Component builders API needs to be enhanced in order to include most of GWT pectin features as a facade.
 4. Basic UI components are available but there's a lack of complex components, such as complete TreeView, Grid, Table with sorting, Menu pull down, etc.
 5. Java 8 lambda expressions are fully supported, Stream API can be used for some basic operations. Lazy evaluation optimization is available but no optimization for parallel (may be it could be achieved using web workers).
 6. Generated js contains all compiled code, too heavy.. Need to be split in several files to be able to cache each one. Also closure js optimizer cannot shrink the file by now, generated code should be changed to allow it. 
 7. Debugging is working for: Chrome, Firefox and Opera. IE and Safari in a near future.

