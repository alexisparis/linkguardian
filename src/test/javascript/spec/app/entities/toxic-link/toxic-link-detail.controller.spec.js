'use strict';

describe('Controller Tests', function() {

    describe('ToxicLink Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockToxicLink;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockToxicLink = jasmine.createSpy('MockToxicLink');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'ToxicLink': MockToxicLink
            };
            createController = function() {
                $injector.get('$controller')("ToxicLinkDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'linkguardianApp:toxicLinkUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
