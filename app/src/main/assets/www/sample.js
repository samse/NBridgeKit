function preferenceGet() {
    nbridge.preference.get('key', 'defaultValue').then(function(value) {
        alert('value : ' + atob(value));
    });
}

function preferenceSet() {
    nbridge.preference.set('key', 'value').then(function(r) {
        alert("success set value");
    });
}

function preferenceRemove() {
    nbridge.preference.remove('key').then(function(r) {
        alert("key is removed");
    });
}

function locationAvailable() {
    nbridge.location.available().then(function(result) {
        // 위치 권한
        alert('available');
    }, function(error) {
        alert('not available');
    });
}

function locationCurrent() {
    nbridge.location.current().then(function(result) {
        updateLocationInfo('longitude : ' + result.longitude + '\n latitude: ' + result.latitude);
    }, function(error) {
        alert(error);
        updateLocationInfo('')
    });
    updateLocationInfo('running');
}

function locationWatch() {
    nbridge.location.watch(function(position) {
        updateLocationInfo('longitude : ' + result.longitude + '\n latitude: ' + result.latitude);
    }).then(function(result) {
    }, function(error) {
        alert(error);
        updateLocationInfo('')
    });
    updateLocationInfo('watching');
}

function locationClearWatch() {
    updateLocationInfo('')
    nbridge.location.clearWatch().then(function(result) {
        alert('success to clear watch');
    }, function(error) {
        alert(error);
    });
}

function updateLocationInfo(msg) {
    document.getElementById('loc').innerHTML = msg
}

function appAppInfo() {
    nbridge.app.appInfo().then(function(result) {
        alert('app version : ' + result.version + "/" + result.build + "\napp name: " + result.name);
    }, function(error) {
        alert(error);
    })
}

function appInfoDeviceInfo() {
    nbridge.app.deviceInfo().then(function(result) {
        alert('device model : ' + result.model + " \nlanguage: " + result.language + "\nversion: " + result.version);
    }, function(error) {
        alert(error);
    })
}

function appClearCache() {
    nbridge.app.clearCache().then(function(result) {
        alert('cleared web cache');
    });
}

function appInfoExit() {
    if (confirm("nBridge 앱을 종료하시겠습니까?")) {
        nbridge.app.exit();
    }
}
