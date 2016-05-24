'use strict';

(function() {

	var MODULE = angular.module('tech.ghp.chat',
			[ 'ngRoute', 'enEasse' ]);

	MODULE.config( function($routeProvider) {
		$routeProvider.when('/', { controller: mainProvider, templateUrl: '/tech.ghp.chat/main/htm/home.htm'});
		$routeProvider.when('/about', { templateUrl: '/tech.ghp.chat/main/htm/about.htm'});
		$routeProvider.otherwise('/');
	});
	
	var alerts = [];
  	var chat = { 
    	users: [], 
   	 	history: [], 
    	user: undefined 
  	};
    
  	function error( msg ) {
    	alerts.push( { type: 'danger', msg: msg });
  	}
  	function webError( d ) {
    	error( d.statusText);
  	}
	
	MODULE.run( function($rootScope, $location) {
		$rootScope.alerts = [];
		$rootScope.closeAlert = function(index) {
			$rootScope.alerts.splice(index, 1);
		};
		$rootScope.page = function() {
			return $location.path();
		}
	});
	
	
	var mainProvider = function($scope, $http, en$easse) {
  
    $scope.chat = chat;
    
    function refreshUsers() {
      var promise = $http.get('/rest/users');
      promise.then( 
        function(d) { 
          angular.copy(d.data, chat.users)
        },
        webError
      );  
    }
    
    refreshUsers();
    
    var close = en$easse.handle("tech/ghp/chat/*", function(e) {
      $scope.$applyAsync(function() {
        if ( e['event.topics'] == 'tech/ghp/chat/message') 
          chat.history.push({ from: e.from, text: e.text });
        else
          refreshUsers();
      });
    }, function(d) { } );
    
    $scope.$on('$destroy', function() { close.close(); })

    $scope.send = function(text) {
      var promise = $http.put('/rest/message', { to: chat.user, text: text});
      promise.then( 
        function(d) {
            chat.history.push({ from: null, text: text })
          $scope.text = "";
        }, 
        webError
      );  
    }
  }
	
})();
