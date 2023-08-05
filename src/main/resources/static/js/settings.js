
var settings = new Vue({
    el: '#nav-settings',
    data: {
        username: '',
        passwordOld: '',
        passwordNew: '',
        passwordConfirm: '',
        passwordResult: null,
        voiceEnabled: false,
        voiceCard: '',
        voiceChatLeft: '',
        voiceChatRight: '',
        voiceRate: 0.0,
        voiceVolume: 0.0
    },
    methods: {
        load() {
            fetch('/api/user')
                .then(response => log(response))
                .then(response => response.json())
                .then(response => {
                    this.username = response.username;
                    this.voiceEnabled = response.voiceEnabled;
                    this.voiceCard = response.voiceCard;
                    this.voiceChatLeft = response.voiceChatLeft;
                    this.voiceChatRight = response.voiceChatRight;
                    this.voiceRate = response.voiceRate;
                    this.voiceVolume = response.voiceVolume;
                })
                .catch(handleError);
        },
        changePassword() {
            fetch('/api/user/password/change', {
                method: 'POST',
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({
                    passwordOld: this.passwordOld,
                    passwordNew: this.passwordNew,
                    passwordConfirm: this.passwordConfirm
                })
            })
                .then(response => log(response))
                .then(response => response.text())
                .then(response => {
                    this.passwordOld = '';
                    this.passwordNew = '';
                    this.passwordConfirm = '';
                    this.passwordResult = response === 'true';
                })
                .catch(handleError);
        },
        saveVoiceEnabled() {
            fetch('/api/user/voice/enabled', {
                method: 'POST',
                body: this.voiceEnabled
            })
                .then(response => log(response))
                .catch(handleError);
        },
        saveVoiceCard() {
            fetch('/api/user/voice/card', {
                method: 'POST',
                body: this.voiceCard
            })
                .then(response => log(response))
                .catch(handleError);
        },
        saveVoiceChatLeft() {
            fetch('/api/user/voice/chat/left', {
                method: 'POST',
                body: this.voiceChatLeft
            })
                .then(response => log(response))
                .catch(handleError);
        },
        saveVoiceChatRight() {
            fetch('/api/user/voice/chat/right', {
                method: 'POST',
                body: this.voiceChatRight
            })
                .then(response => log(response))
                .catch(handleError);
        },
        saveVoiceRate() {
            fetch('/api/user/voice/rate', {
                method: 'POST',
                body: this.voiceRate
            })
                .then(response => log(response))
                .catch(handleError);
        },
        saveVoiceVolume() {
            fetch('/api/user/voice/volume', {
                method: 'POST',
                body: this.voiceVolume
            })
                .then(response => log(response))
                .catch(handleError);
        }
    },
    mounted() {
        this.load();
    }
});
