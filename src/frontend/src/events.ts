import * as m from 'mithril';
import * as storage from 'store';

let bullet: any = require('bullet-pubsub');

document.addEventListener('DOMContentLoaded', function () {

    bullet.on('login', (user) => {
        storage.set('user', JSON.stringify(user));
        m.route.set('/');
    });

    bullet.on('logout', () => {
        storage.remove('user');
        m.route.set('/');
    });

    bullet.on('reloadPage', () => {
        m.route.set(m.route.get());
    });

    bullet.on('xhrResponse', (xhr: XMLHttpRequest) => {
        let token = xhr.getResponseHeader("X-CSRF-Token");
        if (token) {
            storage.set("CSRFToken", token);
        }
    });

}, false);
