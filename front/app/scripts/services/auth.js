'use strict';

/**
 * @ngdoc service
 * @name messengerApp.auth
 * @description
 * # auth
 * Service in the messengerApp.
 */
angular.module('messengerApp')
  .service('auth', function($http, $rootScope){
		var self = this;
		self.authenticate = function(callback){
		  $http.get('/api/account').then(function(response){
				if (response.data.username){
					$rootScope.authenticated = true;
					$rootScope.principal = response.data;
					if(callback) {
						callback();
					}
				}else{
					$rootScope.authenticated = false;
					$rootScope.principal = {'username': 'guest'};
					if(callback){
						callback();
					}
				}
			});
		};
		self.login = function(credentials, callback){
			var transformData = function(obj){
				var str = [];
				for (var p in obj){
					str.push(encodeURIComponent(p) + '=' + encodeURIComponent(obj[p]));
				}
				return str.join('&');
			};
			var headers = {'Content-type' : 'application/x-www-form-urlencoded'};
			var data = transformData(credentials);
			$http.post('/login', data, {headers : headers}).finally(function(){
				self.authenticate(callback);
			});
		};
		self.register = function(data, callback){
			$http.post('/api/account/register', data)
			.then(function(){
				if(callback){
					callback(true);
				}
			},function(){
				if(callback){
					callback(false);
				}
			})
			.finally(function(){
				self.authenticate();
			});
		};
		self.logout = function(){
			$http.post('/logout').finally(function(){
				self.authenticate();
			});
		};
		self.hasCurrentUserRole = function(role){
			var roles = $rootScope.principal._embedded.roles;
      var index = -1;
			for(var i = 0, len = roles.length; i < len; i++) {
        if (roles[i].name === role) {
          index = i;
          break;
        }
      }
			return index !== -1;
		};
	});
