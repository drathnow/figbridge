define(['marionette',
        'app/EventNames',
        'views/IOPointsContentLayout'
        ],
        function(marionette,
                EventNames,
                IOPointsContentLayout) {

    var ContentView = Marionette.Layout.extend({
        template: '#content',

        initialize: function() {
            var that = this;
            this.on(EventNames.SideBar.showPoints, function() {
                that.show(new IOPointsContentLayout());
            }, this);
        },

        onClose: function () {
            ZiosWebApp.vent.off(EventNames.SideBar.showPoints, null, this);
        }

    });

    return ContentView;
});