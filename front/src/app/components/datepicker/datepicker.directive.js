(function() {
  'use strict';

  angular.module("loanWizard")
    .directive('datepicker', datepickerDirective);

  /*** @ngInject */
  function datepickerDirective() {

    var directive = {
      restrict: 'A',
      require: '?ngModel',
      scope: {
        select: "&"
      },
      link: link
    };

    return directive;

    /*** @ngInject */
    function link(scope, element, attrs, ngModelCtrl) {

      var today = new Date();
      element.datetimepicker({ format: 'DD/MM/YYYY' });

      element.on("dp.change", function(e) {
        scope.$apply(function(){
          ngModelCtrl.$setViewValue(e.date.format("DD/MM/YYYY"));
        });
      });

    }
  }
})();
