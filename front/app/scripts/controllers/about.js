'use strict';

/**
 * @ngdoc function
 * @name customersSecurityApp.controller:AboutCtrl
 * @description
 * # AboutCtrl
 * Controller of the customersSecurityApp
 */
angular.module('messengerApp')
  .controller('AboutCtrl', function ($rootScope) {
    $rootScope.activePage = 'about';
  });
