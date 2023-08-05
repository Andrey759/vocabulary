
var newWord = new Vue({
    el: '#new-word',
    data: {
        word: '',
        result: ''
    },
    methods: {
        add() {
            fetch('/api/add/' + this.word)
                .then(response => log(response))
                .then(response => response.text())
                .then(response => {
                    this.result = response;
                    dict.update();
                })
                .catch(handleError);
        }
    },
});
