'use strict';

angular.module('linkguardianApp')
    .controller('ToolsController', function ($scope, $translate, tmhDynamicLocale,
                                             $cookies, $mdDialog, TEMPLATES_PATH, Bookmark, $log,
                                             translateFilter, ToasterService, AuthServerProvider) {

        function BookmarkImportDialogController($scope, $mdDialog,
                                                FileUploader, $timeout, csrf_token) {

            // csrf token mandatory to do the file upload to the server
            // var csrf_token = $cookies.get("CSRF-TOKEN");
            $scope.safeApply = function(fn) {
                var phase = this.$root.$$phase;
                if(phase == '$apply' || phase == '$digest') {
                    if(fn && (typeof(fn) === 'function')) {
                        fn();
                    }
                } else {
                    this.$apply(fn);
                }
            };

            $scope.bookmarks = new Array();

            $scope.step = "choose-file";
            // $scope.step = "links-customization";
            // $scope.step = "upload-in-progress";
            // $scope.step = "upload-finished";
            // $scope.step = "upload-failed";

            $scope.uploadInProgress = false;

            $scope.uploader = new FileUploader({
                url: '/api/bookmarks/parse',
                headers : {
                    //'X-CSRF-TOKEN': csrf_token,
                    'Authorization': 'Bearer ' + AuthServerProvider.getToken()
                },
                alias: 'file',
                autoUpload : true
            });
            $scope.uploader.filters.push({
                name: 'typeFilter',
                fn: function(item /*{File|FileLikeObject}*/, options) {
                    var type = '|' + item.type.slice(item.type.lastIndexOf('/') + 1) + '|';
                    return '|html|HTML|htm|HTM|xhtml|XHTML|xhtm|xHTM|'.indexOf(type) !== -1;
                },
                errorMsg : "bookmarks.import.messages.wrongFileType"
            });
            $scope.uploader.filters.push({
                'name': 'enforceMaxFileSize',
                'fn': function (item) {
                    return item.size <= 2097152; // 2 MiB to bytes
                },
                errorMsg : "bookmarks.import.messages.fileSizeExceeded"
            });

            $scope.resetFileUpload = function() {
                $scope.safeApply(function () {
                    // needed if user try to send the same file.
                    // if value of input is not reset, the file is not re-uploaded
                    angular.forEach(
                        angular.element("input[type='file']"),
                        function (inputElem) {
                            angular.element(inputElem).val(null);
                        }
                    );
                    $scope.uploadInProgress = false;
                    $scope.uploader.progress = 0;
                    $scope.uploader.clearQueue();
                    $scope.uploader.cancelAll();
                });
            };

            // CALLBACKS
            $scope.uploader.onWhenAddingFileFailed = function(item /*{File|FileLikeObject}*/, filter, options) {
                $log.info('onWhenAddingFileFailed', item, filter, options);
                ToasterService.displayError(translateFilter(filter.errorMsg));
            };
            $scope.uploader.onAfterAddingFile = function(fileItem) {
                $log.info('onAfterAddingFile', fileItem);
                $scope.uploadInProgress = true;
            };
            $scope.uploader.onAfterAddingAll = function(addedFileItems) {
                $log.info('onAfterAddingAll', addedFileItems);
            };
            $scope.uploader.onBeforeUploadItem = function(item) {
                $log.info('onBeforeUploadItem', item);
            };
            $scope.uploader.onProgressItem = function(fileItem, progress) {
                $log.info('onProgressItem', fileItem, progress);
            };
            $scope.uploader.onProgressAll = function(progress) {
                $log.info('onProgressAll', progress);
            };
            $scope.uploader.onSuccessItem = function(fileItem, response, status, headers) {
                $log.info('onSuccessItem', fileItem, response, status, headers);
            };
            $scope.uploader.onErrorItem = function(fileItem, response, status, headers) {
                $log.info('onErrorItem', fileItem, response, status, headers);
                $scope.uploadInProgress = false;

                $scope.resetFileUpload();
                if(status == 423) {
                    ToasterService.displayError(translateFilter('error.423'));
                } else {
                    if (response && response.message) {
                        ToasterService.displayError(response.message);
                    }
                }
            };
            $scope.uploader.onCancelItem = function(fileItem, response, status, headers) {
                $log.info('onCancelItem', fileItem, response, status, headers);
                $scope.uploadInProgress = false;
            };
            $scope.uploader.onCompleteItem = function(fileItem, response, status, headers) {
                $log.info('onCompleteItem', fileItem, response, status, headers);
                $scope.bookmarks = response;
                if (status >= 200 && status < 300) {
                    if (response && response.length && response.length > 0) {
                        $timeout(function () {
                            $scope.safeApply(function () {
                                $scope.uploader.progress = 100;

                                $timeout(function () {
                                    $scope.safeApply(function () {
                                        $scope.step = "links-customization";
                                        $scope.uploadInProgress = false;
                                    });
                                }, 100);
                            });
                        }, 10);
                    } else {
                        $scope.resetFileUpload();
                        ToasterService.displayError(translateFilter('bookmarks.import.messages.noLinksFound'));
                    }
                } else {
                    $scope.resetFileUpload();
                    if(status == 503) {
                        ToasterService.displayError(translateFilter('bookmarks.import.messages.bookmarkParsingServiceUnavailable'));
                    } else {
                        if (response && response.message) {
                            ToasterService.displayError(response.message);
                        }
                    }
                }
            };
            $scope.uploader.onCompleteAll = function() {
                $log.info('onCompleteAll');
            };

            // add a tag to a bookmark
            $scope.addTagOnBookmark = function (bookmark, tag) {
            };

            // remove a tag to a bookmark
            $scope.removeTagOnBookmark = function (bookmark, tag) {
            };

            $scope.removeBookmark = function(bookmark) {
                var index = $scope.bookmarks.indexOf(bookmark);
                $scope.bookmarks.splice(index, 1);
            };
            $scope.cancel = function () {
                $mdDialog.cancel();
            };
            $scope.next = function () {
                if ($scope.step === "upload-finished") {
                    //  finished => close the dialog
                    $mdDialog.cancel();
                } else if ($scope.step === "links-customization") {
                    console.log("bookmarks : ", $scope.bookmarks);
                    $scope.submitBookmarks();
                }
            };
            $scope.returnToFileSelection = function() {
                $scope.resetFileUpload();
                $scope.step = "choose-file";
            };

            $scope.submitBookmarks = function() {
                $scope.step = "upload-in-progress";

                Bookmark.batch($scope.bookmarks).$promise.then(function (batch) {
                    $timeout(function() {
                        $scope.safeApply(function () {
                            $scope.step = "upload-finished";
                        })
                    }, 1000);
                }, function (error) {
                    $log.log("error " + error.status + " while posting batch " + JSON.stringify($scope.bookmarks) + " : " + error);
                    // displayI18nError('batch-post', error);
                    $scope.step = "upload-failed";

                    if(error.status == 423) {
                        ToasterService.displayError(translateFilter('error.423'));
                    }
                });
            };
        };

        $scope.showBookmarkImportDialog = function() {

            $mdDialog.show({
                controller: BookmarkImportDialogController,
                templateUrl: TEMPLATES_PATH + 'bookmarkImportDialog.html',
                parent: angular.element(document.body),
                clickOutsideToClose: false,
                locals: {
                    csrf_token: $cookies.get("CSRF-TOKEN")
                }
            });
        };
    });
