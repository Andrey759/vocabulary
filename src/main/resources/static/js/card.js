
var card = new Vue({
    el: '#card',
    data: {
        word: '',
        sentence: '',
        sentenceHtml: 'Loading...',
        explanationHtml: '',
        translationHtml: '',
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
        firstLoad() {
            this.clearFields();
            fetch('/api/next/' + this.word)
                .then(response => log(response))
                .then(response => response.json())
                .then(this.handleResponse)
                .catch(handleError);
        },
        update() {
            this.clearFields();
            fetch('/api/next/' + this.word)
                .then(response => log(response))
                .then(response => response.json())
                .then(this.handleResponse)
                .then(speakCardAuto)
                .catch(handleError);
        },
        reset() {
            fetch('/api/reset/' + this.word)
                .then(response => log(response))
                .then(response => response.json())
                .then(this.handleResponse)
                .then(speakCardAuto)
                .then(() => dict.update())
                .catch(handleError);
        },
        another() {
            this.clearFields();
            fetch('/api/another/' + this.word)
                .then(response => response.json())
                .then(this.handleResponse)
                .then(speakCardAuto)
                .catch(handleError);
        },
        next() {
            fetch('/api/next/' + this.word)
                .then(response => log(response))
                .then(response => response.json())
                .then(this.handleResponse)
                .then(speakCardAuto)
                .catch(handleError);
        },
        clearFields() {
            this.sentence = '';
            this.sentenceHtml = 'Loading...';
            this.explanationHtml = '';
            this.translationHtml = '';
            this.explanationVisible = false;
            this.translationVisible = false;
            this.playing = false;
            responsiveVoice.cancel();
        },
        handleResponse(response) {
            if (response.sentence === '') {
                if (card.sentenceHtml !== 'No cards to repeat') {
                    this.sentenceHtml = 'No cards to repeat';
                }
                setTimeout(() => card.update(), 2000);
            } else {
                this.word = response.word;
                this.sentence = response.sentence;
                this.sentenceHtml = response.sentenceHtml;
                this.explanationHtml = response.explanationHtml;
                this.translationHtml = response.translationHtml;
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
        this.firstLoad();
    }
});
