import m = require('mithril');
import { Comp } from 'mithril';

export let Footer = <Comp<{}, {}>>{

    view: function ({ attrs }) {
        return m("footer.footer", [
            m(".container", [
                m(".content.has-text-centered", [
                    m(`img[src='${require('../assets/img/logo-96.png')}']`),
                ]),
                m(".columns", [
                    m('.column', [
                        m(".content.has-text-centered", [
                            m("h3", 'Company'),
                            m("a[href='/about']", { oncreate: m.route.link }, 'About Us'),
                            m("br"),
                            m("a[href='/contact']", { oncreate: m.route.link }, 'Contact'),
                            m("br"),
                            m("a[href='javascript:void(0)']", 'Blog'),
                        ]),
                    ]),
                    m('.column', [
                        m('.content.has-text-centered', [
                            m("h3", 'Follow Us'),
                            m("a[href='javascript:void(0)']", 'Facebook'),
                            m("br"),
                            m("a[href='javascript:void(0)']", 'Twitter'),
                            m("br"),
                            m("a[href='javascript:void(0)']", 'YouTube'),
                        ]),
                    ]),
                    m('.column', [
                        m('.content.has-text-centered', [
                            m("h3", 'Legal'),
                            m("a[href='javascript:void(0)']", 'Terms and Conditions'),
                            m("br"),
                            m("a[href='javascript:void(0)']", 'Privacy Policy'),
                        ]),
                    ]),
                ]),
                m("hr"),
                m('.content.has-text-centered', [
                    m("p", `Disclaimer area lorem ipsum dolor sit amet,
                    consectetur adipisicing elit. Rerum, nostrum
                    repudiandae saepe.`),
                ])
            ]),
        ]);
    },
};
