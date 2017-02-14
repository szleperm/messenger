'use strict';

/**
 * @ngdoc service
 * @name messengerApp.User
 * @description
 * # User
 * Factory in the customersSecurityApp.
 */
angular.module('messengerApp')
  .factory('User', function ($resource) {
    return $resource(
    	'/api/users/:id',
    	{id: '@id'},
    	{
    		update:{
    			method: 'PUT'
    		}
    	}
    );
  });
