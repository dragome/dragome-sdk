#Crud using builders


``` Java
public class PersonCrudUsingBuilders extends DragomeVisualActivity
{
	List<Person> persons= new ListWrapper<Person>(new ArrayList<Person>());

	public void build()
	{
		PersonService personService= serviceFactory.createSyncService(PersonService.class);
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
