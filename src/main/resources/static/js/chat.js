
var chat = new Vue({
    el: '#chat',
    data: {
        disabled: false,
        messages: [],
        newMessage: ''
    },
    methods: {
        init() {
            this.disabled = true;

            fetch('/api/chat')
                .then(response => log(response))
                .then(response => response.json())
                .then(response => {
                    this.disabled = false;
                    this.messages = response;
                })
                .catch(handleError);
        },
        send() {
            if (this.newMessage === undefined || this.newMessage === '' || /^\s+$/.test(this.newMessage)) {
                console.log("newMessage is empty");
                return;
            }
            this.disabled = true;
            var newMessage = this.newMessage;
            this.newMessage = '';
            this.messages.push({owner: "USER", correctedHtml: newMessage});
            this.messages.push({owner: "BOT", correctedHtml: 'Loading.....'});

            fetch('/api/chat', {method: 'POST', body: newMessage})
                .then(response => log(response))
                .then(response => response.json())
                .then(response => {
                    this.messages = response;
                    speakChatAuto(this.messages[this.messages.length - 2].corrected, this.messages[this.messages.length - 1].corrected);
                })
                .catch(handleError)
                .finally(() => this.disabled = false);
        }
    },
    mounted() {
        this.init();
    }
});
