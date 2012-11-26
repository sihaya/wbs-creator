function TaskDisplay(paper, task, onAddTask, onEditTask) {
    this.task = task
    this.paper = paper
    this.children = []
    this.onAddTask = onAddTask
    this.onEditTask = onEditTask
    
    task.addObserver(this)
}

TaskDisplay.TASK_WIDTH = 100
TaskDisplay.TASK_HEIGHT = 35
TaskDisplay.TASK_OFFSET = 30

TaskDisplay.prototype.clear = function() {
    if (this.rect) {
        this.rect.remove()
    }
    
    if (this.txt) {
        this.txt.remove()
    }
    
    if (this.path) {
        this.path.remove()
    }
}

TaskDisplay.prototype.draw = function() {    
    this.clear()
    
    var level = this.task.getLevel()
    
    if (level == 0) {
        this.x = this.paper.width / 2
        this.y = 30
    } else if (level == 1) {
        var widthPTask = this.paper.width / this.task.getAmountOfSibblings()
        
        this.x = widthPTask * this.task.getSibblingIndex() + widthPTask / 2
        this.y = 2 * TaskDisplay.TASK_HEIGHT + TaskDisplay.TASK_OFFSET
    } else {        
        this.x = this.parent.x + TaskDisplay.TASK_OFFSET
        this.y = 40 * (this.getGraphSibblingIndex() + 1) + this.parent.y
    } 
    
    var child
    for(child in this.children) {
        this.children[child].draw()
    }
    
    var rect = this.paper.rect(this.x, this.y, TaskDisplay.TASK_WIDTH, TaskDisplay.TASK_HEIGHT, 10)
    
    rect.attr("fill", "#f00")
    
    var txt = this.paper.text(this.x + 50, this.y + 15, this.task.getName())
    
    this.rect = rect
    this.txt = txt
    
    var pathString
    // draw the level 0 lines
    if (level == 0 && this.children.length > 0) {
        pathString = "M" + this.xCenter() + "," + this.yBottom()
        pathString += "L" + this.xCenter() + "," + (this.yBottom() + 15)
        pathString += "M" + this.children[0].xCenter() + "," + (this.yBottom() + 15)
        
        var toX = this.children.length > 1 ? this.children[this.children.length - 1].xCenter() : this.xCenter()
        
        pathString += "L" + toX + "," + (this.yBottom() + 15)
        
        for(child in this.children) {
            pathString += "M" + this.children[child].xCenter() + "," + (this.yBottom() + 15)
            pathString += "L" + this.children[child].xCenter() + "," + this.children[child].y
        }        
    } else if (this.children.length > 0) {
        pathString = "M" + (this.x + 10) + "," + this.yBottom()
        pathString += "L" + (this.x + 10) + "," + this.children[this.children.length - 1].yCenter()
        
        for(child in this.children) {
            pathString += "M" + (this.x + 10) + "," + this.children[child].yCenter()
            pathString += "L" + this.children[child].x + "," + this.children[child].yCenter()
        }
    }

    this.path = this.paper.path(pathString)
    
    var cloneRect
    var hasMoved
    var onMove = function(dx, dy) {        
        cloneRect.attr("x", rect.attr("x") + dx)
        cloneRect.attr("y", rect.attr("y") + dy)
        
        hasMoved = true
    }
    
    var onStart = function() {
        cloneRect = rect.clone()
        cloneRect.attr("opacity", 0.8)        
        
        hasMoved = false
    }
    
    var that = this
    var onEnd = function() {
        cloneRect.remove()
        
        if (!hasMoved) {
            that.onEditTask(that.task)
        } else {
            var newTask = that.onAddTask(that.task)
            var newTaskDisplay = new TaskDisplay(that.paper, newTask, that.onAddTask, that.onEditTask)
            that.addChild(newTaskDisplay)
            that.draw()
        }        
    }
    rect.drag(onMove, onStart, onEnd)
}

TaskDisplay.prototype.xCenter = function() {
    return this.x + TaskDisplay.TASK_WIDTH / 2
}

TaskDisplay.prototype.yBottom = function() {
    return this.y + TaskDisplay.TASK_HEIGHT
}

TaskDisplay.prototype.yCenter = function() {
    return this.y + TaskDisplay.TASK_HEIGHT / 2
}

TaskDisplay.prototype.addChild = function(child) {
    this.children.push(child)
    
    child.parent = this
}

TaskDisplay.prototype.taskUpdated = function(task) {
    
    }

TaskDisplay.prototype.getGraphSibblingIndex = function() {
    var idx = 0, i
    for (i = 0; i < this.task.getSibblingIndex(); i++) {
        idx += 1 + this.task.parent.getSubTasks()[i].getTotalSubTasks()
    }
    
    return idx
}
