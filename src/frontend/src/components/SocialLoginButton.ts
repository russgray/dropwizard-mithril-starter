import m = require('mithril');
import { Comp } from 'mithril';

import startCase from 'lodash-es/startCase';

interface SocialLoginButtonController {
    type: "facebook" | "twitter" | "windows" | "google";
    text?: string;
}

export let SocialLoginButton = <Comp<SocialLoginButtonController, {}>>{

    view: ({ attrs }) => {
        return m(`span.${attrs.type}-login-button`, [
            m(`a.button[href='/auth/${attrs.type}']`, [
                m("span.icon", [
                    m(`i.fa.fa-${attrs.type}`),
                ]),
                m("span", attrs.text || startCase(attrs.type)),
            ]),
        ]);
    },
};

export let SocialLoginButtons = <Comp<{}, {}>>{

    view: () => {
        return m(".social-logins", [
            m(".buttons.has-text-left", [
                m(SocialLoginButton, {
                    type: 'facebook',
                }),
                m(SocialLoginButton, {
                    type: 'twitter',
                }),
            ]),
        ]);
    },
};
