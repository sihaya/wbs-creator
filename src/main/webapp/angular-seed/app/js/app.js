'use strict';

// Declare app level module which depends on filters, and services
angular.module('myApp', ['myApp.filters', 'myApp.services', 'myApp.directives', 'ngCookies']).
    config(['$routeProvider', function($routeProvider) {
        $routeProvider.when('/home', {
            templateUrl: 'partials/home.html', 
            controller: HomeController
        });
        $routeProvider.when('/projects', {
            templateUrl: 'partials/projects.html', 
            controller: ProjectsController
        });
        $routeProvider.when('/project/:projectId', {
            templateUrl: 'partials/project.html', 
            controller: ProjectController
        });
        $routeProvider.when('/sheet/:sheetId', {
            templateUrl: 'partials/sheet.html',
            controller: SheetController
        });
        $routeProvider.when('/create-account', {
            templateUrl: 'partials/create-account.html',
            controller: CreateAccountController
        }); 
        $routeProvider.otherwise({
            redirectTo: '/home'
        });
        
    }]);
