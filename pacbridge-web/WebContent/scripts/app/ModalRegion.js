define(['backbone',
        'marionette'],
        function(Backbone,
                Marionette) {

    var ModalRegion = Backbone.Marionette.Region.extend({
        el: "#modalSection",

        constructor: function () {
            Backbone.Marionette.Region.prototype.constructor.apply(this, arguments);
            this.on("show", this.showModal, this);
        },

        getEl: function (selector) {
            var $el = $(selector);
            $el.on("hidden", this.close);
            return $el;
        },

        showModal: function (view) {
            view.on("close", this.hideModal, this);
            this.$el.modal('show');
        },

        hideModal: function () {
            this.$el.modal('hide');
        },

        setOptions: function (options) {
            this.$el.modal(options);
        }
    });

    return ModalRegion;
});
