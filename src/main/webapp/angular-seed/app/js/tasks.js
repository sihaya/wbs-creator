function Task(name) {
    this.name = name
    this.subTasks = []
    this.observers = []
    this.level = 0
}
	
Task.prototype.getName = function() {
    return this.name
}

Task.prototype.setName = function(name) {
    this.name = name;
    
    this.notify()
}
	
Task.prototype.getEffort = function() {
    if (this.subTasks.length > 0) {
        var sum = 0, i
			
        for(i in this.subTasks) {
            sum += this.subTasks[i].getEffort()
        }
			
        return sum
    }
    else {
        return this.effort
    }
}
	
Task.prototype.setEffort = function(effort) {
    this.effort = effort
    
    this.notify()
}
	
Task.prototype.addSubTask = function(name) {
    var subTask = new Task(name)
    subTask.level = this.level + 1
    subTask.parent = this
    
    this.subTasks.push(subTask)

    this.notify()

    return subTask
}

Task.prototype.isLeaf = function() {
    return this.subTasks.length == 0
}

Task.prototype.getSubTasks = function() {
    return this.subTasks
}

Task.prototype.addObserver = function(observer) {
    this.observers.push(observer)
}

Task.prototype.notify = function() {
    var i
    for(i in this.observers) {
        this.observers[i].taskUpdated(this)
    }
}

Task.prototype.getSibblingIndex = function() {
    return this.parent.subTasks.indexOf(this)
}

Task.prototype.getAmountOfSibblings = function() {
    return this.parent.subTasks.length
}

Task.prototype.getParent = function() {
    return this.parent
}

Task.prototype.getLevel = function() {
    return this.level
}

function TaskFactory() {
    
}

TaskFactory.prototype.createTasksFromJson = function(jsonData) {
    var addSubTasks = function(parent, subTasksJsonData) {
        var i
        for(i in subTasksJsonData) {
            var child = parent.addSubTask(subTasksJsonData[i].name)
            
            if (subTasksJsonData[i].subTasks.length > 0) {
                addSubTasks(child, subTasksJsonData[i].subTasks)
            } else {
                child.setEffort(subTasksJsonData[i].effort)
            }
        }
    }
    
    var root = new Task(jsonData.name)
    
    addSubTasks(root, jsonData.subTasks)
    
    return root
}