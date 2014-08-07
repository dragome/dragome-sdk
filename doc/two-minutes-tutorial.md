
#2' Dragome Tutorial

Let's create a simple crud using Dragome.  
**Remember that all we need is pure Java code and pure static HTML files, then Dragome will transform it all to Javascript and dynamic HTML pages automatically.**


We will make use of some tools provided by the SDK: 
* service creation
* component builders

First of all we create a simple activity for building our page extending DragomeVisualActivity:
``` Java
public class PersonCrudUsingBuilders extends DragomeVisualActivity
```
We will use a service located at server side to execute crud actions such as getPersons and savePersons.  
A proxy to the service is obtained using a service factory (see [Services](services.md) for more info), passing PersonService interface as parameter:
``` Java
PersonService personService= serviceFactory.createSyncService(PersonService.class);
```

We have two static components which are save and add buttons, we will use a component builder for their creation. Attaching 
corresponding click listener to each one with lambda expressions.

``` Java
ComponentBuilder<PersonCrudUsingBuilders> componentBuilder= new ComponentBuilder<PersonCrudUsingBuilders>(mainPanel, this);
componentBuilder.bindTemplate("save-button").as(VisualButton.class).onClick(() -> personService.savePersons(persons)).build();
componentBuilder.bindTemplate("add-button").as(VisualButton.class).onClick(() -> persons.add(new Person())).build();
```

Note that mainTemplate is provided as member of DragomeVisualActivity which contains the body of current HTML. For more info: [Template Engine](template-engine.md)   
Then we repeat each element of persons list using "row" template, and creating one component for each property using binding capabilities.

``` Java
componentBuilder.bindTemplate("row").as(VisualPanel.class).toList(persons).repeat((person, b) -> {
    b.bindTemplate("givenName").as(VisualTextField.class).toProperty(Person::getGivenName, Person::setGivenName).build();
	b.bindTemplate("surname").as(VisualTextField.class).toProperty(Person::getSurname, Person::setSurname).build();
	b.bindTemplate("complete-name").as(VisualLabel.class).to(() -> person.getGivenName() + " " + person.getSurname()).build();
	b.bindTemplate("nickname").to(new VisualComboBoxImpl<String>("nickname", Arrays.asList("Pelusa", "Burrito", "Bocha", "Bruja"))).toProperty(Person::getNickname, Person::setNickname).build();
	b.bindTemplate("delete-button").as(VisualButton.class).onClick(() -> persons.remove(person)).build();
});
```

**Ready!**

This is the complete Java code:
``` Java
public class PersonCrudUsingBuilders extends DragomeVisualActivity
{
    PersonService personService= serviceFactory.createSyncService(PersonService.class);
    List<Person> persons= new ListWrapper<Person>(new ArrayList<Person>());

	public void build()
	{
		persons.addAll(personService.getPersons());
		ComponentBuilder<PersonCrudUsingBuilders> componentBuilder= new ComponentBuilder<PersonCrudUsingBuilders>(mainPanel, this);
		componentBuilder.bindTemplate("save-button").as(VisualButton.class).onClick(() -> personService.savePersons(persons)).build();
		componentBuilder.bindTemplate("add-button").as(VisualButton.class).onClick(() -> persons.add(new Person())).build();

		componentBuilder.bindTemplate("row").as(VisualPanel.class).toList(persons).repeat((person, b) -> {
			b.bindTemplate("givenName").as(VisualTextField.class).toProperty(Person::getGivenName, Person::setGivenName).build();
			b.bindTemplate("surname").as(VisualTextField.class).toProperty(Person::getSurname, Person::setSurname).build();
			b.bindTemplate("complete-name").as(VisualLabel.class).to(() -> person.getGivenName() + " " + person.getSurname()).build();
			b.bindTemplate("nickname").to(new VisualComboBoxImpl<String>("nickname", Arrays.asList("Pelusa", "Burrito", "Bocha", "Bruja"))).toProperty(Person::getNickname, Person::setNickname).build();
			b.bindTemplate("delete-button").as(VisualButton.class).onClick(() -> persons.remove(person)).build();
		});
	}
}
```

HTML template:
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
				<th>Complete name</th>
				<th>Nickname</th>
				<th></th>
			</tr>
		</thead>
		<tbody>
			<tr data-template="row">
				<td><input type="text" data-template="givenName" value="Juan"></input></td>
				<td><input type="text" data-template="surname" value="Perez"></input></td>
				<td><span  data-template="complete-name">Juan Perez</span></td>
				<td><select data-template="nickname">JP</select></td>
				<td><button type="submit" data-template="delete-button"></button></td>
			</tr>
		</tbody>
	</table>
</body>

</html>
```






