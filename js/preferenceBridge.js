nbridge.preference = (function(nbridge) {
	return {
		service : 'preference',
		get : function(_key, _value, _isObject) {
			return new Promise(function (resolve, reject) {
				nbridge.callToNative(nbridge.preference.service, 'get', {key : _key, defaultValue : _value})
				.then(function(value) {
					resolve(atob(value));
				}, function(err) {
					reject(err);
				});
			});
		},
		set : function(_key, _value) {
            new Promise(function(resolve, reject) {
                nbridge.callToNative(nbridge.preference.service, 'set', {key : _key, value : _value})
                .then(function() {
                    resolve();
                }, function(err) {
                    reject(err);
                });
            });
		},
		getBoolean : function(_key, _value, _isObject) {
			return new Promise(function (resolve, reject) {
				nbridge.callToNative(nbridge.preference.service, 'getBoolean', {key : _key, defaultValue : _value})
				.then(function(value) {
					resolve(value);
				}, function(err) {
					reject(err);
				});
			});
		},
		setBoolean : function(_key, _value) {
            new Promise(function(resolve, reject) {
                nbridge.callToNative(nbridge.preference.service, 'setBoolean', {key : _key, value : _value})
                .then(function() {
                    resolve();
                }, function(err) {
                    reject(err);
                });
            });
		},
		getInt : function(_key, _value, _isObject) {
			return new Promise(function (resolve, reject) {
				nbridge.callToNative(nbridge.preference.service, 'getInt', {key : _key, defaultValue : _value})
				.then(function(value) {
					resolve(value);
				}, function(err) {
					reject(err);
				});
			});
		},
		setInt : function(_key, _value) {
            new Promise(function(resolve, reject) {
                nbridge.callToNative(nbridge.preference.service, 'setInt', {key : _key, value : _value})
                .then(function() {
                    resolve();
                }, function(err) {
                    reject(err);
                });
            });
		},
		getLong : function(_key, _value, _isObject) {
			return new Promise(function (resolve, reject) {
				nbridge.callToNative(nbridge.preference.service, 'getLong', {key : _key, defaultValue : _value})
				.then(function(value) {
					resolve(value);
				}, function(err) {
					reject(err);
				});
			});
		},
		setLong : function(_key, _value) {
            new Promise(function(resolve, reject) {
                nbridge.callToNative(nbridge.preference.service, 'setLong', {key : _key, value : _value})
                .then(function() {
                    resolve();
                }, function(err) {
                    reject(err);
                });
            });
		},
		remove : function(_key) {
			nbridge.callToNative(nbridge.preference.service, 'remove', {key : _key});
		}
	}
})(nbridge);