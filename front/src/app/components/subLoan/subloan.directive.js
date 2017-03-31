(function() {
    'use strict';

    angular
        .module('loanWizard')
        .directive('subLoan', subLoanDirective);

    /*** @ngInject */
    function subLoanDirective() {

        var directive = {
            restrict: 'EA',
            templateUrl: 'app/components/subLoan/subloan.htm',
            controller: subLoanController,
            controllerAs: 'ctrl',
            scope: {
                subloan: '=data',
            },
            bindToController: true
        };

        /*** @ngInject */
        function subLoanController() {

            var ctrl = this;

            ctrl.validated = false;

        }

        return directive;
    }

})();