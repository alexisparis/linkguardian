angular.module("linkguardianApp").directive("circularMenu",function(){
    return {
        restrict: 'EA',
        replace:true,
        scope:{
            menuId:"=",
            angle:"=",
            placement:"@",
            buttonConfig:"=",
            menuItems:"=",
            onwingClick:"&",
            defaultOpen:"@"
        },

    template:
        '<div class="menuContainer {{jumpAnim}}" style="positions:absolute;z-index: 78;align-self:flex-end;">'+
            '<button class="{{open}}" ng-click="toggleMenu($event);" ' +
                'style="box-shadow: 0 2px 5px 0 rgba(0, 0, 0, 0.26);outline: none;position:absolute;border:none;z-index:79;cursor:pointer;' +
                    'border-radius:{{button.buttonWidth}}px;width:{{button.buttonWidth}}px;height:{{button.buttonWidth}}px;' +
                    'background-color:{{button.color}};color:{{button.textColor}};font-size: 14px;">'+
                '<span class="fas fa-bars" ' +
                    'style="margin: auto; display:block;vertical-align: middle;pointer-events:none;transition:all .3s linear;animation-fill-mode:forwards;"></span>'+
                '<span style="display: block;position:absolute;top:0;left:0;right:0;bottom:0;margin:auto;transition:all .3s linear;pointer-events:none;" ' +
                    'class="fas fa-times">' +
                '</span>'+
            '</button>'+
            '<div class="menu-list1 {{positionClass}}" style="pointer-events:none;position:relative;width:{{width}}px;height:{{height}}px;' +
                                                             'left:{{button.buttonWidth/2}}px;top: -{{height/2 - button.buttonWidth/2}}px;">'+
                '{{wing.show}}' +
                '<div ng-repeat="wing in wings" ' +
                    'data-toggle="tooltip" data-placement="top" title="{{wing.tooltip | translate}}" ' +
                    'class="{{positionClass}} {{open}} {{rotate}}" ' +
                    'style="pointer-events:none;width:{{width}}px;height:{{height}}px;' +
                           'position:absolute;transition: all .3s cubic-bezier(0.680, -0.550, 0.265, 1.550);transform-origin: 0px {{height/2}}px;' +
                           'transform:rotate({{wing.rotate}}deg) scale({{wing.show}});cursor:pointer;">'+
                    //'{{wing.rotate}}' +
                    '<svg ng-attr-width="{{width}}px" ng-attr-height="{{height}}px">'+
                        '<path ' +
                            'ng-attr-d="{{path}}" style="fill:{{wing.color}};pointer-events:auto;" ' +
                            'ng-click="wingClick(wing)" ng-mouseover="hoverIn(wing,$event)" ' +
                            'ng-mouseleave="hoverOut(wing,$event)"></path>'+
                        '<text ng-if="!button.onlyIcon" class="wing-text" ' +
                            'style="fill:{{wing.titleColor}};font-size: 14px;text-shadow: 1px 1px 1px rgba(0, 0, 0, 0.2);" ' +
                            'x="48%" y="50%" dominant-baseline="middle" text-anchor="{{textAnchor}}" letter-spacing="2px" ' +
                            'ng-attr-transform="rotate({{initialTextAngle}},{{width/2}},{{height/2}})">{{wing.title}}</text>'+
                    '</svg>'+
                    '<i ng-if="button.showIcons" ' +
                        'data-fa-transform="rotate-180" ' +
                        'style="color:{{wing.icon.color}};text-align:center;font-size:{{wing.icon.size}}px;width:{{wing.icon.size}}px;' +
                            'height: {{wing.icon.size}}px;transform-origin: 80% 50%;transform: translate({{iconX+15}}px, -{{iconY}}px)" ' + //  rotate(-{{wing.rotate}}deg)
                        'class="{{wing.icon.name}}" classa="fas fa-trash"></i>'+
                '</div>'+
            '</div>'+
        '</div>',
        link:function(scope,elem,attr){

            // scope.icon_rotation = function(rotation){
            //     //console.log("call icon_rotation(" + rotation + ")");
            //     return 180 - rotation;
            //     // var x = 0;
            //     // if (rotation == 180) {
            //     //     x = 1;
            //     // } else if (rotation == 204) {
            //     //     x = -24;
            //     // } else if (rotation == 228) {
            //     //     return -54;
            //     // }
            //     // return x + 180;
            //
            //     // rotatewings :: for delete rotate => 180
            //     // circularmenu.js:221 rotatewings :: for tweet rotate => 204
            //     // circularmenu.js:221 rotatewings :: for copyToClipboard rotate => 228
            //     // circularmenu.js:221 rotatewings :: for markAsRead rotate => 252
            //     // circularmenu.js:221 rotatewings :: for goTo rotate => 276
            // }

            scope.wings = scope.menuItems || [];
            scope.button = scope.buttonConfig || {};
            scope.positionClass = scope.placement || "bottomLeft";
            var angle = scope.angle || 360/scope.wings.length;
            var radius = scope.buttonConfig.menuRadius;
            var offset = scope.buttonConfig.offset || 10;
            var innerRadius = scope.button.buttonWidth/2 + offset;
            var height = radius;
            x1 = parseInt(0 + radius*Math.cos(Math.PI*(360-angle/2)/180));
            y1 = parseInt(height/2 + radius*Math.sin(Math.PI*(360-angle/2)/180));
            x2 = parseInt(0 + radius*Math.cos(Math.PI*(angle/2)/180));
            y2 = parseInt(height/2 + radius*Math.sin(Math.PI*(angle/2)/180));
            a1 = parseInt(0 + innerRadius*Math.cos(Math.PI*(360-angle/2)/180));
            b1 = parseInt(height/2 + innerRadius*Math.sin(Math.PI*(360-angle/2)/180));
            a2 = parseInt(0 + innerRadius*Math.cos(Math.PI*(angle/2)/180));
            b2 = parseInt(height/2 +1+ innerRadius*Math.sin(Math.PI*(angle/2)/180));
            scope.width = radius;
            scope.height = height;
            scope.iconX = scope.button.textAndIcon ? radius - scope.wings[0].icon.size - 4 : radius - radius/2 + scope.wings[0].icon.size/4;
            scope.iconY = height/2 + scope.wings[0].icon.size/2 + 5;
            scope.path = "M"+a1+","+b1+" L"+x1+","+y1+" A"+radius+","+radius+" 0 0, 1"+" "+x2+","+y2+" L"+a2+","+b2+"  A"+innerRadius+","+innerRadius+" 1 0, 0"+" "+a1+","+b1+" z";
            scope.open = "menuclose";
            scope.jumpAnim  = "";
            scope.rotate = "";
            scope.initialTextAngle = 0;
            scope.textAnchor = "left";
            if(scope.positionClass == "topRight" || scope.positionClass == "bottomRight"){
                scope.initialTextAngle = 180;
                scope.textAnchor = "middle";
            }
            elem[0].children[0].children[0].style.transform = "scale(1)";
            elem[0].children[0].children[1].style.transform = "scale(0)";
            var windowElement = angular.element(window)[0];
            var centreX = windowElement.innerWidth/2 - scope.button.buttonWidth/2;
            var centreY = windowElement.innerHeight/2 - scope.button.buttonWidth/2;
            windowElement.onresize = function(){
                centreX = windowElement.innerWidth/2 - scope.button.buttonWidth/2;
                centreY = windowElement.innerHeight/2 - scope.button.buttonWidth/2;
                elem.css({
                    width: scope.button.buttonWidth+'px',
                    height: scope.button.buttonWidth+'px',
                    top: (windowElement.innerHeight- scope.button.buttonWidth - scope.button.gutter.bottom)+'px',
                    left: (windowElement.innerWidth-scope.button.buttonWidth - scope.button.gutter.right)+'px'
                });
            }
            elem.css({
                width: scope.button.buttonWidth+'px',
                height: scope.button.buttonWidth+'px',
                top: (windowElement.innerHeight- scope.button.buttonWidth - scope.button.gutter.bottom)+'px',
                left: (windowElement.innerWidth-scope.button.buttonWidth - scope.button.gutter.right)+'px'
            });

            var startX = 0, startY = 0, x = 20, y =  20;
            var drag = false;
            var dragStart = false;

            scope.inToggle = false;

            elem[0].addEventListener('mousedown',mousedown,false );
            document.addEventListener('mousemove', mousemove, false);
            elem[0].addEventListener('touchmove',touchmove, false);
            elem[0].addEventListener('mouseup', mouseup, false);
            elem[0].addEventListener("touchend", touchend, false);
            scope.toggleMenu = function(){
                if (!scope.inToggle) {
                    scope.inToggle = true;
                    try {
                        var textElem = angular.element(elem).find('.wing-text');
                        if (drag == true) {
                            scope.jumpAnim = "jumpAnim";
                            elem.css({"transition": "all 900ms cubic-bezier(0.680, -0.550, 0.265, 1.550)"});
                            setTimeout(function () {
                                scope.jumpAnim = "";
                                elem.css({"transition": "none"});
                                scope.$apply();
                                scope.inToggle = false;
                            }, 900);

                            elem.css({
                                top: (windowElement.innerHeight - scope.button.buttonWidth - scope.button.gutter.bottom) + 'px',
                                left: scope.button.gutter.left + 'px'
                            });
                            y = windowElement.innerHeight - scope.button.buttonWidth - scope.button.gutter.bottom;
                            x = scope.button.gutter.left;
                            scope.positionClass = "bottomLeft";
                            scope.adjustMenu();
                            textElem.attr('transform', 'rotate(0,' + scope.width / 2 + ',' + scope.height / 2 + ')');
                            textElem.attr('text-anchor', 'left');
                            drag = false;
                        }
                        else if (drag == false) {
                            if (scope.open == "menuclose") {
                                scope.openWings(scope.wings);
                                elem[0].children[0].children[0].style.transform = "scale(0)";
                                elem[0].children[0].children[1].style.transform = "scale(1)";

                                //angular.element(elem[0]).find("span").css({"transform":"scale(0)"});
                                //angular.element(elem[0]).find("img").css({"transform":"scale(1)"});
                                if (scope.positionClass == "topRight" || scope.positionClass == "bottomRight") {
                                    textElem.attr('transform', 'rotate(180,' + scope.width / 2 + ',' + scope.height / 2 + ')');
                                    textElem.attr('text-anchor', 'middle');
                                }
                                setTimeout(function () {
                                    scope.rotateWings(scope.wings);
                                    //scope.open = "menuopen";
                                    scope.$apply();
                                }, 400);
                                setTimeout(function () {
                                    scope.open = "menuopen";
                                    scope.$apply();
                                    scope.inToggle = false;
                                }, 600);
                            }
                            else {
                                scope.rotateWings(scope.wings);
                                elem[0].children[0].children[0].style.transform = "scale(1)";
                                elem[0].children[0].children[1].style.transform = "scale(0)";
                                //angular.element(elem[0]).find("span").css({"transform":"scale(1)"});
                                //angular.element(elem[0]).find("img").css({"transform":"scale(0)"});
                                setTimeout(function () {
                                    scope.closeWings(scope.wings);
                                    scope.open = "menuclose";
                                    scope.$apply();
                                    scope.inToggle = false;
                                }, 400);
                            }
                        }
                    } catch(error) {
                        scope.inToggle = false;
                    }
                }
            };
            scope.initMenu = function(){
                angular.forEach(scope.wings,function(item,index){
                    item.rotate = scope.button.angles.bottomRight;//+"deg";
                    // console.log("initMenu :: for " + item.title + " rotate => " + item.rotate);
                });
            }
            scope.adjustMenu = function(){
                var index = 0;
                angular.forEach(scope.wings,function(item,idx){

                    if ((!item.isVisible || item.isVisible() == true) || scope.open != "menuclose") {

                        if (scope.open == "menuopen") {
                            item.rotate = (scope.button.angles.bottomRight + index * angle);// + "deg";
                        }
                        else if (scope.open == "menuclose") {
                            item.rotate = scope.button.angles.bottomRight;// + "deg";
                        }
                        index = index+1;
                        //console.log("adjustMenu :: for " + item.title + " rotate => " + item.rotate);
                    }
                });
            };
            scope.rotateWings = function(wings){

                var index = 0;
                angular.forEach(wings,function(item,idx){

                    if ((!item.isVisible || item.isVisible() == true) || scope.open != "menuclose") {

                        if (scope.open == "menuclose") {
                            item.rotate = (scope.button.angles.bottomRight + index * angle);// + "deg";
                            // item.icon.rotate = scope.icon_rotation(item.rotate);
                        }
                        else {
                            item.rotate = scope.button.angles.bottomRight;// + "deg";
                            // item.icon.rotate = scope.icon_rotation(item.rotate);
                        }
                        index = index + 1;
                        // console.log("rotatewings :: for " + item.title + " rotate => " + item.rotate);
                    }
                });
            };

            scope.$root.$on('link-circular-menu-opening', function (event, data) {
                if ( scope.open == "menuopen" && data.menuId != scope.menuId ) {
                    scope.toggleMenu();
                }
            });

            scope.openWings = function(wings){
                scope.$root.$broadcast('link-circular-menu-opening', {
                    menuId : scope.menuId
                });
                angular.forEach(wings,function(item){

                    // item.show = 0;
                    if (!item.isVisible || item.isVisible()) {
                        item.show = 1;
                    }
                });
            };
            scope.closeWings = function(wings){
                angular.forEach(wings,function(item){
                    item.show = 0;
                });
            };
            scope.initMenu();

            /////////// XXAP

            scope.hoverIn = function(wing,event){
                if(scope.open == "menuopen"){
                    angular.element(event.target).parent().parent().css("transform","rotate("+wing.rotate+"deg) scale(1.08)");
                }
            };
            scope.hoverOut = function(wing,event){
                if(scope.open == "menuopen"){
                    angular.element(event.target).parent().parent().css("transform","rotate("+wing.rotate+"deg) scale(1)");
                }
            };

            scope.wingClick = function(wing){
                scope.onwingClick({"wing":wing});
                scope.toggleMenu();
            }

            function mousedown(event) {
                if(event.target.tagName == "BUTTON"){
                    scope.jumpAnim = "";
                    event.preventDefault();
                    startX = event.offsetX;
                    startY = event.offsetY;
                    //setTimeout(function(){
                    dragStart = true;
                    //scope.$apply();
                    //},0);
                }
            }
            function mousemove(event) {
                if(dragStart){
                    scope.jumpAnim = "";
                    drag = true;
                    y = event.clientY - startY;
                    x = event.clientX - startX;
                    elem.css({
                        top: y + 'px',
                        left:  x + 'px'
                    });
                }
            }
            function mouseup() {
                dragStart = false;
            }
            function touchmove(event){
                var touch = event.targetTouches[0];
                x = touch.pageX-(scope.button.buttonWidth/2);
                y = touch.pageY-(scope.button.buttonWidth/2);
                drag = true;
                elem.css({
                    top: y + 'px',
                    left:  x + 'px'
                });
                event.preventDefault();
            }
            function touchend(){
                if(drag == true){
                    scope.toggleMenu();
                    drag = false;
                }
            }
        }
    }
})
