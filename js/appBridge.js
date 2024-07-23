nbridge.app = (function(nbridge) {
	return {
		service : 'app',
		appInfo : function() {
			return nbridge.callToNative(this.service, 'appInfo', {});
		}, 
		goSettings: function(_type) {
            nbridge.callToNative(this.service, "goSettings", {type: _type});
        },
		exit : function() {
			nbridge.callToNative(this.service, 'exit', {});
		}
	}
})(nbridge);
