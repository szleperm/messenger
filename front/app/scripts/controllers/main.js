'use strict';

/**
 * @ngdoc function
 * @name customersSecurityApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the customersSecurityApp
 */
angular.module('messengerApp')
  .controller('MainCtrl', function (auth, $rootScope) {
    var self = this;
    $rootScope.activePage = 'home';
    self.accountType = function () {
      if (auth.hasCurrentUserRole('ROLE_ADMIN')) {
        return 'administrator';
      } else {
        return 'regular user';
      }
    };
  });
