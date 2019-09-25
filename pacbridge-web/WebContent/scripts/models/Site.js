define(['marionette'], function(Marionette){
    return Backbone.Model.extend({
        urlRoot: 'srv/sites'
    });
});