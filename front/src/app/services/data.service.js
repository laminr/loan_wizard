(function() {
    'use strict';

    angular
        .module('loanWizard')
        .factory('dataservice', dataservice);

    /*** @ngInject */
    function dataservice($http) {

        return {
            getLoan: getLoan
        };

        function getLoan(loan, callback) {
            //*
            var url = 'http://localhost:5001/loan/get'
                + "/" + loan.borrowed
                + "/" + loan.period
                + "/" + loan.rate
                + "/" + loan.insurance;

            return $http.get(url)
                .success(getData)
                .error(getDataFailed);
            //*/
            /*
            // {capital}/{period}/{rate}/{insurance}
            return $http.post('/loan/get', loan)
                .then(getData)
                .catch(getDataFailed);
            //*/

            /*
            return $http.get('app/data/data.json')
                .then(getData)
                .catch(getDataFailed);
            //*/

            function getData(data) {
                callback(data);
            }

            function getDataFailed(data, status, headers, config) {
                callback({error: "Exception"});
            }
        }
    }


})();
