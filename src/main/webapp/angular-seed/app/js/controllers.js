'use strict';

/* Controllers */
var loggedInUser = {
    'username': 'pete', 
    'password': 'developer'
};

var hostAuth;

hostAuth = host.replace("http://", "http://" + loggedInUser.username + ":" + loggedInUser.password + "@");

function HomeController($scope, $http, $location) {        
    $scope.createAccount = function() {
        console.log("Creating account for " + $scope.newUser.username);        
        $http({
            method: 'POST', 
            url: host + 'user', 
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            data: $.param($scope.newUser)
        }).success(function() {
            console.log("Account succesfully created");
        });
    }
    
    $scope.loginAccount = function() {
        hostAuth = host.replace("http://", "http://" + loggedInUser.username + ":" + loggedInUser.password + "@");
        $location.path('/projects');
    }
    
    $scope.newUser = {
        'username': '', 
        'password': '', 
        'email': ''
    };
    
    $scope.loggedInUser = loggedInUser;
}

/**
 * Controller for project listings
 */
function ProjectsController($scope, $http) {        
    $scope.retrieveProjects = function() {
        $http({
            method: 'GET',
            url: hostAuth + 'user/' + loggedInUser.username
        }).success(function(data) {
            $scope.projects = data;
        });
    }
    
    $scope.createProject = function() {
        $http({
            method: 'POST',
            url: hostAuth + 'project', 
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            data: $.param({
                'username': loggedInUser.username, 
                'projectName': $scope.projectName
            })
        }).success(function() {
            $scope.retrieveProjects(); 
        });
    }
    $scope.projectName = '';    
    
    $scope.retrieveProjects();        
    $scope.loggedInUser = loggedInUser;
}    

/**
 * Project controller for single projects
 */
function ProjectController($scope, $http, $routeParams) {
    $scope.retrieveProject = function() {
        $http({
            method: 'GET',
            url: hostAuth + 'project/' + $routeParams.projectId
        }).success(function(data) {
            $scope.project = data            
        });
    };
    
    $scope.createSheet = function() {
        $http({
            method: 'POST',
            url: hostAuth + 'sheet',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            data: $.param({
                'sheetName': $scope.sheetName, 
                'projectId': $routeParams.projectId
            })
        }).success(function() {
            $scope.retrieveProject();            
        });
    };    
    
    $scope.addMember = function() {
        $http({
            method: 'POST',
            url: hostAuth + 'project/' + $routeParams.projectId + '/members/' + $scope.memberName
        }).success(function(){ 
            $scope.retrieveProject();
        });
    };

    $scope.sheets = [];
    $scope.retrieveProject();
}

function SheetController($scope, $http, $routeParams) {
    var model;
    
    var getTaskModel = function() {		            		
        $.ajax({
            url: host + 'sheet/' + $routeParams.sheetId
        })
        .done(function(data) {
            model = data.root;
				
            draw();
        });
    }
    
    getTaskModel();
	
    var syncUpdateTaskWithServer = function(task) {
        $.ajax({
            type: 'PUT', 
            url: hostAuth + 'task',
            data: {
                'taskId': task.taskId, 
                effort: task.effort, 
                name: task.name
            }
        });
    }
	
    var syncNewTaskWithServer = function(parentTask, newTask) {
        $.ajax({
            type: 'POST', 
            url: hostAuth + 'task',
            data: {
                'parentTaskId': parentTask.taskId
            }
        }).complete(function(xhr, data) {
            var response = xhr.getResponseHeader('location');
            var last = response.split('\/');
					
            newTask.taskId = last[last.length - 1];
        });			
    }
	
    var syncDeleteTaskWithServer = function(taskId) {
        $.ajax({
            type: 'DELETE', 
            url: hostAuth + 'task',
            data: {
                'taskId': taskId
            }
        });
    }
	
    var createTaskDetail = function(taskDetail, level, addSibblingFunction, addChildFunction, deleteTaskFunction) {
        var template = 
        '<div class="task-detail">' +
        '<button class="done">OK</button>' + 
        '<div class="trunk-mask"></div>' +
        '<div class="mask-right"></div>' +
        '<div class="add-sibbling">+</div>' +
        '<div class="add-child">+</div>' + 
        '<div class="del-task">-</div>';			
			
        if (level > 2) {
            template = template + 
            '	<div class="line"></div>';
        } else if (level == 1) {
            template = template + 
            '	<div class="bottomline"></div>';
        } else if (level == 2) {
            template = template +
            '<div class="topline"></div>';
        }			
			
        template = template + 
        '	<div class="name"><input type="text"></input><span></span></div>' + 
        ' <div class="effort"><input type="text"></input><span></span></div>' + 
        '	<div class="number"></div>' +
        '</div>';
		
        template = $(template);
		
        $('div.add-sibbling', template).click(function() {
            addSibblingFunction();
            draw();
        });
        $('div.add-child', template).click(function() {
            addChildFunction();
            draw();
        });
        $('div.del-task', template).click(function() {
            deleteTaskFunction();
			
            syncDeleteTaskWithServer(taskDetail.taskId);
			
            draw();
        });
		
        $('.name span', template).html(taskDetail.name);
        $('.effort span', template).html(taskDetail.effort);
        $('.number', template).html(taskDetail.number);
		
        $(template).click(function() {
            if($(template).hasClass('editing')) {
                return;
            }
		
            $(template).addClass('editing');
			
            $('.effort input', template).val(taskDetail.effort);			
            $('.name input', template).val(taskDetail.name);			
        });
		
        $('button', template).click(function() {
            taskDetail.effort = parseInt($('.effort input', template).val());
            taskDetail.name = $('.name input', template).val();
			
            syncUpdateTaskWithServer(taskDetail);
			
            draw();
        });
		
        return template;
    };
	
    var createSubTask = function(subTask, last, sibblings, parent) {
        var i;
        var template = '<div class="task subtask">' +
        '<div class="trunk">' +
        '<div class="trunk-erase">' +
        '</div>' +			
        '<div class="trunk-outer-erase"></div>' +     
        '</div>';
				
        var result = $(template);
        result.prepend(createTaskDetail(subTask, 2, null, function() {
            var task = {
                name: 'website',
                effort: '0'
            };
			
            syncNewTaskWithServer(subTask, task);
		
            if (!subTask.subTasks) {
                subTask.subTasks = [];				
            }
			
            subTask.subTasks.push(task);
			
            draw();
        },
        function() {
            var idx = sibblings.indexOf(subTask);
            sibblings.splice(idx, idx);
			
        }));
		
        if (last) {
            $(result).addClass('last-task');
        }
		
        for(i in subTask.subTasks) {
            $('> .trunk', result).append(createSubSubTask(subTask.subTasks[i], subTask.subTasks, subTask));
        }
		
        if (!subTask.subTasks || subTask.subTasks.length == 0) {
            $('.trunk', result).hide();
        }
			
        return result;
    }
	
    var createSubSubTask = function(subSubTask, sibblings, parent) {
        var template = '<div class="task subsubtask">' +
        '</div>';
			
        template = $(template);
		
        if (sibblings[sibblings.length - 1] === subSubTask) {
            template.addClass("task-last");
        }
		
        template.append(createTaskDetail(subSubTask, 3, function() {
            var task = {
                name: '',
                effort: '0'
            };
            sibblings.push(task);
				
            syncNewTaskWithServer(parent, task);
					
            draw();
        }, function() {
            var task = {
                name: '',
                effort: '0'
            }
			
            if (!subSubTask.subTasks) {
                subSubTask.subTasks = [];
            }
				
            subSubTask.subTasks.push(task);
				
            syncNewTaskWithServer(subSubTask, task);
				
            draw();
				
        }, function() {
            var j = sibblings.indexOf(subSubTask);
				
            sibblings.splice(j, 1);							
        }));
			
        if (subSubTask.subTasks && subSubTask.subTasks.length > 0) {		
            template.append($(
                '<div class="trunk">' +
                '<div class="trunk-erase"></div>' + 
                '<div class="trunk-outer-erase"></div>' +                                 
                '</div>'));
  	      
            var i;
            for(i in subSubTask.subTasks) {
                $('> .trunk', template).append(createSubSubTask(subSubTask.subTasks[i], subSubTask.subTasks, subSubTask));
            }
        }
			
        return template;
    }
	
    var calcTotals = function(task) {
        var total = 0;
        var i;
		
        if (task.subTasks && task.subTasks.length != 0) {
            for (i in task.subTasks) {
                total += calcTotals(task.subTasks[i]);
            }
        } else {
            total = parseInt(task.effort);
        }
		
        task.effort = total;
		
        return total;
    }
	
    var calcLabel = function(task, prefix) {
        task.number = prefix;
        var i;
		
        if (task.subTasks) {
            for(i = 0; i < task.subTasks.length; i++) {
                calcLabel(task.subTasks[i], prefix + "." + (i + 1));			
            }
        }
    }
	
    var draw = function() {
        var i;
		
        calcTotals(model);
        calcLabel(model, "1");
		
        $('.root').html('');
		
        $('.root').append($('<div class="vertical-line">' +
            '<div class="mask-left"></div>' +
            '</div>'));
	
        $('.root').prepend(createTaskDetail(model, 1, null, function() {
            var task = { 
                name: '',
                effort: '0'
            };
					
            syncNewTaskWithServer(model, task);
		
            model.subTasks.push(task);
					
            draw();
        }));
		
        for(i in model.subTasks) {		
		
            $('.root').append(createSubTask(model.subTasks[i], i == model.subTasks.length - 1, model.subTasks, model));
        }
    };
    
    
}