function TaskDrawingWindow(onSelect) {
    this.onSelect = onSelect
}

TaskDrawingWindow.prototype.select = function(taskDisplay) {
    if (this.currentTask) {
        this.currentTask.deselect()
    }
	
    taskDisplay.select()
    this.currentTask = taskDisplay
    
    if (this.onSelect) {
        this.onSelect(this.currentTask.task)
    }
}

function TaskDisplay(taskDrawingWindow, paper, paperContext, task, onAddTask, onEditTask) {
    this.taskDrawingWindow = taskDrawingWindow
    this.task = task
    this.paperContext = paperContext
    this.paper = paper
    this.children = []
    this.onAddTask = onAddTask
    this.onEditTask = onEditTask
    
    task.addObserver(this)
}

TaskDisplay.TASK_WIDTH = 100
TaskDisplay.TASK_HEIGHT = 35
TaskDisplay.TASK_OFFSET = 30
TaskDisplay.PROPERTY_LINE_HEIGHT = 15

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
    
    if (this.set) {
        this.set.remove()
    }
   
    
    if (this.allSet) {
        this.allSet.remove()
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
        this.y = this.parent.yBottom() + TaskDisplay.TASK_OFFSET
    } else {        
        this.x = this.parent.x + TaskDisplay.TASK_OFFSET
        this.y = this.parent.children.indexOf(this) != 0 ? this.getPreviousGraphSibbling().yBottom() : this.parent.yBottom()
    } 
        
    var rect = this.paper.rect(this.x, this.y, TaskDisplay.TASK_WIDTH, TaskDisplay.TASK_HEIGHT, 10)    
    rect.attr("fill", "#f00")
    
    var txt = this.paper.text(this.x + 50, this.y + 15, this.task.getName())
    
    this.rect = rect
    this.txt = txt
            
    this.set = this.paper.set()
    this.set.push(rect, txt)
    
    this.allSet = this.paper.set()
    this.allSet.push(this.set)
    
    this.yBoxBottomValue = TaskDisplay.TASK_HEIGHT + this.y
    
    this.drawPropertiesBox()
                
    var cloneRect
    var hasMoved
    var onMove = function(dx, dy) {        
        if (!cloneRect) {
            cloneRect = rect.clone()
            cloneRect.attr("opacity", 0.8)  
        }
    
        cloneRect.attr("x", rect.attr("x") + dx)
        cloneRect.attr("y", rect.attr("y") + dy)
        
        hasMoved = true
    }
    
    var onStart = function() {           
        hasMoved = false
    }
    
    var that = this
    var onEnd = function() {
        if (cloneRect) {
            cloneRect.remove()
            cloneRect = null
        }
        
        if (!hasMoved) {                    
            that.taskDrawingWindow.select(that)
        } else {
            var newTask = that.onAddTask(that.task)
            var newTaskDisplay = new TaskDisplay(that.taskDrawingWindow, that.paper, that.paperContext, newTask, that.onAddTask, that.onEditTask)
            that.addChild(newTaskDisplay)
            
            that.startRedraw()
        }        
    }
    
    this.set.dblclick(function() {
        that.beginEdit()
    })
    
    this.set.drag(onMove, onStart, onEnd)
    
    var child
    for(child in this.children) {
        this.children[child].draw()
    }

    this.drawPath()
}

TaskDisplay.prototype.drawPath = function() {
    var level = this.task.getLevel()	  
    var pathString
    // draw the level 0 lines
    if (level == 0 && this.children.length > 0) {
        pathString = "M" + this.xCenter() + "," + this.yBoxBottom()
        pathString += "L" + this.xCenter() + "," + (this.yBottom() + 15)
        pathString += "M" + this.children[0].xCenter() + "," + (this.yBottom() + 15)
        
        var toX = this.children.length > 1 ? this.children[this.children.length - 1].xCenter() : this.xCenter()
        
        pathString += "L" + toX + "," + (this.yBottom() + 15)
        
        for(child in this.children) {
            pathString += "M" + this.children[child].xCenter() + "," + (this.yBottom() + 15)
            pathString += "L" + this.children[child].xCenter() + "," + this.children[child].y
        }        
    } else if (this.children.length > 0) {
        pathString = "M" + (this.x + 10) + "," + this.yBoxBottom()
        pathString += "L" + (this.x + 10) + "," + this.children[this.children.length - 1].yCenter()
        
        for(child in this.children) {
            pathString += "M" + (this.x + 10) + "," + this.children[child].yCenter()
            pathString += "L" + this.children[child].x + "," + this.children[child].yCenter()
        }
    }

    this.path = this.paper.path(pathString)
}

TaskDisplay.prototype.drawPropertiesBox = function() {
    var properties = this.task.getPropertiesDef()
    var height = TaskDisplay.PROPERTY_LINE_HEIGHT * properties.length
	
    var x = this.x + TaskDisplay.TASK_WIDTH / 2 + 5
    var y = this.y + TaskDisplay.TASK_HEIGHT + 5
    

    var propertiesBox = this.paper.rect(x, y, TaskDisplay.TASK_WIDTH - 5, height, 10)
      
    this.propertiesText = this.paper.text(x + 5, y + height / 2, "").attr("text-anchor", "start") 
    
    this.drawPropertiesText()
    
    this.allSet.push(this.propertiesText, propertiesBox)
  
    this.yBottomValue = height + TaskDisplay.TASK_HEIGHT + this.y + 10
}

TaskDisplay.prototype.drawPropertiesText = function() {
    var properties = this.task.getPropertiesDef()
    var prop
    var propertiesText = ""
    for(prop in properties) {
        propertiesText += properties[prop] + ": " + this.task.getPropertyValue(properties[prop]) + "\n"
    }
    
    this.propertiesText.attr("text", propertiesText)
}

TaskDisplay.prototype.deselect = function() {
    this.rect.attr('stroke', "#000")
}

TaskDisplay.prototype.select = function() {
    this.rect.attr('stroke', "#ff0")
}

TaskDisplay.prototype.startRedraw = function() {
    if (this.task.getLevel() <= 1) {
        this.draw();	
    } else {
        this.parent.startRedraw();
    }
}

TaskDisplay.prototype.beginEdit = function() {
    var x, y, inputElement
    
    x = this.paperContext.x + this.rect.attr("x")
    y = this.paperContext.y + this.rect.attr("y")
    inputElement = this.paperContext.inputElement
    
    if ($(inputElement).is(':visible')) {
        $(inputElement).blur()
    }
    
    $(inputElement).unbind()
    
    var that = this;
    $(inputElement).css("width", TaskDisplay.TASK_WIDTH).css("height", TaskDisplay.TASK_HEIGHT).css("left", x + "px").css("top", y + "px").keydown(function(event) {
        if (event.which == 13) {
            event.preventDefault()
            
            that.endEdit()            
        }
    }).show()
    
    $(inputElement).blur(function() {
        that.endEdit()
    })
    
    $(inputElement).val(this.task.getName())
    $(inputElement).focus()
    this.txt.hide()    
}

TaskDisplay.prototype.endEdit = function() {
    $(this.paperContext.inputElement).unbind()
    $(this.paperContext.inputElement).hide()
            
    var newText = $(this.paperContext.inputElement).val()
    
    if (this.onEditTask(this.task, newText)) {
        this.txt.attr("text", newText)
    }
    
    this.txt.show()
}

TaskDisplay.prototype.xCenter = function() {
    return this.x + TaskDisplay.TASK_WIDTH / 2
}

TaskDisplay.prototype.yBottom = function() {
    return this.yBottomValue
}

TaskDisplay.prototype.yBoxBottom = function() {
    return this.yBoxBottomValue
}

TaskDisplay.prototype.yCenter = function() {
    return this.y + TaskDisplay.TASK_HEIGHT / 2
}

TaskDisplay.prototype.addChild = function(child) {
    this.children.push(child)
    
    child.parent = this
}

TaskDisplay.prototype.taskUpdated = function(task) {
    if (this.task.isRemoved()) {
        if (!this.parent.task.isRemoved()) {
            this.parent.removeChild(this)
        }
        
        this.clear()
    } else {
        this.drawPropertiesText()
    }
}

TaskDisplay.prototype.removeChild = function(child) {
    this.children.splice(this.children.indexOf(child), 1)
    
    this.draw()
}

TaskDisplay.prototype.getPreviousGraphSibbling = function() {
    return this.parent.children[this.parent.children.indexOf(this) - 1].getDeepestChild()
}

TaskDisplay.prototype.getDeepestChild = function() {
    if (this.children.length > 0) {
        return this.children[this.children.length - 1].getDeepestChild()
    } else {
        return this
    }
}

TaskDisplay.prototype.getGraphSibblingIndex = function() {
    var idx = 0, i
    for (i = 0; i < this.task.getSibblingIndex(); i++) {
        idx += 1 + this.task.parent.getSubTasks()[i].getTotalSubTasks()
    }
    
    return idx
}
