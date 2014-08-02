# TodoMVC in Dragome

Dragome TodoMVC implementation is a new framework choice contribution for [TodoMVC project] (http://todomvc.com/).
It was built using component builders, inside builders there are several tools involved to achieve the construction and binding:
- Dragome visual componentes
- Form bindings framework (based on [GWT Pectin](https://code.google.com/p/gwt-pectin/))
- MethodLogger instrumentation for detecting getter and setter executions.

The following is the complete source code for UI construction.

``` Java
public class TodosPage extends DragomeVisualActivity
{
	public void build()
	{
		TodoManager todoManager= new TodoManager(ServiceLocator.getInstance().getParametersHandler().getFragment(), new LocalStorage());
		ComponentBuilder<TodoManager> componentBuilder= new ComponentBuilder<TodoManager>(mainPanel, todoManager);

		componentBuilder.bindTemplate("new-todo").as(VisualTextField.class).toProperty(TodoManager::getNewTodo, TodoManager::setNewTodo).onKeyUp((v, c) -> todoManager.addTodo(), KEY_ENTER).build();
		ComponentBuilder<TodoManager> mainSectionBuilder= componentBuilder.bindTemplate("main-section").as(VisualPanel.class).childBuilder();
		VisualCheckbox allChecked= mainSectionBuilder.bindTemplate("toggle-all").as(VisualCheckbox.class).toProperty(TodoManager::isAllChecked, TodoManager::setAllChecked).onClick(v -> todoManager.markAll(!todoManager.isAllChecked())).build();
		mainSectionBuilder.show(allChecked).when(() -> !todoManager.getTodos().isEmpty());
		mainSectionBuilder.showWhen(() -> !todoManager.getTodos().isEmpty());

		mainSectionBuilder.bindTemplate("completed-todo").as(VisualPanel.class).toListProperty(TodoManager::getTodos).filter(TodoManager::getStatusFilter).repeat((todo, builder) -> {
			builder.bindTemplate("todo-input").as(VisualTextField.class).toProperty(Todo::getTitle, Todo::setTitle).onKeyUp((v, c) -> todoManager.doneEditing(todo), KEY_ESC, KEY_ENTER).onBlur(v -> todoManager.doneEditing(todo)).build();
			builder.bindTemplate("title").as(VisualLabel.class).toProperty(Todo::getTitle, Todo::setTitle).onDoubleClick(v -> todoManager.editTodo(todo)).build();
			builder.bindTemplate("completed").as(VisualCheckbox.class).toProperty(Todo::isCompleted, Todo::setCompleted).onClick(v -> todoManager.todoCompleted(todo)).build();
			builder.bindTemplate("destroy").as(VisualButton.class).onClick(v -> todoManager.removeTodo(todo)).build();
			builder.styleWith("completed").when(todo::isCompleted);
			builder.styleWith("editing").when(() -> todo == todoManager.getEditedTodo());
		});

		ComponentBuilder<TodoManager> footerBuilder= componentBuilder.bindTemplate("footer-section").as(VisualPanel.class).childBuilder();
		footerBuilder.showWhen(() -> !todoManager.getTodos().isEmpty());
		footerBuilder.bindTemplate("items-count").as(VisualLabel.class).toProperty(TodoManager::getRemainingCount, TodoManager::setRemainingCount).build();
		footerBuilder.bindTemplate("items-label").as(VisualLabel.class).to(() -> todoManager.getRemainingCount() == 1 ? "item" : "items").build();

		Stream.of("/", "/active", "/completed").forEach(location -> {
			VisualLink link= footerBuilder.bindTemplate("filter:" + location).as(VisualLink.class).onClick(v1 -> todoManager.setLocation(location)).build();
			footerBuilder.style(link).with("selected").when(() -> todoManager.getLocation().equals(location));
		});

		ComponentBuilder<TodoManager> clearCompletedBuilder= footerBuilder.bindTemplate("clear-completed").as(VisualPanel.class).onClick(v2 -> todoManager.clearCompletedTodos()).childBuilder();
		clearCompletedBuilder.bindTemplate("clear-completed-number").as(VisualLabel.class).toProperty(TodoManager::getCompletedCount, TodoManager::setCompletedCount).build();
		clearCompletedBuilder.showWhen(() -> todoManager.getCompletedCount() > 0);
	}
}
```

Html template is just the same template provided by TodoMVC with some "data-template" attributes added for identifying templates parts. It's completly logicless and has the mentioned minimal changes over the original file, which makes it interchangeable using the same logic over any of them because logic is stored in java file. 


``` HTML
<html lang="en">
	<head>
		<meta charset="utf-8">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<meta http-equiv="Pragma" content="no-cache">
		<title>Template • TodoMVC</title>
		<link rel="stylesheet" href="bower_components/todomvc-common/base.css">
		<link rel="stylesheet" href="css/app.css">
		<link rel="stylesheet" href="dragome/dragome.css">
		<script type="text/javascript" src="dragome/dragome.js"></script>
	</head>
	<body>
		<section id="todoapp"> 
			<header id="header">
				<h1>todos</h1>
				<input id="new-todo" data-template="new-todo" placeholder="What needs to be done?" autofocus>
			</header>
			<section id="main" data-template="main-section">
				<input id="toggle-all" type="checkbox" data-template="toggle-all">
				<label for="toggle-all">Mark all as complete</label>
				<ul id="todo-list">
					<li class="to-be-removed" data-template="completed-todo">
						<div class="view">
							<input  data-template="completed" class="toggle" type="checkbox" checked>
							<label data-template="title">Create a TodoMVC template</label>
							<button class="destroy"  data-template="destroy"></button>
						</div>
						<input class="edit" value="Create a TodoMVC template" data-template="todo-input">
					</li>
				</ul>
			</section>
			<footer id="footer" data-template="footer-section">
				<span id="todo-count"><strong  data-template="items-count">1</strong> <span  data-template="items-label">item</span> left</span>
				<ul id="filters">
					<li>
						<a class="selected" href="#/" data-template="filter:/">All</a>
					</li>
					<li>
						<a href="#/active" data-template="filter:/active">Active</a>
					</li>
					<li>
						<a href="#/completed" data-template="filter:/completed">Completed</a>
					</li>
				</ul>
				<button id="clear-completed"  data-template="clear-completed">Clear completed (<span  data-template="clear-completed-number">item</span>)</button>
			</footer>
		</section>
		<footer id="info">
			<p>Double-click to edit a todo</p>
			<p>Created by <a href="http://www.dragome.com">Fernando Petrola</a></p>
			<p>Part of <a href="http://todomvc.com">TodoMVC</a></p>
		</footer>
		<script src="bower_components/todomvc-common/base.js"></script>
		<script src="js/app.js"></script>
	</body>
</html>
```


