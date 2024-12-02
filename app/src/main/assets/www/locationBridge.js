nbridge.location = (function(nbridge) {
	return {
		service : 'location',
		available : function() {
			return nbridge.callToNative(this.service, 'available', {});
		},
		current : function() {
			return nbridge.callToNative(this.service, 'current', {});
		},
		watch : function(watchCb) {
            var option = { retainCallback: watchCb }
			return nbridge.callToNative(this.service, 'watch', option);
		},
		clearWatch: function() {
			return nbridge.callToNative(this.service, 'clearWatch', {});	
		}
	}
})(nbridge);
