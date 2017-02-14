'use strict';

/**
 * @ngdoc function
 * @name customersSecurityApp.controller:LoginCtrl
 * @description
 * # LoginCtrl
 * Controller of the customersSecurityApp
 */
angular.module('messengerApp')
  .controller('LoginCtrl', function (auth, $uibModalInstance, $rootScope) {
    var self = this;
	self.login = function(){
		auth.login(self.credentials, function(){
			self.credentials = {};
			if ($rootScope.authenticated){
				self.error = false;
				$uibModalInstance.dismiss();
			} else {
				self.error = true;
			}
		});		
	};
	self.register = function(){
		auth.register(self.registerData, function(result){
			if (result === true){
				$rootScope.registerSuccess = true;
				$uibModalInstance.dismiss();
			} else {
				self.registerError = true;
			}
		});
	};
	self.change = function(){
		$uibModalInstance.close();
	};
	self.logout = function(){
		auth.logout();
		$uibModalInstance.dismiss();
	};
	self.cancel = function(){
		self.credentials = {};
		$uibModalInstance.dismiss();
	};
  });
