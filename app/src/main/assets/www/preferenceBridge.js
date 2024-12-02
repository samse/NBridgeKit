nbridge.preference = (function(nbridge) {
	return {
		service : 'preference',
		get : function(_key, _value) {
			return nbridge.callToNative(this.service, 'get', {key : _key, defaultValue : _value});
		},
		set : function(_key, _value) {
			return nbridge.callToNative(this.service, 'set', {key : _key, value : _value});
		},
		remove : function(_key) {
            return nbridge.callToNative(this.service, 'remove', {key : _key});
		}
	}
})(nbridge);
