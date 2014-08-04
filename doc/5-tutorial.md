
#5' Dragome Tutorial

Let's create a simple crud using Dragome.  
**Remember that all we need is pure Java code and pure static HTML files, then Dragome will transform it all to Javascript and dynamic HTML pages automatically.**


We will make use of some tools provided by the SDK: 
* service creation
* visual components
* model binding
* template engine
* template repeaters 

First of all we create a simple activity for building our page extending DragomeVisualActivity:
``` Java
public class PersonCrudPage extends DragomeVisualActivity
```
We will use a service located at server side to execute crud actions such as getPersons and savePersons.  
A proxy to the service is obtained using a service factory (see [Services](services.md) for more info), passing PersonService interface as parameter:
``` Java
PersonService personService= serviceFactory.createSyncService(PersonService.class);
```
A HTML template will be used to display each person data.  
So we need to repeat each subtemplate called "row" and locate all person content there, including the button for deleting the item.
Template engine will generate a nested sub template for each element using "data-template" attribute, you can use example data inside it to preview template as static HTML pages, it will be removed in template instantiation. For more info: [Template Engine](template-engine.md) 
``` HTML
<tr data-template="row">
	<td><input type="text" data-template="givenName">Juan</input></td>
	<td><input type="text" data-template="surname">Perez</input></td>
	<td><select data-template="nickname">JP</select></td>
	<td><button data-template="delete-button">delete</button></td>
</tr>
```

Java
``` Java
List<Person> somePersons= personService.getPersons();
new TemplateRepeater<Person>(somePersons, mainTemplate, "row", this::fillTemplate);
```
Note that mainTemplate is provided as member of DragomeVisualActivity which contains the body of current HTML.  
FillTemplate method will be called for each item in persons list, and will be in charge of creating a component for each person field, binding all data, and creating the delete button.  
``` Java
public void fillTemplate(final Person person, Template itemTemplate)
{
	final VisualPanel rowPanel= new VisualPanelImpl(itemTemplate);
	mainPanel.addChild(rowPanel);
	
	rowPanel.addChild(new VisualButtonImpl("delete-button", v -> {
		persons.remove(person);
		rowPanel.getParent().removeChild(rowPanel);
	}));
	
	ModelBinder<Person> modelBinder= new ModelBinder<Person>(person, rowPanel);
	modelBinder.bindToPanel(new VisualTextFieldImpl<String>("givenName"));
	modelBinder.bindToPanel(new VisualTextFieldImpl<String>("surname"));
	modelBinder.bindToPanel(new VisualComboBoxImpl<String>("nickname", Arrays.asList("Pelusa", "Burrito", "Bocha", "Bruja")));
}
```
ModelBinder will bind each component to person field that shares the same name, and will add each component to rowPanel.  
As rowPanel is constructed using row template, each component will be placed at the corresponding placeholder with the same name.

Finally we create buttons for saving and adding new persons to the list.


__Ready!!__

This is the final source code for this person CRUD:
``` Java
public class PersonCrudPage extends DragomeVisualActivity
{
	PersonService personService= serviceFactory.createSyncService(PersonService.class);
	List<Person> persons= new ArrayList<Person>();

	public void build()
	{
		mainPanel.addChild(new VisualButtonImpl("save-button", v -> personService.savePersons(persons)));
		mainPanel.addChild(new VisualButtonImpl("add-button", v -> showPersons(Arrays.asList(new Person()))));

		showPersons(personService.getPersons());
	}

	private void showPersons(final List<Person> somePersons)
	{
		persons.addAll(somePersons);
		new TemplateRepeater<Person>(somePersons, mainTemplate, "row", this::fillTemplate);
	}

	public void fillTemplate(final Person person, Template itemTemplate)
	{
		final VisualPanel rowPanel= new VisualPanelImpl(itemTemplate);
		mainPanel.addChild(rowPanel);

		rowPanel.addChild(new VisualButtonImpl("delete-button", v -> {
			persons.remove(person);
			rowPanel.getParent().removeChild(rowPanel);
		}));

		ModelBinder<Person> modelBinder= new ModelBinder<Person>(person, rowPanel);
		modelBinder.bindToPanel(new VisualTextFieldImpl<String>("givenName"));
		modelBinder.bindToPanel(new VisualTextFieldImpl<String>("surname"));
		modelBinder.bindToPanel(new VisualComboBoxImpl<String>("nickname", Arrays.asList("Pelusa", "Burrito", "Bocha", "Bruja")));
	}
}
```

And this is the complete HTML template:
``` HTML
<html>
<head>
<script type="text/javascript" src="dragome/dragome.js"></script>
</head>

<body>
	<h2>List of persons</h2>
	<button data-template="add-button"></button>
	<button data-template="save-button"></button>
	<table>
		<thead>
			<tr>
				<th>Name</th>
				<th>Last name</th>
				<th>Nickname</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<tr data-template="row">
				<td><span data-template="givenName">Juan</span></td>
				<td><span data-template="surname">Perez</span></td>
				<td><span data-template="nickname">JP</span></td>
				<td><button data-template="delete-button">delete</button></td>
			</tr>
		</tbody>
	</table>
</body>

</html>
```






