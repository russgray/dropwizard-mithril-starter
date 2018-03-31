import * as m from 'mithril';
import { Comp } from 'mithril';

import { SocialLoginButtons } from './components/SocialLoginButton';

let LoginForm = {
    view: () => {

        let formFields = [];

        formFields.push(m(SocialLoginButtons));

        return m('form', formFields);
    },
};

export let LoginPage = <Comp<{}, {}>>{

    view: () => {

        return m(".login-page", [
            m(".hero", [
                m(".hero-body", [
                    m(".container.has-text-centered", [
                        m(".column.is-4.is-offset-4", [
                            m("h3.title.has-text-grey", "Login"),
                            m("p.subtitle.has-text-grey", "Please login to proceed"),
                            m(".box", [
                                m("figure.avatar", [
                                    m(`img[src='${require<string>('./assets/img/fa-sign-in-128.png')}']`),
                                ]),
                                m(LoginForm),
                            ]),
                        ]),
                    ]),
                ]),
            ]),
        ]);
    },
};
