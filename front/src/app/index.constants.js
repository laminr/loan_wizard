/* global toastr:false, moment:false */
(function() {
  'use strict';

  angular
    .module('loanWizard')
    .constant('toastr', toastr)
    .constant('moment', moment);

})();
