
var dict = new Vue({
    el: '#dict',
    data: {
        cards: []
    },
    methods: {
        update() {
            fetch('/api/dict')
                .then(response => log(response))
                .then(response => response.json())
                .then(response => {
                    this.cards = response;
                })
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
            fetch('/api/dict', {
                method: 'DELETE',
                body: word
            })
                .then(response => log(response))
                .then(() => {
                    this.cards = this.cards.filter(card => card.word !== word);
                    if (word === card.word) {
                        card.clearFieldsAndSetLoading();
                        card.loadWithoutAutoplay();
                    }
                })
                .catch(handleError);
        }
    },
    mounted() {
        this.update();
    }
});
