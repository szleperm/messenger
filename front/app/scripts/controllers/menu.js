'use strict';

/**
 * @ngdoc function
 * @name customersSecurityApp.controller:MenuCtrl
 * @description
 * # MenuCtrl
 * Controller of the customersSecurityApp
 */
angular.module('messengerApp')
  .controller('MenuCtrl', function (auth, $uibModal, $rootScope) {
    var self = this;		
	auth.authenticate();
	self.isActive = function(page){
		return page === $rootScope.activePage;
	};
	self.showLogin = function(){
		var modal = $uibModal.open({
			templateUrl: 'views/login.html',
			controller: 'LoginCtrl',
			controllerAs: 'c',			
		});
		modal.result.then(function(){
			self.showRegister();
		});
	};
	self.showLogout = function(){
		$uibModal.open({
			templateUrl: 'views/logout.html',
			controller: 'LoginCtrl',
			controllerAs: 'c',
			size: 'sm'
		});
	};
	self.showRegister = function(){
		var modal = $uibModal.open({
			templateUrl: 'views/register.html',
			controller: 'LoginCtrl',
			controllerAs: 'c',
		});
		modal.result.then(function(){
			self.showLogin();
		});
	};
	self.showChangePassword = function(){
		$uibModal.open({
			templateUrl: 'views/change-password.html',
			controller: 'AccountCtrl',
			controllerAs: 'c',
		});
	};
  });
