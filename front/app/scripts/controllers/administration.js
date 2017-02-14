'use strict';

/**
 * @ngdoc function
 * @name customersSecurityApp.controller:UserManagementCtrl
 * @description
 * # UserManagementCtrl
 * Controller of the customersSecurityApp
 */
angular.module('messengerApp')
  .controller('AdministrationCtrl', function (User) {
    var self = this;
    self.user = new User();
    self.users = [];
    self.itemsByPage = 10;
    self.getAllUsers = function(){
    	self.users = User.query();
    };
    self.updateUser = function(){
        self.user.$update(function(){
            self.getAllUsers();
        });
    };
    self.deleteUser = function(identity){
       var user = User.get({id:identity}, function() {
            user.$delete(function(){
                self.getAllUsers();
            });
       });
    };
    self.getAllUsers();
  });
