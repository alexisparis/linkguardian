(function () {
    'use strict';

    angular.module('linkguardianApp')
    .component('linkView',
        {
            // restrict : 'E',
            bindings : {
                link : '='
            },
            templateUrl: 'app/parts/linkTemplate.html',
            // templates: '<div>toto</div>',
            // controllerAs: 'vm',
            controller: ['$scope', 'ToasterService', 'MyLinks', 'translateFilter', '$mdDialog',
                'TEMPLATES_PATH', '$log', '$window',
                function($scope, ToasterService, MyLinks, translateFilter, $mdDialog,
                         TEMPLATES_PATH, $log, $window) {
                var vm = $scope;
                // console.log("ref : ", $ctrl);
                // this.link = this.ref;

                // delete a link
                vm.deleteLink = function (linkId) {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.delete.inProgress'));
                    $log.log("deleting link " + linkId);
                    MyLinks.deleteLink({
                        id: linkId
                    }).$promise.then(
                        function () {
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + linkId + " deleted");
                                ToasterService.displaySuccess(translateFilter('link.messages.delete.success'));
                                removeLinkFromList(linkId);
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
                vm.markAsRead = function (linkId) {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.markAsRead.inProgress'));
                    MyLinks.markAsRead({
                        id: linkId
                    }).$promise.then(
                        function (data) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + linkId + " mark as read");
                                ToasterService.displaySuccess(translateFilter('link.messages.markAsRead.success'));

                                // link is now read, if we filter read, then remove it
                                if ($scope.search.type == 'UNREAD') {
                                    removeLinkFromList(data.link.id);
                                } else {
                                    replaceLinkInList(data.link);
                                }
                            });
                        },
                        function (error) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("error marking as read link " + linkId);
                                ToasterService.displayI18nError('markAsRead', error);
                            });
                        }
                    );
                };

                // mark as unread
                vm.markAsUnread = function (linkId) {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.markAsUnread.inProgress'));

                    MyLinks.markAsUnread({
                        id: linkId
                    }).$promise.then(
                        function (data) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + linkId + " mark as unread");
                                ToasterService.displaySuccess(translateFilter('link.messages.markAsUnread.success'));

                                // link is now unread, if we filter unread, then remove it
                                if ($scope.search.type == 'READ') {
                                    removeLinkFromList(data.link.id);
                                } else {
                                    replaceLinkInList(data.link);
                                }
                            });
                        },
                        function (error) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("error marking as unread link " + linkId);
                                ToasterService.displayI18nError('markAsUnread', error);
                            });
                        }
                    );
                };

                // tweet
                vm.tweet = function (link) {
                    $log.log("ask to tweet " + link);

                    if (link) {
                        // var tweet = "";
                        var tweet = link.title + ' ' + link.url;
                        if (link.tags) {
                            for (var i = 0; i < link.tags.length; i++) {
                                var currentTag = link.tags[i];
                                tweet = tweet + ' #' + currentTag.label;
                            }
                        }

                        // encode it
                        tweet = encodeURIComponent(tweet);
                        window.open("https://twitter.com/intent/tweet?text=" + tweet, '_blank');
                    }
                };

                vm.redirectToLink = function (link) {
                    scope.showLinkLifecycleDialog(link);
                    $window.open(link.original_url, '_blank');
                };

                vm.urlCopiedToClipboard = function(link) {
                    $log.log("url " + link.original_url + " copied to clipboard");
                    ToasterService.displaySuccess(translateFilter('link.actions.copyClipboard.done'));
                };
                vm.copyToClipboardOnError = function(e) {
                    $log.log("error while trying to copy to clipboard", e);
                    ToasterService.displayError(translateFilter('link.actions.copyClipboard.error'));
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
                vm.addTag = function (linkId, tag) {
                    var focus = $document[0].activeElement;
                    console.log("focused : " + focus);
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.addTag.inProgress'));
                    $log.log("adding tag " + tag + " for link " + linkId);
                    MyLinks.addTag({
                        id: linkId,
                        tag: tag
                    }).$promise.then(
                        function (data) {
                            $log.log("tag '" + tag + "' added to link " + linkId);
                            ToasterService.displayI18nSuccess('addTag', data);
                            // ToasterService.displaySuccess(translateFilter('link.messages.addTag.success'));
                            replaceLinkInList(data.link);

                            // force input
                            setTimeout(function () {
                                focus.focus();
                            }, 200);
                        },
                        function (error) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("error adding tag '" + tag + "' to link " + linkId);
                                ToasterService.displayI18nError('addTag', error);
                                replaceLinkInList(error.data.link);
                            });
                        }
                    );
                };

                // remove a tag to a link
                vm.removeTag = function (linkId, tag) {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.removeTag.inProgress'));
                    $log.log("removing tag " + tag + " for link " + linkId);
                    MyLinks.removeTag({
                        id: linkId,
                        tag: tag
                    }).$promise.then(
                        function (data) {
                            $log.log("tag '" + tag + "' removed to link " + linkId);
                            ToasterService.displayI18nSuccess('removeTag', data);
                            // ToasterService.displaySuccess(translateFilter('link.messages.removeTag.success'));
                            if (tag === $scope.search.tag_input) {
                                removeLinkFromList(linkId);
                            } else {
                                replaceLinkInList(data.link);
                            }
                        },
                        function (error) {
                            $log.log("error removing tag '" + tag + "' to link " + linkId);
                            ToasterService.displayI18nError('removeTag', error);
                            replaceLinkInList(data.link);
                        }
                    );
                };

                // change the note
                vm.showStarDialog = function (link) {
                    $mdDialog.show({
                        controller: StarDialogController,
                        templateUrl: TEMPLATES_PATH + 'starDialog.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true,
                        locals: {
                            link: link
                        }
                    })
                        .then(function (rating) {

                            var toast = ToasterService.displaySpinner(translateFilter('link.messages.updateNote.inProgress'));
                            MyLinks.updateNote({
                                id: link.id,
                                score: rating
                            }).$promise.then(
                                function (data) {
                                    ToasterService.hide(toast).then(function () {
                                        $log.log("link " + link.id + " note updated");
                                        ToasterService.displaySuccess(translateFilter('link.messages.updateNote.success'));
                                        // if we sort by note, then, refresh everything
                                        if ($scope.search.sort == 'NOTE') {
                                            $scope.refreshLinks();
                                        } else {
                                            // else replace the link
                                            replaceLinkInList(data.link);
                                        }
                                    });
                                },
                                function (error) {
                                    ToasterService.hide(toast).then(function () {
                                        $log.log("error updating note for link " + link.id);
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

                vm.noteBgColor = function (link) {
                    var rBase = 37;
                    var gBase = 52;
                    var bBase = 72;

                    var note = 0;
                    if (link && link.note) {
                        note = link.note;
                    }

                    return "rgb(" + Math.trunc(((255 - rBase) / 5) * (5 - note) + rBase) +
                        "," + Math.trunc(((255 - gBase) / 5) * (5 - note) + gBase) +
                        "," + Math.trunc(((255 - bBase) / 5) * (5 - note) + bBase) + ")";
                };
                vm.noteColor = function (link) {
                    if (link && link.note && link.note > 0) {
                        return "#000";
                    } else {
                        return "#bdbdbd";
                    }
                };
            }]
        }
    );
})();
