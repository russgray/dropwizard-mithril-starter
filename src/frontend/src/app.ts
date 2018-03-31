import * as m from 'mithril';

import { User } from './models/User';

import { Page } from './Page';
import { LoginPage } from './LoginPage';

import './events';

import "normalize.css/normalize.css";
import './scss/app';

m.route(document.body, '/', {
    '/': {
        render: () => m(Page, m(LoginPage))
    },
    '/login': {
        render: () => m(Page, m(LoginPage))
    },
    '/logout': {
        onmatch: User.Logout
    },
    '/authenticated': {
        onmatch: User.LoadCurrentUser
    },
});
