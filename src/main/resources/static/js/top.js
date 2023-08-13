
var topPanel = new Vue({
    el: '#top-panel',
    data: {
        word: '',
        enabled: true,
        oldWord: '',
        created: true,
        resultVisible: false
    },
    methods: {
        add() {
            this.enabled = false;
            fetch('/api/card', {
                method: 'POST',
                body: this.word
            })
                .then(response => log(response))
                .then(response => response.text())
                .then(response => {
                    this.oldWord = this.word;
                    this.word = '';
                    this.created = response === 'true';
                    this.resultVisible = true;
                    setTimeout(() => this.resultVisible = false, 5000);
                    dict.update();
                })
                .catch(handleError)
                .finally(() => this.enabled = true);
        }
    },
});
