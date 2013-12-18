// declare a module
var Mdmp = angular.module('Mdmp', []);
// global value
// FIXME services
var login_url = window.location.toString();
login_url = login_url.substring(0, login_url.lastIndexOf("/")) + "/login";
// declare UserService

Mdmp.service('UserService', [ '$http', function($http) {
	this.login = function(user, func) {
		console.log(login_url);
		// TODO call from http, if succes, return true, set-into Cookies
		$http({
			method : 'jsonp',
			url : login_url
		}).success(function(data, status) {
			console.log(data);
			if (data.token) {
				//func();
			}
		}).error(function(data, status) {
			console.log(data + " , status : " + status);
		});
		console.log("login success", user);
		
		return true;
	};
} ]);

// sample Service
Mdmp.service('nametrickService', function() {
	this.reverse = function(name) {
		return name.split("").reverse().join("");
	};
});

// declare main controller MainCtrl
var MainController = function() {

};

var LoginController = function($scope, UserService, $location, $window) {

	$scope.signin = function(user) {

		// UserService.login();
		// console.log(nametrickService.reverse(user.email));
		UserService.login(user, jumpToWorkspace);
	};
	var jumpToWorkspace = function() {
		$window.location = "workspace.html";
	}

};

Mdmp.controller("MainCtrl", MainController);

Mdmp.controller("LoginCtrl", LoginController);
