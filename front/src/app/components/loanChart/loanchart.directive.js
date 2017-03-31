(function() {
  'use strict';

  angular
    .module('loanWizard')
    .directive('ebizLoanCharts', loanCharts);

  /*** @ngInject */
  function loanCharts() {

    var directive = {
      template: '<canvas></canvas>',
      link: link,
      restrict: 'EA',
      scope: {
        height : "=",
        width : "=",
        payments: "="
      },
      controller: LoanChartsController,
      controllerAs: 'ctrl',
      bindToController: true // because the scope is isolated
    };
    return directive;

    //LoanChartsController.$inject = ['$scope'];

    /*** @ngInject */
    function link(scope, element, attributes) {
        var canvas = element.find('canvas');
    }

    /*** @ngInject */
    function LoanChartsController($scope, $element) {

        var ctrl = this;
        var ctx = $element.find('canvas').get(0).getContext("2d");
        ctrl.chart;

        ctx.canvas.width = ctrl.width;
        ctx.canvas.height = ctrl.height;

        $scope.$watch('ctrl.payments', function () {
            createChart(ctrl.payments);
        });

        function createChart (payments) {

           // Get context with jQuery - using jQuery's .get() method.

            var fillColor1 = "rgba(229, 104, 0, 0.2)";
            var strokeColor1 = "rgba(229, 104, 0, 1)";

            var fillColor2 = "rgba(151,187,205,0.2)";
            var strokeColor2 = "rgba(151,187,205,1)";

            var fillColor3 = "rgba(114, 63, 189, 0.2)";
            var strokeColor3 = "rgba(114, 63, 189, 1)";

            var fillColor4 = "rgba(84, 98, 153, 0.2)";
            var strokeColor4 = "rgba(84, 98, 153, 1)";


            var labels = [];
            var basePayments = [];
            var fullPayments = [];
            var interest = [];
            var capital = [];

            _.each(ctrl.payments, function(p){
              labels.push(p.period);
              basePayments.push(p.basePayment);
              fullPayments.push(p.fullPayment);
              interest.push(p.interest);
              capital.push(p.capital);
            });

            var fullPayementDataset = {
                label: "Payements",
                fillColor: fillColor1,
                strokeColor: strokeColor1,
                pointColor: strokeColor1,
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: strokeColor1,
                data: fullPayments
            };

            var basePayementDataset = {
                label: "Base",
                fillColor: fillColor2,
                strokeColor: strokeColor2,
                pointColor: strokeColor2,
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: strokeColor2,
                data: basePayments
            };

            var capitalDataset = {
                label: "Intêret",
                fillColor: fillColor3,
                strokeColor: strokeColor3,
                pointColor: strokeColor3,
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: strokeColor3,
                data: capital
            };

            var interestDataset = {
                label: "Intêret",
                fillColor: fillColor4,
                strokeColor: strokeColor4,
                pointColor: strokeColor4,
                pointStrokeColor: "#fff",
                pointHighlightFill: "#fff",
                pointHighlightStroke: strokeColor4,
                data: interest
            };


            var data = {
                labels: labels,
                datasets: [
                    fullPayementDataset,
                    basePayementDataset,
                    capitalDataset,
                    interestDataset
                ]
            };

            ctrl.chart = new Chart(ctx).Line(data, {pointDot : false});
        }
    }

  }

})();

