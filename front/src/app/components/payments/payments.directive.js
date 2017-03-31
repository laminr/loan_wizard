(function() {
  'use strict';

    angular
        .module('loanWizard')
        .directive('ebizPayments', paymentsDirective);

  /*** @ngInject */
  function paymentsDirective(moment) {

    var directive = {
      restrict: 'EA',
      templateUrl: 'app/components/payments/payments.htm',
      controller: paymentsController,
      controllerAs: 'ctrl',
      bindToController: true,
      scope: {
        payments: "=",
        startingAt: "="
      }
    };

    return directive;

    /*** @ngInject */
    function paymentsController() {
      var ctrl = this;

      ctrl.dateAt = function (period) {
        var start = moment(ctrl.startingAt, "DD/MM/YYYY");
        return start.add(period-1, "M").format("MMM YYYY");
      };
    }

  }

})();
