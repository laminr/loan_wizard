
(function() {
  'use strict';

    angular
        .module('loanWizard')
        .directive('loanPlan', loanDirective);

  /*** @ngInject */
  function loanDirective(dataservice, moment) {

    var directive = {
      restrict: 'EA',
      templateUrl: 'app/components/loan/loan.htm',
      controller: loanController,
      controllerAs: 'ctrl',
      scope: {
        loan: '=',
        id: '='
      },
      bindToController: true
    };

    /*** @ngInject */
    function loanController() {

      var ctrl = this;
      ctrl.showForm = true;
      ctrl.loading = false;

      ctrl.startingAt = moment().format('DD/MM/YYYY');
      // (54537.35, payments, 180, 3.6, RatePeriod.ANNUAL, 31.82

      ctrl.updateStartingAt = function (value) {
        ctrl.startingAt = value.date;
      };

      /*-------------------------*/
      ctrl.getLoan = function() {
        ctrl.loading = true;

        var callback = function(data) {
          if (data.error === undefined) {
            ctrl.loan = data;
            ctrl.showForm = false;
            ctrl.loading = false;
          } else {
            ctrl.showForm = true;
            ctrl.loading = false;
          }
        };

        dataservice.getLoan(ctrl.loan, callback);
      };

    }

    return directive;
  }

})();
