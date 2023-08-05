
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
        reset() {
            this.sentence = '';
            this.sentenceHtml = '';
            this.explanationHtml = '';
            this.translationHtml = '';
            fetch('/api/reset/' + this.word)
                .then(response => log(response))
                .then(response => response.json())
                .then(response => {
                    this.word = response.word;
                    this.sentence = response.sentence;
                    this.sentenceHtml = response.sentenceHtml;
                    this.explanationHtml = response.explanationHtml;
                    this.translationHtml = response.translationHtml;
                    dict.update();
                })
                .catch(handleError);
        },
        another() {
            this.sentence = '';
            this.sentenceHtml = 'Loading...';
            this.explanationHtml = '';
            this.translationHtml = '';
            fetch('/api/another/' + this.word)
                .then(response => response.json())
                .then(response => {
                    this.word = response.word;
                    this.sentence = response.sentence;
                    this.sentenceHtml = response.sentenceHtml;
                    this.explanationHtml = response.explanationHtml;
                    this.translationHtml = response.translationHtml;
                })
                .catch(handleError);
        },
        next() {
            //this.sentence = '';
            //this.sentenceHtml = '';
            //this.explanationHtml = '';
            //this.translationHtml = '';
            fetch('/api/next/' + this.word)
                .then(response => log(response))
                .then(response => response.json())
                .then(this.handleResponse)
                .catch(handleError);
        },
        handleResponse(response) {
            if (response.sentence === '') {
                if (card.sentenceHtml !== 'No cards to repeat') {
                    this.sentenceHtml = 'No cards to repeat';
                }
                setTimeout(() => card.next(), 2000);
            } else {
                this.word = response.word;
                this.sentence = response.sentence;
                this.sentenceHtml = response.sentenceHtml;
                this.explanationHtml = response.explanationHtml;
                this.translationHtml = response.translationHtml;
            }
            /*responsiveVoice.allowSpeechClicked(true);*/
            /*
                                    responsiveVoice.speak(response, 'UK English Male', {rate: 0.8, volume: 1, onend: function () {
                                            responsiveVoice.cancel();
                                            responsiveVoice.speak(response, 'UK English Male', {rate: 0.8, volume: 1});
                                        }});*/
            /* responsiveVoice.setDefaultRate(0.5); setDefaultVoice */
        },
        speak() {
            if (this.playing) {
                this.playing = false;
                responsiveVoice.cancel();
            } else {
                this.playing = true;
                responsiveVoice.speak(this.sentence, 'UK English Male', {rate: 0.8, volume: 1, onend: () => this.playing = false});
            }
        }
    },
    mounted() {
        $('.card-left, .card-right').on('click', function() {
            $(this).css('user-select', 'none');
        });
        responsiveVoice.cancel();
        //responsiveVoice.speak('');
        //setTimeout(() => { responsiveVoice.allowSpeechClicked(true); }, 300);
        //setTimeout(() => { responsiveVoice.allowSpeechClicked(true); }, 1000);
        this.next();
    }
});
