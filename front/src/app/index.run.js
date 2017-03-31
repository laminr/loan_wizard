(function() {
  'use strict';

  angular
    .module('loanWizard')
    .run(runBlock);

  /** @ngInject */
  function runBlock($log) {

    $log.debug('runBlock end');
  }

})();
