#Template Engine

HTML templates are based in pure HTML files.  
They are logic-less, and they are not required to be XHTML or well formed HTML, any HTML will work if it can be parsed by a browser.  
Also no special tags are required, and sub templates (placeholders) are identified using custom data attribute "data-template" which is a standard mechanism.  

The idea behind this template aproach is to completely separate both worlds: graphic design and developers world. This decoupled mechanism allows us to get significant productivity improvements.  

*_This is a basic proposed workflow for these roles interaction:_*

1. Graphic designer creates a first version of static HTML pages 
2. Developer adds all data-template attributes to important elements
3. Developer sends modified HTML pages back to designer, the designer will be able to edit it in same way he did before because only standard attributes were added.
4. While developer continues creating components based on those HTML pages, the designer continues improving the design of HTML pages in parallel, making changes over the static content but taking into account of preserving "data-template" attributes. 
5. Each time there is a new version of static content provided by designer it will be integrated in the application just updating html files to this version, no modifications to source code will be required. In fact any role will be able to update this UI changes just changing HTML files.


###This is how it works: 
For each element containing data-template attribute, template engine creates an instance of Template class following the same structure of HTML.
``` HTML
<html>
<body>
	<table data-template="parent">
		<tr>
			<td data-template="first-child">first</td>
			<td data-template="second-child">second</td>
		</tr>
	</table>
</body>
</html>
```
With this HTML, Template Engine will create a Template instance with name "parent", containing two children that are also instances of Template called "first-child" and "second-child":
``` Java
Template parent= getTemplate("parent");
Template firstChild= parent.getChild("first-child");
Template secondChild= parent.getChild("second-child");
```
In most cases you can create new components just combining existing ones, but in case you need to create a low level component that requires a particular HTML rendering you can access directly to DOM.  
For that, "Template.getContent" method will return this associated element, the one marked with "data-template" attribute.  
Dragome use standard W3C objects to represent all HTML related stuff.
``` Java
org.w3c.dom.Element element= parent.getContent();
element.setAttribute("class", "rounded-table");
```

##DragomeVisualActivity

When you create a new page extending DragomeVisualActivity, it will inherit an already created template called *mainTemplate*, that is built using the body of current HTML page. This is a particular template creation for simplifying page creation and handling.

Loading an external template, those that are present in other static HTML pages, is possible using *templateHandlingStrategy* inherit member.

**From TreeDemoPage example**
``` Java
Template temp1= templateHandlingStrategy.loadTemplate("tree-demo", "tree-skin");
```

And you can find subtemplates by path using:

``` Java
Template rootTemplate= TemplateImpl.getTemplateElementInDepth(temp1, "panel.tree-root");
```




		



