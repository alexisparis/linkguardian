(function () {
    'use strict';

    angular.module('linkguardianApp')
    .component('linkView',
        {
            bindings : {
                link : '=ref',
                linkDeletedCallback : '&',
                linkModifiedCallback : '&',
            },
            templateUrl: 'app/parts/linkTemplate.html',
            controllerAs: 'vm',
            controller: ['$scope', 'ToasterService', 'MyLinks', 'translateFilter', '$mdDialog',
                'TEMPLATES_PATH', '$log', '$window', '$mdConstant',
                function($scope, ToasterService, MyLinks, translateFilter, $mdDialog,
                         TEMPLATES_PATH, $log, $window, $mdConstant) {
                var vm = this;

                vm.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.COMMA];

                // delete a link
                vm.deleteLink = function () {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.delete.inProgress'));
                    $log.log("deleting link " + vm.link.id);
                    MyLinks.deleteLink({
                        id: vm.link.id
                    }).$promise.then(
                        function () {
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + vm.link.id + " deleted");
                                ToasterService.displaySuccess(translateFilter('link.messages.delete.success'));
                                vm.linkDeletedCallback();
                            });
                        },
                        function (error) {
                            ToasterService.hide(toast).then(function () {
                                ToasterService.displayI18nError('delete', error);
                            });
                        }
                    );
                };

                // mark as read
                vm.markAsRead = function () {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.markAsRead.inProgress'));
                    MyLinks.markAsRead({
                        id: vm.link.id
                    }).$promise.then(
                        function (data) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + vm.link.id + " mark as read");
                                ToasterService.displaySuccess(translateFilter('link.messages.markAsRead.success'));
                                vm.linkModifiedCallback();
                            });
                        },
                        function (error) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("error marking as read link " + vm.link.id);
                                ToasterService.displayI18nError('markAsRead', error);
                            });
                        }
                    );
                };

                // mark as unread
                vm.markAsUnread = function () {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.markAsUnread.inProgress'));

                    MyLinks.markAsUnread({
                        id: vm.link.id
                    }).$promise.then(
                        function (data) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + vm.link.id + " mark as unread");
                                ToasterService.displaySuccess(translateFilter('link.messages.markAsUnread.success'));
                                vm.linkModifiedCallback();
                            });
                        },
                        function (error) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("error marking as unread link " + vm.link.id);
                                ToasterService.displayI18nError('markAsUnread', error);
                            });
                        }
                    );
                };

                vm.urlCopiedToClipboard = function() {
                    $log.log("url " + vm.link.original_url + " copied to clipboard");
                    ToasterService.displaySuccess(translateFilter('link.actions.copyClipboard.done'));
                };
                vm.copyToClipboardOnError = function(e) {
                    $log.log("error while trying to copy to clipboard", e);
                    ToasterService.displayError(translateFilter('link.actions.copyClipboard.error'));
                };

                // tweet
                vm.tweet = function () {
                    $log.log("ask to tweet " + vm.link);

                    if (vm.link) {
                        // var tweet = "";
                        var tweet = vm.link.title + ' ' + vm.link.url;
                        if (vm.link.tags) {
                            for (var i = 0; i < vm.link.tags.length; i++) {
                                var currentTag = vm.link.tags[i];
                                tweet = tweet + ' #' + currentTag.label;
                            }
                        }

                        // encode it
                        tweet = encodeURIComponent(tweet);
                        window.open("https://twitter.com/intent/tweet?text=" + tweet, '_blank');
                    }
                };

                vm.redirectToLink = function () {
                    scope.showLinkLifecycleDialog(vm.link);
                    $window.open(link.original_url, '_blank');
                };

                var LinkLifecycleDialogController = function($scope, $mdDialog, link) {
                    $scope.link = link;
                    $scope.rating = 0;
                    if (link.note && link.note >= 1 && link.note <= 5) {
                        $scope.rating = link.note;
                    }

                    $scope.cancel = function () {
                        $mdDialog.cancel();
                    };
                    $scope.markLinkAsRead = function () {
                        mainScope.markAsRead($scope.link.id);
                        $mdDialog.cancel();
                    };
                    $scope.deleteLink = function () {
                        mainScope.deleteLink($scope.link.id);
                        $mdDialog.cancel();
                    };
                    $scope.doNothing = function () {
                        $mdDialog.hide();
                    };
                };

                vm.showLinkLifecycleDialog = function (link) {
                    $mdDialog.show({
                        controller: LinkLifecycleDialogController,
                        templateUrl: TEMPLATES_PATH + 'linkLifecycleDialog.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true,
                        locals: {
                            link: link
                        }
                    });
                };

                // add a tag to a link
                vm.addTag = function (tag) {
                    var focus = $document[0].activeElement;
                    console.log("focused : " + focus);
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.addTag.inProgress'));
                    $log.log("adding tag " + tag + " for link " + vm.link.id);
                    MyLinks.addTag({
                        id: vm.link.id,
                        tag: tag
                    }).$promise.then(
                        function (data) {
                            $log.log("tag '" + tag + "' added to link " + vm.link.id);
                            ToasterService.displayI18nSuccess('addTag', data);
                            vm.linkModifiedCallback();

                            // force input
                            setTimeout(function () {
                                focus.focus();
                            }, 200);
                        },
                        function (error) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("error adding tag '" + tag + "' to link " + vm.link.id);
                                ToasterService.displayI18nError('addTag', error);
                                vm.linkModifiedCallback();
                            });
                        }
                    );
                };

                // remove a tag to a link
                vm.removeTag = function (tag) {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.removeTag.inProgress'));
                    $log.log("removing tag " + tag + " for link " + vm.link.id);
                    MyLinks.removeTag({
                        id: vm.link.id,
                        tag: tag
                    }).$promise.then(
                        function (data) {
                            $log.log("tag '" + tag + "' removed to link " + vm.link.id);
                            ToasterService.displayI18nSuccess('removeTag', data);
                            // ToasterService.displaySuccess(translateFilter('link.messages.removeTag.success'));
                            vm.linkModifiedCallback();
                        },
                        function (error) {
                            $log.log("error removing tag '" + tag + "' to link " + vm.link.id);
                            ToasterService.displayI18nError('removeTag', error);
                            vm.linkModifiedCallback();
                        }
                    );
                };

                // change the note
                vm.showStarDialog = function () {
                    $mdDialog.show({
                        controller: StarDialogController,
                        templateUrl: TEMPLATES_PATH + 'starDialog.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true,
                        locals: {
                            link: vm.link
                        }
                    })
                        .then(function (rating) {

                            var toast = ToasterService.displaySpinner(translateFilter('link.messages.updateNote.inProgress'));
                            MyLinks.updateNote({
                                id: vm.link.id,
                                score: rating
                            }).$promise.then(
                                function (data) {
                                    ToasterService.hide(toast).then(function () {
                                        $log.log("link " + vm.link.id + " note updated");
                                        ToasterService.displaySuccess(translateFilter('link.messages.updateNote.success'));
                                        // if we sort by note, then, refresh everything
                                        vm.linkModifiedCallback();
                                    });
                                },
                                function (error) {
                                    ToasterService.hide(toast).then(function () {
                                        $log.log("error updating note for link " + vm.link.id);
                                        ToasterService.displayI18nError('updateNote', error);
                                    });
                                }
                            );
                        }, function () {
                        });
                };

                function StarDialogController($scope, $mdDialog, link) {
                    $scope.rating = 0;
                    if (link.note && link.note >= 1 && link.note <= 5) {
                        $scope.rating = link.note;
                    }
                    $scope.hide = function () {
                        $mdDialog.hide();
                    };
                    $scope.unset = function () {
                        $scope.rating = undefined;
                    };
                    $scope.cancel = function () {
                        $mdDialog.cancel();
                    };
                    $scope.ok = function () {
                        $mdDialog.hide($scope.rating);
                    };
                };

                vm.noteBgColor = function () {
                    var rBase = 37;
                    var gBase = 52;
                    var bBase = 72;

                    var note = 0;
                    if (vm.link && vm.link.note) {
                        note = vm.link.note;
                    }

                    return "rgb(" + Math.trunc(((255 - rBase) / 5) * (5 - note) + rBase) +
                        "," + Math.trunc(((255 - gBase) / 5) * (5 - note) + gBase) +
                        "," + Math.trunc(((255 - bBase) / 5) * (5 - note) + bBase) + ")";
                };
                vm.noteColor = function () {
                    if (vm.link && vm.link.note && vm.link.note > 0) {
                        return "#000";
                    } else {
                        return "#bdbdbd";
                    }
                };
            }]
        }
    );
})();
