define(['marionette',
        'text!templates/HomeView.html'],
        function(Marionette,
                theTemplate) {

    var HomeView = Marionette.Layout.extend({
        template: _.template(theTemplate),

        regions: {
            conStatus: '#con-status'
        },

        initialize: function() {
        },
    });

    return HomeView;
});