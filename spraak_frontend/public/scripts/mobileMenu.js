/*! flexnav https://github.com/indyplanets/flexnav http://unlicense.org/ 2013-11-28 */
!function () {
    var a;
    a = jQuery, a.fn.flexNav = function (b) {
        var c, d, e, f, g, h, i, j, k, l, m, n;
        return k = a.extend({
            animationSpeed: 250,
            transitionOpacity: !0,
            buttonSelector: ".menu-button",
            hoverIntent: !1,
            hoverIntentTimeout: 150,
            calcItemWidths: !1,
            hover: !0
        }, b), c = a(this), c.addClass("with-js"), k.transitionOpacity === !0 && c.addClass("opacity"), c.find("li").each(function () {
            return a(this).has("ul").length ? a(this).addClass("item-with-ul").find("ul").hide() : void 0
        }), k.calcItemWidths === !0 && (d = c.find(">li"), f = d.length, h = 100 / f, g = h + "%"), c.data("breakpoint") && (e = c.data("breakpoint")), l = function () {
            return c.hasClass("lg-screen") === !0 && k.hover === !0 ? k.transitionOpacity === !0 ? a(this).find(">ul").addClass("flexnav-show").stop(!0, !0).animate({
                height: ["toggle", "swing"],
                opacity: "toggle"
            }, k.animationSpeed) : a(this).find(">ul").addClass("flexnav-show").stop(!0, !0).animate({height: ["toggle", "swing"]}, k.animationSpeed) : void 0
        }, i = function () {
            return c.hasClass("lg-screen") === !0 && a(this).find(">ul").hasClass("flexnav-show") === !0 && k.hover === !0 ? k.transitionOpacity === !0 ? a(this).find(">ul").removeClass("flexnav-show").stop(!0, !0).animate({
                height: ["toggle", "swing"],
                opacity: "toggle"
            }, k.animationSpeed) : a(this).find(">ul").removeClass("flexnav-show").stop(!0, !0).animate({height: ["toggle", "swing"]}, k.animationSpeed) : void 0
        }, j = function () {
            var b;
            if (a(window).width() <= e)return c.removeClass("lg-screen").addClass("sm-screen"), k.calcItemWidths === !0 && d.css("width", "100%"), b = k.buttonSelector + ", " + k.buttonSelector + " .touch-button", a(b).removeClass("active"), a(".one-page li a").on("click", function () {
                return c.removeClass("flexnav-show")
            });
            if (a(window).width() > e) {
                if (c.removeClass("sm-screen").addClass("lg-screen"), k.calcItemWidths === !0 && d.css("width", g), c.removeClass("flexnav-show").find(".item-with-ul").on(), a(".item-with-ul").find("ul").removeClass("flexnav-show"), i(), k.hoverIntent === !0)return a(".item-with-ul").hoverIntent({
                    over: l,
                    out: i,
                    timeout: k.hoverIntentTimeout
                });
                if (k.hoverIntent === !1)return a(".item-with-ul").on("mouseenter", l).on("mouseleave", i)
            }
        }, a(k.buttonSelector).data("navEl", c), n = ".item-with-ul, " + k.buttonSelector, a(n).append('<span class="touch-button"><i class="navicon">&#9660;</i></span>'), m = k.buttonSelector + ", " + k.buttonSelector + " .touch-button", a(m).on("click", function (b) {
            var c, d, e;
            return a(m).toggleClass("active"), b.preventDefault(), b.stopPropagation(), e = k.buttonSelector, c = a(this).is(e) ? a(this) : a(this).parent(e), d = c.data("navEl"), d.toggleClass("flexnav-show")
        }), a(".touch-button").on("click", function () {
            var b, d;
            return b = a(this).parent(".item-with-ul").find(">ul"), d = a(this).parent(".item-with-ul").find(">span.touch-button"), c.hasClass("lg-screen") === !0 && a(this).parent(".item-with-ul").siblings().find("ul.flexnav-show").removeClass("flexnav-show").hide(), b.hasClass("flexnav-show") === !0 ? (b.removeClass("flexnav-show").slideUp(k.animationSpeed), d.removeClass("active")) : b.hasClass("flexnav-show") === !1 ? (b.addClass("flexnav-show").slideDown(k.animationSpeed), d.addClass("active")) : void 0
        }), c.find(".item-with-ul *").focus(function () {
            return a(this).parent(".item-with-ul").parent().find(".open").not(this).removeClass("open").hide(), a(this).parent(".item-with-ul").find(">ul").addClass("open").show()
        }), j(), a(window).on("resize", j)
    }
}.call(this);

jQuery(function ($) {

    var Difi3 = {
        config: {},

        init: function (config) {
            this.mobileMenu();
            this.h1AddOpacity();
            window.setTimeout( this.h1AddOpacityFadeEffect, 100 );
            this.imageToFigure();
            this.figureResize();
            this.tableToResponsive();
            this.inlineButtons();
            this.boxArticleListLink();
            this.boxTransportPageListLink();
            this.scrollToTop();
            this.onLocationHash();
            this.scrollToAnchor();
            this.ieCampaign();
            this.mobileSearchForm();

            var inst = this;
            window.setTimeout(function() {
                inst.sidebarHeight();
            }, 150);
        },

        /**
         * Setup mobile menu
         */
        mobileMenu: function() {
            $("nav ul.menu:first").attr('data-breakpoint', '991'); // Add breakpoint
            $("nav ul.menu:first").flexNav(); // Activate mobile menu

            $( "nav .menu-button .touch-button" ).html( '<div class="navicon-line"></div><div class="navicon-line"></div><div class="navicon-line"></div>' ); // Add navicon line

            // Add span for text styling
            $("nav ul.menu a").each( function( index ) {
                $(this).html('<span>' + $(this).text() + '</span>');
            });

            // Expand active items in menu
            $( "nav ul.menu li.active-trail > .touch-button" ).trigger( "click" );
        },

        /**
         * Add fancy opacity fadein effect on H1
         */
        h1AddOpacity: function() {
            $("#page h1").not('header h1').each( function( index ) {
                $(this).html('<span style="opacity: 0;">' + $(this).html() + '</span>');
            });
        },

        /**
         * Add fancy opacity fadein effect on H1
         */
        h1AddOpacityFadeEffect: function() {
            $("#page h1").not('header h1').each( function( index ) {
                $(this).find('> span').attr('style', 'opacity: 1; transition: opacity 500ms; -webkit-transition: opacity 500ms;');
            });
        },

        /**
         * Transform HTML img to HTML 5 figure
         */
        imageToFigure: function() {
            $( "#content article img.figure" ).each(function( index ) {
                var img_src = $(this).attr('src');
                var img_title = $(this).attr('title');
                var img_alt = $(this).attr('alt');
                var img_class = $(this).attr('class');
                var img_figure;

                img_figure = '<figure';

                if (img_class) {
                    img_figure += ' class="' + img_class + '"';
                }

                img_figure += '>';

                img_figure += '<img src="' + img_src + '" alt="' + img_alt + '" title="' + img_title + '" />';

                if ((img_alt && !$(this).hasClass('hide-alt')) || img_title) {
                    img_figure += '<figcaption>';
                    if (img_title) {
                        img_figure += '<div class="title"><p>' + img_title + '</p></div>';
                    }
                    if (img_alt && !$(this).hasClass('hide-alt')) {
                        img_figure += '<div class="body"><p>' + img_alt + '</p></div>';
                    }
                    img_figure += '</figcaption>';
                }

                img_figure += '</figure>';

                $(this).replaceWith(img_figure);
            });
        },

        /**
         * Add click event to HTML 5 figure and makes it resizable
         */
        figureResize: function() {
            var inst = this;

            $( "#content figure.left, #content figure.right" ).each(function( index ) {
                $(this).css('cursor','pointer');

                $(this).click(function() {
                    var left = $(this).hasClass('left');
                    var right = $(this).hasClass('right');
                    var resized = $(this).hasClass('resized');

                    if ((left || right) &! (resized)) {
                        $(this).addClass('resized');
                    }

                    if ((left || right) && (resized)) {
                        $(this).removeClass('resized');
                    }

                    window.setTimeout( function() {
                        inst.sidebarHeight();
                    }, 300 );
                });
            });
        },

        /**
         * Add click and keypress event to accordion
         */


        /**
         * Add Bootstrap responsive wrapper and css class to HTML table
         */
        tableToResponsive: function() {
            $( "#content table" ).each(function( index ) {
                var table_html = $(this).clone().wrap('<div></div>').parent().html();
                $(this).replaceWith('<div class="table-responsive">' + table_html + '</div>');
            });
        },

        /**
         * Add click and keypress event to inline buttons
         */
        inlineButtons: function() {
            $( "#content div.button" ).each(function( index ) {
                $(this).click(function() {
                    window.location.href = $(this).attr('data-url');
                    return false;
                });

                $(this).keypress(function(event) {
                    if (event.which == 13 || event.which == 32) {
                        window.location.href = $(this).attr('data-url');
                        return false;
                    }
                });
            });
        },

        /**
         * Add click event to article list items
         */
        boxArticleListLink: function() {
            $( ".box.article-list.with-hover-and-active article" ).each(function( index ) {
                $(this).click(function() {
                    window.location.href = $(this).find('h2 a').attr('href');
                });
            });
        },

        /**
         * Add click event to list items on transport page
         */
        boxTransportPageListLink: function() {
            $( ".nodetype-transport .box.menu.transport ul li" ).each(function( index ) {
                $(this).click(function() {
                    window.location.href = $(this).find('a').attr('href');
                });
            });
        },

        /**
         * Always make sidebar same height as main container
         */
        sidebarHeight: function() {
            var inst = this;

            $( "#main #sidebar" ).css('height', '');

            if ($("#main #sidebar").is(':visible') && (inst.isBootstrapBreakpoint('md') || inst.isBootstrapBreakpoint('lg')) ) {
                var mainHeight = $("#main").outerHeight();
                $( "#main #sidebar" ).css('height', mainHeight);
            }
        },

        /**
         * Add scroll to top
         */
        scrollToTop: function() {
            if ($(window).scrollTop() < 100) {
                $('.scroll-to-top').hide();
            }

            $(window).scroll(function(){
                if ($(this).scrollTop() > 100) {
                    $('.scroll-to-top').fadeIn();
                } else {
                    $('.scroll-to-top').fadeOut();
                }
            });

            $('.scroll-to-top').click(function(){
                $('html, body').animate({scrollTop : 0}, 0);
                return false;
            });
        },

        /**
         * Perform action if location.href contains hash (anchor)
         */
        onLocationHash: function() {
            var urlhash = window.location.hash;

            if (urlhash.length > 0) {
                // Get item from hastag
                var item = $(urlhash);

                // If item is an object
                if (item.length) {
                    // Item is a closed accordion...open it
                    if (item.not('.open').hasClass('accordion')) {
                        $( item ).find('.heading').trigger( "click" );
                    }
                }
            }
        },

        /**
         * Smooth scrolling to inline anchors
         */
        scrollToAnchor: function() {
            var root = $('html, body');
            var a = $( '#content a[href^="#"]' );

            a.click(function() {
                var hash = $.attr(this, 'href');
                var item = $(hash);

                if (item.length) {
                    // Change the hash first, then do the scrolling. This retains
                    // the standard functionality of the back/forward buttons.
                    var scrollmem = $(document).scrollTop();
                    window.location.hash = hash;
                    $(document).scrollTop(scrollmem);

                    // Scroll to anchor
                    root.animate({
                            scrollTop: item.offset().top-10},
                        1000
                    );

                    window.setTimeout(function() {
                        // If target is a closed accordion..open it
                        if (item.not('.open').hasClass('accordion')) {
                            item.find('.heading').trigger( "click" );
                        }
                    }, 1250);

                    return false;
                }
            });
        },

        /**
         * Display IE campaign text
         */
        ieCampaign: function() {
            var html = '';
            html += '<div class="alert alert-danger alert-dismissible" role="alert">';
            html += '<button type="button" class="close" data-dismiss="alert"><span aria-hidden="true">&times;</span><span class="sr-only">Lukk</span></button>';

            html += '<p><strong>Til informasjon</strong></p>';
            html += '<p>Du benytter en utdatert versjon av nettleseren Internet Explorer som ikke st&oslash;tter dagens webstandarder eller v&aring;r grafiske profil. Presentasjonen av nettsiden vil derfor avvike fra normalen.</p><p class="text">Vi anbefaler at du oppgraderer til en nyere versjon av IE, eller bruker en annen nettleser når du besøker difi.no.</p><p><a href="http://browsehappy.com/?locale=nn">Informasjon om og lenker til siste versjon av de mest brukte nettleserne</a>.</p>';
            html += '<p>Vennlig hilsen Direktoratet for forvaltning og IKT</p>';

            html += '</div>';

            if ((/msie|MSIE 7/.test(navigator.userAgent)) || (/msie|MSIE 8/.test(navigator.userAgent))) {
                $("#page").prepend(html);
            }
        },

        /**
         * Add searchform for mobile
         */
        mobileSearchForm: function() {
            var inst = this;

            var label = $('header form .form-item-search-block-form label');
            var form = $('header .box.block-search');
            form.prepend('<div class="searchToggler"><a href="#">' + label.text().trim() + '</a></div>');
            var searchToggler = $('header .searchToggler');
            var link = searchToggler.find('a');

            link.click(function() {
                if (inst.isBootstrapBreakpoint('xs')) {
                    var mobilesearch = $('header .mobilesearch .form');
                    var seperator = $('header .seperator');

                    mobilesearch.slideToggle("fast", "swing", function() {
                        if (mobilesearch.is(":visible")) {
                            seperator.addClass('hide');
                            link.addClass('opacity');
                        } else {
                            seperator.removeClass('hide');
                            link.removeClass('opacity');
                        }
                    });
                }

                return false;
            });

            $('header .mobilesearch form').submit(function( event ) {
                event.preventDefault();
                window.location.href = Drupal.settings.basePath + 'search/site/' + $('header .mobilesearch .form-text').val();
            });
        },

        /**
         * @returns
         * true 	- if page is currently using the breakpoint specified as argument
         * false 	- if otherwise
         */
        isBootstrapBreakpoint: function( alias ) {
            return $('.device-' + alias).is(':visible');
        },

        /**
         * Add window resize
         */
        resizer: function() {
            var inst = this;
            var resizeTimer = null;
            $(window).on('resize', function(e) {
                clearTimeout(resizeTimer);
                resizeTimer = setTimeout(function() {
                    inst.sidebarHeight();
                }, 250);

                inst.fluidVideoSetWidthAndHeight();
            });
        }
    };

    /**
     * Document ready...here we go!
     */
    $(document).ready(function() { Difi3.init(); });
});
