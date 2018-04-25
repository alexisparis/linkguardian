'use strict';

angular.module('linkguardianApp')

    .controller('MainController', function ($scope, $rootScope, Principal, $mdConstant, $window,
                                            MyLinks, $timeout, $mdDialog, translateFilter,
                                            SearchTags, $state, $log, TEMPLATES_PATH, $document, freeTour,
                                            DemoService, ToasterService, UserInitiation) {

        $scope.panelSearchOpened = true;
        $scope.panelAddOpened = true;

        $scope.newLink = {};

        $scope.links = [];

        $scope.search = {
            tag: '',
            type: 'UNREAD',
            sort: 'CREATION_DATE',
            sort_direction: 'DESC'
        };

        // check if user is initiated (has created some links, imported some bookmarks)
        // if it is not the case, propose him to onboard
        UserInitiation.isInitiated().$promise.then(
            function(data) {
                if (data.state == true) {
                    //$state.go('free_tour');
                }
            }, function(error) {
                $log.log("could not determine if user is initiated... quit");
            });

        $scope.isAuthenticated = Principal.isAuthenticated;

        if (freeTour === true) {

            DemoService.bind($scope, $state);

            $scope.onboardingIndex = 0;
            $scope.onboardingEnabled = freeTour;

            // fix : in production, onboarding is not displayed due to ng-hide added...
            // todo : find a better solution
            $timeout(
                function() {
                    angular.element(".onboarding-container").removeClass("ng-hide");
                }, 100
            );
        } else {

            $scope.onboardingIndex = 0;
            $scope.onboardingSteps = [{}];

            $log.log("user authenticated ? " + $scope.isAuthenticated());
            if ($scope.isAuthenticated() === false) {
                $state.go("login");
            } else {
                // empty demo initialization to avoid error from ng-onboarding
                $scope.onboardingFinished = function() {};

                Principal.identity().then(function (account) {
                    $scope.account = account;
                    $scope.isAuthenticated = Principal.isAuthenticated;
                });

                $scope.keys = [$mdConstant.KEY_CODE.ENTER, $mdConstant.KEY_CODE.COMMA];

                // ####################
                // last search performed application
                // ####################

                $scope.loadSearchParameters = function() {
                    if ($window.localStorage) {
                        var search = $window.localStorage.getItem("lg.lastSearchPerformed");
                        if (search != null) {
                            $scope.search = JSON.parse(search);
                        }
                    }
                };
                $scope.storeSearchParameters = function() {
                    if ($window.localStorage) {
                        $window.localStorage.setItem("lg.lastSearchPerformed", JSON.stringify($scope.search));
                    }
                };
                $scope.loadSearchParameters();

                // ####################
                // openers methods
                // ####################

                $scope.initPanelOpenersStatus = function() {
                    if ($window.localStorage) {
                        var statusSearch = $window.localStorage.getItem("lg.panelSearchOpened");
                        if (statusSearch != null) {
                            $scope.panelSearchOpened = (statusSearch == 'true');
                        }
                        var statusAdd = $window.localStorage.getItem("lg.panelAddOpened");
                        if (statusAdd != null) {
                            $scope.panelAddOpened = (statusAdd == 'true');
                        }
                    }
                };
                $scope.storeOpenersStatus = function() {
                    if ($window.localStorage) {
                        $window.localStorage.setItem("lg.panelSearchOpened", $scope.panelSearchOpened);
                        $window.localStorage.setItem("lg.panelAddOpened", $scope.panelAddOpened);
                    }
                };
                $scope.panelAddOpenerClicked = function() {
                    $scope.panelAddOpened = $scope.panelAddOpened != true;
                    $scope.storeOpenersStatus();
                };
                $scope.isPanelAddOpened = function() {
                    return $scope.panelSearchOpened;
                };
                $scope.panelSearchOpenerClicked = function() {
                    $scope.panelSearchOpened = $scope.panelSearchOpened != true;
                    $scope.storeOpenersStatus();
                };

                // manage open status of search and add panels
                $scope.$watch('panelSearchOpened', function (newValue, oldValue) {
                    if ($window.innerWidth >= 600) {
                        $scope.panelAddOpened = newValue;
                        $scope.storeOpenersStatus();
                    }
                });
                $scope.$watch('panelAddOpened', function (newValue, oldValue) {
                    if ($window.innerWidth >= 600) {
                        $scope.panelSearchOpened = newValue;
                        $scope.storeOpenersStatus();
                    }
                });
                var refreshOpenersStatusOnResizeEvent = function () {
                    if ($window.innerWidth >= 600) {
                        if ($scope.panelAddOpened == false || $scope.panelSearchOpened == false) {
                            $scope.panelAddOpened = false;
                            $scope.panelSearchOpened = false;
                            $scope.storeOpenersStatus();
                        }
                    }
                };
                $scope.initPanelOpenersStatus();
                angular.element($window).on('resize', refreshOpenersStatusOnResizeEvent);
                refreshOpenersStatusOnResizeEvent();

                // ####################
                // business methods
                // ####################

                /**
                 * return the index of the link with the given id
                 * @param id
                 * @returns the index
                 */
                function findIndexOfLinkWithId(id) {
                    var result = undefined;
                    if ($scope.links) {
                        for (var i = 0; i < $scope.links.length; i++) {

                            var current = $scope.links[i];

                            if (current && current.id === id) {
                                result = i;
                                break;
                            }
                        }
                    }

                    return result;
                };

                /**
                 * return the link with the given id
                 * @param id
                 * @returns {undefined}
                 */
                function findLinkWithId(id) {
                    var result = undefined;
                    var index = findIndexOfLinkWithId(id);
                    if (angular.isDefined(index)) {
                        result = $scope.links[index];
                    }

                    return result;
                };

                /**
                 * remove the link from the list
                 * @param linkId the id of the link
                 */
                function removeLinkFromList(linkId) {
                    var position = findIndexOfLinkWithId(linkId);
                    if (angular.isDefined(position)) {
                        $scope.links.splice(position, 1);
                    }
                }

                /**
                 * method called when a modification on a link has been detected
                 * it refresh the link without refreshing all the data
                 * it can eventually remove the link for example if the link is read and we filter the read links
                 * @param newLink
                 */
                function replaceLinkInList(newLink) {
                    // for each properties
                    var index = findIndexOfLinkWithId(newLink.id);
                    if (angular.isDefined(index)) {

                        var currentLink = $scope.links[index];

                        var properties = [
                            'creation_date',
                            'description',
                            'domain',
                            'locked',
                            'note',
                            'original_url',
                            'read',
                            'tags',
                            'title',
                            'url',
                            'user'];

                        for (var propIndex = 0; propIndex < properties.length; propIndex++) {
                            var currentProperty = properties[propIndex];
                            currentLink[currentProperty] = newLink[currentProperty];
                        }

                        postInitializeLink(currentLink);
                    }
                };

                // ####################
                // add new link
                // ####################

                $scope.addUrlInProgress = false;

                var addLinkSuccessCallbackFactory = function(toast) {
                    return function (link) {
                        if (link && link.status === 208) {
                            ToasterService.displayError(translateFilter('main.addLink.messages.linkExists'));
                            $scope.addUrlInProgress = false;
                        } else {
                            ToasterService.hide(toast).then(function () {
                                $log.log("new link " + $scope.newLink.newurl + " added");
                                $scope.newLink = {};
                                ToasterService.displaySuccess(translateFilter('link.messages.add.success'));
                                $scope.addUrlInProgress = false;

                                $log.log("type == ALL or UNREAD ? " + ($scope.search.type === 'ALL' || $scope.search.type === 'UNREAD'));
                                $log.log("sort == " + $scope.search.sort);
                                $log.log("sort direction == " + $scope.search.sort_direction);
                                $log.log("tag empty ? " + (!$scope.search.tag || $scope.search.tag === ''));
                                if (link && link.link &&
                                    ($scope.search.type === 'ALL' || $scope.search.type === 'UNREAD') &&
                                    $scope.search.sort === 'CREATION_DATE' &&
                                    $scope.search.sort_direction === 'DESC' &&
                                    (!$scope.search.tag || $scope.search.tag === '')) {
                                    postInitializeLink(link.link);
                                    $log.log("==> simply insert tag");

                                    $scope.links.splice(0, 0, link.link);
                                } else if ($scope.search.type === 'READ') {
                                    $log.log("==> read filter set, no need to reload");
                                    // no need to refresh
                                } else {
                                    $log.log("==> reload everything");
                                    $scope.refreshLinks();
                                }
                            });
                        }
                    };
                };

                var addLinkErrorCallbackFactory = function(toast, proposeManualAdd) {
                    return function (error) {
                        console.log("error : ", error);
                        ToasterService.hide(toast).then(function () {
                            $log.log("error while adding link " + JSON.stringify($scope.newLink) + " : " + error);
                            if (error && error.status == 423) {
                                ToasterService.displayError(translateFilter('error.423'));
                            } else {
                                ToasterService.displayI18nError('add', error);

                                if (proposeManualAdd) {
                                    // ask user if he wants to add it manually
                                    $scope.askAddUrlManuallyQuestion(error.data.link);
                                }
                            }
                            $scope.addUrlInProgress = false;
                        });
                    };
                };

                $scope.addLink = function () {
                    var toast = ToasterService.displaySpinner(translateFilter('link.messages.add.inProgress'));
                    $scope.addUrlInProgress = true;
                    $log.log("adding new link : " + JSON.stringify($scope.newLink));
                    MyLinks.addLink({
                        newurl: $scope.newLink.newurl,
                        tag: $scope.newLink.tag
                    }).$promise.then(addLinkSuccessCallbackFactory(toast), addLinkErrorCallbackFactory(toast, true));
                };

                // ####################
                // links loading
                // ####################

                $scope.tagsAjaxRequest = function () {
                    var promise = SearchTags.tags({
                        filter: $scope.search.tag_input
                    }).$promise;

                    // promise.then(function(data) {
                    //     $log.log("tags : " + JSON.stringify(data));
                    // });

                    return promise;
                };

                $scope.tagTextChanged = function () {
                    $scope.refreshLinks();
                };

                $scope.searchInProgress = false;
                $scope.noResults = false;

                $scope.keyUpOnFilterTag = function (event) {
                    if (event.keyCode == 13) {
                        $scope.refreshLinks();
                    }
                };

                $scope.noMoreLinks = false;
                $scope.currentPage = 0;

                // when click on search
                $scope.refreshLinks = function () {
                    $log.log("call refreshlinks");
                    // TODO : remove when adding a new link seems to force to refresh all links
                    //console.trace();
                    $scope.storeSearchParameters();

                    if ($scope.isAuthenticated() == true) {
                        $scope.noMoreLinks = false;
                        $scope.searchInProgress = false;
                        //angular.element("#dynamic-layout-parent").css('height', '0px');
                        angular.element(".dynamic-grid").css('height', '0px');
                        $scope.links.splice(0, $scope.links.length);
                        $scope.currentPage = 0;
                        $scope.loadAdditionalLinks();
                    } else {
                        $state.go("login");
                    }
                };

                $scope.searchCriteriaChanged = function () {
                    // refresh or not refresh
                    $scope.refreshLinks();
                };

                var linkPageSize = 50;
                // if mobile
                if ($window.innerWidth <= 800 && $window.innerHeight <= 600) {
                    linkPageSize = 5;
                }

                // complete a link
                var postInitializeLink = function (currentLink) {
                    if (currentLink) {
                        currentLink.computedTags = new Array();

                        if (currentLink.tags) {
                            for (var j = 0; j < currentLink.tags.length; j++) {
                                currentLink.computedTags.push(currentLink.tags[j].label);
                            }
                        }
                    }
                };

                $scope.loadAdditionalLinks = function () {

                    if ($scope.searchInProgress == false && $scope.noMoreLinks == false) {
                        $scope.noResults = false;
                        $scope.searchInProgress = true;
                        // var toast = displaySpinner(translateFilter('link.messages.list.inProgress'), false);

                        $log.log("load additional links with tag " + $scope.search.tag_input);

                        setTimeout(function () {
                            $log.log("call get by page for " + linkPageSize + " items");
                            MyLinks.getByPage(
                                {
                                    token: $scope.search.tag_input,
                                    read_status: $scope.search.type,
                                    sortBy: $scope.search.sort,
                                    sortType: $scope.search.sort_direction,
                                    page: $scope.currentPage++,
                                    size: linkPageSize
                                }).$promise.then(function (links) {
                                if (links) {

                                    if (links.length < linkPageSize) {
                                        $scope.noMoreLinks = true;
                                    }

                                    for (var i = 0; i < links.length; i++) {
                                        var currentLink = links[i];

                                        postInitializeLink(currentLink);
                                    }

                                    $scope.links = $scope.links.concat(links);
                                }
                                if ($scope.links.length == 0) {
                                    $scope.noResults = true;
                                }
                                $scope.searchInProgress = false;
                                // ToasterService.hide(toast);
                                // $log.log("hiding toast");
                            }, function (error) {
                                ToasterService.displayI18nError('list', error);
                                $scope.searchInProgress = false;
                                // ToasterService.hide(toast);
                                // $log.log("hiding toast");
                            });
                        }, 510);
                    } else {

                        if ($scope.searchInProgress == true) {
                            $log.log("call to loadAdditionalLinks ignored => already running");
                        }
                        if ($scope.noMoreLinks == true) {
                            $log.log("no more links to load => ignored");
                        }
                    }
                };

                // ####################
                // link modification
                // ####################

                // delete a link
                $scope.deleteLink = function (linkId) {
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
                $scope.markAsRead = function (linkId) {
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
                $scope.markAsUnread = function (linkId) {
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
                $scope.tweet = function (linkId) {
                    $log.log("ask to tweet " + linkId);

                    // find the tweet...
                    var link = findLinkWithId(linkId);

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

                // add a tag to a link
                $scope.addTag = function (linkId, tag) {
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
                $scope.removeTag = function (linkId, tag) {
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

                // ask to add manually
                $scope.askAddUrlManuallyQuestion = function (link) {
                    $mdDialog.show({
                        controller: AddUrlManuallyQuestionDialogController,
                        templateUrl: TEMPLATES_PATH + 'addUrlManuallyQuestion.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true,
                        locals: {
                            link: link
                        }
                    })
                        .then(function () {
                            $log.log("opening manually adding link dialog for " + link.original_url + " ...");
                            $scope.showAddUrlManuallyDialog(link);
                        }, function () {
                            $log.log("manually adding link " + link.original_url + " canceled");
                        });
                };

                function AddUrlManuallyQuestionDialogController($scope, $mdDialog, link) {
                    $scope.hide = function () {
                        $mdDialog.hide();
                    };
                    $scope.cancel = function () {
                        $mdDialog.cancel();
                    };
                    $scope.ok = function () {
                        $mdDialog.hide();
                    };
                };

                // add manually
                $scope.showAddUrlManuallyDialog = function (link) {
                    $mdDialog.show({
                        controller: AddUrlManuallyDialogController,
                        templateUrl: TEMPLATES_PATH + 'addUrlManuallyDialog.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true,
                        locals: {
                            link: link
                        }
                    })
                        .then(function (link) {



                        }, function () {
                        });
                };

                function AddUrlManuallyDialogController($scope, $mdDialog, link) {

                    $scope.url = link.original_url;
                    $scope.description = '';

                    $scope.hide = function () {
                        $mdDialog.hide();
                    };
                    $scope.cancel = function () {
                        $mdDialog.cancel();
                    };
                    $scope.save = function () {

                        $mdDialog.hide(link);

                        var toast = ToasterService.displaySpinner(translateFilter('link.messages.add.inProgress'));
                        $scope.addUrlInProgress = true;
                        $log.log("adding new link : " + JSON.stringify($scope.newLink));
                        MyLinks.addLinkManually({
                            newurl: $scope.url,
                            tag: '',
                            description: $scope.description
                        }).$promise.then(addLinkSuccessCallbackFactory(toast), addLinkErrorCallbackFactory(toast, false));
                    };
                };

                // change the note
                $scope.showStarDialog = function (link) {
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

                $scope.noteBgColor = function (link) {
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
                $scope.noteColor = function (link) {
                    if (link.note && link.note > 0) {
                        return "#000";
                    } else {
                        return "#bdbdbd";
                    }
                };

                // ####################
                // cloud of tags
                // ####################

                function CloudOfTagController($scope, $mdDialog) {
                    // $scope.tags;
                    $scope.tagsLoaded = false;
                    $scope.hide = function () {
                        $mdDialog.hide();
                    };
                    $scope.applyFilter = function (v) {
                        $mdDialog.hide(v);
                    };

                    MyLinks.countPerTags().$promise.then(
                        function (counts) {
                            var tags = new Array();

                            var handlersGenerator = function (tag) {
                                return {
                                    click: function () {
                                        $scope.applyFilter(tag);
                                    }
                                };
                            }

                            for (var i = 0; i < counts.length; i++) {
                                var current = counts[i];
                                tags.push({
                                    text: current.tag,
                                    weight: current.count,
                                    handlers: handlersGenerator(current.tag)
                                })
                            }

                            $scope.tags = tags;
                            setTimeout(function () {
                                $scope.tagsLoaded = true;
                            }, 1);
                        });
                };

                $scope.openCloudOfTagDialog = function () {
                    $mdDialog.show({
                        controller: CloudOfTagController,
                        templateUrl: TEMPLATES_PATH + 'cloudOfTagsDialog.html',
                        parent: angular.element(document.body),
                        clickOutsideToClose: true
                    })
                        .then(function (t) {
                            if (t) {
                                $scope.search.tag_input = t;
                                $scope.search.tag = t;
                                $scope.searchCriteriaChanged();
                                $scope.refreshLinks();
                            }
                        }, function () {

                        });
                };

                $scope.redirectToLink = function (link) {
                    $scope.showLinkLifecycleDialog(link);
                    $window.open(link.original_url, '_blank');
                };

                $scope.urlCopiedToClipboard = function(link) {
                    $log.log("url " + link.original_url + " copied to clipboard");
                    ToasterService.displaySuccess(translateFilter('link.actions.copyClipboard.done'));
                };
                $scope.copyTocClipboardOnError = function(e) {
                    $log.log("error while trying to copy to clipboard", e);
                    ToasterService.displayError(translateFilter('link.actions.copyClipboard.error'));
                };

                $scope.showLinkLifecycleDialog = function (link) {
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
                var mainScope = $scope;

                function LinkLifecycleDialogController($scope, $mdDialog, link) {
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

                // // force to load links, especially for mobile
                // $scope.loadAdditionalLinks();
            }
        }
    });
