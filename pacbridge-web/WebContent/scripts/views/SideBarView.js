define(['marionette',
        'metismenu',
        'app/EventNames',
        'text!templates/SideBarView.html'],
        function(Marionette,
        		MetisMenu,
        		EventNames,
        		theTemplate) {

	var SideBarView = Marionette.CompositeView.extend({
		template: _.template(theTemplate),

		ui: {
			home: '#home',
			connections: '#conn',
			outRequests: '#outreq',
			cachedDevs: '#cacdev',
			fooBar: '#foo'
		},

		onShow: function() {
		    $(function() {
		        $('#side-menu').metisMenu();
		    });
		},
		
		events: {
		    'click @ui.home': function() {
                PacBridgeApp.vent.trigger(EventNames.SideBar.home);
		    },

		    'click @ui.connections': function() {
		        PacBridgeApp.vent.trigger(EventNames.SideBar.connection);
		    },

           'click @ui.outRequests': function() {
               PacBridgeApp.vent.trigger(EventNames.SideBar.orequests);
		    },

		    'click @ui.cachedDevs': function() {
		        PacBridgeApp.vent.trigger(EventNames.SideBar.cachedevs);
	        },

            'click @ui.fooBar': function() {
                PacBridgeApp.vent.trigger(EventNames.SideBar.foo);
            }

		}
	});

	return SideBarView;
});