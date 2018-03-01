'use strict';

describe('Controller Tests', function() {

    describe('BookmarkBatch Management Detail Controller', function() {
        var $scope, $rootScope;
        var MockEntity, MockPreviousState, MockBookmarkBatch, MockUser, MockBookmarkBatchItem;
        var createController;

        beforeEach(inject(function($injector) {
            $rootScope = $injector.get('$rootScope');
            $scope = $rootScope.$new();
            MockEntity = jasmine.createSpy('MockEntity');
            MockPreviousState = jasmine.createSpy('MockPreviousState');
            MockBookmarkBatch = jasmine.createSpy('MockBookmarkBatch');
            MockUser = jasmine.createSpy('MockUser');
            MockBookmarkBatchItem = jasmine.createSpy('MockBookmarkBatchItem');
            

            var locals = {
                '$scope': $scope,
                '$rootScope': $rootScope,
                'entity': MockEntity,
                'previousState': MockPreviousState,
                'BookmarkBatch': MockBookmarkBatch,
                'User': MockUser,
                'BookmarkBatchItem': MockBookmarkBatchItem
            };
            createController = function() {
                $injector.get('$controller')("BookmarkBatchDetailController", locals);
            };
        }));


        describe('Root Scope Listening', function() {
            it('Unregisters root scope listener upon scope destruction', function() {
                var eventType = 'linkguardianApp:bookmarkBatchUpdate';

                createController();
                expect($rootScope.$$listenerCount[eventType]).toEqual(1);

                $scope.$destroy();
                expect($rootScope.$$listenerCount[eventType]).toBeUndefined();
            });
        });
    });

});
