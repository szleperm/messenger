'use strict';

/**
 * @ngdoc function
 * @name customersSecurityApp.controller:AccountctrlCtrl
 * @description
 * # AccountctrlCtrl
 * Controller of the customersSecurityApp
 */
angular.module('messengerApp')
  .controller('AccountCtrl', function ($http, $uibModalInstance, $rootScope) {
    var self = this;
    self.changePassword = function(){
    	self.passwordData.username = $rootScope.principal.username;
    	$http.patch('/api/account/change_password', self.passwordData).then(function() {
    		self.passwordError = false;
    		$uibModalInstance.dismiss();
    	}, function(response) {
    		self.passwordError = true;
    		self.errors = response.data;
    	});
    };
    self.cancel = function(){
		self.passwordData = {};
		$uibModalInstance.dismiss();
	};
  });
