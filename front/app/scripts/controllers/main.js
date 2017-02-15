'use strict';

/**
 * @ngdoc function
 * @name messengerApp.controller:MainCtrl
 * @description
 * # MainCtrl
 * Controller of the messengerApp
 */
angular.module('messengerApp')
  .controller('MainCtrl', function (auth, $rootScope, message) {
    var self = this;
    var params = {to: 'user', read: false};
    $rootScope.activePage = 'home';
    message.getCollection('api/messages', params, function (result) {
      self.getMessages(result);
    });

    self.getMessages = function (result) {
      if (result.page) {
        self.messages = result._embedded.messages;
        self.totalMessages = result.page.totalElements;
        self.links = result._links;
      }
    };
    self.accountType = function () {
      if (auth.hasCurrentUserRole('ROLE_ADMIN')) {
        return 'administrator';
      } else {
        return 'regular user';
      }
    };
  });
