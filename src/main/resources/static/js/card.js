
var card = new Vue({
    el: '#card',
    data: {
        word: '',
        sentence: '',
        sentenceHtml: 'Loading...',
        explanationHtml: '',
        translationHtml: '',
        nextStatus: '',
        finishedToday: 0,
        totalElements: 0,
        buttonsEnabled: true,
        explanationVisible: false,
        translationVisible: false,
        playing: false
    },
    methods: {
        explanationClick() {
            this.explanationVisible = !this.explanationVisible;
        },
        translationClick() {
            this.translationVisible = !this.translationVisible;
        },
        loadWithoutAutoplay() {
            fetch('/api/card')
                .then(response => log(response))
                .then(response => response.json())
                .then(this.handleResponse)
                .catch(handleError);
        },
        reset() {
            fetch('/api/card/reset', {
                method: 'POST',
                body: this.word
            })
                .then(response => log(response))
                .then(response => response.json())
                .then(this.handleResponse)
                .then(speakCardAuto)
                .then(() => dict.update())
                .catch(handleError);
        },
        another() {
            this.clearFieldsAndSetLoading();
            fetch('/api/card/another', {
                method: 'POST',
                body: this.word
            })
                .then(response => response.json())
                .then(this.handleResponse)
                .then(speakCardAuto)
                .catch(handleError);
        },
        next() {
            fetch('/api/card/next', {
                method: 'POST',
                body: this.word
            })
                .then(response => log(response))
                .then(response => response.json())
                .then(this.handleResponse)
                .then(speakCardAuto)
                .then(() => dict.update())
                .catch(handleError);
        },
        clearFieldsAndSetLoading() {
            this.sentence = '';
            this.sentenceHtml = 'Loading...';
            this.explanationHtml = '';
            this.translationHtml = '';
            this.nextStatus = 'three days';
            this.buttonsEnabled = false;
            this.explanationVisible = false;
            this.translationVisible = false;
            this.playing = false;
            responsiveVoice.cancel();
        },
        handleResponse(response) {
            if (response.sentence === '') {
                if (card.sentenceHtml !== 'No cards to repeat') {
                    this.word = '';
                    this.sentence = '';
                    this.sentenceHtml = 'No cards to repeat';
                    this.explanationHtml = '';
                    this.translationHtml = '';
                    this.nextStatus = 'three days';
                    this.buttonsEnabled = false;
                    this.explanationVisible = false;
                    this.translationVisible = false;
                }
                setTimeout(() => card.loadWithoutAutoplay(), 2000);
            } else {
                this.word = response.word;
                this.sentence = response.sentence;
                this.sentenceHtml = response.sentenceHtml;
                this.explanationHtml = response.explanationHtml;
                this.translationHtml = response.translationHtml;
                this.nextStatus = response.nextStatus.replaceAll('_', ' ').toLowerCase();
                if (response.finishedToday !== null) {
                    this.finishedToday = response.finishedToday;
                }
                if (response.totalElements !== null) {
                    this.totalElements = response.totalElements;
                }
                setTimeout(() => this.buttonsEnabled = true, 1000);
                this.explanationVisible = false;
                this.translationVisible = false;
            }
        },
        speak() {
            if (this.playing) {
                card.playing = false;
                responsiveVoice.cancel();
            } else {
                speakCard(this.sentence);
            }
        }
    },
    mounted() {
        $('.card-left, .card-right').on('click', function() {
            $(this).css('user-select', 'none');
        });
        responsiveVoice.speak('');
        //setTimeout(() => { responsiveVoice.allowSpeechClicked(true); }, 300);
        setTimeout(() => { responsiveVoice.allowSpeechClicked(true); }, 1000);
        this.clearFieldsAndSetLoading();
        this.loadWithoutAutoplay();
    }
});
