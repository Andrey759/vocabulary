
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
        buttonsEnabled: false,
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
            this.buttonsEnabled = false;
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
            this.buttonsEnabled = false;
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
            this.buttonsEnabled = false;
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
            this.explanationVisible = false;
            this.translationVisible = false;
            this.playing = false;
        },
        handleResponse(response) {
            // Otherwise the picture would blink after each scheduled reloading
            if (card.sentenceHtml !== 'No cards to repeat' || response.sentenceHtml !== 'No cards to repeat') {
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
                this.explanationVisible = false;
                this.translationVisible = false;
            }
            if (response.word === '' && response.sentenceHtml === 'No cards to repeat') {
                setTimeout(() => card.loadWithoutAutoplay(), 2000);
            } else {
                setTimeout(() => this.buttonsEnabled = true, 1000);
            }
        },
        handleSpaceKey(event) {
            if (event.code === 'ArrowLeft' && this.buttonsEnabled && event.target.nodeName !== 'INPUT') {
                this.reset();
                event.preventDefault();
            }
            if ((event.code === 'ArrowUp' || event.code === 'ArrowDown') && this.buttonsEnabled && event.target.nodeName !== 'INPUT') {
                this.another();
                event.preventDefault();
            }
            if (event.code === 'ArrowRight' && this.buttonsEnabled && event.target.nodeName !== 'INPUT') {
                this.next();
                event.preventDefault();
            }
            if (event.code === 'Space' && event.target.nodeName !== 'INPUT') {
                this.speak();
                event.preventDefault();
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
        // $('.card-left, .card-right').on('click', function() {
        //     $(this).css('user-select', 'none');
        // });
        // responsiveVoice.speak('');
        // //setTimeout(() => { responsiveVoice.allowSpeechClicked(true); }, 300);
        // setTimeout(() => { responsiveVoice.allowSpeechClicked(true); }, 1000);
        this.clearFieldsAndSetLoading();
        this.loadWithoutAutoplay();

        document.addEventListener('keydown', this.handleSpaceKey);
    },
    beforeDestroy() {
        document.removeEventListener('keydown', this.handleSpaceKey)
    }
});
