# ![TodoMVC](logo.png)

# TodoMVC Dragome implementation

Dragome TodoMVC implementation is a new framework choice contribution for [TodoMVC project] (http://todomvc.com/).
It was built using component builder, which uses several tools inside involved to construction and binding models:
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

# ![TodoMVC Example](http://todomvc.com/site-assets/screenshot.png)


## Configuration

There some expressions like the following, where component style is depending on the value of editedTodo that is a member of todoManager.
``` Java
	builder.styleWith("editing").when(() -> todo == todoManager.getEditedTodo());
``` 
This dependency must be updated each time editedTodo is changed to reflect the corresponding style in component. In order to configure the SDK to detects changes over the model, todoManager model in this particular case, we need to configure which classes or packages must be instrumented by "method logger plugin" to intercept all modifications applied to models, those that are executed using setters.
To do that we need to create a class that extends ChainedInstrumentationDragomeConfigurator initializing parent with a MethodLoggerConfigurator. We are constructing it with the package of Todo class to include both models (Todo and TodoManager) in instrumentation.

``` Java
@DragomeConfiguratorImplementor
public class TodoMVCApplicationConfigurator extends ChainedInstrumentationDragomeConfigurator
{
    public TodoMVCApplicationConfigurator()
    {
	    MethodLoggerConfigurator methodLoggerConfigurator= new MethodLoggerConfigurator(Todo.class.getPackage().getName());
	    init(methodLoggerConfigurator);
    }
}
``` 

That's all, whenever any setter of Todo or TodoManager models is called, the binding mechanism will detect it and fire an event to check if the value actually changed, and if it does it will execute the expression again to reflect the corresponding style in component.



## Templates
Html template is just the same template provided by TodoMVC with some "data-template" attributes added for identifying templates parts. It's completly logicless and has minimal changes over the original file, which makes it interchangeable using the same logic over any of them because logic is stored in java files (not in templates). 


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
					<li class="dragome-hide" data-template="completed-todo">
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


## Models

### Todo item

``` Java
public class Todo
{
	private String title= "";
	private boolean completed;

	public Todo()
	{
	}

	public Todo(Todo todo)
	{
		setTitle(todo.getTitle());
		setCompleted(todo.isCompleted());
	}

	public Todo(String title, boolean done)
	{
		this.title= title;
		this.completed= done;
	}

	public boolean isCompleted()
	{
		return completed;
	}

	public void setCompleted(boolean done)
	{
		this.completed= done;
	}

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title= title;
	}
}
``` 

### Todos container

``` Java
public class TodoManager
{
    private List<Todo> todos= new ArrayList<Todo>();
    private String newTodo= "";
    private Todo editedTodo;
    private String location= "/";
    private Todo originalTodo;
    private Tester<Todo> statusFilter;
    private boolean allChecked;
    private long completedCount;
    private long remainingCount;
    private LocalStorage localStorage;
    
    public TodoManager()
    {
    }
    
    public TodoManager(String location, LocalStorage localStorage)
    {
        this.localStorage= localStorage;
        setTodos(localStorage.load("todos-dragome"));
        setLocation(location);
    }
    
    public void addTodo()
    {
        String tempNewTodo= getNewTodo().trim();
        if (tempNewTodo.length() == 0)
        return;
        
        getTodos().add(new Todo(tempNewTodo, false));
        setNewTodo("");
        
        update();
    }
    
    private void update()
    {
        calculate();
        setTodos(getTodos());
    }
    
    private void calculate()
    {
        if (getTodos() != null)
        {
            setRemainingCount(getTodos().stream().filter(t -> !t.isCompleted()).count());
            setCompletedCount(getTodos().size() - remainingCount);
            allChecked= remainingCount == 0;
        }
    }
    public void clearCompletedTodos()
    {
        setTodos(getTodos().stream().filter(t -> !t.isCompleted()).collect(Collectors.toList()));
        update();
    }
    
    public void doneEditing(Todo todo)
    {
        setEditedTodo(null);
        todo.setTitle(todo.getTitle().trim());
        
        if (todo.getTitle().length() == 0)
        removeTodo(todo);
    }
    public void editTodo(Todo todo)
    {
        setEditedTodo(todo);
        originalTodo= new Todo(todo);
        setTodos(todos);
    }
    public long getCompletedCount()
    {
        return completedCount;
    }
    public Todo getEditedTodo()
    {
        return editedTodo;
    }
    public String getLocation()
    {
        return location;
    }
    
    public String getNewTodo()
    {
        return newTodo;
    }
    
    public Todo getOriginalTodo()
    {
        return originalTodo;
    }
    
    public long getRemainingCount()
    {
        return remainingCount;
    }
    
    public Tester<Todo> getStatusFilter()
    {
        return statusFilter;
    }
    
    public List<Todo> getTodos()
    {
        return todos;
    }
    
    public boolean isAllChecked()
    {
        return allChecked;
    }
    
    public void markAll(boolean completed)
    {
        getTodos().stream().forEach(t -> t.setCompleted(completed));
    }
    
    public void removeTodo(Todo todo)
    {
        getTodos().remove(todo);
        update();
    }
    
    public void revertEditing(Todo todo)
    {
        getTodos().set(getTodos().indexOf(todo), originalTodo);
        doneEditing(originalTodo);
    }
    
    public void setAllChecked(boolean allChecked)
    {
        this.allChecked= allChecked;
        update();
    }
    
    public void setCompletedCount(long completedCount)
    {
        this.completedCount= completedCount;
        setTodos(todos);
    }
    
    public void setEditedTodo(Todo editedTodo)
    {
        this.editedTodo= editedTodo;
    }
    
    public void setLocation(String location)
    {
        this.location= location != null ? location : "/";
        
        if (this.location.equals("/"))
        setStatusFilter(t -> true);
        else if (this.location.equals("/active"))
        setStatusFilter(t -> !t.isCompleted());
        else if (this.location.equals("/completed"))
        setStatusFilter(t -> t.isCompleted());
        
        update();
    }
    
    public void setNewTodo(String newTodo)
    {
        this.newTodo= newTodo;
    }
    
    public void setOriginalTodo(Todo originalTodo)
    {
        this.originalTodo= originalTodo;
    }
    
    public void setRemainingCount(long remainingCount)
    {
        this.remainingCount= remainingCount;
    }
    
    public void setStatusFilter(Tester<Todo> statusFilter)
    {
        this.statusFilter= statusFilter;
        update();
    }
    
    public void setTodos(List<Todo> todos)
    {
        if (todos == null)
        todos= new ArrayList<Todo>();
        
        this.todos= todos;
        localStorage.save("todos-dragome", todos);
    }
    
    public void todoCompleted(Todo todo)
    {
        int i= todo.isCompleted() ? -1 : 1;
        setRemainingCount(remainingCount + i);
        setCompletedCount(completedCount + (i * -1));
    }
}
``` 

