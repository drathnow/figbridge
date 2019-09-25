define(['marionette', 
        'datatables',
        'views/ConnectionRowView', 
        'text!templates/ConnectionsTable.html'], 
		function(Marionette,
		         Datatables,
		         ConnectionRowView, 
		         theTemplate){
	
	var ConnectionsTableView = Marionette.CompositeView.extend({
		template: _.template(theTemplate),
		itemView: ConnectionRowView,
 		itemViewContainer: 'tbody',
 		
        onShow: function() {
            $('#con-table').DataTable({
                responsive: true,
                aoColumnDefs : [ {
                    bSortable : false,
                    aTargets : [ 7 ]
                } ],

            });
        },

	});
	
	return ConnectionsTableView;
});