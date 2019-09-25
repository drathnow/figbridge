requirejs.config({
    baseUrl : './scripts',
    shim : {
        underscore : {
            exports : '_'
        },

        bootstrap : {
            deps : [ 'jquery']
        },

        backbone : {
            deps : [ 'jquery', 'underscore' ],
            exports : 'Backbone'
        },

        moment : {
            exports : 'moment'
        },

        marionette : {
            deps : [ 'jquery', 'underscore', 'backbone' ],
            exports : 'Marionette'
        },

        text: {
            deps : [ 'jquery', 'underscore', 'backbone' ],
            exports: 'Text'
        },

        hash: {
             exports: 'Hash'
        },

        metismenu: {
            exports: 'MetisMenu'
        }
    },

    paths : {
        jquery : [
                  '//cdnjs.cloudflare.com/ajax/libs/jquery/2.1.1/jquery.min'
                 ],

        underscore : [
                      '//cdnjs.cloudflare.com/ajax/libs/underscore.js/1.6.0/underscore-min'
                     ],

        bootstrap : [
                     '//cdnjs.cloudflare.com/ajax/libs/twitter-bootstrap/3.1.1/js/bootstrap.min'
                    ],

        backbone : [
                    '//cdnjs.cloudflare.com/ajax/libs/backbone.js/1.1.2/backbone-min'
                   ],

        marionette : [
                      '//cdnjs.cloudflare.com/ajax/libs/backbone.marionette/1.8.6/backbone.marionette.min'
                     ],

        moment : [
                  '//cdnjs.cloudflare.com/ajax/libs/moment.js/2.8.3/moment.min'
                 ],

        text: [
               '//cdnjs.cloudflare.com/ajax/libs/require-text/2.0.12/text.min'
              ],

      metismenu: [
                  '//cdnjs.cloudflare.com/ajax/libs/metisMenu/2.4.3/metisMenu.min'
                  ],

      datatables: [
                   '//cdn.datatables.net/t/dt/dt-1.10.11,r-2.0.2/datatables'
                  ]
    }
});


var PacBridgeApp = {};

require(['marionette',
           'views/SideBarView',
           'views/TopNavView',
           'app/ContentController',
           'app/EventNames',
           'app/ModalRegion',
           'app/ErrorDialog',
           'views/ConfirmView'],
           function(Marionette,
                   SideBarView,
                   TopNavView,
                   ContentController,
                   EventNames,
                   ModalRegion,
                   ErrorDialog,
                   ConfirmView) {

    PacBridgeApp = new Marionette.Application();

    PacBridgeApp.addRegions({
        headerRegion: '#header',
        topNavRegion: '#top-nav',
        sideBarRegion : '#sidebar',
        titleRegion: '#title',
        detailRegion: '#detail',
        modalSection: ModalRegion
    });

    PacBridgeApp.logout = function(message) {
        var lgi = new Login();
        lgi.urlRoot += "/logout";
        lgi.fetch();
        window.location.replace("index.html");
    };

    PacBridgeApp.showErrorDialog = function(message) {
        var errorDialog = new ErrorDialog({
            title: 'Error',
            message: message
        });
        errorDialog.on('close', function () {
            PacBridgeApp.modalSection.close();
        }, this);
        PacBridgeApp.modalSection.show(errorDialog);
    };

    PacBridgeApp.getCookieValue = function getCookieValue(a) {
        var b = document.cookie.match('(^|;)\\s*' + a + '\\s*=\\s*([^;]+)');
        return b ? b.pop() : '';
    };

    PacBridgeApp.showHomePage = function() {
        var that = PacBridgeApp;
        var sideBar = new SideBarView();

        sideBar.on(EventNames.TopNav.logout, function() {
            PacBridgeApp.logout();
        });

        sideBar.on(EventNames.NavBar.changePassword, function() {
            PacBridgeApp.changePassword();
        });

        this.sideBarRegion.show(sideBar);
        this.topNavRegion.show(new TopNavView());
        var cc = new ContentController(this.titleRegion, this.detailRegion);
        PacBridgeApp.vent.trigger(EventNames.SideBar.showHome);
    };

    PacBridgeApp.vent.on(EventNames.App.notResponding, function(){
        PacBridgeApp.showErrorDialog('FIG Bridge is not responding. Is it running?');
    });


    PacBridgeApp.on('initialize:after', function() {
        PacBridgeApp.version = '6.4.0.Final-2';
        PacBridgeApp.showHomePage();
    });

    PacBridgeApp.start();
});
