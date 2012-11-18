'use strict';

/* jasmine specs for tasks.js go here */

describe('task', function() {
    it('should return proper values', function() {
        var taskName = "my task name"
        var effort = 443;
        var task = new Task(taskName)
        task.setEffort(effort)
        
        expect(task.getEffort()).toEqual(effort)
        expect(task.getName()).toEqual(taskName)
        expect(task.isLeaf()).toEqual(true)
    })
    
    it('should add a sub task', function() {
        var task = new Task("test")
        
        task.addSubTask("sub task")
        
        expect(task.isLeaf()).toEqual(false)
        expect(task.getSubTasks().length).toEqual(1)        
    })
    
    it('should return a subtask', function() {
        var task = new Task("test")
        
        var name = "sub task"
        var subTask = task.addSubTask(name)
        
        expect(subTask.getName()).toEqual(name)
    })
    
    it('should return sum of effort', function() {
        var task = new Task("test")
        
        var subTask1 = task.addSubTask("sub task1")
        subTask1.setEffort(222)
       
        var subTask2 = task.addSubTask("sub task2")
        subTask2.setEffort(211)
        
        expect(task.getEffort()).toEqual(222 + 211)
    })
    
    it('should call update of observer', function() {
        var calledTask
        var observer = new Object()
        observer.taskUpdated = function(task) {
            calledTask = task
        }
        
        var task = new Task("test")
        task.addObserver(observer)
        task.setEffort(22)
        
        expect(calledTask).toEqual(task)
    })
    
    it('should return level 0 if root', function() {
        var root = new Task("root task")
        
        expect(root.getLevel()).toEqual(0)
    })
    
    it('should return correct sibblingIndex', function() {
        var root = new Task("root task")
        var number1 = root.addSubTask("number 1")
        var number2 = root.addSubTask("number 2")
        
        expect(number1.getSibblingIndex()).toEqual(0)
        expect(number2.getSibblingIndex()).toEqual(1)
        expect(number1.getAmountOfSibblings()).toEqual(2)
    })
    
    it('should return parent', function() {
        var root = new Task("root task")
        
        var sub = root.addSubTask("subTask")
        expect(sub.getParent()).toEqual(root)
    })
})

describe('factory', function() {
    it('should return the tasks', function() {
        var factory = new TaskFactory()
        var jsonData = {
            name: 'root task', 
            subTasks: [{
                name: 'subtask1', 
                subTasks: [{
                    name: 'subsubTask1', 
                    subTasks: []
                }]
                }]
            }
        
        var root = factory.createTasksFromJson(jsonData)
        
        expect(root.getName()).toEqual("root task")
        expect(root.getSubTasks().length).toEqual(1)
        
        var subtask1 = root.getSubTasks()[0]
        expect(subtask1.getName()).toEqual("subtask1")
        expect(subtask1.getSubTasks().length).toEqual(1)
    })
})
