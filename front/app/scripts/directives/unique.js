'use strict';

/**
 * @ngdoc directive
 * @name messengerApp.directive:unique
 * @description
 * # unique
 */
angular.module('messengerApp')
  .directive('unique', function ($timeout, $http) {
    return {
      restrict: 'AE',
      require: 'ngModel',
      scope: {
    	 attribute: '@unique'
      },
      link: function (scope, element, attrs, model) {
    	  model.$asyncValidators.unique = function(modelValue) {
    		  var data = {};
    		  data[scope.attribute] = modelValue;
    		  console.log(data);
    		  return $http.post('/api/account/register/available', data).then(function(res){
    	          $timeout(function(){
    	            model.$setValidity('unique', res.data[scope.attribute]);
    	          }, 1000);
    	        });
    	  };
      }
    };
  });
