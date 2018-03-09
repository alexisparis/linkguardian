'use strict';

angular.module('linkguardianApp')
    .factory('DemoService', function (translateFilter, $timeout, $window) {

        var inputFrequency = 200;
        var suspendedActions = new Array();

        var scrollToTop = function() {
            $("html, body").animate({ scrollTop: 0 }, 1000);
        };
        var scrollToBottom = function() {
            $("html, body").animate({ scrollTop: $(document).height() }, 1000);
        };

        var clearSuspendedActions = function() {
            while(suspendedActions.length > 0) {
                $timeout.cancel(suspendedActions.pop());
            }
        };
        var manuallyEnterValue = function(completeVal, updateModel) {
            updateModel('');
            if (completeVal) {
                for(var i = 0; i <= completeVal.length; i++) {
                    var func = function(index) {
                        return function() {
                            updateModel(completeVal.substring(0, index));
                        };
                    };
                    var newOperation = $timeout(func(i), i * inputFrequency);
                    suspendedActions.push(newOperation);
                }
            }
        };

        var orderClickEvent = function(selector, delay) {
            var newOperation = $timeout(function() {
                angular.element(selector).click();
            }, delay);
            suspendedActions.push(newOperation);
        };
        var hideMdSelects = function(delay) {
            var newOperation = $timeout(function() {
                $(".md-active").removeClass("md-active");
            }, delay);
            suspendedActions.push(newOperation);
        };

        return {
            bind: function (scope, state) {

                // /bot|googlebot|crawler|spider|robot|crawling/i.test(navigator.userAgent)
                // /bot|google|baidu|bing|msn|duckduckgo|teoma|slurp|yandex/i.test(navigator.userAgent)

                // if resize, restart tour
                angular.element($window).bind('resize', function(){
                    state.reload();
                });

                var createOnboardingStep = function(name, position, attachTo, xOffset, yOffset) {
                    return function(callbacks) {
                        var result = {
                            name: name,
                            title: translateFilter('onboarding.' + name + '.title'),
                            position: position || "centered",
                            description: translateFilter('onboarding.' + name + '.description'),
                            //width: 300
                        };
                        if (attachTo) {
                            result.attachTo = attachTo;
                        }
                        if (xOffset) {
                            result.xOffset = xOffset;
                        }
                        if (yOffset) {
                            result.yOffset = yOffset;
                        } else {
                            result.yOffset = -13;
                        }
                        result.undo = function () {
                        };
                        result.done = function () {
                        };
                        result.execute = function () {
                        };
                        if(callbacks) {
                            if (callbacks.undo) {
                                result.undo = callbacks.undo;
                            }
                            if (callbacks.execute) {
                                result.execute = callbacks.execute;
                            }
                            if (callbacks.done) {
                                result.done = callbacks.done;
                            }
                        }
                        return result;
                    }
                };
                // scope.walkthrough = function() {
                //     console.log("clicked");
                // };

                scope.onboardingSteps = [
                    createOnboardingStep("welcome")(),
                    createOnboardingStep("tools")
                    ({
                        undo : function () {
                            // hideMdSelects(100);
                            // scrollToTop();
                        },
                        execute : function () {
                            orderClickEvent("#menu-tools", 1);
                            // orderClickEvent("#sortDirection", 2000);
                            // orderClickEvent("#type", 3500);
                            // hideMdSelects(4900);
                            // scrollToBottom();
                        },
                        done : function () {
                            // orderClickEvent("#menu-tools", 100);
                            // hideMdSelects(100);
                            // scrollToBottom();
                        }
                    }),
                    createOnboardingStep("show-add-url-input"    , "bottom", "#urlInput", -20)
                        ({
                            undo : function () {
                                scope.newLink.newurl = '';
                            },
                            execute : function () {
                                manuallyEnterValue(translateFilter('onboarding.url'), function(val) {
                                    scope.newLink.newurl = val;
                                });
                            },
                            done : function () {
                                scope.newLink.newurl = translateFilter('onboarding.url');
                            }
                        }),
                    createOnboardingStep("show-add-url-tag-input", "bottom", "#tagInput", -20)
                        ({
                            undo : function () {
                                scope.newLink.tag = '';
                            },
                            execute : function () {
                                scope.newLink.newurl = translateFilter('onboarding.url');
                                manuallyEnterValue(translateFilter('onboarding.tag'), function(val) {
                                    scope.newLink.tag = val;
                                });
                            },
                            done : function () {
                                scope.newLink.tag = translateFilter('onboarding.tag');
                            }
                        }),
                    createOnboardingStep("show-add-url-button"   , "left"  , "#submitUrlButton")
                        ({
                            execute : function () {
                                scope.newLink.newurl = translateFilter('onboarding.url');
                                scope.newLink.tag = translateFilter('onboarding.tag');
                            }
                        }),
                    createOnboardingStep("link-added")
                        ({
                            undo : function () {
                                scope.links = new Array();
                                scrollToTop();
                            },
                            execute : function () {
                                scope.newLink.newurl = '';
                                scope.newLink.tag = '';
                                scope.links = new Array();
                                scope.links.push(JSON.parse(translateFilter('onboarding.created_link')));
                                console.log("links count " + scope.links.length);
                                scrollToBottom();
                            }
                        }),
                    createOnboardingStep("action-button-on-link")(),
                    createOnboardingStep("note-button-on-link")(),
                    createOnboardingStep("show-search-panel", "bottom", ".panel-search", -8, -15)
                        ({
                            undo : function () {
                                // hideMdSelects(100);
                                // scrollToTop();
                            },
                            execute : function () {
                                // orderClickEvent("#sort", 500);
                                // orderClickEvent("#sortDirection", 2000);
                                // orderClickEvent("#type", 3500);
                                // hideMdSelects(4900);
                                // scrollToBottom();
                            },
                            done : function () {
                                // hideMdSelects(100);
                                // scrollToBottom();
                            }
                        }),

                    createOnboardingStep("show-cloud-of-tags")(),
                    createOnboardingStep("end")()
                ];
                scope.onboardingFinished = function() {
                    state.go("login");
                };

                // add a watch on onboardingIndex
                scope.$watch('onboardingIndex', function (newValue, oldValue) {
                    // suspend forecasted events that add dynamics effects
                    clearSuspendedActions();

                    var oldStep = scope.onboardingSteps[oldValue];
                    var newStep = scope.onboardingSteps[newValue];

                    // if old step exists, call done of forward or undo else
                    if (newValue != oldValue) {
                        if (newValue > oldValue) {
                            oldStep.done();
                        } else {
                            oldStep.undo();
                        }
                    }
                    newStep.execute();
                });
            }
        };
    });
