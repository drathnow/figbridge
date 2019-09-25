define(['backbone',
        'views/HomeView',
        'views/ConnectionsContentLayout',
        'views/OutgoingRequestsContentLayout',
        'views/CachedDevicesLayout',
        'views/Foo',
        'app/EventNames'],
        function(Backbone,
                HomeView,
                ConnectionsContentLayout,
                OutgoingRequestsContentLayout,
                CachedDevicesLayout,
                Foo,
                EventNames) {

    var ContentController = function(title, detail) {
        var that = this;
        this.titleRegion = title;
        this.contentRegion = detail;

        PacBridgeApp.vent.on(EventNames.SideBar.showHome, function(args) {
            $('.page-title').text('Home');
            that.contentRegion.show(new HomeView());
        });

        PacBridgeApp.vent.on(EventNames.SideBar.connection, function(args) {
            $('.page-title').text('Connections');
            that.contentRegion.show(new ConnectionsContentLayout());
        });

        PacBridgeApp.vent.on(EventNames.SideBar.orequests, function(args) {
            $('.page-title').text('Outgoing Requests');
            that.contentRegion.show(new OutgoingRequestsContentLayout());
        });

        PacBridgeApp.vent.on(EventNames.SideBar.cachedevs, function(args) {
            $('.page-title').text('Cached Devices');
            that.contentRegion.show(new CachedDevicesLayout());
        });

        PacBridgeApp.vent.on(EventNames.SideBar.foo, function(args) {
            $('.page-title').text('Foo');
            that.contentRegion.show(new Foo());
        });
    };

    return ContentController;
});
