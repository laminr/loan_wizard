(function() {
  'use strict';

  angular.module('loanWizard').directive('loanProject', loanProjectDirective);

  /** @ngInject */
  function loanProjectDirective() {

    var directive = {
      restrict: 'EA',
      templateUrl: 'app/components/loanProject/loanProject.htm',
      controller: loanProjectController,
      controllerAs: 'ctrl',
      bindToController: true
    };

    return directive;

    /** @ngInject */
    function loanProjectController($scope, $compile) {

      var ctrl = this;

      ctrl.loans =  [];

      ctrl.addLoanPlan = function () {
        var loan =  {
          "rate" : 2.15,
          "borrowed": 10000,
          "totalCost": "",
          "insurance": 31.82,
          "period": 180,
          "payment": "",
          "payments": []
        };

        ctrl.loans.push(loan);
        ctrl.addDirective(ctrl.loans.length-1);
      };

      ctrl.addDirective = function(index) {
        angular.element(
          document.getElementById('loanProject')
        ).append(
          $compile('<loan-plan loan="ctrl.loans['+index+']" id="'+index+'"></loan-plan>')($scope)
        );
      };

      ctrl.addLoanPlan();
    }
  }


})();
