(function () {
    'use strict';

    angular.module('linkguardianApp')
    .component('linkView',
        {
            bindings : {
                link:'=ref',
                linkDeletedCallback:'&',
                linkModifiedCallback:'&',
                mode:'@',
            },
            templateUrl: 'app/parts/linkTemplate.html',
            controllerAs: 'vm',
            controller: ['$scope', 'ToasterService', 'MyLinks', 'translateFilter', '$mdDialog',
                'TEMPLATES_PATH', '$log', '$window', '$mdConstant', 'clipboard', '$document',
                function($scope, ToasterService, MyLinks, translateFilter, $mdDialog,
                         TEMPLATES_PATH, $log, $window, $mdConstant, clipboard, $document) {
                var vm = this;

                vm.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.COMMA];

                console.log("mode : " + vm.mode);
                console.log("mode 2 : " + $scope.mode);

                vm.forceCloseLinkMenu = function() {
                    $scope.$root.$broadcast('close-all-link-circular-menu-event');
                };

                // delete a link
                vm.deleteLink = function () {
                    vm.forceCloseLinkMenu();
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.delete.inProgress'));
                    $log.log("deleting link " + vm.link.id);
                    MyLinks.deleteLink({
                        id: vm.link.id
                    }).$promise.then(
                        function () {
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + vm.link.id + " deleted");
                                ToasterService.displaySuccess(translateFilter('link.messages.delete.success'));
                                vm.linkDeletedCallback({link : vm.link});
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
                                vm.linkModifiedCallback({link : data.link});
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
                                vm.linkModifiedCallback({link : data.link});
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

                vm.lock = function() {
                    $log.log("calling lock with id " + vm.link.id);
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.markAsLocked.inProgress'));
                    MyLinks.markAsLocked({
                        id: vm.link.id
                    }).$promise.then(
                        function (data) {
                            console.log("data", data);
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + vm.link.id + " mark as locked");
                                ToasterService.displaySuccess(translateFilter('link.messages.markAsLocked.success'));
                                vm.linkModifiedCallback({link : data.link});
                            });
                        },
                        function (error) {
                            $log.error("error when calling lock", error);
                            ToasterService.hide(toast).then(function () {
                                $log.log("error marking as locked link " + vm.link.id);
                                ToasterService.displayI18nError('markAsLocked', error);
                            });
                        }
                    );
                };

                vm.unlock = function() {
                    $log.log("calling unlock with id " + vm.link.id);
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.markAsUnlocked.inProgress'));
                    MyLinks.markAsUnlocked({
                        id: vm.link.id
                    }).$promise.then(
                        function (data) {
                            ToasterService.hide(toast).then(function () {
                                $log.log("link " + vm.link.id + " mark as unlocked");
                                ToasterService.displaySuccess(translateFilter('link.messages.markAsUnlocked.success'));
                                vm.linkModifiedCallback({link : data.link});
                            });
                        },
                        function (error) {
                            $log.error("error when calling unlock", error);
                            ToasterService.hide(toast).then(function () {
                                $log.log("error marking as unlocked link " + vm.link.id);
                                ToasterService.displayI18nError('markAsUnlocked', error);
                            });
                        }
                    );
                };

                vm.copyUrlToClipboard = function() {
                    try {
                        clipboard.copyText(vm.link.original_url);
                        $log.log("url " + vm.link.original_url + " copied to clipboard");
                        ToasterService.displaySuccess(translateFilter('link.actions.copyClipboard.done'));
                    } catch (e) {
                        $log.log("error while trying to copy to clipboard", e);
                        ToasterService.displayError(translateFilter('link.actions.copyClipboard.error'));
                    }
                }

                vm.options = {
                    content: 'Menu',
                    isOpen: false,
                    toggleOnClick: true,
                    size: 'small',
                    background: 'red',
                    button : {
                      content: 'aa',
                      size: 'small'
                    },
                    items: [
                        {
                            content: 'About',
                            onclick: function () {console.log('About');},
                            cssClass : 'ion-trash-a'

                        },
                        {
                            content: 'How',
                            onclick: function () {console.log('About');},
                            cssClass : 'ion-trash-a'
                        }
                    ]
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
                    try {
                        $window.open(vm.link.original_url, '_blank');
                        vm.showLinkLifecycleDialog(vm.link);
                    } catch(err) {
                        ToasterService.displayError(translateFilter('link.actions.redirect.opening.error'));
                        console.log("error", err);
                    }
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
                    vm.forceCloseLinkMenu();
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
                    vm.forceCloseLinkMenu();
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
                            vm.linkModifiedCallback({link : data.link});

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
                    vm.forceCloseLinkMenu();
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
                            vm.linkModifiedCallback({link : data.link});
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
                    vm.forceCloseLinkMenu();
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
                                        vm.linkModifiedCallback({link : data.link});
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

                vm.onMenuClick = function(menu) {
                    console.log("menu clicked : ", menu);
                    menu.callback();
                };

                vm.menuConfig = {
                    "buttonWidth": 40,
                    "menuRadius": 100,
                    "color": "#3A99D8",
                    "offset":5,
                    "textColor": "#ffffff",
                    "showIcons":true,
                    "onlyIcon":true,
                    "textAndIcon": false,
                    "heights" :"100px",
                    "widths" : "100px",
                    "gutter": {
                        "top": 130,
                        "right": 30,
                        "bottom": 30,
                        "left": 30
                    },
                    "angles": {
                        "bottomRight": 180
                    }
                };
                vm.menuItems = new Array();
                vm.buildMenuItems = function() {
                    vm.menuItems.push({
                        "title": "delete",
                        "tooltip": 'link.actions.delete.tooltip',
                        "color": "#EB7260",
                        "show": 0,
                        "titleColor": "#fff",
                        "icon": {
                            "color": "#fff",
                            "name": "fas fa-trash",
                            "size": 20
                        },
                        "callback": vm.deleteLink
                    });
                    vm.menuItems.push({
                        "title": "tweet",
                        "tooltip": 'link.actions.tweetIt.tooltip',
                        "color": "#3998D7",
                        "show": 0,
                        "titleColor": "#000",
                        "icon": {
                            "color": "#fff",
                            "name": "fab fa-twitter",
                            "size": 20
                        },
                        "callback": vm.tweet
                    });
                    vm.menuItems.push({
                        "title": "copyToClipboard",
                        "tooltip": 'link.actions.copyClipboard.tooltip',
                        "color": "#49A8E7",
                        //"rotate": 90,
                        "show": 0,
                        "titleColor": "#fff",
                        "icon": {
                            "color": "#fff",
                            "name": "fas fa-copy",
                            "size": 20
                        },
                        "callback": vm.copyUrlToClipboard
                    });
                    vm.menuItems.push({
                        "title": "markAsUnread",
                        "tooltip": 'link.actions.markAsUnread.tooltip',
                        "color": "#59B8F7",
                        "show": 0,
                        "titleColor": "#fff",
                        "icon": {
                            "color": "#fff",
                            "name": "fas fa-eye-slash",
                            "size": 20
                        },
                        "callback": vm.markAsUnread,
                        "isVisible" : function() {
                            return vm.link.read;
                        }
                    });
                    vm.menuItems.push({
                        "title": "markAsRead",
                        "tooltip": 'link.actions.markAsRead.tooltip',
                        "color": "#69C8FF",
                        "show": 0,
                        "titleColor": "#fff",
                        "icon": {
                            "color": "#fff",
                            "name": "fas fa-eye",
                            "size": 20
                        },
                        "callback": vm.markAsRead,
                        "isVisible" : function() {
                            return !vm.link.read;
                        }
                    });
                    vm.menuItems.push({
                        "title": "goTo",
                        "tooltip": 'link.actions.goTo.tooltip',
                        "color": "#79D8FF",
                        "show": 0,
                        "titleColor": "#fff",
                        "icon": {
                            "color": "#fff",
                            "name": "fas fa-arrow-righta fa-globe",
                            "size": 20
                        },
                        "callback": vm.redirectToLink
                    });
                    vm.angle = 120 / (vm.menuItems.length - 1);
                };
                vm.buildMenuItems();

            }]
        }
    );
})();
