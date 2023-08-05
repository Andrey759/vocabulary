
var log = (response) => {
    console.log(response);
    return response;
}
var handleError = (response) => {
    if (('' + response).indexOf('SyntaxError') >= 0 || ('' + response).indexOf('TypeError') >= 0) {
        window.location.href = "/";
    } else {
        console.log(response);
    }
}
var speak = (text) => {
    responsiveVoice.speak(text, 'UK English Male', {rate: settings.voiceRate, volume: settings.voiceVolume});
}
var stopSpeak = () => {
    responsiveVoice.cancel();
}
