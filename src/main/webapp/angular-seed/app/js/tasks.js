function Task(name, propertiesDef) {
    this.name = name
    this.subTasks = []
    this.observers = []
    this.level = 0
    this.properties = {}
    this.propertiesDef = propertiesDef
}

Task.prototype.getPropertiesDef = function() {
    return this.propertiesDef
}

Task.prototype.getPropertyValue = function(x) {
    if (this.subTasks.length >= 1) {
        var sum = 0, i
			
        for(i in this.subTasks) {
            sum += this.subTasks[i].getPropertyValue(x)
        }
			
        return sum    
    } else {
        return this.properties[x] ? this.properties[x] : 0
    }
}

Task.prototype.setPropertyValue = function(x, value) {
    this.properties[x] = value
    
    this.notify()
}
 
	
Task.prototype.getName = function() {
    return this.name
}

Task.prototype.setName = function(name) {
    this.name = name;
    
    this.notify()    
}
	
Task.prototype.addSubTask = function(name) {
    var subTask = new Task(name, this.propertiesDef)
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
    
    if (this.parent) {
        this.parent.notify()
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

Task.prototype.getTotalSubTasks = function() {
    if (this.isLeaf()) {
        return 0
    } else {
        var sum = 0
        for(var task in this.subTasks) {
            sum += 1 + this.subTasks[task].getTotalSubTasks()
        }
    
        return sum
    }
}

Task.prototype.getTaskId = function() {
    return this.taskId
}

Task.prototype.setTaskId = function(taskId) {
    this.taskId = taskId
}

Task.prototype.getProperties = function() {    
    var result = [], p
    for(p in this.propertiesDef) {
        result.push({
            name: this.propertiesDef[p], 
            value: this.getPropertyValue(this.propertiesDef[p])
        })
    }
    
    return result
}

function TaskFactory() {
    
}

TaskFactory.prototype.createTasksFromJson = function(jsonData) {
    var propertiesDef = ['effort']
    
    var addSubTasks = function(parent, subTasksJsonData) {
        var i
        for(i in subTasksJsonData) {
            var child = parent.addSubTask(subTasksJsonData[i].name)
            child.setTaskId(subTasksJsonData[i].taskId)
            
            if (subTasksJsonData[i].subTasks.length > 0) {
                addSubTasks(child, subTasksJsonData[i].subTasks)
            } else {
                child.setPropertyValue('effort', subTasksJsonData[i].effort)
            }
        }
    }
    
    var root = new Task(jsonData.name, propertiesDef)
    root.setTaskId(jsonData.taskId)
    
    addSubTasks(root, jsonData.subTasks)
    
    return root
}
