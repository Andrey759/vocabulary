
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
var speakCard = () => {
    if (settings.voiceCard !== 'NONE' && card.sentence) {
        card.playing = true;
        responsiveVoice.speak(card.sentence, voiceMap(settings.voiceCard),
            {rate: settings.voiceRate, volume: settings.voiceVolume, onend: () => card.playing = false});
    }
}
var speakCardAuto = () => {
    if (settings.voiceEnabled && settings.voiceCard !== 'NONE' && card.sentence) {
        card.playing = true;
        responsiveVoice.speak(card.sentence, voiceMap(settings.voiceCard),
            {rate: settings.voiceRate, volume: settings.voiceVolume, onend: () => card.playing = false});
    }
}
var speakChatAuto = (right, left) => {
    speak(right, settings.voiceChatRight, () => speak(left, settings.voiceChatLeft));
}
var speak = (text, voice, onend) => {
    if (settings.voiceEnabled && voice !== 'NONE') {
        responsiveVoice.speak(text, voiceMap(voice),
            {rate: settings.voiceRate, volume: settings.voiceVolume, onend: onend});
    }
}
var voiceMap = (voice) => {
    switch (voice) {
        case 'UK_ENGLISH_MALE':
            return 'UK English Male';
        case 'UK_ENGLISH_FEMALE':
            return 'UK English Female';
        case 'US_ENGLISH_FEMALE':
            return 'US English Female';
        case 'US_ENGLISH_MALE':
            return 'US English Male';
    }
}
