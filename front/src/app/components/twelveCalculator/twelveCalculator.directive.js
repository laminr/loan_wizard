(function() {
    'use strict';

    angular
        .module('loanWizard')
        .directive('ebizTwelveCalculator', twelveCalculator);

    function twelveCalculator() {

      var directive = {
          restrict: 'EA',
          templateUrl: 'app/components/twelveCalculator/twelveCalculator.htm',
          scope: {
              label : "@",
              type : "@",
          },
          controller: twelveCalculatorController,
          controllerAs: 'ctrl',
          bindToController: true // because the scope is isolated
      };

      return directive;

      //TwelveDividerController.$inject = ['$scope'];

      /*** @ngInject */
      function twelveCalculatorController() {

        var ctrl = this;

        ctrl.yearly = 1;
        ctrl.monthly = ctrl.type === "divide" ? Math.round((ctrl.yearly / 12) * 10000) / 10000 : ctrl.yearly * 12;

        ctrl.updateMonthtly = function () {
          if (ctrl.type === "divide") {
            ctrl.monthly =  Math.round((ctrl.yearly / 12) * 10000) / 10000;
          } else {
            ctrl.monthly = ctrl.yearly * 12;
          }
        };

      }

    }

})();



