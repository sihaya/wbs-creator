function TaskDisplay(paper, task) {
    this.task = task
    this.paper = paper
    this.children = []
    
    task.addObserver(this)
}

TaskDisplay.TASK_WIDTH = 100
TaskDisplay.TASK_HEIGHT = 35
TaskDisplay.TASK_OFFSET = 30

TaskDisplay.prototype.draw = function() {    
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
        
        var child
        for(child in this.children) {
            pathString += "M" + this.children[child].xCenter() + "," + (this.yBottom() + 15)
            pathString += "L" + this.children[child].xCenter() + "," + this.children[child].y
        }        
    } else if (this.children.length > 0) {
        pathString = "M" + (this.x + 10) + "," + this.yBottom()
        pathString += "L" + (this.x + 10) + "," + this.children[this.children.length - 1].yCenter()
        
        var child
        for(child in this.children) {
            pathString += "M" + (this.x + 10) + "," + this.children[child].yCenter()
            pathString += "L" + this.children[child].x + "," + this.children[child].yCenter()
        }
    }

    this.paper.path(pathString)
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

TaskDisplay.prototype.taskUpdate = function(task) {
    
    }

TaskDisplay.prototype.getGraphSibblingIndex = function() {
    var idx = 0, i
    for (i = 0; i < this.task.getSibblingIndex(); i++) {
        idx += 1 + this.task.parent.getSubTasks()[i].getTotalSubTasks()
    }
    
    return idx
}
