
var dict = new Vue({
    el: '#dict',
    data: {
        elements: []
    },
    methods: {
        update() {
            fetch('/api/dict')
                .then(response => log(response))
                .then(response => response.json())
                .then(response => this.elements = response)
                .catch(handleError);
        },
        change(word, status) {
            fetch('/api/dict', {
                method: 'POST',
                headers: { "Content-Type": "application/json" },
                body: JSON.stringify({ word: word, status: status })
            })
                .then(response => log(response))
                .then(response => response.json())
                .then(card.handleResponse)
                .catch(handleError);
        },
        deleteWord(word) {
            this.elements = this.elements.filter(dictDto => dictDto.word !== word);
            fetch('/api/dict', {
                method: 'DELETE',
                body: word
            })
                .then(response => log(response))
                .then(response => response.json())
                .then(card.handleResponse)
                .catch(handleError);
        }
    },
    mounted() {
        this.update();
    }
});
