function TaskDisplay(paper, task) {
    this.task = task
    this.paper = paper
    
    task.addObserver(this)
}

TaskDisplay.prototype.draw = function() {
    var level = this.task.getLevel()
    
    if (level == 0) {
        this.x = this.paper.width / 2
        this.y = 30
    } else if (level == 1) {
        var widthPTask = this.paper.width / this.task.getAmountOfSibblings()
        
        this.x = widthPTask * this.task.getSibblingIndex() + widthPTask / 2
        this.y = 60
    } else {
        var widthPTask = this.paper.width / this.task.getParent().getAmountOfSibblings()
        var xOfParent = widthPTask * this.task.getParent().getSibblingIndex() + widthPTask / 2
        
        this.x = xOfParent + 10
        this.y = 40 * (this.task.getSibblingIndex() + 1) + 60
    }
    
    var rect = this.paper.rect(this.x, this.y, 100, 35, 10)
    rect.attr("fill", "#f00")
    
    var txt = this.paper.text(this.x + 50, this.y + 15, this.task.getName())
    
    this.rect = rect
    this.txt = txt
}

TaskDisplay.prototype.taskUpdate = function(task) {
    
}

